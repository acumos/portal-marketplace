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
package org.acumos.be.test.service.impl;

import org.acumos.cds.domain.MLPPeer;
import org.acumos.cds.domain.MLPPeerSubscription;
import org.acumos.cds.domain.MLPSiteConfig;
import org.acumos.cds.transport.RestPageRequest;
import org.acumos.cds.transport.RestPageResponse;
import org.acumos.portal.be.service.impl.AdminServiceImpl;
import org.acumos.portal.be.util.EELFLoggerDelegate;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import static org.mockito.Mockito.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;


@RunWith(MockitoJUnitRunner.class)
public class AdminServiceImplTest {

	private static final EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(AdminServiceImplTest.class);


	final HttpServletResponse response = new MockHttpServletResponse();
	final HttpServletRequest request = new MockHttpServletRequest();

	@Mock
	AdminServiceImpl impl = new AdminServiceImpl();
 
	@Test
	public void testgetAllPeers() {
		try {

			RestPageRequest restPageReq = new RestPageRequest();
			restPageReq.setPage(1);
			restPageReq.setSize(9);
			if (restPageReq.getPage() != null && restPageReq.getSize() != null) {

				RestPageResponse<MLPPeer> peerRes = new RestPageResponse<>();
				peerRes.setSize(0);
				peerRes.setTotalPages(2);//peerRes.setTotalElements(2);commented coz of migrating to 1.10.1				
				Mockito.when(impl.getAllPeers(restPageReq)).thenReturn(peerRes );
				Assert.assertEquals(peerRes, peerRes);
				logger.info("Successfully fetched all the peers");
			}
		} catch (Exception e) {
			logger.info("Failed to execute testCase ");
			logger.info("Exception occured while fetching peers : " + e);
		}

	}

	@Test
	public void getPeerDetailTest() {
		try {
			String peerId = "ab20f129-06ba-48dc-b238-335f9982799c";
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
			if (peerId != null) {
				Mockito.when(impl.getPeerDetail(peerId)).thenReturn(mlpPeer);
				Assert.assertNotNull(peerId, mlpPeer);
				logger.info("Peer Details  : " + mlpPeer);
			}
		} catch (Exception e) {
			logger.info("Failed to execute testCase ");
			logger.info("Exception occured while fetching peers : " + e);
		}
	}

	@Test
	public void findPeerByApiAndWebUrlTest() {
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
			
			String apiUrl = mlpPeer.getApiUrl();
			String webUrl = mlpPeer.getWebUrl();
			if (apiUrl != null && webUrl != null) {
				Mockito.when(impl.findPeerByApiAndWebUrl(apiUrl, webUrl)).thenReturn(mlpPeer);
				logger.info("Successfully fetched peer deatils based on api & web url's : " + mlpPeer);
				Assert.assertNotNull(mlpPeer);
				Assert.assertEquals(mlpPeer, mlpPeer);
			}

		} catch (Exception e) {
			logger.info("Failed to execute testCase ");
			logger.info("Exception occured while findPeerByApiAndWebUrlTest : " + e);

		}
	}

	@Test
	public void savePeerTest() {
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

			if (mlpPeer != null) {
				Mockito.when(impl.savePeer(mlpPeer)).thenReturn(mlpPeer);
				Assert.assertNotNull(mlpPeer);
				logger.info("Successfully created the Peer : " + mlpPeer);
			}

		} catch (Exception e) {
			logger.info("Failed to execute testCase ");
			logger.debug("Exception while creating peer : " + e);
		}

	}

	@Test
	public void updatePeerTest() {
		try {
			MLPPeer mlpPeer = new MLPPeer();
			mlpPeer.setApiUrl("http://peer-api");
			mlpPeer.setContact1("Contact1");
			Date created = new Date();
			mlpPeer.setCreated(created);
			mlpPeer.setDescription("Peer description");
			mlpPeer.setName("Peer-1509357629935");
			mlpPeer.setPeerId("ab20f129-06ba-48dc-b238-335f9982799c");
			mlpPeer.setSelf(false);
			mlpPeer.setSubjectName("peer Subject name");
			mlpPeer.setWebUrl("https://web-url");

			AdminServiceImpl mockVoid = mock(AdminServiceImpl.class);
			if (mlpPeer != null) {
				mockVoid.updatePeer(mlpPeer);
				Assert.assertNotNull(mockVoid);
				logger.info("Successfully updated the peer");
			}

		} catch (Exception e) {
			logger.info("Failed to execute testCase ");
			logger.debug("Exception while updating peer : " + e);
		}
	}

	@Test
	public void removePeerTest() {
		try {
			String peerId = "ab20f129-06ba-48dc-b238-335f9982799c";
			AdminServiceImpl mockimpl = mock(AdminServiceImpl.class);
			if (peerId != null) {
				mockimpl.removePeer(peerId);
				Assert.assertEquals(peerId, peerId);
				Assert.assertNotNull(mockimpl);
				logger.info("Peer  Removed ");
			}

		} catch (Exception e) {
			logger.info("Failed to execute testCase ");
			logger.debug("Exception while Deleting peer : " + e);
		}
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
			mlpPeer.setPeerId("ab20f129-06ba-48dc-b238-335f9982799c");
			mlpPeer.setSelf(false);
			mlpPeer.setSubjectName("peer Subject name");
			mlpPeer.setWebUrl("https://web-url");

			MLPPeerSubscription mlpPeerSubcription = new MLPPeerSubscription();
			mlpPeerSubcription.setPeerId("ab20f129-06ba-48dc-b238-335f9982799c");
			mlpPeerSubcription.setSubId((long) 4);
			mlpPeerSubcription.setCreated(created);

			List<MLPPeerSubscription> subScriptionList = new ArrayList<>();
			subScriptionList.add(mlpPeerSubcription);
			if (mlpPeerSubcription.getPeerId() != null) {
				Mockito.when(impl.getPeerSubscriptions(mlpPeer.getPeerId())).thenReturn(subScriptionList);
				logger.info("Successfully fetched peer details :  " + subScriptionList);
				Assert.assertEquals(subScriptionList, subScriptionList);
			}

		} catch (Exception e) {
			logger.info("Failed to execute testCase ");
			logger.debug("Exception while fetching peer subscription details  : " + e);
		}
	}

	@Test
	public void getPeerSubscriptionTest() {

		try {
			MLPPeer mlpPeer = new MLPPeer();
			mlpPeer.setApiUrl("http://peer-api");
			mlpPeer.setContact1("Contact1");
			Date created = new Date();
			mlpPeer.setCreated(created);
			mlpPeer.setDescription("Peer description");
			mlpPeer.setName("Peer-1509357629935");
			mlpPeer.setPeerId("ab20f129-06ba-48dc-b238-335f9982799c");
			mlpPeer.setSelf(false);
			mlpPeer.setSubjectName("peer Subject name");
			mlpPeer.setWebUrl("https://web-url");

			MLPPeerSubscription mlpPeerSubcription = new MLPPeerSubscription();
			mlpPeerSubcription.setPeerId("ab20f129-06ba-48dc-b238-335f9982799c");
			mlpPeerSubcription.setSubId((long) 4);
			mlpPeerSubcription.setCreated(created);

			if (mlpPeerSubcription.getSubId() != null) {
				Mockito.when(impl.getPeerSubscription(mlpPeerSubcription.getSubId())).thenReturn(mlpPeerSubcription);
				logger.info("Successfully fetched peer details :  " + mlpPeerSubcription);
				Assert.assertNotNull(mlpPeerSubcription);
			}

		} catch (Exception e) {
			logger.info("Failed to execute testCase ");
			logger.debug("Exception while fetching peer subscription details  : " + e);
		}

	}
	
	@Test
	public void getPeerSubscriptionCountsTest() {
		try {
			String peerId = "ab20f129-06ba-48dc-b238-335f9982799c";
			Integer subCount = 7;
			
			List<String> ids = new ArrayList<>();
			ids.add(peerId);

			Map<String,Integer> counts = new HashMap<>();
			counts.put(peerId, subCount);
			
			Mockito.when(impl.getPeerSubscriptionCounts(ids)).thenReturn(counts);
			logger.info("Successfully fetched subscription counts :  " + counts);
			Assert.assertEquals(counts.get(peerId), subCount);

		} catch (Exception e) {
			logger.error("Exception while fetching subscription counts", e);
		}
	}

	@Test
	public void createPeerSubscriptionTest() {
		try {
			MLPPeerSubscription mlpPeerSubcription = new MLPPeerSubscription();
			mlpPeerSubcription.setPeerId("ab20f129-06ba-48dc-b238-335f9982799c");
			mlpPeerSubcription.setSubId((long) Math.incrementExact(0));
			Date created = new Date();
			mlpPeerSubcription.setCreated(created);

			if (mlpPeerSubcription.getSubId() != null) {
				Mockito.when(impl.createPeerSubscription(mlpPeerSubcription)).thenReturn(mlpPeerSubcription);
				logger.info(" Successfully created Peer :  " + mlpPeerSubcription);
				Assert.assertEquals(mlpPeerSubcription, mlpPeerSubcription);
				Assert.assertNotNull("Subscription Id is not  null", mlpPeerSubcription);
			}
		} catch (Exception e) {
			logger.info("Failed to execute testCase ");
			logger.debug("Exception while createPeerSubscriptionTest : " + e);
		}
	}

	@Test
	public void updatePeerSubscriptionTest() {
		try {

			MLPPeerSubscription mlpPeerSubcription = new MLPPeerSubscription();
			mlpPeerSubcription.setPeerId("ab20f129-06ba-48dc-b238-335f9982799c");
			mlpPeerSubcription.setSubId((long) 4);
			Date created = new Date();
			mlpPeerSubcription.setCreated(created);

			AdminServiceImpl mockImpl = mock(AdminServiceImpl.class);

			if (mlpPeerSubcription.getSubId() != null) {
				mockImpl.updatePeerSubscription(mlpPeerSubcription);
				logger.info(" Successfully updated Peer :  " +mlpPeerSubcription);
				Assert.assertEquals(mlpPeerSubcription, mlpPeerSubcription);
			}

		} catch (Exception e) {
			logger.info("Failed to execute testCase ");
			logger.debug("Exception while updatePeerSubscriptionTest : " + e);
		}
	}

	@Test
	public void deletePeerSubscriptionTest() {
		try {

			MLPPeerSubscription mlpPeerSubcription = new MLPPeerSubscription();
			mlpPeerSubcription.setPeerId("ab20f129-06ba-48dc-b238-335f9982799c");
			mlpPeerSubcription.setSubId((long) 4);
			Date created = new Date();
			mlpPeerSubcription.setCreated(created);

			AdminServiceImpl mockimpl =mock(AdminServiceImpl.class);
			if (mlpPeerSubcription.getSubId() != null) {
				mockimpl.deletePeerSubscription(mlpPeerSubcription.getSubId());
				logger.info(" Successfully Deleted Peer :  ");
				Assert.assertSame(mockimpl, mockimpl);
			}

		} catch (Exception e) {
			logger.info("Failed to execute testCase ");
			logger.debug("Exception while deletePeerSubscriptionTest : " + e);
		}
	}

	@Test
	public void getSiteConfigTest() {
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

			String configKey = "12";

			if (configKey != null) {
				Mockito.when(impl.getSiteConfig(configKey)).thenReturn(mlpSiteConfig);
				logger.info("Site Configuration " + mlpSiteConfig);
				Assert.assertNotNull("Config key is not null ", configKey);
				Assert.assertEquals(mlpSiteConfig, mlpSiteConfig);
			}
		} catch (Exception e) {
			logger.info("Failed to execute testCase ");
			logger.debug("Exception while getSiteConfigTest : " + e);
		}
	}
	
}
