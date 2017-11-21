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

public class MLArtifactValidationStatus {

	private String artifactTaskId;
	private String artifactId;
	private String status;
	private String validationTaskType;
	
	/**
	 * @return the artifactTaskId
	 */
	public String getArtifactTaskId() {
		return artifactTaskId;
	}
	/**
	 * @param artifactTaskId the artifactTaskId to set
	 */
	public void setArtifactTaskId(String artifactTaskId) {
		this.artifactTaskId = artifactTaskId;
	}
	/**
	 * @return the artifactId
	 */
	public String getArtifactId() {
		return artifactId;
	}
	/**
	 * @param artifactId the artifactId to set
	 */
	public void setArtifactId(String artifactId) {
		this.artifactId = artifactId;
	}
	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	
	/**
	 * @return the validationTaskType
	 */
	public String getValidationTaskType() {
		return validationTaskType;
	}
	/**
	 * @param validationTaskType the validationTaskType to set
	 */
	public void setValidationTaskType(String validationTaskType) {
		this.validationTaskType = validationTaskType;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MLArtifactValidationStatus [artifactTaskId=" + artifactTaskId + ", artifactId=" + artifactId
				+ ", status=" + status + "]";
	}
}
