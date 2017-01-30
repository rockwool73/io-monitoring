package dk.heick.io.monitoring.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import dk.heick.io.monitoring.IOMonitoringConstants;
import dk.heick.io.monitoring.validation.ValidateUtils;
import dk.heick.io.monitoring.validation.ValidationException;

/**
 * Different utilities for files.
 * @author Frederik Heick
 * @since 1.0
 */
public class FileUtils {
	
	public static final String getFilePath(File file) {
		if (file!=null) {
			return file.getAbsolutePath();
		} else {
			return "[file is null]";
		}
	}
	
	public static final File getCurrentDirectory() {
		return Paths.get("").toAbsolutePath().toFile();
	}
	
	public static final void createDirectory(File directory) throws IOException {
		if (directory==null) {
			throw new IOException("Directory is null.");
		} else if (!directory.exists()) {
			directory.mkdirs();
			if (!directory.exists()) {
				throw new IOException("Directory ["+directory.getAbsolutePath()+"] could not be created.");
			}
		}
	}
	
	public static final void renameDirectory(File directory,String toDirectoryName) throws IOException {
		if (directory==null) {
			throw new IOException("Directory is null.");
		} else if (!directory.exists()) {
			throw new IOException("Directory ["+directory.getAbsolutePath()+"] do not exists.");
		} else if (!directory.isDirectory()) {
			throw new IOException("Directory ["+directory.getAbsolutePath()+"] is not a directory.");
		} else {
			try {
				ValidateUtils.validateNotEmpty("toDirectoryName", toDirectoryName);
				File destination = new File(directory.getParentFile(),toDirectoryName);
				renameDirectory(directory,destination);
			} catch (ValidationException e) {
				throw new IOException(e.getMessage(),e);
			}			
		}					
	}
	
	public static final void renameDirectory(File sourceDirectory,File destinationDirectory) throws IOException {
		if (sourceDirectory==null) {
			throw new IOException("Source Directory is null.");
		} else if (!sourceDirectory.exists()) {
			throw new IOException("Source Directory ["+sourceDirectory.getAbsolutePath()+"] do not exists.");
		} else if (!sourceDirectory.isDirectory()) {
			throw new IOException("Source Directory ["+sourceDirectory.getAbsolutePath()+"] is not a directory.");
		} else if (destinationDirectory==null) {
			throw new IOException("Destination Directory is null.");
		} else if (destinationDirectory.exists()) {
			throw new IOException("Destination Directory ["+destinationDirectory.getAbsolutePath()+"] already exists.");
		} else {
			renameFileGCAssist(sourceDirectory, destinationDirectory);		
		}			
	}
	
	public static final void rename(File source,File destination) throws IOException {
		if (source==null) {
			throw new IOException("Source is null.");
		} else if (!source.exists()) {
			throw new IOException("Source ["+source.getAbsolutePath()+"] do not exists.");
		} else if (destination==null) {
			throw new IOException("Destination is null.");
		} else if (destination.exists()) {
			throw new IOException("Destination ["+destination.getAbsolutePath()+"] already exists.");
		} else {
			renameFileGCAssist(source, destination);		
		}	
		
	}
	
	public static final void deleteDirectoryOnlyFiles(File directory) throws IOException {
		if (directory==null) {
			throw new IOException("Directory is null.");
		} else if (!directory.exists()) {
			throw new IOException("Directory ["+directory.getAbsolutePath()+"] do not exists.");
		} else if (!directory.isDirectory()) {
			throw new IOException("Directory ["+directory.getAbsolutePath()+"] is not a directory.");
		} else {			
			for (File file : directory.listFiles()) {
				if (file.isFile()) {
					deleteFileGCAssist(file);
				}
			}			
		}
	}
	
	public static final void deleteDirectoryOnlySubDirectories(File directory) throws IOException {
		if (directory==null) {
			throw new IOException("Directory is null.");
		} else if (!directory.exists()) {
			throw new IOException("Directory ["+directory.getAbsolutePath()+"] do not exists.");
		} else if (!directory.isDirectory()) {
			throw new IOException("Directory ["+directory.getAbsolutePath()+"] is not a directory.");
		} else {			
			for (File file : directory.listFiles()) {
				if (file.isDirectory()) {					
					deleteDirectory(file,true);		
					deleteFileGCAssist(file);
				} 
			}			
		}
	}	
	
	public static final void deleteDirectory(File directory,boolean deleteSubDirectories) throws IOException {
		if (directory==null) {
			throw new IOException("Directory is null.");
		} else if (!directory.exists()) {
			throw new IOException("Directory ["+directory.getAbsolutePath()+"] do not exists.");
		} else if (!directory.isDirectory()) {
			throw new IOException("Directory ["+directory.getAbsolutePath()+"] is not a directory.");
		} else {			
			for (File file : directory.listFiles()) {				
				if ((file.isDirectory()) && (file.exists())) {
					if (deleteSubDirectories) {
						deleteDirectory(file,deleteSubDirectories);
					} else {
						throw new IOException("Will not delete sub directory ["+file.getAbsolutePath()+"]. Please enable this feature if needed.");
					} 				
					if ((file.exists()) && (!file.delete())) {
						throw new IOException("Can not delete directory ["+file.getAbsolutePath()+"]. Should be empty ["+file.listFiles().length+"].");
					}
				} else {
					deleteFile(file);
				}
			}		
			if ((directory.exists()) && (directory.list().length==0)) {
				if (!directory.delete()) {
					throw new IOException("Can not delete directory ["+directory.getAbsolutePath()+"]. Should be empty ["+directory.listFiles().length+"].");
				}
			}
		}
	}
	
	public static final void deleteFile(File file) throws IOException {
		if (file==null) {
			throw new IOException("File is null.");
		} else if (!file.exists()) {
			throw new IOException("File ["+file.getAbsolutePath()+"] do not exists.");
		} else if (!file.isFile()) {
			throw new IOException("File ["+file.getAbsolutePath()+"] is not a file.");
		} else {	
			deleteFileGCAssist(file);
		}
	}
	
	private static final void deleteFileGCAssist(File file) throws IOException {
		if (!file.exists()) {
			return;
		} else {
			final int RETRY=3;
			for( int i=0;i<RETRY;i++) {
				try {
					if (file.delete()) {
						break;
					} else {
						try {
							Thread.sleep(50);
						} catch (InterruptedException e) {						
						}
						System.gc();
						Thread.yield();	
					}
				} catch (SecurityException e) {
					throw new IOException(e);
				}
			}
			if (file.exists()) {
				throw new IOException("Can not delete ["+file.getAbsolutePath()+"].");
			}
		}
	}
	
	private static final void renameFileGCAssist(File source,File destination) throws IOException {
		if (!source.exists()) {
			throw new IOException("Source ["+source.getAbsolutePath()+"] do not exists.");
		} else if (destination.exists()) {
			throw new IOException("Destination ["+destination.getAbsolutePath()+"] already exists.");
		} else {
			for( int i=0;i<3;i++) {
				if (source.renameTo(destination)) {
					break;
				} else {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {						
					}
					System.gc();
					Thread.yield();	
				}
			}
			if (source.exists()) {
				throw new IOException("Source still exists, can not rename ["+source.getAbsolutePath()+"].");
			} else if (!destination.exists()) {				
				throw new IOException("Destination do not exists, can not rename ["+destination.getAbsolutePath()+"].");
			}
		}
	}	
	
	/**
	 * Tests if a file is <b>LOCKED</b> by another process or thread. <br/>
	 * This is not a trivial thing in Java (for some reason), so we have to go through several hoops to tests this.<br/> 
	 * The test has these parts
	 * <ul>
	 * 		<li>If file do not exists <code>false</code> is returned.</li>
	 * 		<li>Creating an <tt>RandomAccessFile</tt> with "read write" access.</li>
	 * 		<li>Trying to obtain a "FileLock", is success return <code>true</code> otherwise <code>false</code>.</li>
	 * 		<li><tt>RandomAccessFile</tt> can throw a "FileNotFoundException" if that contains the message 
	 * 			"(The process cannot access the file because it is being used by another process)"; <code>true</code> is returned otherwise <code>false</code>.</li>
	 * </ul>
	 * @param file a file
	 * @return <code>true</code> is the file is <b>LOCKED</b>, otherwise <code>false</code>.
	 * @see #isFileUnlocked(File)
	 */
	public static final boolean isFileLocked(File file) {
		if (file.exists()) {
			//long lastModified = file.lastModified();
	    	try {
	    		
		        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
		        FileChannel fileChannel = randomAccessFile.getChannel();
		        FileLock lock = null;
		        try {		            
		            lock = fileChannel.tryLock();
		            return false;
		        } catch (Exception ex) {
		        	return true;
		        } finally {
		            if (lock != null) {
		                lock.release();
		            }		 
		            if (fileChannel != null) {
		                fileChannel.close();
		            }		 
		            if (randomAccessFile != null) {
		            	randomAccessFile.close();
		            }
		           // file.setLastModified(lastModified);
		        }
	    	} catch (FileNotFoundException e) {
	    		if (e.getMessage().contains("used by another process")) {
	    			return true;
	    		} else {
	    			return false;
	    		}
	    	} catch (IOException e) {	    		
	    		return false;
	    	}
	    } else {
	    	System.err.println("File do not exists ["+file.getAbsolutePath()+"]");
	    	return false;
	    }	
	}
	
	/**
	 * Tests if a file is <b>NOT LOCKED</b> by another process or thread. <br/>
	 * This is not a trivial thing in Java (for some reason), so we have to go through several hoops to tests this.<br/> 
	 * The negated boolean of "<tt>isFileLocked(File)</tt>". (See this method for details)
	 * @param file a file
	 * @return <code>true</code> is the file is <b>NOT LOCKED</b>, otherwise <code>false</code>.
	 * @see #isFileLocked(File)
	 */
	public static final boolean isFileUnlocked(File file) {
		return (!isFileLocked(file));		     
    }
	
	/**
	 * Loads a file into a string using the InputStreamReader with a UTF_8 Charset and a default buffersize of 2048. 
	 * @param file the file to read
	 * @return the File as String, with the valid encoding.
	 * @throws IOException if <tt>File</tt> is not found or unreadable, or the <tt>charset</tt> is invalid.
	 * @see #loadAsString(File, int, String)
	 */
	public static final String loadAsString(File file) throws IOException {
		return loadAsString(file,2048,StandardCharsets.UTF_8); 
	}
	

	/**
	 * Loads a file into a string using the InputStreamReader with a charset and a default buffersize of 2048.
	 * @param file the file to read
	 * @param charset a valid charset for the encoding.
	 * @return the File as String, with the valid encoding.
	 * @throws IOException if <tt>File</tt> is not found or unreadable, or the <tt>charset</tt> is invalid.
	 * @see #loadAsString(File, int, String)
	 * @see StandardCharsets
	 */
	public static final String loadAsString(File file,Charset charset) throws IOException {
		return loadAsString(file,2048,charset); 
	}
	
	/**
	 * Loads a file into a string using the InputStreamReader with a charset and buffersize.
	 * @param file the file to read. 
	 * @param bufferSize the buffer size to load the file with using the <tt>InputStreamReader.read(byte[],int,int)</tt> method, if <tt>bufferSize</tt> is less than 1, 2048 is used.
	 * @param charset a valid charset for the encoding.
	 * @return the File as String, with the valid encoding.
	 * @throws IOException if <tt>File</tt> is not found or unreadable, or the <tt>charset</tt> is invalid or if <tt>charsetName</tt> is <code>null</code> or empty..
	 * @see StandardCharsets
	 */
	public static final String loadAsString(File file, int bufferSize,Charset charset) throws IOException {
		try {
			ValidateUtils.validateNotNull("File", file);
			ValidateUtils.validateNotNull("Chartset", charset);				
			if (bufferSize<1) {
				bufferSize=2048;
			} else if (charset==null) {
				throw new IOException("Charset is null.");
			}
			InputStreamReader in = null;
			FileInputStream fis = null;
			final char[] buffer = new char[bufferSize];
			final StringBuilder out = new StringBuilder();
			
			fis = new FileInputStream(file);
			in = new InputStreamReader(fis,charset);
			try {
				for (;;) {
					int rsz = in.read(buffer, 0, buffer.length);
					if (rsz < 0) {
						break;
					}
					out.append(buffer, 0, rsz);
				}
			} finally {
				in.close();
			}
			return out.toString();
		} catch (ValidationException e1) {
			throw new IOException(e1.getMessage(),e1);		
		} catch (UnsupportedEncodingException e) {
			throw new IOException(e.getMessage(),e);
		} catch (IOException e) {
			throw new IOException(e.getMessage(),e);
		}
		
	}
	
	
	/**
	 * Load a file to a list of Strings, using the <tt>Files.readAllLines(File,Charset)</tt> and the <tt>Charset.defaultCharset()</tt>.	
	 * @param file the file
	 * @return the list of strings in the file.
	 * @throws IOException if the file could not be loaded.
	 */
	public static final StringList loadAsLines(File file) throws IOException {
		try {			
			return new StringList(Files.readAllLines(file.toPath(),Charset.defaultCharset()));
		} catch (IOException e) {
			throw new IOException(e.getMessage(),e);
		} catch (NullPointerException e) {			
			throw new IOException(e.getMessage(),e);
		}
	}	
	
	/**
	 * Sets the a file last modified timestamp to now.
	 * @param file the file to touch.
	 * @throws IOException if the <tt>file</tt> is <code>null</code>, or any SecurityException is thrown.
	 * @see File#setLastModified(long)
	 * @see #setFileLastModified(File, long)
	 */
    public static final void touch(File file) throws IOException {
        setFileLastModified(file,System.currentTimeMillis());
    }
    
    /**
     * Calls File.setLastModified(long time). 
     * @param file the file whose modified time is to be set, if less than zero, current time milliseconds is used.
     * @param time the time to which the last modified time is to be set. If this is -1, the current time is used.
     * @throws IOException if <tt>file</tt> is <code>null</code> or unable to set <tt>setLastModified</tt> on the file.
     * @see File#setLastModified(long)
     */
    public static final void setFileLastModified(File file, long time) throws IOException {
    	if (file==null) {
    		throw new IOException("File is null.");
    	} else if (time<0) {
    		throw new IOException("Time for lastModified can not be negative.");
    	} else { 
      		try {
    			file.setLastModified(time);
    		} catch (IllegalArgumentException e) {
    			throw new IOException(e.getMessage(),e);
    		} catch (SecurityException e) {
    			throw new IOException(e.getMessage(),e);
    		}
    	}
    }
    
    /**
     * TODO
     * @param file
     * @return
     * @throws NullPointerException
     */
    public final static String getExtension(File file) throws NullPointerException {
    	if (file==null) {
    		throw new NullPointerException("File is null.");
    	} else if (file.isDirectory()) {
    		return "dir";
    	} else {
    		int pos = file.getName().lastIndexOf('.');
    		if (pos>-1) {
    			return file.getName().substring(pos+1).trim();
    		} else {
    			return "unknown";
    		}
    	}
    }
    
    /**
     * TODO 
     * @param file
     * @return
     */
	public static final boolean isLockFile(File file) {
		if (file!=null) {
			String ext = "."+getExtension(file);			
			return (ext.equalsIgnoreCase(IOMonitoringConstants.LOCK_FILE_EXTENSION));
		} else {
			return false;
		}		
	}
}
