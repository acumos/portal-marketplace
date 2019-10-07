package org.acumos.portal.be.common;

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
		String loggedInUserName = null;
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null) {
			throw new IllegalArgumentException("Not able to determine authenticated user");
		}
		Object principal = authentication.getPrincipal();
		if (principal instanceof UserDetails) {
			loggedInUserName = ((UserDetails) principal).getUsername();
		}
		return loggedInUserName;
	}
}
