package task;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Path;

import task.Progress.Status;

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
		this.progress = new SimpleProgress();
	}

	/**
	 * The active thread (if any) dedicated for this download mission.
	 */
	transient Thread t;

	SimpleProgress progress;

	/**
	 * Starts the download mission by creating a new thread and activates it.
	 */
	public void start() {
		progress.status = Status.IN_PROGRESS;
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
		if (progress.status == Status.IN_PROGRESS && t != null)
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
		if (progress.status == Status.IN_PROGRESS && t != null)
			t.join();
	}

	public SimpleProgress getProgress() {
		return progress;
	}

	private class SimpleTask implements Runnable {

		@Override
		public void run() {

			try {
				URLConnection conn = url.openConnection();

				if (progress.current != 0)
					conn.setRequestProperty("Range", "Bytes=" + progress.current + "-");

				try (InputStream in = conn.getInputStream();
						FileOutputStream out = new FileOutputStream(path.toFile(), progress.current != 0);) {
					if (progress.total == -1)
						progress.total = conn.getContentLengthLong() + progress.current;

					byte[] buf = new byte[8 * 1024];
					int len;

					while ((len = in.read(buf)) != -1) {
						out.write(buf, 0, len);
						progress.current += len;
						if (Thread.interrupted()) {
							progress.status = Status.PAUSED;
							return;
						}
					}

					progress.status = Status.FINISHED;

				} catch (FileNotFoundException e) {
					progress.status = Status.FAILED;
				} catch (IOException e) {
					progress.status = Status.FAILED;
				}

			} catch (IOException e) {
				progress.status = Status.FAILED;
			}

		}

	}

	private class SimpleProgress extends Progress {

		private static final long serialVersionUID = 9142711264221651021L;

		long current = 0;

		@Override
		public long getCurrentSize() {
			return current;
		}

	}
}