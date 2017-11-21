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

import javax.servlet.http.HttpServletRequest;

import org.acumos.portal.be.APINames;
import org.acumos.portal.be.common.JSONTags;
import org.acumos.portal.be.common.JsonRequest;
import org.acumos.portal.be.common.JsonResponse;
import org.acumos.portal.be.common.RestPageRequestBE;
import org.acumos.portal.be.common.RestPageResponseBE;
import org.acumos.portal.be.service.AsyncServices;
import org.acumos.portal.be.transport.MLSolution;
import org.acumos.portal.be.transport.UploadSolution;
import org.acumos.portal.be.util.EELFLoggerDelegate;
import org.apache.http.client.ClientProtocolException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.ApiOperation;

/**
 * @author Vineet Tripathi
 *
 */
@Controller
@RequestMapping("/webBasedOnBoarding")
public class WebBasedOnboardingController  extends AbstractController {

	private static final EELFLoggerDelegate log = EELFLoggerDelegate
			.getLogger(MarketPlaceCatalogServiceController.class);


	@Autowired
	private AsyncServices asyncService;

	
	@Async
	@CrossOrigin
	@ApiOperation(value = "adding Solution for Market Place Catalog.", response = RestPageResponseBE.class)
	@RequestMapping(value = { APINames.ADD_TO_CATALOG}, method = RequestMethod.POST, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<RestPageResponseBE<MLSolution>> addToCatalog(@RequestHeader("Authorization") String authorization, @RequestHeader(value="provider", required=false) String provider ,@RequestBody JsonRequest<UploadSolution> restPageReq, @PathVariable("userId") String userId ) {
		
		log.debug(EELFLoggerDelegate.debugLogger, "addToCatalog");
		RestPageResponseBE<MLSolution> mlSolutions = null;
		JsonResponse<RestPageResponseBE<MLSolution>> data = new JsonResponse<>();
	    
	    System.out.println("Execute method asynchronously - "+ Thread.currentThread().getName());
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
	
}
