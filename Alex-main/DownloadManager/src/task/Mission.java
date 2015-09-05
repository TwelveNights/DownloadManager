package task;

import java.io.File;
import java.io.Serializable;
import java.net.URL;
import java.nio.file.Path;

/**
 * Mission is the abstract base class for all download missions that involve
 * creating one or more threads in the process.
 */
public abstract class Mission implements Serializable {

	private static final long serialVersionUID = -6372000897518949968L;

	/**
	 * The URL from which this mission downloads a file.
	 */
	URL url;
	/**
	 * The File where downloaded data is stored.
	 */
	File file;

	/**
	 * The status of this mission.
	 */
	Status status = Status.NOT_STARTED;

	/**
	 * Starts the download mission.
	 */
	public abstract void start();

	/**
	 * Stops the active thread(s). Note that said thread is not immediately
	 * stopped. getStatus() or join() must be called to ensure that said thread
	 * is safely stopped.
	 */
	public abstract void pause();

	/**
	 * Waits for all thread(s) dedicated for this mission to die.
	 * 
	 * @throws InterruptedException
	 *             if any thread has interrupted the current thread. The
	 *             interrupted status of the current thread is cleared when this
	 *             exception is thrown.
	 */
	public abstract void join() throws InterruptedException;

	/**
	 * @return The URL from which this mission downloads a file.
	 */
	public URL getUrl() {
		return url;
	}

	/**
	 * @return The File where downloaded data is stored.
	 */
	public File getFile() {
		return file;
	}

	/**
	 * @return The path of the downloaded file.
	 */
	public Path getPath() {
		return file.toPath();
	}

	/**
	 * @return the full size of the file to be downloaded.
	 */
	abstract public long getTotalSize();

	/**
	 * @return the size of part of the file downloaded.
	 */
	abstract public long getCurrentSize();

	/**
	 * @return current status of the mission.
	 */
	public final Status getStatus() {
		return status;
	}
}