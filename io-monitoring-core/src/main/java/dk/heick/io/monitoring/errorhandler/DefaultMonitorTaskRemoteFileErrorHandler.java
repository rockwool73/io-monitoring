package dk.heick.io.monitoring.errorhandler;

import java.io.File;

import dk.heick.io.monitoring.GenericFileChange;

/**
 * Default implementation of MonitorTaskRemoteFileErrorHandler. <br>
 * The only thing this does is log an error.
 * @author Frederik Heick
 * @param <T> the file type that be used as generic input for GenericFileChange
 * @see GenericFileChange
 */
public class DefaultMonitorTaskRemoteFileErrorHandler<T> implements MonitorTaskRemoteFileErrorHandler<T> {

	/**
	 * Constructor.
	 */
	public DefaultMonitorTaskRemoteFileErrorHandler() {
		super();
	}

	@Override
	public void onRemoteCopyToLocalFileFailure(GenericFileChange<T> sourceFile, File targetFile, Exception e) {
		//NOTHING		
	}
	
	@Override
	public void onRemoteDeleteFileFailure(GenericFileChange<T> sourceFile, Exception e) {
		//NOTHING		
	}

}
