package dk.heick.io.monitoring.file;

import java.io.File;

import dk.heick.io.monitoring.GenericFileChange;
import dk.heick.io.monitoring.utils.FileUtils;

/**
 * Value object that holds data about a file actual state in a point in time. <br>
 * This includes
 * <ul>
 * 	<li>When was the file last modified.</li>
 *  <li>What was the file size in bytes.</li>
 *  <li>The time the data above was extracted.</li>
 * </ul>
 * @author Frederik Heick
 * @version 1.0
 */
public class FileChange extends GenericFileChange<File>  {
	
	/**
	 * Constructor for the first time the file is monitored.
	 * @param file the file
	 */
	public FileChange(File file) {
		super(file);		
	}

	/**
	 * Constructor, with a previous file change record.
	 * @param previous the previous FileChange instance. If <code>null</code> then it is the first time the file is monitored.
	 * @param file the file.
	 */
	public FileChange(GenericFileChange<File> previous) {
		super(previous);		
	}
	
	@Override
	protected boolean existsFile() {
		return getFile().exists();
	}
	
	@Override
	public String getFileName() {
		return getFile().getAbsolutePath();
	}
	
	@Override
	protected void init(File file) {
		setLocked(FileUtils.isFileLocked(file));
		setModified(file.lastModified());
		setSize(file.length());		
	}
	
}
