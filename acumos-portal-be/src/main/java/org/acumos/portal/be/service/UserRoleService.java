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

package org.acumos.portal.be.service;

import java.util.List;

import org.acumos.cds.domain.MLPCatalog;
import org.acumos.cds.domain.MLPRole;
import org.acumos.cds.domain.MLPRoleFunction;
import org.acumos.cds.domain.MLPUser;
import org.acumos.cds.transport.RestPageRequest;
import org.acumos.cds.transport.RestPageResponse;
import org.acumos.portal.be.common.exception.UserServiceException;
import org.acumos.portal.be.transport.MLRole;
import org.acumos.portal.be.transport.MLRoleFunction;
import org.acumos.portal.be.transport.User;

public interface UserRoleService {

	List<MLRole> getAllRoles() throws UserServiceException;

	MLPRole getRole(String roleId) throws UserServiceException;

	MLPRole createRole(MLRole role) throws UserServiceException;  

	List<MLRole> getRolesForUser(String userId);
	
	MLRole getRoleCountForUser(RestPageRequest pageRequest);
	
	void updateRole(String roleId,String roleName) throws UserServiceException;

	void deleteRole(String roleId) throws UserServiceException;

	MLRoleFunction getRoleFunction(String roleId, String roleFunctionId) throws UserServiceException;

	MLPRoleFunction createRoleFunction(MLPRoleFunction mlpRoleFunction) throws UserServiceException;

	void deleteRoleFunction(String roleId, String roleFunctionId) throws UserServiceException;

	void updateRoleFunction(MLPRoleFunction mlpRoleFunction) throws UserServiceException;

	void addUserRole(String userId, String roleId);

	//void updateUserRole(String userId, String roleId);

	void updateUserRole(User user);

	void updateUserRoleMulti(List<String> userId, List<String> roleId, List<String> updatedRoleId);

	void updateUserRoles(User user);

	List<MLRole> getRoleUsersCount();

	void addCatalogsInRole(List<String> catalogIds, String roleId);

	void dropCatalogsInRole(List<String> catalogIds, String roleId);

	List<MLPRoleFunction> getRoleFunctions(String roleId);

	List<MLPCatalog> getRoleCatalogs(String roleId);

	void updateModulePermission(String roleId, List<String> modulePermissions);

	void updateCatalogsInRole(List<String> catalogIds, String roleId);
	
	List<MLPUser> getRoleUsers(String roleId);
	
	void dropUsersInRole(List<String> userIds, String roleId);
	
	RestPageResponse<MLPCatalog> getUserAccessCatalogs(String userId,RestPageRequest pageRequest);

}
