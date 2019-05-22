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
package org.acumos.portal.be.service;

import java.io.File;
import java.io.InputStream;

import javax.servlet.http.HttpServletResponse;

import org.acumos.portal.be.transport.MLSolutionDownload;
import org.springframework.web.multipart.MultipartFile;


/**
 * Interface to support downloading of Docker Images and Artifacts for the Machine Learning Model
 */
public interface PushAndPullSolutionService {
	
	public File downloadModelDockerImage(String modelName, String imageName, String version);
	
	//TODO Add parameters to the interface
	public InputStream downloadModelArtifact(String artifactId);

	String getFileNameByArtifactId(String artifactId);

	MLSolutionDownload getSolutionDownload(String solutionId, String artifactId, String userId);

	public void downloadModelArtifact(String artifactId, HttpServletResponse respose);

	void downloadModelDocument(String documentId, HttpServletResponse response);

	public String getFileNameByDocumentId(String documentId);
	
	public boolean uploadLicense(MultipartFile file, String userId, String solutionId, String revisionId, String versionId);
}
