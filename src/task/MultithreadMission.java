package task;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayDeque;
import java.util.ArrayList;

/**
 * MultithreadMission is a class representing a download mission utilizing
 * multiple Threads to obtain data from an HTTP server and then writes it down
 * to a given file. Notably, MultithreadMission fails if and only if it fails to
 * obtain file length. Any fail in downloading segments will not flag the
 * mission as failed, nor will it stop the mission. Instead, the mission will
 * retry until explicitly paused. The resulting Files may be incomplete if the
 * server fails to report the correct file size.
 */
public class MultithreadMission extends AbstractMission {

	private static final long serialVersionUID = -5442512424368409551L;

	/**
	 * Constructs a MultithreadMission that downloads from URL to the specified
	 * File.
	 * 
	 * @param url
	 *            The URL from which this mission downloads a file.
	 * @param file
	 *            The File to which the downloaded data is stored to.
	 */
	public MultithreadMission(URL url, File file) {
		super(url, file);
	}

	/**
	 * Constructs a MultithreadMission that downlaods from URL to the specified
	 * Path.
	 * 
	 * @param url
	 *            The URL from which this mission downloads a file.
	 * @param path
	 *            The Path to which the downloaded file is stored to. File name
	 *            must be included.
	 */
	public MultithreadMission(URL url, Path path) {
		super(url, path.toFile());
	}

	/**
	 * Number of threads a mission creates.
	 */
	public static int THREAD_NUMBER = 8;

	/**
	 * Size of each download segments.
	 */
	public static final long SEGMENT_LENGTH = 8 * 1024 * 1024;

	/**
	 * A flag showing whether this mission is being paused.
	 */
	transient boolean interrupted = false;

	long total = -1;
	long current = 0;

	/**
	 * A progress table that shows the progress of each segment. Each entry
	 * should be accessed by at most one thread at a time.
	 */
	long[] progress;

	/**
	 * A pool that stores the segment number of unfinished segments. Access to
	 * this field must acquire the lock first.
	 */
	ArrayDeque<Integer> todo;

	/**
	 * Threads dedicated for this download mission. Access to this field must
	 * acquire the lock first.
	 */
	transient ArrayList<Thread> threads;

	/**
	 * Starts the download mission. Fails if the download file is bigger than
	 * SEGMENT_LENGTH * Integer.MAX_VALUE. If the mission is in the process of
	 * being paused, execution will be halted until the mission comes to a
	 * complete stop.
	 */
	@Override
	synchronized public void start() {

		if (interrupted) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (status == Status.IN_PROGRESS || status == Status.FINISHED)
			return;
		status = Status.IN_PROGRESS;

		// Sets up the mission in multiple steps.

		// Gets file size if not known.
		if (total == -1) {
			try {
				total = url.openConnection().getContentLengthLong();
			} catch (IOException e) {
				this.status = Status.FAILED;
				e.printStackTrace();
				return;
			}

			if (total > SEGMENT_LENGTH * Integer.MAX_VALUE) {
				// Fails immediately if file is too big
				this.status = Status.FAILED;
				return;
			}
		}

		/*
		 * This block allows multiple attempts of file creation should the first
		 * attempt fail. Checks file size and start over if there is a mismatch.
		 * Note that file identity other than its size is not verified.
		 */
		if (!file.exists() || file.length() != total) {
			try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
				raf.setLength(total);
				// Progress is reset once a new file is created.
				progress = null;
				todo = null;
				current = 0;
			} catch (FileNotFoundException e) {
				status = Status.FAILED;
				e.printStackTrace();
				return;
			} catch (IOException e) {
				status = Status.FAILED;
				e.printStackTrace();
				return;
			}
		}

		if (progress == null) {
			// Calculate the number of sections needed and initialize the
			// progress table.
			// Type-cast fails if file is too big, which should be prevented by
			// a check.
			int l = (int) ((total - 1) / SEGMENT_LENGTH + 1);

			progress = new long[l];

			todo = new ArrayDeque<Integer>();
			for (int i = 0; i < l; i++)
				todo.add(i);
		}

		// Start threads
		if (threads == null)
			threads = new ArrayList<Thread>(Math.min(THREAD_NUMBER, progress.length));

		startThreads();
	}

	/**
	 * Allocate a new thread on a unfinished mission, adds the thread to the
	 * thread pool.
	 */
	private void startThreads() {
		for (int i = 0; i < threads.size(); i++) {
			Thread t = new Thread(new WorkerTask());

			synchronized (threads) {
				threads.add(t);
			}

			t.start();
		}
	}

	/**
	 * Flag this mission to be paused. Note that threads for this mission is not
	 * immediately stopped. getStatus() or join() must be called to ensure that
	 * this mission is safely stopped.
	 */
	@Override
	synchronized public void pause() {
		interrupted = true;
	}

	@Override
	/**
	 * Waits for all threads dedicated for this mission to die.
	 */
	synchronized public void join() throws InterruptedException {
		if (status == Status.IN_PROGRESS)
			this.wait();
	}

	/**
	 * @return the full size of the file to be downloaded. -1 if the size is
	 *         unknown. Note that this method could return 0 should the file
	 *         being empty.
	 */
	@Override
	public long getTotalSize() {
		return total;
	}

	/**
	 * @return the size of part of the file downloaded.
	 */
	@Override
	public long getCurrentSize() {
		return current;
	}

	private class WorkerTask implements Runnable {

		@Override
		public void run() {
			while (!interrupted) {
				// Atomically pop a value, then execute on it.
				int i;
				synchronized (todo) {
					if (todo.isEmpty())
						break;
					i = todo.pop();
				}
				download(i);
			}
			terminate();
		}

		private void download(int segmentNumber) {

			try {
				URLConnection conn = url.openConnection();

				long offset = segmentNumber * SEGMENT_LENGTH;
				long start = offset + progress[segmentNumber];
				long end = offset + SEGMENT_LENGTH - 1;
				conn.setRequestProperty("Range", "Bytes=" + start + "-" + end);

				try (InputStream in = conn.getInputStream();
						AsynchronousFileChannel out = AsynchronousFileChannel.open(file.toPath(),
								StandardOpenOption.WRITE);) {

					while (!interrupted) {
						byte[] buf = new byte[8 * 1024];
						int len = in.read(buf);

						if (len == -1)
							// Skip the rest if segment finished
							return;

						out.write(ByteBuffer.wrap(buf, 0, len), start);
						start += len;
						progress[segmentNumber] += len;
						synchronized (progress) {
							current += len;
						}
					}

					/*
					 * byte[] buf = new byte[8 * 1024]; int len;
					 * 
					 * while ((len = in.read(buf)) != -1) {
					 * out.write(ByteBuffer.wrap(buf, 0, len), start);
					 * 
					 * start += len; progress[segmentNumber] += len;
					 * synchronized (progress) { current += len; }
					 * 
					 * if (interrupted) { onInterrupt(); return; } }
					 */

				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			// Put # back if method is interrupted abruptly
			synchronized (todo) {
				todo.add(segmentNumber);
			}

		}

		private void terminate() {
			synchronized (threads) {
				// Remove this thread
				threads.remove(Thread.currentThread());

				if (threads.isEmpty()) {
					synchronized (MultithreadMission.this) {
						// Change status
						synchronized (todo) {
							if (todo.isEmpty()) {
								status = Status.FINISHED;
							} else {
								status = Status.PAUSED;
							}
						}
						
						// Unblock threads joining this mission
						interrupted = false;
						MultithreadMission.this.notifyAll();
					}
				}
			}
		}

	}

}
