package task;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;

public interface Mission {

	public void start();

	public void pause();

	public void join() throws InterruptedException;

	public URL getUrl();

	public File getFile();

	default public Path getPath() {
		return getFile().toPath();
	}

	public long getTotalSize();

	public long getCurrentSize();

	public Status getStatus();

}
