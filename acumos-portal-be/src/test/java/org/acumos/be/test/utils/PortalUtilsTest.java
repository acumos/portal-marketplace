/*-
 * ===============LICENSE_START=======================================================
 * Acumos
 * ===================================================================================
 * Copyright (C) 2019 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
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

package org.acumos.be.test.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.lang.invoke.MethodHandles;
import java.time.Instant;
import java.util.ArrayList;

import org.acumos.cds.domain.MLPPeer;
import org.acumos.cds.domain.MLPRole;
import org.acumos.cds.domain.MLPRoleFunction;
import org.acumos.cds.domain.MLPUserLoginProvider;
import org.acumos.cds.transport.RestPageResponse;
import org.acumos.portal.be.common.exception.AcumosServiceException;
import org.acumos.portal.be.transport.MLPeer;
import org.acumos.portal.be.transport.MLRole;
import org.acumos.portal.be.transport.MLRoleFunction;
import org.acumos.portal.be.transport.OauthUser;
import org.acumos.portal.be.transport.UserMasterObject;
import org.acumos.portal.be.util.PortalUtils;
import org.junit.Test;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.PageRequest;

public class PortalUtilsTest {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());	

	@Mock
	Environment env;
	
	@Test
	public void convertRestPageResponseTest() {
		ArrayList<Integer> intList = new ArrayList<>();
		intList.add(1);
		intList.add(2);
		intList.add(3);
		PageRequest pageRequest = PageRequest.of(0, 3);
		int totalElements = 15;
		RestPageResponse<Integer> first = new RestPageResponse<>(intList, pageRequest, totalElements);

		ArrayList<Long> longList = new ArrayList<>();
		for (int i : intList) {
			longList.add((long) i);
		}
		RestPageResponse<Long> second = PortalUtils.convertRestPageResponse(first, longList);
		
		assertNotNull(second);
		assertNotNull(second.getContent());
		assertEquals(first.getNumber(), second.getNumber());
		assertEquals(first.getSize(), second.getSize());
		assertEquals(first.getTotalElements(), second.getTotalElements());
		assertEquals(first.getTotalPages(), second.getTotalPages());
		assertEquals(first.getPageable(), second.getPageable());
		assertEquals(first.isFirst(), second.isFirst());
		assertEquals(first.isLast(), second.isLast());
		assertEquals(first.isEmpty(), second.isEmpty());
		assertEquals(first.getNumberOfElements(), second.getNumberOfElements());
	}
	
	@Test
	public void convertUserMasterIntoOauthUserTest() {
		
		UserMasterObject userMasterObject = new UserMasterObject();
		userMasterObject.setUserId("user");
		userMasterObject.setProviderCd("providerCd");
		userMasterObject.setProviderUserId("ProviderUserId");
		userMasterObject.setRank(0);
		userMasterObject.setDisplayName("displayName");
		userMasterObject.setProfileURL("profileURL");
		userMasterObject.setImageURL("ImageURL");
		userMasterObject.setSecret("secret");
		userMasterObject.setAccessToken("AccessToken");
		userMasterObject.setRefreshToken("RefreshToken");
		Instant expireTime = Instant.now();
		Instant created = Instant.now();
		Instant modified = Instant.now();
		userMasterObject.setExpireTime(expireTime);
		userMasterObject.setCreated(created);
		userMasterObject.setModified(modified);
		
		OauthUser oauthUser = PortalUtils.convertUserMasterIntoOauthUser(userMasterObject);
		
		assertNotNull(oauthUser);
	}
	
	@Test
	public void convertToOathUserTest() {
		MLPUserLoginProvider mlpUserLoginProvider = new MLPUserLoginProvider();
		mlpUserLoginProvider.setUserId("userID");
		mlpUserLoginProvider.setProviderCode("providerID");
		mlpUserLoginProvider.setProviderUserId("providerUserID");
		mlpUserLoginProvider.setRank(0);
		mlpUserLoginProvider.setDisplayName("displayName");
		mlpUserLoginProvider.setProfileUrl("profileURL");
		mlpUserLoginProvider.setImageUrl("ImageUrl");
		mlpUserLoginProvider.setSecret("Secret");
		mlpUserLoginProvider.setAccessToken("AccessToken");
		mlpUserLoginProvider.setRefreshToken("RefreshToken");
		Instant created = Instant.now();
		Instant modified = Instant.now();
		mlpUserLoginProvider.setCreated(created);
		mlpUserLoginProvider.setModified(modified);
		
		OauthUser oauthUser = PortalUtils.convertToOathUser(mlpUserLoginProvider);
		
		assertNotNull(oauthUser);
	}
	
	@Test
	public void convertToMLRoleFunctionTest() {
		MLPRoleFunction mlpRoleFunction = new MLPRoleFunction();
		mlpRoleFunction.setRoleFunctionId("RoleFunctionID");
		mlpRoleFunction.setName("Name");
		Instant created = Instant.now();
		Instant modified = Instant.now();
		mlpRoleFunction.setCreated(created);
		mlpRoleFunction.setModified(modified);
		
		MLRoleFunction mlRoleFunction = PortalUtils.convertToMLRoleFunction(mlpRoleFunction);
		
		assertNotNull(mlRoleFunction);
	}
	
	@Test
	public void convertToMLPRoleTest() {
		MLRole mlRole = new MLRole();
		mlRole.setRoleId("RoleID");
		mlRole.setName("Name");
		Instant created = Instant.now();
		Instant modified = Instant.now();
		mlRole.setCreated(created);
		mlRole.setModified(modified);
		mlRole.setActive(false);
		
		MLPRole mlpRole = PortalUtils.convertToMLPRole(mlRole);
		
		assertNotNull(mlpRole);
	}
	
	@Test
	public void convertToMLRoleTest() {
		
		MLPRole mlpRole = new MLPRole();
		
		mlpRole.setRoleId("RoleID");
		mlpRole.setName("Name");
		Instant created = Instant.now();
		Instant modified = Instant.now();
		mlpRole.setCreated(created);
		mlpRole.setModified(modified);
		mlpRole.setActive(false);
		
		MLRole mlRole = PortalUtils.convertToMLRole(mlpRole);
		
		assertNotNull(mlRole);
	}
	
	@Test
	public void convertToMLPeerTest() {
		MLPPeer peer = new MLPPeer();
		peer.setApiUrl("apiURL");
		peer.setContact1("contact");
		Instant created = Instant.now();
		Instant modified = Instant.now();
		peer.setCreated(created);
		peer.setModified(modified);
		peer.setName("Name");
		peer.setPeerId("PeerID");
		peer.setStatusCode("statusCode");
		peer.setSubjectName("SubjectName");
		peer.setDescription("description");
		peer.setWebUrl("webURL");
		
		MLPeer mPeer = PortalUtils.convertToMLPeer(peer);
		
		assertNotNull(mPeer);
	}
	
}
