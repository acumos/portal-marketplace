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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acumos.cds.domain.MLPPeer;
import org.acumos.cds.domain.MLPPeerSubscription;
import org.acumos.cds.domain.MLPSiteConfig;
import org.acumos.cds.transport.RestPageRequest;
import org.acumos.cds.transport.RestPageResponse;
import org.acumos.portal.be.common.JsonRequest;
import org.acumos.portal.be.common.JsonResponse;
import org.acumos.portal.be.controller.AdminServiceController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

@RunWith(MockitoJUnitRunner.class)
public class AdminServiceControllerTest {
	private static Logger logger = LoggerFactory.getLogger(FilterServiceControllerTest.class);

	final HttpServletResponse response = new MockHttpServletResponse();
	final HttpServletRequest request = new MockHttpServletRequest();

	@Mock
	AdminServiceController adminServiceController = new AdminServiceController();

	@Test
	public void getPeerListTest() {
		try {
			MLPPeer mlpPeer = new MLPPeer();
			mlpPeer.setActive(true);
			mlpPeer.setApiUrl("http://peer-api");
			mlpPeer.setContact1("Contact1");
			mlpPeer.setContact2("Contact2");
			Date created = new Date();
			mlpPeer.setCreated(created);
			mlpPeer.setDescription("Peer description");
			mlpPeer.setName("Peer-1509357629935");
			mlpPeer.setPeerId(String.valueOf(Math.incrementExact(0)));
			mlpPeer.setSelf(false);
			mlpPeer.setSubjectName("peer Subject name");
			mlpPeer.setWebUrl("https://web-url");

			MLPPeer mlpPeer2 = new MLPPeer();
			mlpPeer2.setActive(true);
			mlpPeer2.setApiUrl("http://peer-api");
			mlpPeer2.setContact1("Contact1");
			mlpPeer2.setContact2("Contact2");
			mlpPeer2.setCreated(created);
			mlpPeer2.setDescription("Peer description");
			mlpPeer2.setName("Peer-1509357629935");
			mlpPeer2.setPeerId(String.valueOf(Math.incrementExact(11)));
			mlpPeer2.setSelf(false);
			mlpPeer2.setSubjectName("peer Subject name");
			mlpPeer2.setWebUrl("https://web-url");

			JsonResponse<RestPageResponse<MLPPeer>> peerRes = new JsonResponse<>();
			RestPageResponse<MLPPeer> responseBody = new RestPageResponse<>();
			responseBody.setNumberOfElements(2);
			peerRes.setResponseBody(responseBody);

			RestPageRequest restPageReq = new RestPageRequest();
			restPageReq.setPage(0);
			restPageReq.setSize(2);
			List<MLPPeer> peerList = new ArrayList<>();
			if (restPageReq.getPage() != null && restPageReq.getSize() != null) {

				peerList.add(mlpPeer);
				peerList.add(mlpPeer2);
			}

			Mockito.when(adminServiceController.getPeerList(restPageReq)).thenReturn(peerRes);
			logger.info("get Peer list  : " + peerList);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void getPeerDetailsTest() {
		try {
			MLPPeer mlpPeer = new MLPPeer();
			mlpPeer.setActive(true);
			mlpPeer.setApiUrl("http://peer-api");
			mlpPeer.setContact1("Contact1");
			mlpPeer.setContact2("Contact2");
			Date created = new Date();
			mlpPeer.setCreated(created);
			mlpPeer.setDescription("Peer description");
			mlpPeer.setName("Peer-1509357629935");
			mlpPeer.setPeerId(String.valueOf(Math.incrementExact(0)));
			mlpPeer.setSelf(false);
			mlpPeer.setSubjectName("peer Subject name");
			mlpPeer.setWebUrl("https://web-url");

			JsonResponse<MLPPeer> peerRes = new JsonResponse<>();
			peerRes.setResponseBody(mlpPeer);

			String peerId = mlpPeer.getPeerId();
			Mockito.when(adminServiceController.getPeerDetails(peerId)).thenReturn(peerRes);
			logger.info("get Peer list  : " + peerRes.getResponseBody());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void createPeerTest() {
		try {
			MLPPeer mlpPeer = new MLPPeer();
			mlpPeer.setActive(true);
			mlpPeer.setApiUrl("http://peer-api");
			mlpPeer.setContact1("Contact1");
			mlpPeer.setContact2("Contact2");
			Date created = new Date();
			mlpPeer.setCreated(created);
			mlpPeer.setDescription("Peer description");
			mlpPeer.setName("Peer-1509357629935");
			mlpPeer.setPeerId(String.valueOf(Math.incrementExact(0)));
			mlpPeer.setSelf(false);
			mlpPeer.setSubjectName("peer Subject name");
			mlpPeer.setWebUrl("https://web-url");

			JsonRequest<MLPPeer> peerReq = new JsonRequest<>();
			peerReq.setBody(mlpPeer);
			JsonResponse<Object> peerRes = new JsonResponse<>();
			peerRes.setResponseBody(mlpPeer);

			Mockito.when(adminServiceController.createPeer(peerReq)).thenReturn(peerRes);
			logger.info("Create peer  : " + peerRes.getResponseBody());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void updatePeerTest() {

		try {
			MLPPeer mlpPeer = new MLPPeer();
			mlpPeer.setActive(true);
			mlpPeer.setApiUrl("http://peer-api");
			mlpPeer.setContact1("Contact1");
			mlpPeer.setContact2("Contact2");
			Date created = new Date();
			mlpPeer.setCreated(created);
			mlpPeer.setDescription("Peer description");
			mlpPeer.setName("Peer-1509357629935");
			mlpPeer.setPeerId(String.valueOf(Math.incrementExact(0)));
			mlpPeer.setSelf(false);
			mlpPeer.setSubjectName("peer Subject name");
			mlpPeer.setWebUrl("https://web-url");

			JsonRequest<MLPPeer> peerReq = new JsonRequest<>();
			peerReq.setBody(mlpPeer);
			JsonResponse<Object> peerRes = new JsonResponse<>();
			peerRes.setResponseBody(mlpPeer);

			String peerId = mlpPeer.getPeerId();
			Mockito.when(adminServiceController.updatePeer(peerId, peerReq)).thenReturn(peerRes);
			logger.info("Update peer  : " + peerRes.getResponseBody());

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Test
	public void removePeerTest() {

		try {
			MLPPeer mlpPeer = new MLPPeer();
			mlpPeer.setActive(true);
			mlpPeer.setApiUrl("http://peer-api");
			mlpPeer.setContact1("Contact1");
			mlpPeer.setContact2("Contact2");
			Date created = new Date();
			mlpPeer.setCreated(created);
			mlpPeer.setDescription("Peer description");
			mlpPeer.setName("Peer-1509357629935");
			mlpPeer.setPeerId(String.valueOf(Math.incrementExact(0)));
			mlpPeer.setSelf(false);
			mlpPeer.setSubjectName("peer Subject name");
			mlpPeer.setWebUrl("https://web-url");

			JsonRequest<MLPPeer> peerReq = new JsonRequest<>();
			peerReq.setBody(mlpPeer);
			JsonResponse<Object> peerRes = new JsonResponse<>();
			peerRes.setResponseBody(mlpPeer);

			String peerId = mlpPeer.getPeerId();
			Mockito.when(adminServiceController.removePeer(peerId)).thenReturn(peerRes);
			logger.info("Successfully removed the peer: " + peerRes.getResponseBody());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void getPeerSubscriptionsTest() {
		try {
			MLPPeer mlpPeer = new MLPPeer();
			mlpPeer.setActive(true);
			mlpPeer.setApiUrl("http://peer-api");
			mlpPeer.setContact1("Contact1");
			mlpPeer.setContact2("Contact2");
			Date created = new Date();
			mlpPeer.setCreated(created);
			mlpPeer.setDescription("Peer description");
			mlpPeer.setName("Peer-1509357629935");
			mlpPeer.setPeerId(String.valueOf(Math.incrementExact(0)));
			mlpPeer.setSelf(false);
			mlpPeer.setSubjectName("peer Subject name");
			mlpPeer.setWebUrl("https://web-url");

			MLPPeerSubscription mlpPeerSubcription = new MLPPeerSubscription();
			mlpPeerSubcription.setPeerId(mlpPeer.getPeerId());
			mlpPeerSubcription.setSubId((long) Math.incrementExact(0));
			mlpPeerSubcription.setCreated(created);

			List<MLPPeerSubscription> subScriptionList = new ArrayList<>();
			subScriptionList.add(mlpPeerSubcription);

			JsonResponse<List<MLPPeerSubscription>> subscriptionRes = new JsonResponse<>();
			subscriptionRes.setResponseBody(subScriptionList);
			String peerId = mlpPeer.getPeerId();
			Mockito.when(adminServiceController.getPeerSubscriptions(peerId)).thenReturn(subscriptionRes);
			logger.info("Subscription List : " + subscriptionRes.getResponseBody());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void getPeerSubscriptionDetailsTest() {

		try {
			MLPPeer mlpPeer = new MLPPeer();
			mlpPeer.setActive(true);
			mlpPeer.setApiUrl("http://peer-api");
			mlpPeer.setContact1("Contact1");
			mlpPeer.setContact2("Contact2");
			Date created = new Date();
			mlpPeer.setCreated(created);
			mlpPeer.setDescription("Peer description");
			mlpPeer.setName("Peer-1509357629935");
			mlpPeer.setPeerId(String.valueOf(Math.incrementExact(0)));
			mlpPeer.setSelf(false);
			mlpPeer.setSubjectName("peer Subject name");
			mlpPeer.setWebUrl("https://web-url");

			MLPPeerSubscription mlpPeerSubcription = new MLPPeerSubscription();
			mlpPeerSubcription.setPeerId(mlpPeer.getPeerId());
			mlpPeerSubcription.setSubId((long) Math.incrementExact(0));
			mlpPeerSubcription.setCreated(created);

			List<MLPPeerSubscription> subScriptionList = new ArrayList<>();
			subScriptionList.add(mlpPeerSubcription);

			JsonResponse<MLPPeerSubscription> subscriptionRes = new JsonResponse<>();
			subscriptionRes.setResponseBody(mlpPeerSubcription);
			Long subId = mlpPeerSubcription.getSubId();
			Mockito.when(adminServiceController.getPeerSubscriptionDetails(subId)).thenReturn(subscriptionRes);
			logger.info("Peer Subscription Details  : " + subscriptionRes.getResponseBody());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void createPeerSubscription() {
		try {
			MLPPeer mlpPeer = new MLPPeer();
			mlpPeer.setActive(true);
			mlpPeer.setApiUrl("http://peer-api");
			mlpPeer.setContact1("Contact1");
			mlpPeer.setContact2("Contact2");
			Date created = new Date();
			mlpPeer.setCreated(created);
			mlpPeer.setDescription("Peer description");
			mlpPeer.setName("Peer-1509357629935");
			mlpPeer.setPeerId(String.valueOf(Math.incrementExact(0)));
			mlpPeer.setSelf(false);
			mlpPeer.setSubjectName("peer Subject name");
			mlpPeer.setWebUrl("https://web-url");

			MLPPeerSubscription mlpPeerSubcription = new MLPPeerSubscription();
			mlpPeerSubcription.setPeerId(mlpPeer.getPeerId());
			mlpPeerSubcription.setSubId((long) Math.incrementExact(0));
			mlpPeerSubcription.setCreated(created);

			JsonRequest<MLPPeerSubscription> subscriptionReq = new JsonRequest<>();
			subscriptionReq.setBody(mlpPeerSubcription);
			JsonResponse<MLPPeerSubscription> subscriptionRes = new JsonResponse<>();
			subscriptionRes.setResponseBody(mlpPeerSubcription);
			Mockito.when(adminServiceController.createPeerSubscription(subscriptionReq)).thenReturn(subscriptionRes);
			logger.info("Successfully created peer subcrition  : " + subscriptionRes.getResponseBody());

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Test
	public void updatePeerSubscriptionTest() {

		try {
			MLPPeer mlpPeer = new MLPPeer();
			mlpPeer.setActive(true);
			mlpPeer.setApiUrl("http://peer-api");
			mlpPeer.setContact1("Contact1");
			mlpPeer.setContact2("Contact2");
			Date created = new Date();
			mlpPeer.setCreated(created);
			mlpPeer.setDescription("Peer description");
			mlpPeer.setName("Peer-1509357629935");
			mlpPeer.setPeerId(String.valueOf(Math.incrementExact(0)));
			mlpPeer.setSelf(false);
			mlpPeer.setSubjectName("peer Subject name");
			mlpPeer.setWebUrl("https://web-url");

			MLPPeerSubscription mlpPeerSubcription = new MLPPeerSubscription();
			mlpPeerSubcription.setPeerId(mlpPeer.getPeerId());
			mlpPeerSubcription.setSubId((long) Math.incrementExact(0));
			mlpPeerSubcription.setCreated(created);

			JsonRequest<MLPPeerSubscription> subscriptionReq = new JsonRequest<>();
			subscriptionReq.setBody(mlpPeerSubcription);
			JsonResponse<Object> subscriptionRes = new JsonResponse<>();
			subscriptionRes.setResponseBody(mlpPeerSubcription);
			Mockito.when(adminServiceController.updatePeerSubscription(subscriptionReq)).thenReturn(subscriptionRes);
			logger.info("Successfully updated peer subscription  : " + subscriptionRes.getResponseBody());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void deletePeerSubscriptionTest() {

		try {
			MLPPeer mlpPeer = new MLPPeer();
			mlpPeer.setActive(true);
			mlpPeer.setApiUrl("http://peer-api");
			mlpPeer.setContact1("Contact1");
			mlpPeer.setContact2("Contact2");
			Date created = new Date();
			mlpPeer.setCreated(created);
			mlpPeer.setDescription("Peer description");
			mlpPeer.setName("Peer-1509357629935");
			mlpPeer.setPeerId(String.valueOf(Math.incrementExact(0)));
			mlpPeer.setSelf(false);
			mlpPeer.setSubjectName("peer Subject name");
			mlpPeer.setWebUrl("https://web-url");

			MLPPeerSubscription mlpPeerSubcription = new MLPPeerSubscription();
			mlpPeerSubcription.setPeerId(mlpPeer.getPeerId());
			mlpPeerSubcription.setSubId((long) Math.incrementExact(0));
			mlpPeerSubcription.setCreated(created);

			Long subId = mlpPeerSubcription.getSubId();

			JsonRequest<MLPPeerSubscription> subscriptionReq = new JsonRequest<>();
			subscriptionReq.setBody(mlpPeerSubcription);
			JsonResponse<Object> subscriptionRes = new JsonResponse<>();
			subscriptionRes.setResponseBody(mlpPeerSubcription);
			Mockito.when(adminServiceController.deletePeerSubscription(subId)).thenReturn(subscriptionRes);
			logger.info("Successfully deleted peer subscription  : " + subscriptionRes.getResponseBody());

		} catch (Exception e) {
			e.printStackTrace();
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

			JsonResponse<MLPSiteConfig> configRes = new JsonResponse<>();
			configRes.setResponseBody(mlpSiteConfig);

			String configKey = mlpSiteConfig.getConfigKey();
			Mockito.when(adminServiceController.getSiteConfiguration(configKey)).thenReturn(configRes);
			logger.info("Site Configuration Details :" + configRes.getResponseBody());

		} catch (Exception e) {
			e.printStackTrace();
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

			JsonResponse<MLPSiteConfig> configRes = new JsonResponse<>();
			configRes.setResponseBody(mlpSiteConfig);

			JsonRequest<MLPSiteConfig> mlpSiteConfigReq = new JsonRequest<>();
			mlpSiteConfigReq.setBody(mlpSiteConfig);

			String configKey = mlpSiteConfig.getConfigKey();
			Mockito.when(adminServiceController.createSiteConfig(mlpSiteConfigReq)).thenReturn(configRes);
			logger.info("created Configuration Details :" + configRes.getResponseBody());

		} catch (Exception e) {
			e.printStackTrace();
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

			JsonResponse<MLPSiteConfig> configRes = new JsonResponse<>();
			configRes.setResponseBody(mlpSiteConfig);

			JsonRequest<MLPSiteConfig> mlpSiteConfigReq = new JsonRequest<>();
			mlpSiteConfigReq.setBody(mlpSiteConfig);

			String configKey = mlpSiteConfig.getConfigKey();
			Mockito.when(adminServiceController.updateSiteConfig(configKey, mlpSiteConfigReq)).thenReturn(configRes);
			logger.info("Updated  Configuration Details :" + configRes.getResponseBody());

		} catch (Exception e) {
			e.printStackTrace();
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

			JsonResponse<Object> configRes = new JsonResponse<>();
			configRes.setResponseBody(mlpSiteConfig);

			JsonRequest<MLPSiteConfig> mlpSiteConfigReq = new JsonRequest<>();
			mlpSiteConfigReq.setBody(mlpSiteConfig);

			String configKey = mlpSiteConfig.getConfigKey();
			Mockito.when(adminServiceController.deleteSiteConfig(configKey)).thenReturn(configRes);
			logger.info("Deleted Configuration Details :" + configRes.getResponseBody());

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
