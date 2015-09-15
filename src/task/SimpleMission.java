package task;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Path;
import java.util.function.Consumer;

/**
 * SimpleMission is a class representing a download mission utilizing one Thread
 * to obtain data from an HTTP server and then writes it down to a given file.
 */
public class SimpleMission extends AbstractMission {

	private static final long serialVersionUID = 5113449782029278939L;

	/**
	 * Constructs a SimpleMission that downlaods from URL to the specified File.
	 * 
	 * @param url
	 *            The URL from which this mission downloads a file.
	 * @param file
	 *            The File to which the downloaded data is stored to.
	 */
	public SimpleMission(URL url, File file) {
		super(url, file);
	}

	/**
	 * Constructs a SimpleMission that downlaods from URL to the specified Path.
	 * 
	 * @param url
	 *            The URL from which this mission downloads a file.
	 * @param path
	 *            The Path to which the downloaded file is stored to. File name
	 *            must be included.
	 */
	public SimpleMission(URL url, Path path) {
		super(url, path.toFile());
	}

	long current = 0;
	long total = -1;

	/**
	 * The active thread (if any) dedicated for this download mission.
	 */
	transient Thread t;

	/**
	 * A Consumer that handles FileNotFoundException and IOException.
	 */
	Consumer<Exception> exceptionHandler = (Exception e) -> e.printStackTrace();

	/**
	 * Starts the download mission by creating a new thread and activates it. Do
	 * nothing if the mission is already started or finished.
	 */
	@Override
	synchronized public void start() {
		if (status == Status.IN_PROGRESS || status == Status.FINISHED)
			return;
		status = Status.IN_PROGRESS;

		// Starts over if file not found.
		if (!file.exists())
			current = 0;

		t = new Thread(new SimpleTask());
		t.start();
	}

	/**
	 * Stops the active thread. Do nothing if no thread is active. Note that
	 * said thread is not immediately stopped. getStatus() or join() must be
	 * called to ensure that said thread is safely stopped.
	 */
	@Override
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
	@Override
	synchronized public void join() throws InterruptedException {
		if (status == Status.IN_PROGRESS && t != null)
			t.join();
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

	@Override
	public long getCurrentSize() {
		return current;
	}

	/**
	 * Defines the behavior the mission handles exception in a seperate thread.
	 * 
	 * @param handler
	 *            The function responsible for exception handling.
	 *            FileNotFoundException and IOException are to be expected. Use
	 *            Thread.currentThread() to refer to the running thread.
	 * @see FileNotFoundException
	 * @see IOException
	 * @see Thread#currentThread()
	 */
	public void setUncaughtExceptionHandler(Consumer<Exception> handler) {
		this.exceptionHandler = handler;
	}

	private class SimpleTask implements Runnable {

		@Override
		public void run() {

			try {
				URLConnection conn = url.openConnection();

				if (current != 0)
					conn.setRequestProperty("Range", "Bytes=" + current + "-");

				try (InputStream in = conn.getInputStream();
						FileOutputStream out = new FileOutputStream(file, current != 0);) {
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
				}

			} catch (Exception e) {
				status = Status.FAILED;
				handleException(e);
			}

		}

		void handleException(Exception e) {
			exceptionHandler.accept(e);
		}

	}
}