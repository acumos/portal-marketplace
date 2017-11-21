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

package org.acumos.portal.be.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.acumos.portal.be.common.JsonRequest;
import org.acumos.portal.be.service.UserRoleService;
import org.acumos.portal.be.transport.MLRole;
import org.acumos.portal.be.transport.MLRoleFunction;
import org.acumos.portal.be.transport.User;
import org.acumos.portal.be.util.EELFLoggerDelegate;
import org.acumos.portal.be.util.PortalUtils;
import org.springframework.stereotype.Service;

import org.acumos.cds.client.ICommonDataServiceRestClient;
import org.acumos.cds.domain.MLPRole;
import org.acumos.cds.domain.MLPRoleFunction;
import org.acumos.cds.domain.MLPUser;
import org.acumos.cds.transport.RestPageRequest;
import org.acumos.cds.transport.RestPageResponse;

@Service
public class UserRoleServiceImpl extends AbstractServiceImpl implements UserRoleService{

	private static final EELFLoggerDelegate log = EELFLoggerDelegate.getLogger(UserRoleServiceImpl.class);
	
	public UserRoleServiceImpl()	{
		
	}
	
	@Override
	public List<MLRole> getAllRoles() {
		log.debug(EELFLoggerDelegate.debugLogger, "getAllRoles");
		List<MLRole> mlRoles = null;
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		Map<String, Object> queryParameters = new HashMap<>();
		//queryParameters.put("active", "Y"); // Fetch all active roles
		List<MLPRole> mlpRoles = dataServiceRestClient.searchRoles(queryParameters, false);
		if (!PortalUtils.isEmptyList(mlpRoles)) {
			mlRoles = new ArrayList<>();
			for (MLPRole mlpRole : mlpRoles) {
				MLRole mlRole = PortalUtils.convertToMLRole(mlpRole);
				mlRoles.add(mlRole);
				//break;
			}
		}
		return mlRoles;
	}
	
	
	@Override
    public List<MLRole> getRolesForUser(String userId) {
        log.debug(EELFLoggerDelegate.debugLogger, "getAllRoles");
        List<MLRole> mlRoles = null;
        ICommonDataServiceRestClient dataServiceRestClient = getClient();
        List<MLPRole> mlpRoles = dataServiceRestClient.getUserRoles(userId);
        if (!PortalUtils.isEmptyList(mlpRoles)) {
            mlRoles = new ArrayList<>();
            for (MLPRole mlpRole : mlpRoles) {
                MLRole mlRole = PortalUtils.convertToMLRole(mlpRole);
                mlRoles.add(mlRole);
                break;
            }
        }
        return mlRoles; 
    }
	

	@Override
	public MLRole getRole(String roleId) {
		log.debug(EELFLoggerDelegate.debugLogger, "getRole");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		MLRole mlRole = null;
		MLPRole mlpRole = dataServiceRestClient.getRole(roleId);
		if (mlpRole != null) {
			mlRole = PortalUtils.convertToMLRole(mlpRole);
		}
		return mlRole;
	}

	@Override
	public MLPRole createRole(MLRole role) {
		log.debug(EELFLoggerDelegate.debugLogger, "createRole");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		MLPRole mlpRole = null;
		mlpRole = dataServiceRestClient.createRole(PortalUtils.convertToMLPRole(role));
		return mlpRole;
	}

	@Override
	public void updateRole(JsonRequest<MLPRole> role) {
		log.debug(EELFLoggerDelegate.debugLogger, "updateRole");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		dataServiceRestClient.updateRole(role.getBody());
	}

	@Override
	public void deleteRole(String roleId) {
		log.debug(EELFLoggerDelegate.debugLogger, "deleteRole");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		dataServiceRestClient.deleteRole(roleId);
	}

	@Override
	public MLRoleFunction getRoleFunction(String roleId, String roleFunctionId) {
		log.debug(EELFLoggerDelegate.debugLogger, "getRole");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		MLRoleFunction mlRoleFunction = null;
		MLPRoleFunction mlpRoleFunction = dataServiceRestClient.getRoleFunction(roleId,roleFunctionId);	
		if (mlpRoleFunction != null) {
			mlRoleFunction = PortalUtils.convertToMLRoleFunction(mlpRoleFunction);
		}
		return mlRoleFunction;
	}

	@Override
	public MLPRoleFunction createRoleFunction(MLPRoleFunction mlRoleFunction) {  
		log.debug(EELFLoggerDelegate.debugLogger, "createRole");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		MLPRoleFunction mlpRolefunction = null;
		mlpRolefunction = dataServiceRestClient.createRoleFunction(mlRoleFunction);
		return mlpRolefunction;
	}

	@Override
	public void updateRoleFunction(JsonRequest<MLPRoleFunction> mlpRoleFunction) {
		log.debug(EELFLoggerDelegate.debugLogger, "deleteRole");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		dataServiceRestClient.updateRoleFunction(mlpRoleFunction.getBody());		
	}
	
	@Override
	public void deleteRoleFunction(String roleId, String roleFunctionId) {
		log.debug(EELFLoggerDelegate.debugLogger, "deleteRole");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		dataServiceRestClient.deleteRoleFunction(roleId,roleFunctionId);
	}

	@Override
	public void addUserRole(String userId, String roleId) {
		log.debug(EELFLoggerDelegate.debugLogger, "addUserRole");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		dataServiceRestClient.addUserRole(userId,roleId);
	}	
	
	@Override
	public void updateUserRole(User user) {
		log.debug(EELFLoggerDelegate.debugLogger, "updateUserRole");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		if(user.getUserAssignedRolesList() !=null && user.getUserId() != null){
			for(MLPRole role : user.getUserAssignedRolesList())
			dataServiceRestClient.dropUserRole(user.getUserId(), role.getRoleId());
		}
		if(user.getUserNewRoleList() !=null && user.getUserId() != null){
			for(String roleId : user.getUserNewRoleList()){
			dataServiceRestClient.addUserRole(user.getUserId(),roleId);
			}
		}

	}
	
	@Override
	public void updateUserRoleMulti(List<String> userIdList, List<String> roleIdList,List<String> updatedRoleIdList) {
		log.debug(EELFLoggerDelegate.debugLogger, "updateUserRole");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		for (String userId : userIdList) {
			for (String updatedRoleId : updatedRoleIdList) {
				if(updatedRoleId != null)
				  dataServiceRestClient.addUserRole(userId,updatedRoleId);
			}
			for (String roleId : roleIdList) {
				if(roleId !=null){
					dataServiceRestClient.dropUserRole(userId, roleId);
				}
			}
		}
		
	
	}

	@Override
    public MLRole getRoleCountForUser(RestPageRequest pageRequest) {
        log.debug(EELFLoggerDelegate.debugLogger, "getAllRoles");
        MLRole roleUserMap = new MLRole();
        ICommonDataServiceRestClient dataServiceRestClient = getClient();        
		RestPageResponse<MLPUser> userList = dataServiceRestClient.getUsers(pageRequest);
		RestPageResponse<MLPRole> roleList = dataServiceRestClient.getRoles(pageRequest);
		 
		Map<String, Map<String, String>> roleIdUserCount = new HashMap<>();
		int i=0;
		for (MLPUser mlpUser : userList) {
			
			String userId = mlpUser.getUserId();
			Map<String, String> roleDetails = new HashMap<>();
			List<MLPRole> mlpRoles = dataServiceRestClient.getUserRoles(userId);
			String roleId = mlpRoles.get(i).getRoleId();
			roleDetails.put("roleId",roleId);
			String roleName = mlpRoles.get(i).getName();
			roleDetails.put("roleName",roleName);
			String userCount = mlpRoles.size()+"";
			roleDetails.put("userCount",userCount);
			 
			
			roleIdUserCount.put(roleId, roleDetails);
		//	roleUserMap.setRoleIdUserCount(roleIdUserCount);
			i++;
		}
		
        
        return roleUserMap; 
    }

	@Override
	public void updateUserRoles(User user) {
		log.debug(EELFLoggerDelegate.debugLogger, "updateUserRoles");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();		
		if(user.getUserIdList() != null && user.getUserNewRoleList()!= null){
			for(String userId : user.getUserIdList()){				
				dataServiceRestClient.updateUserRoles(userId, user.getUserNewRoleList());
			}
		}
	}
	
	@Override
	public List<MLRole> getRoleUsersCount() {
		log.debug(EELFLoggerDelegate.debugLogger, "updateUserRoles");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		RestPageRequest pageRequest = new RestPageRequest();
		RestPageResponse<MLPRole> roleList = dataServiceRestClient.getRoles(pageRequest);
		List<MLRole> mlRoleList = new ArrayList<>();

		Map<MLRole, Long> rolesCountMap = new HashMap<>();
		for (MLPRole mlpRole : roleList) {
			MLRole mlRole = PortalUtils.convertToMLRole(mlpRole);
			Long roleCount = dataServiceRestClient.getRoleUsersCount(mlRole.getRoleId());
			mlRole.setRoleCount(roleCount.intValue());
			mlRoleList.add(mlRole);
			rolesCountMap.put(mlRole, roleCount);
		}
		return mlRoleList;
	}
}
