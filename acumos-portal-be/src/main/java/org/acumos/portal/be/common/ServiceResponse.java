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

package org.acumos.portal.be.common;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;


/**
* This class represents a common format set for the request body sent from the client.
* Getters and setters encapsulate the fields of a class by making them accessible 
* only through its public methods and keep the values themselves private.
 * @param <T> Type inside request
*/

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "serviceResponse")
public class ServiceResponse implements Serializable {

	public ServiceResponse(AuthenticationSuccess authenticationSuccess) {
		super();
		this.authenticationSuccess = authenticationSuccess;
	}

	public ServiceResponse() {
		super();
	}

	private static final long serialVersionUID = 7576436006913504403L;


	private AuthenticationSuccess authenticationSuccess;
	

	private String authenticationFailure;

	public AuthenticationSuccess getAuthenticationSuccess() {
		return authenticationSuccess;
	}

	public void setAuthenticationSuccess(AuthenticationSuccess authenticationSuccess) {
		this.authenticationSuccess = authenticationSuccess;
	}

	/**
	 * @return the authenticationFailure
	 */
	public String getAuthenticationFailure() {
		return authenticationFailure;
	}

	/**
	 * @param authenticationFailure the authenticationFailure to set
	 */
	public void setAuthenticationFailure(String authenticationFailure) {
		this.authenticationFailure = authenticationFailure;
	}

}
