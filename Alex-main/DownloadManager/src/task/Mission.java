package task;

import java.io.Serializable;
import java.net.URL;
import java.nio.file.Path;

import task.Progress.Status;

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
	 * The Path to which the downloaded file is stored to. File name must be
	 * included.
	 */
	Path path;

	/**
	 * Starts the download mission.
	 */
	public abstract void start();

	/**
	 * Stops the active thread(s). Note that said thread is not immediately
	 * stopped. inProgress() or join() must be called to ensure that said thread
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

	public abstract Progress getProgress();

	/**
	 * @return the full size of the file to be downloaded.
	 */
	public long getTotalSize() {
		return getProgress().getTotalSize();
	}

	/**
	 * @return the size of part of the file downloaded.
	 */
	public long getCurrentSize() {
		return getProgress().getCurrentSize();
	}

	public Status getStatus() {
		return getProgress().status;
	}

}
