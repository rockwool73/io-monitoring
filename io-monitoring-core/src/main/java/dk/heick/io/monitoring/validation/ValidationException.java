package dk.heick.io.monitoring.validation;

/**
 * ValidationException used by <tt>Validation</tt> and <tt>ValidateUtils</tt>.
 * @author Frederik Heick
 * @see Validation
 * @see ValidateUtils
 */
public class ValidationException extends Exception {

	private static final long serialVersionUID = 6676410037151031710L;

	/**
	 * Constructor.
	 * @param message exception message.
	 */
	public ValidationException(String message) {
		super(message);		
	}

	/**
	 * Constructor.
	 * @param message exception message.
	 * @param cause root exception
	 */
	public ValidationException(String message, Throwable cause) {
		super(message, cause);		
	}

}
