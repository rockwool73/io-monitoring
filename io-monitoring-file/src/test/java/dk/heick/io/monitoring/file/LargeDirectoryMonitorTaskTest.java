package dk.heick.io.monitoring.file;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Properties;

import org.apache.log4j.BasicConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import dk.heick.io.monitoring.errorhandler.MonitorTaskLocalFileErrorHandler;
import dk.heick.io.monitoring.filter.EndsWithFileFilter;
import dk.heick.io.monitoring.processor.DefaultFileProcessor;
import dk.heick.io.monitoring.processor.FileProcessor;
import dk.heick.io.monitoring.timer.MonitorTaskTimer;
import dk.heick.io.monitoring.utils.TempFileManager;
import dk.heick.io.monitoring.utils.TimeSpan;

/**
 * Creates java.util.timer MonitorTask
 * Create a loop that  
 * 		creates X files
 * 		of Y size
 * 		that take Z time to create
 * test for file detected and processed
 * test for files gone into error

 * @author Frederik
 *
 */
public class LargeDirectoryMonitorTaskTest {
	
	private TempFileManager tempFileManager = new TempFileManager();
	private MonitorTaskTimer timer;
	
	@Before
	public void before() {
		BasicConfigurator.configure();
		System.out.println("#######################################");
		System.out.println("  THIS IS A LONG RUNNING TEST ");
		System.out.println("  Is takes about 4 minuttes.");
		System.out.println("#######################################");
	}
	
	@After
	public void after() {
		try {
			tempFileManager.cleanup();
		} catch (IOException e) {			
			e.printStackTrace();
		}
		if (timer!=null) {
			timer.cancelAll();
		}
	}

	@Test
	public void testLargeDirectoryMonitorTaskTest() {
		
		File ROOT = tempFileManager.createTempDirectory();
		System.out.println("ROOT:"+ROOT.getAbsolutePath());
		DirectoryMonitorTaskConfiguration configuration = new DirectoryMonitorTaskConfiguration(
				ROOT,					
				getFileProcessor(),
				getFileFilter(),
				TimeSpan.createMilliSeconds(1379)
		);
		configuration.setLocalFileErrorHandler(getMonitorTaskLocalFileErrorHandler());
		//configuration.withVerbose();
		
		DirectoryMonitorTask task = null;
		try {
			task = new DirectoryMonitorTask(configuration);
			
			timer = new MonitorTaskTimer();
			timer.addMonitoringTask(task, 500L);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		long start = System.currentTimeMillis();
			
		FileCreationThread kb5 = new FileCreationThread("kb5",ROOT,30, 5*1024, 1000,1,false, "data", "txt");
		FileCreationThread kb5lock = new FileCreationThread("kb5lock",ROOT,25, 5*1024, 12000,1,true, "data", "txt");
		//FileCreationThread kb25 = new FileCreationThread("kb25",ROOT,35, 25*1024, 6500,2,false, "data", "txt");
		//FileCreationThread kb35lock = new FileCreationThread("kb35lock",ROOT,60, 35*1024, 1000,3,true, "data", "txt");
		//FileCreationThread kb250 = new FileCreationThread("kb250",ROOT,50, 255*1024,4000,4,false, "data", "txt");
		//FileCreationThread kb500lock = new FileCreationThread("kb500lock",ROOT,20, 500*1024, 8000,5,true, "data", "txt");
		kb5.start();
		kb5lock.start();
		//kb25.start();
		//kb35lock.start();
		//kb250.start();
		//kb500lock.start();
		//
		try {
			kb5.join();
			kb5lock.join();
			//kb25.join();
			//kb35lock.join();
			//kb250.join();
			//kb500lock.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}	
		
		for (int i=0;i<50;i++) {
			System.out.println("Waiting");
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {				
			}
		}
		
		assertEquals(30+25+35+60+50+20,task.getDirectoryArchiveSubdirectory().list().length);
		assertEquals(3,task.getConfiguration().getDirectory().list().length);
		assertEquals(0,task.getDirectoryErrorSubdirectory().list().length);
		System.out.println("DONE time ("+(System.currentTimeMillis()-start)+" ms)");
	}
	
	private FileFilter getFileFilter() {
		return new EndsWithFileFilter(".txt");
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
	
	private MonitorTaskLocalFileErrorHandler getMonitorTaskLocalFileErrorHandler() {
		return new MonitorTaskLocalFileErrorHandler() {
			@Override
			public void onDeleteFileFailure(File file, Exception e) {
				System.err.println("onDeleteFileFailure:"+file.getAbsolutePath()+":"+e.getMessage());
				//fail(e.getMessage());
				
			}
			@Override
			public boolean onMovedToErrorDirectory(File file, File errorFile, Exception e) {
								
				return true;
			}
			@Override
			public void onMoveFileFailure(File sourceFile, File targetFile, Exception e) {
				System.err.println("onMoveFileFailure:"+sourceFile.getAbsolutePath()+":"+e.getMessage());
				//fail(e.getMessage());				
			}			
		};
	}

}
