package dk.heick.io.monitoring.errorhandler;

import java.io.File;

/**
 * Default implementation of MonitorTaskLocalFileErrorHandler. <br>
 * The only thing this does is log an error.
 * @author Frederik Heick
 */
public class DefaultMonitorTaskLocalFileErrorHandler implements MonitorTaskLocalFileErrorHandler {

	/**
	 * Constructor.
	 */
	public DefaultMonitorTaskLocalFileErrorHandler() {
		super();
	}
	
	@Override
	public boolean onMovedToErrorDirectory(File file, File errorFile, Exception e) {		
		return true;
	}
	@Override
	public void onDeleteFileFailure(File file, Exception e) {
		//NOTHING
	}
	@Override
	public void onMoveFileFailure(File sourceFile, File targetFile, Exception e) {
		//NOTHING		
		
	}
	
}
