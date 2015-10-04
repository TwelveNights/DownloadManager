package exception;

public class IncorrectMissionStateException extends RuntimeException {

	private static final long serialVersionUID = 4202825047227427078L;

	public IncorrectMissionStateException(String message) {
		super(message);
	}

}
