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

package org.acumos.portal.be.security.jwt;


import org.acumos.portal.be.common.exception.MalformedException;
import org.acumos.portal.be.service.UserService;
import org.acumos.portal.be.service.impl.AbstractServiceImpl;
import org.acumos.portal.be.service.impl.JWTTokenValidation;
import org.acumos.portal.be.service.impl.PortalRestClienttImpl;
import org.acumos.portal.be.transport.User;
import org.acumos.portal.be.util.PortalUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;

import org.acumos.cds.domain.MLPUser;


@Service
public class TokenValidation extends AbstractServiceImpl implements JWTTokenValidation {
	
	@Autowired
	JwtTokenUtil jwtTokenUtil;

	@Autowired
	UserService userService;
	

	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());	


	public boolean tokenRegnerationAndValidation(String userToken) throws MalformedException {
		Boolean isVallidToken = false;
		// JwtTokenUtil jwtTokenUtil = new JwtTokenUtil();

		if (userToken != null) {
			// check token expired or not
			if (!jwtTokenUtil.isTokenExpired(userToken)) {
				String userName = jwtTokenUtil.getUsernameFromToken(userToken);
				if (userName != null) {
					MLPUser mlpUser = userService.findUserByUsername(userName);
					if (mlpUser != null) {
						String jwtTokenFromDB = mlpUser.getAuthToken();
						if (jwtTokenFromDB != null) {
							String userNameFromDB = jwtTokenUtil.getUsernameFromToken(jwtTokenFromDB);
							MLPUser mlpUserFromDB = userService.findUserByUsername(userNameFromDB);
							if (mlpUserFromDB != null) {
								if (mlpUserFromDB.getUserId().equals(mlpUser.getUserId())) {
									isVallidToken = true;
								} else {
									isVallidToken = false;
								}
							}
						}
					}
				}

			}
		}
		log.debug(" isVallidToken : " + isVallidToken);
		return isVallidToken;
	}

}
