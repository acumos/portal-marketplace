package org.acumos.be.test.service.impl;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import java.lang.invoke.MethodHandles;

import org.acumos.cds.domain.MLPSiteConfig;
import org.acumos.portal.be.common.exception.AcumosServiceException;
import org.acumos.portal.be.service.impl.DeployCloudServiceImpl;
import org.acumos.portal.be.transport.MLK8SiteConfig;
import org.acumos.portal.be.util.PortalConstants;
import org.apache.http.HttpStatus;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

public class DeployCloudServiceImplTest {

	@InjectMocks
	DeployCloudServiceImpl deployCloudServiceImpl;
	
	@Mock
	Environment env;
	@Rule
	public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().port(8000));
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	public static final String LOCAL_HOST="http://localhost:8000/";
	public static final String DEPLOY_TO_K8="/deploy/";
	private final String url = LOCAL_HOST+"ccds";
	private final String user = "ccds_client";
	private final String pass = "ccds_client";
	private static final String GET_K8SITECONFIG = "/ccds/site/config/"+PortalConstants.K8CLUSTER_CONFIG_KEY;

	@Test
	public void getSiteConfigTest() throws AcumosServiceException {
		
		MLPSiteConfig siteConfig = new MLPSiteConfig();
		siteConfig.setConfigKey(PortalConstants.K8CLUSTER_CONFIG_KEY);
		siteConfig.setUserId("101");
		siteConfig.setConfigValue("[ { \"name\": \"cluster1\" }, { \"name\": \"cluster2\" }, { \"name\": \"cluster3\" } ]");
		ObjectMapper mapper = new ObjectMapper();
		String jsonResponse=null;
		try {
			jsonResponse=mapper.writeValueAsString(siteConfig);
		} catch (JsonProcessingException e) {
			logger.error("Exception occurred while parsing rest page response to string ",e.getMessage());
		}
		stubFor(get(urlEqualTo(GET_K8SITECONFIG)).willReturn(
                aResponse().withStatus(HttpStatus.SC_ACCEPTED).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withBody(jsonResponse)));
		when(env.getProperty("cdms.client.url")).thenReturn(url);
		when(env.getProperty("cdms.client.username")).thenReturn(user);
		when(env.getProperty("cdms.client.password")).thenReturn(pass);
		MLK8SiteConfig successResponse= deployCloudServiceImpl.getSiteConfig(PortalConstants.K8CLUSTER_CONFIG_KEY);
		assertNotNull(successResponse);
		assertEquals(siteConfig.getConfigKey(), successResponse.getConfigKey());
		
	}
	
	@Test
	public void deployToK8Test() {
		String userId ="101";
		String solutionId = "Sol_test";
		String revisionId = "rev_test";
		String envId ="env_test";
		String json="{ \"taskId\": 9999 }";
		stubFor(post(urlEqualTo(DEPLOY_TO_K8)).willReturn(
                aResponse().withStatus(HttpStatus.SC_ACCEPTED).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withBody("{ \"taskId\": 9999 }")));
		
		when(env.getProperty("k8_deploy.url")).thenReturn(LOCAL_HOST);
		ResponseEntity<String> jsonResponseSuccess=deployCloudServiceImpl.deployToK8(userId, solutionId, revisionId, envId);
		assertNotNull(jsonResponseSuccess);
		assertEquals(json, jsonResponseSuccess.getBody().toString());
	}
}
