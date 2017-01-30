package dk.heick.io.monitoring.ftp;

import org.apache.commons.net.ftp.FTPFile;

import dk.heick.io.monitoring.GenericFileChange;

/**
 * Value object that holds data about a ftp file actual state in a point in time. <br>
 * This includes
 * <ul>
 * 	<li>When was the file last modified. (org.apache.commons.net.ftp.FTPFile.getTimestamp())</li>
 *  <li>What was the file size in bytes. (org.apache.commons.net.ftp.FTPFile.getSize())</li>
 *  <li>The time the data above was extracted.</li>
 *  <li>The file is newer marked as locked.</li>
 * </ul>
 * @author Frederik Heick
 * @version 1.0
 * @see FTPFile#getTimestamp()
 * @see FTPFile#getSize()
 */
public class FtpFileChange extends GenericFileChange<WrappedFTPFile> {
	
	/**
	 * Constructor for the first time the ftp file is monitored.
	 * @param file the file
	 */
	public FtpFileChange(WrappedFTPFile file) {
		super(file);		
	}
	
	/**
	 * Constructor, with a previous  file change record.
	 * @param previous the previous FileChange instance. If <code>null</code> then it is the first time the ftp file is monitored.
	 * @param file the ftp file.
	 */
	public FtpFileChange(GenericFileChange<WrappedFTPFile> previous, WrappedFTPFile file) {
		super(previous, file);		
	}

	@Override
	protected boolean existsFile() {		
		return true;
	}
	@Override
	protected void init(WrappedFTPFile file) {
		setLocked(false);
		setModified(file.getFile().getTimestamp().getTimeInMillis());
		setSize(file.getFile().getSize());		
	}	
	
	@Override
	public String getFileName() {
		return getFile().getFile().getName();
	}
}
