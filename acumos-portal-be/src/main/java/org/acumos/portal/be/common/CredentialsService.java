package org.acumos.portal.be.common;

import org.acumos.portal.be.security.AuthenticatedUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
