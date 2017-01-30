package dk.heick.io.monitoring.ftp;

import org.apache.commons.net.ftp.FTP;

public enum FtpFileType {
	
	ASCII(FTP.ASCII_FILE_TYPE),
	BINARY(FTP.BINARY_FILE_TYPE);
		
	private int type;
	FtpFileType(int type) {
		this.type=type;
	}
	public int getType() {
		return type;
	}

}
