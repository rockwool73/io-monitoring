package dk.heick.io.monitoring.errorhandler;

import java.io.File;

import dk.heick.io.monitoring.GenericFileChange;

/**
 * Interface for handling interface if a remote file IO error occurs.
 * @author Frederik Heick
 * @param <T> the file type that be used as generic input for GenericFileChange
 * @see GenericFileChange
 */
public interface MonitorTaskRemoteFileErrorHandler<T> {
	
	/**
	 * Called when having an IO problem delete a remote file.
	 * @param sourceFile the file in question.
	 * @param e the exception.
	 */
	public void onRemoteDeleteFileFailure(GenericFileChange<T> sourceFile,Exception e);
	            	
	/**
	 * Called when having an IO problem copying a remote file to a local file.
	 * @param sourceFile the remote file.
	 * @param targetFile the local file.
	 * @param e the exception.
	 */
	public void onRemoteCopyToLocalFileFailure(GenericFileChange<T> sourceFile,File targetFile,Exception e);

}
