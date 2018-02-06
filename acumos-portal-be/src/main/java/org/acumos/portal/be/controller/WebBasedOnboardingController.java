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
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;

import org.acumos.portal.be.APINames;
import org.acumos.portal.be.common.JSONTags;
import org.acumos.portal.be.common.JsonRequest;
import org.acumos.portal.be.common.JsonResponse;
import org.acumos.portal.be.common.RestPageResponseBE;
import org.acumos.portal.be.service.AsyncServices;
import org.acumos.portal.be.service.MessagingService;
import org.acumos.portal.be.transport.MLSolution;
import org.acumos.portal.be.transport.MLStepResult;
import org.acumos.portal.be.transport.UploadSolution;
import org.acumos.portal.be.util.EELFLoggerDelegate;
import org.apache.http.client.ClientProtocolException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
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
public class WebBasedOnboardingController  extends AbstractController {

	private static final EELFLoggerDelegate log = EELFLoggerDelegate
			.getLogger(MarketPlaceCatalogServiceController.class);


	@Autowired
	private AsyncServices asyncService;

	@Autowired
	private MessagingService messagingService;
	
	
	@Async
	@ApiOperation(value = "adding Solution for Market Place Catalog.", response = RestPageResponseBE.class)
	@RequestMapping(value = { APINames.ADD_TO_CATALOG}, method = RequestMethod.POST, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<RestPageResponseBE<MLSolution>> addToCatalog(@RequestHeader("Authorization") String authorization, @RequestHeader(value="provider", required=false) String provider ,@RequestBody JsonRequest<UploadSolution> restPageReq, @PathVariable("userId") String userId ) {
		
		log.debug(EELFLoggerDelegate.debugLogger, "addToCatalog");
		JsonResponse<RestPageResponseBE<MLSolution>> data = new JsonResponse<>();	    
	    
		try {
			if (restPageReq != null) {
				UploadSolution solution = restPageReq.getBody();
				//this will just call the async service and 
				//futher that async service will proceed untill the task is not completed.
				//restPageReq.getBody() will get( modelType, modelToolkitType, name) which required to proceed
				//String provider = request.getHeader("provider");
				String access_token = authorization;
				asyncService.callOnboarding(userId, solution, provider, access_token);
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
				data.setResponseDetail("Solutions OnBoarded Successfully");
			}
			/*if (mlSolutions != null) {
				
				data.setResponseBody(mlSolutions);
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
				data.setResponseDetail("Solutions OnBoarded Successfully");
			}*/
		} catch (FileNotFoundException e) {
			
			data.setErrorCode(JSONTags.TAG_ERROR_CODE);
			data.setResponseDetail("FileNotFoundException Occurred OnBoarding Solutions for Market Place Catalog");
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred OnBoarding Solutions for Market Place Catalog",
					e);
		}catch (ClientProtocolException e) {
			
			data.setErrorCode(JSONTags.TAG_ERROR_CODE);
			data.setResponseDetail("ClientProtocolException Occurred OnBoarding Solutions for Market Place Catalog");
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred OnBoarding Solutions for Market Place Catalog",
					e);
		}catch (InterruptedException e) {
			
			data.setErrorCode(JSONTags.TAG_ERROR_CODE);
			data.setResponseDetail("InterruptedException Occurred OnBoarding Solutions for Market Place Catalog");
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred OnBoarding Solutions for Market Place Catalog",
					e);
		}catch (ConnectException e) {
			
			data.setErrorCode(JSONTags.TAG_ERROR_CODE);
			data.setResponseDetail("ConnectException Occurred OnBoarding Solutions for Market Place Catalog");
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred OnBoarding Solutions for Market Place Catalog",
					e);
		}catch (Exception e) {
			
			data.setErrorCode(JSONTags.TAG_ERROR_CODE);
			data.setResponseDetail("Exception Occurred OnBoarding Solutions for Market Place Catalog");
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred OnBoarding Solutions for Market Place Catalog",
					e);
		}
		return data;
	}
	
	
	
	@ApiOperation(value = "getting message for the OnBoarded Solution.", response = RestPageResponseBE.class)
	@RequestMapping(value = { APINames.MESSAGING_STATUS}, method = RequestMethod.POST, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<List<MLStepResult>> messagingStatus(@PathVariable("userId") String userId, @PathVariable("trackingId") String trackingId) {
		
		log.debug(EELFLoggerDelegate.debugLogger, "messagingStatus");
		JsonResponse<List<MLStepResult>> data = new JsonResponse<>();
	    	     
		try {
			 		
			List<MLStepResult> responseBody =  messagingService.callOnBoardingStatusList(userId, trackingId);
			data.setResponseBody(responseBody);			 
			data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
			data.setResponseDetail("Solutions OnBoarded Successfully");
			 			 
		}catch (Exception e) {
			
			data.setErrorCode(JSONTags.TAG_ERROR_CODE);
			data.setResponseDetail("Exception Occurred while providing Status of the Solutions OnBoarded for Market Place Catalog");
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred while providing Status of the Solutions OnBoarded for Market Place Catalog",
					e);
		}
		return data;
	}
	
	
	
	/*@ApiOperation(value = "getting message for the OnBoarded Solution.", response = MLStepResult.class)
	@RequestMapping(value = { APINames.MESSAGING_STATUS}, method = RequestMethod.POST, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<MLStepResult> messagingStatus(@PathVariable("userId") String userId, @PathVariable("trackingId") String trackingId) {
		
		log.debug(EELFLoggerDelegate.debugLogger, "messagingStatus");
		JsonResponse<MLStepResult> data = new JsonResponse<>();	     
		try {			 
			
			MLStepResult responseBody =  null;
			responseBody = messagingService.callOnBoardingStatus(userId, trackingId);
			data.setResponseBody(responseBody);			
			data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
			data.setResponseDetail("Solutions OnBoarded Successfully");		 
			 
		}catch (Exception e) {
			
			data.setErrorCode(JSONTags.TAG_ERROR_CODE);
			data.setResponseDetail("Exception Occurred while providing Status of the Solutions OnBoarded for Market Place Catalog");
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred while providing Status of the Solutions OnBoarded for Market Place Catalog",
					e);
		}
		return data;
	}*/
}