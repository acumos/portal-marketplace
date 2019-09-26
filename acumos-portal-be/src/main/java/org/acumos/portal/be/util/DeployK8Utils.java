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

package org.acumos.portal.be.util;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.time.Instant;
import java.util.Map;

import org.acumos.cds.client.HttpComponentsClientHttpRequestFactoryBasicAuth;
import org.apache.http.HttpHost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;



@Component
public class DeployK8Utils {
	
	@Autowired
	Environment env;
	
	public URI buildUri(final String[] path,
			final Map<String, Object> queryParams) {
			String baseUrl = env.getProperty("k8_deploy.url");
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl);
			for (int p = 0; p < path.length; ++p) {
				if (path[p] == null)
					throw new IllegalArgumentException("Unexpected null at path index " + Integer.toString(p));
				builder.pathSegment(path[p]);
			}
			if (queryParams != null && queryParams.size() > 0) {
				for (Map.Entry<String, ? extends Object> entry : queryParams.entrySet()) {
					if (entry.getKey() == null || entry.getValue() == null) {
						throw new IllegalArgumentException("Unexpected null key or value");
					} else if (entry.getValue() instanceof Instant) {
						// Server expects point-in-time as Long (not String)
						builder.queryParam(entry.getKey(), ((Instant) entry.getValue()).toEpochMilli());
					} else if (entry.getValue().getClass().isArray()) {
						Object[] array = (Object[]) entry.getValue();
						for (Object o : array) {
							if (o == null)
								builder.queryParam(entry.getKey(), "null");
							else if (o instanceof Instant)
								builder.queryParam(entry.getKey(), ((Instant) o).toEpochMilli());
							else
								builder.queryParam(entry.getKey(), o.toString());
						}
					} else {
						builder.queryParam(entry.getKey(), entry.getValue().toString());
					}
				}
			}
			return builder.build().toUri();
		}

	public RestTemplate getRestTemplate(String webapiUrl) {

		RestTemplate restTemplate = new RestTemplate();

		if (webapiUrl == null)
			throw new IllegalArgumentException("Null URL not permitted");

		URL url = null;
		try {
			url = new URL(webapiUrl);
		} catch (MalformedURLException ex) {
			throw new RuntimeException("Failed to parse URL", ex);
		}
		final HttpHost httpHost = new HttpHost(url.getHost(), url.getPort());
		CloseableHttpClient httpClient = null;
		httpClient = HttpClientBuilder.create().build();
		HttpComponentsClientHttpRequestFactoryBasicAuth requestFactory = new HttpComponentsClientHttpRequestFactoryBasicAuth(
				httpHost);
		requestFactory.setHttpClient(httpClient);
		restTemplate.setRequestFactory(requestFactory);
		return restTemplate;
	}
}
