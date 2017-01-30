package dk.heick.io.monitoring.validation;

/**
 * A validation interface.
 * @author Frederik Heick
 * @version 1.0
 */
public interface Validation {
	
	/**
	 * Validates "something" is not valid an ValidationException is thrown.
	 * @throws ValidationException if "something" is not valid.
	 */
	public void validate() throws ValidationException;

}
