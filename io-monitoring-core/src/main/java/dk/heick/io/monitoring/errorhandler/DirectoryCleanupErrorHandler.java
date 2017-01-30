package dk.heick.io.monitoring.errorhandler;

import java.io.File;

public interface DirectoryCleanupErrorHandler {
	
	/**
	 * Called when having an IO problem delete a local file.
	 * @param file the file in question.
	 * @param e the exception.
	 */
	public void onDeleteFileFailure(File file,Exception e);
}
