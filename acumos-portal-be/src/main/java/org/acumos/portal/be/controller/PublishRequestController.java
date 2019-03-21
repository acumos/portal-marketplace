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

import org.acumos.cds.domain.MLPPublishRequest;
import org.acumos.portal.be.common.JSONTags;
import org.acumos.portal.be.common.JsonRequest;
import org.acumos.portal.be.common.JsonResponse;
import org.acumos.portal.be.common.PagableResponse;
import org.acumos.portal.be.common.exception.AcumosServiceException;
import org.acumos.portal.be.service.PublishRequestService;
import org.acumos.portal.be.transport.MLPublishRequest;
import org.acumos.portal.be.transport.ResponseVO;
import org.acumos.portal.be.transport.RestPageRequestPortal;
import org.acumos.portal.be.util.SanitizeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.ApiOperation;

@Controller
@RequestMapping("/publish/request")
public class PublishRequestController extends AbstractController {
	
	@Autowired
	private PublishRequestService publishRequestService;
	
	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());	

	/**
	 * 
	 */
	public PublishRequestController() {
		// TODO Auto-generated constructor stub
	}
	
	@ApiOperation(value = "Get Latest publish requests for the revision Id.", response = JsonResponse.class)
    @RequestMapping(value = {"/search/revision/{revisionId}"},method = RequestMethod.GET, produces = APPLICATION_JSON)
    @ResponseBody
    public JsonResponse<MLPublishRequest> searchPublishRequestByRevId(HttpServletRequest request, @PathVariable String revisionId, HttpServletResponse response) {
		
		revisionId = SanitizeUtils.sanitize(revisionId);
		
		JsonResponse<MLPublishRequest> data = new JsonResponse<>();
		try {
			MLPublishRequest mlPublishRequest = publishRequestService.searchPublishRequestByRevId(revisionId);
			data.setResponseBody(mlPublishRequest);
			data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
			data.setResponseDetail("Publish Request fetched Successfully.");
			log.debug("searchPublishRequestByRevId: size is {} ", mlPublishRequest);
		} catch (Exception e) {
			e.printStackTrace();
			data.setErrorCode(JSONTags.TAG_ERROR_CODE);
			data.setResponseDetail("Exception occured while fetching the request");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			log.error("Exception Occurred Fetching Publish Request", e);
		}
		return data;
	}
	
	@ApiOperation(value = "Get paginated publish requests.", response = ResponseVO.class, responseContainer = "List")
    @RequestMapping(value = {""},method = RequestMethod.POST, produces = APPLICATION_JSON)
	@PreAuthorize("hasAuthority(T(org.acumos.portal.be.security.RoleAuthorityConstants).PUBLISHER)")
    @ResponseBody
    public PagableResponse<List<MLPublishRequest>> getAllPublishRequest(HttpServletRequest request, @RequestBody JsonRequest<RestPageRequestPortal> restPageReq, HttpServletResponse response) {
		PagableResponse<List<MLPublishRequest>> mlPublishRequestList = new PagableResponse<>();
		try {
			mlPublishRequestList = publishRequestService.getAllPublishRequest(restPageReq.getBody().getPageRequest());
			
			log.debug("getAllPublishRequest: size is {} ", mlPublishRequestList.getSize());
		} catch (Exception e) {
			mlPublishRequestList.setResponseDetail("Exception Occurred Fetching Publish Request");
			log.error("Exception Occurred Fetching Publish Request", e);
		}
		return mlPublishRequestList;
	}
	
	@ApiOperation(value = "Get publish requests by Id.", response = MLPublishRequest.class)
    @RequestMapping(value = {"/{publishRequestId}"},method = RequestMethod.GET, produces = APPLICATION_JSON)
	@PreAuthorize("hasAuthority(T(org.acumos.portal.be.security.RoleAuthorityConstants).PUBLISHER)")
    @ResponseBody
    public JsonResponse<MLPublishRequest> getPublishRequest(HttpServletRequest request,@PathVariable("publishRequestId") long publishRequestId, HttpServletResponse response) {
		JsonResponse<MLPublishRequest> data = new JsonResponse<>();
		try {
			MLPublishRequest publishRequest = publishRequestService.getPublishRequestById(publishRequestId);
			data.setResponseBody(publishRequest);
			data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
			data.setResponseDetail("Publish Request fetched Successfully.");
			log.debug("Publish Request fetched Successfully : {}", publishRequestId);
		} catch (Exception e) {
			data.setErrorCode(JSONTags.TAG_ERROR_CODE);
			data.setResponseDetail("Exception occured while fetching the request");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			log.error("Exception occured while fetching the request :", e);
		}
		return data;
	}

	@ApiOperation(value = "Update publish request by Id.", response = MLPPublishRequest.class)
    @RequestMapping(value = {"/{publishRequestId}"},method = RequestMethod.PUT, produces = APPLICATION_JSON)
	@PreAuthorize("hasAuthority(T(org.acumos.portal.be.security.RoleAuthorityConstants).PUBLISHER)")
    @ResponseBody
    public JsonResponse<MLPublishRequest> updatePublishRequest(HttpServletRequest request, @PathVariable("publishRequestId") String publishRequestId, @RequestBody JsonRequest<MLPublishRequest> mlPublishRequest, HttpServletResponse response) {
		JsonResponse<MLPublishRequest> data = new JsonResponse<>();
		try {
			MLPublishRequest updatedPublishRequest = publishRequestService.updatePublishRequest(mlPublishRequest.getBody());
			data.setResponseBody(updatedPublishRequest);
			data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
			data.setResponseDetail("Publish Request updated Successfully.");
			log.debug("Publish Request updated Successfully : {}", updatedPublishRequest.getPublishRequestId());
		} catch (AcumosServiceException e) {
			data.setErrorCode(JSONTags.TAG_ERROR_CODE);
			data.setResponseDetail("Exception occured while Updating the request");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			log.error("Exception occured while Updating the request :", e);
		}
		return data;
	}

	@ApiOperation(value = "Withdraw publish request.", response = MLPPublishRequest.class)
    @RequestMapping(value = {"/withdraw/{publishRequestId}"},method = RequestMethod.PUT, produces = APPLICATION_JSON)
    @ResponseBody
    public JsonResponse<MLPublishRequest> updatePublishRequest(HttpServletRequest request, @PathVariable("publishRequestId") long publishRequestId, HttpServletResponse response) {
		JsonResponse<MLPublishRequest> data = new JsonResponse<>();
		try {
			MLPublishRequest updatedPublishRequest = publishRequestService.withdrawPublishRequest(publishRequestId, (String) request.getAttribute("loginUserId"));
			data.setResponseBody(updatedPublishRequest);
			data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
			data.setResponseDetail("Publish Request updated Successfully.");
			log.debug("Publish Request updated Successfully : {}", updatedPublishRequest.getPublishRequestId());
		} catch (AcumosServiceException e) {
			data.setErrorCode(JSONTags.TAG_ERROR_CODE);
			data.setResponseDetail("Exception occured while Updating the request");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			log.error("Exception occured while Updating the request :", e);
		}
		return data;
	}
}
