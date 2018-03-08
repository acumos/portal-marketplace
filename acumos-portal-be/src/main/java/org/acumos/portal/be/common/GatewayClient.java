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
import java.util.List;
import java.util.Map;

import org.acumos.cds.domain.MLPPeer;
import org.acumos.cds.domain.MLPSolution;
import org.acumos.portal.be.util.EELFLoggerDelegate;
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
		
		//log.debug(EELFLoggerDelegate.debugLogger, API.Roots.LOCAL + "" + API.Paths.PING);		
		URI uri = FederationAPI.PING.buildUri(this.baseUrl,peerId);
		
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
	 * @param theSelection
	 *            key-value pairs; ignored if null or empty. Gives special treatment
	 *            to Date-type values.
	 * @return List of MLPSolutions from Remote Acumos
	 * @throws HttpStatusCodeException
	 *             Throws HttpStatusCodeException is remote acumos is not available
	 */
	public JsonResponse<List<MLPSolution>> getSolutions(Map<String, Object> theSelection)
			throws HttpStatusCodeException {

		String selectorParam = null;
		try {
			selectorParam = theSelection == null ? null
					: Base64Utils.encodeToString(PortalUtils.mapToJsonString(theSelection).getBytes("UTF-8"));
		}
		catch (Exception x) {
			throw new IllegalArgumentException("Cannot process the selection argument", x);
		}

		URI uri = FederationAPI.SOLUTIONS.buildUri(this.baseUrl, selectorParam == null ? Collections.EMPTY_MAP
				: Collections.singletonMap(FederationAPI.QueryParameters.SOLUTIONS_SELECTOR, selectorParam));
		log.info(EELFLoggerDelegate.debugLogger, "Query for " + uri);
		ResponseEntity<JsonResponse<List<MLPSolution>>> response = null;
		try {
			response = restTemplate.exchange(uri, HttpMethod.GET, null,
					new ParameterizedTypeReference<JsonResponse<List<MLPSolution>>>() {
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
	 * @param theArtifactId
	 *            Artifact ID
	 * @return Resource
	 * @throws HttpStatusCodeException
	 *             On failure
	 */
	/*public Resource downloadArtifact(String theArtifactId) throws HttpStatusCodeException {
		URI uri = FederationAPI.ARTIFACT_DOWNLOAD.buildUri(this.baseUrl, theArtifactId);
		log.info(EELFLoggerDelegate.debugLogger, "Query for " + uri);
		ResponseEntity<Resource> response = null;
		try {
			response = restTemplate.exchange(uri, HttpMethod.GET, null, Resource.class);
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

		if (response == null) {
			return null;
		} else {
			return response.getBody();
		}
	} */
	
	/**
	 */
	public JsonResponse<MLPSolution> getSolution(String theSolutionId)
			throws HttpStatusCodeException {

		URI uri = FederationAPI.SOLUTION_DETAIL.buildUri(this.baseUrl, theSolutionId);
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
