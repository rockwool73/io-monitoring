package dk.heick.io.monitoring.file;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Properties;
import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import dk.heick.io.monitoring.filter.EndsWithFileFilter;
import dk.heick.io.monitoring.filter.OnlyFilesFileFilter;
import dk.heick.io.monitoring.processor.DefaultFileProcessor;
import dk.heick.io.monitoring.processor.FileProcessor;
import dk.heick.io.monitoring.utils.TempFileManager;
import dk.heick.io.monitoring.utils.TimeSpan;
import dk.heick.io.monitoring.validation.ValidationException;

public class DirectoryMonitorTaskTest {
	
	
	private static File ROOT = null;
	//private static File LOCAL_HOME_DIR;
    private static final TimeSpan STABLE_TIME=TimeSpan.createSeconds(1);
    private static final String EXTENSION=".txt";
    private static final int MIN_FILE_SIZE=10000;
    private static final int RANDOM_FILE_SIZE=100000;    
    private static final int FILE_COUNT=3;
    
	private TempFileManager tempManager = null;
	
	@Before
	public void before() throws Exception {
		tempManager = new TempFileManager();
		ROOT = tempManager.createTempDirectory("junittest_dir");
	}
	@After
	public void after() throws Exception {
		tempManager.cleanup();			
	}
    
    
    /*@BeforeClass 
    public static void doBeforeClass() throws IOException {
    	ROOT = Files.createTempDirectory("junittest_dir");
    	LOCAL_HOME_DIR = ROOT.toFile();
    }
    
    @Before
	public void setUp() throws Exception {				 
		tearDown();			
		System.out.println(LOCAL_HOME_DIR.getAbsolutePath());
		if (!LOCAL_HOME_DIR.exists()) {
			assertTrue(LOCAL_HOME_DIR.mkdirs());
		}		
	}*/
    

	/*@After
	public void tearDown() throws Exception {
		File archive = new File(ROOT,IOMonitoringConstants.DIRECTORY_NAME_ARCHIVE);
		if (archive.exists()) {
			for (File f: archive.listFiles()) {
				if (f.isDirectory()) {
					for (File f2: f.listFiles()) {
						f2.delete();
					}
				}
				f.delete();
			}
		}
		File error = new File(ROOT,IOMonitoringConstants.DIRECTORY_NAME_ERROR);
		if (error.exists()) {
			for (File f: error.listFiles()) {
				if (f.isDirectory()) {
					for (File f2: f.listFiles()) {
						f2.delete();
					}
				}
				f.delete();
			}
		}		
		if (ROOT.exists()) {
			for (File f: ROOT.listFiles()) {
				f.delete();
			}
			ROOT.delete();
		}			
	} */

	@Test
	public void testDirectoryMonitorTask() {
		
		//create 1 in process		
		try {
			DirectoryMonitorTaskConfiguration configuration = new DirectoryMonitorTaskConfiguration(
					ROOT,					
					getFileProcessor(),
					getFileFilter(),
					STABLE_TIME
			);
			if  (!configuration.getDirectoryProcess().exists()) {
				configuration.getDirectoryProcess().mkdirs();
			}
			createFile(new File(configuration.getDirectoryProcess(),"previous"+EXTENSION),getFileContent());			
			DirectoryMonitorTask task = new DirectoryMonitorTask(configuration);	
			task.validate();			
			
			assertEquals(0,task.getConfiguration().getDirectoryProcess().list().length);
			assertEquals(1,task.getConfiguration().getDirectory().listFiles(new OnlyFilesFileFilter()).length);
			//
			for (int i=0;i<FILE_COUNT;i++) {
				File file = new File(configuration.getDirectory(),"/hest_"+i+EXTENSION);
				createFile(file,getFileContent());
			}
			File[] files01 = configuration.getDirectory().listFiles(new OnlyFilesFileFilter());			
			assertEquals(FILE_COUNT+1,files01.length);
			task.runTask();
			files01 = configuration.getDirectory().listFiles(new OnlyFilesFileFilter());
			assertEquals(FILE_COUNT+1,files01.length);
			assertEquals(FILE_COUNT+1,task.size());
			Thread.sleep(STABLE_TIME.getTime()+500);
			task.runTask();
			assertEquals(0,task.getConfiguration().getDirectoryProcess().list().length);
			assertEquals(0,task.getConfiguration().getDirectoryProcess().list().length);			
			assertEquals(FILE_COUNT,task.getDirectoryArchiveSubdirectory().listFiles().length);
			assertEquals(2,task.getDirectoryErrorSubdirectory().listFiles().length);
			assertEquals(0,task.getConfiguration().getDirectoryProcess().listFiles().length);			
			assertEquals(3,task.getDirectoryArchiveSubdirectory().listFiles().length);
			assertEquals(0,task.size());
		} catch (ValidationException | IOException | InterruptedException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
			
	}
	
	private FileFilter getFileFilter() throws ValidationException {
		return new EndsWithFileFilter(".txt");
	}
	
	private FileProcessor getFileProcessor() {
		return new DefaultFileProcessor() {
			@Override
			public void process(Properties context, File file) throws Exception {
				if (file.getName().contains("hest_2.txt")) {
					System.out.println("Invalid file processing ["+file.getName()+"]");
					throw new Exception("Invalid txt");
				} else {
					byte[] bytes = Files.readAllBytes(file.toPath());
					System.out.println("Processing file ["+file.getName()+"] size ["+bytes.length+"]");					
				}
			}
		};
	}	
	
	public void createFile(File file,String content) throws IOException {
		FileOutputStream out = new FileOutputStream(file);
		out.write(content.getBytes());
		out.flush();
		out.close();
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

		
}
