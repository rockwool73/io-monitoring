package dk.heick.io.monitoring.file;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import dk.heick.io.monitoring.GenericFileChange;
import dk.heick.io.monitoring.MonitorTask;
import dk.heick.io.monitoring.filter.OnlyFilesFileFilter;
import dk.heick.io.monitoring.processor.FileProcessor;
import dk.heick.io.monitoring.timer.MonitorTaskTimer;
import dk.heick.io.monitoring.utils.FileLastModifiedComparator;
import dk.heick.io.monitoring.utils.FileUtils;
import dk.heick.io.monitoring.utils.ListUtils;
import dk.heick.io.monitoring.validation.ValidationException;

/**
 * <H2>DirectoryMonitoringTask</H2>
 * The a class which monitors a directory for files and process them. <br>
 * This class is triggered in the <tt>MonitorTaskTimerWatcher</tt> instance which is typically initiated in the <tt>ServletContextListener</tt>.<br>
 * <br>
 * This class implements a series of <b>conventions</b>.
 * <br>
 * <ul>
 * 	<li>Monitors one directory, and only files in that directory.</li>
 * </ul>
 * The sub directories is created as follows.
 * <ul>
 * 	<li><tt>"directory"</tt> [dir] (refereed to as <b>"input"</b> directory) (the directory where files is monitored.)
 * 	  <ul>
 * 		<li><tt>".archive"</tt> [dir] - where files is archived after succesful processing.</li>
 * 		<li><tt>".error"</tt> [dir] - where files is stored if an exception occurs.)</li>
 * 		<li><tt>".process"</tt> [dir] - where files housed when they are being processed, where after they are moved to '.archive' or '.error'.</li>
 * 	  </ul>
 *  </li>
 * </ul>
 * 
 * <b>Initialization</b><br><br>
 * When the class is instancetiated. The following this happens.<br>
 * <ul>
 * 	<li>The "<tt>input</tt>" director is checked that it exists, is a directory and there is read and write access.</li>
 * 	<li>The subdirectories <tt>".archive"</tt>, <tt>".error"</tt> and <tt>".process"</tt> is created if they do not exists, and checked for read and write access.</li>
 *  <li>All the files in the <tt>"input"</tt> directory that passes the <tt>FileFilter</tt> will be added to the file monitor map.</li>
 * 	<li>All files in the <tt>".process"</tt> directory that ends with <tt>".lock"</tt> is deleted, and the rest of the files is copy out to the <tt>"input"</tt> directory.
 * 	  <ul>
 * 		<li>This is files that has begone processing, but not completed when the process was terminated.</li>
 * 		<li>Your process has to be robust for this situation, when trying to process the files again.</li>
 * 		<li>If another file with the same already exists in the <tt>"directory"</tt>, the <tt>".process"</tt> file is preappend a timestamp "yyyyMMdd-HHmmss".</li>
 * 		<li>When the files is moved to <tt>"input"</tt> directory they are rediscovered by the process.</li>
 * 	  </ul>
 *  </li>
 * </ul>
 * 
 * <b>Monitoring</b>
 * <ul>
 * 	<li>When new file is created in the <tt>"input"</tt> directory and it is a <tt>File</tt> and passes the <tt>FileFilter</tt> instance; <br>
 * 	    if that is not <code>null</code>, in that case all files is added,  it will be added to the monitor map.
 * 	</li>
 * 	<li>When a file is "stable" the processing of the file will begin shortly after. - By "stable" the definition is:
 * 		<ul>
 * 			<li>"File.lastModified" hasnt changed in the last "stableTime" in ms.</li>
 * 			<li>"File.length" hasnt changed in the last "stableTime" in ms.</li>
 * 			<li><b>Note:</b> Has tried to implement a file locked test, but have been unsuccessfull.</li>
 * 		</ul>
 * 	</li>
 * 	<li>When a file is "stable" it will be moved to <tt>".process"</tt> directory.
 * 	  <ul>
 * 		<li>If a file in the <tt>".process"</tt> directory already has that filename, 
 * 		    the file to be renamed will be preappended format(<tt>yyyyMMdd_hhmmss-</tt>) example(<i>20181128_134512-</i>).
 * 	  </ul>	
 *  </li>
 * </ul>
 * 
 * <b>Processing</b>
 * <ul>
 * 	<li>After file change detection, we iterate a list of all files in <tt>".process"</tt> directory.</li>
 * 	<li>We start to process a file, if the file do not have a corresponding file with the same name, but ends with <tt>".lock"</tt> Many threads can process file.
 * 	<ol>
 * 		<li>Create a lock file, which is the same filename but is post appended  <tt>".lock"</tt>.</li>
 * 		<li>Call the method <code>FileProcessor.process(file)</code></li>
 * 		<li>If the metod returns without an exception, we move the file to the <tt>".archive"</tt> directory in a subdirectory with the current date (<tt>yyyy-MM-dd</tt>).</li>
 * 		<li>If the method throws an exception - see section "Exception handling".</li>
 * 		<li>Lastly the lock file is deleted.</li>
 * 	</ol>
 * 	</li>
 * </ul>
 * 
 * <b>Exception handling</b>
 * <ul>
 * 	<li>We move the file to <tt>".error"</tt> directory in a subdirectory with the current date (<tt>yyyy-MM-dd</tt>).</li>
 * 	<li>If a file in the archive subdirectory already exists with that name we preappend but preappended (<tt>yyyyMMdd_hhmmss-</tt>) example(<i>20181128_134512-</i>).</li>
 * 	<li>In that directory we create another file, with the same filename, but post appended <tt>".errorlog"</tt>, where in the stacktrace of the expection is written.</li>
 * 	<li>Than the method <code>FileProcessor.onError(File,Exception)</code> is called. Where the you can execute any additional actions needed.</li>
 * </ul>
 * 
 * <b>Notes</b>
 * <ul>
 * 	<li>If any exception occurs in the flow the file is handle like described in Processing section.</li>
 * 	<li><i>Not implemented:</i> If any <tt>".lock"</tt> files in the <tt>".process"</tt> directory is older than <tt>processTimeout</tt> we throw a an exception a file,
 * 	    section "Exception handling".
 * 	<li>To reprocess a file that has not been moved from <tt>".process"</tt> directory, delete the  <tt>".lock"</tt> file.</li>
 * 	<li>To reprocess a file from archive, move to the <tt>"input"</tt> directory.</li> 
 * 	<li><b>Idea:</b> Should processing start up in a thread, with a max thread pool size, hence can handle timeout issues? Requires fixed constructor.</li>
 * </ul>
 * <br>
 * @author Frederik Heick
 * @see MonitorTaskTimer
 * @see FileProcessor
 * @see FileFilter
 */
public class DirectoryMonitorTask extends MonitorTask<File, DirectoryMonitorTaskConfiguration> {
	//
	private OnlyFilesFileFilter instanceOnlyFilesFileFilter = null;

	/**
	 * Constructor.
	 * @param configuration the configuration
	 * @throws ValidationException if the <tt>configuration</tt> isnt validate or cannot be initialized.
	 * @throws NullPointerException if the <tt>configuration</tt> is <code>null</code>.
	 */
	public DirectoryMonitorTask(DirectoryMonitorTaskConfiguration configuration) throws ValidationException,NullPointerException {
		super(configuration);		
	}
	@Override
	protected void initialize() {
		getLogger().info("Initializing monitoring directory ["+getConfiguration().getDirectory().getAbsolutePath()+"].");
		
		//Init process
		File[] files = getConfiguration().getDirectoryProcess().listFiles(new OnlyFilesFileFilter(null));
		for (File file : files) {
			if (FileUtils.isLockFile(file)) {
				deleteFile(file);
			} else {
				moveFileToInput(file);				
			}
		}		
	}	

	private OnlyFilesFileFilter getInstanceOnlyFilesFileFilter() {
		if (instanceOnlyFilesFileFilter==null) {
			instanceOnlyFilesFileFilter = new OnlyFilesFileFilter(getConfiguration().getFilter());
		}
		return instanceOnlyFilesFileFilter;
	}
	
	private File[] getDetectedFiles() {
		File[] list = getConfiguration().getDirectory().listFiles(getInstanceOnlyFilesFileFilter());
		Arrays.sort(list,new FileLastModifiedComparator());
		return list;
	}
	private List<File> getMonitoringFiles() {
		List<File> list = ListUtils.asList(getFileMonitor().keySet().iterator());
		Collections.sort(list,new FileLastModifiedComparator());
		return list;
	}

	
	@Override
	protected void detecting() {
		verboseDebug("Running task - detecting");
		int filesPolled=0;
		for (File file : getDetectedFiles()) {			
			GenericFileChange<File> previousFileChange = getFileMonitor().get(file);	
			//Other timer executions can have remove the file, check for null.
			if (previousFileChange!=null) {	
				if (!file.exists()) {
					getLogger().warn("File ["+file.getAbsolutePath()+"] do not exists any more, removed from monitor.");
					getFileMonitor().remove(file);							
				} /*else {
					if (!file.isFile()) {
						getFileMonitor().put(file,currFileChange);
						filesPolled++;
					}
				}	*/	
			} else {
				if (file.isFile()) {
					getFileMonitor().put(file,new FileChange(file));
					filesPolled++;
				}
			}
			if (!getConfiguration().doContinuePollFiles(filesPolled)) {
				break;
			}
		}
	}
	@Override
	protected void monitoring() {	
		verboseDebug("Running task - monitoring");
		for (File file :getMonitoringFiles()) {			
			GenericFileChange<File>  previousFileChange = getFileMonitor().get(file);		
			//System.out.println("PREV_"+previousFileChange.getModified()+"/"+previousFileChange.getSize());
			//Other timer executions can have remove the file, check for null.
			if (previousFileChange!=null) {				
				GenericFileChange<File>  currentFileChange = new FileChange(previousFileChange);
				//System.out.println("CURR_"+currentFileChange.getModified()+"/"+currentFileChange.getSize());
				if (currentFileChange.isStable(previousFileChange, getConfiguration().getStableTime().getTime())) {													
					verboseDebug("File is stable ["+file.getName()+"].");
					moveFileToProcess(file);
					getFileMonitor().remove(file);									
				} else {
					//verboseDebug("File is NOT stable ["+file.getName()+"].");
					if (!file.exists()) {
						getLogger().warn("File ["+file.getAbsolutePath()+"] do not exists any more, removed from monitor.");
						getFileMonitor().remove(file);
					} else if (!getConfiguration().doContinueMonitorFile(currentFileChange.getMonitoringTime())) {
						String msg = "File ["+currentFileChange.getFile().getAbsolutePath()+"] has been monitored more than ["+getConfiguration().getMonitorTimeout()+"] ms, removing file from monitor.";
						getLogger().error(msg);						
						getFileMonitor().remove(file);
						moveFileToError(currentFileChange.getFile(), new IOException(msg));
					} else {
						getFileMonitor().put(file,currentFileChange);
					}
				}
			} else {
				if (!file.exists()) {
					getLogger().warn("File ["+file.getAbsolutePath()+"] do not exists any more, removed from monitor.");
					getFileMonitor().remove(file);
				} else {
					getFileMonitor().put(file, new FileChange(file));
				}
			} 
		}
	}
	
}
