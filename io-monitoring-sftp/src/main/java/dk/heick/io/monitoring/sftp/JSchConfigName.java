package dk.heick.io.monitoring.sftp;

import java.util.Hashtable;

/**
 * https://epaul.github.io/jsch-documentation/javadoc/com/jcraft/jsch/JSch.html#setConfig-java.util.Hashtable-
 * @author Frederik Heick
 */
public enum JSchConfigName {
		
	Algorithm_Kex("kex"),
	Algorithm_server_host_key("server_host_key"),
	Algorithm_cipher_s2c("cipher.s2c"),
	Algorithm_cipher_c2s("cipher.c2s"),
	Algorithm_mac_c2s("mac.c2s"),
	Algorithm_mac_s2c("mac.s2c"),
	Algorithm_compression_c2s("compression.c2s"),
	Algorithm_compression_s2c("compression.s2c"),
	Algorithm_lang_s2c("lang.s2c"),
	Algorithm_lang_c2s("lang.c2s"),
	//
	Other_compression_level("compression_level"),	
	Other_PreferredAuthentications("PreferredAuthentications"),	
	Other_StrictHostKeyChecking("StrictHostKeyChecking"),	
	Other_HashKnownHosts("HashKnownHosts"),	
	Other_CheckCiphers("CheckCiphers");
	
	
	public static final String YES_VALUE="yes";
	public static final String NO_VALUE="no";
	
	private String key;
	
	
	JSchConfigName(String key) {
		this.key=key;
	}
	public String getKey() {
		return key;
	}
	
	public static final void assignYesNoValue(Hashtable<String,String> config,JSchConfigName name,boolean value) {
		if (value) {
			config.put(name.getKey(), JSchConfigName.YES_VALUE);
		} else {
			config.put(name.getKey(), JSchConfigName.NO_VALUE);
		}
	}
	
}
