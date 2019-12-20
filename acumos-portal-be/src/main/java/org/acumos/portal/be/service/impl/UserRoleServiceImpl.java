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

import java.lang.invoke.MethodHandles;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.acumos.cds.client.ICommonDataServiceRestClient;
import org.acumos.cds.domain.MLPCatalog;
import org.acumos.cds.domain.MLPRole;
import org.acumos.cds.domain.MLPRoleFunction;
import org.acumos.cds.domain.MLPUser;
import org.acumos.cds.transport.RestPageRequest;
import org.acumos.cds.transport.RestPageResponse;
import org.acumos.portal.be.service.UserRoleService;
import org.acumos.portal.be.service.UserService;
import org.acumos.portal.be.transport.MLRole;
import org.acumos.portal.be.transport.MLRoleFunction;
import org.acumos.portal.be.transport.User;
import org.acumos.portal.be.util.PortalUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserRoleServiceImpl extends AbstractServiceImpl implements UserRoleService{

	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());	
	
	@Autowired
	private UserService userService;
	
	public UserRoleServiceImpl()	{
		
	}
	
	@Override
	public List<MLRole> getAllRoles() {
		log.debug("getAllRoles");
		List<MLRole> mlRoles = null;
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		Map<String, Object> queryParameters = new HashMap<>();
		queryParameters.put("active", true);
		RestPageRequest pageRequest = new RestPageRequest();
		pageRequest.setPage(0);
		pageRequest.setSize(1000);
		List<MLPRole> roleList = dataServiceRestClient.searchRoles(queryParameters, true, pageRequest).getContent();
		if (!PortalUtils.isEmptyList(roleList)) {
			mlRoles = new ArrayList<>();
			for (MLPRole mlpRole : roleList) {
				MLRole mlRole = PortalUtils.convertToMLRole(mlpRole);
				mlRoles.add(mlRole);
			}
		}
		return mlRoles;
	}
	
	
	@Override
    public List<MLRole> getRolesForUser(String userId) {
        log.debug("getAllRoles");
        List<MLRole> mlRoles = null;
        ICommonDataServiceRestClient dataServiceRestClient = getClient();
        List<MLPRole> mlpRoles = dataServiceRestClient.getUserRoles(userId);
        if (!PortalUtils.isEmptyList(mlpRoles)) {
            mlRoles = new ArrayList<>();
            for (MLPRole mlpRole : mlpRoles) {
                MLRole mlRole = PortalUtils.convertToMLRole(mlpRole);
                mlRoles.add(mlRole);
            }
        }
        return mlRoles; 
    }
	

	@Override
	public MLPRole getRole(String roleId) {
		log.debug("getRole :{}" +roleId);
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		return dataServiceRestClient.getRole(roleId);		
	}

	@Override
	public MLPRole createRole(MLRole role) {
		log.debug("createRole");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		MLPRole mlpRole = null;
		//Always create the role with active status
		role.setActive(true);
		mlpRole = dataServiceRestClient.createRole(PortalUtils.convertToMLPRole(role));
		return mlpRole;
	}

	@Override
	public void updateRole(String roleId,String roleName) {
		log.debug("updateRole");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		MLPRole mlpRole=new MLPRole();
		mlpRole.setRoleId(roleId);
		mlpRole.setName(roleName);
		mlpRole.setActive(true);
		mlpRole.setModified(Instant.now());
		dataServiceRestClient.updateRole(mlpRole);
	}

	@Override
	public void deleteRole(String roleId) {
		log.debug("deleteRole");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		dataServiceRestClient.deleteRole(roleId);
	}

	@Override
	public MLRoleFunction getRoleFunction(String roleId, String roleFunctionId) {
		log.debug("getRole");
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
		log.debug("createRole");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		MLPRoleFunction mlpRolefunction = null;
		mlpRolefunction = dataServiceRestClient.createRoleFunction(mlRoleFunction);
		return mlpRolefunction;
	}

	@Override
	public void updateRoleFunction(MLPRoleFunction mlpRoleFunction) {
		log.debug("updateRoleFunction : "+mlpRoleFunction.getRoleId());
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		dataServiceRestClient.updateRoleFunction(mlpRoleFunction);		
	}
	
	@Override
	public void deleteRoleFunction(String roleId, String roleFunctionId) {
		log.debug("deleteRole");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		dataServiceRestClient.deleteRoleFunction(roleId,roleFunctionId);
	}

	@Override
	public void addUserRole(String userId, String roleId) {
		log.debug("addUserRole");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		dataServiceRestClient.addUserRole(userId,roleId);
	}	
	
	@Override
	public void updateUserRole(User user) {
		log.debug("updateUserRole");
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
		log.debug("updateUserRole");
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
        log.debug("getAllRoles");
        MLRole roleUserMap = new MLRole();
        ICommonDataServiceRestClient dataServiceRestClient = getClient();        
		RestPageResponse<MLPUser> userList = dataServiceRestClient.getUsers(pageRequest);
		 
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
			i++;
		}
		return roleUserMap; 
    }

	@Override
	public void updateUserRoles(User user) {
		log.debug("updateUserRoles");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();		
		if(user.getUserIdList() != null && user.getUserNewRoleList()!= null){
			for(String userId : user.getUserIdList()){				
				dataServiceRestClient.updateUserRoles(userId, user.getUserNewRoleList());
			}
		}
	}
	
	@Override
	public List<MLRole> getRoleUsersCount() {
		log.debug("updateUserRoles");
		List<MLRole> mlRoleList = new ArrayList<>();

		Map<String, List<MLRole>> mlRoleMap = new HashMap<>();
		List<User> userList = userService.getAllUser();
		if (userList != null && !userList.isEmpty()) {
			List<MLRole> mlRoleProcessingList = new ArrayList<>();
			for (User user : userList) {
				if ("true".equals(user.getActive())) {
					for (MLPRole mlpRole : user.getUserAssignedRolesList()) {
						MLRole mlRole = PortalUtils.convertToMLRole(mlpRole);
						if (mlRoleMap.containsKey(mlRole.getRoleId())) {
							mlRoleProcessingList = new ArrayList<>();
							mlRoleProcessingList.addAll(mlRoleMap.get(mlRole.getRoleId()));
							mlRoleProcessingList.add(mlRole);
							mlRoleMap.put(mlRole.getRoleId(), mlRoleProcessingList);
						} else {
							mlRoleProcessingList = new ArrayList<>();
							mlRoleProcessingList.add(mlRole);
							mlRoleMap.put(mlRole.getRoleId(), mlRoleProcessingList);
						}

					}
				}
			}
			for (Entry<String, List<MLRole>> entrySet : mlRoleMap.entrySet()) {
				List<MLRole> mlRoles = entrySet.getValue();
				if (!mlRoles.isEmpty()) {
					MLRole mlRole = mlRoles.get(0);
					mlRole.setRoleCount(mlRoles.size());
					mlRoleList.add(mlRole);
				}

			}
		}
		return mlRoleList;
	}

	
	@Override
	public void addCatalogsInRole(List<String> catalogIds, String roleId) {
		log.debug("addCatalogsInRole for role : "+roleId);
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		dataServiceRestClient.addCatalogsInRole(catalogIds, roleId);
	}

	@Override
	public void dropCatalogsInRole(List<String> catalogIds, String roleId) {
		log.debug("dropCatalogsInRole for role : "+roleId);
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		dataServiceRestClient.dropCatalogsInRole(catalogIds, roleId);
	}

	@Override
	public List<MLPRoleFunction> getRoleFunctions(String roleId) {
		log.debug("getRoleFunctions for role : "+roleId);
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		return dataServiceRestClient.getRoleFunctions(roleId);
	}

	@Override
	public List<MLPCatalog> getRoleCatalogs(String roleId) {
		log.debug("getRoleCatalogs for role : "+roleId);
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		RestPageRequest pageRequest = new RestPageRequest();
		pageRequest.setPage(0);
		pageRequest.setSize(1000);
		return dataServiceRestClient.getRoleCatalogs(roleId,pageRequest).getContent();
	}

	@Override
	public void updateModulePermission(String roleId,List<String> modulePermissions) {
		log.debug("updateModulePermission for role : "+roleId);
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		List<MLPRoleFunction> roleFunctions=dataServiceRestClient.getRoleFunctions(roleId);
		for(MLPRoleFunction roleFunction:roleFunctions){
			dataServiceRestClient.deleteRoleFunction(roleId, roleFunction.getRoleFunctionId());
		}
		for (String permission : modulePermissions) {
			MLPRoleFunction roleFunction = new MLPRoleFunction();
			roleFunction.setRoleId(roleId);
			roleFunction.setName(permission);
			dataServiceRestClient.createRoleFunction(roleFunction);
		}
	}

	@Override
	public void updateCatalogsInRole(List<String> catalogIds, String roleId) {
		log.debug("updateModulePermission for role : "+roleId);
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		RestPageRequest pageRequest = new RestPageRequest();
		pageRequest.setPage(0);
		pageRequest.setSize(1000);
		List<MLPCatalog> catalogs=dataServiceRestClient.getRoleCatalogs(roleId,pageRequest).getContent();
		List<String> catalogList=catalogs.stream().map(MLPCatalog::getCatalogId).collect(Collectors.toList());
		if(!PortalUtils.isEmptyList(catalogList)) 
			dataServiceRestClient.dropCatalogsInRole(catalogList, roleId);
		dataServiceRestClient.addCatalogsInRole(catalogIds, roleId);
	}

	@Override
	public List<MLPUser> getRoleUsers(String roleId) {
		log.debug("getRoleCatalogs for role : "+roleId);
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		RestPageRequest pageRequest = new RestPageRequest();
		pageRequest.setPage(0);
		pageRequest.setSize(1000);
		return dataServiceRestClient.getRoleUsers(roleId,pageRequest).getContent();
	
	}

	@Override
	public void dropUsersInRole(List<String> userIds, String roleId) {
		log.debug("addUsersInRole for role : "+roleId);
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		dataServiceRestClient.dropUsersInRole(userIds, roleId);
	}

	@Override
	public List<String> getUserAccessCatalogIds(String userId) {
		log.debug("getUserAccessCatalogIds for user : "+userId);
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		return dataServiceRestClient.getUserAccessCatalogIds(userId);
	}
}
