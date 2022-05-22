package com.tdvc.doccollector.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;

import com.fasterxml.jackson.databind.JsonNode;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.EncryptionMethod;

/**
 * @author TheDaVinciCodes
 *
 */
@Service
public class FileService {

	private final Path basePath;
	private Path rootDir;
	private final String zipEncrypt;
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	FileService(@Value("${document.persist.path}") Path basePath,
			@Value("${document.zip.password}") String zipEncrypt) {
		this.basePath = basePath;
		this.zipEncrypt = zipEncrypt;
	}

	void persistFile(String claim, JsonNode document, String date, String customer) {

		final String fileName = document.path("metadata").path("metadata").path("pagefile").asText("");
		final byte[] byteArray = Base64.getDecoder().decode(document.path("file").asText(""));
		final Path filePath = basePath.resolve(customer.replaceAll("\\s+","")).resolve(date).resolve(claim).resolve(fileName);
		final Path parentDir = filePath.getParent();
		rootDir = filePath.getParent().getParent().getParent();

		try {
			if (!Files.exists(rootDir))
				Files.createDirectories(rootDir);

			if (!Files.exists(parentDir))
				Files.createDirectories(parentDir);

			Files.write(filePath, byteArray);

		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	void protectFile() {
		final ZipFile zipFile = new ZipFile(basePath.resolve("claimed-doc.zip").toString(), zipEncrypt.toCharArray());
		final ZipParameters zipParameters = new ZipParameters();
		zipParameters.setEncryptFiles(true);
		zipParameters.setCompressionLevel(CompressionLevel.HIGHER);
		zipParameters.setEncryptionMethod(EncryptionMethod.ZIP_STANDARD);

		try {
			zipFile.addFolder(rootDir.toFile(), zipParameters);
			zipFile.close();
			logger.info("Documents compressed and password protected under the corresponding root: " + zipFile);

			if (Files.exists(rootDir))
			FileSystemUtils.deleteRecursively(rootDir);

		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}

	}

}
