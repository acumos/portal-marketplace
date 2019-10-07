/*-
 * ===============LICENSE_START=======================================================
 * Acumos
 * ===================================================================================
 * Copyright (C) 2019 Nordix Foundation
 * ===================================================================================
 * This Acumos software file is distributed by Nordix Foundation
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

package org.acumos.be.test.security;

import org.acumos.cds.domain.MLPUser;
import org.acumos.portal.be.security.AuthenticatedUserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

/**
 * Allows us to mock the security context in unit tests
 * ensuring we test the getLoggedInUserName feature in integration tests
 * 
 * If using mock testing you can mock CredentialService.getLoggedInUserName as an
 * alternative;
 */
public class WithMockCustomUserSecurityContextFactory
		implements WithSecurityContextFactory<WithMLMockUser> {
	@Override
	public SecurityContext createSecurityContext(WithMLMockUser customUser) {
		SecurityContext context = SecurityContextHolder.createEmptyContext();
		MLPUser mockUser = new MLPUser();
		mockUser.setLoginName(customUser.username());
		mockUser.setFirstName(customUser.name());
		AuthenticatedUserDetails principal = new AuthenticatedUserDetails(mockUser);
		Authentication auth = new UsernamePasswordAuthenticationToken(principal,
				principal.getPassword(), principal.getAuthorities());
		context.setAuthentication(auth);
		return context;
	}
}
