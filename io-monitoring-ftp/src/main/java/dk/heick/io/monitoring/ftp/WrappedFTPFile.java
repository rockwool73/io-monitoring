package dk.heick.io.monitoring.ftp;

import org.apache.commons.net.ftp.FTPFile;

import dk.heick.io.monitoring.utils.WrappedFile;

public class WrappedFTPFile implements WrappedFile<FTPFile>{
	
	private FTPFile file;

	public WrappedFTPFile(FTPFile file) {
		super();
		this.file=file;
	}
	@Override
	public FTPFile getFile() {
		return file;
	}
	@Override
	public String getFileName() {
		return getFile().getName();
	}
	@Override
	public boolean equals(Object obj) {
		if (obj==null) {
			return false;
		} else if (obj instanceof WrappedFTPFile) {
			WrappedFTPFile other = (WrappedFTPFile)obj;			
			return getFile().getName().equals(other.getFile().getName());
		} else {
			return false;
		}		
	}
	
	@Override
	public int hashCode() {
		return getFile().getName().hashCode();
	}
	
	@Override
	public String toString() {
		return getFile().getName();
	}

}
