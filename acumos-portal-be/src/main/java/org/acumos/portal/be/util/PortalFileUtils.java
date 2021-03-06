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

import org.apache.commons.io.FileUtils;
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
		if (extension.equalsIgnoreCase("zip")) {
			try (ZipInputStream zipinputstream = new ZipInputStream(file.getInputStream())) {
				zipentry = zipinputstream.getNextEntry();

				if (zipentry.getName().endsWith(".onnx") || zipentry.getName().endsWith(".pfa")) {
					flag = true;
				}

				if (!flag) {
					while (zipentry != null) {
						zipentry = readWriteOnboardedFile(destinationPath, buf, zipentry, zipinputstream);
					}
					flag = false;
				}
			} catch (IOException e) {
				log.error("Exception occured during extracting zip File", e.getMessage());
			}
		} else {
			String entryName = getFileName(file, destinationPath);
			try {
				readWriteOnboardedFile(destinationPath, buf, file, entryName);
			} catch (IOException e) {
				log.error("Exception occured during extracting onnx/pfa File", e.getMessage());
			}
	    	flag = true;
	    }
		return flag;	    
	}

	private static void readWriteOnboardedFile(String destinationPath, byte[] buf, MultipartFile file, String entryName)
			throws IOException {
		File newFile = new File(entryName);
		FileUtils.writeByteArrayToFile(newFile, file.getBytes());
	}

	private static ZipEntry readWriteOnboardedFile(String destinationPath, byte[] buf, ZipEntry zipentry,
			ZipInputStream zipinputstream) throws IOException {
		// for each entry to be extracted
		String entryName = destinationPath + File.separator + zipentry.getName();
		entryName = entryName.replace('/', File.separatorChar);
		entryName = entryName.replace('\\', File.separatorChar);
		log.debug("entryname " + entryName);
		int n;
		File newFile = new File(entryName);
		if (zipentry.isDirectory()) {
			if (!newFile.mkdirs()) {
				return zipentry;
			}
			zipentry = zipinputstream.getNextEntry();
			return zipentry;
		}
		try (FileOutputStream fileoutputstream = new FileOutputStream(entryName)) {
			while ((n = zipinputstream.read(buf, 0, 1024)) > -1) {
				fileoutputstream.write(buf, 0, n);
			}
		} catch (IOException e) {
			log.error("Exception occured during extracting File", e.getMessage());
		} finally {
			zipinputstream.closeEntry();
			zipentry = zipinputstream.getNextEntry();
		}
		return zipentry;
	}

	private static String getFileName(MultipartFile file, String destinationPath) {
		String entryName = destinationPath + File.separator + file.getOriginalFilename();
		entryName = entryName.replace('/', File.separatorChar);
		entryName = entryName.replace('\\', File.separatorChar);
		return entryName;
	}
}