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

import java.lang.invoke.MethodHandles;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.acumos.cds.domain.MLPNotification;
import org.acumos.portal.be.APINames;
import org.acumos.portal.be.common.CommonConstants;
import org.acumos.portal.be.common.CredentialsService;
import org.acumos.portal.be.common.JSONTags;
import org.acumos.portal.be.common.JsonResponse;
import org.acumos.portal.be.service.NotificationService;
import org.acumos.portal.be.service.PublishSolutionService;
import org.acumos.portal.be.transport.ResponseVO;
import org.acumos.portal.be.util.SanitizeUtils;
import org.acumos.securityverification.domain.Workflow;
import org.acumos.securityverification.utils.SVConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());	
	
	@Autowired
	private PublishSolutionService publishSolutionService;
	
	@Autowired
	private NotificationService notificationService;
	
	@Autowired
	CredentialsService credentialService;

	private static final String MSG_SEVERITY_ME = "ME";
	/**
	 * 
	 */
	public PublishSolutionServiceController() {
		// TODO Auto-generated constructor stub
	}

	@ApiOperation(value = "Publishes a given SolutionId for userId with selected visibility.", response = ResponseVO.class)
    @RequestMapping(value = {APINames.PUBLISH},method = RequestMethod.PUT, produces = APPLICATION_JSON)
    @ResponseBody
    public JsonResponse<Object> publishSolution(HttpServletRequest request, @PathVariable("solutionId") String solutionId, @RequestParam("ctlg") String catalogId, @RequestParam("visibility") String visibility,
			@RequestParam("userId") String userId, @RequestParam("revisionId") String revisionId, HttpServletResponse response) {
		
		solutionId = SanitizeUtils.sanitize(solutionId);
		revisionId = SanitizeUtils.sanitize(revisionId);
		String loggedInUserName  = credentialService.getLoggedInUserName();
		
		log.debug("publishSolution={}", solutionId, visibility);
		log.info("publishSolution={}", solutionId, visibility);
		JsonResponse<Object> data = new JsonResponse<>();
		UUID trackingId = UUID.randomUUID();
		try {
			String workflowId = (visibility.equalsIgnoreCase(CommonConstants.PUBLIC)
					? SVConstants.PUBLISHPUBLIC : SVConstants.PUBLISHCOMPANY);
			Workflow workflow = performSVScan(solutionId, revisionId, workflowId, loggedInUserName).get();
			if (!workflow.isWorkflowAllowed()) {
				data.setErrorCode((isReasonInfo(workflow.getReason())) ? JSONTags.TAG_INFO_SV : JSONTags.TAG_ERROR_SV);
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				data.setResponseDetail(workflow.getReason());
				log.error("SV failure during publish: " + workflow.getReason());
				// Check for the unique name in the market place before publishing.
			} else if (!publishSolutionService.checkUniqueSolName(solutionId)) {
				data.setErrorCode(JSONTags.TAG_ERROR_CODE);
				data.setResponseDetail("Model name is not unique. Please update model name before publishing");
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			} else {
				String publishStatus = publishSolutionService.publishSolution(solutionId, visibility, userId, revisionId, catalogId,trackingId);
				// code to create notification
	            MLPNotification notificationObj = new MLPNotification();
	            notificationObj.setMsgSeverityCode(MSG_SEVERITY_ME);
	
				notificationObj.setMessage(publishStatus);
				notificationObj.setTitle(publishStatus);
				notificationService.generateNotification(notificationObj, userId);
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
				data.setResponseDetail(trackingId.toString());
			}
		} catch (Exception e) {
			data.setErrorCode(JSONTags.TAG_ERROR_CODE_EXCEPTION);
			data.setResponseDetail("Exception Occurred while Publishing Model");
			log.error( "Exception Occurred while publishSolution()", e);
		}
		return data;
	}
	
	@ApiOperation(value = "Unpublishes a given SolutionId for userId with selected visibility.", response = ResponseVO.class)
    @RequestMapping(value = {APINames.UNPUBLISH},method = RequestMethod.PUT, produces = APPLICATION_JSON)
    @ResponseBody
    public JsonResponse<Object> unpublishSolution(HttpServletRequest request, @PathVariable("solutionId") String solutionId, @RequestParam("ctlg") String catalogId,
    		@RequestParam("userId") String userId,@RequestParam( value = "publishRequestId", required = false, defaultValue = "0" ) long publishRequestId, HttpServletResponse response) {
		
		solutionId = SanitizeUtils.sanitize(solutionId);
		
		log.debug("unpublishSolution={}", solutionId, catalogId);
		 JsonResponse<Object> data = new JsonResponse<>();
		try {
			//TODO As of now it does not check if User Account already exists. Need to first check if the account exists in DB
			String unpublishStatus=publishSolutionService.unpublishSolution(solutionId, catalogId, userId,publishRequestId);
			data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
			data.setResponseDetail("Solutions unpublished Successfully");
			MLPNotification notificationObj = new MLPNotification();
            notificationObj.setMsgSeverityCode(MSG_SEVERITY_ME);

			notificationObj.setMessage(unpublishStatus);
			notificationObj.setTitle(unpublishStatus);
			notificationService.generateNotification(notificationObj, userId);
		} catch (Exception e) {
			 data.setErrorCode(JSONTags.TAG_ERROR_CODE);
 			 data.setResponseDetail("Exception Occurred while unpublishSolution()");
			log.error( "Exception Occurred while unpublishSolution()", e);
		}
		return data;
	}
}
