package dk.heick.io.monitoring;

import java.util.Timer;

public interface TaskTimer {
	
	public Timer getTimer();
	
	public void cancelAll();
		
	public void stop();
	
	public boolean isStopped();
	
	public boolean isRunning();

}
