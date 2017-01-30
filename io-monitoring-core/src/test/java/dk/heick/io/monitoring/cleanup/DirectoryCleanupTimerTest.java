package dk.heick.io.monitoring.cleanup;

import static org.junit.Assert.*;

import org.junit.Test;

import dk.heick.io.monitoring.validation.ValidationException;

public class DirectoryCleanupTimerTest {

	@Test
	public void testDirectoryCleanupTimer() {
		DirectoryCleanupTimer t = new DirectoryCleanupTimer();
		assertNotNull(t);
		assertNotNull(t.getTasks());
		assertEquals(0,t.getTasks().size());
		assertNotNull(t.getTimer());
		assertEquals(true,t.isRunning());
		assertEquals(false,t.isStopped());
		t.stop();
	}

	@Test
	public void testAddDirectoryCleanupTaskDirectoryCleanupTask() {
		DirectoryCleanupTimer t = new DirectoryCleanupTimer();
		try {
			t.addDirectoryCleanupTask(null);
			fail();
		} catch (ValidationException e) {		
		} finally {
			t.stop();
		}
	}

	
}
