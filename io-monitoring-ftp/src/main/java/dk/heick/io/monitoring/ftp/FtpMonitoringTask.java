package dk.heick.io.monitoring.ftp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketException;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPConnectionClosedException;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPListParseEngine;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.io.CopyStreamException;

import dk.heick.io.monitoring.GenericFileChange;
import dk.heick.io.monitoring.MonitorTask;
import dk.heick.io.monitoring.filter.OnlyFilesFileFilter;
import dk.heick.io.monitoring.utils.FileUtils;
import dk.heick.io.monitoring.utils.ListUtils;
import dk.heick.io.monitoring.validation.ValidationException;

/**
 * This class is dependent (based) on 
 * <ul>
 * 	<li>commons-net</li>
 *	<li>commons-net</li>
 *	<li>3.5</li>	
 * </ul>
 *  
 * 
 * https://commons.apache.org/proper/commons-net/examples/ftp/FTPClientExample.java
 * @author Frederik Heick
 * @see <a href="https://commons.apache.org/proper/commons-net/apidocs/org/apache/commons/net/ftp/FTPClient.html">Apache Commons Net FTPClient 3.5</a>
 * @see <a href="https://commons.apache.org/proper/commons-net/examples/ftp/FTPClientExample.java">Example</a>
 */
public class FtpMonitoringTask extends MonitorTask<WrappedFTPFile, FtpMonitorTaskConfiguration> {
	
	private FtpOnlyFilesFileFilter instanceOnlyFilesFileFilter = null;


	public FtpMonitoringTask(FtpMonitorTaskConfiguration configuration) throws ValidationException,NullPointerException {
		super(configuration);		
	}
	@Override
	protected void initialize() {
		getLogger().info("Initializing monitoring directory ["+getConfiguration().getDirectory().getAbsolutePath()+"].");		
		//Init process
		File[] files = getConfiguration().getDirectoryProcess().listFiles(new OnlyFilesFileFilter(null));
		for (File file : files) {
			if (FileUtils.isLockFile(file)) {
				file.delete();
			} 
		}
	}
	
	@Override
	public void validate() throws ValidationException {
		super.validate();
		FTPClient client = getNewFTPClient();
		try {						
			doFTPConnect(client);										
		} catch (IOException e) {
			throw new ValidationException(e.getMessage(),e);
		} finally {
			try {
				doFTPDisconnect(client);
			} catch (IOException e) {				
			}
		}	
	}


	private FtpOnlyFilesFileFilter getInstanceOnlyFilesFileFilter() {
		if (instanceOnlyFilesFileFilter==null) {
			instanceOnlyFilesFileFilter = new FtpOnlyFilesFileFilter(getConfiguration().getFtpFileFilter());
		}
		return instanceOnlyFilesFileFilter;
	}
	
	protected FTPClient getNewFTPClient() {
		return new FTPClient();
	}

	public GenericFileChange<WrappedFTPFile> getMonitoredFile(FTPFile fileType) {	
		return super.getMonitoredFile(new WrappedFTPFile(fileType));
	}

	private int doFTPConnect(FTPClient client) throws IOException,SocketException {
		int reply=-1;
		client.setAutodetectUTF8(true);

		//
		if (getConfiguration().getFtpConnectTimeout()!=null) {
			client.setConnectTimeout(getConfiguration().getFtpConnectTimeout());
		}
		if (getConfiguration().getFtpDataTimeout()!=null) {
			client.setDataTimeout(getConfiguration().getFtpDataTimeout());
		}		
		if (getConfiguration().getFtpDefaultTimeout()!=null) {
			client.setDefaultTimeout(getConfiguration().getFtpDefaultTimeout());
		}			
		if (getConfiguration().getFtpKeepAlive()!=null) {
			client.setKeepAlive(getConfiguration().getFtpKeepAlive());
		}		
		if (getConfiguration().getFtpProxy()!=null) {
			client.setProxy(getConfiguration().getFtpProxy());
		}
		if (getConfiguration().getFtpSoTimeout()!=null) {
			client.setSoTimeout(getConfiguration().getFtpSoTimeout());
		}
		
		if (getConfiguration().getPort() > 0) {
			client.connect(getConfiguration().getHost(), getConfiguration().getPort());
        } else {
        	client.connect(getConfiguration().getHost());
        }
		//
		client.setFileType(getConfiguration().getFtpFileType().getType());
		additionalConfiguration(client);
		//
		getLogger().debug("FTP Server is ["+client.getSystemType()+"], Status ["+client.getStatus().trim()+"]");
		reply = client.getReplyCode();
		if (!FTPReply.isPositiveCompletion(reply)) {				
			throw new IOException("Exception in connecting to FTP Server, Status ["+client.getStatus().trim()+"]");
		}								
		client.login(getConfiguration().getUsername(), getConfiguration().getPassword());
		reply = client.getReplyCode();
		if (!FTPReply.isPositiveCompletion(reply)) {
			throw new IOException("Login unsuccessful ["+getConfiguration().getUsername()+"] ["+getConfiguration().getPassword()+"] Status ["+client.getStatus().trim()+"] .");
		}
		//client.setFileType(FTP.BINARY_FILE_TYPE);						
		client.enterLocalPassiveMode();
		client.setControlKeepAliveTimeout(300);
		reply = client.getReplyCode();
		//
		return reply;
	}
	
	/**
	 * Method that be overriden to set additional configurations on the FTPClient, which not part of the FtpMonitorTaskConfiguration.
	 * @param client the FTPClient.
	 * @throws IOException 
	 */
	private void additionalConfiguration(FTPClient client) throws IOException {
		if (getConfiguration().getAdditionalFTPConfiguration()!=null) {
			getConfiguration().getAdditionalFTPConfiguration().configure(client);
		}
	}
	
	private void doFTPDisconnect(FTPClient client) throws IOException {
		client.disconnect();
	}	
	/**
	 * http://superuser.com/questions/880410/prevent-file-from-being-accessed-as-its-being-uploaded
	 * http://www.proftpd.org/docs/directives/linked/config_ref_HiddenStores.html
	 */
	@Override
	protected void detecting() {
		verboseDebug("Running task - detecting");
		FTPClient client = getNewFTPClient();
		int reply = -1;
		try {			
			reply = doFTPConnect(client);			
			//
			client.changeWorkingDirectory(getConfiguration().getRemoteDirectory());
			reply = client.getReplyCode();
			FTPListParseEngine engine = client.initiateListParsing(getConfiguration().getRemoteDirectory());			
			FTPFile[] ftpFiles = engine.getFiles(getInstanceOnlyFilesFileFilter());
			//reply = client.getReplyCode();
			
			for (FTPFile ftpFile : ftpFiles) {				
				WrappedFTPFile wrappedFtpFile = new WrappedFTPFile(ftpFile);
				
				GenericFileChange<WrappedFTPFile> previousFileChange = getMonitoredFile(wrappedFtpFile);				
				if (previousFileChange==null) {
					getFileMonitor().put(wrappedFtpFile,new FtpFileChange(wrappedFtpFile));
				} else {
					GenericFileChange<WrappedFTPFile> currentFileChange = new FtpFileChange(previousFileChange,wrappedFtpFile);					
					if (currentFileChange.isStable(previousFileChange, getConfiguration().getStableTime().getTime())) {
						File localFile = new File(getConfiguration().getDirectory(),wrappedFtpFile.getFile().getName());						
						copyRemoteToLocal(client,wrappedFtpFile,localFile);
						deleteRemote(client,wrappedFtpFile);
						moveFileToProcess(localFile);							
						getFileMonitor().remove(wrappedFtpFile);						
					} else if (currentFileChange.getMonitoringTime()>getConfiguration().getMonitorTimeout().getTime()) {
						getLogger().error("FTPFile ["+currentFileChange.getFileName()+"] has been monitored more than ["+getConfiguration().getMonitorTimeout()+"] ms, removing file from monitor.");						
						getFileMonitor().remove(wrappedFtpFile);
					}
				}
			}			
			for (WrappedFTPFile wrappedFtpFile : ListUtils.asList(getFileMonitor().keySet().iterator())) {
				FTPFile[] result = client.listFiles(wrappedFtpFile.getFile().getName());
				reply = client.getReplyCode();
				if ((result==null) || (result.length==0) || (result[0]==null)) {
					getLogger().warn("FTPFile ["+wrappedFtpFile.getFile().getName()+"] do not exists any more, removed from monitor.");
					getFileMonitor().remove(wrappedFtpFile);
				}
			}				
		} catch (SocketException e) {
			getLogger().error("SocketException, FTPReply ["+reply+"], "+e.getMessage(),e);
		} catch (IOException e) {
			getLogger().error("IOException, FTPReply ["+reply+"], "+e.getMessage(),e);
		} finally {
			if (client!=null) {
				try {
					doFTPDisconnect(client);					
				} catch (IOException e1) {								
				}
			}
		}
	}


	protected void deleteRemote(FTPClient client,WrappedFTPFile remoteFile) throws IOException {
		try {
			client.deleteFile(remoteFile.getFile().getName());
		} catch (FTPConnectionClosedException e) {
			getLogger().error(e.getMessage(),e);
			getConfiguration().getRemoteErrorHandler().onRemoteDeleteFileFailure(new FtpFileChange(remoteFile), e);
			//
		} catch (IOException e) {
			getLogger().error(e.getMessage(),e);
			getConfiguration().getRemoteErrorHandler().onRemoteDeleteFileFailure(new FtpFileChange(remoteFile), e);
			//
		}
	}		
	
	protected void copyRemoteToLocal(FTPClient client,WrappedFTPFile remoteFile,File localFile) {
		OutputStream output=null;		
		try {
			localFile.createNewFile();
			output = new FileOutputStream(localFile);			
			if (!client.retrieveFile(remoteFile.getFile().getName(), output)) {				
				getLogger().error("Error copying FTPFile ["+remoteFile.getFile().getName()+"], retrieveFile method returned [false], FTPReply ["+client.getReplyCode()+"] Status ["+client.getStatus().trim()+"]");
				getConfiguration().getRemoteErrorHandler().onRemoteCopyToLocalFileFailure(new FtpFileChange(remoteFile), localFile, new IOException("FTPReply ["+client.getReplyCode()+"] Status ["+client.getStatus().trim()+"]"));			
			} else {
				getLogger().info("FTPFile ["+remoteFile.toString()+"] transfered to ["+localFile.getAbsolutePath()+"], size ["+remoteFile.getFile().getSize()+"]");
			}
			output.flush();
		} catch (FTPConnectionClosedException e) {
			getLogger().error(e.getMessage(),e);
			getConfiguration().getRemoteErrorHandler().onRemoteCopyToLocalFileFailure(new FtpFileChange(remoteFile), localFile, e);
			//								
		} catch (CopyStreamException e) {
			getLogger().error(e.getMessage(),e);
			getConfiguration().getRemoteErrorHandler().onRemoteCopyToLocalFileFailure(new FtpFileChange(remoteFile), localFile, e);
			//
		} catch (IOException e) {
			getLogger().error(e.getMessage(),e);
			getConfiguration().getRemoteErrorHandler().onRemoteCopyToLocalFileFailure(new FtpFileChange(remoteFile), localFile, e);
			//
		} finally {
			if (output!=null) {
				try {					
					output.close();
				} catch (IOException e) {					
				}
			}
		}
		
	}

	@Override
	protected void monitoring() {
		//NOTHINGs				
	}	

}
