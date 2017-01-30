package dk.heick.io.monitoring.sftp;

import java.util.Objects;

import com.jcraft.jsch.ChannelSftp.LsEntry;

import dk.heick.io.monitoring.utils.WrappedFile;

public class WrappedSFtpFile implements WrappedFile<LsEntry> {
		
	private LsEntry lsEntry;
	
	public WrappedSFtpFile(LsEntry lsEntry) {
		super();
		this.lsEntry=lsEntry;
		
	}

	@Override
	public String getFileName() {
		return getFile().getFilename();
	}

	@Override
	public LsEntry getFile() {
		return lsEntry;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(getFile());
	}
	@Override
	public boolean equals(Object obj) {
		if (obj==null) {
			return false;
		} else if (obj instanceof WrappedSFtpFile) {
			WrappedSFtpFile other = (WrappedSFtpFile)obj;
			return getFile().equals(other.getFile());
		} else {
			return false;
		}
	}
	@Override
	public String toString() {
		return getFileName();
	}	

}
