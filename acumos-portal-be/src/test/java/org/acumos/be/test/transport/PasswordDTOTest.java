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

import org.acumos.portal.be.transport.PasswordDTO;
import org.junit.Assert;
import org.junit.Test;	

public class PasswordDTOTest {
	
	@Test	
	public void testOauthUserParameter(){
		String userId = "83d5259f-48b7-4fe1-9fd6-d1166f8f3691";
		String oldPassword = "dsds";
		String newPassword = "dd24fs";

		PasswordDTO passwordDTO = new  PasswordDTO();
		
		passwordDTO.setUserId(userId);		
		passwordDTO.setOldPassword(oldPassword);
		passwordDTO.setNewPassword(newPassword);
		
		
		Assert.assertEquals(oldPassword, passwordDTO.getOldPassword());
		Assert.assertEquals(userId, passwordDTO.getUserId());
		Assert.assertEquals(newPassword, passwordDTO.getNewPassword());
		

	}

}
