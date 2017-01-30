package dk.heick.io.monitoring.cleanup;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.BasicConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import dk.heick.io.monitoring.utils.TempFileManager;


/**
 * ROOT
 * 		-- DIR1
 *   		<empty>
 *   	-- DIR2
 *   		file1.txt (0 day)
 *   		file2.txt (1 day)
 *   		file3.txt (2 day)
 *   		file4.txt (3 day)
 *   		file5.txt (4 day)
 *   	-- DIR3
 *   			-- DIR4
 *   				file6.txt (1 day)
 *   				file7.txt (4 day)
 *   			-- DIR5
 *   				<empty>
 *   			-- DIR6
 *   				<empty>
 *   	-- DIR7
 *   			-- DIR8
 *   				file8.txt (8 day)
 *   				file9.txt (8 day)
 *   				filenotdelete01.xml
 *   			-- DIR9
 *   				<empty>
 *   				-- DIR10
 *   					<empty>
 *   			-- DIR11
 *   				<empty>
 *   			filenotdelete02.xml
 */
public class DirectoryCleanupTaskTest {
	
	private TempFileManager tempFileManager = null;

	@Before
	public void before() {
		BasicConfigurator.configure();
		tempFileManager = new TempFileManager();
		File root = tempFileManager.createTempRootDirectory("root");
		System.out.println(root.getAbsolutePath());
		File dir1 = tempFileManager.createDirectory(root, "dir1");
		File dir2 = tempFileManager.createDirectory(root, "dir2");
		File dir3 = tempFileManager.createDirectory(root, "dir3");
		File dir7 = tempFileManager.createDirectory(root, "dir7");
		//
		File file1 = tempFileManager.createTempFile(dir2, "file1", "txt");
		File file2 = tempFileManager.createTempFile(dir2, "file2", "txt");
		File file3 = tempFileManager.createTempFile(dir2, "file3", "txt");
		File file4 = tempFileManager.createTempFile(dir2, "file4", "txt");
		File file5 = tempFileManager.createTempFile(dir2, "file5", "txt");
		//
		File dir4 = tempFileManager.createDirectory(dir3,"dir4");
		File dir5 = tempFileManager.createDirectory(dir3,"dir5");
		File dir6 = tempFileManager.createDirectory(dir3,"dir6");
		//
		File file6 = tempFileManager.createTempFile(dir4, "file6", "txt");
		File file7 = tempFileManager.createTempFile(dir4, "file7", "txt");
		//
		File dir8 = tempFileManager.createDirectory(dir7,"dir8");
		File dir9 = tempFileManager.createDirectory(dir7,"dir9");
		File dir10 = tempFileManager.createDirectory(dir9,"dir10");
		File dir11 = tempFileManager.createDirectory(dir7,"dir11");
		//
		File file8 = tempFileManager.createTempFile(dir8, "file8", "txt");
		File file9 = tempFileManager.createTempFile(dir8, "file9", "txt");
		//
		File filenotdelete01 = tempFileManager.createTempFile(dir8, "filenotdelete01", "xml");
		File filenotdelete02 = tempFileManager.createTempFile(dir7, "filenotdelete02", "xml");
		//		
		//
		tempFileManager.setFileAgeInDays(file1, 0);
		tempFileManager.setFileAgeInDays(file2, 1);
		tempFileManager.setFileAgeInDays(file3, 2);
		tempFileManager.setFileAgeInDays(file4, 3);
		tempFileManager.setFileAgeInDays(file5, 4);
		tempFileManager.setFileAgeInDays(file6, 1);
		tempFileManager.setFileAgeInDays(file7, 4);
		tempFileManager.setFileAgeInDays(file8, 18);
		tempFileManager.setFileAgeInDays(file9, 8);
		
		assertTrue(file1.exists());
		assertTrue(file2.exists());
		assertTrue(file3.exists());
		assertTrue(file4.exists());
		assertTrue(file5.exists());
		assertTrue(file6.exists());
		assertTrue(file7.exists());
		assertTrue(file8.exists());
		assertTrue(file9.exists());
	}

	@After
	public void after() {
		try {
			tempFileManager.cleanup();
		} catch (IOException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}	

	@Test
	public void testDirectoryCleanupTask() {
		System.out.println(tempFileManager.toString());
		DirectoryCleanupConfiguration configuration = new DirectoryCleanupConfiguration(tempFileManager.getTempDirectory("root"));
		DirectoryCleanupTask dct = new DirectoryCleanupTask(configuration);
		
		assertTrue(tempFileManager.getDirectory("dir2").exists());
		assertEquals(4,tempFileManager.getTempDirectory("root").list().length);
		assertEquals(0,tempFileManager.getDirectory("dir1").list().length);
		assertEquals(5,tempFileManager.getDirectory("dir2").list().length);
		assertEquals(3,tempFileManager.getDirectory("dir3").list().length);
		assertEquals(2,tempFileManager.getDirectory("dir4").list().length);
		assertEquals(0,tempFileManager.getDirectory("dir5").list().length);
		assertEquals(0,tempFileManager.getDirectory("dir6").list().length);
		assertEquals(4,tempFileManager.getDirectory("dir7").list().length);
		assertEquals(3,tempFileManager.getDirectory("dir8").list().length);
		assertEquals(1,tempFileManager.getDirectory("dir9").list().length);
		assertEquals(0,tempFileManager.getDirectory("dir10").list().length);
		assertEquals(0,tempFileManager.getDirectory("dir11").list().length);		
		
		dct.runTask();
		assertFalse(tempFileManager.existsDirectory("dir1"));
		assertTrue(tempFileManager.existsDirectory("dir2"));
		assertTrue(tempFileManager.existsDirectory("dir3"));
		assertTrue(tempFileManager.existsDirectory("dir4"));
		assertFalse(tempFileManager.existsDirectory("dir5"));
		assertFalse(tempFileManager.existsDirectory("dir6"));
		assertTrue(tempFileManager.existsDirectory("dir7"));
		assertTrue(tempFileManager.existsDirectory("dir8"));
		assertFalse(tempFileManager.existsDirectory("dir9"));
		assertFalse(tempFileManager.existsDirectory("dir10"));
		assertFalse(tempFileManager.existsDirectory("dir11"));
		
		assertEquals(3,tempFileManager.getTempDirectory("root").list().length);
		assertEquals(5,tempFileManager.getDirectorySize("dir2"));
		assertEquals(1,tempFileManager.getDirectorySize("dir3"));
		assertEquals(2,tempFileManager.getDirectorySize("dir4"));
		assertEquals(2,tempFileManager.getDirectorySize("dir7"));
		assertEquals(2,tempFileManager.getDirectorySize("dir8"));
		assertEquals(0,tempFileManager.getDirectorySize("dir9"));
		
		assertTrue(tempFileManager.doTempFileExists("file1", "txt"));
		assertTrue(tempFileManager.doTempFileExists("file2", "txt"));
		assertTrue(tempFileManager.doTempFileExists("file3", "txt"));
		assertTrue(tempFileManager.doTempFileExists("file4", "txt"));
		assertTrue(tempFileManager.doTempFileExists("file5", "txt"));
		assertTrue(tempFileManager.doTempFileExists("file6", "txt"));
		assertTrue(tempFileManager.doTempFileExists("file7", "txt"));
		assertFalse(tempFileManager.doTempFileExists("file8", "txt"));
		assertTrue(tempFileManager.doTempFileExists("file9", "txt"));
		assertTrue(tempFileManager.doTempFileExists("filenotdelete01", "xml"));
		assertTrue(tempFileManager.doTempFileExists("filenotdelete02", "xml"));
		
		/**
		 * ROOT
		 * 		-- DIR1
		 *   		<empty>
		 *   	-- DIR2
		 *   		file1.txt (0 day)
		 *   		file2.txt (1 day)
		 *   		file3.txt (2 day)
		 *   		file4.txt (3 day)
		 *   		file5.txt (4 day)
		 *   	-- DIR3
		 *   			-- DIR4
		 *   				file6.txt (1 day)
		 *   				file7.txt (4 day)
		 *   			-- DIR5
		 *   				<empty>
		 *   			-- DIR6
		 *   				<empty>
		 *   	-- DIR7
		 *   			-- DIR8
		 *   				file8.txt (8 day)
		 *   				file9.txt (8 day)
		 *   				filenotdelete01.xml
		 *   			-- DIR9
		 *   				<empty>
		 *   				-- DIR10
		 *   					<empty>
		 *   			-- DIR11
		 *   				<empty>
		 *   			filenotdelete02.xml
		 */
	}


}
