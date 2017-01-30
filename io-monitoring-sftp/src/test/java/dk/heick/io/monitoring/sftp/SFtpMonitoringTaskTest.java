package dk.heick.io.monitoring.sftp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import org.apache.commons.vfs2.FileSelectInfo;
import org.apache.commons.vfs2.FileSelector;
import org.apache.log4j.BasicConfigurator;
import org.apache.sshd.SshServer;
import org.apache.sshd.common.NamedFactory;
import org.apache.sshd.server.Command;
import org.apache.sshd.server.CommandFactory;
import org.apache.sshd.server.PasswordAuthenticator;
import org.apache.sshd.server.command.ScpCommandFactory;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.session.ServerSession;
import org.apache.sshd.server.sftp.SftpSubsystem;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.heick.io.monitoring.processor.DefaultFileProcessor;
import dk.heick.io.monitoring.processor.FileProcessor;
import dk.heick.io.monitoring.sftp2.SFtpMonitorTaskConfiguration;
import dk.heick.io.monitoring.sftp2.SFtpMonitoringTask;
import dk.heick.io.monitoring.utils.TempFileManager;
import dk.heick.io.monitoring.utils.TimeSpan;
import dk.heick.io.monitoring.validation.ValidationException;


public class SFtpMonitoringTaskTest {
	
	public final static int SFTP_PORT=22999;
	private File hostkeyFile = new File("src/test/resources/hostkey.ser");
	private TempFileManager tempManager;
	private Logger logger = LoggerFactory.getLogger(SFtpMonitoringTaskTest.class);
	private MockSFTPServer server = new MockSFTPServer(SFTP_PORT,hostkeyFile);

	//
	
	public final static String SFTP_WORK_ROOT="src/test/resources/sftpserver";
	//public final static String SFTP_WORK_ROOT="src/test/resources/sftpserver/datadir";
	public final static String SFTP_REMOTE_DIR="src/test/resources/sftpserver/datadir";
	public final static String SFTP_HOST="localhost";	
	
	public final static String SFTP_USERNAME="remote-username";
	public final static String SFTP_PASSWORD="remote-password";
	
	private static final int FILE_COUNT=10;
    private static final int MIN_FILE_SIZE=10000;
    private static final int RANDOM_FILE_SIZE=100000;
    private static final TimeSpan STABLE_TIME=TimeSpan.createSeconds(1);
    private static final String EXTENSION=".txt";
    
    private static File LOCAL_DIR = null;

	@Before
	public void before() throws Exception {
		server.create();
		server.start();
		tempManager = server.getTempFileManager();
	}

	@After
	public void after() throws Exception {		
		server.stop();
	}
	/*
	private String uploadFile(ChannelSftp sftpChannel,String filename,String content) throws SftpException, IOException {
		String uploadedFileName = SFTP_WORK_ROOT+"/"+filename;
		tempManager.add(new File(uploadedFileName));
		ByteArrayInputStream bais = null;
		try {
			bais = new ByteArrayInputStream(content.getBytes());
			sftpChannel.put(bais, uploadedFileName);
			return uploadedFileName;
		} finally {
			if (bais!=null) {
				bais.close();
			}
		}
	}*/
	
	//http://www.simplecodestuffs.com/sftp-using-java-vfs/
	/*
	private String uploadFileComplete(String filename,String content) throws Exception {
		JSch jsch = new JSch();
		Hashtable<String,String> config = new Hashtable<String,String>();
		config.put("StrictHostKeyChecking", "no");
		JSch.setConfig(config);
		Session session = jsch.getSession(SFTP_USERNAME, SFTP_HOST, SFTP_PORT);
		session.setPassword(SFTP_PASSWORD);
		session.connect();
		Channel channel = session.openChannel("sftp");
		channel.connect();
		ChannelSftp sftpChannel = (ChannelSftp) channel;
				
		String uploadedFileName = SFTP_REMOTE_DIR+"/"+filename;
		tempManager.add(new File(uploadedFileName));
		ByteArrayInputStream bais = null;
		try {
			bais = new ByteArrayInputStream(content.getBytes());
			sftpChannel.put(bais, uploadedFileName);
			sftpChannel.chmod(777, uploadedFileName);
			//sftpChannel.chmod(permissions, path);
		} finally {
			if (bais!=null) {
				bais.close();
			}
		}
		if (sftpChannel.isConnected()) {
			sftpChannel.exit();
			logger.debug("Disconnected channel");
		}
		if (session.isConnected()) {
			session.disconnect();
			logger.debug("Disconnected session");
		}
		return uploadedFileName;
	}	*/
	/*

	@Test
	public void testPutAndGetFile() throws Exception {
		JSch jsch = new JSch();
		Hashtable<String,String> config = new Hashtable<String,String>();
		config.put("StrictHostKeyChecking", "no");
		JSch.setConfig(config);
		Session session = jsch.getSession(SFTP_USERNAME, SFTP_HOST, SFTP_PORT);
		session.setPassword(SFTP_PASSWORD);
		session.connect();
		Channel channel = session.openChannel("sftp");
		channel.connect();
		ChannelSftp sftpChannel = (ChannelSftp) channel;
		
		
		final String testFileContents = "some file contents";
		/*
		String uploadedFileName = SFTP_WORK_ROOT+"/"+SFTP_REMOTE_DIR+"/uploadFile.txt";
		tempManager.add(new File(uploadedFileName));
		sftpChannel.put(new ByteArrayInputStream(testFileContents.getBytes()), uploadedFileName);
		/
		
		String uploadedFileName = uploadFile(sftpChannel, "uploadFile.txt", testFileContents);
		
		String downloadedFileName = "downLoadFile.txt";
		
		File dir = tempManager.createTempDirectory();
		File downloadedFile = new File(dir,downloadedFileName);
		FileOutputStream fos = new FileOutputStream(downloadedFile);
						
		
		sftpChannel.get(uploadedFileName, fos);		
		fos.flush();
		fos.close();
		
		System.out.println(downloadedFile.getAbsolutePath());
		assertTrue(downloadedFile.exists());
		String fileData = tempManager.loadContent(downloadedFile);
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
	*/
	@Test
	public void testDetecting() throws Exception {
		SFtpMonitorTaskConfiguration conf = new SFtpMonitorTaskConfiguration(
				SFTP_HOST,
				SFTP_PORT,
				SFTP_USERNAME,
				SFTP_PASSWORD,
				SFTP_REMOTE_DIR,
				LOCAL_DIR,				
				getFileProcessor(),
				getFileSelector(),
				STABLE_TIME
		);
		conf.setStrictHostKeyChecking(false);
		try {
			SFtpMonitoringTask task= new SFtpMonitoringTask(conf);			
			task.validate();			
			System.out.println(conf.toString());
			task.runTask();
			
			for (int i=0;i<FILE_COUNT;i++) {
				//uploadFileComplete("/hest_"+i+EXTENSION, getFileContent());				
			}
			
			Thread.sleep(STABLE_TIME.getTime()+500);
			task.runTask();
			Thread.sleep(STABLE_TIME.getTime()+500);
			task.runTask();
			Thread.sleep(STABLE_TIME.getTime()+500);
			task.runTask();
			Thread.sleep(STABLE_TIME.getTime()+500);
			task.runTask();
			Thread.sleep(STABLE_TIME.getTime()+500);
			task.runTask();
			Thread.sleep(STABLE_TIME.getTime()+500);
			task.runTask();
			assertEquals(FILE_COUNT,task.getDirectoryArchiveSubdirectory().listFiles().length);
			assertEquals(0,task.getDirectoryErrorSubdirectory().listFiles().length);
			assertEquals(0,task.getConfiguration().getDirectoryProcess().listFiles().length);						
			//
		} catch (ValidationException e) {
			e.printStackTrace();
			fail(e.getMessage());
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}	

	
	public String getFileContent() {
		StringBuilder s = new StringBuilder();
		Random randomizer = new Random();
		int fileSize=randomizer.nextInt(RANDOM_FILE_SIZE)+MIN_FILE_SIZE;
		for (int i1=0;i1<fileSize;i1++) {
			s.append('a');
		}
		return s.toString();
	}
	
	private FileProcessor getFileProcessor() {
		return new DefaultFileProcessor() {
			@Override
			public void process(Properties context, File file) throws Exception {
				byte[] bytes = Files.readAllBytes(file.toPath());
				System.out.println("Processing file ["+file.getName()+"] size ["+bytes.length+"]");				
			}
		};
	}	
	
	private FileSelector getFileSelector() {
		return new FileSelector() {

			@Override
			public boolean includeFile(FileSelectInfo fileInfo) throws Exception {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean traverseDescendents(FileSelectInfo fileInfo) throws Exception {
				// TODO Auto-generated method stub
				return false;
			}
			
		};
	}
/*
	private SFtpFileFilter getSFtpFileFilter() {
		return new SFtpFileFilter() {
			@Override
			public boolean accept(WrappedSFtpFile sftpFile) {
				return sftpFile.getFile().getFilename().toLowerCase().endsWith(EXTENSION);
			}
		};
	}*/

}
