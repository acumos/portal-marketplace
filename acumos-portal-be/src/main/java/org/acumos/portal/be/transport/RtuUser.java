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
 * 
 * Author: Vasudeva Rao Kallepalli
 * ===============LICENSE_END=========================================================
 */

package org.acumos.portal.be.transport;

import java.util.Objects;


public class RtuUser extends AbstractResponseObject {
	
	private String firstName;
	private String lastName;
	private String emailId;
	private boolean active;
	private String userId;
	private boolean associatedWithRtuFlag=false;
	


	public RtuUser() {
	}
	
	public RtuUser(RtuUser userCopy) {
		this.firstName = userCopy.getFirstName();
		this.lastName = userCopy.getLastName();
		this.emailId = userCopy.getEmailId();
		this.active = userCopy.isActive();
		this.userId = userCopy.getUserId();
		this.associatedWithRtuFlag=userCopy.isAssociatedWithRtuFlag();
	}
	


	@Override
	public String toString() {
		return "RtuUser [firstName=" + firstName + ", lastName=" + lastName + ", emailId=" + emailId + ", active=" + active + ", userId=" + userId  + ", isAssociatedWithRtu=" + associatedWithRtuFlag + "]";
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public boolean isAssociatedWithRtuFlag() {
		return associatedWithRtuFlag;
	}

	public void setAssociatedWithRtuFlag(boolean associatedWithRtuFlag) {
		this.associatedWithRtuFlag = associatedWithRtuFlag;
	}

	
	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (!(that instanceof RtuUser))
			return false;
		RtuUser thatObj = (RtuUser) that;
		return Objects.equals(userId, thatObj.userId);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(firstName, lastName, emailId, active,userId,associatedWithRtuFlag);
	}

}

