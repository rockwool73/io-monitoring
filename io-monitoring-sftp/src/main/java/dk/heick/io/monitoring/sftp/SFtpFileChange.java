package dk.heick.io.monitoring.sftp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.heick.io.monitoring.GenericFileChange;

public class SFtpFileChange extends GenericFileChange<WrappedSFtpFile> {

	private Logger logger;	
	
	
	/**
	 * Constructor for the first time the sftp file is monitored.
	 * @param file the file
	 */
	public SFtpFileChange(WrappedSFtpFile file) {
		super(file);		
	}
	
	/**
	 * Constructor, with a previous  file change record.
	 * @param previous the previous FileChange instance. If <code>null</code> then it is the first time the sftp file is monitored.
	 * @param file the sftp file.
	 */
	public SFtpFileChange(GenericFileChange<WrappedSFtpFile> previous, WrappedSFtpFile file) {
		super(previous, file);		
	}
	
	
	
	public final Logger getLogger() {
		if (logger==null) {
			logger = LoggerFactory.getLogger(getClass().getName());					
		}
		return logger;
	}

	@Override
	protected void init(WrappedSFtpFile file) {
		setLocked(false);				
		setModified(file.getFile().getAttrs().getMTime());		
		setSize(file.getFile().getAttrs().getSize());		
	}

	@Override
	protected boolean existsFile() {
		return getFile()!=null;
	}

	@Override
	public String getFileName() {
		return getFile().getFileName();
	}
	

	

}
