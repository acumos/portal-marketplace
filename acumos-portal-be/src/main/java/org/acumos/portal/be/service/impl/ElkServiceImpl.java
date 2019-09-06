package org.acumos.portal.be.service.impl;

import java.lang.invoke.MethodHandles;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import org.acumos.cds.client.HttpComponentsClientHttpRequestFactoryBasicAuth;
import org.acumos.portal.be.service.ElkService;
import org.acumos.portal.be.transport.ElasticStackIndiceResponse;
import org.acumos.portal.be.transport.ElasticStackIndices;
import org.acumos.portal.be.transport.ElkArchiveResponse;
import org.acumos.portal.be.transport.ElkArchive;
import org.acumos.portal.be.transport.ElkCreateSnapshotRequest;
import org.acumos.portal.be.transport.ElkDeleteSnapshotRequest;
import org.acumos.portal.be.transport.ElkGetRepositoriesResponse;
import org.acumos.portal.be.transport.ElkGetSnapshotsResponse;
import org.acumos.portal.be.transport.ElkRepositoriesRequest;
import org.acumos.portal.be.transport.ElkRepositoriesResponse;
import org.acumos.portal.be.transport.ElkRestoreSnapshotRequest;
import org.acumos.portal.be.transport.ElkSnapshotsResponse;
import org.acumos.portal.be.util.ElkClientConstants;
import org.acumos.portal.be.util.URIUtil;
import org.apache.http.HttpHost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ElkServiceImpl implements ElkService {

	private RestTemplate restTemplate;

	@Autowired
	private Environment env;

	URIUtil uriUtil=new URIUtil();

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());


	@Override public ElkRepositoriesResponse createRepository(ElkRepositoriesRequest request) {
		uriUtil.setEnvironment(env);
		URI uri = uriUtil.buildUri(new String[] {
				ElkClientConstants.SNAPSHOT_CREATE_REPOSITORY }, null);
		logger.debug("createRepository: uri {}", uri); 
		restTemplate = getRestTemplate(uri.toString());
		ElkRepositoriesResponse response=restTemplate.postForObject(uri, request, ElkRepositoriesResponse.class); 
		return response;
	}

	@Override
	public ElkGetRepositoriesResponse getAllRepositories() {
		uriUtil.setEnvironment(env);
		URI uri =uriUtil.buildUri(new String[] { ElkClientConstants.GET_ALL_REPOSITORIES }, null);
		logger.debug("getAllRepositories: uri {}", uri);
		restTemplate = getRestTemplate(uri.toString());
		ElkGetRepositoriesResponse response = restTemplate.getForObject(uri, ElkGetRepositoriesResponse.class);
		return response;
	}

	@Override
	public ElkRepositoriesResponse deleteRepository(ElkRepositoriesRequest request) {
		uriUtil.setEnvironment(env);
		URI uri =uriUtil.buildUri(new String[] { ElkClientConstants.SNAPSHOT_DELETE_REPOSITORY_REQUEST }, null);
		logger.debug("deleteRepository: uri {}", uri);
		restTemplate = getRestTemplate(uri.toString());
		ElkRepositoriesResponse response = restTemplate.postForObject(uri, request, ElkRepositoriesResponse.class);
		return response;
	}

	@Override
	public ElkSnapshotsResponse createSnapshots(ElkCreateSnapshotRequest request) {
		uriUtil.setEnvironment(env);
		URI uri =uriUtil.buildUri(new String[] { ElkClientConstants.CREATE_SNAPSHOT_REQUEST }, null);
		logger.debug("createRepository: uri {}", uri);
		restTemplate = getRestTemplate(uri.toString());
		ElkSnapshotsResponse response= restTemplate.postForObject(uri, request, ElkSnapshotsResponse.class);
		return response;
	}

	@Override
	public ElkGetSnapshotsResponse getAllSnapshots() {
		uriUtil.setEnvironment(env);
		URI uri =uriUtil.buildUri(new String[] { ElkClientConstants.GET_ALL_SNAPSHOTS }, null);// , null);
		logger.debug("getAllRepositories: uri {}", uri);
		restTemplate = getRestTemplate(uri.toString());
		ElkGetSnapshotsResponse response = restTemplate.getForObject(uri, ElkGetSnapshotsResponse.class);
		return response;
	}

	@Override
	public ElkSnapshotsResponse deleteSnapshots(ElkDeleteSnapshotRequest request) {
		uriUtil.setEnvironment(env);
		URI uri =uriUtil.buildUri(new String[] { ElkClientConstants.DELETE_SNAPSHOT_REQUEST }, null);
		logger.debug("deleteSnapshots: uri {}", uri);
		restTemplate = getRestTemplate(uri.toString());
		ElkSnapshotsResponse response = restTemplate.postForObject(uri, request, ElkSnapshotsResponse.class);
		return response;
	}

	@Override
	public ElasticStackIndiceResponse restoreSnapshots(ElkRestoreSnapshotRequest request) {
		uriUtil.setEnvironment(env);
		URI uri =uriUtil.buildUri(new String[] { ElkClientConstants.RESTORE_SNAPSHOT_REQUEST }, null);
		logger.debug("restoreSnapshots: uri {}", uri);
		restTemplate = getRestTemplate(uri.toString());
		ElasticStackIndiceResponse response = restTemplate.postForObject(uri, request, ElasticStackIndiceResponse.class);
		return response;
	}

	@Override
	public ElasticStackIndices getAllIndices() {
		uriUtil.setEnvironment(env);
		URI uri =uriUtil.buildUri(new String[] { ElkClientConstants.GET_ALL_INDICES }, null);
		logger.debug("getAllIndices: uri {}", uri);
		restTemplate = getRestTemplate(uri.toString());
		ElasticStackIndices response = restTemplate.getForObject(uri,ElasticStackIndices.class);
		return response;
	}

	@Override
	public ElasticStackIndiceResponse deleteIndices(ElasticStackIndices request) {
		uriUtil.setEnvironment(env);
		URI uri =uriUtil.buildUri(new String[] { ElkClientConstants.DELETE_INDICES }, null);
		logger.debug("deleteIndices: uri {}", uri);
		restTemplate = getRestTemplate(uri.toString());
		ElasticStackIndiceResponse response = restTemplate.postForObject(uri, request, ElasticStackIndiceResponse.class);
		return response;
	}
	
	@Override
	public ElkArchiveResponse getAllArchive() {
		uriUtil.setEnvironment(env);
		URI uri =uriUtil.buildUri(new String[] { ElkClientConstants.GET_ARCHIVE }, null);
		logger.debug("getAllIndices: uri {}", uri);
		restTemplate = getRestTemplate(uri.toString());
		ElkArchiveResponse response = restTemplate.getForObject(uri,ElkArchiveResponse.class);
		return response;
	}
	
	@Override
	public ElkArchiveResponse archiveAction(ElkArchive elkArchive) {
		uriUtil.setEnvironment(env);
		URI uri =uriUtil.buildUri(new String[] { ElkClientConstants.ARCHIVE_ACTION }, null);
		logger.debug("deleteIndices: uri {}", uri);
		restTemplate = getRestTemplate(uri.toString());
		ElkArchiveResponse response = restTemplate.postForObject(uri, elkArchive, ElkArchiveResponse.class);
		return response;
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
