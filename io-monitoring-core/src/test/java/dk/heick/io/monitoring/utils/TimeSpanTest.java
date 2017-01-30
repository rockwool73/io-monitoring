package dk.heick.io.monitoring.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

public class TimeSpanTest {

	@Test
	public void testCreateHours() {
		TimeSpan kct = TimeSpan.createHours(6);
		assertEquals(6, kct.getFactor(),0.01);
		assertEquals(TimeUnit.HOURS, kct.getTimeUnit());
		assertEquals(TimeUnit.HOURS.toMillis(6), kct.getTime());
	}

	@Test
	public void testCreateDays() {
		TimeSpan kct = TimeSpan.createDays(6);
		assertEquals(6, kct.getFactor(),0.01);
		assertEquals(TimeUnit.DAYS, kct.getTimeUnit());
		assertEquals(TimeUnit.DAYS.toMillis(6), kct.getTime());
	}

	@Test
	public void testTimeSpan() {
		try {
			new TimeSpan(0,TimeUnit.DAYS);
			fail();
		} catch (IllegalArgumentException e) {			
		}
		try {
			new TimeSpan(1,null);
			fail();
		} catch (NullPointerException e) {			
		}
	}


}
