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
package org.acumos.be.test.transport;

import java.time.Instant;

import org.acumos.portal.be.transport.UserMasterObject;
import org.junit.Assert;
import org.junit.Test;

public class UserMasterObjectTest {
	
		
	@Test	
	public void testOauthUserParameter(){
		
		String firstName = "abc";
		String lastName = "xyz";
		String emailId = "zbc@techm.com";
		String username = "zbc";
		boolean active = true;
		Instant lastLogin = Instant.now();
		Instant created = Instant.now();
		Instant modified = Instant.now();
		Instant createdDate = Instant.now();
		Instant modifiedDate = Instant.now();
		String providerCd = "dsds";
		String providerUserId = "dd24fs";
		String userId = "83d5259f-48b7-4fe1-9fd6-d1166f8f3691";
		int rank = 21;
		String displayName = "abc";
		String profileURL = "http://xyz.com";
		String imageURL = "http://image.com";
		String secret = "sffsf";
		String accessToken = "453thfhy5y6";
		String refreshToken = "3f4f5g22ds";
		Instant expireTime = Instant.now();		
		
		
		UserMasterObject userMasterObject = new UserMasterObject();
		userMasterObject.setActive(active);
		userMasterObject.setCreated(created);
		userMasterObject.setEmailId(emailId);
		userMasterObject.setFirstName(firstName);
		userMasterObject.setLastLogin(lastLogin);
		userMasterObject.setLastName(lastName);
		userMasterObject.setModified(modified);
		userMasterObject.setUserId(userId);
		userMasterObject.setUsername(username);
		
		userMasterObject.setAccessToken(accessToken);
		userMasterObject.setCreated(createdDate);
		userMasterObject.setDisplayName(displayName);
		userMasterObject.setExpireTime(expireTime);
		userMasterObject.setImageURL(imageURL);
		userMasterObject.setModifiedDate(modifiedDate);
		userMasterObject.setRank(rank);
		userMasterObject.setSecret(secret);
		userMasterObject.setRefreshToken(refreshToken);
		userMasterObject.setProviderUserId(providerUserId);
		userMasterObject.setProviderCd(providerCd);
		userMasterObject.setProfileURL(profileURL);
		userMasterObject.setAccessToken(accessToken);
		userMasterObject.setCreated(createdDate);
		userMasterObject.setDisplayName(displayName);
		userMasterObject.setExpireTime(expireTime);
		userMasterObject.setImageURL(imageURL);
		userMasterObject.setModifiedDate(modifiedDate);
		userMasterObject.setRank(rank);
		userMasterObject.setSecret(secret);
		userMasterObject.setRefreshToken(refreshToken);
		userMasterObject.setProviderUserId(providerUserId);
		userMasterObject.setProviderCd(providerCd);
		userMasterObject.setProfileURL(profileURL);
		
		Assert.assertEquals(active, userMasterObject.isActive());
//		Assert.assertEquals(created, userMasterObject.getCreated());
		Assert.assertEquals(emailId, userMasterObject.getEmailId());
		Assert.assertEquals(firstName, userMasterObject.getFirstName());
		Assert.assertEquals(lastName, userMasterObject.getLastName());
		Assert.assertEquals(modified, userMasterObject.getModified());
		Assert.assertEquals(userId, userMasterObject.getUserId());
		Assert.assertEquals(username, userMasterObject.getUsername());		
		Assert.assertEquals(accessToken, userMasterObject.getAccessToken());
		//Assert.assertEquals(createdDate, userMasterObject.getCreated());
		Assert.assertEquals(displayName, userMasterObject.getDisplayName());
		//Assert.assertEquals(expireTime, userMasterObject.getExpireTime());
		Assert.assertEquals(lastName, userMasterObject.getLastName());
		Assert.assertEquals(imageURL, userMasterObject.getImageURL());
		//Assert.assertEquals(modifiedDate, userMasterObject.getModifiedDate());
		Assert.assertEquals(rank, userMasterObject.getRank());		
		Assert.assertEquals(secret, userMasterObject.getSecret());
		Assert.assertEquals(refreshToken, userMasterObject.getRefreshToken());
		Assert.assertEquals(providerUserId, userMasterObject.getProviderUserId());
		Assert.assertEquals(providerCd, userMasterObject.getProviderCd());
		Assert.assertEquals(profileURL, userMasterObject.getProfileURL());
		
	}
}
