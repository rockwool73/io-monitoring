package dk.heick.io.monitoring.cleanup;

import java.io.File;
import java.io.FileFilter;

import dk.heick.io.monitoring.IOMonitoringConstants;
import dk.heick.io.monitoring.MonitorTaskConfiguration;
import dk.heick.io.monitoring.errorhandler.DefaultDirectoryCleanupErrorHandler;
import dk.heick.io.monitoring.errorhandler.DirectoryCleanupErrorHandler;
import dk.heick.io.monitoring.filter.OnlyFilesFileFilter;
import dk.heick.io.monitoring.utils.TimeSpan;
import dk.heick.io.monitoring.validation.ValidateUtils;
import dk.heick.io.monitoring.validation.Validation;
import dk.heick.io.monitoring.validation.ValidationException;

/**
 * A configuration class that holds configuration for local directory cleanup. <br/>
 * this includes the ability to validate the configuration.<br/>
 * If not in the constructor or implicit set, the default values is as follows.
 * <ul>
 * 		<li>keepContentTime : IOCleanupConstants.DEFAULT_KEEP_CONTENT_TIME = 14 days</li>
 * 		<li>maxDeletingProcessingTime : IOCleanupConstants.DEFAULT_MAX_DELETING_PROCESSING_TIME = 30 minutes</li>
 * 		<li>filter : new OnlyFilesFileFilter()</li>
 * 		<li>deleteEmptyDirectories : true</li>
 * 		<li>maxDepth : -1 (no max depth)</li>
 * 		<li>enabled : true</li>
 * </ul>
 * @author Frederik Heick
 * @version 1.0
 * @see IOMonitoringConstants
 */
public class DirectoryCleanupConfiguration implements Validation {

	private boolean basedOnMonitorTask=false;
	private File directory = null;
	private TimeSpan keepContentTime = IOCleanupConstants.DEFAULT_KEEP_CONTENT_TIME;
	private TimeSpan maxDeletingProcessingTime = IOCleanupConstants.DEFAULT_MAX_DELETING_PROCESSING_TIME;
	private FileFilter filter = new OnlyFilesFileFilter();
	private boolean deleteEmptyDirectories=true;
	private int maxDepth=-1;
	private boolean enabled=true;
	private DirectoryCleanupErrorHandler errorHandler = new DefaultDirectoryCleanupErrorHandler();
	
	
	public final static DirectoryCleanupConfiguration cleanupArchive(MonitorTaskConfiguration monitorTask,int keepContentTimeInDays) {
		DirectoryCleanupConfiguration result = new DirectoryCleanupConfiguration(monitorTask.getDirectoryError());
		result.setKeepContentTime(TimeSpan.createDays(keepContentTimeInDays))
			  .setDeleteEmptyDirectories(true)
			  .setMaxDepth(1)
			  .setBasedOnMonitorTask(true);
		return result;		
	}
	
	public final static DirectoryCleanupConfiguration cleanupError(MonitorTaskConfiguration monitorTask,int keepContentTimeInDays) {
		DirectoryCleanupConfiguration result = new DirectoryCleanupConfiguration(monitorTask.getDirectoryError());
		result.setKeepContentTime(TimeSpan.createDays(keepContentTimeInDays))
			  .setDeleteEmptyDirectories(true)
			  .setMaxDepth(1)
			  .setBasedOnMonitorTask(true);
		return result;		
	}
	
	public DirectoryCleanupConfiguration(File directory) {
		super();
		this.directory=directory;
	}
	
	
	@Override
	public void validate() throws ValidationException {
		ValidateUtils.validateDirectory("Directory", getDirectory());
		ValidateUtils.validateNotNull("FileFilter", getFilter());
		ValidateUtils.validateNotNull("KeepContentTime", getKeepContentTime());		
		if (isBasedOnMonitorTask()) {
			if (getMaxDepth()<1) {
				throw new ValidationException("To clean MonitorTask ["+getDirectory().getName()+"] directory, the configuration either needs to be recursive or have a maxdepth of at least 1.");
			}					
		}
	}
	


	
	public final File getDirectory() {
		return directory;
	}
	public final TimeSpan getMaxDeletingProcessingTime() {
		return maxDeletingProcessingTime;
	}
	public TimeSpan getKeepContentTime() {
		return keepContentTime;
	}
	public final boolean isDeleteEmptyDirectories() {
		return deleteEmptyDirectories;
	}
	public final FileFilter getFilter() {
		return filter;
	}
	public final int getMaxDepth() {
		return maxDepth;
	}
	public final boolean isNoMaxDepth() {
		return getMaxDepth()<0;
	}
	public final boolean isEnabled() {
		return enabled;
	}
	public final boolean isDisabled() {
		return !isEnabled();
	}
	public final DirectoryCleanupErrorHandler getErrorHandler() {
		if (errorHandler==null) {
			errorHandler = new DefaultDirectoryCleanupErrorHandler();
		}
		return errorHandler;
	}
	
	protected final boolean isBasedOnMonitorTask() {
		return basedOnMonitorTask;
	}
	protected final void setBasedOnMonitorTask(boolean basedOnMonitorTask) {
		this.basedOnMonitorTask = basedOnMonitorTask;
	}
	
	
	
	public final DirectoryCleanupConfiguration setMaxDeletingProcessingTime(long maxDeletingProcessingTime) {		
		if (IOCleanupConstants.DEFAULT_MAX_DELETING_PROCESSING_TIME.isLessThan(TimeSpan.createMilliSeconds(maxDeletingProcessingTime))) {
			this.maxDeletingProcessingTime = TimeSpan.createMilliSeconds(maxDeletingProcessingTime);
		}
		return this;
	}
	public final DirectoryCleanupConfiguration setKeepContentTime(TimeSpan keepContentTime) {
		this.keepContentTime = keepContentTime;
		return this;
	}
	public final DirectoryCleanupConfiguration setDeleteEmptyDirectories(boolean deleteEmptyDirectories) {
		this.deleteEmptyDirectories = deleteEmptyDirectories;
		return this;
	}
	public final DirectoryCleanupConfiguration setFilter(FileFilter filter) {
		this.filter = new OnlyFilesFileFilter(filter);
		return this;
	}
	public final DirectoryCleanupConfiguration setMaxDepth(int maxDepth) {
		this.maxDepth = maxDepth;
		return this;
	}
	public final DirectoryCleanupConfiguration setNoMaxDepth() {
		return setMaxDepth(IOCleanupConstants.NO_MAX_DEPTH);
	}
	
	protected final DirectoryCleanupConfiguration setEnabled(boolean enabled) {
		this.enabled = enabled;
		return this;
	}	
	public final DirectoryCleanupConfiguration enable() {
		return setEnabled(true);
	}
	public final DirectoryCleanupConfiguration disable() {
		return setEnabled(false);
	}
	
	public final DirectoryCleanupConfiguration setErrorHandler(DirectoryCleanupErrorHandler errorHandler) {
		this.errorHandler = errorHandler;
		return this;
	}
	

}
