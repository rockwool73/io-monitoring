package dk.heick.io.monitoring.file;

import java.io.File;
import java.io.FileFilter;

import dk.heick.io.monitoring.MonitorTaskConfiguration;
import dk.heick.io.monitoring.filter.OnlyFilesFileFilter;
import dk.heick.io.monitoring.processor.FileProcessor;
import dk.heick.io.monitoring.utils.TimeSpan;
import dk.heick.io.monitoring.validation.ValidateUtils;
import dk.heick.io.monitoring.validation.ValidationException;

public class DirectoryMonitorTaskConfiguration extends MonitorTaskConfiguration {

	private FileFilter filter = new OnlyFilesFileFilter();	
	
	public DirectoryMonitorTaskConfiguration(File directory,FileProcessor fileProcessor,FileFilter filter) {
		super(directory,fileProcessor);
		this.filter=filter;		
	}

	public DirectoryMonitorTaskConfiguration(File directory,FileProcessor fileProcessor,FileFilter filter,TimeSpan stableTime) {
		super(directory,fileProcessor,stableTime);
		this.filter=filter;		
	}
	
	
	@Override
	public void validate() throws ValidationException {	
		super.validate();
		ValidateUtils.validateNotNull("FileFilter", getFilter());
	}
	
	
	/**
	 * The FileFilter if any, that select the files that will be accepted in the monitoring process.
	 * @return the monitoring file filter, if <code>null</code> all files is accepted. 
	 */
	public FileFilter getFilter() {
		return filter;
	}
	
	public DirectoryMonitorTaskConfiguration setFilter(FileFilter filter) {
		this.filter = filter;
		return this;
	}

	@Override
	public String toString() {
		return super.toString()+ " - DirectoryMonitorTaskConfiguration [filter=" + filter.getClass().getName() + "]";
	}

}
