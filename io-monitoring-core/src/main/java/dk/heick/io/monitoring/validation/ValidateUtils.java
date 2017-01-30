package dk.heick.io.monitoring.validation;

import java.io.File;
import java.nio.file.Path;
import java.util.Date;

import dk.heick.io.monitoring.utils.TimeSpan;

/**
 * Different methods to validate objects or throw a ValidationException.
 * @author Frederik Heick
 */
public class ValidateUtils {
	
	private ValidateUtils() {
		super();
	}

	/**
	 * Validates an object for not being <code>null</code>. 
	 * @param name the name of value is return in the ValidationException for better traceable exception message.	 
	 * @param value the value instance.
	 * @throws ValidationException if <tt>value</tt> is <code>null</code>.
	 */
	public static final void validateNotNull(String name,Object value) throws ValidationException {
		if (value==null){
			throw new ValidationException("Parameter ["+name+"] is null");
		}
	}
	
	/**
	 * Validates an object for not being <code>null</code>. 
	 * @param name the name of value is return in the NullPointerException for better traceable exception message.	 
	 * @param value the value instance.
	 * @throws NullPointerException if <tt>value</tt> is <code>null</code>.
	 */
	public static final void validateNotNullNotChecked(String name,Object value) throws NullPointerException {
		if (value==null){
			throw new NullPointerException("Parameter ["+name+"] is null");
		}
	}
	
	/**
	 * Validates a string for not being null or empty (after trim)
	 * @param name the name of value is return in the ValidationException for better traceable exception message.	
	 * @param value the string value
	 * @throws ValidationException if <tt>value</tt> is <code>null</code> or empty after trim.
	 */
	public static final void  validateNotEmpty(String name, String value) throws ValidationException {
		validateNotEmpty(name, value,true);
	}
	
	/**
	 * Validates a string for not being null or empty, with or with out trim is optional.
	 * @param name the name of value is return in the ValidationException for better traceable exception message.
	 * @param value the string value
	 * @param trimmed if the <tt>value</tt> shall be trimmed before evaluation. 
	 * @throws ValidationException if <tt>value</tt> is <code>null</code> or empty after trim (if used).
	 */
	public static final void validateNotEmpty(String name, String value,boolean trimmed) throws ValidationException {
		validateNotNull(name, value);
		if (trimmed) {
			value = value.trim();
		} 
		if (value.length()==0) {
			throw new ValidationException("Parameter ["+name+"] is empty.");
		}
	}
	
	/**
	 * Validates a String for minimum and maximum length.
	 * @param name the name of value is return in the ValidationException for better traceable exception message.
	 * @param value the string value to validated.
	 * @param trimmed if the <tt>value</tt> shall be trimmed before evaluation.
	 * @param minLength the minimum length of the string (inclusive), if <code>null</code> or less than <tt>0</tt> than no minimum length.
	 * @param maxLength the maximum length of the string (inclusive), if <code>null</code> or less than <tt>0</tt> than no maximum length.
	 * @throws ValidationException if <tt>value</tt> fails the restrictions.
	 */
	public static final void validateLength(String name,String value,boolean trimmed,Integer minLength,Integer maxLength) throws ValidationException {
		validateNotNull(name, value);
		if (trimmed) {
			value = value.trim();
		}
		if ((minLength!=null) && (minLength>=0) && (value.length()<minLength)) {
			throw new ValidationException("Parameter ["+name+"] is shorter ["+value.length()+"] than allowed length ["+minLength+"].");
		}
		if ((maxLength!=null) && (maxLength>=0) && (value.length()>maxLength)) {
			throw new ValidationException("Parameter ["+name+"] is longer ["+value.length()+"] than allowed length ["+maxLength+"].");
		}
	}
	
	/**
	 * Validates a Integer value for minimum and maximum value.
	 * @param name the name of value is return in the ValidationException for better traceable exception message.
	 * @param value the string value to validated.
	 * @param minValue the minimum value (inclusive), if <code>null</code> than no minimum value.
	 * @param maxValue the maximum value (inclusive), if <code>null</code> than no maximum value.
	 * @throws ValidationException if value is out side minimum or maximum range.
	 */
	public static final void validateRange(String name,Integer value,Integer minValue,Integer maxValue) throws ValidationException {
		validateNotNull(name, value);
		if ((minValue!=null) && (value<minValue)) {
			throw new ValidationException("Parameter ["+name+"] is below ["+value+"] than allowed value ["+minValue+"].");
		}
		if ((maxValue!=null) && (value>maxValue)) {
			throw new ValidationException("Parameter ["+name+"] is above ["+value+"] than allowed value ["+maxValue+"].");
		}
	}
	
	/**
	 * Validates a Long value for minimum and maximum value.
	 * @param name the name of value is return in the ValidationException for better traceable exception message.
	 * @param value the string value to validated.
	 * @param minValue the minimum value (inclusive), if <code>null</code> than no minimum value.
	 * @param maxValue the maximum value (inclusive), if <code>null</code> than no maximum value.
	 * @throws ValidationException if value is out side minimum or maximum range.
	 */
	public static final void validateRange(String name,Long value,Long minValue,Long maxValue) throws ValidationException {
		validateNotNull(name, value);
		if ((minValue!=null) && (value<minValue)) {
			throw new ValidationException("Parameter ["+name+"] is below ["+value+"] than allowed value ["+minValue+"].");
		}
		if ((maxValue!=null) && (value>maxValue)) {
			throw new ValidationException("Parameter ["+name+"] is above ["+value+"] than allowed value ["+maxValue+"].");
		}
	}	
	
	/**
	 * Validates a Double value for minimum and maximum value.
	 * @param name the name of value is return in the ValidationException for better traceable exception message.
	 * @param value the string value to validated.
	 * @param minValue the minimum value (inclusive), if <code>null</code> than no minimum value.
	 * @param maxValue the maximum value (inclusive), if <code>null</code> than no maximum value.
	 * @throws ValidationException if value is out side minimum or maximum range.
	 */
	public static final void validateRange(String name,Double value,Double minValue,Double maxValue) throws ValidationException {
		validateNotNull(name, value);
		if ((minValue!=null) && (value<minValue)) {
			throw new ValidationException("Parameter ["+name+"] is below ["+value+"] than allowed value ["+minValue+"].");
		}
		if ((maxValue!=null) && (value>maxValue)) {
			throw new ValidationException("Parameter ["+name+"] is above ["+value+"] than allowed value ["+maxValue+"].");
		}
	}	
		
	/**
	 * Validates a Date value for minimum and maximum value.
	 * @param name the name of value is return in the ValidationException for better traceable exception message.
	 * @param value the string value to validated.
	 * @param minValue the minimum value (inclusive), if <code>null</code> than no minimum value.
	 * @param maxValue the maximum value (inclusive), if <code>null</code> than no maximum value.
	 * @throws ValidationException if value is out side minimum or maximum range.
	 */
	public static final void validateRange(String name,Date value,Date minValue,Date maxValue) throws ValidationException {
		validateNotNull(name, value);
		if ((minValue!=null) && (value.compareTo(minValue)<0)) {
			throw new ValidationException("Parameter ["+name+"] is below ["+value+"] than allowed value ["+minValue+"].");
		}
		if ((maxValue!=null) && (value.compareTo(maxValue)>0)) {
			throw new ValidationException("Parameter ["+name+"] is above ["+value+"] than allowed value ["+maxValue+"].");
		}
	}	
	
	/**
	 * Validates a TimeSpan value for minimum and maximum value.
	 * @param name the name of value is return in the ValidationException for better traceable exception message.
	 * @param value the string value to validated.
	 * @param minValue the minimum value (inclusive), if <code>null</code> than no minimum value.
	 * @param maxValue the maximum value (inclusive), if <code>null</code> than no maximum value.
	 * @throws ValidationException if value is out side minimum or maximum range.
	 */
	public static final void validateRange(String name,TimeSpan value,TimeSpan minValue,TimeSpan maxValue) throws ValidationException {
		validateNotNull(name, value);
		if ((minValue!=null) && (value.compareTo(minValue)<0)) {
			throw new ValidationException("Parameter ["+name+"] is below ["+value+"] than allowed value ["+minValue+"].");
		}
		if ((maxValue!=null) && (value.compareTo(maxValue)>0)) {
			throw new ValidationException("Parameter ["+name+"] is above ["+value+"] than allowed value ["+maxValue+"].");
		}
	}		
	
	/**
	 * Validates that directory is valid.
	 * @param name the name of value is return in the ValidationException for better traceable exception message.
	 * @param directory the directory
	 * @throws ValidationException if the directory is <code>null</code>, or not valid directory.
	 */
	public static final void validateDirectory(String name,Path directory) throws ValidationException {	
		validateNotNull(name, directory);
		validateDirectory(name,directory.toFile());		
	}
	
	/**
	 * Validates that a directory is valid.
	 * @param name the name of value is return in the ValidationException for better traceable exception message.	
	 * @param directory the directory.
	 * @throws ValidationException if directory is <code>null</code>, do not exists, isnt a directory, or cant read or write to it.
	 * @see File#isDirectory()
	 * @see File#exists()
	 * @see File#canRead()
	 * @see File#canWrite()
	 */
	public static final void validateDirectory(String name,File directory) throws ValidationException {		
		validateNotNull(name, directory);
		if (!directory.exists()) {
			throw new ValidationException("Directory ["+directory.getAbsolutePath()+"] do not exists.");
		} else if (!directory.isDirectory()) {
			throw new ValidationException("Directory ["+directory.getAbsolutePath()+"] is not a directory.");
		} else if (!directory.canRead()) {				
			throw new ValidationException("Can not read from directory ["+directory.getAbsolutePath()+"].");
		} else if (!directory.canWrite()) {
			throw new ValidationException("Can not write to directory ["+directory.getAbsolutePath()+"] .");
		}		
	}
	
	/**
	 * Validates that a file is valid.
	 * @param name the name of value is return in the ValidationException for better traceable exception message.	
	 * @param file the file.
	 * @throws ValidationException if file is <code>null</code>, do not exists, isnt a file, or cant read or write to it.
	 * @see File#isFile()
	 * @see File#exists()
	 * @see File#canRead()
	 * @see File#canWrite()
	 */
	public static final void validateFile(String name,Path file) throws ValidationException {	
		validateNotNull(name, file);
		validateFile(name,file.toFile());		
	}
	
	/**
	 * Validates that a file is valid.
	 * @param name the name of value is return in the ValidationException for better traceable exception message.	
	 * @param file the file.
	 * @throws ValidationException if file is <code>null</code>, do not exists, isnt a file, or cant read or write to it.
	 * @see File#isFile()
	 * @see File#exists()
	 * @see File#canRead()
	 * @see File#canWrite()
	 */
	public static final void validateFile(String name,File file) throws ValidationException {		
		validateNotNull(name, file);
		if (!file.exists()) {
			throw new ValidationException("File ["+file.getAbsolutePath()+"] do not exists.");
		} else if (!file.isFile()) {
			throw new ValidationException("File ["+file.getAbsolutePath()+"] is not a directory.");
		} else if (!file.canRead()) {				
			throw new ValidationException("Can not read from file ["+file.getAbsolutePath()+"].");
		} else if (!file.canWrite()) {
			throw new ValidationException("Can not write to file ["+file.getAbsolutePath()+"] .");
		}		
	}
	/**
	 * Some external exception indicated that a validation has failed, this method wrap this in a <tt>ValidationException</tt>. 
	 * @param name the name of value is return in the ValidationException for better traceable exception message.	
	 * @param e the exception
	 * @throws ValidationException an ValidationException that wrappes the exception. 
	 */
	public static final void validateFailed(String name,Exception e) throws ValidationException {
		throw new ValidationException("Validation failed for ["+name+"], "+e.getMessage(),e);
	}

} 