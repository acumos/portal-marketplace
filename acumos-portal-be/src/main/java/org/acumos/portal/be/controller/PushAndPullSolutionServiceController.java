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
package org.acumos.portal.be.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acumos.cds.domain.MLPSiteConfig;
import org.acumos.portal.be.APINames;
import org.acumos.portal.be.common.exception.StorageException;
import org.acumos.portal.be.service.AdminService;
import org.acumos.portal.be.service.PushAndPullSolutionService;
import org.acumos.portal.be.service.StorageService;
import org.acumos.portal.be.util.EELFLoggerDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.annotations.ApiOperation;

@Controller
@RequestMapping("/")
public class PushAndPullSolutionServiceController extends AbstractController {

	private static final EELFLoggerDelegate log = EELFLoggerDelegate
			.getLogger(PushAndPullSolutionServiceController.class);

	@Autowired
	private StorageService storageService;

	@Autowired
	private PushAndPullSolutionService pushAndPullSolutionService;

	@Autowired
	AdminService adminService;

	/**
	 * 
	 */

	public PushAndPullSolutionServiceController() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Sends Dockerized Image Tar ball file of the Artifact for the Solution.
	 * 
	 * @param solutionId
	 *            solution ID
	 * @param artifactId
	 *            artifact ID
	 * @param revisionId
	 *            revision ID
	 * @param userId
	 *            user ID
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 */
	@ApiOperation(value = "API to download the dockerized Image Artifact of the Machine Learning Solution", response = InputStream.class, responseContainer = "List", code = 200)
	@RequestMapping(value = {
			APINames.DOWNLOADS_SOLUTIONS }, method = RequestMethod.GET, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	@ResponseBody
	public void downloadSolutionArtifact(@PathVariable("solutionId") String solutionId,
			@RequestParam("artifactId") String artifactId, @RequestParam("revisionId") String revisionId,
			@RequestParam("userId") String userId, HttpServletRequest request, HttpServletResponse response) {
		try {

			/**
			 * Steps to be implemented a. Invoke Common Data Service to get the Solution &
			 * Artifact Details b. Invoke download downloadModelDockerImage() to get the
			 * Docker Image File c. Send back the file as a tar file to the UI
			 */

			String artifactFileName = pushAndPullSolutionService.getFileNameByArtifactId(artifactId);
			response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
			response.setHeader("Pragma", "no-cache");
			response.setHeader("Expires", "0");
			response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
			response.setHeader("x-filename", artifactFileName);
			response.setHeader("Content-Disposition", "attachment; filename=\"" + artifactFileName + "\"");
			response.setStatus(HttpServletResponse.SC_OK);

			pushAndPullSolutionService.downloadModelArtifact(artifactId, response);
			pushAndPullSolutionService.getSolutionDownload(solutionId, artifactId, userId);
			/*if (resource.available() > 0) {
				org.apache.commons.io.IOUtils.copy(resource, response.getOutputStream());
				response.flushBuffer();
			} else {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			}*/

		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			log.error(EELFLoggerDelegate.errorLogger,
					"Exception Occurred downloading a artifact for a Solution in Push and Pull Solution serive", e);
		}
		// return resource;
	}

	/**
	 * Upload the model zip file to the temporary folder on server.
	 * @param file 
	 * zip file
	 * @param userId
	 * user ID 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 * @throws IOException 
	 */
	@ApiOperation(value = "API to Upload the model to the server")
	@RequestMapping(value = {
			APINames.UPLOAD_USER_MODEL }, method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@ResponseBody
	public void uploadModel(@RequestParam("file") MultipartFile file, @PathVariable("userId") String userId,
			HttpServletRequest request, HttpServletResponse response) throws IOException {

		log.debug(EELFLoggerDelegate.debugLogger, "uploadModel for user " + userId);

		// Check if the Onboarding is enabled in the site configuration
		MLPSiteConfig mlpSiteConfig = adminService.getSiteConfig("site_config");
		if (mlpSiteConfig != null) {
			String configJson = mlpSiteConfig.getConfigValue();
			ObjectMapper mapper = new ObjectMapper();
			try {
				Map<String, Object> configObj = mapper.readValue(configJson, Map.class);
				if (configObj != null) {
					List<Map<String, Object>> fields = (List<Map<String, Object>>) configObj.get("fields");
					for (Map<String, Object> items : fields) {
						if ("enableOnBoarding".equalsIgnoreCase((String) items.get("name"))) {
							Map<String, String> dataVal = (Map<String, String>) items.get("data");
							if (dataVal != null) {
								String val = dataVal.get("name");
								if ("Disabled".equalsIgnoreCase(val)) {
									log.info("Uploading the model is Disabled from Admin");
									response.setStatus(HttpServletResponse.SC_FORBIDDEN);
									return;
								}
							}
						}

					}
				}
			} catch (JsonParseException e) {
				log.error("Exception Occured while parsing site configuration.", e.getMessage());
				log.info("Exception Occured while parsing site configuration. Do Nothing");
			} catch (JsonMappingException e) {
				log.error("Exception Occured while parsing site configuration.", e.getMessage());
				log.info("Exception Occured while parsing site configuration. Do Nothing");
			} catch (IOException e) {
				log.error("Exception Occured while parsing site configuration.", e.getMessage());
				log.info("Exception Occured while parsing site configuration. Do Nothing");
			}
		}
		if (StringUtils.isEmpty(userId)) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			log.info("User Id Required to uplpoad the model");
			return;
		}
		try {

			storageService.store(file, userId);

		} catch (StorageException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().write(e.getMessage());
			response.flushBuffer();
			
			log.error(EELFLoggerDelegate.errorLogger,
					"Exception Occurred while uploading the model in Push and Pull Solution serive", e);
		}
		catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			log.error(EELFLoggerDelegate.errorLogger,
					"Exception Occurred while uploading the model in Push and Pull Solution serive", e);
		}
		// return resource;
	}
}
