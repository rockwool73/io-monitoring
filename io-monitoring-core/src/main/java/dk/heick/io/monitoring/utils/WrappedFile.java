package dk.heick.io.monitoring.utils;

public interface WrappedFile<T> {
	
	public String getFileName();
	
	public T getFile();

}
