package dk.heick.io.monitoring.ftp;

import java.io.File;
import java.net.Proxy;

import org.apache.commons.net.ftp.FTPFileFilter;

import dk.heick.io.monitoring.IOMonitoringConstants;
import dk.heick.io.monitoring.MonitorTaskConfiguration;
import dk.heick.io.monitoring.errorhandler.DefaultMonitorTaskRemoteFileErrorHandler;
import dk.heick.io.monitoring.errorhandler.MonitorTaskRemoteFileErrorHandler;
import dk.heick.io.monitoring.processor.FileProcessor;
import dk.heick.io.monitoring.utils.TimeSpan;
import dk.heick.io.monitoring.validation.ValidateUtils;
import dk.heick.io.monitoring.validation.ValidationException;

/**
 * 
 * This class is dependent (based) on 
 * <ul>
 * 	<li>commons-net</li>
 *	<li>commons-net</li>
 *	<li>3.5</li>	
 * </ul> 
 * @author Frederik Heick
 * @see <a href="https://commons.apache.org/proper/commons-net/apidocs/org/apache/commons/net/ftp/FTPClient.html">Apache Commons Net FTPClient 3.5</a>
 */
public class FtpMonitorTaskConfiguration extends MonitorTaskConfiguration {
	
	private String host;
	private int port;
	private String username;
	private String password;
	private String remoteDirectory;
	private FTPFileFilter ftpFileFilter;
	//
	private Integer ftpBufferSize=null;
	private Integer ftpConnectTimeout=null;
	private Integer ftpDataTimeout=null;
	private Integer ftpDefaultTimeout=null;
	private Boolean ftpKeepAlive=null;
	private Proxy ftpProxy=null;
	private Integer ftpSoTimeout=null;
	private FtpFileType ftpFileType = FtpFileType.BINARY;
	private AdditionalFtpConfiguration additionalFTPConfiguration = null;
	private MonitorTaskRemoteFileErrorHandler<WrappedFTPFile> remoteErrorHandler = new DefaultMonitorTaskRemoteFileErrorHandler<WrappedFTPFile>();
	
	public FtpMonitorTaskConfiguration(String host,int port,String username,String password,String remoteDirectory,File localDirectory,FileProcessor fileProcessor,FTPFileFilter ftpFileFilter) {
		this(host,port,username,password,remoteDirectory,localDirectory,fileProcessor,ftpFileFilter,IOMonitoringConstants.DEFAULT_STABLE_TIME);
	}
	
	public FtpMonitorTaskConfiguration(String host,int port,String username,String password,String remoteDirectory,File localDirectory,FileProcessor fileProcessor,FTPFileFilter ftpFileFilter,TimeSpan stableTime) {
		super(localDirectory,fileProcessor,stableTime);
		this.host=host;
		this.port= port;
		this.username=username;
		this.password=password;
		this.remoteDirectory=remoteDirectory;
		this.ftpFileFilter=ftpFileFilter;
	}
	
	public void validate() throws ValidationException {
		super.validate();
		ValidateUtils.validateNotEmpty("host", host, true);
		ValidateUtils.validateRange("port", port, 21, null);
		ValidateUtils.validateNotEmpty("username", username);
		ValidateUtils.validateNotNull("remoteDirectory", remoteDirectory);
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
	public final FTPFileFilter getFtpFileFilter() {
		return ftpFileFilter;
	}
	public final Integer getFtpBufferSize() {
		return ftpBufferSize;
	}
	public final Integer getFtpConnectTimeout() {
		return ftpConnectTimeout;
	}
	public final Integer getFtpDataTimeout() {
		return ftpDataTimeout;
	}
	public final Integer getFtpDefaultTimeout() {
		return ftpDefaultTimeout;
	}
	public final Boolean getFtpKeepAlive() {
		return ftpKeepAlive;
	}
	public final Proxy getFtpProxy() {
		return ftpProxy;
	}
	public final Integer getFtpSoTimeout() {
		return ftpSoTimeout;
	}
	public final FtpFileType getFtpFileType() {
		return ftpFileType;
	}
	public final AdditionalFtpConfiguration getAdditionalFTPConfiguration() {
		return additionalFTPConfiguration;
	}
	public final MonitorTaskRemoteFileErrorHandler<WrappedFTPFile> getRemoteErrorHandler() {
		if (remoteErrorHandler==null) {
			remoteErrorHandler = new DefaultMonitorTaskRemoteFileErrorHandler<WrappedFTPFile>(); 
		}
		return remoteErrorHandler;
	}
	
	public final FtpMonitorTaskConfiguration setFtpBufferSize(Integer ftpBufferSize) {
		this.ftpBufferSize = ftpBufferSize;
		return this;
	}
	public final FtpMonitorTaskConfiguration setFtpConnectTimeout(Integer ftpConnectTimeout) {
		this.ftpConnectTimeout = ftpConnectTimeout;
		return this;
	}
	public final FtpMonitorTaskConfiguration setFtpDataTimeout(Integer ftpDataTimeout) {
		this.ftpDataTimeout = ftpDataTimeout;
		return this;
	}
	public final FtpMonitorTaskConfiguration setFtpDefaultTimeout(Integer ftpDefaultTimeout) {
		this.ftpDefaultTimeout = ftpDefaultTimeout;
		return this;
	}
	public final FtpMonitorTaskConfiguration setFtpKeepAlive(Boolean ftpKeepAlive) {
		this.ftpKeepAlive = ftpKeepAlive;
		return this;
	}
	public final FtpMonitorTaskConfiguration setFtpProxy(Proxy ftpProxy) {
		this.ftpProxy = ftpProxy;
		return this;
	}
	public final FtpMonitorTaskConfiguration setFtpFileFilter(FTPFileFilter ftpFileFilter) {
		this.ftpFileFilter = ftpFileFilter;
		return this;
	}
	public final FtpMonitorTaskConfiguration setFtpFileType(FtpFileType ftpFileType) {
		this.ftpFileType = ftpFileType;
		return this;
	}
	public final FtpMonitorTaskConfiguration setAdditionalFTPConfiguration(AdditionalFtpConfiguration additionalFTPConfiguration) {
		this.additionalFTPConfiguration = additionalFTPConfiguration;
		return this;
	}
	public final FtpMonitorTaskConfiguration setRemoteErrorHandler(MonitorTaskRemoteFileErrorHandler<WrappedFTPFile> remoteErrorHandler) {
		this.remoteErrorHandler = remoteErrorHandler;
		return this;
	}
		
}
