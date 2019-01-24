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

public class MLPublishRequest{
	private long publishRequestId;
	private String solutionId;
	private String revisionId;
	private String requestUserId;
	private String requestUserName;
	private String approverId;
	private String requestStatusCode;
	private String comment;
	private String requestorName;
	private String solutionName;
	private String revisionName;
	private String revisionStatusCode;
	private String revisionStatusName;
	private String requestStatusName;
	private Instant creationDate;
	private Instant lastModifiedDate;
	/**
	 * @return the publishRequestId
	 */
	public long getPublishRequestId() {
		return publishRequestId;
	}
	/**
	 * @param publishRequestId the publishRequestId to set
	 */
	public void setPublishRequestId(long publishRequestId) {
		this.publishRequestId = publishRequestId;
	}
	/**
	 * @return the solutionId
	 */
	public String getSolutionId() {
		return solutionId;
	}
	/**
	 * @param solutionId the solutionId to set
	 */
	public void setSolutionId(String solutionId) {
		this.solutionId = solutionId;
	}
	/**
	 * @return the revisionId
	 */
	public String getRevisionId() {
		return revisionId;
	}
	/**
	 * @param revisionId the revisionId to set
	 */
	public void setRevisionId(String revisionId) {
		this.revisionId = revisionId;
	}
	/**
	 * @return the requestUserId
	 */
	public String getRequestUserId() {
		return requestUserId;
	}
	/**
	 * @param requestUserId the requestUserId to set
	 */
	public void setRequestUserId(String requestUserId) {
		this.requestUserId = requestUserId;
	}
	/**
	 * @return the requestUserName
	 */
	public String getRequestUserName() {
		return requestUserName;
	}
	/**
	 * @param requestUserName the requestUserName to set
	 */
	public void setRequestUserName(String requestUserName) {
		this.requestUserName = requestUserName;
	}
	/**
	 * @return the approverId
	 */
	public String getApproverId() {
		return approverId;
	}
	/**
	 * @param approverId the approverId to set
	 */
	public void setApproverId(String approverId) {
		this.approverId = approverId;
	}
	/**
	 * @return the requestStatusCode
	 */
	public String getRequestStatusCode() {
		return requestStatusCode;
	}
	/**
	 * @param requestStatusCode the requestStatusCode to set
	 */
	public void setRequestStatusCode(String requestStatusCode) {
		this.requestStatusCode = requestStatusCode;
	}
	/**
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}
	/**
	 * @param comment the comment to set
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}
	/**
	 * @return the creationDate
	 */
	public Instant getCreationDate() {
		return creationDate;
	}
	/**
	 * @param creationDate the creationDate to set
	 */
	public void setCreationDate(Instant creationDate) {
		this.creationDate = creationDate;
	}
	/**
	 * @return the lastModifiedDate
	 */
	public Instant getLastModifiedDate() {
		return lastModifiedDate;
	}
	/**
	 * @param lastModifiedDate the lastModifiedDate to set
	 */
	public void setLastModifiedDate(Instant lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}
	/**
	 * @return the requestorName
	 */
	public String getRequestorName() {
		return requestorName;
	}
	/**
	 * @param requestorName the requestorName to set
	 */
	public void setRequestorName(String requestorName) {
		this.requestorName = requestorName;
	}
	/**
	 * @return the solutionName
	 */
	public String getSolutionName() {
		return solutionName;
	}
	/**
	 * @param solutionName the solutionName to set
	 */
	public void setSolutionName(String solutionName) {
		this.solutionName = solutionName;
	}
	/**
	 * @return the revisionName
	 */
	public String getRevisionName() {
		return revisionName;
	}
	/**
	 * @param revisionName the revisionName to set
	 */
	public void setRevisionName(String revisionName) {
		this.revisionName = revisionName;
	}
	/**
	 * @return the revisionStatusCode
	 */
	public String getRevisionStatusCode() {
		return revisionStatusCode;
	}
	/**
	 * @param revisionStatusCode the revisionStatusCode to set
	 */
	public void setRevisionStatusCode(String revisionStatusCode) {
		this.revisionStatusCode = revisionStatusCode;
	}
	/**
	 * @return the revisionStatusName
	 */
	public String getRevisionStatusName() {
		return revisionStatusName;
	}
	/**
	 * @param revisionStatusName the revisionStatusName to set
	 */
	public void setRevisionStatusName(String revisionStatusName) {
		this.revisionStatusName = revisionStatusName;
	}
	/**
	 * @return the requestStatusName
	 */
	public String getRequestStatusName() {
		return requestStatusName;
	}
	/**
	 * @param requestStatusName the requestStatusName to set
	 */
	public void setRequestStatusName(String requestStatusName) {
		this.requestStatusName = requestStatusName;
	}
}