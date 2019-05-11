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

/**
 * 
 */
package org.acumos.portal.be.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

public class PortalFileUtils {

	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());	

	public static boolean extractZipFile(MultipartFile file, String destinationPath) {
	    byte[] buf = new byte[1024];
	    ZipEntry zipentry;
	    boolean flag = false;
	    String extension = FilenameUtils.getExtension(file.getOriginalFilename());
	    if(extension.equalsIgnoreCase("zip")) {
	    	try (ZipInputStream zipinputstream = new ZipInputStream(file.getInputStream())){
		        zipentry = zipinputstream.getNextEntry();		        		        
		        while (zipentry != null) {
		            //for each entry to be extracted
	        		String entryName = getFileName(file, destinationPath);
		            log.info("entryname " + entryName);
	                File newFile = new File(entryName);
		            if (zipentry.isDirectory()){
		                if (!newFile.mkdirs()) {
	                         break;
	                    }
	                    zipentry = zipinputstream.getNextEntry();
	                    continue;
	                }
		            FileOutputStream fileoutputstream = new FileOutputStream(entryName);
		            readWriteOnboardedFile(buf, file, fileoutputstream);
		            zipinputstream.closeEntry();
		            zipentry = zipinputstream.getNextEntry();
		         }
		        flag = true;
		     } catch (IOException e) {
		        log.error("Exception occured during extracting File", e.getMessage());
		       }
	    }else if(extension.equalsIgnoreCase("onnx") || extension.equalsIgnoreCase("pfa")) {
	    	String entryName = getFileName(file, destinationPath);
            log.info("entryname onnx / pfa file " + entryName);
            try {
            	FileOutputStream fileoutputstream = new FileOutputStream(entryName);
            	readWriteOnboardedFile(buf, file, fileoutputstream);
			} catch (IOException e) {
				 log.error("Exception occured during extracting onnx / pfa File", e.getMessage());
			}
	    	flag = true;
	    }
		return flag;	    
	}

	private static void readWriteOnboardedFile(byte[] buf, MultipartFile file, FileOutputStream fileoutputstream) throws IOException, FileNotFoundException {				
	    File newFile = new File(file.getOriginalFilename());
	    newFile.createNewFile();
	    fileoutputstream = new FileOutputStream(newFile);
	    fileoutputstream.write(file.getBytes());
	    fileoutputstream.close();				 
	}

	private static String getFileName(MultipartFile file, String destinationPath) {
		String entryName = destinationPath+File.separator + file.getName();
		entryName = entryName.replace('/', File.separatorChar);
		entryName = entryName.replace('\\', File.separatorChar);
		return entryName;
	}
}