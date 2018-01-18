package org.acumos.be.test.security.jwt;

import org.acumos.cds.domain.MLPUser;
import org.acumos.portal.be.security.AuthenticatedUserDetails;
import org.junit.Assert;
import org.junit.Test;

public class AuthenticatedUserDetailsTest {

	@Test
	public void authenticatedUserDetailsParam() {
		
		MLPUser user = new MLPUser();
		user.setUserId("a001");
		user.setLoginName("testUser");
		Assert.assertNotNull(user);
		Assert.assertNotNull(new AuthenticatedUserDetails(user));
	}
}
