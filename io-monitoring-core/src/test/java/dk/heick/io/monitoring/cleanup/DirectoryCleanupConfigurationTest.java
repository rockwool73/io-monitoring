package dk.heick.io.monitoring.cleanup;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import dk.heick.io.monitoring.errorhandler.DefaultDirectoryCleanupErrorHandler;
import dk.heick.io.monitoring.filter.OnlyFilesFileFilter;
import dk.heick.io.monitoring.utils.TempFileManager;
import dk.heick.io.monitoring.validation.ValidationException;

public class DirectoryCleanupConfigurationTest {

	private TempFileManager tempFileManager = null;

	@Before
	public void before() {
		tempFileManager = new TempFileManager();
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
	public void testDirectoryCleanupConfiguration() {
		DirectoryCleanupConfiguration c = new DirectoryCleanupConfiguration(null);
		assertNotNull(c);
		try {
			c.validate();
			fail();
		} catch (ValidationException e) {
		}
		c = new DirectoryCleanupConfiguration(tempFileManager.createTempDirectory());
		assertNotNull(c);
		try {
			c.validate();		
			assertFalse(c.isBasedOnMonitorTask());
			assertNotNull(c.getDirectory());
			assertTrue(c.getDirectory().exists());
			assertEquals(IOCleanupConstants.DEFAULT_KEEP_CONTENT_TIME, c.getKeepContentTime());
			assertEquals(IOCleanupConstants.DEFAULT_MAX_DELETING_PROCESSING_TIME, c.getMaxDeletingProcessingTime());
			assertNotNull(c.getFilter());
			assertEquals(OnlyFilesFileFilter.class,c.getFilter().getClass());
			assertTrue(c.isDeleteEmptyDirectories());
			assertEquals(-1,c.getMaxDepth());
			assertTrue(c.isNoMaxDepth());
			assertTrue(c.isEnabled());
			assertFalse(c.isDisabled());
			assertTrue(c.isDeleteEmptyDirectories());
			assertNotNull(c.getErrorHandler());
			assertEquals(DefaultDirectoryCleanupErrorHandler.class,c.getErrorHandler().getClass());			
		} catch (ValidationException e) {
			fail(e.getMessage());
		}
	}
	/*
	@Test
	public void testCleanupArchive() {
		fail("Not yet implemented");
	}

	@Test
	public void testCleanupError() {
		fail("Not yet implemented");
	}

	@Test
	public void testValidate() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetDirectory() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetMaxDeletingProcessingTime() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetKeepContentTime() {
		fail("Not yet implemented");
	}

	@Test
	public void testIsDeleteEmptyDirectories() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetFilter() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetMaxDepth() {
		fail("Not yet implemented");
	}

	@Test
	public void testIsNoMaxDepth() {
		fail("Not yet implemented");
	}

	@Test
	public void testIsEnabled() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetErrorHandler() {
		fail("Not yet implemented");
	}

	@Test
	public void testIsBasedOnMonitorTask() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetBasedOnMonitorTask() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetMaxDeletingProcessingTime() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetKeepContentTime() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetDeleteEmptyDirectories() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetFilter() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetMaxDepth() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetNoMaxDepth() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetEnabled() {
		fail("Not yet implemented");
	}

	@Test
	public void testEnable() {
		fail("Not yet implemented");
	}

	@Test
	public void testDisable() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetErrorHandler() {
		fail("Not yet implemented");
	}*/

}
