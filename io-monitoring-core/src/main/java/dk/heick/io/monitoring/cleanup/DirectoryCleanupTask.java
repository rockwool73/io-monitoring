package dk.heick.io.monitoring.cleanup;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.heick.io.monitoring.utils.FileLastModifiedComparator;
import dk.heick.io.monitoring.utils.FileUtils;
import dk.heick.io.monitoring.validation.ValidateUtils;
import dk.heick.io.monitoring.validation.Validation;
import dk.heick.io.monitoring.validation.ValidationException;

/**
 * A task definition that cleans up a directory.
 * @author Frederik Heick
 * @version 1.0
 */
public class DirectoryCleanupTask implements Validation {
		
	private DirectoryCleanupConfiguration configuration;
	private Logger logger=null;

	/**
	 * Constructor
	 * @param configuration the cleanup configuration, <code>null</code> is not allowed, will fail validation.
	 * @see #validate()
	 */
	public DirectoryCleanupTask(DirectoryCleanupConfiguration configuration) {			
		this.configuration=configuration;
	}
	
	/**
	 * Gets the configuration.
	 * @return
	 */
	public DirectoryCleanupConfiguration getConfiguration() {
		return configuration;
	}

	/**
	 * Validates that the configuration is not <code>null</code> and the <tt>configuration</tt> is valid.
	 */
	@Override
	public void validate() throws ValidationException {
		ValidateUtils.validateNotNull("DirectoryCleanupConfiguration", getConfiguration());
		getConfiguration().validate();		
	}
	
	/**
	 * Runs the cleanup task.
	 */
	public void runTask() {
		cleanup();
	}
	
	/**
	 * Gets the logger
	 * @return the logger.
	 */
	public Logger getLogger() {
		if (logger==null) {
			logger = LoggerFactory.getLogger(getClass().getName()+"["+getConfiguration().getDirectory().getAbsolutePath()+"]");
		}
		return logger;
	}
	
	private void cleanup() {
		if (!getConfiguration().isEnabled()) {
			return;			
		} else {
			CleanupProgress progress = new CleanupProgress();			
			try {
				internalCleanup(progress,getConfiguration().getDirectory());
				if (progress.getProgressTime()>getConfiguration().getMaxDeletingProcessingTime().getTime()) {
					getLogger().info("Stopped cleanup. Reached max progress time ["+getConfiguration().getMaxDeletingProcessingTime()+"] Deleted files ["+progress.getFilesDeleted()+"], Deleted directories ["+progress.getDirectoriesDeleted()+"]. In time ["+progress.getProgressTime()+"] ms.");
				} else {
					getLogger().info("Stopped cleanup. Done Deleted files ["+progress.getFilesDeleted()+"], Deleted directories ["+progress.getDirectoriesDeleted()+"]. In time ["+progress.getProgressTime()+"] ms.");
				}
			} catch (IOException e) {
				getLogger().error(e.getMessage(),e);
			}			
		}
	}
	
	private boolean processDepth(CleanupProgress progress) {
		if (getConfiguration().isNoMaxDepth()) {
			return true;
		} else {
			return (progress.getLevel()<=getConfiguration().getMaxDepth());
		}
	}
	 
	private boolean isFileToOld(File file) {
		long age = System.currentTimeMillis() - file.lastModified();
		return age>getConfiguration().getKeepContentTime().getTime();
	}
	
	private void internalCleanup(CleanupProgress progress,File directory) throws IOException {		
		if (processDepth(progress)) {			
			File[] paths =directory.listFiles();
			Arrays.sort(paths,new FileLastModifiedComparator());			
			for (File path : paths) {
				if (progress.getProgressTime()>getConfiguration().getMaxDeletingProcessingTime().getTime()) {
					getLogger().info("Stopped cleanup. Reached max time ["+getConfiguration().getMaxDeletingProcessingTime()+"] Deleted files ["+progress.getFilesDeleted()+"], Deleted directories ["+progress.getDirectoriesDeleted()+"]. In time ["+progress.getProgressTime()+"] ms.");
					return;
				} else if (path.isDirectory()) {						
						progress.incrementLevel();
						internalCleanup(progress, path);	
						deleteEmptyDirectory(progress,path);						
				} else if (path.isFile()) {
					if (isFileToOld(path)) {
						try {							
							getLogger().debug("File is to old will be deleted :"+path.getAbsolutePath());
							FileUtils.deleteFile(path);
							progress.incrementFilesDeleted();
						} catch (IOException e) {
							getConfiguration().getErrorHandler().onDeleteFileFailure(path, e);
							throw e;
						}
					}
				}				
			}			
		} else {
			return;
		}		
	}	
	
	private void deleteEmptyDirectory(CleanupProgress progress,File directory) throws IOException {		
		if ((directory.isDirectory()) && (getConfiguration().isDeleteEmptyDirectories())) {
			if (directory.list().length==0) {
				try {
					FileUtils.deleteDirectory(directory, false);
					progress.incrementDirectoriesDeleted();
				} catch (IOException e) {
					getConfiguration().getErrorHandler().onDeleteFileFailure(directory, e);
					throw e;
				}															
			}
		}
	}
	
	
	class CleanupProgress {
		
		private long startTime;
		private int filesDeleted;
		private int directoriesDeleted;
		private int level;
		
		public CleanupProgress() {
			this.startTime=System.currentTimeMillis();
			this.filesDeleted=0;
			this.directoriesDeleted=0;
			this.level=0;
		}
		public long getStartTime() {
			return startTime;
		}
		public long getProgressTime() {
			return System.currentTimeMillis() -  getStartTime();
		}
		public int getDirectoriesDeleted() {
			return directoriesDeleted;
		}
		public int getFilesDeleted() {
			return filesDeleted;
		}
		public int getLevel() {
			return level;
		}
		public void incrementDirectoriesDeleted() {
			this.directoriesDeleted++;
		}
		public void incrementFilesDeleted() {
			this.filesDeleted++;
		}
		public void incrementLevel() {
			this.level++;
		}
		@Override
		public String toString() {
			return "CleanupProgress [startTime=" + startTime + ", filesDeleted=" + filesDeleted + ", directoriesDeleted=" + directoriesDeleted + "]";
		}	
	}	
}
