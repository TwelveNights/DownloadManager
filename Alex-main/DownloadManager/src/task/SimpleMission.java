package task;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Path;

/**
 * SimpleMission is a class representing a download mission utilizing one Thread
 * to obtain data from an http server and then writes it down to a given file.
 */
public class SimpleMission extends Mission {

	private static final long serialVersionUID = 5113449782029278939L;

	/**
	 * Constructs a SimpleMission with the specified URL and Path.
	 * 
	 * @param url
	 *            The URL from which this mission downloads a file.
	 * @param path
	 *            The Path to which the downloaded file is stored to. File name
	 *            must be included.
	 */
	public SimpleMission(URL url, Path path) {
		this.url = url;
		this.path = path;
	}

	long current = 0;
	long total = -1;

	/**
	 * The active thread (if any) dedicated for this download mission.
	 */
	transient Thread t;

	/**
	 * Starts the download mission by creating a new thread and activates it.
	 */
	public void start() {
		status = Status.IN_PROGRESS;
		t = new Thread(new SimpleTask());
		t.start();
	}

	/**
	 * Stops the active thread. Do nothing if no thread is active or said thread
	 * is not initialized. Note that said thread is not immediately stopped.
	 * inProgress() or join() must be called to ensure that said thread is
	 * safely stopped.
	 */
	public void pause() {
		if (status == Status.IN_PROGRESS && t != null)
			t.interrupt();
	}

	/**
	 * Wait for the active thread (if any) to die.
	 * 
	 * @throws InterruptedException
	 *             if any thread has interrupted the current thread. The
	 *             interrupted status of the current thread is cleared when this
	 *             exception is thrown.
	 */
	public void join() throws InterruptedException {
		if (status == Status.IN_PROGRESS && t != null)
			t.join();
	}

	@Override
	public long getTotalSize() {
		return total;
	}

	@Override
	public long getCurrentSize() {
		return current;
	}

	private class SimpleTask implements Runnable {

		@Override
		public void run() {

			try {
				URLConnection conn = url.openConnection();

				if (current != 0)
					conn.setRequestProperty("Range", "Bytes=" + current + "-");

				try (InputStream in = conn.getInputStream();
						FileOutputStream out = new FileOutputStream(path.toFile(), current != 0);) {
					if (total == -1)
						total = conn.getContentLengthLong() + current;

					byte[] buf = new byte[8 * 1024];
					int len;

					while ((len = in.read(buf)) != -1) {
						out.write(buf, 0, len);
						current += len;
						if (Thread.interrupted()) {
							status = Status.PAUSED;
							return;
						}
					}

					status = Status.FINISHED;

				} catch (FileNotFoundException e) {
					status = Status.FAILED;
				} catch (IOException e) {
					status = Status.FAILED;
				}

			} catch (IOException e) {
				status = Status.FAILED;
			}

		}

	}

}