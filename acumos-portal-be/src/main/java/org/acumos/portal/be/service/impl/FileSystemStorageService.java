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

import java.io.File;
import java.io.FileWriter;
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
import org.acumos.portal.be.util.PortalFileUtils;
import org.acumos.portal.be.util.PortalUtils;
import org.apache.commons.io.FilenameUtils;
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
	private static final String ENV_MODELSTORAGE = "model.storage.folder.name";

	@Override
	public boolean store(MultipartFile file, String userId, boolean flag) {
		String filename = StringUtils.cleanPath(file.getOriginalFilename());
		
		log.debug("uploadModel for user " + userId + " file nameeee: " + filename + "fileeee: "+file+ "Fodlerrrrrrrrrrr    "+env.getProperty(ENV_MODELSTORAGE));
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
				
				log.debug("Remove Previously Uploaded files for User  " + userId );
				Path modelFolderLocation = Paths
						.get(env.getProperty(ENV_MODELSTORAGE) + File.separator + userId);

				log.debug("Upload Location Path  " + modelFolderLocation);
				
				Files.createDirectories(modelFolderLocation);
				
				log.debug("Directory Created at Upload Location Path  " + modelFolderLocation);
	
				try {
					file.transferTo(new File( env.getProperty(ENV_MODELSTORAGE) + File.separator + userId + File.separator + FileSystemStorageService.LICENSE_FILE ));	
					result=true;
				} catch (Exception e) {
					throw new StorageException("Failed to store file " + filename, e);
				}
				
			} else {
				if (!filename.endsWith(".zip") &&  !filename.endsWith(".onnx") && !filename.endsWith(".pfa")) {
					log.error(".zip, .onnx or .pfa File Required. Original File :  " + filename );
					throw new StorageException(".zip, .onnx or .pfa File Required. Original File : " + filename);
				}
	
				if (!validateFile(file)) {
					log.error("On-boarded Model does not contain required files " + filename );
					throw new StorageException("On-boarded Model does not contain required files: " + getMissingFiles(file));
				}
				
				log.debug("Remove Previously Uploaded files for User  " + userId );
				Path modelFolderLocation = Paths
						.get(env.getProperty(ENV_MODELSTORAGE) + File.separator + userId);
				
				log.debug("Upload Location Path  " + modelFolderLocation);
				
				Files.createDirectories(modelFolderLocation);
				
				log.debug("Directory Created at Upload Location Path  " + modelFolderLocation);
	
				try {
					result = PortalFileUtils.extractZipFile(file, env.getProperty(ENV_MODELSTORAGE) + File.separator + userId);
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
		Boolean rdataFilePresent=false;
		String blacklist = PortalUtils.getEnvProperty(env, ENV_BLACKLIST);
		String pattern = "(?!^.*(" + blacklist + ")\\/.*$)^.*$";
		Predicate<ZipEntry> filter = entry -> 
			!entry.isDirectory() && entry.getName().matches(pattern);
			
		String extension = FilenameUtils.getExtension(file.getOriginalFilename());
		if(extension.equalsIgnoreCase("zip")) {
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
					
					if(zipEntry.getName().endsWith(".rdata") || zipEntry.getName().endsWith(".r")
							|| zipEntry.getName().endsWith(".Rdata") || zipEntry.getName().endsWith(".R"))
						rdataFilePresent=true;
				}
				zis.closeEntry();
				zipEntry = zis.getNextEntry();
			}
			zis.close();
		}else if(extension.equalsIgnoreCase("onnx") || extension.equalsIgnoreCase("pfa")) {			
				onnxPfaFilePresent = true;
		}		

		if (zipFilePresent && schemaFilePresent && metadataFilePresent || rdataFilePresent)
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
		Boolean rdataFilePresent=false;
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
				
				if(zipEntry.getName().endsWith(".rdata") || zipEntry.getName().endsWith(".r")
						|| zipEntry.getName().endsWith(".Rdata") || zipEntry.getName().endsWith(".R"))
					rdataFilePresent=true;
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
		if(!rdataFilePresent) {
			files.add("rdata file(Specific to R model)");
		}
		return String.join(", ", files);
	}

	@Override
	public void deleteAll(String userId) {
		FileSystemUtils.deleteRecursively(
				Paths.get(env.getProperty(ENV_MODELSTORAGE) + File.separator + userId)
						.toFile());
	}

	public void setEnvironment(Environment environment){
		env = environment;
	}
	
	public boolean createJsonFile(String jsonString, String userId) {
		boolean responseFlag=false;
		try{
			log.debug("Remove Previously Uploaded files for User  " + userId );
			
			Path modelFolderLocation = Paths
				.get(env.getProperty(ENV_MODELSTORAGE) + File.separator + userId);

			log.debug("Upload Location Path  " + modelFolderLocation);
			Files.createDirectories(modelFolderLocation);
			log.debug("Directory Created at Upload Location Path  " + modelFolderLocation);
			try(FileWriter file = new FileWriter(env.getProperty(ENV_MODELSTORAGE) + File.separator + userId + File.separator + LICENSE_FILE)){
				file.write(jsonString);
				file.flush();
				responseFlag=true;
			} catch (IOException e) {
				throw new StorageException("Failed to store file " + LICENSE_FILE, e);
			}
		} catch (Exception e) {
			throw new StorageException("Failed to store file " + LICENSE_FILE, e);
		}
		return responseFlag;
	}

	public void deleteLicenseFile(String userId) throws AcumosServiceException {
		try {
			File file=new File( env.getProperty(ENV_MODELSTORAGE) + File.separator + userId + File.separator + FileSystemStorageService.LICENSE_FILE  );
			file.delete();
		}
		catch (Exception e) {
			throw new AcumosServiceException("Failed to delete file " + FileSystemStorageService.LICENSE_FILE, e);
		}
	}		
	public void deleteProtoFile(String userId) throws AcumosServiceException {
			try {
				File fileDirectory=new File( env.getProperty(ENV_MODELSTORAGE) + File.separator + userId );
				File[] fList = fileDirectory.listFiles();
				File protoFile=null;
				if (fList != null) {
					for (File file : fList) {
						if (file.isFile() && file.getName().contains(".proto")) {
							protoFile=new File(file.getAbsolutePath());
							
						}
					}
				}
				if(protoFile !=null)
					protoFile.delete();
			}
			catch (Exception e) {
				throw new AcumosServiceException("Failed to delete proto file " , e);
			}
	}

	@Override
	public boolean storeProtoFile(MultipartFile file, String userId, boolean protoUploadFlag) {
		String filename = StringUtils.cleanPath(file.getOriginalFilename());
		
		log.debug("uploadModel for user " + userId + " file nameeee: " + filename + "fileeee: "+file+ "Fodlerrrrrrrrrrr    "+env.getProperty(ENV_MODELSTORAGE));
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
			
			if(protoUploadFlag){						
				
				log.debug("Remove Previously Uploaded files for User  " + userId );
				Path modelFolderLocation = Paths
						.get(env.getProperty(ENV_MODELSTORAGE) + File.separator + userId);

				log.debug("Upload Location Path  " + modelFolderLocation);
				
				Files.createDirectories(modelFolderLocation);
				
				log.debug("Directory Created at Upload Location Path  " + modelFolderLocation);
	
				try {
					file.transferTo(new File( env.getProperty(ENV_MODELSTORAGE) + File.separator + userId + File.separator + filename ));	
					result=true;
				} catch (Exception e) {
					throw new StorageException("Failed to store file " + filename, e);
				}
				
			}
		}catch (IOException e) {
			throw new StorageException("Failed to store file " + filename, e);
		}
		return result;
	}
}
