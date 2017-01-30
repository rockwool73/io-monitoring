package dk.heick.io.monitoring;

import java.text.SimpleDateFormat;

import dk.heick.io.monitoring.utils.TimeSpan;

public interface IOMonitoringConstants {
	
	public final static String DIRECTORY_NAME_ARCHIVE=".archive";
	public final static String DIRECTORY_NAME_ERROR=".error";
	public final static String DIRECTORY_NAME_PROCESS=".process";
	public final static String FILE_DATE_FORMAT="yyyyMMdd_HHmmssSSS";
	public final static String SUBDIRECTORY_DATE_FORMAT="yyyy-MM-dd";
	public final static String LOCK_FILE_EXTENSION=".lock";
	public final static String ERROR_FILE_EXTENSION=".errorlog";
	//
	public final static SimpleDateFormat FILE_DATE_FORMATTER = new SimpleDateFormat(FILE_DATE_FORMAT);
	public final static SimpleDateFormat SUB_DIRECTORY_DATE_FORMATTER = new SimpleDateFormat(SUBDIRECTORY_DATE_FORMAT);	
	//
	public final static TimeSpan DEFAULT_STABLE_TIME=TimeSpan.createMilliSeconds(1500);
	public final static boolean DEFAULT_ARCHIVING=true;
	public final static int DEFAULT_MAX_MESSAGE_POLLING=1000;
	public final static TimeSpan DEFAULT_MAX_PROCESSING_TIME= TimeSpan.createMinutes(5);
	public final static TimeSpan DEFAULT_MONITOR_TIMEOUT=TimeSpan.createHours(1);
	public final static TimeSpan DEFAULT_LOCK_FILE_TIMEOUT=TimeSpan.createMinutes(20);	
	public final static TimeSpan DEFAULT_MAX_DELETING_PROCESSING_TIME=TimeSpan.createSeconds(90);
	//
	public final static TimeSpan MIN_STABLE_TIME=TimeSpan.createMilliSeconds(100);
	public final static TimeSpan MIN_MONITOR_TIMEOUT=TimeSpan.createMinutes(1);
	public final static TimeSpan MIN_LOCK_FILE_TIMEOUT=TimeSpan.createMinutes(5);
}
