package org.acumos.be.test.controller;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acumos.portal.be.common.JsonResponse;
import org.acumos.portal.be.common.exception.AcumosServiceException;
import org.acumos.portal.be.controller.DeployCloudController;
import org.acumos.portal.be.service.impl.DeployCloudServiceImpl;
import org.acumos.portal.be.transport.K8ConfigValue;
import org.acumos.portal.be.transport.MLK8SiteConfig;
import org.acumos.portal.be.util.PortalConstants;
import org.apache.http.HttpStatus;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.github.tomakehurst.wiremock.junit.WireMockRule;

public class DeployCloudControllerTest {
	
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	@InjectMocks
	DeployCloudController deployCloudController;
	@Mock
	DeployCloudServiceImpl deployCloudServiceImpl;
	
	@Mock
	Environment env;
	
	@Rule
	public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().port(8000));
	
	final HttpServletResponse response = new MockHttpServletResponse();
	final HttpServletRequest request = new MockHttpServletRequest();
	public static final String DEPLOY_TO_K8="/deploy/";
	public static final String LOCALHOST_URL="http:localhost:8000/";

	@Test
	public void deployToK8Test() {
		
		String userId ="101";
		String solutionId = "Sol_test";
		String revisionId = "rev_test";
		String envId ="env_test";
		JsonResponse<String> jsonRes=new JsonResponse<>();
		String json="{ \"taskId\": 9999 }";
		jsonRes.setContent(json);
		ResponseEntity<String> responseEntity=new ResponseEntity<>(json,org.springframework.http.HttpStatus.ACCEPTED);

		stubFor(post(urlEqualTo(DEPLOY_TO_K8)).willReturn(
                aResponse().withStatus(HttpStatus.SC_ACCEPTED).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withBody("{ \"taskId\": 9999 }")));
		Mockito.when(env.getProperty("k8_deploy.url")).thenReturn(LOCALHOST_URL);
		when(deployCloudServiceImpl.deployToK8(userId, solutionId, revisionId, envId)).thenReturn(responseEntity);
		JsonResponse<String> jsonResponseSuccess=deployCloudController.deployToK8(request, userId, solutionId, revisionId, envId, response);
		assertNotNull(jsonResponseSuccess);
		assertEquals(json, jsonResponseSuccess.getResponseBody());
		
		ResponseEntity<String> responseEntityFail=new ResponseEntity<>(org.springframework.http.HttpStatus.BAD_REQUEST);
		when(deployCloudServiceImpl.deployToK8(userId, solutionId, revisionId, envId)).thenReturn(responseEntityFail);
		JsonResponse<String> jsonResponseFail=deployCloudController.deployToK8(request, userId, solutionId, revisionId, envId, response);
		assertNull(jsonResponseFail.getResponseBody());
	}
	
	@Test
	public void getDeployToK8ConfigTest() throws AcumosServiceException {
		String userId ="101";
		MLK8SiteConfig configValue=new MLK8SiteConfig();
		configValue.setConfigKey(PortalConstants.K8CLUSTER_CONFIG_KEY);
		configValue.setUserId("101");
		K8ConfigValue k8configValue=new K8ConfigValue();
		List<K8ConfigValue> list=new ArrayList<>();
		k8configValue.setName("CLUSTER1");
		k8configValue.setName("CLUSTER2");
		list.add(k8configValue);
		configValue.setK8ConfigValueList(list);
		
		
		when(deployCloudServiceImpl.getSiteConfig(PortalConstants.K8CLUSTER_CONFIG_KEY)).thenReturn(configValue);
		JsonResponse<MLK8SiteConfig> successResponse=deployCloudController.getDeployToK8Config(request, userId, response);
		assertNotNull(successResponse);
		assertEquals(configValue, successResponse.getResponseBody());
		
		when(deployCloudServiceImpl.getSiteConfig(PortalConstants.K8CLUSTER_CONFIG_KEY)).thenReturn(null);
		JsonResponse<MLK8SiteConfig> failResponse=deployCloudController.getDeployToK8Config(request, userId, response);
		assertNull(failResponse.getResponseBody());
	}
}
