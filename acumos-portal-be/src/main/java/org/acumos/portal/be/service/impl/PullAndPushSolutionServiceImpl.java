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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.acumos.cds.ArtifactTypeCode;
import org.acumos.cds.client.ICommonDataServiceRestClient;
import org.acumos.cds.domain.MLPArtifact;
import org.acumos.cds.domain.MLPDocument;
import org.acumos.cds.domain.MLPSolutionDownload;
import org.acumos.nexus.client.NexusArtifactClient;
import org.acumos.nexus.client.RepositoryLocation;
import org.acumos.portal.be.docker.DockerClientFactory;
import org.acumos.portal.be.docker.DockerConfiguration;
import org.acumos.portal.be.docker.cmd.SaveImageCommand;
import org.acumos.portal.be.service.PushAndPullSolutionService;
import org.acumos.portal.be.transport.MLSolutionDownload;
import org.acumos.portal.be.util.EELFLoggerDelegate;
import org.acumos.portal.be.util.PortalUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.github.dockerjava.api.DockerClient;


@Service
public class PullAndPushSolutionServiceImpl extends AbstractServiceImpl implements PushAndPullSolutionService {

	private static final EELFLoggerDelegate log = EELFLoggerDelegate.getLogger(PullAndPushSolutionServiceImpl.class);

	@Autowired
	private Environment env;
	
	@Autowired
	private DockerConfiguration dockerConfiguration;

	@Override
	public File downloadModelDockerImage(String modelName, String imageName, String version) {

		log.debug(EELFLoggerDelegate.debugLogger, "downloadModelDockerImage ={}", imageName);
		/**
		 * Steps for downloading a DockerImage
		 * a. Save the Docker Image in a File
		 * b. Return the file  back to the COntroller so that it can send back the response as tar file.
		 */
		File imageFile = null;
		/*DockerClient dockerClient = null;
		final byte[] buffer = new byte[2048];
		int read;
		try {
			//TODO make docker client singleton
			dockerClient = DockerClientFactory.getDockerClient(dockerConfiguration);
			final File tmpDir = new File(System.getProperty("tmp." + imageName + "."+ System.currentTimeMillis()));
			imageFile = new File(tmpDir, modelName + "-" + version + ".tar");
			imageFile.createNewFile();
			imageFile.deleteOnExit();
			//InputStream and outStream buffers handled by try block
			try(OutputStream imageOutput = new BufferedOutputStream(new FileOutputStream(imageFile))) {
				try(InputStream imageInput = IOUtils.toBufferedInputStream(dockerClient.saveImageCmd(imageName).exec())) {
					while ((read = imageInput.read(buffer)) > -1) {
						imageOutput.write(buffer, 0, read);
					}
				}
			}
		} catch (Exception e) {
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred while Downloading Docker Image ={}", e);
		} */
		return imageFile;
	}

	/*
	 * Gets artifact to memory then returns an InputStream with contents of the buffer.
	 */
	@Override
	public InputStream downloadModelArtifact(String artifactId) {
		log.debug(EELFLoggerDelegate.debugLogger, "downloadModelArtifact.1 begins for artifact {}",
				artifactId);
		InputStream inputStream = null;
		ByteArrayOutputStream byteArrayOutputStream = null;
		try {
			ICommonDataServiceRestClient dataServiceRestClient = getClient();
			MLPArtifact mlpArtifact = dataServiceRestClient.getArtifact(artifactId);
			if (mlpArtifact != null && !mlpArtifact.getUri().isEmpty()) {
				if (mlpArtifact.getArtifactTypeCode().equalsIgnoreCase(ArtifactTypeCode.DI.toString())) {
					DockerClient dockerClient = DockerClientFactory.getDockerClient(dockerConfiguration);
					try {
						SaveImageCommand saveImageCommand = new SaveImageCommand(mlpArtifact.getUri(), null, null, null,
								true);
						saveImageCommand.setClient(dockerClient);
						inputStream = saveImageCommand.getDockerImageStream();
						log.debug(EELFLoggerDelegate.debugLogger,
								"downloadModelArtifact.1 received stream for artifact {}", artifactId);
					} catch (Exception e) {
						log.error(EELFLoggerDelegate.errorLogger, "downloadModelArtifact.1 inner failed", e);
					} finally {
						try {
							dockerClient.close();
						} catch (IOException e) {
							log.warn("downloadModelArtifact.1 failed to close docker client", e);
						}
					}
				} else {
					NexusArtifactClient artifactClient = getNexusClient();
					byteArrayOutputStream = artifactClient.getArtifact(mlpArtifact.getUri());
					log.debug(EELFLoggerDelegate.debugLogger,
							"downloadModelArtifact.1 received content for artifact {}", artifactId);
					inputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
					if (byteArrayOutputStream != null) {
						byteArrayOutputStream.close();
					}
				}
			}
		} catch (Exception e) {
			log.error(EELFLoggerDelegate.errorLogger, "downloadModelArtifact.1 outer failed", e);
		}
		return inputStream;
	}

	/*
	 * Fetches artifact by ID and writes stream to response output stream. Docker
	 * images are streamed directly; other artifact types are first fetched to
	 * memory.
	 */
	@Override
	public void downloadModelArtifact(String artifactId, HttpServletResponse response) {
		log.debug(EELFLoggerDelegate.debugLogger, "downloadModelArtifact.2 begins for artifact {}",
				artifactId);
		try {
			ICommonDataServiceRestClient dataServiceRestClient = getClient();
			MLPArtifact mlpArtifact = dataServiceRestClient.getArtifact(artifactId);
			if (mlpArtifact != null && !mlpArtifact.getUri().isEmpty()) {
				if (mlpArtifact.getArtifactTypeCode().equalsIgnoreCase(ArtifactTypeCode.DI.toString())) {
					DockerClient dockerClient = DockerClientFactory.getDockerClient(dockerConfiguration);
					try {
						SaveImageCommand saveImageCommand = new SaveImageCommand(mlpArtifact.getUri(), null, null, null,
								true);
						saveImageCommand.setClient(dockerClient);
						// Keep the default buffer size as 8 if no buffer limit is provided
						Integer buffer = Integer.parseInt(env.getProperty("portal.feature.download_bufferSize", "8"));
						if (buffer <= 0) {
							buffer = 8;
						}
						log.debug(EELFLoggerDelegate.debugLogger,
								"downloadModelArtifact.2 directing docker stream to response for artifact {}", artifactId);
						saveImageCommand.getDockerImageStream(response, buffer);
					} catch (Exception e) {
						log.error(EELFLoggerDelegate.errorLogger, "downloadModelArtifact.2 inner failed", e);
					} finally {
						try {
							dockerClient.close();
						} catch (IOException e) {
							log.warn("downloadModelArtifact.2 failed to close docker client", e);
						}
					}
				} else {
					NexusArtifactClient artifactClient = getNexusClient();
					ByteArrayOutputStream byteArrayOutputStream = artifactClient.getArtifact(mlpArtifact.getUri());
					log.debug(EELFLoggerDelegate.debugLogger,
							"downloadModelArtifact.2 copying content stream for artifact {}", artifactId);
					byteArrayOutputStream.writeTo(response.getOutputStream());
					response.flushBuffer();
					if (byteArrayOutputStream != null) {
						byteArrayOutputStream.close();
					}
				}
			}
		} catch (Exception e) {
			log.error(EELFLoggerDelegate.errorLogger, "downloadModelArtifact.2 outer failed", e);
		}
	}

	@Override
	public String getFileNameByArtifactId(String artifactId) {
        log.debug(EELFLoggerDelegate.debugLogger, "getArtifactById for artifact ID {}", artifactId);

        String artifactFileName = null;
        ICommonDataServiceRestClient dataServiceRestClient = getClient();
        MLPArtifact mlpArtifact = dataServiceRestClient.getArtifact(artifactId);
        if (mlpArtifact != null) {
            String uri = mlpArtifact.getUri();
            if (!uri.isEmpty()) {
                artifactFileName = uri.substring(uri.lastIndexOf("/") + 1, uri.length());
                if(mlpArtifact.getArtifactTypeCode().equalsIgnoreCase(ArtifactTypeCode.DI.toString())) {
                	artifactFileName += ".tar";
                	}
                }
            }
		return artifactFileName;
	}

	public String getFileNameByDocumentId(String documentId) {
        log.debug(EELFLoggerDelegate.debugLogger, "getDocumentNameById for document ID {}", documentId);

        String artifactFileName = null;
        ICommonDataServiceRestClient dataServiceRestClient = getClient();
        MLPDocument mlpDocument = dataServiceRestClient.getDocument(documentId);
        if (mlpDocument != null) {
            artifactFileName = mlpDocument.getName();
        }
		return artifactFileName;
	}

	/*
	 * Fetches document by ID and writes stream to response output stream.
	 */
	@Override
	public void downloadModelDocument(String documentId, HttpServletResponse response) {
		log.debug(EELFLoggerDelegate.debugLogger, "downloadModelDocument.2 begins for document {}",
				documentId);
		try {
			ICommonDataServiceRestClient dataServiceRestClient = getClient();
			MLPDocument mlpDocument = dataServiceRestClient.getDocument(documentId);
			if (mlpDocument != null && !mlpDocument.getUri().isEmpty()) {
				NexusArtifactClient nexusClient = getNexusClient();
				ByteArrayOutputStream byteArrayOutputStream = nexusClient.getArtifact(mlpDocument.getUri());
				log.debug(EELFLoggerDelegate.debugLogger,
						"downloadModelDocument.2 copying content stream for document {}", mlpDocument);
				byteArrayOutputStream.writeTo(response.getOutputStream());
				response.flushBuffer();
				if (byteArrayOutputStream != null) {
					byteArrayOutputStream.close();
				}
			}
		} catch (Exception e) {
			log.error(EELFLoggerDelegate.errorLogger, "downloadModelDocument.2 outer failed", e);
		}
	}

	@Override
	public MLSolutionDownload getSolutionDownload(String solutionId, String artifactId, String userId) {
		log.debug(EELFLoggerDelegate.debugLogger, "getSolutionDownload for solution ID {}", solutionId);
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		MLPSolutionDownload download = new MLPSolutionDownload();
		download.setSolutionId(solutionId);
		download.setArtifactId(artifactId);
		download.setUserId(userId);
		download.setDownloadDate(new Date());
		MLSolutionDownload mlSolutionDownload = PortalUtils
				.convertToMLSolutionDownload(dataServiceRestClient.createSolutionDownload(download));
		return mlSolutionDownload;
	}

}
