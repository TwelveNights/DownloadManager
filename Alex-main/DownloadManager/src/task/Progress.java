package task;

import java.io.Serializable;

public abstract class Progress implements Serializable {

	private static final long serialVersionUID = 7124403183454198815L;

	long total = -1;

	abstract public long getCurrentSize();

	public long getTotalSize() {
		return total;
	}

	public enum Status {
		IN_PROGRESS, NOT_STARTED, PAUSED, FINISHED, FAILED
	}

	Status status = Status.NOT_STARTED;

	public Status getStatus() {
		return status;
	}

}
