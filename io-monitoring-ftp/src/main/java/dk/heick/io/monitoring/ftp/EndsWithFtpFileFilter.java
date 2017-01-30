package dk.heick.io.monitoring.ftp;

import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileFilter;

public class EndsWithFtpFileFilter implements FTPFileFilter {
	
	private String extension;
	
	public EndsWithFtpFileFilter(String extension) {
		this.extension=extension;
	}
	public String getExtension() {
		return extension;
	}
	@Override
	public boolean accept(FTPFile file) {
		return file.isFile() && file.getName().toLowerCase().endsWith(getExtension().toLowerCase());
	}

}
