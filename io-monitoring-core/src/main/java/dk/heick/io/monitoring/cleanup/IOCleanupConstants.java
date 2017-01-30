package dk.heick.io.monitoring.cleanup;

import dk.heick.io.monitoring.utils.TimeSpan;

/**
 * Cleanup constants
 * @author Frederik Heick
 */
public interface IOCleanupConstants {
	
	/**
	 * The default value that files will be kept "14 days". 
	 */
	public final static TimeSpan DEFAULT_KEEP_CONTENT_TIME=TimeSpan.createDays(14);
	
	/**
	 * The default time for how long the cleanup process must take every scheduled run. "30 minutes".
	 */
	public final static TimeSpan DEFAULT_MAX_DELETING_PROCESSING_TIME=TimeSpan.createMinutes(30);
	
	/**
	 * The minimum allowed time in millisconds for how long the cleanup process must take every scheduled run. "1 minute".
	 */
	public final static TimeSpan MININUM_MAX_DELETING_PROCESSING_TIME=TimeSpan.createMinutes(1);
	
	/**
	 * The minimum allowed time for in how long the start delay is for running the cleanup process once in a timer. "5 seconds".
	 */
	public final static TimeSpan MININUM_TIMER_START_DELAY=TimeSpan.createSeconds(5);
	
	/**
	 * The minimum allowed time for in how long the between running the cleanup process once in a timer. "5 minutes".
	 */
	public final static TimeSpan MININUM_TIMER_PERIOD=TimeSpan.createMinutes(5);
	
	/**
	 * The default start delay (5 minuttes)
	 */
	public final static TimeSpan DEFAULT_CLEANUP_START_DELAY=TimeSpan.createMinutes(5);
	
	/**
	 * The default cleanup interval (6 hours)
	 */
	public final static TimeSpan DEFAULT_CLEANUP_TIME=TimeSpan.createHours(6);
	
	
	/**
	 * No max depth for recursive folder deletion.
	 */
	public final static int NO_MAX_DEPTH=-1;
	
	

}
