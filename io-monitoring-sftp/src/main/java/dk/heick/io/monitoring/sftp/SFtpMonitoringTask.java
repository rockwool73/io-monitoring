package dk.heick.io.monitoring.sftp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

import dk.heick.io.monitoring.GenericFileChange;
import dk.heick.io.monitoring.MonitorTask;
import dk.heick.io.monitoring.filter.OnlyFilesFileFilter;
import dk.heick.io.monitoring.utils.FileUtils;
import dk.heick.io.monitoring.validation.ValidationException;

/**
 * https://epaul.github.io/jsch-documentation/javadoc/index.html?com/jcraft/jsch/ChannelSftp.LsEntry.html
 * https://epaul.github.io/jsch-documentation/javadoc/index.html
 * http://www.javased.com/index.php?api=com.jcraft.jsch.ChannelSftp.LsEntry
 * @author Frederik
 *
 */
public class SFtpMonitoringTask extends MonitorTask<WrappedSFtpFile, SFtpMonitorTaskConfiguration> {
	
	public final static String JSCH_CURRENT_DIRECTORY=".";

	private JSchConnection connection  = null;
	
	public SFtpMonitoringTask(SFtpMonitorTaskConfiguration configuration) throws ValidationException, NullPointerException {
		super(configuration);
	}

	@Override
	protected void initialize() {
		getLogger().info("Initializing monitoring directory [" + getConfiguration().getDirectory().getAbsolutePath() + "].");
		// Init process
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
		try {						
			sftpConnect();	
			sftpChangeDirectory();
		} catch (JSchException e) {
			throw new ValidationException(e.getMessage(),e);
		} catch (SftpException e) {
			throw new ValidationException(e.getMessage(),e);
		} finally {		
			sftpDisconnect();
		}	
	}
	
	

	@Override
	protected void detecting() {
		try {
			sftpConnect();
			sftpChangeDirectory();
			List<ChannelSftp.LsEntry> sftpFiles = getSftpRemoteFiles();
			for (ChannelSftp.LsEntry sftpFile : sftpFiles) {
				if (!sftpIsFile(sftpFile)) {
					continue;
				} else {
					WrappedSFtpFile wrappedSFtpFile = new WrappedSFtpFile(sftpFile);
					//
					GenericFileChange<WrappedSFtpFile> previousFileChange = getMonitoredFile(wrappedSFtpFile);
					if (previousFileChange == null) {
						getFileMonitor().put(wrappedSFtpFile, new SFtpFileChange(wrappedSFtpFile));
					} else {
						GenericFileChange<WrappedSFtpFile> currentFileChange = new SFtpFileChange(previousFileChange, wrappedSFtpFile);
						if (currentFileChange.isStable(previousFileChange, getConfiguration().getStableTime().getTime())) {
							File localFile = new File(getConfiguration().getDirectory(),wrappedSFtpFile.getFileName());
							sftpCopyFile(wrappedSFtpFile,localFile);							
							sftpDeleteFile(wrappedSFtpFile);							
							moveFileToProcess(localFile);
							getFileMonitor().remove(wrappedSFtpFile);
						} else if (!getConfiguration().doContinueMonitorFile(currentFileChange.getMonitoringTime())) {
							getLogger().error("SFTPFile [" + currentFileChange.getFileName() + "] has been monitored more than ["+ getConfiguration().getMonitorTimeout() + "] ms, removing file from monitor.");
							getFileMonitor().remove(wrappedSFtpFile);
						}
					}
				}				
			}			
		} catch (JSchException e) {
			//TODO			
		} catch (SftpException e) {
			//TODO
		} catch (IOException e) {
		} finally {		
			sftpDisconnect();
		}	
	}
	




	@Override
	protected final void monitoring() {
		//NOTHING		
	}
	
	private void sftpConnect() throws JSchException {
		connection = new JSchConnection(getConfiguration());
		connection.connect();
	}
	
	private void sftpChangeDirectory() throws JSchException, SftpException {
		if (isConnected()) {
			getConnection().getChannelSftp().cd(getConfiguration().getRemoteDirectory());
		} else {
			throw new JSchException("Can not change directory, is not connected.");
		}
	}
	
	private List<ChannelSftp.LsEntry> getSftpRemoteFiles() throws JSchException, SftpException {
		if (isConnected()) {
			@SuppressWarnings("unchecked")
			Vector<ChannelSftp.LsEntry> vector = (Vector<ChannelSftp.LsEntry>)getConnection().getChannelSftp().ls(JSCH_CURRENT_DIRECTORY);
			List<ChannelSftp.LsEntry> result = new ArrayList<ChannelSftp.LsEntry>();
			if (getConfiguration().getSFtpFileFilter()!=null) {
				for (ChannelSftp.LsEntry lse : vector) {
					if (getConfiguration().getSFtpFileFilter().accept(lse)) {
						result.add(lse);
					}
				}
			} else {
				result.addAll(vector);
			}
			return result;
		} else {
			throw new JSchException("Can not change directory, is not connected.");
		}
	}
	
	private boolean sftpIsFile(ChannelSftp.LsEntry lse) {
		return (!lse.getAttrs().isDir() && !lse.getAttrs().isLink());
	}
	private void sftpCopyFile(WrappedSFtpFile wrappedSFtpFile, File localFile) throws JSchException,IOException,SftpException {
		if (isConnected()) {
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(localFile);
				getConnection().getChannelSftp().get(wrappedSFtpFile.getFileName(), fos);
			} finally {
				if (fos!=null) {
					try {
						fos.flush();
						fos.close();
					} catch (IOException e) {
						getLogger().warn("SFTP Error flushing and closing file ["+localFile.getAbsolutePath()+"], "+e.getMessage(),e);
					}					
				}
			}			
		} else {
			throw new JSchException("Can not change directory, is not connected.");
		}
	}

	private void sftpDeleteFile(WrappedSFtpFile wrappedSFtpFile) throws JSchException, SftpException {
		if (isConnected()) {
			getConnection().getChannelSftp().rm(wrappedSFtpFile.getFile().getFilename());
		} else {
			throw new JSchException("Can not change directory, is not connected.");
		}
		
	}
	
	private void sftpDisconnect() {
		try {
			if (getConnection()!=null) {
				getConnection().disconnect();
			}
		} finally {
			setConnection(null);
		}
	}
	private JSchConnection getConnection() {
		return connection;
	}
	private void setConnection(JSchConnection connection) {
		this.connection = connection;
	}
	private boolean isConnected() {
		if (getConnection()!=null) {
			return getConnection().isConnected();
		} else {
			return false;
		}
	}
	
}
