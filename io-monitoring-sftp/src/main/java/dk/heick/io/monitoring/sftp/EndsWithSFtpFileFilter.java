package dk.heick.io.monitoring.sftp;

import com.jcraft.jsch.ChannelSftp.LsEntry;

public class EndsWithSFtpFileFilter implements SFtpFileFilter {

	private String extension;
	
	public EndsWithSFtpFileFilter(String extension) {
		this.extension=extension;
	}
	public String getExtension() {
		return extension;
	}

	@Override
	public boolean accept(LsEntry file) {
		return file.getFilename().toLowerCase().endsWith(getExtension().toLowerCase());
	}
}
