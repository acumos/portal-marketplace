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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acumos.portal.be.APINames;
import org.acumos.portal.be.common.JSONTags;
import org.acumos.portal.be.common.JsonResponse;
import org.acumos.portal.be.service.ValidationStatusService;
import org.acumos.portal.be.transport.MLModelValidationCheck;
import org.acumos.portal.be.transport.MLModelValidationStatus;
import org.acumos.portal.be.util.EELFLoggerDelegate;
import org.acumos.portal.be.util.SanitizeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.ApiOperation;

@Controller
@RequestMapping("/")
public class ValidationStatusController  extends AbstractController {

private static final EELFLoggerDelegate log = EELFLoggerDelegate.getLogger(ValidationStatusController.class);
	
	@Autowired
	private ValidationStatusService validationStatusService;
	
	/**
	 * 
	 */
	public ValidationStatusController() {
		// TODO Auto-generated constructor stub
	}

	/*@ApiOperation(value = "Updates the validation status for the given TaskId, solutionId and revision Id in the Database. To be invoked by Validation Backend", response = JsonResponse.class)
    @RequestMapping(value = {APINames.MODEL_VALIDATION_UPDATE},method = RequestMethod.PUT, produces = APPLICATION_JSON)
    @ResponseBody
    public JsonResponse<Object> updateValidationTaskStatus(HttpServletRequest request, @PathVariable("taskId") String taskId, @RequestBody MLModelValidationStatus mlModelValidationStatus,
			HttpServletResponse response) {
		
		taskId = SanitizeUtils.sanitize(taskId);
		
		log.debug(EELFLoggerDelegate.debugLogger, "updateValidationTaskStatus={}", taskId);
		JsonResponse<Object> data = new JsonResponse<>();
		try {
			validationStatusService.updateValidationTaskStatus(taskId, mlModelValidationStatus);
			//generateNotification(notification,userId);
			data.setStatusCode(200);
			data.setResponseDetail("Validation status updated Successfully");
		} catch (Exception e) {
			data.setErrorCode(JSONTags.TAG_ERROR_CODE);
			data.setStatusCode(400);
			data.setResponseDetail("Validation status updated Failed");
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred while updateValidationTaskStatus()", e);
		}
		return data;
	}*/
	
	/*@ApiOperation(value = "Gets the validation status for the given solutionId and revision Id", response = MLModelValidationCheck.class)
    @RequestMapping(value = {APINames.MODEL_VALIDATION},method = RequestMethod.GET, produces = APPLICATION_JSON)
    @ResponseBody
    public JsonResponse<MLModelValidationCheck> getValidationTaskStatus(HttpServletRequest request, @PathVariable("solutionId") String solutionId, @PathVariable("revisionId") String revisionId, 
    		HttpServletResponse response) {
		
		solutionId = SanitizeUtils.sanitize(solutionId);
		revisionId = SanitizeUtils.sanitize(revisionId);
		
		log.debug(EELFLoggerDelegate.debugLogger, "getValidationTaskStatus={}", solutionId, revisionId);
		JsonResponse<MLModelValidationCheck> data = new JsonResponse<>();
		try {
			MLModelValidationCheck status = validationStatusService.getSolutionValidationTaskStatus(solutionId, revisionId);
			if(status != null) {
				data.setResponseBody(status);
				//generateNotification(notification,userId);
				data.setStatusCode(200);
				data.setResponseDetail("Validation status retrieved Successfully");
			} else {
				data.setErrorCode(JSONTags.TAG_ERROR_CODE);
				data.setStatusCode(400);
				data.setResponseDetail("No validation Status updates available");
			}
		} catch (Exception e) {
			data.setErrorCode(JSONTags.TAG_ERROR_CODE);
			data.setStatusCode(400);
			data.setResponseDetail("Validation status retrieval Failed");
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred while getValidationTaskStatus()", e);
		}
		return data;
	}*/
	
}
