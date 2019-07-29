package org.acumos.portal.be.service.impl;

import java.lang.invoke.MethodHandles;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URI;
import java.net.URL;
import java.time.Instant;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.acumos.cds.client.HttpComponentsClientHttpRequestFactoryBasicAuth;
import org.acumos.portal.be.service.ElkService;
import org.acumos.portal.be.transport.ElkSnapshotsResponse;
import org.acumos.portal.be.util.ElkClientConstants;
import org.apache.http.HttpHost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class ElkServiceImpl implements ElkService {

	@Autowired
	private Environment env;

//	@Autowired
	private RestTemplate restTemplate;

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	/*
	 * @Override public String createRepository(ElkRepositoriesRequest request) {
	 * URI uri = buildUri(new String[] {
	 * ElkClientConstants.SNAPSHOT_CREATE_REPOSITORY }, null, null);
	 * logger.debug("createRepository: uri {}", uri); return
	 * restTemplate.postForObject(uri, request, String.class); }
	 * 
	 */



	@Override
	public ElkSnapshotsResponse getAllSnapshots() {

		URI uri = buildUri(new String[] { ElkClientConstants.GET_ALL_SNAPSHOTS }, null);// , null);
		logger.debug("getAllRepositories: uri {}", uri);
		System.out.println("getAllRepositories: uri {}  " + uri);
		restTemplate = getRestTemplate(uri.toString());
//		init();
		ElkSnapshotsResponse response = restTemplate.getForObject(uri, ElkSnapshotsResponse.class);
//		ResponseEntity<ElkGetRepositoriesResponse> response = getRestTemplate().exchange(uri, HttpMethod.GET, null,
//				new ParameterizedTypeReference<ElkGetRepositoriesResponse>() {
//				});
		System.out.println(response);
		return response;
	}

	protected URI buildUri(final String[] path,
			final Map<String, Object> queryParams/* , RestPageRequest pageRequest */) {
		String baseUrl = env.getProperty("elk.url");
		System.out.println("BASEURL :::: " + baseUrl);
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
//			if (pageRequest != null) {
//				if (pageRequest.getSize() != null)
//					builder.queryParam("page", Integer.toString(pageRequest.getPage()));
//				if (pageRequest.getPage() != null)
//					builder.queryParam("size", Integer.toString(pageRequest.getSize()));
//				if (pageRequest.getFieldToDirectionMap() != null && pageRequest.getFieldToDirectionMap().size() > 0) {
//					for (Map.Entry<String, String> entry : pageRequest.getFieldToDirectionMap().entrySet()) {
//						String value = entry.getKey() + (entry.getValue() == null ? "" : ("," + entry.getValue()));
//						builder.queryParam("sort", value);
//					}
//				}
//			}
		return builder.build()/* .encode() */.toUri();
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
