package dk.heick.io.monitoring.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.StandardCharsets;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import dk.heick.io.monitoring.IOMonitoringConstants;

public class FileUtilsTest {
	
	private TempFileManager tempManager = null;
	private File rootDirectory;
	
	@Before
	public void before() throws Exception {
		tempManager = new TempFileManager();
		rootDirectory = tempManager.createTempDirectory("root");
	}
	@After
	public void after() throws Exception {
		tempManager.cleanup();			
	}

	@Test
	public void testGetCurrentDirectory() {
		assertEquals("io-monitoring-core",FileUtils.getCurrentDirectory().getName());		
	}

	@Test
	public void testCreateDirectory() {
		try {
			FileUtils.createDirectory(null);
			fail();
		} catch (IOException e) {			
		}
		File dir = new File(rootDirectory,"hans");
		tempManager.add(dir);
		try {			
			FileUtils.createDirectory(dir);
			assertTrue(dir.exists());
			FileUtils.createDirectory(dir);
		} catch (IOException e) {
			fail(e.getMessage());
		} 
	}

	@Test
	public void testRenameDirectoryFileString() {
		File dirFrom = tempManager.createTempDirectory(rootDirectory,"hans2");	
		File dirTo = new File(rootDirectory,"hans3");
		tempManager.add(dirTo);
		
		try {
			FileUtils.renameDirectory(null, "hans3");
			fail();
		} catch (IOException e) {			
		}
		try {
			FileUtils.renameDirectory(new File("lkasjsldkj"), "hans3");
			fail();
		} catch (IOException e) {			
		}
		try {
			String name = null;
			FileUtils.renameDirectory(new File("lkasjsldkj"),name);
			fail();
		} catch (IOException e) {			
		}
		try {
			FileUtils.renameDirectory(dirFrom," ");
			fail();
		} catch (IOException e) {			
		}
		
		try {
			FileUtils.renameDirectory(dirFrom, "hans3");
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testRenameDirectoryFileFile() {
		File  dirFrom = tempManager.createTempDirectory(rootDirectory,"b1");	
		File dirFromNotExists = new File(rootDirectory,"bNotExists");
		File dirTo = new File(rootDirectory,"b2");
		dirFrom.mkdir();
		//
		try {
			FileUtils.renameDirectory(dirFrom, tempManager.createNullFile());
			fail();
		} catch (IOException e) {			
		}
		//
		try {
			FileUtils.renameDirectory(dirFromNotExists, tempManager.createNullFile());
			fail();
		} catch (IOException e) {			
		}
		//
		try {
			FileUtils.renameDirectory(tempManager.createNullFile(), dirTo);
			fail();
		} catch (IOException e) {
		}
		//
		try {
			FileUtils.renameDirectory(dirFrom, dirTo);
			assertFalse(dirFrom.exists());
			assertTrue(dirTo.exists());
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testDeleteDirectoryOnlyFiles() {
		try {
			FileUtils.deleteDirectoryOnlyFiles(null);
			fail();
		} catch (IOException e) {			
		}
		//
		try {
			FileUtils.deleteDirectoryOnlyFiles(new File(rootDirectory,"lsdkfjslkdfjsldkfjsdkl"));
			fail();
		} catch (IOException e) {			
		}
		//
		try {			
			File file = tempManager.createTempFile(rootDirectory);
			FileUtils.deleteDirectoryOnlyFiles(file);
			fail();
		} catch (IOException e) {			
		}
		//
		try {
			File dirNotExists = new File(rootDirectory,"bNotExists");
			FileUtils.deleteDirectoryOnlyFiles(dirNotExists);
			fail();
		} catch (IOException e) {			
		}
		//		
		File dir = tempManager.createTempDirectory(rootDirectory,"c1");									
		File f1 = tempManager.createTempFile(dir,"aa3114", ".txt");
		File f2 = tempManager.createTempFile(dir,"aa233462", ".txt");
		File f3 = tempManager.createTempFile(dir,"aa2813", ".txt");
		File d1 = tempManager.createTempDirectory(dir,"akl235");
		File d2 = tempManager.createTempDirectory(dir,"akl234");			

		try {			
			FileUtils.deleteDirectoryOnlyFiles(dir);
			assertFalse(f1.exists());
			assertFalse(f2.exists());
			assertFalse(f3.exists());
			assertTrue(d1.exists());
			assertTrue(d2.exists());
		} catch (IOException e) {	
			fail(e.getMessage());
		}
	}

	@Test
	public void testDeleteDirectoryOnlySubDirectories() {
		try {
			FileUtils.deleteDirectoryOnlySubDirectories(null);
			fail();
		} catch (IOException e) {			
		}
		//
		try {
			FileUtils.deleteDirectoryOnlySubDirectories(new File(rootDirectory,"lsdkfjslkdfjsldkfjsdkl"));
			fail();
		} catch (IOException e) {			
		}
		//
		try {
			File file = tempManager.createTempFile(rootDirectory,"djdjdjdjsghs");						
			FileUtils.deleteDirectoryOnlySubDirectories(file);
			fail();
		} catch (IOException e) {			
		}
		//
		try {
			File dirNotExists = new File(rootDirectory,"bNotExists");
			FileUtils.deleteDirectoryOnlySubDirectories(dirNotExists);
			fail();
		} catch (IOException e) {			
		}
		//					
		File dir = tempManager.createTempDirectory(rootDirectory,"c1");							
		File f1 = tempManager.createTempFile(dir,"aa83114", ".txt");
		File f2 = tempManager.createTempFile(dir,"aa2933462", ".txt");
		File f3 = tempManager.createTempFile(dir,"aa72813", ".txt");
		File d1 = tempManager.createTempDirectory(dir,"ak4l235");
		File d2 = tempManager.createTempDirectory(dir,"ak4l234");	

		try {			
			FileUtils.deleteDirectoryOnlySubDirectories(dir);
			assertTrue(f1.exists());
			assertTrue(f2.exists());
			assertTrue(f3.exists());
			assertFalse(d1.exists());
			assertFalse(d2.exists());
		} catch (IOException e) {	
			fail(e.getMessage());
		}
	}
	
	

	@Test
	public void testLoadAsStringFileIntString() {
		File file = new File("src/test/resources/test01.txt");
		try {
			FileUtils.loadAsString(null, 2048, StandardCharsets.UTF_8);
			fail();
		} catch (IOException e) {		
		}
		try {
			FileUtils.loadAsString(file, -88, null);
			fail();
		} catch (IOException e) {		
		}
		try {
			String result = FileUtils.loadAsString(file,1024, StandardCharsets.UTF_8);
			assertNotNull(result);
			assertEquals("aa\r\nbb\r\ncc\r\ndd", result);
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testLoadAsLines() {
		File file = new File("src/test/resources/test01.txt");
		try {
			FileUtils.loadAsLines(null);
			fail();
		} catch (IOException e) {		
		}
		try {
			StringList result = FileUtils.loadAsLines(file);
			assertNotNull(result);
			assertEquals(4, result.size());			
			assertEquals("aa", result.get(0));
			assertEquals("bb", result.get(1));
			assertEquals("cc", result.get(2));
			assertEquals("dd", result.get(3));
		} catch (IOException e) {		
		}
		assertTrue(file.exists());		
	}
	
	@Test
	public void testSetFileLastModified() {
		try {
			File file = tempManager.createTempFile(rootDirectory,"a123", ".txt");							
			FileUtils.setFileLastModified(file, 50);			
			assertEquals(50, file.lastModified());
			try {
				FileUtils.setFileLastModified(file, -50);
				fail();
			} catch (IOException e) {				
			}
			try {
				FileUtils.setFileLastModified(null, 50);
				fail();
			} catch (IOException e) {				
			}
		} catch (IOException e) {
			fail(e.getMessage());
		} 
	}

	@Test
	public void testTouch() {		
		try {
			File file = tempManager.createTempFile(rootDirectory,"a123", ".txt");			
			long t1 = file.lastModified();
			Thread.sleep(500);
			FileUtils.touch(file);
			long t2 = file.lastModified();
			assertTrue(t2>t1);
		} catch (IOException e) {
			fail(e.getMessage());
		} catch (InterruptedException e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testGetExtension() {		
		
		assertEquals("dir", FileUtils.getExtension(rootDirectory));
		//
		File file = tempManager.createTempFile(rootDirectory,"a123", ".txt");
		assertEquals("txt", FileUtils.getExtension(file));
		//
		file = tempManager.createTempFile(rootDirectory,"a1234", "txt");
		assertEquals("txt", FileUtils.getExtension(file));
		//
		assertEquals("txt", FileUtils.getExtension(new File("lskdfj.txt")));
		assertEquals("unknown", FileUtils.getExtension(new File("lskdfj")));
		
		try {
			FileUtils.getExtension(null);
			fail();
		} catch (NullPointerException e) {			
		}
	}
	
	@Test
	public void testIsLockFile() {
		File file = null;
		//
		assertFalse(FileUtils.isLockFile(file));
		assertFalse(FileUtils.isLockFile(new File("my.txt")));
		//		
		assertTrue(FileUtils.isLockFile(new File("my"+IOMonitoringConstants.LOCK_FILE_EXTENSION)));
		assertTrue(FileUtils.isLockFile(new File(" my"+IOMonitoringConstants.LOCK_FILE_EXTENSION+" ")));
		assertTrue(FileUtils.isLockFile(new File(("my"+IOMonitoringConstants.LOCK_FILE_EXTENSION).toUpperCase())));
		assertTrue(FileUtils.isLockFile(new File(("my"+IOMonitoringConstants.LOCK_FILE_EXTENSION).toLowerCase())));		
	}


	@Test
	public void testIsFileLocked() {
		File file = new File("src/test/resources/Book1.xlsx");		
		long lastmod1 = file.lastModified();
		long start = System.currentTimeMillis();
		assertFalse(FileUtils.isFileLocked(file));
		assertTrue(FileUtils.isFileUnlocked(file));
		long end = System.currentTimeMillis();
		System.out.println("Time:"+(end-start));
		//OBTAIN LOCK
		
		try {
	        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
	        FileChannel fileChannel = randomAccessFile.getChannel();
	        FileLock lock = null;
	        try {		            
	            fileChannel.tryLock();
	            assertTrue(FileUtils.isFileLocked(file));
	    		assertFalse(FileUtils.isFileUnlocked(file));
	        } catch (Exception ex) {
	        	fail(ex.getMessage());
	        } finally {
	            if (lock != null) {
	                lock.release();
	            }		 
	            if (fileChannel != null) {
	                fileChannel.close();
	            }		 
	            if (randomAccessFile != null) {
	            	randomAccessFile.close();
	            }
	        }
    	} catch (FileNotFoundException e) {
    		fail(e.getMessage());
    	} catch (IOException e) {	    		
    		fail(e.getMessage());
    	}
		assertFalse(FileUtils.isFileLocked(file));
		assertTrue(FileUtils.isFileUnlocked(file));
		long lastmod2 = file.lastModified();
		assertEquals(lastmod1,lastmod2);
		
		//TODO maybe make other Thread that tries to make a FileLock		
	}
	
	
}
