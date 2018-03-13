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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acumos.cds.domain.MLPPeer;
import org.acumos.cds.domain.MLPPeerSubscription;
import org.acumos.cds.domain.MLPSolution;
import org.acumos.portal.be.APINames;
import org.acumos.portal.be.common.Clients;
import org.acumos.portal.be.common.GatewayClient;
import org.acumos.portal.be.common.JSONTags;
import org.acumos.portal.be.common.JsonRequest;
import org.acumos.portal.be.common.JsonResponse;
import org.acumos.portal.be.common.exception.AcumosServiceException;
import org.acumos.portal.be.util.EELFLoggerDelegate;
import org.acumos.portal.be.util.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.ApiOperation;

@Controller
@RequestMapping(APINames.GATEWAY)
public class GatewayController extends AbstractController {
	
	private static final EELFLoggerDelegate log = EELFLoggerDelegate.getLogger(GatewayController.class);
	
	
	@Autowired
	private Environment env;
	
	@Autowired
	Clients clients;
	
	/**
	 * 
	 */
	public GatewayController() {
		// TODO Auto-generated constructor stub
	}
	
	
	/**
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 * @param peerId
	 * @return MLP Peer
	 */
	@ApiOperation(value = "Check the connectivity to gateway instance", response = JsonResponse.class)
	@RequestMapping(value = {APINames.PING},method = RequestMethod.GET, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<MLPPeer> pingGateway(HttpServletRequest request, @PathVariable("peerId") String peerId, HttpServletResponse response) {
		JsonResponse<MLPPeer> data = new JsonResponse<>();
		JsonResponse<MLPPeer> peer = null;
		try {
			if(peerId != null && peerId != "") {
				GatewayClient gateway = clients.getGatewayClient();
				peer = gateway.ping(peerId);
			}
			if (peer == null){
				throw new AcumosServiceException(AcumosServiceException.ErrorCode.OBJECT_NOT_FOUND, "Solution Not Found");
			} else {
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
				data.setResponseBody(peer.getContent());
			}
		}catch(Exception e) {
			data = new JsonResponse<>();
			data.setErrorCode(JSONTags.TAG_ERROR_CODE_FAILURE);
			response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
			data.setResponseCode(String.valueOf(HttpServletResponse.SC_BAD_GATEWAY));
			data.setResponseDetail(e.getMessage());
			log.error(EELFLoggerDelegate.errorLogger, "Failed to get Connection",e);
		}
		return data;
	}

	/**
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 * @param peerSubscription
	 * @return MLPSolution Solution
	 */
	@ApiOperation(value = "Get All the solutions according to the catagories or toolkit type selected", response = JsonResponse.class)
	@RequestMapping(value = {"/solutions"},method = RequestMethod.POST, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<List<MLPSolution>> getSolutions(HttpServletRequest request, @RequestBody JsonRequest<MLPPeerSubscription> peerSubscription, HttpServletResponse response) {
		JsonResponse<List<MLPSolution>> data = new JsonResponse<>();
		String selector = null;
		Map<String, Object> selectorMap = new HashMap<>();
		JsonResponse<List<MLPSolution>> solutions = null;
		if(peerSubscription != null) {
			try {
				MLPPeerSubscription mlpPeerSubscription = peerSubscription.getBody();
				if  (mlpPeerSubscription != null ) {
					selector = mlpPeerSubscription.getSelector();
					selectorMap = JsonUtils.serializer().mapFromJson(selector);
				}
				if (selectorMap != null && selectorMap.size() > 0) {
					GatewayClient gateway = clients.getGatewayClient();
					solutions = gateway.getSolutions(mlpPeerSubscription.getPeerId(), selector);
					
					log.info(JsonUtils.serializer().toPrettyString(solutions));
				}
				if (solutions == null){
					throw new AcumosServiceException(AcumosServiceException.ErrorCode.OBJECT_NOT_FOUND, "Solution Not Found");
				} else {
					data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
					data.setResponseBody(solutions.getContent());
				}
			}catch(Exception e) {
				data = new JsonResponse<>();
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_FAILURE);
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
				data.setResponseDetail(e.getMessage());
				log.error(EELFLoggerDelegate.errorLogger, "Exception Occured while fetching the solutions",e);
			}
		}
		return data;
	}

	/**
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 * @param solutionId
	 * @return MLPSolution Solution
	 */
	@ApiOperation(value = "Get Solution with the provided solutionId", response = JsonResponse.class)
	@RequestMapping(value = {"{solutionId}/solution/{peerId}"},method = RequestMethod.GET, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<MLPSolution> getSolution(HttpServletRequest request, @PathVariable("solutionId") String solutionId,
			@PathVariable("peerId") String peerId, HttpServletResponse response) {
		JsonResponse<MLPSolution> data = new JsonResponse<MLPSolution>();
		JsonResponse<MLPSolution> solution = null;
		if(solutionId != null) {
			try {
				GatewayClient gateway = clients.getGatewayClient();
				solution = gateway.getSolution(peerId,solutionId);
				if (solution == null){
					throw new AcumosServiceException(AcumosServiceException.ErrorCode.OBJECT_NOT_FOUND, "Solution Not Found");
				} else {
					data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
					data.setResponseBody(solution.getContent());
				}
			}catch(Exception e) {
				data = new JsonResponse<>();
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_FAILURE);
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
				data.setResponseDetail(e.getMessage());
				log.error(EELFLoggerDelegate.errorLogger, "No Solution Found for Dolution Id : " + solutionId, e);
			}
		}
		return data;
	}

}
