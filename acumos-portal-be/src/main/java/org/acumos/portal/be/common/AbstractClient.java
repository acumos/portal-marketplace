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
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.client.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.function.SingletonSupplier;
import org.springframework.web.client.RestTemplate;
/**
 * 
 *  AbstractClient
 * 
 *        
 */
public abstract class AbstractClient {

	protected static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());	
	
	protected final String baseUrl;
	protected final RestTemplate restTemplate;

	/**
	 * Builds a restTemplate. If user and pass are both supplied, uses basic
	 * HTTP authentication; if either one is missing, no authentication is used.
	 * 
	 * @param theTarget
	 *            URL of the web endpoint
	 * @param theClient
	 *						underlying http client
	 */
	public AbstractClient(String theTarget,
													HttpClient theClient) {
		if (theTarget == null)
			throw new IllegalArgumentException("Null URL not permitted");

		URL url = null;
		try {
			url = new URL(theTarget);
			this.baseUrl = url.toExternalForm();
		}
		catch (MalformedURLException ex) {
			throw new RuntimeException("Failed to parse targedt URL", ex);
		}

		this.restTemplate = new RestTemplateBuilder()
				.requestFactory(SingletonSupplier.of(
					new HttpComponentsClientHttpRequestFactory(theClient)))	
				.rootUri(this.baseUrl).build();
	}
}	
