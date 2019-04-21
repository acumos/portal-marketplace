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

import org.acumos.portal.be.transport.RtuUser;
import org.junit.Assert;
import org.junit.Test;

public class RtuUserTest {

	@Test	
	public void testOauthUserParameter(){
		
		String firstName="Acumos";
		String lastName="Test";
		String emailId="admin@acumos.com";
		boolean active=true;
		String userId="abcd-123456-efgh-789";
		boolean associatedWithRtuFlag=false;

		
		RtuUser user= new RtuUser();
		user.setActive(active);
		user.setEmailId(emailId);
		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setUserId(userId);
		user.setAssociatedWithRtuFlag(associatedWithRtuFlag);
		
		Assert.assertEquals(emailId, user.getEmailId());
		Assert.assertEquals(firstName, user.getFirstName());
		Assert.assertEquals(lastName, user.getLastName());
		Assert.assertEquals(userId, user.getUserId());
	}
}
