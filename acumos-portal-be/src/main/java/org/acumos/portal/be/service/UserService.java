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
 */
package org.acumos.portal.be.service;

import java.util.List;

import org.acumos.portal.be.transport.MLRole;
import org.acumos.portal.be.transport.User;

import org.acumos.cds.domain.MLPRole;
import org.acumos.cds.domain.MLPUser;
import org.acumos.cds.transport.RestPageRequest;

public interface UserService {
	
	User save(User user);
	
	List<User> getAllUser();
	
	MLPUser login(String username, String password);
	
	MLPUser findUserByEmail(String emailId);
	
	MLPUser findUserByUsername(String username);
	
	boolean delete();
	
	/**
	 * Method to support User Account Password Reset functionality 
	 * @param emailId
	 * email ID
	 * @return true or false
	 * @throws Exception on failure
	 */
	boolean resetUserPassword(String emailId) throws Exception;
	
	/**
	 *  Method to support User Account Password Update functionality 
	 * @param userId 
	 * user ID
	 * @param oldPassword 
	 * old password
	 * @param newPassword
	 * new password
	 * @return true or false
	 * @throws Exception on failure
	 */
	boolean changeUserPassword(String userId, String oldPassword, String newPassword) throws Exception;
	
	void updateUser(User user);
	
	void forgetPassword(MLPUser mlpUser) throws Exception;

	MLPUser findUserByUserId(String userId);  
	
	List<MLPRole> getUserRole(String userId);
	
	void updateUserImage(MLPUser user);

	void updateBulkUsers(MLPUser mlpUser);

	MLRole getRoleCountForUser(RestPageRequest pageRequest);

	void deleteBulkUsers(String mlpUser);

	void generatePassword(MLPUser mlpUser);

}
