package dk.heick.io.monitoring.ftp;

import java.io.IOException;

import org.apache.commons.net.ftp.FTPClient;

public interface AdditionalFtpConfiguration {
	
	public void configure(FTPClient client) throws IOException;

}
