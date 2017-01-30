package dk.heick.io.monitoring.filter;

import java.io.File;
import java.io.FileFilter;

import dk.heick.io.monitoring.IOMonitoringConstants;

/**
 * A file filter that only accepts file that ends with a specific string.
 * @author Frederik Heick
 */
public class EndsWithFileFilter implements FileFilter {
	
	private String endsWith;
	private boolean caseSensitive=false;
	
	/**
	 * Gets a new instance of case insensitive EndsWithFileFilter that looks for ".xml" files.
	 * @return a new instance.
	 */
	public static final EndsWithFileFilter getNewXmlFilter() {
		return new EndsWithFileFilter(".xml");
	}
	
	/**
	 * Gets a new instance of case insensitive EndsWithFileFilter that looks for ".txt" files.
	 * @return a new instance.
	 */
	public static final EndsWithFileFilter getNewTxtFilter() {
		return new EndsWithFileFilter(".txt");
	}
	
	/**
	 * Gets a new instance of case insensitive EndsWithFileFilter that looks for lock files  ".lock" files.
	 * @return a new instance.
	 * @see IOMonitoringConstants#LOCK_FILE_EXTENSION
	 */
	public static final EndsWithFileFilter getNewLockFilter() {
		return new EndsWithFileFilter(IOMonitoringConstants.LOCK_FILE_EXTENSION);
	}
		
	/**
	 * Gets a new instance of case insensitive EndsWithFileFilter that looks for errorlog files  ".errorlog" files.
	 * @return a new instance.
	 * @see IOMonitoringConstants#ERROR_FILE_EXTENSION
	 */
	public static final EndsWithFileFilter getNewErrorLogFilter() {
		return new EndsWithFileFilter(IOMonitoringConstants.ERROR_FILE_EXTENSION);
	}	

	/**
	 * Constructor, where the filter is not case sensitive.
	 * @param endsWith the value the file name must end with to be accepted.
	 */
	public EndsWithFileFilter(String endsWith) {
		this(endsWith,false);
	}
	
	/**
	 * Constructor-
	 * @param endsWith the value the file name must end with to be accepted.
	 * @param caseSensitive if the test should be case sensitive or not.
	 */
	public EndsWithFileFilter(String endsWith,boolean caseSensitive) {		
		this.caseSensitive=caseSensitive;
		if (!caseSensitive) {
			this.endsWith=endsWith.toLowerCase();
		} else {
			this.endsWith=endsWith;
		}
	}
	
	/**
	 * The value the file should end with.
	 * @return the ends with value.
	 */
	public String getEndsWith() {
		return endsWith;
	}
	
	/**
	 * If the ends-with comparison should be case sensitive or not.
	 * @return is case sensitive comparison.
	 */
	public boolean isCaseSensitive() {
		return caseSensitive;
	}
	
	
	@Override
	public boolean accept(File file) {
		String name = file.getName();
		if (isCaseSensitive()) {
			return name.endsWith(getEndsWith());
		} else {
			return name.toLowerCase().endsWith(getEndsWith());
		}
	}

}
