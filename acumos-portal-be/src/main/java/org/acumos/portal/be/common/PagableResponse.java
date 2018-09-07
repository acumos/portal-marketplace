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

public class PagableResponse<T> implements Serializable {

	private static final long serialVersionUID = -2934104266393591755L;

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
	 * Json property totalElements.
	 */
	@JsonProperty(value = "totalElements")
	private long totalElements;
	
	/**
	 * Json property totalElements.
	 */
	@JsonProperty(value = "totalPages")
	private int totalPages;
	
	
	/**
	 * Json property totalElements.
	 */
	@JsonProperty(value = "size")
	private int size;
	
	
	public String getResponseDetail() {
		return responseDetail;
	}

	public void setResponseDetail(String responseDetail) {
		this.responseDetail = responseDetail;
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

	/**
	 * @return the totalElements
	 */
	public long getTotalElements() {
		return totalElements;
	}

	/**
	 * @param totalElements the totalElements to set
	 */
	public void setTotalElements(long totalElements) {
		this.totalElements = totalElements;
	}

	/**
	 * @return the totalPages
	 */
	public int getTotalPages() {
		return totalPages;
	}

	/**
	 * @param totalPages the totalPages to set
	 */
	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}

	/**
	 * @return the size
	 */
	public int getSize() {
		return size;
	}

	/**
	 * @param size the size to set
	 */
	public void setSize(int size) {
		this.size = size;
	}
}

