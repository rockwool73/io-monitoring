package dk.heick.io.monitoring.ftp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.Random;

import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileFilter;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockftpserver.fake.FakeFtpServer;
import org.mockftpserver.fake.UserAccount;
import org.mockftpserver.fake.filesystem.FileEntry;
import org.mockftpserver.fake.filesystem.FileSystem;
import org.mockftpserver.fake.filesystem.WindowsFakeFileSystem;

import dk.heick.io.monitoring.IOMonitoringConstants;
import dk.heick.io.monitoring.processor.DefaultFileProcessor;
import dk.heick.io.monitoring.processor.FileProcessor;
import dk.heick.io.monitoring.utils.TimeSpan;
import dk.heick.io.monitoring.validation.ValidationException;

public class FtpMonitoringTaskTest {

	
	private static Path ROOT = null;
	private static File LOCAL_HOME_DIR;
	
	private static final String FTP_DIR="ftpserver";
    private static final String FTP_HOME_DIR = "src/test/resources/"+FTP_DIR;
    private static final String REMOTE_DIR = "dir";
    private static final int FILE_COUNT=10;
    private static final int MIN_FILE_SIZE=10000;
    private static final int RANDOM_FILE_SIZE=100000;
    private static final File FTP_ROOT = new File(FTP_HOME_DIR);
    private static final File FTP_SUBDIR = new File(FTP_HOME_DIR,REMOTE_DIR);
    private static final TimeSpan STABLE_TIME=TimeSpan.createSeconds(1);
    private static final String EXTENSION=".txt";
    //
    public static final String USERNAME = "user";
    public static final String PASSWORD = "password";
    private static int PORT=21;
	
    private FakeFtpServer fakeFtpServer=null;
    
    @BeforeClass 
    public static void doBeforeClass() throws IOException {
    	ROOT = Files.createTempDirectory("junittest_ftp");
    	LOCAL_HOME_DIR = ROOT.toFile();
    }

	@Before
	public void setUp() throws Exception {
		tearDown();
		//		
		System.out.println(LOCAL_HOME_DIR.getAbsolutePath());
		if (!LOCAL_HOME_DIR.exists()) {
			assertTrue(LOCAL_HOME_DIR.mkdirs());
		}		
		fakeFtpServer = new FakeFtpServer();
		fakeFtpServer.setServerControlPort(0); // use any free port

		FileSystem fileSystem = new WindowsFakeFileSystem();
		for (int i=0;i<FILE_COUNT;i++) {
			String afile = new File(FTP_SUBDIR,"/hest_"+i+EXTENSION).getAbsolutePath();
			fileSystem.add(new FileEntry(afile	, getFileContent()));
		}
		fakeFtpServer.setFileSystem(fileSystem);

		UserAccount userAccount = new UserAccount(USERNAME, PASSWORD, FTP_ROOT.getAbsolutePath());
		fakeFtpServer.addUserAccount(userAccount);

		fakeFtpServer.start();
		PORT = fakeFtpServer.getServerControlPort();
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
	
	@Test
	public void testDetecting() {
		FtpMonitorTaskConfiguration conf = new FtpMonitorTaskConfiguration(
				"localhost",
				PORT,
				USERNAME,
				PASSWORD,
				FTP_SUBDIR.getAbsolutePath(),
				LOCAL_HOME_DIR,				
				getFileProcessor(),
				getFtpFilter(),
				STABLE_TIME
		);
		try {
			FtpMonitoringTask task= new FtpMonitoringTask(conf);
			task.validate();			
			System.out.println(conf.toString());
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
	

	
	private FTPFileFilter getFtpFilter() {
		return new FTPFileFilter() {			
			@Override
			public boolean accept(FTPFile file) {
				return file.isFile() && file.getName().toLowerCase().endsWith(EXTENSION);
			}
		};
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


	@After
	public void tearDown() throws Exception {
		if (fakeFtpServer!=null) {
			fakeFtpServer.stop();				
			for (File f: new File(LOCAL_HOME_DIR,IOMonitoringConstants.DIRECTORY_NAME_ARCHIVE).listFiles()) {
				if (f.isDirectory()) {
					for (File f2: f.listFiles()) {
						f2.delete();
					}
				}
				f.delete();
			}
			for (File f: new File(LOCAL_HOME_DIR,IOMonitoringConstants.DIRECTORY_NAME_ERROR).listFiles()) {
				if (f.isDirectory()) {
					for (File f2: f.listFiles()) {
						f2.delete();
					}
				}
				f.delete();		
			}
			for (File f: LOCAL_HOME_DIR.listFiles()) {
				f.delete();
			}
			LOCAL_HOME_DIR.delete();
		}
	}
		


}
