package dk.heick.io.monitoring;

import java.io.File;

import dk.heick.io.monitoring.errorhandler.DefaultMonitorTaskLocalFileErrorHandler;
import dk.heick.io.monitoring.errorhandler.MonitorTaskLocalFileErrorHandler;
import dk.heick.io.monitoring.processor.FileProcessor;
import dk.heick.io.monitoring.utils.TimeSpan;
import dk.heick.io.monitoring.validation.ValidateUtils;
import dk.heick.io.monitoring.validation.Validation;
import dk.heick.io.monitoring.validation.ValidationException;

/**
 * Abstract configuration class that holds common configuration for local/remote directory monitoring. <br/>
 * this includes the ability to validate the configuration.<br/>
 * If not in the constructor or implicit set, the default values is as follows.
 * <ul>
 * 		<li>stableTime : IOMonitoringConstants.DEFAULT_STABLE_TIME = 1500 ms</li>
 * 		<li>archiving : IOMonitoringConstants.DEFAULT_ARCHIVING = true</li>
 * 		<li>maxMessagesPolling : IOMonitoringConstants.DEFAULT_MAX_MESSAGE_POLLING = 1000 files</li>
 * 		<li>maxProcessingTime : IOMonitoringConstants.DEFAULT_MAX_PROCESSING_TIME = 5 minutes</li>
 * 		<li>monitorTimeout : IOMonitoringConstants.DEFAULT_MONITOR_TIMEOUT = 1 hour</li>
 * 		<li>lockFileTimeout : IOMonitoringConstants.DEFAULT_LOCK_FILE_TIMEOUT = 20 minutes</li>
 * 		<li>localFileErrorHandler : new DefaultMonitorTaskLocalFileErrorHandler()</li>
 * </ul>
 * @author Frederik Heick
 * @version 1.0
 * @see IOMonitoringConstants
 */
public abstract class MonitorTaskConfiguration implements Validation {
	
	private File directory;
	private FileProcessor fileProcessor;
	//
	private File directoryArchive;
	private File directoryError;	
	private File directoryProcess;
	//
	private TimeSpan stableTime = IOMonitoringConstants.DEFAULT_STABLE_TIME;	
	private boolean archiving = IOMonitoringConstants.DEFAULT_ARCHIVING;
	private int maxMessagesPolling = IOMonitoringConstants.DEFAULT_MAX_MESSAGE_POLLING;
	private TimeSpan maxProcessingTime = IOMonitoringConstants.DEFAULT_MAX_PROCESSING_TIME;
	private TimeSpan monitorTimeout = IOMonitoringConstants.DEFAULT_MONITOR_TIMEOUT;
	private TimeSpan lockFileTimeout = IOMonitoringConstants.DEFAULT_LOCK_FILE_TIMEOUT;
	private MonitorTaskLocalFileErrorHandler localFileErrorHandler = new DefaultMonitorTaskLocalFileErrorHandler();
	private boolean verbose = false;
	
	/**
	 * Constructor of MonitorTaskConfiguration
	 * @param directory the directory which has to be monitor
	 * @param fileProcessor the implementation of the FileProcessor interface. 
	 */
	public MonitorTaskConfiguration(File directory,FileProcessor fileProcessor) {
		this(directory,fileProcessor,IOMonitoringConstants.DEFAULT_STABLE_TIME);
	}
		
	/**
	 * Constructor of MonitorTaskConfiguration
	 * @param directory the directory which has to be monitor
	 * @param fileProcessor the implementation of the FileProcessor interface. 
	 * @param stableTime the amount of time a file has to be stable before being processed.
	 */
	public MonitorTaskConfiguration(File directory,FileProcessor fileProcessor,TimeSpan stableTime) {
		super();
		this.directory=directory;		
		this.fileProcessor=fileProcessor;		
		this.directoryArchive = new File(directory,IOMonitoringConstants.DIRECTORY_NAME_ARCHIVE);
		this.directoryError = new File(directory,IOMonitoringConstants.DIRECTORY_NAME_ERROR);
		this.directoryProcess = new File(directory,IOMonitoringConstants.DIRECTORY_NAME_PROCESS);
		setStableTime(stableTime);
	}
	
	@Override
	public void validate() throws ValidationException {
		ValidateUtils.validateNotNull("Directory", directory);
		if ((!directory.exists()) && (!directory.mkdir())) {
			throw new ValidationException("Unable to create directory ["+directory.getAbsolutePath()+"].");
		}
		
		ValidateUtils.validateDirectory("Directory",directory);
		ValidateUtils.validateRange("StableTime",stableTime,new TimeSpan(100L),null);
		//
		ValidateUtils.validateNotNull("FileProcessor",getFileProcessor());
		getFileProcessor().validate();
		
		if ((!directoryArchive.exists()) && (!directoryArchive.mkdir())) {
			throw new ValidationException("Unable to create directory ["+directoryArchive.getAbsolutePath()+"].");
		}
		if ((!directoryError.exists()) && (!directoryError.mkdir())) {
			throw new ValidationException("Unable to create directory ["+directoryError.getAbsolutePath()+"].");
		}
		if ((!directoryProcess.exists()) && (!directoryProcess.mkdir())) {
			throw new ValidationException("Unable to create directory ["+directoryProcess.getAbsolutePath()+"].");
		}
	}

	
	/**
	 * The directory in where we will monitor files.
	 * @return the monitoring directory
	 */
	public final File getDirectory() {
		return directory;
	}
	public final FileProcessor getFileProcessor() {
		return fileProcessor;
	}
	public final File getDirectoryArchive() {
		return directoryArchive;
	}
	public final File getDirectoryError() {
		return directoryError;
	}
	public final File getDirectoryProcess() {
		return directoryProcess;
	}
	public final TimeSpan getStableTime() {
		return stableTime;
	}
	
	public final int getMaxMessagesPolling() {
		return maxMessagesPolling;
	}
	public final TimeSpan getMaxProcessingTime() {
		return maxProcessingTime;
	}
	public final boolean isArchiving() {
		return archiving;
	}
	public final TimeSpan getMonitorTimeout() {
		return monitorTimeout;
	}
	public final TimeSpan getLockFileTimeout() {
		return lockFileTimeout;
	}
	public final MonitorTaskLocalFileErrorHandler getLocalFileErrorHandler() {
		if (localFileErrorHandler==null) {
			localFileErrorHandler = new DefaultMonitorTaskLocalFileErrorHandler();
		}
		return localFileErrorHandler;
	}	
	public boolean isVerbose() {
		return verbose;
	}

	public final MonitorTaskConfiguration setArchiving(boolean archiving) {
		this.archiving = archiving;
		return this;
	}
	public final MonitorTaskConfiguration withArchiving() {
		return setArchiving(true);
	}
	public final MonitorTaskConfiguration withNoArchiving() {
		return setArchiving(false);
	}	
	
	public final MonitorTaskConfiguration setMaxMessagesPolling(int maxMessagesPolling) {		
		if (maxMessagesPolling>0) {
			this.maxMessagesPolling = maxMessagesPolling;
		}
		return this;
	}
	public final MonitorTaskConfiguration setMaxProcessingTime(TimeSpan maxProcessingTime) {	
		this.maxProcessingTime = maxProcessingTime;
		return this;
	}
	public final MonitorTaskConfiguration withNoMaxProcessingTime() {
		setMaxProcessingTime(null);
		return this;
	}
	public final MonitorTaskConfiguration setStableTime(TimeSpan stableTime) {
		ValidateUtils.validateNotNullNotChecked("StableTime", stableTime);
		if (stableTime.isGreaterThan(IOMonitoringConstants.MIN_STABLE_TIME)) {
			this.stableTime = stableTime;
		}
		return this;
	}
	public final MonitorTaskConfiguration setMonitorTimeout(TimeSpan monitorTimeout) {
		if (monitorTimeout!=null) {
			if (monitorTimeout.isGreaterThan(IOMonitoringConstants.MIN_MONITOR_TIMEOUT)) {
				this.monitorTimeout = monitorTimeout;
			}
		} else {
			this.monitorTimeout = null;
		}		
		return this;
	}
	
	public final MonitorTaskConfiguration withNoMonitorTimeout() {
		setMonitorTimeout(null);
		return this;
	}
	
	public final MonitorTaskConfiguration setLockFileTimeout(TimeSpan lockFileTimeout) {
		if (lockFileTimeout!=null) {
			if (lockFileTimeout.isGreaterThan(IOMonitoringConstants.MIN_LOCK_FILE_TIMEOUT)) {
				this.lockFileTimeout = lockFileTimeout;
			}
		} else {
			this.lockFileTimeout = null;
		}
		return this;
	}
	public final MonitorTaskConfiguration withNoLockFileTimeout() {
		setLockFileTimeout(null);
		return this;
	}
	
	public final MonitorTaskConfiguration setLocalFileErrorHandler(MonitorTaskLocalFileErrorHandler localFileErrorHandler) {
		if (localFileErrorHandler!=null) {
			this.localFileErrorHandler = localFileErrorHandler;
		}
		return this;
	}
	
	public MonitorTaskConfiguration withVerbose() {
		this.verbose = true;
		return this;
	}
	public MonitorTaskConfiguration withNoVerbose() {
		this.verbose = false;
		return this;
	}
		
	
	public final boolean doContinueProcessing(long processingStart) {
		if (getMaxProcessingTime()==null) {
			return true;
		} else {
			long time = System.currentTimeMillis() - processingStart;
			return time<getMaxProcessingTime().getTime();
		}
	}
	public final boolean doContinuePollFiles(long filesPolled) {
		if (getMaxMessagesPolling()<1) {
			return true;
		} else {			
			return filesPolled<getMaxMessagesPolling();
		}
	}	
	public final boolean doContinueMonitorFile(long fileMonitoringTime) {
		if (getMonitorTimeout().isLessThan(new TimeSpan(1))) {
			return true;
		} else {			
			return fileMonitoringTime<getMonitorTimeout().getTime();
		}
	}
	

}
