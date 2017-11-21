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

import org.acumos.be.test.controller.UserServiceControllerTest;
import org.acumos.cds.client.CommonDataServiceRestClientImpl;
import org.acumos.cds.client.ICommonDataServiceRestClient;
import org.acumos.cds.domain.MLPPeer;
import org.acumos.cds.domain.MLPPeerSubscription;
import org.acumos.cds.domain.MLPSiteConfig;
import org.acumos.cds.transport.RestPageRequest;
import org.acumos.cds.transport.RestPageResponse;
import org.acumos.portal.be.service.impl.AbstractServiceImpl;
import org.acumos.portal.be.service.impl.AdminServiceImpl;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.core.env.Environment;
public class AdminServiceImplTest {

	private static Logger logger = LoggerFactory.getLogger(AdminServiceImplTest.class);

	@Mock
	Environment env;

	@Mock
	AdminServiceImplTest test;

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	private final String url = "http://localhost:8002/ccds";
	private final String user = "ccds_client";
	private final String pass = "ccds_client";

	private ICommonDataServiceRestClient cmnDataService;

	private AbstractServiceImpl abstractImpl;

	@Before
	public void createClient() throws Exception {
		cmnDataService = CommonDataServiceRestClientImpl.getInstance(url.toString(), user, pass);
	}
 
	@Test
	public void testgetAllPeers() {
		try {

			RestPageRequest restPageReq = new RestPageRequest();
			restPageReq.setPage(1);
			restPageReq.setSize(9);
			if (restPageReq.getPage() != null && restPageReq.getSize() != null) {

				when(env.getProperty("cdms.client.url")).thenReturn("http://localhost:8002/ccds");
				when(env.getProperty("cdms.client.username")).thenReturn("ccds_client");
				when(env.getProperty("cdms.client.password")).thenReturn("ccds_client");
				AdminServiceImpl impl = new AdminServiceImpl();
				impl.setEnvironment(env);
				RestPageResponse<MLPPeer> mlpPeers = impl.getAllPeers(restPageReq);
				logger.info("mlpPeers :    " + mlpPeers);

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
			when(env.getProperty("cdms.client.url")).thenReturn("http://localhost:8002/ccds");
			when(env.getProperty("cdms.client.username")).thenReturn("ccds_client");
			when(env.getProperty("cdms.client.password")).thenReturn("ccds_client");
			AdminServiceImpl impl = new AdminServiceImpl();
			impl.setEnvironment(env);
			if (peerId != null) {
				MLPPeer mlpeer = impl.getPeerDetail(peerId);
				logger.info("Peer Details  : " + mlpeer);
			}
		} catch (Exception e) {
			logger.info("Failed to execute testCase ");
			logger.info("Exception occured while fetching peers : " + e);
		}
	}

	@Test
	public void findPeerByApiAndWebUrlTest() {
		try {
			when(env.getProperty("cdms.client.url")).thenReturn("http://localhost:8002/ccds");
			when(env.getProperty("cdms.client.username")).thenReturn("ccds_client");
			when(env.getProperty("cdms.client.password")).thenReturn("ccds_client");
			AdminServiceImpl impl = new AdminServiceImpl();
			impl.setEnvironment(env);
			String apiUrl = "http://peer-api";
			String webUrl = "https://web-url";
			if (apiUrl != null && webUrl != null) {
				MLPPeer mlpeer = impl.findPeerByApiAndWebUrl(apiUrl, webUrl);
				logger.info("Successfully fetched peer deatils based on api & web url's : " + mlpeer);
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

			when(env.getProperty("cdms.client.url")).thenReturn("http://localhost:8002/ccds");
			when(env.getProperty("cdms.client.username")).thenReturn("ccds_client");
			when(env.getProperty("cdms.client.password")).thenReturn("ccds_client");
			AdminServiceImpl impl = new AdminServiceImpl();
			impl.setEnvironment(env);

			if (mlpPeer != null) {
				MLPPeer savePeer = impl.savePeer(mlpPeer);
				logger.info("Successfully created the Peer : " + savePeer);
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
			mlpPeer.setActive(true);
			mlpPeer.setApiUrl("http://peer-api");
			mlpPeer.setContact1("Contact1");
			mlpPeer.setContact2("Contact2");
			Date created = new Date();
			mlpPeer.setCreated(created);
			mlpPeer.setDescription("Peer description");
			mlpPeer.setName("Peer-1509357629935");
			mlpPeer.setPeerId("ab20f129-06ba-48dc-b238-335f9982799c");
			mlpPeer.setSelf(false);
			mlpPeer.setSubjectName("peer Subject name");
			mlpPeer.setWebUrl("https://web-url");

			when(env.getProperty("cdms.client.url")).thenReturn("http://localhost:8002/ccds");
			when(env.getProperty("cdms.client.username")).thenReturn("ccds_client");
			when(env.getProperty("cdms.client.password")).thenReturn("ccds_client");
			AdminServiceImpl impl = new AdminServiceImpl();
			impl.setEnvironment(env);

			if (mlpPeer != null) {
				impl.updatePeer(mlpPeer);
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
			when(env.getProperty("cdms.client.url")).thenReturn("http://localhost:8002/ccds");
			when(env.getProperty("cdms.client.username")).thenReturn("ccds_client");
			when(env.getProperty("cdms.client.password")).thenReturn("ccds_client");
			AdminServiceImpl impl = new AdminServiceImpl();
			impl.setEnvironment(env);
			if (peerId != null) {
				impl.removePeer(peerId);
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
			mlpPeer.setActive(true);
			mlpPeer.setApiUrl("http://peer-api");
			mlpPeer.setContact1("Contact1");
			mlpPeer.setContact2("Contact2");
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

			when(env.getProperty("cdms.client.url")).thenReturn("http://localhost:8002/ccds");
			when(env.getProperty("cdms.client.username")).thenReturn("ccds_client");
			when(env.getProperty("cdms.client.password")).thenReturn("ccds_client");
			AdminServiceImpl impl = new AdminServiceImpl();
			impl.setEnvironment(env);
			List<MLPPeerSubscription> subScriptionList = new ArrayList<>();
			subScriptionList.add(mlpPeerSubcription);
			if (mlpPeerSubcription.getPeerId() != null) {
				List<MLPPeerSubscription> subScriptionList1 = impl.getPeerSubscriptions(mlpPeer.getPeerId());
				logger.info("Successfully fetched peer details :  " + subScriptionList1);
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
			mlpPeer.setActive(true);
			mlpPeer.setApiUrl("http://peer-api");
			mlpPeer.setContact1("Contact1");
			mlpPeer.setContact2("Contact2");
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

			when(env.getProperty("cdms.client.url")).thenReturn("http://localhost:8002/ccds");
			when(env.getProperty("cdms.client.username")).thenReturn("ccds_client");
			when(env.getProperty("cdms.client.password")).thenReturn("ccds_client");
			AdminServiceImpl impl = new AdminServiceImpl();
			impl.setEnvironment(env);
			if (mlpPeerSubcription.getSubId() != null) {
				MLPPeerSubscription subScription = impl.getPeerSubscription(mlpPeerSubcription.getSubId());
				logger.info("Successfully fetched peer details :  " + subScription);
			}

		} catch (Exception e) {
			logger.info("Failed to execute testCase ");
			logger.debug("Exception while fetching peer subscription details  : " + e);
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

			when(env.getProperty("cdms.client.url")).thenReturn("http://localhost:8002/ccds");
			when(env.getProperty("cdms.client.username")).thenReturn("ccds_client");
			when(env.getProperty("cdms.client.password")).thenReturn("ccds_client");
			AdminServiceImpl impl = new AdminServiceImpl();
			impl.setEnvironment(env);

			if (mlpPeerSubcription.getSubId() != null) {
				MLPPeerSubscription mlpeersubs = impl.createPeerSubscription(mlpPeerSubcription);
				logger.info(" Successfully created Peer :  " + mlpeersubs);
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

			when(env.getProperty("cdms.client.url")).thenReturn("http://localhost:8002/ccds");
			when(env.getProperty("cdms.client.username")).thenReturn("ccds_client");
			when(env.getProperty("cdms.client.password")).thenReturn("ccds_client");
			AdminServiceImpl impl = new AdminServiceImpl();
			impl.setEnvironment(env);

			if (mlpPeerSubcription.getSubId() != null) {
				impl.updatePeerSubscription(mlpPeerSubcription);
				logger.info(" Successfully updated Peer :  ");
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

			when(env.getProperty("cdms.client.url")).thenReturn("http://localhost:8002/ccds");
			when(env.getProperty("cdms.client.username")).thenReturn("ccds_client");
			when(env.getProperty("cdms.client.password")).thenReturn("ccds_client");
			AdminServiceImpl impl = new AdminServiceImpl();
			impl.setEnvironment(env);

			if (mlpPeerSubcription.getSubId() != null) {
				impl.deletePeerSubscription(mlpPeerSubcription.getSubId());
				logger.info(" Successfully Deleted Peer :  ");
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

			when(env.getProperty("cdms.client.url")).thenReturn("http://localhost:8002/ccds");
			when(env.getProperty("cdms.client.username")).thenReturn("ccds_client");
			when(env.getProperty("cdms.client.password")).thenReturn("ccds_client");
			AdminServiceImpl impl = new AdminServiceImpl();
			impl.setEnvironment(env);

			String configKey = "12";

			if (configKey != null) {
				MLPSiteConfig mlpSiteConfig1 = impl.getSiteConfig(configKey);
				logger.info("Site Configuration " + mlpSiteConfig1);
			}
		} catch (Exception e) {
			logger.info("Failed to execute testCase ");
			logger.debug("Exception while getSiteConfigTest : " + e);
		}
	}
	
}
