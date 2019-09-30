/*-
 * ===============LICENSE_START=======================================================
 * Acumos
 * ===================================================================================
 * Copyright (C) 2019 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acumos.portal.be.APINames;
import org.acumos.portal.be.common.JSONTags;
import org.acumos.portal.be.common.JsonResponse;
import org.acumos.portal.be.service.DeployCloudService;
import org.acumos.portal.be.transport.MLK8SiteConfig;
import org.acumos.portal.be.util.PortalConstants;
import org.acumos.portal.be.util.SanitizeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.ApiOperation;

@Controller
@RequestMapping("/deployCloud")
public class DeployCloudController {
	
	@Autowired
	DeployCloudService deployCloudService;
	
	protected static final String APPLICATION_JSON = "application/json";
	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@ApiOperation(value = "Deploy to K8", response = String.class)
	@RequestMapping(value = {APINames.DEPLOY_TO_K8}, method = RequestMethod.POST, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<String> deployToK8(HttpServletRequest request,
			@RequestParam("userId") String userId,@RequestParam("solutionId") String solutionId,@RequestParam("revisionId") String revisionId ,@RequestParam("envId") String envId, HttpServletResponse response) {
		log.debug("deployToK8 : userId:"+userId+" ,solutionId: "+solutionId+" ,revisionId: "+revisionId+" ,envId: "+envId);
		JsonResponse<String> data = new JsonResponse<>();
		userId = SanitizeUtils.sanitize(userId);
		solutionId = SanitizeUtils.sanitize(solutionId);
		envId = SanitizeUtils.sanitize(envId);
		SanitizeUtils.sanitize(userId);
		ResponseEntity<String> deployToK8Response=null;
		try {
			deployToK8Response=deployCloudService.deployToK8(userId,solutionId,revisionId,envId);
			if (deployToK8Response.getBody() != null && deployToK8Response.getStatusCodeValue() == 202) {
				data.setResponseBody(deployToK8Response.getBody());
				data.setErrorCode(JSONTags.ROLE_CREATED);
				response.setStatus(HttpServletResponse.SC_ACCEPTED);
				data.setResponseDetail("Deployed successfully to K8");
			} else {
				data.setErrorCode(JSONTags.TAG_STATUS_FAILURE);
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
				data.setResponseDetail("Error occured while deploying to K8");
				log.error("Error occured while deploying to K8");
			}
		} catch (Exception e) {
			data.setErrorCode(JSONTags.TAG_ERROR_CODE_FAILURE);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			data.setResponseDetail("Exception occurred while deploying to K8");
			log.error("Exception occurred while deploying to K8", e);
		}
		return data;
	
	}
	
	@ApiOperation(value = "Get Site Config By Id", response = String.class)
	@RequestMapping(value = { APINames.GET_DEPLOY_TO_K8_CONFIG }, method = RequestMethod.POST, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<MLK8SiteConfig> getDeployToK8Config(HttpServletRequest request,
			@RequestParam("userId") String userId, HttpServletResponse response) {
		log.debug("getSiteConfig");
		JsonResponse<MLK8SiteConfig> data = new JsonResponse<>();
		try {
			MLK8SiteConfig configValue = deployCloudService.getSiteConfig(PortalConstants.K8CLUSTER_CONFIG_KEY);
			if (configValue != null) {
				data.setResponseBody(configValue);
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
				data.setResponseDetail("K8 Config Fetched Successfully");
				log.info("K8 Config Fetched Successfully");
			} else {
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_FAILURE);
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				data.setResponseDetail("Error occured while fetching K8 config");
				log.error("Error occured while fetching K8 config");
			}
		} catch (Exception e) {
			data.setErrorCode(JSONTags.TAG_ERROR_CODE_EXCEPTION);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			data.setResponseDetail("Exception occured while fetching K8 config");
			log.error("Exception occured while fetching K8 config", e);
		}
		return data;
	}
	
}
