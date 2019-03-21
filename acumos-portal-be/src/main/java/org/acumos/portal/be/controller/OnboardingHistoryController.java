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

import java.lang.invoke.MethodHandles;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.acumos.cds.transport.RestPageRequest;
import org.acumos.portal.be.APINames;
import org.acumos.portal.be.common.JSONTags;
import org.acumos.portal.be.common.JsonRequest;
import org.acumos.portal.be.common.JsonResponse;
import org.acumos.portal.be.common.PagableResponse;
import org.acumos.portal.be.common.RestPageResponseBE;
import org.acumos.portal.be.service.OnboardingHistoryService;
import org.acumos.portal.be.transport.MLStepResult;
import org.acumos.portal.be.transport.MLTask;
import org.acumos.portal.be.transport.RestPageRequestPortal;
import org.acumos.portal.be.util.SanitizeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import io.swagger.annotations.ApiOperation;

@Controller
@RequestMapping("/onboardinghistory")
public class OnboardingHistoryController extends AbstractController {

	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());	

	@Autowired
	private OnboardingHistoryService onboardingHistoryService;

	@ApiOperation(value = "getting tasks  for  User.", response = RestPageResponseBE.class)
	@RequestMapping(value = { APINames.GET_TASKS }, method = RequestMethod.POST, produces = APPLICATION_JSON)
	@ResponseBody
	public PagableResponse<List<MLTask>> getTasks(HttpServletRequest request, @PathVariable("userId") String userId,
			@RequestBody JsonRequest<RestPageRequestPortal> restPageReqPortal, HttpServletResponse response) {

		userId = SanitizeUtils.sanitize(userId);

		log.debug("getTasks");
		PagableResponse<List<MLTask>> data = new PagableResponse<>();
		try {
			data = onboardingHistoryService.getTasks(restPageReqPortal.getBody(), userId);

			log.debug("getTasks : size is {} " + data.getSize());
			data.setResponseDetail("getTasks Successful");
			data.setResponseCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
		} catch (Exception e) {

			data.setResponseCode(JSONTags.TAG_ERROR_CODE);
			data.setResponseDetail("Exception Occurred while Retrieving Tasks");
			log.error("Exception Occurred while Retrieving Tasks", e);
			response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
		}
		return data;
	}

	@ApiOperation(value = "get Step Results for a Task.", response = RestPageResponseBE.class)
	@RequestMapping(value = { APINames.GET_STEP_RESULTS }, method = RequestMethod.POST, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<List<MLStepResult>> getStepResults(HttpServletRequest request,
			@PathVariable("taskId") String taskId, HttpServletResponse response) {

		taskId = SanitizeUtils.sanitize(taskId);

		log.debug(" getStepResults");
		JsonResponse<List<MLStepResult>> data = new JsonResponse<>();
		try {
			List<MLStepResult> responseBody = onboardingHistoryService.getStepResults(Long.valueOf(taskId));
			data.setResponseBody(responseBody);
			data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
			data.setResponseDetail("getStepResults Successful");

		} catch (Exception e) {

			data.setErrorCode(JSONTags.TAG_ERROR_CODE);
			data.setResponseDetail("Exception Occurred while getStepResults");
			log.error("Exception Occurred while getStepResults", e);
			response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
		}
		return data;
	}

}
