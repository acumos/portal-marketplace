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

package org.acumos.portal.be.service.impl;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Map;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import org.acumos.cds.CCDSConstants;
import org.acumos.cds.client.HttpComponentsClientHttpRequestFactoryBasicAuth;
import org.acumos.cds.domain.MLPSolution;
import org.acumos.cds.domain.MLPUser;
import org.acumos.cds.transport.LoginTransport;

/**
 * 
 * @author Ashwin Sharma
 * 
 * Temporary Client until we have login functions available in Common Data MicroService
 */
public class PortalRestClienttImpl {

	private static Logger logger = LoggerFactory.getLogger(PortalRestClienttImpl.class);
	
	private final String baseUrl;
	private final RestTemplate restTemplate;

	/**
	 * Builds a restTemplate. If user and pass are both supplied, uses basic
	 * HTTP authentication; if either one is missing, no authentication is used.
	 * 
	 * @param webapiUrl
	 *            URL of the web endpoint
	 * @param user
	 *            user name; ignored if null
	 * @param pass
	 *            password; ignored if null
	 */
	public PortalRestClienttImpl(String webapiUrl, String user, String pass) {
		if (webapiUrl == null)
			throw new IllegalArgumentException("Null URL not permitted");

		URL url = null;
		try {
			url = new URL(webapiUrl);
			baseUrl = url.toExternalForm();
		} catch (MalformedURLException ex) {
			throw new RuntimeException("Failed to parse URL", ex);
		}
		final HttpHost httpHost = new HttpHost(url.getHost(), url.getPort());

		// Build a client with a credentials provider
		CloseableHttpClient httpClient = null;
		if (user != null && pass != null) {
			CredentialsProvider credsProvider = new BasicCredentialsProvider();
			credsProvider.setCredentials(new AuthScope(httpHost), new UsernamePasswordCredentials(user, pass));
			httpClient = HttpClientBuilder.create().setDefaultCredentialsProvider(credsProvider).build();
		} else {
			httpClient = HttpClientBuilder.create().build();
		}
		// Create request factory
		HttpComponentsClientHttpRequestFactoryBasicAuth requestFactory = new HttpComponentsClientHttpRequestFactoryBasicAuth(
				httpHost);
		requestFactory.setHttpClient(httpClient);

		// Put the factory in the template
		restTemplate = new RestTemplate();
		restTemplate.setRequestFactory(requestFactory);
	}

	public MLPUser login(LoginTransport login) throws HttpStatusCodeException {
		URI uri = buildUri(new String[] { "user", "login"}, null);
		logger.debug("login: uri {}", uri);
		MLPUser mlpUser = restTemplate.postForObject(uri, login, MLPUser.class);
		return mlpUser;
	}
	
	public void updateUser(MLPUser user) throws HttpStatusCodeException {
		URI uri = buildUri(new String[] { CCDSConstants.USER_PATH, user.getUserId()}, null);
		logger.debug("updateUser: url {}", uri);
		restTemplate.put(uri, user);
	}
	
	public void updateSolution(MLPSolution solution) throws HttpStatusCodeException {
		URI uri = buildUri(new String[] { CCDSConstants.SOLUTION_PATH, solution.getSolutionId()}, null);
		logger.debug("updateSolution: url {}", uri);
		restTemplate.put(uri, solution);
	}
	
	/**
	 * Builds URI by adding specified path segments and query parameters to the base URL.
	 * 
	 * @param path Array of path segments
	 * @param queryParams
	 *            key-value pairs; ignored if null or empty.
	 * @return
	 */
	private URI buildUri(final String[] path, final Map<String,String> queryParams) {
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(this.baseUrl);
		for (int p = 0; p < path.length; ++p)
			builder.pathSegment(path[p]);
		if (queryParams != null && queryParams.size() > 0) {
			for (Map.Entry<String, String> entry: queryParams.entrySet())
				builder.queryParam(entry.getKey(), entry.getValue());
		}
		return builder.build().encode().toUri();
	}
	
	public RestTemplate geRestTemplate() {
		return this.restTemplate;
	}
	
}
