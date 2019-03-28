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

import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.acumos.cds.domain.MLPPeer;
import org.acumos.cds.domain.MLPSolution;
import org.acumos.cds.transport.ErrorTransport;
import org.acumos.portal.be.util.JsonUtils;
import org.acumos.portal.be.util.PortalUtils;
import org.apache.http.client.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Base64Utils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;

import com.fasterxml.jackson.core.type.TypeReference;

/**
 * 
 * FederationClient
 */
public class GatewayClient extends AbstractClient {

	protected static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

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
	public JsonResponse<MLPPeer> ping(String peerId) throws HttpStatusCodeException {

		Map<String, String> uriParams = new HashMap<String, String>();
		uriParams.put("peerId", peerId);
		URI uri = FederationAPI.PING.buildUri(this.baseUrl, uriParams);
		log.info("Query for " + uri);
		ResponseEntity<JsonResponse<MLPPeer>> response = null;
		try {
			response = restTemplate.exchange(uri, HttpMethod.GET, null,
					new ParameterizedTypeReference<JsonResponse<MLPPeer>>() {
					});
		} catch (HttpStatusCodeException x) {
			log.error(uri + " failed" + ((response == null) ? "" : (" " + response)), x);
			ErrorTransport et = JsonUtils.serializer().fromJson(x.getResponseBodyAsString(),
					new TypeReference<ErrorTransport>() {
					});
			throw new HttpClientErrorException(x.getStatusCode(), et.getError());
		} catch (Throwable t) {
			log.error(uri + " unexpected failure.", t);
		} finally {
			log.info(uri + " response " + response);
		}
		return response == null ? null : response.getBody();
	}

	/**
	 * 
	 * @param jsonString
	 *            JSON string for selector
	 * @return List of MLPSolutions from Remote Acumos
	 * @throws HttpStatusCodeException
	 *             Throws HttpStatusCodeException is remote acumos is not
	 *             available
	 */
	public JsonResponse<List<MLPSolution>> getSolutions(String peerId, String jsonString)
			throws HttpStatusCodeException {

		Map<String, Object> theSelection = PortalUtils.jsonStringToMap(jsonString);
		Map<String, String> uriParams = new HashMap<String, String>();
		uriParams.put("peerId", peerId);
		String selectorParam = null;
		try {
			selectorParam = theSelection == null ? null
					: Base64Utils.encodeToString(PortalUtils.mapToJsonString(theSelection).getBytes("UTF-8"));
		} catch (Exception x) {
			throw new IllegalArgumentException("Cannot process the selection argument", x);
		}

		URI uri = FederationAPI.SOLUTIONS.buildUri(this.baseUrl,
				selectorParam == null ? Collections.EMPTY_MAP
						: Collections.singletonMap(FederationAPI.QueryParameters.SOLUTIONS_SELECTOR, selectorParam),
				uriParams);

		log.info("Query for " + uri);
		ResponseEntity<JsonResponse<List<MLPSolution>>> response = null;
		try {
			response = restTemplate.exchange(uri, HttpMethod.GET, null,
					new ParameterizedTypeReference<JsonResponse<List<MLPSolution>>>() {
					});

			log.info("Response of Get Solutions for Peer : " + JsonUtils.serializer().toString(response));

		} catch (HttpStatusCodeException x) {
			log.error(uri + " failed" + ((response == null) ? "" : (" " + response)), x);
			ErrorTransport et = JsonUtils.serializer().fromJson(x.getResponseBodyAsString(),
					new TypeReference<ErrorTransport>() {
					});
			throw new HttpClientErrorException(x.getStatusCode(), et.getError());
		} catch (Throwable t) {
			log.error(uri + " unexpected failure.", t);
		} finally {
			log.info(uri + " response " + response);
		}
		return response == null ? null : response.getBody();
	}

	/**
	 */
	public JsonResponse<MLPSolution> getSolution(String peerId, String theSolutionId) throws HttpStatusCodeException {

		Map<String, String> uriParams = new HashMap<String, String>();
		uriParams.put("peerId", peerId);
		uriParams.put("solutionId", theSolutionId);
		URI uri = FederationAPI.SOLUTION_DETAIL.buildUri(this.baseUrl, peerId, theSolutionId);
		log.info("Query for " + uri);
		ResponseEntity<JsonResponse<MLPSolution>> response = null;
		try {
			response = restTemplate.exchange(uri, HttpMethod.GET, null,
					new ParameterizedTypeReference<JsonResponse<MLPSolution>>() {
					});
		} catch (HttpStatusCodeException x) {
			log.error(uri + " failed" + ((response == null) ? "" : (" " + response)), x);
			ErrorTransport et = JsonUtils.serializer().fromJson(x.getResponseBodyAsString(),
					new TypeReference<ErrorTransport>() {
					});
			throw new HttpClientErrorException(x.getStatusCode(), et.getError());
		} catch (Throwable t) {
			log.error(uri + " unexpected failure.", t);
		} finally {
			log.info(uri + " response " + response);
		}
		return response == null ? null : response.getBody();
	}

}
