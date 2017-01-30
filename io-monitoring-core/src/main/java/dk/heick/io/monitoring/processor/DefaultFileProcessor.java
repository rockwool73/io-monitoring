package dk.heick.io.monitoring.processor;

import java.io.File;
import java.util.Properties;
import java.util.UUID;

import dk.heick.io.monitoring.validation.ValidationException;

/**
 * Default implementation of the FileProcessor. <br>
 * If extending this class remember to call <code>super.method()</code>, if the method you are overriding, if needed.<br>
 * The only method not implemented is "<code>process(Properties context,File file)</code>.
 * @author Frederik Heick
 * @version 1.0
 */
public abstract class DefaultFileProcessor implements FileProcessor {
	
	public final static String CORRELATION_ID="CORRELATION_ID";
	public final static String FILE_NAME="FILE_NAME";
	
	/**
	 * Constructor.
	 */
	public DefaultFileProcessor() {
		super();
	}

	/**
	 * Two properties is set. <br>
	 * <ul>
	 * 		<li>Name "CORRELATION_ID" is set with a random UUID.</li>
	 *      <li>Name "FILE_NAME" is set with <code>file.getName()</code>.</li>
	 * </ul>
	 * @see #CORRELATION_ID
	 * @see #FILE_NAME
	 */
	@Override
	public void beforeProcess(Properties context, File file) {
		context.setProperty(CORRELATION_ID,UUID.randomUUID().toString());
		context.setProperty(FILE_NAME,file.getName());
	}

	/**
	 * Always returns <code>true</code>. Nothing else happens in this method.
	 */
	@Override
	public boolean onError(Properties context, long start, File file, Exception e) {
		return true;
	}

	/**
	 * Always returns <code>true</code>. Nothing else happens in this method.
	 */
	@Override
	public boolean onSuccess(Properties context, long start, File file) {
		return true;
	}
	
	@Override
	public void validate() throws ValidationException {
		//NOTHING
	}

}
