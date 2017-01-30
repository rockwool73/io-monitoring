package dk.heick.io.monitoring.sftp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.apache.sshd.common.NamedFactory;
import org.apache.sshd.server.Command;
import org.apache.sshd.server.CommandFactory;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.auth.password.PasswordAuthenticator;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.scp.ScpCommandFactory;
import org.apache.sshd.server.session.ServerSession;
import org.apache.sshd.server.subsystem.sftp.SftpSubsystemFactory;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import dk.heick.io.monitoring.utils.TempFileManager;

public class MockSFTPServer {
	
	private Logger logger = LoggerFactory.getLogger(MockSFTPServer.class);	
	private SshServer sshd = null;
	private int port;
	private File hostkeyFile;
	private TempFileManager tempFileManager = new TempFileManager();
	

	public MockSFTPServer(int port,File hostkeyFile) {
		super();
		this.port=port;
		this.hostkeyFile=hostkeyFile;
		tempFileManager.add(hostkeyFile);
	}
	public Logger getLogger() {
		return logger;
	}
	public SshServer getSshd() {
		return sshd;
	}
	public int getPort() {
		return port;
	}
	public File getHostkeyFile() {
		return hostkeyFile;
	}
	public TempFileManager getTempFileManager() {
		return tempFileManager;
	}
	public void create() {
		sshd = SshServer.setUpDefaultServer();
		sshd.setPort(22999);				
		sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider(getHostkeyFile()));
		sshd.setPasswordAuthenticator(new PasswordAuthenticator() {
			public boolean authenticate(String username, String password, ServerSession session) {				
				return true;
			}
		});		
		sshd.setCommandFactory(new ScpCommandFactory());		
		List<NamedFactory<Command>> namedFactoryList = new ArrayList<NamedFactory<Command>>();				
	    namedFactoryList.add(new SftpSubsystemFactory());	    		
		sshd.setSubsystemFactories(namedFactoryList);
	}
	
	public void start() throws Exception {
		getSshd().start();
	}
	public void stop() throws Exception {
		getSshd().stop(true);
		tempFileManager.cleanup();
	}
	
	public void uploadFile(File file) throws Exception {
		JSch jsch = new JSch();
		ChannelSftp sftpChannel = null;
		Session session = null;
		try {
		 
			Hashtable<String,String> config = new Hashtable<String,String>();
			config.put("StrictHostKeyChecking", "no");
			JSch.setConfig(config);
			
			session = jsch.getSession("remote-username", "localhost", getPort());		
			session.setPassword("remote-password");
			 
			session.connect();
			 
			Channel channel = session.openChannel("sftp");
			channel.connect();		
			 
			sftpChannel = (ChannelSftp) channel;
			 
			String uploadedFileName = "mockftp/data/"+file.getName();
			sftpChannel.put(new ByteArrayInputStream(getFileContent(file)), uploadedFileName);
			 
		} finally {
			if (sftpChannel.isConnected()) {
				sftpChannel.exit();
				logger.debug("Disconnected channel");
			}
		 
			if (session.isConnected()) {
				session.disconnect();
				logger.debug("Disconnected session");
			}
		}
		 
	}	
	
	private byte[] getFileContent(File file) throws IOException {
		return Files.readAllBytes(file.toPath());
	}

}
