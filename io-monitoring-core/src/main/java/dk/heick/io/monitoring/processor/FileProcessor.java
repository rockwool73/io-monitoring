package dk.heick.io.monitoring.processor;

import java.io.File;
import java.util.Properties;

import dk.heick.io.monitoring.validation.Validation;

/**
 * How to process a file a File after it has become stable.<br>
 * Do <b>NOT</b> have any private variables in this class, since it can be used many time by many threads.<br>
 * @author Frederik Heick
 */
public interface FileProcessor extends Validation {
	
	/**
	 * To setup any context nessecary.
	 * Do <b>NOT</b> open, move or rename the file.<br>  
	 * @param context context properties that can be carried around.
	 * @param file the file
	 */
	public void beforeProcess(Properties context,File file);
	
	/**
	 * What to do with the stable file.<br>
	 * Only load the data from the file.
	 * Do <b>NOT</b> move or rename the file.<br>
	 * Remember to <b>CLOSE</b> any file handles.
	 * @param context context properties that can be carried around. 
	 * @param file the file to process	 
	 * @throws Exception any exception, <tt>DirectoryMonitorTask</tt> will handle further issues
	 * @see #beforeProcess(Properties, File)
	 */
	public void process(Properties context,File file) throws Exception;	
	
	/**
	 * If the <tt>DirectoryMonitorTask</tt> or the method <tt>process(context,file)</tt> throws an exception.<br>
	 * This method is called. <br>
	 * This is where you can do any additional error handling, ex send an alert to a monitoring system.
	 * Do <b>NOT</b> move or rename the file.<br>
	 * @param context context properties that can be carried around.
	 * @param start the start time when process of the file began. 
	 * @param file the file
	 * @param e the exception.
	 * @return if the <tt>DirectoryMonitorTask</tt> should process the next file or not.
	 * @see #beforeProcess(Properties, File)
	 * @see #process(Properties, File)
	 */
	public boolean onError(Properties context,long start,File file,Exception e);

	/**
	 * If the file was processed successfully by the method <tt>process(context,file)</tt>, without an exception, do
	 * you need any additional work to be done.<br>
	 * Do <b>NOT</b> open, move or rename the file.<br>
	 * @param context context properties that can be carried around.
	 * @param start the start time when process of the file began.
	 * @param file the file.
	 * @return if the <tt>DirectoryMonitorTask</tt> should process the next file or not.
	 * @see #beforeProcess(Properties, File)
	 * @see #process(Properties, File)
	 */
	public boolean onSuccess(Properties context,long start,File file);
}
