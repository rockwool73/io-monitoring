package dk.heick.io.monitoring.errorhandler;

import java.io.File;

import dk.heick.io.monitoring.IOMonitoringConstants;
import dk.heick.io.monitoring.MonitorTaskConfiguration;

/**
 * Interface for handling interface if a local file IO error occurs.
 * @author Frederik Heick
 * @see DefaultMonitorTaskLocalFileErrorHandler
 * @version 1.0
 */
public interface MonitorTaskLocalFileErrorHandler {
		
	/**
	 * When file failed being processed and shall be moved to the ".error" folder.
	 * @param file the file path in the error folder.
	 * @param errorFile the error file containing the complete stacketrace
	 * @param e the exception.
	 * @return <code>true</code>; the files is kept in the error folder, if <code>false</code> than the two files is deleted, because they was handled.
	 * @see MonitorTaskConfiguration#generateErrorFile(File, String)
	 * @see IOMonitoringConstants#DIRECTORY_NAME_ERROR
	 */
	public boolean onMovedToErrorDirectory(File file,File errorFile, Exception e);

	/**
	 * Called when having an IO problem delete a local file.
	 * @param file the file in question.
	 * @param e the exception.
	 */
	public void onDeleteFileFailure(File file,Exception e);
		
	/**
	 * Called when having an IO problem moving a local file
	 * @param sourceFile the source file to be moved.
	 * @param targetFile the target file to be moved to.
	 * @param e the exception.
	 */
	public void onMoveFileFailure(File sourceFile,File targetFile, Exception e);
}
