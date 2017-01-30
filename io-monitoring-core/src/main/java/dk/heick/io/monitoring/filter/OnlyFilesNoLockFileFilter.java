package dk.heick.io.monitoring.filter;

import java.io.File;
import java.io.FileFilter;

import dk.heick.io.monitoring.utils.FileUtils;

/**
 * File filter the only accepts file which is files and is not a "lock" file.
 * @author Frederik Heick
 * @see File#isFile()
 * @see LockFileUtils#isLockFile(File)
 */
public class OnlyFilesNoLockFileFilter implements FileFilter {
	
	/**
	 * Constructor.
	 */
	public OnlyFilesNoLockFileFilter() {
		super();
	}
	@Override
	public boolean accept(File file) {
		return file.isFile() && !FileUtils.isLockFile(file);
	}

}