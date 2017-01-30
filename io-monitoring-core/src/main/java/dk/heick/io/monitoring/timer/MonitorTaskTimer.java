package dk.heick.io.monitoring.timer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import dk.heick.io.monitoring.MonitorTask;
import dk.heick.io.monitoring.TaskTimer;
import dk.heick.io.monitoring.validation.ValidationException;

/**
 * A class that holds a timer for monitoring many different directory using the <tt>DirectoryMonitorTask</tt>.
 * @author Frederik Heick
 * @since 1.0
 */
public class MonitorTaskTimer implements TaskTimer {
	
	/**
	 * The default start delay (1000 ms)
	 */
	public final static long DEFAULT_MONITORING_START_DELAY_MS=1000;
	
	/**
	 * The default monitoring interval (1000 ms)
	 */
	public final static long DEFAULT_MONITORING_TIME_MS=1000;
	
	private Timer timer;
	private boolean running=true;
	private List<MonitorTaskTimerTaskWrapper> tasks = new ArrayList<MonitorTaskTimerTaskWrapper>();

	
	/**
	 * Constructor.
	 */
	public MonitorTaskTimer() {
		super();
		timer = new Timer(true);	
		running=true;
	}
	
	public List<MonitorTask<?,?>> getTasks() {
		List<MonitorTask<?,?>> list = new ArrayList<MonitorTask<?,?>>();
		for (MonitorTaskTimerTaskWrapper task : tasks) {
			list.add(task.getTask());
		} 
		return Collections.unmodifiableList(list);
	}
	/**
	 * Adds new DirectoryMonitoringTimerTask to the MonitorTaskTimerWatcher, using default values.
	 * @param task a new DirectoryMonitoringTask
	 * @see #DEFAULT_MONITORING_START_DELAY_MS
	 * @see #DEFAULT_MONITORING_TIME_MS
	 */
	public void addTimerTask(MonitorTask<?,?> task) throws ValidationException {
		addMonitoringTimerTask(task,DEFAULT_MONITORING_START_DELAY_MS,DEFAULT_MONITORING_TIME_MS);
	}
	public void addMonitoringTask(MonitorTask<?,?> task,long period) throws ValidationException {
		addMonitoringTimerTask(task,DEFAULT_MONITORING_START_DELAY_MS,period);		
	}	
	public void addMonitoringTimerTask(MonitorTask<?,?> task,long startDelay,long period) throws ValidationException {
		task.validate();
		MonitorTaskTimerTaskWrapper wrapper = new MonitorTaskTimerTaskWrapper(task);
		tasks.add(wrapper);
		if (startDelay<10) {
			startDelay = DEFAULT_MONITORING_START_DELAY_MS;
		}
		if (period<10) {
			period = DEFAULT_MONITORING_TIME_MS;
		}
		timer.scheduleAtFixedRate(wrapper, startDelay, period);	
	}

	@Override
	public Timer getTimer() {
		return timer;
	}
	
	/**
	 * Cancels all tasks, important to do in "contextDestroy".
	 */
	@Override
	public void cancelAll() {
		for (MonitorTaskTimerTaskWrapper wrapper : tasks) {
			wrapper.cancel();			
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
	
	
	class MonitorTaskTimerTaskWrapper extends TimerTask {
		private MonitorTask<?,?> task;
		public MonitorTaskTimerTaskWrapper(MonitorTask<?,?> task) {
			this.task=task;
		}
		public MonitorTask<?, ?> getTask() {
			return task;
		}
		@Override
		public void run() {
			System.out.println("Running timer task");
			getTask().runTask();			
		}		
	}

}
