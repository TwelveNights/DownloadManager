package task;

import java.io.File;
import java.io.Serializable;
import java.net.URL;

import exception.IncorrectMissionStateException;

public class ExtendedMission implements Serializable, Mission {

	private static final long serialVersionUID = 8554493008724213049L;

	MaskableMission mission;
	File unmaskedFile;
	String extension;

	public ExtendedMission(MaskableMission mission, String extension) throws IncorrectMissionStateException {
		if (mission.getStatus() != Status.NOT_STARTED)
			throw new IncorrectMissionStateException("Mission is already started");

		this.mission = mission;
		this.extension = extension;

		// TODO add extension to mission
		unmaskedFile = mission.getFile();
		mission.setFile(new File(unmaskedFile.getParentFile(), unmaskedFile.getName() + extension));
	}

	@Override
	public void start() {
		mission.start();
	}

	@Override
	public void pause() {
		mission.pause();
	}

	@Override
	public void join() throws InterruptedException {
		mission.join();
	}

	@Override
	public URL getUrl() {
		return mission.getUrl();
	}

	@Override
	public File getFile() {
		return unmaskedFile;
	}
	
	public String getExtension() {
		return extension;
	}

	@Override
	public long getTotalSize() {
		return mission.getTotalSize();
	}

	@Override
	public long getCurrentSize() {
		return mission.getCurrentSize();
	}

	@Override
	public Status getStatus() {
		return mission.getStatus();
	}

	public void unmask() {
		if (mission.getStatus() != Status.FINISHED)
			throw new IncorrectMissionStateException("Mission is not finished");
		
		mission.getFile().renameTo(unmaskedFile);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((unmaskedFile == null) ? 0 : unmaskedFile.hashCode());
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
		if (obj instanceof Mission) {
			Mission other = (Mission) obj;
			return ((unmaskedFile == null) ? (other.getFile() == null) : unmaskedFile.equals(other.getFile()));
		}
		return false;
	}
}
