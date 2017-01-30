package dk.heick.io.monitoring.sftp;

import java.io.File;

import dk.heick.io.monitoring.IOMonitoringConstants;
import dk.heick.io.monitoring.MonitorTaskConfiguration;
import dk.heick.io.monitoring.errorhandler.DefaultMonitorTaskRemoteFileErrorHandler;
import dk.heick.io.monitoring.errorhandler.MonitorTaskRemoteFileErrorHandler;
import dk.heick.io.monitoring.processor.FileProcessor;
import dk.heick.io.monitoring.utils.TimeSpan;
import dk.heick.io.monitoring.validation.ValidateUtils;
import dk.heick.io.monitoring.validation.ValidationException;

public class SFtpMonitorTaskConfiguration extends MonitorTaskConfiguration {
	
	private String host;
	private int port;
	private String username;
	private String password;
	private String remoteDirectory;
	private TimeSpan timeout= TimeSpan.createSeconds(2);
	private SFtpFileFilter sftpFileFilter;
	private boolean strictHostKeyChecking=false;
	private AdditionalSFtpConfiguration additionalSFTPConfiguration = null;
	private MonitorTaskRemoteFileErrorHandler<WrappedSFtpFile> remoteErrorHandler = new DefaultMonitorTaskRemoteFileErrorHandler<WrappedSFtpFile>();

	public SFtpMonitorTaskConfiguration(String host,int port,String username,String password,String remoteDirectory,File localDirectory,FileProcessor fileProcessor,SFtpFileFilter sftpFileFilter) {
		this(host,port,username,password,remoteDirectory,localDirectory,fileProcessor,sftpFileFilter,IOMonitoringConstants.DEFAULT_STABLE_TIME);
	}
	public SFtpMonitorTaskConfiguration(String host,int port,String username,String password,String remoteDirectory,File localDirectory,FileProcessor fileProcessor,SFtpFileFilter sftpFileFilter,TimeSpan stableTime) {
		super(localDirectory,fileProcessor,stableTime);
		this.host=host;
		this.port= port;
		this.username=username;
		this.password=password;
		this.remoteDirectory=remoteDirectory;
		this.sftpFileFilter=sftpFileFilter;		
	}
	
	public final String getHost() {
		return host;
	}
	public final int getPort() {
		return port;
	}
	public final String getUsername() {
		return username;
	}
	public final String getPassword() {
		return password;
	}
	public final String getRemoteDirectory() {
		return remoteDirectory;
	}	
	public TimeSpan getTimeout() {
		return timeout;
	}
	public boolean isStrictHostKeyChecking() {
		return strictHostKeyChecking;
	}
	public AdditionalSFtpConfiguration getAdditionalSFTPConfiguration() {
		return additionalSFTPConfiguration;
	}
	public MonitorTaskRemoteFileErrorHandler<WrappedSFtpFile> getRemoteErrorHandler() {
		if (remoteErrorHandler==null) {
			remoteErrorHandler = new DefaultMonitorTaskRemoteFileErrorHandler<WrappedSFtpFile>(); 
		}
		return remoteErrorHandler;
	}
	public SFtpFileFilter getSFtpFileFilter() {
		return sftpFileFilter;
	}

	public SFtpMonitorTaskConfiguration setTimeout(int timeout) {
		this.timeout = TimeSpan.createMilliSeconds(timeout);
		return this;
	}
	public SFtpMonitorTaskConfiguration setTimeout(TimeSpan timeout) throws NullPointerException {
		this.timeout = timeout;
		if (this.timeout==null) {
			throw new NullPointerException("Timeout timespan is null.");
		}
		return this;
	}
	
	public SFtpMonitorTaskConfiguration setStrictHostKeyChecking(boolean strictHostKeyChecking) {
		this.strictHostKeyChecking = strictHostKeyChecking;
		return this;
	}
	public SFtpMonitorTaskConfiguration setAdditionalSFTPConfiguration(AdditionalSFtpConfiguration additionalSFTPConfiguration) {
		this.additionalSFTPConfiguration = additionalSFTPConfiguration;
		return this;
	}
	
	public SFtpMonitorTaskConfiguration setSFtpFileFilter(SFtpFileFilter sftpFileFilter) {
		this.sftpFileFilter = sftpFileFilter;
		return this;
	}
	
	public SFtpMonitorTaskConfiguration setRemoteErrorHandler(MonitorTaskRemoteFileErrorHandler<WrappedSFtpFile> remoteErrorHandler) {
		this.remoteErrorHandler = remoteErrorHandler;
		return this;
	}
	
	public boolean hasAdditionalSFtpConfiguration() {
		return getAdditionalSFTPConfiguration()!=null; 
	}
	
	
	public void validate() throws ValidationException {
		super.validate();
		ValidateUtils.validateNotEmpty("host", host, true);
		ValidateUtils.validateRange("port", port, 22, null);
		ValidateUtils.validateNotEmpty("username", username);
		ValidateUtils.validateNotNull("remoteDirectory", remoteDirectory);
		ValidateUtils.validateNotNull("remoteErrorHandler", getRemoteErrorHandler());		
	}
	
	
}
