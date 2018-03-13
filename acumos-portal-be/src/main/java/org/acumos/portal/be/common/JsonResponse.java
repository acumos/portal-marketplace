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


/**
* This class represents a common format set for the response send to the client.
* Getters and setters encapsulate the fields of a class by making them accessible 
* only through its public methods and keep the values themselves private.
* @JsonProperty(name), tells Jackson ObjectMapper to map the JSON property name to the annotated Java field's name.
*/

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class JsonResponse<T> implements Serializable {

	private static final long serialVersionUID = -2934104266393591755L;

	/**
	 * Json property status.
	 */
	@JsonProperty(value = JSONTags.TAG_RESPONSE_STATUS)
	private Boolean status;

	/**
	 * Json property statusCode.
	 */
	@JsonProperty(value = JSONTags.TAG_RESPONSE_STATUS_CODE)
	private int statusCode;
	
	/**
	 * Json property responseDetail.
	 */
	@JsonProperty(value = JSONTags.TAG_RESPONSE_DETAIL)
	private String responseDetail;

	/**
	 * Json property responseCode.
	 */
	@JsonProperty(value = JSONTags.TAG_RESPONSE_CODE)
	private String responseCode;

	/**
	 * Json property responseBody. It represents the type of generic object.
	 */
	@JsonProperty(value = JSONTags.TAG_RESPONSE_BODY)
	private T responseBody;
	
	/**
	 * Json property content. It represents the type of generic object.
	 */
	@JsonProperty(value = JSONTags.TAG_RESPONSE_CONTENT)
	private T content;
	
	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}

	public String getResponseDetail() {
		return responseDetail;
	}

	public void setResponseDetail(String responseDetail) {
		this.responseDetail = responseDetail;
	}

	/**
	 * Json property errorCode.
	 */
	@JsonProperty(value = JSONTags.TAG_ERROR_CODE)
	private String errorCode;

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
		
	public String getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	public T getResponseBody() {
		return responseBody;
	}

	public void setResponseBody(T responseBody) {
		this.responseBody = responseBody;
	}

	public T getContent() {
		return content;
	}

	public void setContent(T responseBody) {
		this.content = responseBody;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
}
