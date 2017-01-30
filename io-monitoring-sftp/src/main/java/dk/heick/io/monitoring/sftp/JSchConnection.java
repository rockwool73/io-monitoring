package dk.heick.io.monitoring.sftp;

import java.util.Hashtable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class JSchConnection {
	
	public final static String PROTOCOL_SFTP="sftp";

	private SFtpMonitorTaskConfiguration configuration;
	private Logger logger;	
	
	private JSch jsch;
	private Session session;
	private Channel channel;
	private ChannelSftp channelSftp = null;		
	
	public JSchConnection(SFtpMonitorTaskConfiguration configuration) {
		super();
		this.configuration=configuration;
	}
	public void connect() throws JSchException {
		
		//## JSCH ##########################################
		jsch = new JSch();
		if (getConfiguration().hasAdditionalSFtpConfiguration()) {
			getConfiguration().getAdditionalSFTPConfiguration().modifyJSch(jsch);				
		}
		
		//## CONFIG ########################################
		Hashtable<String,String> config = new Hashtable<String,String>();
		JSchConfigName.assignYesNoValue(config, JSchConfigName.Other_StrictHostKeyChecking, getConfiguration().isStrictHostKeyChecking());
		if (getConfiguration().hasAdditionalSFtpConfiguration()) {
			getConfiguration().getAdditionalSFTPConfiguration().modifyConfig(config);
		}
		
		//## SESSION #######################################
		session = jsch.getSession(
				getConfiguration().getUsername(), 
				getConfiguration().getHost(), 
				getConfiguration().getPort());		
		session.setPassword(getConfiguration().getPassword());		 
		session.connect();
		
		//## CHANNEL #######################################
		channel = session.openChannel(PROTOCOL_SFTP);
		channel.connect();

		//## CHANNELSFTP ###################################			
		channelSftp = (ChannelSftp) channel;
		
	}
	public void disconnect() {			
		if (getChannelSftp()==null) {		
			if (getChannelSftp().isConnected()) {
				getChannelSftp().exit();						
				getLogger().debug("Disconnected sftp channel");				
			}
		}
		if (getSession()!=null) {
			if (getSession().isConnected()) {
				getSession().disconnect();
				getLogger().debug("Disconnected sftp session");
			}
		}			
	}
	
	public final boolean isConnected() {
		if (getChannelSftp()!=null) {
			return getChannelSftp().isConnected();
		} else {
			return false;
		}
	}
	
	
	public SFtpMonitorTaskConfiguration getConfiguration() {
		return configuration;
	}
	public Channel getChannel() {
		return channel;
	}
	public ChannelSftp getChannelSftp() {
		return channelSftp;
	}
	public JSch getJsch() {
		return jsch;
	}
	public Session getSession() {
		return session;
	}
	
	public Logger getLogger() {
		if (logger==null) {
			logger = LoggerFactory.getLogger(JSchConnection.class);					
		}
		return logger;
	}

}
