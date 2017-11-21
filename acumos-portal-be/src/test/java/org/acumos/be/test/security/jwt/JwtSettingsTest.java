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

/**
 * 
 *  @author VS00509519
 */
package org.acumos.be.test.security.jwt;

import org.acumos.portal.be.security.jwt.JwtSettings;
import org.junit.Assert;
import org.junit.Test;

public class JwtSettingsTest {

	@Test
	public void testMailDataParameter() {
		JwtSettings jwtSettings = new JwtSettings();
		Integer tokenExpirationTime = 1000000;
		String tokenIssuer = "john";
		String tokenSigningKey = "secret key";
		Integer refreshTokenExpTime = 50000;
		
		jwtSettings.setTokenExpirationTime(tokenExpirationTime);
		jwtSettings.setTokenIssuer(tokenIssuer);
		jwtSettings.setTokenSigningKey(tokenSigningKey);
		jwtSettings.setRefreshTokenExpTime(refreshTokenExpTime);

		Assert.assertEquals(tokenExpirationTime, jwtSettings.getTokenExpirationTime());
		Assert.assertEquals(tokenIssuer, jwtSettings.getTokenIssuer());
		Assert.assertEquals(tokenSigningKey, jwtSettings.getTokenSigningKey());
		Assert.assertEquals(refreshTokenExpTime, jwtSettings.getRefreshTokenExpTime());
	}

}
