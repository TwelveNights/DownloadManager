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

	public SimpleMission(URL url, Path path) {
		this.url = url;
		this.path = path;
	}

	Thread t;

	// Variables representing progress in byte
	long current = 0;
	long total = -1;

	public void start() {
		t = new Thread(new SimpleTask());
		t.start();
	}

	private class SimpleTask implements Runnable {

		@Override
		public void run() {

			try {
				URLConnection conn = url.openConnection();
				if (total == -1) {
					total = conn.getContentLengthLong();
				} else {
					conn.setRequestProperty("Range", "Bytes="+current+"-");
				}

				if (Thread.interrupted())
					return;
				
				try (InputStream in = conn.getInputStream();
						FileOutputStream out = new FileOutputStream(path.toFile());) {

					byte[] buf = new byte[64 * 1024];
					int len;

					while ((len = in.read(buf)) != -1) {
						out.write(buf, 0, len);
						total += len;
						if (Thread.interrupted())
							return;
					}

				} catch (FileNotFoundException e) {
					// TODO
					e.printStackTrace();
					System.out.println("Failed to create file");
				} catch (IOException e) {
					// TODO
					e.printStackTrace();
				}

			} catch (IOException e) {
				// TODO
				e.printStackTrace();
			}

		}

	}

}