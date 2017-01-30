package dk.heick.io.monitoring.sftp;

import java.util.Hashtable;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public interface AdditionalSFtpConfiguration {
		
	public void modifyJSch(JSch jsch);
	
	public void modifyConfig(Hashtable<String,String> config);
	
	public void modifySession(Session session);
	
	public void modifyChannel(Channel channel);
	
	public void modifyChannelSftp(ChannelSftp sftpChannel);
	

	
	
	
}
