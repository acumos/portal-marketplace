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

import org.acumos.cds.transport.RestPageRequest;

public class RestPageRequestPortal {
	
	public RestPageRequestPortal() {
		
	}

	private String[] nameKeyword;
	private String[] descriptionKeyword;	
	private String authorKeyword;
	private boolean active;
	private String[] modelTypeCodes;
	private String[] accessTypeCodes;
	private String[] validationStatusCodes;
	private String[] tags;
	private RestPageRequest pageRequest;
	private String sortBy;
	private String sortById;
	private String[] ownerIds;
	private String userId;
	private String taskStatus;
	
	/**
	 * @return the ownerIds
	 */
	public String[] getOwnerIds() {
		return ownerIds;
	}
	/**
	 * @param ownerIds the ownerIds to set
	 */
	public void setOwnerIds(String[] ownerIds) {
		this.ownerIds = ownerIds;
	}
	public String[] getNameKeyword() {
		return nameKeyword;
	}
	public void setNameKeyword(String[] nameKeyword) {
		this.nameKeyword = nameKeyword;
	}
	public String[] getDescriptionKeyword() {
		return descriptionKeyword;
	}
	public void setDescriptionKeyword(String[] descriptionKeyword) {
		this.descriptionKeyword = descriptionKeyword;
	}
	public String getAuthorKeyword() {
		return authorKeyword;
	}
	public void setAuthorKeyword(String authorKeyword) {
		this.authorKeyword = authorKeyword;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public String[] getModelTypeCodes() {
		return modelTypeCodes;
	}
	public void setModelTypeCodes(String[] modelTypeCodes) {
		this.modelTypeCodes = modelTypeCodes;
	}
	public String[] getAccessTypeCodes() {
		return accessTypeCodes;
	}
	public void setAccessTypeCodes(String[] accessTypeCodes) {
		this.accessTypeCodes = accessTypeCodes;
	}
	public String[] getValidationStatusCodes() {
		return validationStatusCodes;
	}
	public void setValidationStatusCodes(String[] validationStatusCodes) {
		this.validationStatusCodes = validationStatusCodes;
	}
	public String[] getTags() {
		return tags;
	}
	public void setTags(String[] tags) {
		this.tags = tags;
	}
	public RestPageRequest getPageRequest() {
		return pageRequest;
	}
	public void setPageRequest(RestPageRequest pageRequest) {
		this.pageRequest = pageRequest;
	}
	public String getSortBy() {
		return sortBy;
	}
	public void setSortBy(String sortBy) {
		this.sortBy = sortBy;
	}
	public String getSortById() {
		return sortById;
	}
	public void setSortById(String sortById) {
		this.sortById = sortById;
	}
	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}
	/**
	 * @param userId the userId to set
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getTaskStatus() {
		return taskStatus;
	}
	public void setTaskStatus(String taskStatus) {
		this.taskStatus = taskStatus;
	}		
}
