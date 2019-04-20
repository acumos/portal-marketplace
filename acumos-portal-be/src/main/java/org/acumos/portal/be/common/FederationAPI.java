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
import java.util.Collection;
import java.util.Map;

import org.springframework.web.util.UriComponentsBuilder;

/**
 * Specifies the REST API that makes up the federation interface. 
 */
public enum FederationAPI {

	SOLUTIONS(Paths.SOLUTIONS, Queries.SOLUTIONS),
	//SOLUTIONS(Paths.SOLUTIONS),
	SOLUTION_DETAIL(Paths.SOLUTION_DETAILS),
	CATALOGS(Paths.CATALOGS),
	PING(Paths.PING);

	private String path;
	private String[] query;

	FederationAPI(String thePath) {
		this.path = thePath;
	}

	FederationAPI(String thePath, String[] theQueryParams) {
		this.path = thePath;
		this.query = theQueryParams;
	}

	public String path() {
		return this.path;
	}

	public String[] query() {
		return this.query;
	}

	/*public Map<String, ?> queryParams(Map<String, ?> theParams) {
		for (String queryParam : this.query) {
			if (!theParams.containsKey(queryParam)) {
				theParams.put(queryParam, null);
			}
		}
		return theParams;
	}*/

	public String toString() {
		return this.path;
	}

	/**
	 * Prepares a 'full' URI for this API call. It will contain all query
	 * parameters.
	 * 
	 * @param theHttpUrl
	 *            URL
	 * @return URI
	 */
	public UriComponentsBuilder uriBuilder(String theHttpUrl) {
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(theHttpUrl).path(this.path);
		if (this.query != null) {
			for (String queryParam : this.query) {
				builder.queryParam(queryParam, "{" + queryParam + "}");
			}
		}
		return builder;
	}	

	/**
	 * Prepares a URI containing only the query param present in the given
	 * collection.
	 * 
	 * @param theHttpUrl
	 *            URL
	 * @param theParams
	 *            parameters
	 * @return URI
	 */
	public UriComponentsBuilder uriBuilder(String theHttpUrl, Collection<String> theParams) {
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(theHttpUrl).path(this.path);
		if (this.query != null) {
			for (String queryParam : this.query) {
				if (theParams.contains(queryParam)) {
					builder.queryParam(queryParam, "{" + queryParam + "}");
				}
			}
		}
		return builder;
	}
	
	public UriComponentsBuilder uriBuilder(String theHttpUrl,  Map<String, ?> theParams) {
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(theHttpUrl).path(this.path);
		if (this.query != null) {
			for (String queryParam : this.query) {
				for (Map.Entry<String, ?> entry : theParams.entrySet()) {
					//System.out.println("Key : " + entry.getKey() + " Value : " + entry.getValue());
					if (queryParam.equals(entry.getKey())) {
						builder.queryParam(entry.getKey(), entry.getValue());
					}
				}
			}
		}
		return builder;
	}

	/*
	 * the params include both path and query params.
	 */
	public URI buildUri(String theHttpUrl, Map<String, ?> theParams) {
		return uriBuilder(theHttpUrl, theParams.keySet()).buildAndExpand(theParams).encode().toUri();
	}
	
	
	/*
	 * the params include both path and query params.
	 */
	public URI buildUri(String theHttpUrl, Map<String, ?> theParamsQuery , Map<String, ?> theParams) {
		
		return uriBuilder(theHttpUrl, theParamsQuery).buildAndExpand(theParams).encode().toUri();		
	}

	/**
	 * Order based version. All query params must be present.
	 * 
	 * @param theHttpUrl
	 *            URL
	 * @param theParams
	 *            Parameters
	 * @return URI
	 */
	public URI buildUri(String theHttpUrl, String... theParams) {
		return uriBuilder(theHttpUrl).buildAndExpand(theParams).encode().toUri();
	}	

	public static class Roots {

		public static final String FEDERATION = "/";		
		
	}

	public static class Paths {

		public static final String SOLUTIONS = "/peer/{peerId}/solutions";
		public static final String SOLUTION_DETAILS = "/peer/{peerId}/solutions/{solutionId}";
		public static final String PING = "/peer/{peerId}/ping";
		public static final String CATALOGS = "/peer/{peerId}/catalogs";
	}	
	
	public static class QueryParameters {

		public static final String SOLUTIONS_SELECTOR = "selector";
	}

	public static class Queries {

		public static final String[] SOLUTIONS = { QueryParameters.SOLUTIONS_SELECTOR };
	}
	
}
