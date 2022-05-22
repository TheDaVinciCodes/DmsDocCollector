package com.tdvc.doccollector.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

/**
 * @author TheDaVinciCodes
 *
 */
@Service
public class SftpService {

	private final Logger logger = LoggerFactory.getLogger(SftpService.class);

	@Value("${sftp.host}")
	String host;

	@Value("${sftp.port}")
	Integer port;

	@Value("${sftp.username}")
	String username;

	@Value("${sftp.password}")
	String password;

	@Value("${sftp.private-key.path}")
	String privateKey;

	@Value("${sftp.session-timeout}")
	Integer sessionTimeout;

	@Value("${sftp.channel-timeout}")
	Integer channelTimeout;

	@Value("${sftp.local.file-path}")
	String localFilePath;

	@Value("${sftp.remote.file-path}")
	String remoteFilePath;

	public void uploadSftp() {
		logger.info("Start upload file");
		boolean isUploaded = uploadFile();
		logger.info("Upload result: " + isUploaded);
	}

	public boolean uploadFile() {
		ChannelSftp channelSftp = createChannelSftp();

		try {
			channelSftp.put(localFilePath, remoteFilePath);
			return true;
		} catch (SftpException ex) {
			logger.error("Error upload file", ex);
		} finally {
			disconnectChannelSftp(channelSftp);
		}

		return false;

	}

	private ChannelSftp createChannelSftp() {
		try {
			JSch jSch = new JSch();
			jSch.addIdentity(privateKey);
			Session session = jSch.getSession(username, host, port);
			session.setConfig("PreferredAuthentications", "publickey");
			session.setConfig("StrictHostKeyChecking", "no");
			session.setPassword(password);
			session.connect(sessionTimeout);
			Channel channel = session.openChannel("sftp");
			channel.connect(channelTimeout);
			return (ChannelSftp) channel;
		} catch (JSchException ex) {
			logger.error("Create ChannelSftp error", ex);
		}

		return null;
	}

	private void disconnectChannelSftp(ChannelSftp channelSftp) {
		try {
			if (channelSftp == null)
				return;

			if (channelSftp.isConnected())
				channelSftp.disconnect();

			if (channelSftp.getSession() != null)
				channelSftp.getSession().disconnect();

		} catch (Exception ex) {
			logger.error("SFTP disconnect error", ex);
		}

	}
}
