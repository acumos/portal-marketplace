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

import java.util.Date;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acumos.cds.MessageSeverityCode;
import org.acumos.cds.ValidationStatusCode;
import org.acumos.cds.domain.MLPNotification;
import org.acumos.portal.be.APINames;
import org.acumos.portal.be.common.JSONTags;
import org.acumos.portal.be.common.JsonResponse;
import org.acumos.portal.be.service.MarketPlaceCatalogService;
import org.acumos.portal.be.service.NotificationService;
import org.acumos.portal.be.service.PublishSolutionService;
import org.acumos.portal.be.transport.MLNotification;
import org.acumos.portal.be.transport.MLSolution;
import org.acumos.portal.be.transport.ResponseVO;
import org.acumos.portal.be.util.EELFLoggerDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.ApiOperation;

@Controller
@RequestMapping("/")
public class PublishSolutionServiceController extends AbstractController {

	private static final EELFLoggerDelegate log = EELFLoggerDelegate.getLogger(PublishSolutionServiceController.class);
	
	@Autowired
	private PublishSolutionService publishSolutionService;
	
	@Autowired
	private NotificationService notificationService;
	
	@Autowired
	private MarketPlaceCatalogService catalogService;
	
	/**
	 * 
	 */
	public PublishSolutionServiceController() {
		// TODO Auto-generated constructor stub
	}

	@ApiOperation(value = "Publishes a given SolutionId for userId with selected visibility.", response = ResponseVO.class)
    @RequestMapping(value = {APINames.PUBLISH},method = RequestMethod.PUT, produces = APPLICATION_JSON)
    @ResponseBody
    public JsonResponse<Object> publishSolution(HttpServletRequest request, @PathVariable("solutionId") String solutionId, @RequestParam("visibility") String visibility,
			@RequestParam("userId") String userId, @RequestParam("revisionId") String revisionId, HttpServletResponse response) {
		log.debug(EELFLoggerDelegate.debugLogger, "publishSolution={}", solutionId, visibility);
		JsonResponse<Object> data = new JsonResponse<>();
		UUID trackingId = UUID.randomUUID();
		try {

			//Check for the unique name in the market place before publishing.
			if (!publishSolutionService.checkUniqueSolName(solutionId)) {
				data.setErrorCode(JSONTags.TAG_ERROR_CODE);
				data.setResponseDetail("Model name is not unique. Please update model name before publishing");
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				return data;
			}
			
			boolean published = publishSolutionService.publishSolution(solutionId, visibility, userId, revisionId, trackingId);
			// code to create notification
            MLPNotification notificationObj = new MLPNotification();
            notificationObj.setMsgSeverityCode(MessageSeverityCode.ME.toString());
            String notificationmsg = null;
			MLSolution solutionDetail = catalogService.getSolution(solutionId);
			if (published || ValidationStatusCode.PS.toString().equalsIgnoreCase(solutionDetail.getValidationStatusCode())) {			
				
				if (visibility.equals("PB")){
					notificationmsg = solutionDetail.getName() + " published to public marketplace";
				}else if (visibility.equals("OR")){
					notificationmsg = solutionDetail.getName() + " published to company marketplace";
				}else{
					notificationmsg = solutionDetail.getName() + " published to marketplace";
				}
			}else{
				if (visibility.equals("PB")){
					notificationmsg = "Failed to publish " +solutionDetail.getName() + " to public marketplace";
				}else if (visibility.equals("OR")){
					notificationmsg = "Failed to publish " +solutionDetail.getName() + " to company marketplace";
				}else{
					notificationmsg = "Failed to publish " +solutionDetail.getName() + " to marketplace";
				}
			}
			notificationObj.setMessage(notificationmsg);
			notificationObj.setTitle(notificationmsg);
			notificationService.generateNotification(notificationObj, userId);
			data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
			data.setResponseDetail(trackingId.toString());
		} catch (Exception e) {
			data.setErrorCode(JSONTags.TAG_ERROR_CODE);
			data.setResponseDetail("Exception Occurred while Publishing Model");
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred while publishSolution()", e);
		}
		return data;
	}
	
	@ApiOperation(value = "Unpublishes a given SolutionId for userId with selected visibility.", response = ResponseVO.class)
    @RequestMapping(value = {APINames.UNPUBLISH},method = RequestMethod.PUT, produces = APPLICATION_JSON)
    @ResponseBody
    public JsonResponse<Object> unpublishSolution(HttpServletRequest request, @PathVariable("solutionId") String solutionId, @RequestParam("visibility") String visibility,
    		@RequestParam("userId") String userId, HttpServletResponse response) {
		log.debug(EELFLoggerDelegate.debugLogger, "unpublishSolution={}", solutionId, visibility);
		 JsonResponse<Object> data = new JsonResponse<>();
		try {
			//TODO As of now it does not check if User Account already exists. Need to first check if the account exists in DB
			publishSolutionService.unpublishSolution(solutionId, visibility, userId);
			data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
			data.setResponseDetail("Solutions unpublished Successfully");
		} catch (Exception e) {
			 data.setErrorCode(JSONTags.TAG_ERROR_CODE);
 			 data.setResponseDetail("Exception Occurred while unpublishSolution()");
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred while unpublishSolution()", e);
		}
		return data;
	}
}
