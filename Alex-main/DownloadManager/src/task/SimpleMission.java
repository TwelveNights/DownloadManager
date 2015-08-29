package task;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Path;

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

	/**
	 * The active thread (if any) dedicated for this download mission.
	 */
	transient Thread t;

	/**
	 * Download progress represented in number of bytes downloaded
	 */
	// TODO set public for test purpose
	public long current = 0;
	public long total = -1;

	/**
	 * Starts the download mission by creating a new thread and activates it.
	 */
	public void start() {
		this.inProgress = true;
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
		if (this.inProgress && t != null)
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
		if (this.inProgress && t != null)
			t.join();
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
						total = conn.getContentLengthLong();

					byte[] buf = new byte[8 * 1024];
					int len;

					while ((len = in.read(buf)) != -1) {
						out.write(buf, 0, len);
						current += len;
						if (Thread.interrupted()) {
							return;
						}
					}

				} catch (FileNotFoundException e) {
					// TODO
					e.printStackTrace();
					System.out.println("Failed to create/access file");
				} catch (IOException e) {
					// TODO
					e.printStackTrace();
					System.out.println("Failed to establish connection");
				}

			} catch (IOException e) {
				// TODO
				e.printStackTrace();
				System.out.println("Failed to establish connection");
			} finally {
				inProgress = false;
			}

		}

	}
}