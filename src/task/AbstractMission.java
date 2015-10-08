package task;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.nio.file.Path;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import exception.IncorrectMissionStateException;

/**
 * Mission is the abstract base class for all download missions that involve
 * creating one or more threads in the process.
 */
public abstract class AbstractMission implements Mission, Serializable {

	private static final long serialVersionUID = -6372000897518949968L;

	public AbstractMission(URL url, File file) {
		this.url = url;
		this.file = file;
	}

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

	BiConsumer<AbstractMission, Exception> uncaughtExceptionHandler = 
			(AbstractMission m, Exception e) -> e.printStackTrace();
	Consumer<AbstractMission> completionHandler = (AbstractMission m) -> {};

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
	public void addUncaughtExceptionHandler(BiConsumer<AbstractMission, Exception> handler) {
		this.uncaughtExceptionHandler = uncaughtExceptionHandler.andThen(handler);
	}

	/**
	 * Defines what to do when the mission finishes.
	 * 
	 * @param handler
	 *            The function that is called when the mission finishes.
	 */
	public void addCompletionHandler(Consumer<AbstractMission> handler) {
		this.completionHandler = completionHandler.andThen(handler);
	}

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

	public synchronized void setUrl(URL url) {
		if (status != Status.NOT_STARTED && status != Status.FINISHED)
			throw new IncorrectMissionStateException("Given mission is already started");
		this.url = url;
	}

	/**
	 * @return The File where downloaded data is stored.
	 */
	public File getFile() {
		return file;
	}

	public synchronized void setFile(File file) {
		if (status != Status.NOT_STARTED && status != Status.FINISHED)
			throw new IncorrectMissionStateException("Given mission is already started");
		this.file = file;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((file == null) ? 0 : file.hashCode());
		return result;
	}

	/**
	 * Comparison uses equals() method of File. Note that this returns whether
	 * two missions download to the same file system location, regardless of the
	 * URL from which files are downloaded.
	 * 
	 * @see File#equals(Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof AbstractMission) {
			AbstractMission other = (AbstractMission) obj;
			return ((file == null) ? (other.getFile() == null) : file.equals(other.getFile()));
		}
		return false;
	}

}
