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

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.acumos.cds.domain.MLPPeer;
import org.acumos.cds.domain.MLPPeerSubscription;
import org.acumos.cds.domain.MLPRole;
import org.acumos.cds.domain.MLPSiteConfig;
import org.acumos.cds.domain.MLPUser;
import org.acumos.cds.transport.RestPageRequest;
import org.acumos.cds.transport.RestPageResponse;
import org.acumos.portal.be.common.JsonRequest;
import org.acumos.portal.be.common.JsonResponse;
import org.acumos.portal.be.common.RestPageResponseBE;
import org.acumos.portal.be.controller.AdminServiceController;
import org.acumos.portal.be.service.AdminService;
import org.acumos.portal.be.service.UserRoleService;
import org.acumos.portal.be.service.UserService;
import org.acumos.portal.be.service.impl.AdminServiceImpl;
import org.acumos.portal.be.transport.MLRequest;
import org.acumos.portal.be.transport.MLSolution;
import org.acumos.portal.be.transport.TransportData;
import org.acumos.portal.be.transport.User;
import org.acumos.portal.be.util.EELFLoggerDelegate;
import org.acumos.portal.be.util.PortalUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(MockitoJUnitRunner.class)
public class AdminServiceControllerTest {
	private static final EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(AdminServiceControllerTest.class);
	
	@Autowired
	private MockMvc mockMvc;
	
	@Mock
	private AdminServiceImpl adminImpl;
	
	@Mock
	private AdminService adminService;
	
	@InjectMocks
	private AdminServiceController adminController;
	
	@Mock
	UserService userService;
	
	@Autowired
	private UserRoleService userRoleService;

	
	@Mock
	private Environment env;

	private HttpServletResponse response = new MockHttpServletResponse();
	private HttpServletRequest request = new MockHttpServletRequest();

	@Before
	public void setUp() throws Exception {
		mockMvc = standaloneSetup(adminController).build();
	}
	
	@Test
	public void removePeerTest() {

			MLPPeer mlpPeer = new MLPPeer();
			mlpPeer.setApiUrl("http://peer-api");
			mlpPeer.setContact1("Contact1");
			Date created = new Date();
			mlpPeer.setCreated(created);
			mlpPeer.setDescription("Peer description");
			mlpPeer.setName("Peer-1509357629935");
			mlpPeer.setPeerId(String.valueOf(Math.incrementExact(0)));
			mlpPeer.setSelf(false);
			mlpPeer.setSubjectName("peer Subject name");
			mlpPeer.setWebUrl("https://web-url");
			Assert.assertNotNull(mlpPeer);
			JsonRequest<MLPPeer> peerReq = new JsonRequest<>();
			peerReq.setBody(mlpPeer);
			JsonResponse<Object> peerRes = new JsonResponse<>();
			peerRes.setResponseBody(mlpPeer);

			String peerId = mlpPeer.getPeerId();
			Assert.assertNotNull(peerId);
			
			AdminService myList = mock(AdminService.class);
		    doNothing().when(myList).removePeer(isA(String.class));
		    myList.removePeer(peerId);
		    peerRes = adminController.removePeer(peerId);
			logger.info("Successfully removed the peer: " + peerRes.getResponseBody());
			Assert.assertNotNull(peerRes);

			String peerId1 =null;
			adminController.removePeer(peerId1);
	}

	@Test
	public void getPeerSubscriptionsTest() {
		try {
			MLPPeer mlpPeer = new MLPPeer();
			mlpPeer.setApiUrl("http://peer-api");
			mlpPeer.setContact1("Contact1");
			Date created = new Date();
			mlpPeer.setCreated(created);
			mlpPeer.setDescription("Peer description");
			mlpPeer.setName("Peer-1509357629935");
			mlpPeer.setPeerId(String.valueOf(Math.incrementExact(0)));
			mlpPeer.setSelf(false);
			mlpPeer.setSubjectName("peer Subject name");
			mlpPeer.setWebUrl("https://web-url");
			Assert.assertNotNull(mlpPeer);
			MLPPeerSubscription mlpPeerSubcription = new MLPPeerSubscription();
			mlpPeerSubcription.setPeerId(mlpPeer.getPeerId());
			mlpPeerSubcription.setSubId((long) Math.incrementExact(0));
			mlpPeerSubcription.setCreated(created);
			Assert.assertNotNull(mlpPeerSubcription);
			List<MLPPeerSubscription> subScriptionList = new ArrayList<>();
			subScriptionList.add(mlpPeerSubcription);
			JsonResponse<List<MLPPeerSubscription>> subscriptionRes = new JsonResponse<>();
			subscriptionRes.setResponseBody(subScriptionList);
			String peerId = mlpPeer.getPeerId();
			Assert.assertNotNull(peerId);
			Mockito.when(adminService.getPeerSubscriptions(peerId)).thenReturn(subScriptionList);
			Assert.assertNotNull(subScriptionList);
			subscriptionRes = adminController.getPeerSubscriptions(peerId);
			logger.info("Subscription List : " + subscriptionRes.getResponseBody());
			Assert.assertNotNull(subscriptionRes);

		} catch (Exception e) {
			logger.info("failed tot execute the above test case");
		}
	}

	@Test
	public void getPeerSubscriptionDetailsTest() {

		try {
			MLPPeer mlpPeer = new MLPPeer();
			mlpPeer.setApiUrl("http://peer-api");
			mlpPeer.setContact1("Contact1");
			Date created = new Date();
			mlpPeer.setCreated(created);
			mlpPeer.setDescription("Peer description");
			mlpPeer.setName("Peer-1509357629935");
			mlpPeer.setPeerId(String.valueOf(Math.incrementExact(0)));
			mlpPeer.setSelf(false);
			mlpPeer.setSubjectName("peer Subject name");
			mlpPeer.setWebUrl("https://web-url");
			Assert.assertNotNull(mlpPeer);
			MLPPeerSubscription mlpPeerSubcription = new MLPPeerSubscription();
			mlpPeerSubcription.setPeerId(mlpPeer.getPeerId());
			mlpPeerSubcription.setSubId((long) Math.incrementExact(0));
			mlpPeerSubcription.setCreated(created);
			List<MLPPeerSubscription> subScriptionList = new ArrayList<>();
			subScriptionList.add(mlpPeerSubcription);
			JsonResponse<MLPPeerSubscription> subscriptionRes = new JsonResponse<>();
			subscriptionRes.setResponseBody(mlpPeerSubcription);
			Long subId = mlpPeerSubcription.getSubId();
			Mockito.when(adminService.getPeerSubscription(subId)).thenReturn(mlpPeerSubcription);
			Assert.assertNotNull(mlpPeerSubcription);
			subscriptionRes = adminController.getPeerSubscriptionDetails(subId);
			logger.info("Peer Subscription Details  : " + subscriptionRes.getResponseBody());
			Assert.assertNotNull(subscriptionRes);
		} catch (Exception e) {
			logger.info("failed tot execute the above test case");
		}
	}

	@Test
	public void createPeerSubscriptionTest() {
		try {
			MLPPeer mlpPeer = new MLPPeer();
			mlpPeer.setApiUrl("http://peer-api");
			mlpPeer.setContact1("Contact1");
			Date created = new Date();
			mlpPeer.setCreated(created);
			mlpPeer.setDescription("Peer description");
			mlpPeer.setName("Peer-1509357629935");
			mlpPeer.setPeerId(String.valueOf(Math.incrementExact(0)));
			mlpPeer.setSelf(false);
			mlpPeer.setSubjectName("peer Subject name");
			mlpPeer.setWebUrl("https://web-url");
			Assert.assertNotNull(mlpPeer);
			MLPPeerSubscription mlpPeerSubcription = new MLPPeerSubscription();
			mlpPeerSubcription.setPeerId(mlpPeer.getPeerId());
			mlpPeerSubcription.setSubId((long) Math.incrementExact(0));
			mlpPeerSubcription.setCreated(created);
			Assert.assertNotNull(mlpPeerSubcription);
			JsonRequest<MLPPeerSubscription> subscriptionReq = new JsonRequest<>();
			subscriptionReq.setBody(mlpPeerSubcription);
			JsonResponse<MLPPeerSubscription> subscriptionRes = new JsonResponse<>();
			subscriptionRes.setResponseBody(mlpPeerSubcription);
			Mockito.when(adminService.createPeerSubscription(subscriptionReq.getBody())).thenReturn(mlpPeerSubcription);
			subscriptionRes = adminController.createPeerSubscription(subscriptionReq);
			logger.info("Successfully created peer subcrition  : " + subscriptionRes.getResponseBody());
			Assert.assertNotNull(subscriptionRes);
		} catch (Exception e) {
			logger.info("failed tot execute the above test case");
		}

	}

	@Test
	public void updatePeerSubscriptionTest() {

		
			MLPPeer mlpPeer = new MLPPeer();
			mlpPeer.setApiUrl("http://peer-api");
			mlpPeer.setContact1("Contact1");
			Date created = new Date();
			mlpPeer.setCreated(created);
			mlpPeer.setDescription("Peer description");
			mlpPeer.setName("Peer-1509357629935");
			mlpPeer.setPeerId(String.valueOf(Math.incrementExact(0)));
			mlpPeer.setSelf(false);
			mlpPeer.setSubjectName("peer Subject name");
			mlpPeer.setWebUrl("https://web-url");
			Assert.assertNotNull(mlpPeer);
			MLPPeerSubscription mlpPeerSubcription = new MLPPeerSubscription();
			mlpPeerSubcription.setPeerId(mlpPeer.getPeerId());
			mlpPeerSubcription.setSubId((long) Math.incrementExact(0));
			mlpPeerSubcription.setCreated(created);
			Assert.assertNotNull(mlpPeerSubcription);
			JsonRequest<MLPPeerSubscription> subscriptionReq = new JsonRequest<>();
			subscriptionReq.setBody(mlpPeerSubcription);
			JsonResponse<Object> subscriptionRes = new JsonResponse<>();
			subscriptionRes.setResponseBody(mlpPeerSubcription);
			AdminService myList = mock(AdminService.class);
		    doNothing().when(myList).updatePeerSubscription(isA(MLPPeerSubscription.class));
		    myList.updatePeerSubscription(subscriptionReq.getBody());
			subscriptionRes = adminController.updatePeerSubscription(subscriptionReq);
			logger.info("Successfully updated peer subscription  : " + subscriptionRes.getResponseBody());
			Assert.assertNotNull(subscriptionRes);
		
	}

	@Test
	public void deletePeerSubscriptionTest() {

		try {
			MLPPeer mlpPeer = new MLPPeer();
			mlpPeer.setApiUrl("http://peer-api");
			mlpPeer.setContact1("Contact1");
			Date created = new Date();
			mlpPeer.setCreated(created);
			mlpPeer.setDescription("Peer description");
			mlpPeer.setName("Peer-1509357629935");
			mlpPeer.setPeerId(String.valueOf(Math.incrementExact(0)));
			mlpPeer.setSelf(false);
			mlpPeer.setSubjectName("peer Subject name");
			mlpPeer.setWebUrl("https://web-url");
			Assert.assertNotNull(mlpPeer);
			MLPPeerSubscription mlpPeerSubcription = new MLPPeerSubscription();
			mlpPeerSubcription.setPeerId(mlpPeer.getPeerId());
			mlpPeerSubcription.setSubId((long) Math.incrementExact(0));
			mlpPeerSubcription.setCreated(created);
			Assert.assertNotNull(mlpPeerSubcription);
			Long subId = mlpPeerSubcription.getSubId();

			JsonRequest<MLPPeerSubscription> subscriptionReq = new JsonRequest<>();
			subscriptionReq.setBody(mlpPeerSubcription);
			JsonResponse<Object> subscriptionRes = new JsonResponse<>();
			subscriptionRes.setResponseBody(mlpPeerSubcription);
			AdminService myList = mock(AdminService.class);
		    doNothing().when(myList).deletePeerSubscription(isA(Long.class));
		    myList.deletePeerSubscription(subId);
			subscriptionRes = adminController.deletePeerSubscription(subId);
			logger.info("Successfully deleted peer subscription  : " + subscriptionRes.getResponseBody());
			Assert.assertNotNull(subscriptionRes);
			

			subscriptionRes = adminController.deletePeerSubscription(null);
			
			Assert.assertNotNull(subscriptionRes);
			Assert.assertEquals("Remove Peer Subscription Failed", subscriptionRes.getResponseDetail());
		   //Assert.assertEquals("Success", subscriptionRes.getResponseDetail());
		} catch (Exception e) {
			logger.info("failed tot execute the above test case");
		}

	}

	@Test
	public void getSiteConfigurationTest() {
		try {
			MLPSiteConfig mlpSiteConfig = new MLPSiteConfig();
			mlpSiteConfig.setConfigKey("Site_configuration_Key");
			mlpSiteConfig.setConfigValue(
					"{\"siteInstanceName\":\"Acumos\",\"ConnectionConfig\": {\"socketTimeout\":\"300\",\"connectionTimeout\":\"10\"}}");
			mlpSiteConfig.setUserId("41058105-67f4-4461-a192-f4cb7fdafd34");
			Date created = new Date();
			mlpSiteConfig.setCreated(created);
			Date modified = new Date();
			mlpSiteConfig.setModified(modified);
			Assert.assertNotNull(mlpSiteConfig);
			JsonResponse<MLPSiteConfig> configRes = new JsonResponse<>();
			configRes.setResponseBody(mlpSiteConfig);
			Assert.assertNotNull(configRes);
			String configKey = mlpSiteConfig.getConfigKey();
			Mockito.when(adminService.getSiteConfig(configKey)).thenReturn(mlpSiteConfig);
			configRes = adminController.getSiteConfiguration(configKey, mock(HttpServletResponse.class));
			logger.info("Site Configuration Details :" + configRes.getResponseBody());
			Assert.assertNotNull(configRes);
		} catch (Exception e) {
			logger.info("failed tot execute the above test case");
		}
	}

	@Test
	public void createSiteConfigTest() {
		try {
			MLPSiteConfig mlpSiteConfig = new MLPSiteConfig();
			mlpSiteConfig.setConfigKey("Site_configuration_Key");
			mlpSiteConfig.setConfigValue(
					"{\"siteInstanceName\":\"Acumos\",\"ConnectionConfig\": {\"socketTimeout\":\"300\",\"connectionTimeout\":\"10\"}}");
			mlpSiteConfig.setUserId("41058105-67f4-4461-a192-f4cb7fdafd34");
			Date created = new Date();
			mlpSiteConfig.setCreated(created);
			Date modified = new Date();
			mlpSiteConfig.setModified(modified);
			Assert.assertNotNull(mlpSiteConfig);
			JsonResponse<MLPSiteConfig> configRes = new JsonResponse<>();
			configRes.setResponseBody(mlpSiteConfig);
			Assert.assertNotNull(configRes);
			JsonRequest<MLPSiteConfig> mlpSiteConfigReq = new JsonRequest<>();
			mlpSiteConfigReq.setBody(mlpSiteConfig);
			Assert.assertNotNull(mlpSiteConfigReq);
			String configKey = mlpSiteConfig.getConfigKey();
			Assert.assertNotNull(configKey);
			
			Mockito.when(adminService.getSiteConfig(configKey)).thenReturn(mlpSiteConfig);
			configRes = adminController.createSiteConfig(mlpSiteConfigReq, mock(HttpServletResponse.class));
			logger.info("created Configuration Details :" + configRes.getResponseBody());
			Assert.assertNotNull(configRes);
			

			configRes = adminController.createSiteConfig(null, mock(HttpServletResponse.class));
			Assert.assertNotNull(configRes);
		} catch (Exception e) {
			logger.info("failed tot execute the above test case");
		}
	}

	@Test
	public void updateSiteConfigTest() {

		try {
			MLPSiteConfig mlpSiteConfig = new MLPSiteConfig();
			mlpSiteConfig.setConfigKey("Site_configuration_Key");
			mlpSiteConfig.setConfigValue(
					"{\"siteInstanceName\":\"Acumos\",\"ConnectionConfig\": {\"socketTimeout\":\"300\",\"connectionTimeout\":\"10\"}}");
			mlpSiteConfig.setUserId("41058105-67f4-4461-a192-f4cb7fdafd34");
			Date created = new Date();
			mlpSiteConfig.setCreated(created);
			Date modified = new Date();
			mlpSiteConfig.setModified(modified);
			Assert.assertNotNull(mlpSiteConfig);
			JsonResponse<MLPSiteConfig> configRes = new JsonResponse<>();
			configRes.setResponseBody(mlpSiteConfig);
			Assert.assertNotNull(configRes);
			JsonRequest<MLPSiteConfig> mlpSiteConfigReq = new JsonRequest<>();
			mlpSiteConfigReq.setBody(mlpSiteConfig);
			Assert.assertNotNull(mlpSiteConfigReq);
			String configKey = mlpSiteConfig.getConfigKey();
			
			AdminService myList = mock(AdminService.class);
		    doNothing().when(myList).updateSiteConfig(isA(MLPSiteConfig.class));
		    myList.updateSiteConfig(mlpSiteConfigReq.getBody());
			configRes = adminController.updateSiteConfig(configKey, mlpSiteConfigReq, mock(HttpServletResponse.class));
			logger.info("Updated  Configuration Details :" + configRes.getResponseBody());
			Assert.assertNotNull(configRes);
		} catch (Exception e) {
			logger.info("failed tot execute the above test case");
		}

	}

	@Test
	public void deleteSiteConfigTest() {

		try {
			MLPSiteConfig mlpSiteConfig = new MLPSiteConfig();
			mlpSiteConfig.setConfigKey("Site_configuration_Key");
			mlpSiteConfig.setConfigValue(
					"{\"siteInstanceName\":\"Acumos\",\"ConnectionConfig\": {\"socketTimeout\":\"300\",\"connectionTimeout\":\"10\"}}");
			mlpSiteConfig.setUserId("41058105-67f4-4461-a192-f4cb7fdafd34");
			Date created = new Date();
			mlpSiteConfig.setCreated(created);
			Date modified = new Date();
			mlpSiteConfig.setModified(modified);
			Assert.assertNotNull(mlpSiteConfig);
			JsonResponse<Object> configRes = new JsonResponse<>();
			configRes.setResponseBody(mlpSiteConfig);
			Assert.assertNotNull(configRes);
			JsonRequest<MLPSiteConfig> mlpSiteConfigReq = new JsonRequest<>();
			mlpSiteConfigReq.setBody(mlpSiteConfig);
			Assert.assertNotNull(mlpSiteConfigReq);
			String configKey = mlpSiteConfig.getConfigKey();
			
			AdminService myList = mock(AdminService.class);
		    doNothing().when(myList).deleteSiteConfig(isA(String.class));
		    myList.deleteSiteConfig(configKey);
			configRes = adminController.deleteSiteConfig(configKey, mock(HttpServletResponse.class));
			logger.info("Deleted Configuration Details :" + configRes.getResponseBody());
			Assert.assertNotNull(configRes);
		} catch (Exception e) {
			logger.info("failed tot execute the above test case");
		}
	}

	@Test
	public void getPeerListTest() {
		try {
			MLPPeer mlpPeer = new MLPPeer();
			mlpPeer.setApiUrl("http://peer-api");
			mlpPeer.setContact1("Contact1");
			Date created = new Date();
			mlpPeer.setCreated(created);
			mlpPeer.setDescription("Peer description");
			mlpPeer.setName("Peer-1509357629935");
			mlpPeer.setPeerId(String.valueOf(Math.incrementExact(0)));
			mlpPeer.setSelf(false);
			mlpPeer.setSubjectName("peer Subject name");
			mlpPeer.setWebUrl("https://web-url");
			Assert.assertNotNull(mlpPeer);
			JsonResponse<RestPageResponse<MLPPeer>> peerRes = new JsonResponse<>();
			RestPageResponse<MLPPeer> responseBody = new RestPageResponse<>();
			responseBody.setNumberOfElements(2);
			peerRes.setResponseBody(responseBody);

			RestPageRequest restPageReq = new RestPageRequest();
			restPageReq.setPage(0);
			restPageReq.setSize(9);
			List<MLPPeer> peerList = new ArrayList<>();
			if (restPageReq.getPage() != null && restPageReq.getSize() != null) {
				peerList.add(mlpPeer);
			}
			
			Mockito.when(adminService.getAllPeers(restPageReq)).thenReturn(responseBody);
			peerRes = adminController.getPeerList(restPageReq);
			Assert.assertNotNull(peerRes);
		} catch (Exception e) {
			logger.info("failed tot execute the above test case");
		}
	}

	@Test
	public void getPeerDetailsTest() {
		try {
			MLPPeer mlpPeer = new MLPPeer();
			mlpPeer.setApiUrl("http://peer-api");
			mlpPeer.setContact1("Contact1");
			Date created = new Date();
			mlpPeer.setCreated(created);
			mlpPeer.setDescription("Peer description");
			mlpPeer.setName("Peer-1509357629935");
			mlpPeer.setPeerId("62e46a5a-2c26-4dee-b320-b4e48303d24d");
			mlpPeer.setSelf(true);
			mlpPeer.setSubjectName("peer Subject name");
			mlpPeer.setWebUrl("https://web-url");
			
			String peerId = "62e46a5a-2c26-4dee-b320-b4e48303d24d";
			Assert.assertNotNull(peerId);
			JsonResponse<MLPPeer> jsonResponse =  new JsonResponse<>();
			jsonResponse.setResponseBody(mlpPeer);
			Mockito.when(adminService.getPeerDetail(peerId)).thenReturn(mlpPeer);
			Assert.assertNotNull(mlpPeer);
			jsonResponse = adminController.getPeerDetails(peerId);
			Assert.assertNotNull(jsonResponse);
		} catch (Exception e) {
			logger.info("failed tot execute the above test case");
		}
	}

	@Test
	public void createPeerTest() {
	
			MLPPeer mlpPeer = new MLPPeer();
			mlpPeer.setApiUrl("http://peer-api");
			mlpPeer.setContact1("Contact1");
			Date created = new Date();
			mlpPeer.setCreated(created);
			mlpPeer.setDescription("Peer description");
			mlpPeer.setName("Peer-1509357629935");
			mlpPeer.setPeerId("c17c0562-c6df-4a0c-9702-ba8175eb23fd");
			mlpPeer.setSelf(true);
			mlpPeer.setSubjectName("peer Subject name");
			mlpPeer.setWebUrl("https://web-url");
			Assert.assertNotNull(mlpPeer);
			JsonRequest<MLPPeer> content = new  JsonRequest<>(); 
			content.setBody(mlpPeer);
			JsonResponse<Object> jsonResponse =  new JsonResponse<>();
			jsonResponse.setResponseBody(mlpPeer);
			String apiUrl = "http://peer-api";
			String webUrl = "https://web-url";
			Mockito.when(adminImpl.findPeerByApiAndWebUrl(apiUrl, webUrl)).thenReturn(mlpPeer);
			Mockito.when(adminImpl.savePeer(mlpPeer)).thenReturn(mlpPeer);
			jsonResponse = adminController.createPeer(content);
			Assert.assertNotNull(jsonResponse);
			
			content.setBody(null);
			jsonResponse = adminController.createPeer(content);
	}

	@Test
	public void updatePeerTest() {
			MLPPeer mlpPeer = new MLPPeer();
			mlpPeer.setApiUrl("http://peer-api");
			mlpPeer.setContact1("Contact1");
			mlpPeer.setDescription("Peer description");
			mlpPeer.setName("Peer-1509357629935");
			mlpPeer.setPeerId("c17c0562-c6df-4a0c-9702-ba8175eb23fd");
			mlpPeer.setSelf(true);
			mlpPeer.setSubjectName("peer Subject name");
			mlpPeer.setWebUrl("https://web-url");
			Assert.assertNotNull(mlpPeer);
			AdminServiceImpl mockImpl = mock(AdminServiceImpl.class);
			mockImpl.updatePeer(mlpPeer);
			JsonRequest<MLPPeer> peer = new JsonRequest<>();
			JsonResponse<Object> content = new JsonResponse<>();
			peer.setBody(mlpPeer);
			content.setStatus(true);
			content.setResponseDetail("Success");
			adminService.updatePeer(peer.getBody());
			AdminService myList = mock(AdminService.class);
		    doNothing().when(myList).updatePeer(isA(MLPPeer.class));
		    myList.updatePeer(peer.getBody());
			content = adminController.updatePeer(mlpPeer.getPeerId(),peer);
			Assert.assertNotNull(content);

			mlpPeer.setPeerId(null);
			peer.setBody(null);
			content = adminController.updatePeer(mlpPeer.getPeerId(),peer);
	}	
	
	@Test
	public void getVersionTest() {
		TransportData data=adminController.getVersion();
		Assert.assertNotNull(data);
	}
	
	@Test
	public void getDocurlTest() {
		JsonResponse<String> responseVO = adminController.getDocurl(request, response);
		Assert.assertNotNull(responseVO);
	}
	
	@Test
	public void getAllRequestsTest() {
		List<MLRequest> requestList = new ArrayList<>();
		MLRequest req = new MLRequest();
		req.setSender("senderName");
		req.setRequestId("ahgk162a");
		requestList.add(req);
		RestPageRequest restPageReq = new RestPageRequest();
		restPageReq.setPage(0);
		restPageReq.setSize(9);
		when(adminService.getAllRequests(restPageReq)).thenReturn(requestList);
		JsonResponse<RestPageResponseBE> response = adminController.getAllRequests(restPageReq);
		Assert.assertNotNull(response);
	}
	
	@Test
	public void updateRequestTest() {
		JsonRequest<MLRequest> mlrequest = new JsonRequest<>();
		MLRequest req = new MLRequest();
		req.setSender("senderName");
		req.setRequestId("1a8e8b73-1ce7-41e8-a364-93f5b57deb14");
		mlrequest.setBody(req);
		AdminService service = mock(AdminService.class);
		doNothing().when(service).updateMLRequest(req);
		JsonResponse<Object> response = adminController.updateRequest(mlrequest);
		Assert.assertNotNull(response);
	}
	
	@Test
	public void createSubscriptionTest() {
		String peerId = "agf145";
		JsonRequest<List<MLSolution>> solList = new JsonRequest<>();
		List<MLSolution> list = new ArrayList<>();
		MLSolution sol = new MLSolution();
		sol.setSolutionId("1a8e8b73-1ce7-41e8-a364-93f5b57deb14");
		sol.setName("Robot");
		list.add(sol);
		solList.setBody(list);
		AdminService service = mock(AdminService.class);
		doNothing().when(service).createSubscription(list, peerId);
		JsonResponse<MLPPeerSubscription> response = adminController.createSubscription(solList, peerId);
		Assert.assertNotNull(response);
	}
	

	@Test
	public void addUserRoleTest() {
		try {
			MLPRole mlpRole = new MLPRole();
			mlpRole.setName("Admin");
			Date created = new Date();
			mlpRole.setCreated(created);
			mlpRole.setRoleId("12345678-abcd-90ab-cdef-1234567890ab");
			Assert.assertNotNull(mlpRole);
			User user = new User();
			user.setActive("Y");
			user.setFirstName("test-first-name");
			user.setJwttoken(
					"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJNYW5pbW96aGlUMSIsInJvbGUiOm51bGwsImNyZWF0ZWQiOjE1MTAxMzgyMzY4NjcsImV4cCI6MTUxMDc0MzAzNiwibWxwdXNlciI6eyJjcmVhdGVkIjoxNTA4MjM0Njk2MDAwLCJtb2RpZmllZCI6MTUwOTk2MDg5NTAwMCwidXNlcklkIjoiNDEwNTgxMDUtNjdmNC00NDYxLWExOTItZjRjYjdmZGFmZDM0IiwiZmlyc3ROYW1lIjoiTWFuaW1vemhpVDEiLCJtaWRkbGVOYW1lIjpudWxsLCJsYXN0TmFtZSI6IlQyIiwib3JnTmFtZSI6bnVsbCwiZW1haWwiOiJNYW5pbW96aGlUMUBnbWFpLmNvbSIsImxvZ2luTmFtZSI6Ik1hbmltb3poaVQxIiwibG9naW5IYXNoIjpudWxsLCJsb2dpblBhc3NFeHBpcmUiOm51bGwsImF1dGhUb2tlbiI6bnVsbCwiYWN0aXZlIjp0cnVlLCJsYXN0TG9naW4iOm51bGwsInBpY3R1cmUiOm51bGx9fQ.bLzIAFVUIPMVE_WD0-BvMupFyHyy90mw_je1PmnvP34swv1ZUW_SL7DBoKeSGnIf_zhtDp8V8d3Q3pAiWMjLyA");
			user.setEmailId("testemail@test.com");
			user.setUserId("f0ebe707-d436-40cf-9b0a-ed1ce8da1f2b");
			user.setLoginName("test-User-Name");
			user.setRole(mlpRole.getName());
			user.setRoleId(mlpRole.getRoleId());
			List<String> newRoleList = new ArrayList<>();
			newRoleList.add("Admin");
			
			MLPUser mlpUser = PortalUtils.convertToMLPUserForUpdate(user);
			Assert.assertNotNull(user);
			String roleId = mlpRole.getRoleId();
			Assert.assertEquals(roleId, user.getRoleId());
			JsonRequest<User> userreq = new JsonRequest<>();
			userreq.setBody(user);
			JsonResponse<MLPRole> value = new JsonResponse<>();
			value.setResponseBody(mlpRole);
			String userId= user.getUserId();
			
			UserRoleService userRoleService = Mockito.mock(UserRoleService.class);
			Mockito.doNothing().when(userRoleService).addUserRole(userId, roleId);
			Mockito.when(userService.findUserByEmail(user.getEmailId())).thenReturn(mlpUser);
			Mockito.when(userService.findUserByUsername(user.getUsername())).thenReturn(mlpUser);
			JsonResponse<MLPRole> data = adminController.addUser(request, userreq, response);
			Assert.assertNotNull(data);
			Assert.assertEquals("User already exist", data.getResponseDetail());
			

			Mockito.when(userService.findUserByEmail(user.getEmailId())).thenReturn(null);
			Mockito.when(userService.findUserByUsername(user.getUsername())).thenReturn(mlpUser);
			data = adminController.addUser(request, userreq, response);
			Assert.assertNotNull(data);
			Assert.assertEquals("User already exist", data.getResponseDetail());
			
			Mockito.when(userService.findUserByEmail(user.getEmailId())).thenReturn(null);
			Mockito.when(userService.findUserByUsername(user.getUsername())).thenReturn(null);
			Mockito.when(userService.save(user)).thenReturn(user);
			
			data = adminController.addUser(request, userreq, response);
			Assert.assertNotNull(data);
			Assert.assertEquals("Error Occurred while addUserRole()", data.getResponseDetail());
			
			user.setUserNewRoleList(newRoleList);
			Mockito.when(userService.findUserByEmail(user.getEmailId())).thenReturn(null);
			Mockito.when(userService.findUserByUsername(user.getUsername())).thenReturn(null);
			UserService service = mock(UserService.class);
			//userRoleService rservice=mock(userRoleService.class);
			doNothing().when(userRoleService).addUserRole(userId,roleId);
			Mockito.when(userService.save(user)).thenReturn(user);
			
			data = adminController.addUser(request, userreq, response);
			Assert.assertNotNull(data);

			logger.info("Successfully added user role");
			Assert.assertNotNull(value);
		} catch (Exception e) {
			
			logger.info("Eception while fetching addUserRoleTest ", e);
		}
	}
	
	@Test
	public void isSignUpEnabledTest() {
		String signup_enabled ="true";
		when(env.getProperty("portal.feature.signup_enabled", "true")).thenReturn(signup_enabled);
		JsonResponse<String> jresponse =new JsonResponse<String>();
		jresponse=adminController.isSignUpEnabled(request, response);
		Assert.assertNotNull(jresponse);
	}
		
}
