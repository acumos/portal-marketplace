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
package org.acumos.be.test.config;

import static org.junit.Assert.assertTrue;

import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.acumos.cds.domain.MLPPeer;
import org.acumos.cds.domain.MLPSolution;
import org.acumos.portal.be.common.Clients;
import org.acumos.portal.be.common.GatewayClient;
import org.acumos.portal.be.common.JsonResponse;
import org.acumos.portal.be.config.HttpClientConfigurationBuilder;
import org.apache.http.client.HttpClient;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringRunner;



/**
 */
@RunWith(SpringRunner.class)
@ContextHierarchy({
	@ContextConfiguration(classes = org.acumos.portal.be.config.GatewayClientConfiguration.class)
})
@SpringBootTest(classes = org.acumos.portal.be.Application.class,
								webEnvironment = WebEnvironment.RANDOM_PORT,
								properties = {
										"client.ssl.key-store=classpath:acumosa.pkcs12",
										"client.ssl.key-store-password=acumosa",
										"client.ssl.key-store-type=PKCS12",
										"client.ssl.key-password = acumosa",
										"client.ssl.trust-store=classpath:acumosTrustStore.jks",
										"client.ssl.trust-store-password=acumos",
										"client.ssl.client-auth=need",
										"gateway.url=http://localhost/gateway"
								})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FederationClientTest {

	private final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());	
		
	@Autowired
	private Clients clients;
	
	
	@Test
	public void testHttpClient() {
		
		/*
		 * HttpClient client = prepareHttpClient(); assertTrue(client != null);
		 */	
	}	
	
	@Test
	public void testFederationClient() {
		
		GatewayClient  federationClient = clients.getGatewayClient();
		assertTrue(federationClient  != null);		
	}
	
	@Test
	public void testPing() {
		
		GatewayClient  federationClient = clients.getGatewayClient();
		assertTrue(federationClient  != null);
		String peerId="b87b36f0-a600-441d-bd7c-8472bebf81ce";
		JsonResponse<MLPPeer> response  = federationClient.ping(peerId);		
		assertTrue(response == null);				
	}
	
	@Test
	public void testGetSolutions() {
		
		GatewayClient  federationClient = clients.getGatewayClient();
		assertTrue(federationClient  != null);
		//Map<String, Object> theSelection = new HashMap<String, Object>();
		//String selection = "{\"modelTypeCode\":\"CL\",\"toolKitTypeCode\":\"SK\"}";
		//theSelection.put("modelTypeCode", "CL");
		String peerId="b87b36f0-a600-441d-bd7c-8472bebf81ce";
		String theSelection = "{\"modelTypeCode\":\"CL\",\"toolKitTypeCode\":\"SK\"}";
		//String theSelection = "toolkit:CL";
		JsonResponse<List<MLPSolution>> response  = federationClient.getSolutions(peerId,theSelection);		
		assertTrue(response == null);				
	}
	
	@Test
	public void testSolution() {
		
		GatewayClient  federationClient = clients.getGatewayClient();
		assertTrue(federationClient  != null);
		String peerId="b87b36f0-a600-441d-bd7c-8472bebf81ce";
		String solutionId = "test1c351f5d-3e38-419a-873a-ce2e63bb1d7d";
		JsonResponse<MLPSolution> response = federationClient.getSolution(peerId,solutionId);		
		assertTrue(response == null);				
	}

	private HttpClient prepareHttpClient() {
		return new HttpClientConfigurationBuilder()
								.withSSL(new HttpClientConfigurationBuilder.SSLBuilder()
															.withKeyStore("classpath:/acumosa.pkcs12")
															.withKeyStorePassword("acumosa")
															//.withKeyPassword("acumosb")
															.withTrustStore("classpath:/acumosTrustStore.jks")
															.withTrustStoreType("JKS")
															.withTrustStorePassword("acumos")
															.build())
								.buildConfig()
								.buildClient();
	}
}

