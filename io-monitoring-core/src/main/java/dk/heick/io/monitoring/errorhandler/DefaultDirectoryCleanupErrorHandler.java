package dk.heick.io.monitoring.errorhandler;

import java.io.File;

public class DefaultDirectoryCleanupErrorHandler implements DirectoryCleanupErrorHandler {
	
	public DefaultDirectoryCleanupErrorHandler() {
		super();
	}
	
	@Override
	public void onDeleteFileFailure(File file, Exception e) {
		//NOTHING		
	}

}
