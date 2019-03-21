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
import java.lang.invoke.MethodHandles;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.acumos.portal.be.common.exception.AcumosServiceException;
import org.acumos.portal.be.common.exception.StorageException;
import org.acumos.portal.be.service.StorageService;
import org.acumos.portal.be.util.FileUtils;
import org.acumos.portal.be.util.PortalUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileSystemStorageService implements StorageService {

	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());	

	@Autowired
	private Environment env;

	private static final String ENV_BLACKLIST = "onboarding.directory.blacklist";
	private static final String LICENSE_FILE = "license.json";

	@Override
	public boolean store(MultipartFile file, String userId, boolean flag) {
		String filename = StringUtils.cleanPath(file.getOriginalFilename());
		
		log.debug("uploadModel for user " + userId + " file nameeee: " + filename + "fileeee: "+file+ "Fodlerrrrrrrrrrr    "+env.getProperty("model.storage.folder.name"));
		boolean result = false;
		try {
			if (file.isEmpty()) {
				log.error("Failed to store empty file " + filename );
				throw new StorageException("Failed to store empty file " + filename);
			}
			if (filename.contains("..")) {
				// This is a security check
				log.error("Cannot store file with relative path outside current directory " + filename );
				throw new StorageException(
						"Cannot store file with relative path outside current directory " + filename);
			}
			
			if(flag){
				
				if (!filename.endsWith(".json")) {				
					log.error("json File Required. Original File :  " + filename );
					throw new StorageException("json File Required. Original File : " + filename);
				}		
				
				log.debug("Remove Previously Uploaded files for User  " + userId );
				Path modelFolderLocation = Paths
						.get(env.getProperty("model.storage.folder.name") + File.separator + userId);

				log.debug("Upload Location Path  " + modelFolderLocation);
				
				Files.createDirectories(modelFolderLocation);
				
				log.debug("Directory Created at Upload Location Path  " + modelFolderLocation);
	
				try {
					file.transferTo(new File( env.getProperty("model.storage.folder.name") + File.separator + userId + File.separator + FileSystemStorageService.LICENSE_FILE ));	

				} catch (Exception e) {
					throw new StorageException("Failed to store file " + filename, e);
				}
				
			} else {
				if (!filename.endsWith(".zip")) {
					log.error("Zip File Required. Original File :  " + filename );
					throw new StorageException("Zip File Required. Original File : " + filename);
				}
	
				if (!validateFile(file)) {
					log.error("Zip File does not contain required files " + filename );
					throw new StorageException("Zip File does not contain required files: " + getMissingFiles(file));
				}
				// Remove older files before uploading another solution files
				deleteAll(userId);
				log.debug("Remove Previously Uploaded files for User  " + userId );
				Path modelFolderLocation = Paths
						.get(env.getProperty("model.storage.folder.name") + File.separator + userId);
				
				log.debug("Upload Location Path  " + modelFolderLocation);
				
				Files.createDirectories(modelFolderLocation);
				
				log.debug("Directory Created at Upload Location Path  " + modelFolderLocation);
	
				try {
					result = FileUtils.extractZipFile(file, env.getProperty("model.storage.folder.name") + File.separator + userId);
					log.debug("Close all File Resource ");
				} catch (Exception e) {
					throw new StorageException("Failed to store file " + filename, e);
				}
			}
		} catch (AcumosServiceException | IOException e) {
			throw new StorageException("Failed to store file " + filename, e);
		}
		return result;
	}

	private Boolean validateFile(MultipartFile file) throws IOException, AcumosServiceException {
		Boolean zipFilePresent = false;
		Boolean schemaFilePresent = false;
		Boolean metadataFilePresent = false;
		Boolean onnxPfaFilePresent = false;
		String blacklist = PortalUtils.getEnvProperty(env, ENV_BLACKLIST);
		String pattern = "(?!^.*(" + blacklist + ")\\/.*$)^.*$";
		Predicate<ZipEntry> filter = entry -> 
			!entry.isDirectory() && entry.getName().matches(pattern);
		ZipInputStream zis = new ZipInputStream(file.getInputStream());
		ZipEntry zipEntry = zis.getNextEntry();
		while (zipEntry != null) {
			if (filter.test(zipEntry)) {
				if (zipEntry.getName().endsWith(".zip") || zipEntry.getName().endsWith(".jar") || zipEntry.getName().endsWith(".bin") || zipEntry.getName().endsWith(".tar") || zipEntry.getName().toUpperCase().endsWith(".R"))
					zipFilePresent = true;
	
				if (zipEntry.getName().endsWith(".proto"))
					schemaFilePresent = true;
	
				if (zipEntry.getName().endsWith(".json"))
					metadataFilePresent = true;
				
				if (zipEntry.getName().endsWith(".onnx") || zipEntry.getName().endsWith(".pfa"))
					onnxPfaFilePresent = true;
			}
			zis.closeEntry();
			zipEntry = zis.getNextEntry();
		}
		zis.close();

		if (zipFilePresent && schemaFilePresent && metadataFilePresent)
			return true;
		else if(onnxPfaFilePresent)
			return true;
		else
			return false;
	}
	
	private String getMissingFiles(MultipartFile file) throws IOException, AcumosServiceException {
		Boolean zipFilePresent = false;
		Boolean schemaFilePresent = false;
		Boolean metadataFilePresent = false;
		String blacklist = PortalUtils.getEnvProperty(env, ENV_BLACKLIST);
		String pattern = "(?!^.*(" + blacklist + ")\\/.*$)^.*$";
		Predicate<ZipEntry> filter = entry -> 
			!entry.isDirectory() && entry.getName().matches(pattern);
		ZipInputStream zis = new ZipInputStream(file.getInputStream());
		ZipEntry zipEntry = zis.getNextEntry();
		while (zipEntry != null) {
			if (filter.test(zipEntry)) {
				if (zipEntry.getName().endsWith(".zip") || zipEntry.getName().endsWith(".jar") || zipEntry.getName().endsWith(".bin") || zipEntry.getName().endsWith(".tar") || zipEntry.getName().toUpperCase().endsWith(".R"))
					zipFilePresent = true;
	
				if (zipEntry.getName().endsWith(".proto"))
					schemaFilePresent = true;
	
				if (zipEntry.getName().endsWith(".json"))
					metadataFilePresent = true;
			}
			zis.closeEntry();
			zipEntry = zis.getNextEntry();
		}
		zis.close();
		
		List<String> files = new ArrayList<String>();
		if (!zipFilePresent) {
			files.add("model zip");
		}
		if (!schemaFilePresent) {
			files.add("schema proto");
		}
		if (!metadataFilePresent) {
			files.add("metadata json");
		}
		return String.join(", ", files);
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