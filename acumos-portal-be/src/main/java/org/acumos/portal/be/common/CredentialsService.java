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

package org.acumos.portal.be.common;

import org.acumos.portal.be.security.AuthenticatedUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


/**
 * Service to securely get the user name Rather than accessing the user name from the client / query
 * parameter
 */
@Service
public class CredentialsService {

	/**
	 * 
	 * Note - If service doesn't require authentication this api should not be called.
	 * 
	 * @return authenticated user name (not id)
	 */
	public String getLoggedInUserName() {
		return getAuth().getUsername();
	}

		/**
	 * 
	 * Note - If service doesn't require authentication this api should not be called.
	 * 
	 * @return authenticated user id (not name)
	 */
	public String getLoggedInUserId() {
		return getAuth().getUserId();
	}

	private AuthenticatedUserDetails getAuth() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null) {
			throw new IllegalArgumentException("Not able to determine authenticated user");
		}
		Object principal = authentication.getPrincipal();
		if (principal instanceof AuthenticatedUserDetails) {
			return ((AuthenticatedUserDetails) principal);
		}
		return null;
	}
}
