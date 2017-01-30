package dk.heick.io.monitoring.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

public class FileCreationThread extends Thread {
	
	private static final String S64="abcdefghijklmnopqrstuvwxyzæøå1234567890abcdefghijklmnopqrstuvwx";

	private File directory;
	private int numberOfFiles;
	private long fileSize;
	private long timeToCreate;
	private int sleepCount;	
	private boolean obtainLock;
	private String suffix;
	private String extension;

	public FileCreationThread(String name,File directory, int numberOfFiles, long fileSize, long timeToCreate,int sleepCount, boolean obtainLock, String suffix, String extension) {
		super(name);
		this.directory=directory;
		this.numberOfFiles = numberOfFiles;
		this.fileSize = fileSize;
		this.timeToCreate = timeToCreate;
		this.sleepCount = sleepCount;
		this.obtainLock = obtainLock;
		this.suffix = suffix;
		this.extension = extension;
	}
	public File getDirectory() {
		return directory;
	}
	public int getNumberOfFiles() {
		return numberOfFiles;
	}
	public long getFileSize() {
		return fileSize;
	}
	public long getTimeToCreate() {
		return timeToCreate;
	}
	public int getSleepCount() {
		return sleepCount;
	}
	public boolean isObtainLock() {
		return obtainLock;
	}
	public String getSuffix() {
		return suffix;
	}
	public String getExtension() {
		return extension;
	}
	
	public void run() {		
		for (int i=0;i<getNumberOfFiles();i++) {
			createFile(i);
			isleep(150);
		}
	}
	
	

	
	private void createFile(int index) {
		File file = null;
		try {
			long start = System.currentTimeMillis();
			if (isObtainLock()) {
				file = createFileObtainLock(index);
			} else {
				file = createFileNotObtainLock(index);
			}
			System.out.println("File create ["+file.getName()+"] size ["+file.length()+"] Time ["+(System.currentTimeMillis()-start)+"]");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private File createFileObtainLock(int index) throws Exception {		
		File file = getFile(index);
        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
        FileChannel fileChannel = randomAccessFile.getChannel();
        FileLock lock = null;
        try {		            
            fileChannel.tryLock();            
            long sleepTime = (getTimeToCreate()/getSleepCount())-10L;
			long bytes = getFileSize() / getSleepCount();
			for (int i=0;i<getSleepCount();i++) {
				randomAccessFile.write(createFileData(bytes).getBytes());
				isleep(sleepTime);
			}			
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
        }
        return file;	
	}
	
	
	private File createFileNotObtainLock(int index) throws Exception {
		File file = getFile(index);
		FileOutputStream out = new FileOutputStream(file);
		try {			
			long sleepTime = (getTimeToCreate()/getSleepCount())-10L;		
			long bytes = getFileSize() / getSleepCount();
			for (int i=0;i<getSleepCount();i++) {
				out.write(createFileData(bytes).getBytes());
				isleep(sleepTime);
			}
		} finally {
			out.flush();
			out.close();
		}
		return file;
	}
	
	private String createFileData(long bytes) {
		StringBuilder s = new StringBuilder();
		for (long i=0;i<(bytes/64);i++) {
			s.append(S64);
		}
		s.append(S64);
		return s.toString();
	}
	private void isleep(long ms) {
		try {
			sleep(ms);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}	

	private File getFile(int index) {
		return new File(getDirectory(),getName()+"_"+getSuffix()+"_"+index+"_"+getFileSize()+"."+getExtension());
	}

}
