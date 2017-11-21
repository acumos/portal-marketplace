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

package org.acumos.portal.be.transport;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.acumos.cds.domain.MLPTag;
import org.acumos.portal.be.common.JSONTags;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author
 *	MLRole Class to hold user roles Information
 */
public class MLRole {

	public MLRole() {

	}
	private Map<MLRole, Long> roleIdUserCount;
		
	public Map<MLRole, Long> getRoleIdUserCount() {
		return roleIdUserCount;
	}
	public void setRoleIdUserCount(Map<MLRole, Long> roleIdUserCount) {
		this.roleIdUserCount = roleIdUserCount;
	}

	@JsonProperty(value=JSONTags.ROLE_ID)
	private String roleId;
	@JsonProperty(value=JSONTags.ROLE_NAME)
	private String name;
	@JsonProperty(value=JSONTags.ROLE_ACTIVE)
	private boolean active;
	@JsonProperty(value=JSONTags.ROLE_CREATED)
	private Date created;
	@JsonProperty(value=JSONTags.ROLE_MODIFIED)
	private Date modified;
	private List<String> permissionList;
	private int roleCount;
	
	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getModified() {
		return modified;
	}

	public void setModified(Date modified) {
		this.modified = modified;
	}

	public List<String> getPermissionList() {
		return permissionList;
	}

	public void setPermissionList(List<String> permissionList) {
		this.permissionList = permissionList;
	}
	public int getRoleCount() {
		return roleCount;
	}
	public void setRoleCount(int roleCount) {
		this.roleCount = roleCount;
	}
	

}
