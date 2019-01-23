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
import java.util.Map;
import java.util.Set;

import org.acumos.cds.domain.MLPRole;
import org.acumos.cds.domain.MLPTag;


public class User extends AbstractResponseObject {
	
	private String firstName;
	private String lastName;
	private String emailId;
	private String username;
	private String password;
	private String active;
	private Instant lastLogin;
	private Instant created;
	private Instant modified;
	private String userId;
	private String loginName;
	private String orgName;
	private byte[] picture;
	private String jwttoken;
	private String role;
	private String roleId;
	private String updatedRole;
	private String updatedRoleId;
	private List<String> userIdList;
	private List<String> userNewRoleList;
	private List<MLPRole> userAssignedRolesList;
	private Map<String,List<String>> userRolesList;
	private String bulkUpdate;
	private String apiTokenHash;
	private String verifyToken;
	private String status;
	private Set<MLPTag> tags;
	private String apiToken;


	public String getBulkUpdate() {
		return bulkUpdate;
	}

	public void setBulkUpdate(String bulkUpdate) {
		this.bulkUpdate = bulkUpdate;
	}

	public List<MLPRole> getUserAssignedRolesList() {
		return userAssignedRolesList;
	}

	public void setUserAssignedRolesList(List<MLPRole> userAssignedRolesList) {
		this.userAssignedRolesList = userAssignedRolesList;
	}
	
	
	public List<String> getUserIdList() {
		return userIdList;
	}
	
	public void setUserIdList(List<String> userIdList) {
		this.userIdList = userIdList;
	}

	public List<String> getUserNewRoleList() {
		return userNewRoleList;
	}

	public void setUserNewRoleList(List<String> userNewRoleList) {
		this.userNewRoleList = userNewRoleList;
	}

	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getRoleId() {
		return roleId;
	}
	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	/**
	 * Default Constructor
	 */
	public User() {
	}
	/**
	 * 
	 * Clone of the User Object
	 * @param userCopy Object to copy
	 * 
	 */
	public User(User userCopy) {
		this.firstName = userCopy.getFirstName();
		this.lastName = userCopy.getLastName();
		this.emailId = userCopy.getEmailId();
		this.username = userCopy.getUsername();
		this.active = userCopy.getActive();
		this.lastLogin = userCopy.getLastLogin();
		this.created = userCopy.getCreated();
		this.modified = userCopy.getModified();
		this.userId = userCopy.getUserId();
	}
	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}
	/**
	 * @param firstName the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}
	/**
	 * @param lastName the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	/**
	 * @return the emailId
	 */
	public String getEmailId() {
		return emailId;
	}
	/**
	 * @param emailId the emailId to set
	 */
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}
	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}	 
	/**
	 * @return the lastLogin
	 */
	public Instant getLastLogin() {
		return lastLogin;
	}
	/**
	 * @param lastLogin the lastLogin to set
	 */
	public void setLastLogin(Instant lastLogin) {
		this.lastLogin = lastLogin;
	}
	/**
	 * @return the created
	 */
	public Instant getCreated() {
		return created;
	}
	/**
	 * @param created the created to set
	 */
	public void setCreated(Instant created) {
		this.created = created;
	}
	/**
	 * @return the modified
	 */
	public Instant getModified() {
		return modified;
	}
	/**
	 * @param modified the modified to set
	 */
	public void setModified(Instant modified) {
		this.modified = modified;
	}
	
	
	@Override
	public String toString() {
		return "User [firstName=" + firstName + ", lastName=" + lastName + ", emailId=" + emailId + ", username="
				+ username + ", active=" + active + ", lastLogin=" + lastLogin + ", created="
				+ created + ", modified=" + modified + "]";
	}
	public String getLoginName() {
		return loginName;
	}
	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}
	public String getOrgName() {
		return orgName;
	}
	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}
	public String getActive() {
		return active;
	}
	public void setActive(String active) {
		this.active = active;
	}
	public byte[] getPicture() {
		return picture;
	}
	public void setPicture(byte[] picture) {
		this.picture = picture;
	}
	
	public String getJwttoken() {
		return jwttoken;
	}

	public void setJwttoken(String jwttoken) {
		this.jwttoken = jwttoken;
	}
	public String getUpdatedRole() {
		return updatedRole;
	}
	public void setUpdatedRole(String updatedRole) {
		this.updatedRole = updatedRole;
	}
	public String getUpdatedRoleId() {
		return updatedRoleId;
	}
	public void setUpdatedRoleId(String updatedRoleId) {
		this.updatedRoleId = updatedRoleId;
	}

	public Map<String, List<String>> getUserRolesList() {
		return userRolesList;
	}

	public void setUserRolesList(Map<String, List<String>> userRolesList) {
		this.userRolesList = userRolesList;
	}

	public String getApiTokenHash() {
		return apiTokenHash;
	}

	public void setApiTokenHash(String apiTokenHash) {
		this.apiTokenHash = apiTokenHash;
	}

	/**
	 * @return the verifyToken
	 */
	public String getVerifyToken() {
		return verifyToken;
	}

	/**
	 * @param verifyToken the verifyToken to set
	 */
	public void setVerifyToken(String verifyToken) {
		this.verifyToken = verifyToken;
	}

	public void setStatus(String status) {
		this.status = status;
		
	}

	public String getStatus() {
		return status;
	}

	/**
	 * @return the tags
	 */
	public Set<MLPTag> getTags() {
		return tags;
	}

	/**
	 * @param tags the tags to set
	 */
	public void setTags(Set<MLPTag> tags) {
		this.tags = tags;
	}

	public String getApiToken() {
		return apiToken;
	}

	public void setApiToken(String apiToken) {
		this.apiToken = apiToken;
	}
}

