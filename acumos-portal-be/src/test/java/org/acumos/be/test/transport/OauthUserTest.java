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

import org.acumos.portal.be.transport.OauthUser;
import org.junit.Assert;
import org.junit.Test;

public class OauthUserTest {

	@Test	
	public void testOauthUserParameter(){
		String userId = "83d5259f-48b7-4fe1-9fd6-d1166f8f3691";
		String providerCd = "dsds";
		String providerUserId = "dd24fs";
		int rank = 21;
		String displayName = "abc";
		String jwtToken = "sf35dhfh35";
		boolean loginPassExpire = true;
		String profileURL = "http://xyz.com";
		String imageURL = "http://image.com";
		String secret = "sffsf";
		String accessToken = "453thfhy5y6";
		String refreshToken = "3f4f5g22ds";
		Instant expireTime = Instant.now();
		Instant createdDate = Instant.now();
		Instant modifiedDate = Instant.now();

		OauthUser oauthUser = new  OauthUser();
		
		oauthUser.setUserId(userId);		
		oauthUser.setAccessToken(accessToken);
		oauthUser.setCreatedDate(createdDate);
		oauthUser.setDisplayName(displayName); 
		oauthUser.setExpireTime(expireTime);
		oauthUser.setImageURL(imageURL);
		oauthUser.setJwtToken(jwtToken);
		oauthUser.setLoginPassExpire(loginPassExpire);
		oauthUser.setModifiedDate(modifiedDate);
		oauthUser.setProfileURL(profileURL);
		oauthUser.setProviderCd(providerCd);
		oauthUser.setProviderUserId(providerUserId);
		oauthUser.setRank(rank);
		oauthUser.setRefreshToken(refreshToken);
		oauthUser.setSecret(secret);
		
		Assert.assertEquals(accessToken, oauthUser.getAccessToken());
		Assert.assertEquals(userId, oauthUser.getUserId());
		Assert.assertEquals(createdDate, oauthUser.getCreatedDate());
		Assert.assertEquals(displayName, oauthUser.getDisplayName());
		Assert.assertEquals(expireTime, oauthUser.getExpireTime());
		Assert.assertEquals(imageURL, oauthUser.getImageURL());
		Assert.assertEquals(loginPassExpire, oauthUser.isLoginPassExpire());
		Assert.assertEquals(userId, oauthUser.getUserId());
		Assert.assertEquals(modifiedDate, oauthUser.getModifiedDate());
		Assert.assertEquals(profileURL, oauthUser.getProfileURL());
		Assert.assertEquals(providerCd, oauthUser.getProviderCd());
		Assert.assertEquals(providerUserId, oauthUser.getProviderUserId());
		Assert.assertEquals(rank, oauthUser.getRank());
		Assert.assertEquals(refreshToken, oauthUser.getRefreshToken());
		Assert.assertEquals(jwtToken, oauthUser.getJwtToken());
		
		OauthUser oauthUserObj = new OauthUser(oauthUser);
		Assert.assertNotNull(oauthUserObj);
		Assert.assertNotNull(oauthUserObj.toString());
		
	}


}
