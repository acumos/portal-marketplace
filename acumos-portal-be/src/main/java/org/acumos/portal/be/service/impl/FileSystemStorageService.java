/*-
 * ===============LICENSE_START=======================================================
 * Acumos
 * ===================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
 * ===================================================================================
 * This Acumos software file is distributed by AT&T and Tech Mahindra
 * under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * This file is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ===============LICENSE_END=========================================================
 */

package org.acumos.portal.be.service.impl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.acumos.portal.be.common.exception.StorageException;
import org.acumos.portal.be.controller.PushAndPullSolutionServiceController;
import org.acumos.portal.be.service.StorageService;
import org.acumos.portal.be.util.EELFLoggerDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileSystemStorageService implements StorageService {

	private static final EELFLoggerDelegate log = EELFLoggerDelegate
			.getLogger(PushAndPullSolutionServiceController.class);

	@Autowired
	private Environment env;

	@Override
	public void store(MultipartFile file, String userId) {
		String filename = StringUtils.cleanPath(file.getOriginalFilename());
		
		log.debug(EELFLoggerDelegate.debugLogger, "uploadModel for user " + userId + " file name " + filename);
		
		try {
			if (file.isEmpty()) {
				log.error(EELFLoggerDelegate.errorLogger, "Failed to store empty file " + filename );
				throw new StorageException("Failed to store empty file " + filename);
			}
			if (filename.contains("..")) {
				// This is a security check
				log.error(EELFLoggerDelegate.errorLogger, "Cannot store file with relative path outside current directory " + filename );
				throw new StorageException(
						"Cannot store file with relative path outside current directory " + filename);
			}

			if (!filename.contains(".zip")) {
				// This is a security check
				log.error(EELFLoggerDelegate.errorLogger, "Zip File Required. Original File :  " + filename );
				throw new StorageException("Zip File Required. Original File : " + filename);
			}

			if (!validateFile(file)) {
				log.error(EELFLoggerDelegate.errorLogger, "Zip File does not contain required files " + filename );
				throw new StorageException("Zip File does not contain required files " + filename);
			}
			// Remove older files before uploading another solution files
			deleteAll(userId);
			log.debug(EELFLoggerDelegate.debugLogger, "Remove Previously Uploaded files for User  " + userId );
			Path modelFolderLocation = Paths
					.get(env.getProperty("model.storage.folder.name") + File.separator + userId);
			
			log.debug(EELFLoggerDelegate.debugLogger, "Upload Location Path  " + modelFolderLocation);
			
			Files.createDirectories(modelFolderLocation);
			
			log.debug(EELFLoggerDelegate.debugLogger, "Directory Created at Upload Location Path  " + modelFolderLocation);

			try {
				ZipInputStream zis = new ZipInputStream(file.getInputStream());
				ZipEntry zipEntry = zis.getNextEntry();
				while (zipEntry != null) {
					String filePath = env.getProperty("model.storage.folder.name") + File.separator + userId + File.separator
							+ zipEntry.getName();
					log.debug(EELFLoggerDelegate.debugLogger, "Extracting zip File path   " + filePath);
					
					if (!zipEntry.isDirectory()) {
						// if the entry is a file, extracts it
						extractFile(zis, filePath);
						log.debug(EELFLoggerDelegate.debugLogger, "File Extracted  " + filePath);
					}

					zis.closeEntry();
					zipEntry = zis.getNextEntry();
				}
				zis.close();
				
				log.debug(EELFLoggerDelegate.debugLogger, "Close all File Resource ");

			} catch (IOException e) {
				// TODO Auto-generated catch block
				
				throw new StorageException("Failed to store file " + filename, e);
			}
		} catch (IOException e) {
			throw new StorageException("Failed to store file " + filename, e);
		}
	}

	private Boolean validateFile(MultipartFile file) throws IOException {
		Boolean zipFilePresent = false;
		Boolean schemaFilePresent = false;
		Boolean metadataFilePresent = false;
		ZipInputStream zis = new ZipInputStream(file.getInputStream());
		ZipEntry zipEntry = zis.getNextEntry();
		while (zipEntry != null) {

			if (zipEntry.getName().contains(".zip") || zipEntry.getName().contains(".jar") || zipEntry.getName().contains(".bin") || zipEntry.getName().contains(".tar") || zipEntry.getName().toUpperCase().contains(".R"))
				zipFilePresent = true;

			if (zipEntry.getName().contains(".proto"))
				schemaFilePresent = true;

			if (zipEntry.getName().contains(".json"))
				metadataFilePresent = true;

			zis.closeEntry();
			zipEntry = zis.getNextEntry();
		}
		zis.close();

		if (zipFilePresent && schemaFilePresent && metadataFilePresent)
			return true;
		else
			return false;
	}

	@Override
	public void deleteAll(String userId) {
		FileSystemUtils.deleteRecursively(
				Paths.get(env.getProperty("model.storage.folder.name") + File.separator + userId)
						.toFile());
	}

	private void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
		byte[] bytesIn = new byte[1024];
		int read = 0;
		while ((read = zipIn.read(bytesIn)) != -1) {
			bos.write(bytesIn, 0, read);
		}
		bos.close();
	}

	public void setEnvironment(Environment environment){
		env = environment;
	}
}
