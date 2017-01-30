package dk.heick.io.monitoring.filter;

import java.io.File;
import java.io.FileFilter;

/**
 * Filters for directories only, and child filter if any. <br>
 * Will first verify that the File is a directory (<tt>File.isDirectory()</tt>) than run the child filter if not <code>null</code>.
 * @author Frederik Heick
 * @version 1.0
 * @see File#isDirectory()
 */
public class OnlyDirectoryFileFilter extends ChildFileFilter {
	
	/**
	 * Constructor, will only filter for directory only.
	 * @see File#isFile()
	 */
	public OnlyDirectoryFileFilter() {
		super();
	}	
	
	/**
	 * Constructor, will only filter for directories only, and for child filter if not <code>null</code>.
	 * @param childFilter the child filter which will be run afterwards if not <code>null</code>.
	 */
	public OnlyDirectoryFileFilter(FileFilter childFilter) {
		super(childFilter);
	}	
	
	@Override
	public boolean accept(File file) {
		return file.isDirectory() && acceptChildFilter(file);
	}
}
