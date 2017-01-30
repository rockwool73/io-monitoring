package dk.heick.io.monitoring.utils;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * A class which instance holds a time span.
 * @author Frederik Heick
 * @version 1.0
 */
public class TimeSpan implements Comparable<TimeSpan> {
	
	private double factor;
	private TimeUnit timeUnit;
	private long time;
	
	/**
	 * Creates a time span with a number of milliseconds.
	 * @param seconds the number of milliseconds, has to greater than 0.
	 * @return a TimeSpan instance with the number of milliseconds.
	 * @throws IllegalArgumentException if <tt>milliseconds</tt> is less than <code>1</code>.
	 */
	public final static TimeSpan createMilliSeconds(long milliseconds) throws IllegalArgumentException,NullPointerException {
		return new TimeSpan(milliseconds, TimeUnit.MILLISECONDS);
	}
	
	/**
	 * Creates a time span with a number of seconds.
	 * @param seconds the number of seconds, has to greater than 0.
	 * @return a TimeSpan instance with the number of seconds.
	 * @throws IllegalArgumentException if <tt>seconds</tt> is less than <code>1</code>.
	 */
	public final static TimeSpan createSeconds(int seconds) throws IllegalArgumentException {
		return new TimeSpan(seconds, TimeUnit.SECONDS);
	}
	
	/**
	 * Creates a time span with a number of minutes.
	 * @param seconds the number of seconds, has to greater than 0.
	 * @return a TimeSpan instance with the number of minutes.
	 * @throws IllegalArgumentException if <tt>minutes</tt> is less than <code>1</code>.
	 */
	public final static TimeSpan createMinutes(int minutes) throws IllegalArgumentException {
		return new TimeSpan(minutes, TimeUnit.MINUTES);
	}	
	
	/**
	 * Creates a time span with a number of hours.
	 * @param seconds the number of hours, has to greater than 0.
	 * @return a TimeSpan instance with the number of hours.
	 * @throws IllegalArgumentException if <tt>hours</tt> is less than <code>1</code>.
	 */
	public final static TimeSpan createHours(double hours) throws IllegalArgumentException,NullPointerException {
		return new TimeSpan(hours, TimeUnit.HOURS);
	}
	
	/**
	 * Creates a time span with a number of days.
	 * @param seconds the number of days, has to greater than 0.
	 * @return a TimeSpan instance with the number of days.
	 * @throws IllegalArgumentException if <tt>days</tt> is less than <code>1</code>.
	 */
	public final static TimeSpan createDays(double days) throws IllegalArgumentException,NullPointerException {
		return new TimeSpan(days, TimeUnit.DAYS);
	}
		
	/**
	 * Creates a time span instance.
	 * @param factor the factor which the time unit is multiplied.
	 * @param timeUnit the unit
	 * @throws IllegalArgumentException if <tt>factor</tt> is less than <code>1</code>.
	 * @throws NullPointerException if <tt>timeUnit</tt> is <code>null</code>.
	 */
	public TimeSpan(double factor,TimeUnit timeUnit) throws IllegalArgumentException,NullPointerException {
		super();
		if (factor<1) {
			throw new IllegalArgumentException("TimeSpan [factor] must be greather than zero.");
		} else  if (timeUnit==null) {
			throw new NullPointerException("TimeSpan [TimeUnit] is null.");
		} else {
			this.factor=factor;
			this.timeUnit=timeUnit;
			this.time = Double.valueOf(timeUnit.toMillis(1)*factor).longValue();
		}		
	}
	
	/**
	 * Constructor, 
	 * @param milliSeconds
	 * @throws IllegalArgumentException
	 * @throws NullPointerException
	 */
	public TimeSpan(long milliSeconds) throws IllegalArgumentException,NullPointerException {
		this(milliSeconds,TimeUnit.MILLISECONDS);
	}
	
	/**
	 * The factor which the time unit is multiplied.
	 * @return the factor
	 */
	public double getFactor() {
		return factor;
	}
	
	/**
	 * The time unit.
	 * @return the unit.
	 */
	public TimeUnit getTimeUnit() {
		return timeUnit;
	}

	/**
	 * The time span in milliseconds.
	 * @return the factor multiplied with the time unit (in milliseconds)
	 */
	public long getTime() {
		return time;
	}
	
	@Override
	public int compareTo(TimeSpan o) {
		if (getTime()<o.getTime()) {
			return -1;
		} else if (getTime()>o.getTime()) {
			return 1;
		} else {
			return 0;
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj==null) {
			return false;
		} else if (obj instanceof TimeSpan) {
			TimeSpan other = (TimeSpan)obj;
			return getTime()==other.getTime();
		} else {
			return false;
		}	
	}
	
	public boolean isLessThan(TimeSpan other) throws NullPointerException {
		return compareTo(other)<0;
	}
	public boolean isGreaterThan(TimeSpan other) throws NullPointerException {
		return compareTo(other)>0;
	}
	
	
	@Override
	public int hashCode() {
		return Objects.hash(getFactor(),getTimeUnit());
	}

	@Override
	public String toString() {
		return "TimeSpan [factor=" + factor + ", timeUnit=" + timeUnit + ", time=" + time + "]";
	}
	
	
	
}
