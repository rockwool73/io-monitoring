package dk.heick.io.monitoring.sftp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class DummySDFTPTest {
	
	private Logger logger = LoggerFactory.getLogger(DummySDFTPTest.class);
	
	private SshServer sshd = null;

	@Before
	public void beforeTestSetup() throws Exception {
		sshd = SshServer.setUpDefaultServer();
		sshd.setPort(22999);
		File hostkeyFile = new File("src/test/resources/hostkey.ser");
		sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider(hostkeyFile));
		sshd.setPasswordAuthenticator(new PasswordAuthenticator() {

			public boolean authenticate(String username, String password, ServerSession session) {
				// TODO Auto-generated method stub
				return true;
			}
		});

		CommandFactory myCommandFactory = new CommandFactory() {
			public Command createCommand(String command) {
				System.out.println("Command: " + command);
				return null;
			}
		};		
		sshd.setCommandFactory(new ScpCommandFactory());
		
		List<NamedFactory<Command>> namedFactoryList = new ArrayList<NamedFactory<Command>>();			
		// this needs to be stubbed out based on your implementation
	   /* namedFactoryList.add( new NamedFactory<Command>(){
			public Command create() {						
				return new SftpSubsystemFactory();
			}
			public String getName() {
				return "SFTP stuff";
			}
	    	
	    });	   */ 
	    namedFactoryList.add(new SftpSubsystemFactory());	    
		
		sshd.setSubsystemFactories(namedFactoryList);
		sshd.start();
	}

	@After
	public void teardown() throws Exception {
		sshd.stop();
	}
	
	

	@Test
	public void testPutAndGetFile() throws Exception {
		JSch jsch = new JSch();
		 
		Hashtable<String,String> config = new Hashtable<String,String>();
		config.put("StrictHostKeyChecking", "no");
		JSch.setConfig(config);
		
		Session session = jsch.getSession("remote-username", "localhost", 22999);		
		session.setPassword("remote-password");
		 
		session.connect();
		
		
		 
		Channel channel = session.openChannel("sftp");
		channel.connect();
		
		
		 
		ChannelSftp sftpChannel = (ChannelSftp) channel;
		
		
		 
		final String testFileContents = "some file contents";
		 
		String uploadedFileName = "uploadFile";
		sftpChannel.put(new ByteArrayInputStream(testFileContents.getBytes()), uploadedFileName);
		 
		String downloadedFileName = "downLoadFile";
		sftpChannel.get(uploadedFileName, downloadedFileName);
		 
		File downloadedFile = new File(downloadedFileName);
		assertTrue(downloadedFile.exists());
		 
		String fileData = getFileContents(downloadedFile);
		 
		assertEquals(testFileContents, fileData);
		 
		if (sftpChannel.isConnected()) {
			sftpChannel.exit();
			logger.debug("Disconnected channel");
		}
		 
		if (session.isConnected()) {
			session.disconnect();
			logger.debug("Disconnected session");
		}
		 
	}

	private String getFileContents(File downloadedFile) throws IOException {
		return new String(Files.readAllBytes(downloadedFile.toPath()),StandardCharsets.UTF_8).toString();
	}
}
