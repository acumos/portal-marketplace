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

package org.acumos.nexus.client;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.acumos.nexus.client.data.UploadArtifactInfo;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.maven.wagon.ConnectionException;
import org.apache.maven.wagon.StreamingWagon;
import org.apache.maven.wagon.authentication.AuthenticationException;
import org.codehaus.plexus.util.IOUtil;
import org.springframework.web.client.RestTemplate;

public class NexusArtifactClient {
	private String nexusRepoHost;
	private String nexusRepoPort;
	private String nexusRepoHttpScheme;
	private String nexusArtifactRepo;
	private RepositoryLocation repositoryLocation;
	private final RestTemplate restTemplate;

	/**
	 * @param nexusRepoHost
	 *            Nexus Repository Host e.g example.com
	 * @param nexusRepoPort
	 *            Nexus Repository Port e.g 8081
	 * @param nexusRepoHttpScheme
	 *            Nexus Repository Scheme Http or Https
	 * @param nexusArtifactRepo
	 *            Nexus Artifact Repository Location e.g Acumos
	 */
	public NexusArtifactClient(String nexusRepoHost, String nexusRepoPort, String nexusRepoHttpScheme,
			String nexusArtifactRepo) {
		this.nexusRepoHost = nexusRepoHost;
		this.nexusRepoPort = nexusRepoPort;
		this.nexusRepoHttpScheme = nexusRepoHttpScheme;
		this.nexusArtifactRepo = nexusArtifactRepo;

		URL url = null;
		try {
			url = new URL(repositoryLocation.getUrl());
		} catch (MalformedURLException ex) {
			throw new RuntimeException("Failed to parse URL", ex);
		}
		final HttpHost httpHost = new HttpHost(url.getHost(), url.getPort());

		// Build a client with a credentials provider
		CloseableHttpClient httpClient = null;
		if (repositoryLocation.getUsername() != null && repositoryLocation.getPassword() != null) {
			CredentialsProvider credsProvider = new BasicCredentialsProvider();
			credsProvider.setCredentials(new AuthScope(httpHost), new UsernamePasswordCredentials(
					repositoryLocation.getUsername(), repositoryLocation.getPassword()));
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

	public NexusArtifactClient(RepositoryLocation repositoryLocation) {
		this.repositoryLocation = repositoryLocation;

		URL url = null;
		try {
			url = new URL(repositoryLocation.getUrl());
		} catch (MalformedURLException ex) {
			throw new RuntimeException("Failed to parse URL", ex);
		}
		final HttpHost httpHost = new HttpHost(url.getHost(), url.getPort());

		// Build a client with a credentials provider
		CloseableHttpClient httpClient = null;
		if (repositoryLocation.getUsername() != null && repositoryLocation.getPassword() != null) {
			CredentialsProvider credsProvider = new BasicCredentialsProvider();
			credsProvider.setCredentials(new AuthScope(httpHost), new UsernamePasswordCredentials(
					repositoryLocation.getUsername(), repositoryLocation.getPassword()));
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

	public String getNexusRepoHost() {
		return nexusRepoHost;
	}

	public void setNexusRepoHost(String nexusRepoHost) {
		this.nexusRepoHost = nexusRepoHost;
	}

	public String getNexusRepoPort() {
		return nexusRepoPort;
	}

	public void setNexusRepoPort(String nexusRepoPort) {
		this.nexusRepoPort = nexusRepoPort;
	}

	public String getNexusRepoHttpScheme() {
		return nexusRepoHttpScheme;
	}

	public void setNexusRepoHttpScheme(String nexusRepoHttpScheme) {
		this.nexusRepoHttpScheme = nexusRepoHttpScheme;
	}

	public String getNexusArtifactRepo() {
		return nexusArtifactRepo;
	}

	public void setNexusArtifactRepo(String nexusArtifactRepo) {
		this.nexusArtifactRepo = nexusArtifactRepo;
	}

	/**
	 * @param groupId
	 *            GroupId where the Artifacts needs to stored.
	 * @param artifactId
	 *            ArtifactId is the name of the artifact
	 * @param version
	 *            Version of the artifact
	 * @param packaging
	 *            Packaging of the Artifact
	 * @param contentLength
	 *            ContentLength of the Artifact
	 * @param inputStream
	 *            InputStream containing artifact
	 * @return UploadArtifactInfo
	 * @throws ConnectionException
	 *             On failure to connect
	 */
	public UploadArtifactInfo uploadArtifact(String groupId, String artifactId, String version, String packaging,
			long contentLength, InputStream inputStream) throws ConnectionException {
		StreamingWagon streamWagon = null;
		UploadArtifactInfo artifactInfo = null;
		try {
			String mvnPath = MvnRepoWagonConnectionManager.createMvnPath(groupId, artifactId, version, packaging);
			artifactInfo = new UploadArtifactInfo(groupId, artifactId, version, packaging, mvnPath, 180000);
			if (repositoryLocation != null) {
				streamWagon = MvnRepoWagonConnectionManager.createWagon(repositoryLocation);
			} else {
				// TODO Need to add a new method to create Wagon based on the Parameters
			}
			streamWagon.putFromStream(inputStream, mvnPath, contentLength, -1);
		} catch (ConnectionException e) {
			// TODO Auto-generated catch block

		} catch (AuthenticationException e) {
			// TODO Auto-generated catch block

		} catch (Exception e) {
			// TODO Auto-generated catch block

		} finally {
			IOUtil.close(inputStream);
			streamWagon.disconnect();
		}
		return artifactInfo;

	}

	public ByteArrayOutputStream getArtifact(String artifactReference) throws ConnectionException {
		StreamingWagon streamWagon = null;
		ByteArrayOutputStream outputStream = null;
		try {
			if (repositoryLocation != null) {
				streamWagon = MvnRepoWagonConnectionManager.createWagon(repositoryLocation);
			} else {
				// TODO Need to add a new method to create Wagon based on the Parameters
			}
			outputStream = new ByteArrayOutputStream();
			streamWagon.getToStream(artifactReference, outputStream);
		} catch (ConnectionException e) {
			// TODO Auto-generated catch block

		} catch (AuthenticationException e) {
			// TODO Auto-generated catch block

		} catch (Exception e) {
			// TODO Auto-generated catch block

		} finally {
			IOUtil.close(outputStream);
			streamWagon.disconnect();
		}
		return outputStream;
	}

	/**
	 * Deletes Artifacts from Nexus Repository
	 * 
	 * @param artifactReference
	 *            : Artifact path to be deleted
	 * @throws URISyntaxException 
	 *             on bad URI
	 */
	public void deleteArtifact(String artifactReference) throws URISyntaxException  {

		if (restTemplate != null && artifactReference != null) {
			URI url = null;
			if (repositoryLocation.getUrl().endsWith("/")) {
				url = new URI(repositoryLocation.getUrl() + artifactReference);
			} else {
				url = new URI(repositoryLocation.getUrl() + "/" + artifactReference);
			}
			restTemplate.delete(url);
		}

	}

}
