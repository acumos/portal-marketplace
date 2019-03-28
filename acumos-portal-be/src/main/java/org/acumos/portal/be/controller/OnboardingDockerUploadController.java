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


package org.acumos.portal.be.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acumos.cds.domain.MLPSolution;
import org.acumos.cds.domain.MLPSolutionRevision;
import org.acumos.cds.transport.RestPageResponse;
import org.acumos.portal.be.APINames;
import org.acumos.portal.be.common.JSONTags;
import org.acumos.portal.be.common.JsonRequest;
import org.acumos.portal.be.common.JsonResponse;
import org.acumos.portal.be.common.RestPageRequestBE;
import org.acumos.portal.be.common.exception.AcumosServiceException;
import org.acumos.portal.be.service.MarketPlaceCatalogService;
import org.acumos.portal.be.service.OnboardingDockerService;
import org.acumos.portal.be.transport.MLSolution;
import org.acumos.portal.be.util.EELFLoggerDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.ApiOperation;

@Controller
@RequestMapping("/onboardingDocker")
public class OnboardingDockerUploadController  extends AbstractController {
	
	private static final EELFLoggerDelegate log = EELFLoggerDelegate.getLogger(OnboardingDockerUploadController.class);

	
	@Autowired
	private OnboardingDockerService onboardingDockerService;
	
	@Autowired
	private MarketPlaceCatalogService catalogService;
	
	Map<String, String> toolkitTypeDetails = new HashMap<>();
	protected boolean dcaeflag = false;

	//search for model name 
	@ApiOperation(value = "Get search solution according to queryparamters sent.", response = MLSolution.class, responseContainer = "List")
	@RequestMapping(value = { APINames.DOCKER_SEARCH_SOLUTION }, method = RequestMethod.POST, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<RestPageResponse<MLPSolution>> getSearchSolutions(HttpServletRequest request, HttpServletResponse response,
			@RequestBody JsonRequest<RestPageRequestBE> restPageReq) {
		RestPageResponse<MLPSolution> mlSolutions = null;
		JsonResponse<RestPageResponse<MLPSolution>> data = new JsonResponse<>();
		try {
			mlSolutions = onboardingDockerService.getRelatedSolution(restPageReq) ;
			
			if (mlSolutions != null) {
				data.setResponseBody(mlSolutions);
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
				data.setResponseDetail("Solutions fetched Successfully");
				log.debug(EELFLoggerDelegate.debugLogger, "getSearchSolutions  at portal using docker upload: size is {} ", mlSolutions.getSize());
			}else if(mlSolutions == null) {
				 
				data.setResponseBody(mlSolutions);
				data.setResponseCode(String.valueOf(HttpServletResponse.SC_BAD_REQUEST));
				data.setErrorCode("Null Pointer");
				data.setResponseDetail("Search Term is not created");
				data.setStatus(false);
			}
		} catch (AcumosServiceException e) {
			data.setErrorCode(e.getErrorCode());
			data.setResponseDetail(e.getMessage());
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred Fetching Solutions for Onboarding Docker Upload Controller at portal using docker upload",
					e);
		}
		return data;
	}	
	
	
	//get the docker image url
	
	@ApiOperation(value = "Gets Artifacts URL based on Solution Revision from the Catalog of the local Acumos Instance .", responseContainer = "String")
	@RequestMapping(value = { APINames.ARTIFACTS_URL }, method = RequestMethod.GET, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<String> getArtifactsUrl(HttpServletRequest request,
			HttpServletResponse response, @PathVariable("solutionId") String solutionId) {
		
		JsonResponse<String> data = new JsonResponse<String>();
		List<MLPSolutionRevision> solutionRevisions = null;
		try {
			solutionRevisions = catalogService.getSolutionRevision(solutionId);
			
			// getting artifact url of the latest revision id
			
			String artifactsUrl = onboardingDockerService.getLatestArtifactsUrl(solutionRevisions);
			
			if(artifactsUrl != null){
				
				data.setContent(artifactsUrl);				
				data.setResponseBody(artifactsUrl);
				data.setResponseCode(String.valueOf(HttpServletResponse.SC_OK));
				data.setResponseDetail(JSONTags.TAG_STATUS_SUCCESS);
				data.setStatus(true);
				response.setStatus(HttpServletResponse.SC_OK);
				log.debug(EELFLoggerDelegate.debugLogger, "getArtifactsUrl : artifact URL is {} ",
						artifactsUrl);
				
			}else if (artifactsUrl == null) {
				
				data.setContent("Artifact URL is not created");
				data.setResponseBody("Artifact URL is not created");
				data.setResponseCode(String.valueOf(HttpServletResponse.SC_BAD_REQUEST));
				data.setErrorCode("Null Pointer");
				data.setResponseDetail("Artifact URL is not created");
				data.setStatus(false);
			}
			
			
		}catch (AcumosServiceException e) {
			data.setErrorCode(e.getErrorCode());
			data.setResponseDetail(e.getMessage());
			data.setStatus(false);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			log.error(EELFLoggerDelegate.errorLogger,
					"Exception Occurred Fetching Solution Revisions for Docker Upload Controller", e);
		}
		return data;
	}
	
	
	
	
}
