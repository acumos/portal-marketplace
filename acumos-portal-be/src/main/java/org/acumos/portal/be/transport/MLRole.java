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

import java.time.Instant;
import java.util.List;

import org.acumos.portal.be.common.JSONTags;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *	MLRole Class to hold user roles Information
 */
public class MLRole {

	public MLRole() {

	}
	@JsonProperty(value=JSONTags.ROLE_ID)
	private String roleId;
	@JsonProperty(value=JSONTags.ROLE_NAME)
	private String name;
	@JsonProperty(value=JSONTags.ROLE_ACTIVE)
	private boolean active;
	@JsonProperty(value=JSONTags.ROLE_CREATED)
	private Instant created;
	@JsonProperty(value=JSONTags.ROLE_MODIFIED)
	private Instant modified;
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

	public Instant getCreated() {
		return created;
	}

	public void setCreated(Instant created) {
		this.created = created;
	}

	public Instant getModified() {
		return modified;
	}

	public void setModified(Instant modified) {
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
