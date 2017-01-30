package dk.heick.io.monitoring.cleanup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import dk.heick.io.monitoring.TaskTimer;
import dk.heick.io.monitoring.utils.TimeSpan;
import dk.heick.io.monitoring.validation.ValidateUtils;
import dk.heick.io.monitoring.validation.ValidationException;

public class DirectoryCleanupTimer implements TaskTimer {
	
	private Timer timer;
	private boolean running=true;
	private List<DirectoryCleanupTaskWrapper> tasks = new ArrayList<DirectoryCleanupTaskWrapper>();

	/**
	 * Constructor.
	 */
	public DirectoryCleanupTimer() {
		super();
		timer = new Timer(true);
		running=true;
	}
	public List<DirectoryCleanupTask> getTasks() {
		List<DirectoryCleanupTask> list = new ArrayList<DirectoryCleanupTask>();
		for (DirectoryCleanupTaskWrapper task : tasks) {
			list.add(task.getTask());
		} 
		return Collections.unmodifiableList(list);		
	}
	
	@Override
	public Timer getTimer() {
		return timer;
	}
	
	/**
	 * Cancels all tasks, important to do in "contextDestroy".
	 */
	public void cancelAll() {
		for (DirectoryCleanupTaskWrapper task : tasks) {
			task.cancel();
		}			
	}
	
	@Override
	public void stop() {
		cancelAll();
		getTimer().cancel();
		getTimer().purge();
		running=false;
	}
	@Override
	public boolean isRunning() {		
		return running;
	}
	@Override
	public boolean isStopped() {
		return !isRunning();
	}
	
	/**
	 * Adds new DirectoryMonitoringTimerTask to the MonitorTaskTimerWatcher, using default values.
	 * @param task a new DirectoryMonitoringTask
	 * @see #DEFAULT_MONITORING_START_DELAY_MS
	 * @see #DEFAULT_MONITORING_TIME_MS
	 */
	public void addDirectoryCleanupTask(DirectoryCleanupTask task) throws ValidationException {
		addDirectoryCleanupTask(task,IOCleanupConstants.DEFAULT_CLEANUP_START_DELAY,IOCleanupConstants.DEFAULT_CLEANUP_TIME);
	}
	public void addDirectoryCleanupTask(DirectoryCleanupTask task,TimeSpan period) throws ValidationException {
		addDirectoryCleanupTask(task,IOCleanupConstants.DEFAULT_CLEANUP_START_DELAY,period);		
	}	
	public void addDirectoryCleanupTask(DirectoryCleanupTask task,TimeSpan startDelay,TimeSpan period) throws ValidationException {
		ValidateUtils.validateNotNull("DirectoryCleanupTask", task);
		ValidateUtils.validateNotNull("StartDelay", startDelay);
		ValidateUtils.validateNotNull("Period", period);
		//
		DirectoryCleanupTaskWrapper wrapper = new DirectoryCleanupTaskWrapper(task);
		tasks.add(wrapper);
		if (startDelay.getTime()<IOCleanupConstants.MININUM_TIMER_START_DELAY.getTime()) {
			startDelay = IOCleanupConstants.DEFAULT_CLEANUP_START_DELAY;
		}
		if (period.getTime()<IOCleanupConstants.MININUM_TIMER_PERIOD.getTime()) {
			period = IOCleanupConstants.DEFAULT_CLEANUP_TIME;
		}
		timer.scheduleAtFixedRate(wrapper, startDelay.getTime(), period.getTime());	
	}
	
	class DirectoryCleanupTaskWrapper extends TimerTask {
		private DirectoryCleanupTask task;
		public DirectoryCleanupTaskWrapper(DirectoryCleanupTask task) {
			this.task=task;
		}
		public DirectoryCleanupTask getTask() {
			return task;
		}
		@Override
		public void run() {
			getTask().runTask();			
		}		
	}	
}
