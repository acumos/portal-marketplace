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
package org.acumos.be.test.controller;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.mockito.Mockito.when;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acumos.cds.domain.MLPCatalog;
import org.acumos.cds.domain.MLPPeer;
import org.acumos.cds.domain.MLPPeerSubscription;
import org.acumos.cds.domain.MLPSolution;
import org.acumos.federation.client.GatewayClient;
import org.acumos.federation.client.config.BasicAuthConfig;
import org.acumos.federation.client.config.ClientConfig;
import org.acumos.federation.client.config.TlsConfig;
import org.acumos.portal.be.common.Clients;
import org.acumos.portal.be.common.JsonRequest;
import org.acumos.portal.be.common.JsonResponse;
import org.acumos.portal.be.controller.GatewayController;
import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.github.tomakehurst.wiremock.junit.WireMockRule;

@RunWith(MockitoJUnitRunner.class)
public class GatewayControllerTest {

	@InjectMocks
	private GatewayController gatewayController;
	
	@Mock
	private Clients clients;
	
	@Rule
	public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().port(9084));
	
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	
	private static final String peerApiUrl = "http://localhost:9084";
	private HttpServletResponse response = new MockHttpServletResponse();
	private HttpServletRequest request = new MockHttpServletRequest();
	
	@Test
	public void getPingTest() {
		
		stubFor(get(urlEqualTo("/peer/somepeerid/ping")).willReturn(
                aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withBody("{}")));
		ClientConfig cconf = getConfig("acumosa");
		cconf.getSsl().setKeyAlias("acumosa");
		GatewayClient client = new GatewayClient(peerApiUrl, cconf);
		when(clients.getGatewayClient()).thenReturn(client);
		JsonResponse<MLPPeer> result = gatewayController.pingGateway(request, "somepeerid", response);
		Assert.assertNotNull(result);
	}
	
	@Test
	public void getSolutions() {
		
		JsonRequest<MLPPeerSubscription> peerSubscription = new JsonRequest<>();
		MLPPeerSubscription sub=new MLPPeerSubscription();
		sub.setSubId(1234L);
		sub.setPeerId("somepeerid");
		sub.setUserId("1a8e8b73-1ce7-41e8-a364-93f5b57deb14");
		
		String selector="{\"catalogId\":\"somecatid\"}";
		sub.setSelector(selector);
		peerSubscription.setBody(sub);
		ClientConfig cconf = getConfig("acumosa");
		cconf.getSsl().setKeyAlias("acumosa");
		stubFor(get(urlEqualTo("/peer/somepeerid/solutions?catalogId=somecatid")).willReturn(
                aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withBody("{\"content\":[{\"solutionId\":\"someId\"},{\"solutionId\":\"someOtherId\"}]}")));
		GatewayClient client = new GatewayClient(peerApiUrl, cconf);
		when(clients.getGatewayClient()).thenReturn(client);
		JsonResponse<List<MLPSolution>> result = gatewayController.getSolutions(request, peerSubscription, response);
		Assert.assertNotNull(result);
	}
	
	@Test
	public void getSolution() {

		ClientConfig cconf = getConfig("acumosa");
		cconf.getSsl().setKeyAlias("acumosa");
		stubFor(get(urlEqualTo("/peer/somepeerid/solutions/someid")).willReturn(
                aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withBody("{\"content\":{\"solutionId\":\"someId\",\"picture\":\"9999\",\"revisions\":[{\"artifacts\":[],\"documents\":[],\"revCatDescription\":{}}]}}")));
		GatewayClient client = new GatewayClient(peerApiUrl, cconf);
		when(clients.getGatewayClient()).thenReturn(client);	
		JsonResponse<MLPSolution> result = gatewayController.getSolution(request, "someid", "somepeerid", response);
		Assert.assertNotNull(result);
	}
	
	@Test
	public void getCatalogsTest() {

		ClientConfig cconf = getConfig("acumosa");
		cconf.getSsl().setKeyAlias("acumosa");
		stubFor(get(urlEqualTo("/peer/somepeerid/catalogs")).willReturn(
                aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withBody("{ \"content\": [ { \"catalogId\": \"1\" }, { \"catalogId\": \"2\" }]}")));
		GatewayClient client = new GatewayClient(peerApiUrl, cconf);
		when(clients.getGatewayClient()).thenReturn(client);	
		JsonResponse<List<MLPCatalog>> result = gatewayController.getCatalogs(request, "somepeerid", response);
		Assert.assertNotNull(result);
	}
	
	public static ClientConfig getConfig(String name) {
		ClientConfig ret = new ClientConfig();
		TlsConfig tls = new TlsConfig();
		tls.setKeyStore("classpath:" + name + ".pkcs12");
		tls.setKeyStoreType("PKCS12");
		tls.setKeyStorePassword(name);
		tls.setTrustStore("classpath:acumosTrustStore.jks");
		tls.setTrustStorePassword("acumos");
		ret.setSsl(tls);
		BasicAuthConfig creds = new BasicAuthConfig();
		creds.setUsername(name);
		creds.setPassword(name);
		ret.setCreds(creds);
		return ret;
	}
}
