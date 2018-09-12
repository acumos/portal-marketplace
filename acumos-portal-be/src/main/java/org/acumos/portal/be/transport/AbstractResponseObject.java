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

import java.util.List;

import org.acumos.cds.domain.MLPRole;

public class AbstractResponseObject {

	/**
	 * Abstract superclass for Response from Controllers.
	 */
	public AbstractResponseObject() {
	}

	private boolean loginPassExpire;
	private String JwtToken;
	private List<MLPRole> userAssignedRolesList;
	private boolean isAdmin;
	private boolean isPublisher;
	private boolean firstLogin;

	public boolean isLoginPassExpire() {
		return loginPassExpire;
	}

	public void setLoginPassExpire(boolean loginPassExpire) {
		this.loginPassExpire = loginPassExpire;
	}

	public String getJwtToken() {
		return JwtToken;
	}

	public void setJwtToken(String jwtToken) {
		JwtToken = jwtToken;
	}

	public List<MLPRole> getUserAssignedRolesList() {
		return userAssignedRolesList;
	}

	public void setUserAssignedRolesList(List<MLPRole> userAssignedRolesList) {
		this.userAssignedRolesList = userAssignedRolesList;
	}

	/**
	 * @return the isAdmin
	 */
	public boolean isAdmin() {
		return isAdmin;
	}

	/**
	 * @param isAdmin the isAdmin to set
	 */
	public void setAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}

	/**
	 * @return the isPublisher
	 */
	public boolean isPublisher() {
		return isPublisher;
	}

	/**
	 * @param isPublisher the isPublisher to set
	 */
	public void setPublisher(boolean isPublisher) {
		this.isPublisher = isPublisher;
	}

	/**
	 * @return the firstLogin
	 */
	public boolean isFirstLogin() {
		return firstLogin;
	}

	/**
	 * @param firstLogin the firstLogin to set
	 */
	public void setFirstLogin(boolean firstLogin) {
		this.firstLogin = firstLogin;
	}
}
