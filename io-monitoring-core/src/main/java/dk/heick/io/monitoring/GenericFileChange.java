package dk.heick.io.monitoring;

/**
 * Abstract class that holds file infomations.
 * @author Frederik Heick
 * @param <T> the generic file type
 * @version 1.0
 */
public abstract class GenericFileChange<T> {
	
	private T file;
	private long created;
	private long modified;
	private long size;
	private long lastChecked;
	private boolean locked;
	
	/**
	 * Constructor
	 * @param file the file instance
	 * @throws NullPointerException if <tt>file</tt> is <code>null</code>.
	 */
	public GenericFileChange(T file) throws NullPointerException {
		super();
		this.file=file;
		this.created = System.currentTimeMillis();
		this.lastChecked = System.currentTimeMillis();
		init(file);
		
	}
	/**
	 * Constructor.
	 * @param previous the previous <tt>GenericFileChange</tt> instance.
	 * @throws NullPointerException if <tt>file</tt> is <code>null</code>.
	 */
	public GenericFileChange(GenericFileChange<T> previous) throws NullPointerException {
		this(previous.getFile());
		this.created = previous.getCreated();		
		if (isStable(previous)) {		
			this.lastChecked = previous.getLastChecked();
		} else {			
			this.lastChecked = System.currentTimeMillis();
						
		}			
	}
	
	/**
	 * Evaluates if the file is stable.
	 * @param previous a previous instance of FileChange
	 * @param stableTime how long the file has to be stable before being deemed to be stable.
	 * @return <code>true</code> if "lastModified" is the same, if "filesize" is the and the time between the to sets of values is greater than "stableTime" milliseconds, otherwise <code>false</code>.
	 */
	public final boolean isStable(GenericFileChange<T> previous,long stableTime) {
		boolean b0 = isStable(previous);		
		long diff = System.currentTimeMillis()-previous.getLastChecked();				
		boolean b1 = (diff>stableTime);		
		return b0 && b1;
	}
		
	/**
	 * Determines if a file is stable. <br>
	 * The rules is:
	 * <ul>
	 * 		<li>The file exists.</li>
	 *      <li>The modified timestamp is the same as the previous modified timestamp. Last time the file was modified.</li>
	 *      <li>The file size is the same as the previous file size.</li>
	 *      <li>The file is not locked..</li>
	 * </ul>
	 * @param previous the previous instance to compare too for stability, if <code>null</code> it always returns <code>false</code>.
	 * @return <code>true</code> if the file is stable (is above), otherwise <code>false</code>.
	 * @see #existsFile()
	 * @see #getModified()
	 * @see #getSize()
	 * @see #isLocked()
	 */
	public final boolean isStable(GenericFileChange<T> previous) {
		if (previous!=null) {
			boolean b0 = existsFile();
			boolean b1 = (getModified()==previous.getModified());
			boolean b2 = (getSize()==previous.getSize());
			boolean b3 = !isLocked();					
			return b0 && b1 && b2 && b3;
		} else {
			return false;
		}
	}	
	
	
	/**
	 * Initialize the values:
	 * <ul>
	 * 		<li>modified - the last time the file was modified.</li>
	 * 		<li>size - the file size in bytes.</li>
	 * 		<li>locked - if the file is locked.</li>
	 * </ul>
	 * @param file the file instance.
	 * @see #setModified(long)
	 * @see #setSize(long)
	 * @see #setLocked(boolean)
	 */
	protected abstract void init(T file);
		
	/**
	 * Validates if the file exists.
	 * @return <code>true</code> if the file exists, otherwise <code>false</code>.
	 */
	protected abstract boolean existsFile();
	
	/**
	 * Get the complete name of the file as a String.
	 * @return the file name.
	 */
	public abstract String getFileName();
	
	/**
	 * Set the timestamp for when the file was last modified.
	 * @param modified the file last modified timestamp
	 */
	protected final void setModified(long modified) {
		this.modified = modified;
	}	
	
	/**
	 * Sets the file size in bytes.
	 * @param size the files size.
	 */
	protected final void setSize(long size) {
		this.size = size;
	}	
	
	/**
	 * Sets if the file is locked or not.
	 * @param locked is the file locked.
	 */
	protected final void setLocked(boolean locked) {
		this.locked = locked;
	}
	
	/**
	 * The file.
	 * @return the file.
	 */
	public final T getFile() {
		return file;
	}	
	
	/**
	 * When the file monitoring was initial started.
	 * @return file monitoring created timestamp.
	 */
	public final long getCreated() {
		return created;
	}	
	
	/**
	 * The file modified timestamp.
	 * @return The file modified timestamp.
	 */
	public final long getModified() {
		return modified;
	}	
	
	/**
	 * Is the file locked.
	 * @return <code>true</code> if the file is locked, otherwise <code>false</code>.
	 */
	public boolean isLocked() {
		return locked;
	}	
	
	/**
	 * The timestamp on when the the file was last checked
	 * @return last checked timestamp.
	 */
	public final long getLastChecked() {
		return lastChecked;
	}	
	
	/**
	 * The size of the file in bytes
	 * @return file byte size.
	 */
	public final long getSize() {
		return size;
	}
	

	
	/**
	 * The amount of time in milliseconds from when the file was started to be monitored until now.
	 * @return the monitoring time.
	 */
	public final long getMonitoringTime() {
		return Math.abs(getLastChecked() - getCreated());
	}
	
	@Override
	public String toString() {
		return getClass().getName()+" [file=" + file.toString()
				+ ", created=" + created 
				+ ", lastModified=" + modified 
				+ ", size=" + size
				+ ", locked=" + locked 
				+ ", lastCheck=" + lastChecked + "]";
	}

	@Override
	public int hashCode() {
		return getFile().hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj==null) {
			return false;
		} else if (obj instanceof GenericFileChange) {
			@SuppressWarnings("unchecked")
			GenericFileChange<T> other = (GenericFileChange<T>)obj;
			return getFile().equals(other.getFile());
		} else {
			return false;
		}
	}	
}
