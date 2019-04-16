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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acumos.cds.domain.MLPSolution;
import org.acumos.cds.domain.MLPSolutionRevision;
import org.acumos.cds.domain.MLPUser;
import org.acumos.cds.transport.RestPageRequest;
import org.acumos.cds.transport.RestPageResponse;
import org.acumos.portal.be.APINames;
import org.acumos.portal.be.common.JSONTags;
import org.acumos.portal.be.common.JsonRequest;
import org.acumos.portal.be.common.JsonResponse;
import org.acumos.portal.be.common.RestPageRequestBE;
import org.acumos.portal.be.common.RestPageResponseBE;
import org.acumos.portal.be.common.exception.AcumosServiceException;
import org.acumos.portal.be.service.AsyncServices;
import org.acumos.portal.be.service.MarketPlaceCatalogService;
import org.acumos.portal.be.service.OnboardingDockerService;
import org.acumos.portal.be.transport.MLSolution;
import org.acumos.portal.be.transport.UploadSolution;
import org.acumos.portal.be.util.DockerUploadResult;
import org.acumos.portal.be.util.PortalUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.ApiOperation;

@Controller
@RequestMapping("/onboardingDocker")
public class OnboardingDockerUploadController<T>  extends AbstractController {
	
	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	@Autowired
	private OnboardingDockerService onboardingDockerService;
	
	@Autowired
	private MarketPlaceCatalogService catalogService;
	
	@Autowired
	private AsyncServices asyncService;
	

	@ApiOperation(value = "Get search solution according to queryparamters sent.", response = MLSolution.class, responseContainer = "List")
	@RequestMapping(value = { APINames.DOCKER_SEARCH_SOLUTION }, method = RequestMethod.POST, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<RestPageResponse<MLPSolution>> getSearchSolutions(HttpServletRequest request, HttpServletResponse response,
			@RequestBody JsonRequest<RestPageRequestBE> restPageReq) {
		RestPageResponse<MLPSolution> mlSolutions = null;
		JsonResponse<RestPageResponse<MLPSolution>> data = new JsonResponse<>();
		try {
			
			if (!PortalUtils.isEmptyOrNullString(restPageReq.getBody().getSearchTerm())) {					
				if(restPageReq.getBody().getDescription().equals("relatedSearch")) {
					
					mlSolutions = onboardingDockerService.getRelatedSolution(restPageReq) ;	
				}else if(restPageReq.getBody().getDescription().equals("exactSearch")) {					
					Map<String, Object> solutoinNameParameter =  new HashMap<>();
					solutoinNameParameter.put("name", restPageReq.getBody().getSearchTerm());
					solutoinNameParameter.put("userId", restPageReq.getBody().getUserId());
					mlSolutions = catalogService.getMLPSolutionBySolutionName(solutoinNameParameter, false, new RestPageRequest());
					data.setResponseBody(mlSolutions);
				}									
			} else {				
				data.setResponseBody(mlSolutions);
				populateMsg(null, 
						String.valueOf(HttpServletResponse.SC_BAD_REQUEST), 
						JSONTags.TAG_ERROR_CODE_FAILURE, 
						"Solution Search Term is Empty", 
						false);
				
				return data;
			}
			
			if (mlSolutions != null) {				
				data.setResponseBody(mlSolutions);
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
				data.setResponseDetail("Solutions fetched Successfully");
				log.debug("getSearchSolutions  at portal using docker upload: size is {} ", mlSolutions.getSize());				
			} else  {				 
				data.setResponseBody(mlSolutions);
				populateMsg(null, 
						String.valueOf(HttpServletResponse.SC_BAD_REQUEST), 
						JSONTags.TAG_ERROR_CODE_FAILURE, 
						"Solutions fetched are Empty", 
						false);
				return data;
			}
		} catch (AcumosServiceException e) {
			
			data.setErrorCode(e.getErrorCode());
			data.setResponseDetail(e.getMessage());
			log.error("Exception Occurred Fetching Solutions for Onboarding Docker Upload Controller at portal using docker upload" + e.getMessage());
		}
		return data;
	}
	
	@ApiOperation(value = "Gets Artifacts URL based on Solution Revision from the Catalog of the local Acumos Instance .", responseContainer = "String")
	@RequestMapping(value = { APINames.ARTIFACTS_URL }, method = RequestMethod.POST, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<String> getArtifactsUrl(HttpServletRequest request,
			HttpServletResponse response, 					
			@RequestHeader(value = "provider", required = false) String provider,
			@RequestHeader("Authorization") String authorization,
			@RequestBody JsonRequest<UploadSolution> restPageReq			
			) {
		
		JsonResponse<String> data = new JsonResponse<String>();
		List<MLPSolutionRevision> solutionRevisions = null;
		 
		try {
			String artifactsUrl = createSolutionsUsingDockerURL(request, response, provider, authorization, restPageReq);
			
			if(artifactsUrl != null){
				data.setContent(artifactsUrl);				
				data.setResponseBody(artifactsUrl);
				data.setResponseCode(String.valueOf(HttpServletResponse.SC_OK));
				data.setResponseDetail(JSONTags.TAG_STATUS_SUCCESS);
				data.setStatus(true);
				response .setStatus(HttpServletResponse.SC_OK);
				log.debug("getArtifactsUrl : artifact URL is {} ",
						artifactsUrl);				
			} else if (artifactsUrl == null) {				
				data.setContent("Artifact URL is not created");
				data.setResponseBody("Artifact URL is not created");
				data.setResponseCode(String.valueOf(HttpServletResponse.SC_BAD_REQUEST));
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_FAILURE);
				data.setResponseDetail("Artifact URL is not created");
				data.setStatus(false);
			}				
		} catch (Exception e) {
			data.setErrorCode(JSONTags.TAG_ERROR_CODE_FAILURE);
			data.setResponseDetail(e.getMessage());
			data.setStatus(false);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			log.error("Exception Occurred Fetching Solution Revisions for Docker Upload Controller", e);
		}
		return data;
	}	
	
	private String createSolutionsUsingDockerURL(HttpServletRequest request, 
											   HttpServletResponse response,
											   String provider,
											   String authorization,
											   JsonRequest<UploadSolution> restPageReq) {
		
		log.info("createSolutionsUsingDockerURL");			
		String uuid = UUID.randomUUID().toString();
		//final String requestId = MDC.get(ONAPLogConstants.MDCs.REQUEST_ID);
		final MLPUser requestUser = (MLPUser) request.getAttribute("mlpuser");
		String dockerfileURIStr = null;
		try {
			if (restPageReq != null) {
				UploadSolution solution = restPageReq.getBody();
				String modelName = null;
				String dockerfileURI = restPageReq.getBody().getDockerfileURI();
				if (restPageReq.getBody() != null){
					modelName = restPageReq.getBody().getName();					
				}
			    String devEnvironment = null;
			    DockerUploadResult dockerUploadResult = new DockerUploadResult();
			    asyncService.callOnboarding(uuid, requestUser, solution, provider, authorization, modelName , dockerfileURI, devEnvironment,dockerUploadResult);
			    if(dockerUploadResult !=null && dockerUploadResult.getDockerArtifcatUrl()!=null) {
			    	dockerfileURIStr = dockerUploadResult.getDockerArtifcatUrl();
			    }
				
			} else {
				 populateMsg( null, 
						 String.valueOf(HttpServletResponse.SC_BAD_REQUEST),
						 JSONTags.TAG_ERROR_CODE_FAILURE,
						 "Request Object solutionId is not created",
							false);			
			}
		}catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			log.error("Exception Occurred createSolutionsUsingDockerURL for Docker Upload Controller", e);
		}
		return dockerfileURIStr;
	}
	
	private void populateMsg(JsonResponse<RestPageResponseBE<MLSolution>> data, 
									String responseCode,
									String errorCode,
									String responseDetail,
									boolean status) {
		data.setResponseCode(responseCode);
		data.setErrorCode(JSONTags.TAG_ERROR_CODE_FAILURE);
		data.setResponseDetail("Search Term is empty");
		data.setStatus(false);
	}
}