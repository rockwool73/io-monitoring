package dk.heick.io.monitoring;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.heick.io.monitoring.filter.OnlyFilesNoLockFileFilter;
import dk.heick.io.monitoring.utils.FileUtils;
import dk.heick.io.monitoring.utils.StringUtils;
import dk.heick.io.monitoring.validation.ValidateUtils;
import dk.heick.io.monitoring.validation.Validation;
import dk.heick.io.monitoring.validation.ValidationException;

/**
 * Abstract class that implemented File monitoring capabilities by the conventions described.
 * @author Frederik Heick
 * @param <FileType> The "File" instance, currently either "<code>java.io.File</code>" or "<code>org.apache.commons.net.ftp.FTPFile</code>".  
 * @param <Configuration> and instance that inherited <code>MonitorTaskConfiguration</code>
 */																									//TODO
public abstract class MonitorTask<FileType,Configuration extends MonitorTaskConfiguration> implements Validation {
	
	private Configuration configuration;
	
	private Map<FileType,GenericFileChange<FileType>> fileMonitor = Collections.synchronizedMap(new HashMap<FileType,GenericFileChange<FileType>>());
	private Logger logger;	

	public MonitorTask(Configuration configuration) throws NullPointerException, ValidationException {
		super();		
		this.configuration=configuration;
		this.configuration.validate();
		initialize();
	}
	
	/**
	 * Initialization, initiated by the constructor.
	 */
	protected abstract void initialize();
	
	/**
	 * Here you detect new "files" and add them to the FileMonitor
	 * @see #getFileMonitor()
	 */
	protected abstract void detecting();
	
	/**
	 * Here you monitor the detected "files" and move them to ".process" directory when they are stable
	 * @see MonitorTask#moveFileToProcess(File)
	 * @see MonitorTaskConfiguration#getDirectoryProcess()
	 * @see IOMonitoringConstants#DIRECTORY_NAME_PROCESS
	 */
	protected abstract void monitoring();
	
	/**
	 * Here you process the stable "files" that has been moved to ".process" directory.
	 * Generic implementation is provided. 
	 */
	protected void processing() {
		verboseDebug("Running task - processing");
		if (getConfiguration().getFileProcessor()!=null) {
			boolean doNext=true;
			long processingStart=System.currentTimeMillis();
			for (File file : getConfiguration().getDirectoryProcess().listFiles(new OnlyFilesNoLockFileFilter())) {		
				File lockFile = generateLockFile(file);				
				if ((doNext) && (file.exists()) && (!lockFile.exists())) {	
					long start = System.currentTimeMillis();
					Properties context = new Properties();
					try {										
						Files.createFile(lockFile.toPath());
						getConfiguration().getFileProcessor().beforeProcess(context,file);
						getConfiguration().getFileProcessor().process(context,file);
						getLogger().info("Processed file ["+file.getName()+"] successfully in ["+(System.currentTimeMillis()-start)+"] ms.");
						moveFileToArchive(file);
						doNext = getConfiguration().getFileProcessor().onSuccess(context, start, file) &&
								 getConfiguration().doContinueProcessing(processingStart);
												
						//
					} catch (Exception e) {																		
						moveFileToError(file,e);						
						getLogger().error("File ["+file.getName()+"]  failed in ["+(System.currentTimeMillis()-start)+"] ms and moved to ["+IOMonitoringConstants.DIRECTORY_NAME_ERROR+"] directory, "+e.getMessage(),e);
						doNext = getConfiguration().getFileProcessor().onError(context,start,file, e) &&
								 getConfiguration().doContinueProcessing(processingStart);						
					} finally {		
						deleteFile(lockFile);											
					}
				} else if ((file.exists()) && (lockFile.exists()) && (hasLockFileTimedOut(lockFile))) {
					if (!FileUtils.isFileLocked(file)) {							
						String msg = String.format("Lock file [%s] for File [%s] is older than [%d] ms, which is lock file timeout.",lockFile.getAbsolutePath(),file.getAbsolutePath(),getConfiguration().getLockFileTimeout());
						deleteFile(lockFile);							
						moveFileToError(file, new Exception(msg));
					} else {
						getLogger().error("Lockfile ["+lockFile.getAbsolutePath()+"] has timed out but File ["+file.getAbsolutePath()+"] still seems to be locked.");
					}
				}
			}	
		}
	}
	
	/**
	 * Gets the number of monitored files.
	 * @return monitored files count.
	 */
	public final int size() {
		return fileMonitor.size(); 
	}
	
	public final Configuration getConfiguration() {
		return configuration;
	}
	public final Logger getLogger() {
		if (logger==null) {
			logger = LoggerFactory.getLogger(getClass().getName());					
		}
		return logger;
	}
	
	public final void runTask() {
		verboseDebug("Running task");
		//STEP 1 - Detect new files
		detecting();
		//STEP 2 - move stable files to process directory
		monitoring();			
		//STEP 3 - process files that is not being processed		
		processing();
	}
	
	@Override
	public void validate() throws ValidationException {
		ValidateUtils.validateNotNull("configuration", getConfiguration());
		getConfiguration().validate();		
	}	

	protected final Iterator<FileType> getMonitoredFiles() {
		return fileMonitor.keySet().iterator();
	}
	protected final Map<FileType,GenericFileChange<FileType>> getFileMonitor() {
		return fileMonitor;
	}
	public final GenericFileChange<FileType> getMonitoredFile(FileType fileType) {
		return getFileMonitor().get(fileType);
	}

	protected final void moveFileToInput(File file) {
		try {
			verboseDebug("Moving file ["+file.getAbsolutePath()+"] to input folder ["+getConfiguration().getDirectory()+"].");
			moveFile(file,"",getConfiguration().getDirectory());
		} catch (IOException e) {
			getLogger().error(e.getMessage(),e);
			getConfiguration().getLocalFileErrorHandler().onMoveFileFailure(file, getConfiguration().getDirectory(), e);    			
		}
	}
	protected final void moveFileToProcess(File file) {
		try {
			verboseDebug("Moving file ["+file.getAbsolutePath()+"] to process folder ["+getConfiguration().getDirectoryProcess()+"].");
			moveFile(file,"",getConfiguration().getDirectoryProcess());
		} catch (IOException e) {
			getLogger().error(e.getMessage(),e);			
			getConfiguration().getLocalFileErrorHandler().onMoveFileFailure(file, getConfiguration().getDirectoryProcess(), e);
		}
	}
	
	protected final void moveFileToArchive(File file) {
		if (getConfiguration().isArchiving()) {
			try {
				verboseDebug("Moving file ["+file.getAbsolutePath()+"] to archive folder ["+getDirectoryArchiveSubdirectory()+"].");
				moveFile(file,"",getDirectoryArchiveSubdirectory());
			} catch (IOException e) {
				getLogger().error(e.getMessage(),e);				
				getConfiguration().getLocalFileErrorHandler().onMoveFileFailure(file, getDirectoryArchiveSubdirectory(), e);
			}
		} else {
			deleteFile(file);				
		}
		
	}
	protected final void moveFileToError(File file,Exception e) {		
		String prefix = getFilenamePrefix(getDirectoryErrorSubdirectory(),file);
		File errorFile = createErrorFile(file,prefix,e);
		try {
			verboseDebug("Moving file ["+file.getAbsolutePath()+"] to error folder ["+getDirectoryErrorSubdirectory()+"].");
			File fileInErrorFolder = moveFile(file,prefix,getDirectoryErrorSubdirectory());
			if (getConfiguration().getLocalFileErrorHandler().onMovedToErrorDirectory(file, errorFile, e)==false) {
				deleteFile(fileInErrorFolder);
				deleteFile(errorFile);
			}
		} catch (IOException e2) {
			getConfiguration().getLocalFileErrorHandler().onMoveFileFailure(file, getDirectoryErrorSubdirectory(), e2);
		}
	}

	protected final void deleteFile(File file) {
		try {
			verboseDebug("Deleting file ["+file.getAbsolutePath()+"].");
			FileUtils.deleteFile(file);
		} catch (IOException e) {
			getLogger().error(e.getMessage(),e);
			getConfiguration().getLocalFileErrorHandler().onDeleteFileFailure(file, e);
		}
	}
	
	protected final boolean hasLockFileTimedOut(File lockFile) {
		if (getConfiguration().getLockFileTimeout()==null) {
			return false;
		} else {		
			long lockFileAge = System.currentTimeMillis() - lockFile.lastModified();
			return (lockFileAge > getConfiguration().getLockFileTimeout().getTime());				
		}
	}
	
	private final File moveFile(File file,String prefix,File toDirectory) throws IOException {
		File newFile = new File(toDirectory,prefix+file.getName());		
		FileUtils.rename(file, newFile);
		return newFile;
	}
	private final File createErrorFile(File file,String prefix,Exception exception) {
		File errorFile = generateErrorFile(file,prefix);		
		FileWriter fileWriter = null;
		try {
			//			
			fileWriter = new FileWriter(errorFile);
			if (exception!=null) {				
				fileWriter.write(StringUtils.getStacktrace(exception));
			} else {
				fileWriter.write("Exception is null.");
			}
			//		
			fileWriter.flush();
			return errorFile;
			//
		} catch (IOException e) {			
			getLogger().error("Error saving error file infomation ["+errorFile.getAbsolutePath()+"]",e);
			return null;
		} finally {
			if (fileWriter!=null) {
				try {
					fileWriter.close();
				} catch (IOException e) {					
				}
			}
		}
	}	
	
	
	public final boolean isLockFile(File file) {
		if (file!=null) {
			String ext = "."+FileUtils.getExtension(file);		
			return (ext.equalsIgnoreCase(IOMonitoringConstants.LOCK_FILE_EXTENSION));
		} else {
			return false;
		}		
	}
	

	public final void verboseDebug(String msg) {
		if (getConfiguration().isVerbose()) {
			getLogger().debug(msg);
		}		
	}
	
	public final File getDirectoryArchiveSubdirectory() {
		return createDateSubDirectory(getConfiguration().getDirectoryArchive());
	}
	public final File getDirectoryErrorSubdirectory() {
		return createDateSubDirectory(getConfiguration().getDirectoryError());
	}	
	/**
	 * 
	 * @param file
	 * @return
	 */
	protected final File generateLockFile(File file) {
		return new File(file.getParentFile(),file.getName()+IOMonitoringConstants.LOCK_FILE_EXTENSION);
	}
	
	/**
	 * 
	 * @param file
	 * @param prefix
	 * @return
	 */
	protected final File generateErrorFile(File file,String prefix) {
		return new File(getDirectoryErrorSubdirectory(),prefix+file.getName()+IOMonitoringConstants.ERROR_FILE_EXTENSION);
	}
	
	/**
	 * 
	 * @param directory
	 * @return
	 */
	protected File createDateSubDirectory(File directory) {
		File result = new File(directory,IOMonitoringConstants.SUB_DIRECTORY_DATE_FORMATTER.format(new Date()));
		if (!result.exists()) {
			result.mkdir();
		}
		return result;
	}
	
	
	protected final String getFilenamePrefix(File toDirectory,File file) {
		File newFile = new File(toDirectory,file.getName());
		if (newFile.exists()) {
			return IOMonitoringConstants.FILE_DATE_FORMATTER.format(new Date())+"_";
		} else {
			return "";
		}		
	}
}
