package dk.heick.io.monitoring.filter;

import java.io.File;
import java.io.FileFilter;

/**
 * Filters for files only, and child filter if any. <br>
 * Will first verify that the File is a file (<tt>File.isFile()</tt>) than run the child filter if not <code>null</code>.
 * @author Frederik Heick
 * @version 1.0
 * @see File#isFile()
 */
public class OnlyFilesFileFilter extends ChildFileFilter {
	
	/**
	 * Constructor, will only filter for files only.
	 * @see File#isFile()
	 */
	public OnlyFilesFileFilter() {
		super();
	}	
	
	/**
	 * Constructor, will only filter for files only, and for child filter if not <code>null</code>.
	 * @param childFilter the child filter which will be run afterwards if not <code>null</code>.
	 */
	public OnlyFilesFileFilter(FileFilter childFilter) {
		super(childFilter);
	}
	
	
	@Override
	public boolean accept(File file) {
		return file.isFile() && acceptChildFilter(file);
	}
}
