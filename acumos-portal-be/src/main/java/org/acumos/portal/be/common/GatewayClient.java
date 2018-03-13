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

package org.acumos.portal.be.common;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.acumos.cds.domain.MLPPeer;
import org.acumos.cds.domain.MLPSolution;
import org.acumos.portal.be.util.EELFLoggerDelegate;
import org.acumos.portal.be.util.JsonUtils;
import org.acumos.portal.be.util.PortalUtils;
import org.apache.http.client.HttpClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Base64Utils;
import org.springframework.web.client.HttpStatusCodeException;

/**
 * 
 * FederationClient
 */
public class GatewayClient extends AbstractClient {
	
	protected static final EELFLoggerDelegate log = EELFLoggerDelegate.getLogger(GatewayClient.class);	


	/**
	 * @param theTarget
	 *            Target
	 * @param theClient
	 *            HttpClient
	 */
	public GatewayClient(String theTarget, HttpClient theClient) {
		super(theTarget, theClient);
	}

	/**
	 */
	public JsonResponse<MLPPeer> ping(String peerId)
			throws HttpStatusCodeException {
		
		Map<String, String> uriParams = new HashMap<String, String>();
		uriParams.put("peerId", peerId);		
		URI uri = FederationAPI.PING.buildUri(this.baseUrl,uriParams);		
		log.info(EELFLoggerDelegate.debugLogger, "Query for " + uri);
		ResponseEntity<JsonResponse<MLPPeer>> response = null;
		try {
			response = restTemplate.exchange(uri, HttpMethod.GET, null,
					new ParameterizedTypeReference<JsonResponse<MLPPeer>>() {
					});
		}
		catch (HttpStatusCodeException x) {
			log.error(EELFLoggerDelegate.errorLogger, uri + " failed" + ((response == null) ? "" : (" " + response)), x);
			throw x;
		}
		catch (Throwable t) {
			log.error(EELFLoggerDelegate.errorLogger, uri + " unexpected failure.", t);
		}
		finally {
			log.info(EELFLoggerDelegate.debugLogger, uri + " response " + response);
		}
		return response == null ? null : response.getBody();	
   }
	
	/**
	 * 
	 * @param jsonString
	 *            JSON string for selector
	 * @return List of MLPSolutions from Remote Acumos
	 * @throws HttpStatusCodeException
	 *             Throws HttpStatusCodeException is remote acumos is not available
	 */
	public JsonResponse<List<MLPSolution>> getSolutions(String peerId,String jsonString)
			throws HttpStatusCodeException {
		
		Map<String, Object> theSelection = PortalUtils.jsonStringToMap(jsonString);		
		Map<String, String> uriParams = new HashMap<String, String>();
		uriParams.put("peerId", peerId);
		String selectorParam = null;
		try {
			selectorParam = theSelection == null ? null
					// : UriUtils.encodeQueryParam(Utils.mapToJsonString(theSelection),"UTF-8");
					: Base64Utils.encodeToString(PortalUtils.mapToJsonString(theSelection).getBytes("UTF-8"));
		}
		catch (Exception x) {
			throw new IllegalArgumentException("Cannot process the selection argument", x);
		}
		
		URI uri = FederationAPI.SOLUTIONS.buildUri(this.baseUrl, selectorParam == null ? Collections.EMPTY_MAP
				: Collections.singletonMap(FederationAPI.QueryParameters.SOLUTIONS_SELECTOR, selectorParam),uriParams);
		
		log.info(EELFLoggerDelegate.debugLogger, "Query for " + uri);
		//System.out.println("uri=" + uri);
		ResponseEntity<JsonResponse<List<MLPSolution>>> response = null;
		try {
			response = restTemplate.exchange(uri, HttpMethod.GET, null,
					new ParameterizedTypeReference<JsonResponse<List<MLPSolution>>>() {
					});
			
			log.info( "Gateway Client : " + JsonUtils.serializer().toPrettyString(restTemplate.exchange(uri, HttpMethod.GET, null,
					new ParameterizedTypeReference<JsonResponse<List<MLPSolution>>>() {
					})));
			
			log.info(JsonUtils.serializer().toPrettyString(response));
			
			log.info( "Gateway Content : " + JsonUtils.serializer().toPrettyString(response.getBody().getContent()));
		}
		catch (HttpStatusCodeException x) {
			log.error(EELFLoggerDelegate.errorLogger, uri + " failed" + ((response == null) ? "" : (" " + response)), x);
			throw x;
		}
		catch (Throwable t) {
			log.error(EELFLoggerDelegate.errorLogger, uri + " unexpected failure.", t);
		}
		finally {
			log.info(EELFLoggerDelegate.debugLogger, uri + " response " + response);
		}
		return response == null ? null : response.getBody();
	}	
	
	/**
	 */
	public JsonResponse<MLPSolution> getSolution(String peerId,String theSolutionId)
			throws HttpStatusCodeException {

		Map<String, String> uriParams = new HashMap<String, String>();
		uriParams.put("peerId", peerId);
		uriParams.put("solutionId", theSolutionId);
		URI uri = FederationAPI.SOLUTION_DETAIL.buildUri(this.baseUrl, peerId,theSolutionId);
		log.info(EELFLoggerDelegate.debugLogger, "Query for " + uri);
		ResponseEntity<JsonResponse<MLPSolution>> response = null;
		try {
			response = restTemplate.exchange(uri, HttpMethod.GET, null,
					new ParameterizedTypeReference<JsonResponse<MLPSolution>>() {
					});
		}
		catch (HttpStatusCodeException x) {
			log.error(EELFLoggerDelegate.errorLogger, uri + " failed" + ((response == null) ? "" : (" " + response)), x);
			throw x;
		}
		catch (Throwable t) {
			log.error(EELFLoggerDelegate.errorLogger, uri + " unexpected failure.", t);
		}
		finally {
			log.info(EELFLoggerDelegate.debugLogger, uri + " response " + response);
		}
		return response == null ? null : response.getBody();
	}
	
}
