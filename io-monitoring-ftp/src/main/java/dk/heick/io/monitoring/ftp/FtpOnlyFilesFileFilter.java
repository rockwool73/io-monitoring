package dk.heick.io.monitoring.ftp;

import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileFilter;

public class FtpOnlyFilesFileFilter implements FTPFileFilter {
	private FTPFileFilter child;
	public FtpOnlyFilesFileFilter(FTPFileFilter child) {
		this.child=child;
	}
	public FTPFileFilter getChild() {
		return child;
	}
	
	@Override
	public boolean accept(FTPFile file) {
		if (file.isFile() && file.isValid()) {
			if (getChild()!=null) {
				return getChild().accept(file);
			} else {
				return true;
			}
		} else {
			return false;
		}
	}
	
}