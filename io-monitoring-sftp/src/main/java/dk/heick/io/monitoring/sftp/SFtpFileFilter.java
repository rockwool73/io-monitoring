package dk.heick.io.monitoring.sftp;

import com.jcraft.jsch.ChannelSftp;

public interface SFtpFileFilter {
	
	public boolean accept(ChannelSftp.LsEntry file);
	
}
