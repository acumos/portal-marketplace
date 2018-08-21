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
package org.acumos.portal.be.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.multipart.MultipartFile;

public class FileUtils {

	private static final EELFLoggerDelegate log = EELFLoggerDelegate.getLogger(FileUtils.class);


	public static void extractZipFile(MultipartFile file, String destinationPath) {
	    try {
	        byte[] buf = new byte[1024];
	        ZipInputStream zipinputstream = null;
	        ZipEntry zipentry;
	        zipinputstream = new ZipInputStream(file.getInputStream());

	        zipentry = zipinputstream.getNextEntry();
	        while (zipentry != null) {
	            //for each entry to be extracted
	            String entryName = destinationPath+File.separator + zipentry.getName();
	            entryName = entryName.replace('/', File.separatorChar);
	            entryName = entryName.replace('\\', File.separatorChar);
	            log.info("entryname " + entryName);
	            int n;
	            FileOutputStream fileoutputstream;
	            File newFile = new File(entryName);
	            if (zipentry.isDirectory()) {
	                if (!newFile.mkdirs()) {
	                    break;
	                }
	                zipentry = zipinputstream.getNextEntry();
	                continue;
	            }

	            fileoutputstream = new FileOutputStream(entryName);

	            while ((n = zipinputstream.read(buf, 0, 1024)) > -1) {
	                fileoutputstream.write(buf, 0, n);
	            }

	            fileoutputstream.close();
	            zipinputstream.closeEntry();
	            zipentry = zipinputstream.getNextEntry();

	        }

	        zipinputstream.close();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
}
