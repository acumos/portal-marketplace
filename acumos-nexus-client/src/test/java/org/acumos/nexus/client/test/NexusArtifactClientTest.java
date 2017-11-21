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

package org.acumos.nexus.client.test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

import org.acumos.nexus.client.NexusArtifactClient;
import org.acumos.nexus.client.RepositoryLocation;
import org.acumos.nexus.client.data.UploadArtifactInfo;
import org.codehaus.plexus.util.FileUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NexusArtifactClientTest {
	private static Logger logger = LoggerFactory.getLogger(NexusArtifactClientTest.class);
	
	@Ignore
	@Test
	public void testGetArtifact() {
		
		try{
			RepositoryLocation repositoryLocation = new RepositoryLocation();
			repositoryLocation.setId("0");
			repositoryLocation.setUrl("http://mvnrepo.com:8081/repository/maven-central");
			repositoryLocation.setUsername("test");
			repositoryLocation.setPassword("test");
			repositoryLocation.setProxy("");
			NexusArtifactClient artifactClient = new NexusArtifactClient(repositoryLocation);
			
			ByteArrayOutputStream outputStream = artifactClient.getArtifact("ch/qos/logback/logback-classic/1.1.11/logback-classic-1.1.11.jar");
			if(outputStream !=null) {
				outputStream.close();
			}
		} catch (Exception e) {
			logger.error("Exception occurred while executing testGetArtifact:", e);
		}
	}
	
	@Test
	public void testUploadArtifact() {
		
		try{
			RepositoryLocation repositoryLocation = new RepositoryLocation();
			repositoryLocation.setId("1");
			repositoryLocation.setUrl("http://mvnrepo.com:8081/repository/maven-central");
			repositoryLocation.setUsername("test");
			repositoryLocation.setPassword("test");
			repositoryLocation.setProxy("");
			NexusArtifactClient artifactClient = new NexusArtifactClient(repositoryLocation);
			
			//Test Only - START
			//to create an Artifact.
			File tempFile = File.createTempFile( "uploadArtifact", "txt" ); 
            tempFile.deleteOnExit(); 
            String content = "put top secret"; 
            FileUtils.fileWrite( tempFile.getAbsolutePath(), content ); 
            FileInputStream fileInputStream = new FileInputStream( tempFile );
			//Test Only - END
			try {
				UploadArtifactInfo artifactInfo = artifactClient.uploadArtifact("com.model", "uploadArtifact", "1.0.0-SNAPSHOT", "txt", content.length(), fileInputStream);
				logger.debug("Uploaded Artifcta Referece: "+ artifactInfo.getArtifactMvnPath());
			} finally {
				fileInputStream.close();
				tempFile.delete();
			}
		} catch (Exception e) {
			logger.error("Exception occurred while executing testUploadArtifact:", e);
		}
	}
	
	@Test
	public void testDeleteArtifact() {
		try{
			RepositoryLocation repositoryLocation = new RepositoryLocation();
			repositoryLocation.setId("1");
			repositoryLocation.setUrl("http://mvnrepo.com:8081/repository/maven-central");
			repositoryLocation.setUsername("test");
			repositoryLocation.setPassword("test");
			repositoryLocation.setProxy("");
			NexusArtifactClient artifactClient = new NexusArtifactClient(repositoryLocation);
			
			artifactClient.deleteArtifact("ch/qos/logback/logback-classic/1.1.11/logback-classic-1.1.11.jar");
			
		} catch (Exception e) {
			logger.error("Exception occurred while executing testDeleteArtifact:", e);
		}
	}
}
