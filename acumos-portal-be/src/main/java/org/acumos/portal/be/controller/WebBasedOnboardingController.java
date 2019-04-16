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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ScheduledExecutorService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acumos.cds.domain.MLPTaskStepResult;
import org.acumos.cds.domain.MLPUser;
import org.acumos.portal.be.APINames;
import org.acumos.portal.be.common.JSONTags;
import org.acumos.portal.be.common.JsonRequest;
import org.acumos.portal.be.common.JsonResponse;
import org.acumos.portal.be.common.RestPageResponseBE;
import org.acumos.portal.be.logging.ONAPLogConstants;
import org.acumos.portal.be.service.AsyncServices;
import org.acumos.portal.be.service.MessagingService;
import org.acumos.portal.be.transport.Broker;
import org.acumos.portal.be.transport.MLSolution;
import org.acumos.portal.be.transport.MLStepResult;
import org.acumos.portal.be.transport.RestPageRequestPortal;
import org.acumos.portal.be.transport.UploadSolution;
import org.acumos.portal.be.util.SanitizeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.ApiOperation;

@Controller
@RequestMapping("/webBasedOnBoarding")
public class WebBasedOnboardingController extends AbstractController {

	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());	

	@Autowired
	private AsyncServices asyncService;

	@Autowired
	private MessagingService messagingService;
	

	@ApiOperation(value = "adding Solution for Market Place Catalog.", response = RestPageResponseBE.class)
	@RequestMapping(value = { APINames.ADD_TO_CATALOG }, method = RequestMethod.POST, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<RestPageResponseBE<MLSolution>> addToCatalog(HttpServletRequest request,
			HttpServletResponse response, @RequestHeader("Authorization") String authorization,
			@RequestHeader(value = "provider", required = false) String provider,
			@RequestBody JsonRequest<UploadSolution> restPageReq, @PathVariable("userId") String userId) {

		log.info("addToCatalog");
		JsonResponse<RestPageResponseBE<MLSolution>> data = new JsonResponse<>();
		String uuid = UUID.randomUUID().toString();	
		
		final String requestId = MDC.get(ONAPLogConstants.MDCs.REQUEST_ID);

		if (request.getAttribute("mlpuser") == null) {
			data.setErrorCode(JSONTags.TAG_ERROR_CODE);
			data.setResponseDetail(
					"Exception Occurred OnBoarding Solutions for Market Place Catalog: User Not Logged In");
			log.error(
					"Exception Occurred OnBoarding Solutions for Market Place Catalog: User Not Logged In");
			return data;
		}

		final MLPUser requestUser = (MLPUser) request.getAttribute("mlpuser");
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);

		try {
			if (restPageReq != null) {
				UploadSolution solution = restPageReq.getBody();
				 	
				// this will just call the async service and
				// futher that async service will proceed until the task is not
				// completed.
				// restPageReq.getBody() will get( modelType, modelToolkitType,
				// name) which required to proceed
				Map<String, Object> toReturn =null;
				// String provider = request.getHeader("provider");
				String access_token = authorization;
				try {
					FutureTask<HttpResponse> futureTask_1 = new FutureTask<HttpResponse>(new Callable<HttpResponse>() {
						@Override
						public HttpResponse call() throws FileNotFoundException, ClientProtocolException,
								InterruptedException, IOException {
							MDC.put(ONAPLogConstants.MDCs.REQUEST_ID, requestId);
							String modelName = null;
							String dockerfileURI = null;
							String deploymentEnv = null;
							if (restPageReq.getBody() != null){
								modelName = restPageReq.getBody().getName();
								dockerfileURI = restPageReq.getBody().getDockerfileURI();
								if(restPageReq.getBody().getDeploymentEnv() != null){
									deploymentEnv = restPageReq.getBody().getDeploymentEnv();
								}
							}
							
							
							return (HttpResponse) asyncService.callOnboarding(uuid, requestUser, solution, provider,
									access_token, modelName, dockerfileURI, deploymentEnv);
						}
					});
					executor.execute(futureTask_1);
				} finally {
					executor.shutdown();
				}

				data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
				data.setResponseDetail(uuid);
			}
		} catch (Exception e) {

			data.setErrorCode(JSONTags.TAG_ERROR_CODE);
			data.setResponseDetail("Exception Occurred OnBoarding Solutions for Market Place Catalog");
			log.error(
					"Exception Occurred OnBoarding Solutions for Market Place Catalog", e);
		}
		return data;
	}

	@ApiOperation(value = "getting message for the OnBoarded Solution.", response = RestPageResponseBE.class)
	@RequestMapping(value = { APINames.MESSAGING_STATUS }, method = RequestMethod.POST, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<List<MLStepResult>> messagingStatus(@PathVariable("userId") String userId,
			@PathVariable("trackingId") String trackingId) {

		userId = SanitizeUtils.sanitize(userId);
		trackingId = SanitizeUtils.sanitize(trackingId);

		log.debug("messagingStatus");
		JsonResponse<List<MLStepResult>> data = new JsonResponse<>();

		try {

			List<MLStepResult> responseBody = messagingService.callOnBoardingStatusList(userId, trackingId);
			data.setResponseBody(responseBody);
			data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
			data.setResponseDetail("Solutions OnBoarded Successfully");

		} catch (Exception e) {

			data.setErrorCode(JSONTags.TAG_ERROR_CODE);
			data.setResponseDetail(
					"Exception Occurred while providing Status of the Solutions OnBoarded for Market Place Catalog");
			log.error(
					"Exception Occurred while providing Status of the Solutions OnBoarded for Market Place Catalog", e);
		}
		return data;
	}

	/**
	 * 
	 * @param stepResult
	 * @param response
	 * @return
	 */
	@ApiOperation(value = "Create StepResult", response = MLPTaskStepResult.class)
	@RequestMapping(value = { APINames.CREATE_STEP_RESULT }, method = RequestMethod.PUT, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<MLPTaskStepResult> createStepResult(@RequestBody MLPTaskStepResult stepResult,
			HttpServletResponse response) {

		JsonResponse<MLPTaskStepResult> data = new JsonResponse<>();
		try {
			if (stepResult != null) {
				MLPTaskStepResult result = messagingService.createStepResult(stepResult);
				if (result != null) {
					data.setResponseBody(result);
					data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
					data.setResponseDetail("Step result created Successfully");
					log.debug("Step result created Successfully :  ");
				} else {
					data.setErrorCode(JSONTags.TAG_ERROR_CODE_FAILURE);
					data.setResponseDetail("Error occured while createStepResult");
					log.error( "Error Occurred createStepResult :");
				}
			} else {
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_FAILURE);
				data.setResponseDetail("Error occured while createStepResult");
				log.error( "Error Occurred createStepResult :");
			}
		} catch (Exception e) {
			data.setErrorCode(JSONTags.TAG_ERROR_CODE_EXCEPTION);
			data.setResponseDetail("Exception occured while createStepResult");
			log.error( "Exception Occurred createStepResult :", e);
		}
		return data;
	}

	/**
	 * 
	 * @param stepResult
	 * @param response
	 * @return
	 */
	@ApiOperation(value = "Create StepResult", response = MLPTaskStepResult.class)
	@RequestMapping(value = { APINames.UPDATE_STEP_RESULT }, method = RequestMethod.PUT, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<MLPTaskStepResult> updateStepResult(@RequestBody MLPTaskStepResult stepResult,
			HttpServletResponse response) {

		JsonResponse<MLPTaskStepResult> data = new JsonResponse<>();
		try {
			if (stepResult != null) {
				messagingService.updateStepResult(stepResult);
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
				data.setResponseDetail("Step result updated Successfully");
				log.debug("Step result updated Successfully :  ");
			} else {
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_FAILURE);
				data.setResponseDetail("Error occured while updateStepResult");
				log.error( "Error Occurred updateStepResult :");
			}
		} catch (Exception e) {
			data.setErrorCode(JSONTags.TAG_ERROR_CODE_EXCEPTION);
			data.setResponseDetail("Exception occured while updateStepResult");
			log.error( "Exception Occurred updateStepResult :", e);
		}
		return data;
	}

	@ApiOperation(value = "Create StepResult", response = MLPTaskStepResult.class)
	@RequestMapping(value = { APINames.DELETE_STEP_RESULT }, method = RequestMethod.DELETE, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<MLPTaskStepResult> deleteStepResult(HttpServletRequest request,
			@PathVariable("userId") Long stepResultId, HttpServletResponse response) {

		JsonResponse<MLPTaskStepResult> data = new JsonResponse<>();
		try {
			if (stepResultId != null) {
				messagingService.deleteStepResult(stepResultId);
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
				data.setResponseDetail("Step result deleted Successfully");
				log.debug("Step result deleted Successfully :  ");
			} else {
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_FAILURE);
				data.setResponseDetail("Error occured while deleteStepResult");
				log.error( "Error Occurred deleteStepResult :");
			}
		} catch (Exception e) {
			data.setErrorCode(JSONTags.TAG_ERROR_CODE_EXCEPTION);
			data.setResponseDetail("Exception occured while deleteStepResult");
			log.error( "Exception Occurred deleteStepResult :", e);
		}
		return data;
	}

	@ApiOperation(value = "Searching step result with solution id", response = MLPTaskStepResult.class)
	@RequestMapping(value = { APINames.SEARCH_STEP_RESULT }, method = RequestMethod.GET, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<List<MLPTaskStepResult>> findStepresultBySolutionId(
			@PathVariable("solutionId") String solutionId, @PathVariable("revisionId") String revisionId) {

		solutionId = SanitizeUtils.sanitize(solutionId);
		revisionId = SanitizeUtils.sanitize(revisionId);

		JsonResponse<List<MLPTaskStepResult>> data = new JsonResponse<>();
		if (solutionId != null) {
			try {
				List<MLPTaskStepResult> mlpStepresult = messagingService.findStepresultBySolutionId(solutionId,
						revisionId);
				if (mlpStepresult != null) {
					data.setResponseBody(mlpStepresult);
					data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
					data.setResponseDetail("Step result fetched Successfully");
					log.debug("Step result fetched Successfully :  ");
				}
			} catch (Exception e) {
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_EXCEPTION);
				data.setResponseDetail("Exception occured while searchStepResults");
				log.error( "Exception Occurred searchStepResults :", e);
			}
		}
		return data;
	}

	@ApiOperation(value = "dummy api for Broker.", response = RestPageResponseBE.class)
	@RequestMapping(value = { APINames.BROKER }, method = RequestMethod.POST, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<Broker> messagingStatus(@RequestBody JsonRequest<Broker> brokerDetail) {

		log.debug("broker details");

		JsonResponse<Broker> data = new JsonResponse<>();

		try {
			Broker responseBody = new Broker();
			responseBody.setResponseName("test_name");
			responseBody.setResponseContent("test_COntent");
			data.setResponseBody(responseBody);

		} catch (Exception e) {

			data.setErrorCode(JSONTags.TAG_ERROR_CODE);
			data.setResponseDetail(
					"Exception Occurred while providing Status of the Solutions OnBoarded for Market Place Catalog");
			log.error(
					"Exception Occurred while providing Status of the Solutions OnBoarded for Market Place Catalog", e);
		}
		return data;
	}

	@RequestMapping(value = { APINames.CONVERT_TO_ONAP }, method = RequestMethod.POST, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<List<MLStepResult>> convertToOnap(@PathVariable("solutionId") String solutionId,
			@PathVariable("revisionId") String revisionId, @PathVariable("userId") String userId,
			@PathVariable("modName") String modName) {
		JsonResponse<List<MLStepResult>> data = new JsonResponse<>();
		Boolean isONAPCompatible = false;
		String tracking_id = UUID.randomUUID().toString();
		final String requestId = MDC.get(ONAPLogConstants.MDCs.REQUEST_ID);

		isONAPCompatible = asyncService.checkONAPCompatible(solutionId, revisionId, userId, tracking_id);

		if (isONAPCompatible) {
			ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
			FutureTask<HttpResponse> futureTask_1 = new FutureTask<HttpResponse>(new Callable<HttpResponse>() {
				@Override
				public HttpResponse call()
						throws FileNotFoundException, ClientProtocolException, InterruptedException, IOException {
					MDC.put(ONAPLogConstants.MDCs.REQUEST_ID, requestId);
					return (HttpResponse) asyncService.convertSolutioToONAP(solutionId, revisionId, userId, tracking_id,
							modName);
				}
			});
			executor.execute(futureTask_1);
			executor.shutdown();
		} else {
			// Create failed step result
		}

		data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
		data.setResponseDetail(tracking_id.toString());
		return data;
	}

	@RequestMapping(value = { APINames.CHECK_ONAP_COMPATIBLE }, method = RequestMethod.GET, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<String> checkONAPCompatible(@PathVariable("solutionId") String solutionId,
			@PathVariable("revisionId") String revisionId) {

		solutionId = SanitizeUtils.sanitize(solutionId);
		revisionId = SanitizeUtils.sanitize(revisionId);

		JsonResponse<String> data = new JsonResponse<>();
		Boolean isONAPCompatible = false;

		isONAPCompatible = asyncService.checkONAPCompatible(solutionId, revisionId);

		data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
		data.setResponseDetail(isONAPCompatible.toString());
		return data;
	}
}
