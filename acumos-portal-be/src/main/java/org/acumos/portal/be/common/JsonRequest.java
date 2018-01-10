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

import org.acumos.portal.be.common.JSONTags;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
* This class represents a common format set for the request body sent from the client.
* Getters and setters encapsulate the fields of a class by making them accessible 
* only through its public methods and keep the values themselves private.
 * @param <T> Type inside request
*/

public class JsonRequest<T> implements Serializable{

	private static final long serialVersionUID = 7576436006913504503L;

	/**
	 * Json property requestFrom.
	 */
	@JsonProperty(value = JSONTags.TAG_REQUEST_FROM)
	private String requestFrom;

	/**
	 * Json property requestId.
	 */
	@JsonProperty(value = JSONTags.TAG_REQUEST_ID)
	private String requestId;
	
	/**
	 * Json property body. It represents the type of generic object.
	 */
	@JsonProperty(value = JSONTags.TAG_REQUEST_BODY)
	private T body;

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	
	
	public T getBody() {
		return body;
	}

	public void setBody(T body) {
		this.body = body;
	}

	public String getRequestFrom() {
		return requestFrom;
	}

	public void setRequestFrom(String requestFrom) {
		this.requestFrom = requestFrom;
	}
	
}
