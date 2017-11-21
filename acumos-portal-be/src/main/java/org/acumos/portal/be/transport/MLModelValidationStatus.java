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

public class MLModelValidationStatus {
	
	private String taskId;
	private String solutionId;
	private String revisionId;
	private String status;
	private String visibility;
	private List<MLArtifactValidationStatus> artifactValidationStatus;
	/**
	 * @return the taskId
	 */
	public String getTaskId() {
		return taskId;
	}
	/**
	 * @param taskId the taskId to set
	 */
	public void setTaskId(String taskId) {
		this.taskId = taskId;
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
	 * @return the visibility
	 */
	public String getVisibility() {
		return visibility;
	}
	/**
	 * @param visibility the visibility to set
	 */
	public void setVisibility(String visibility) {
		this.visibility = visibility;
	}
	/**
	 * @return the artifactValidationStatus
	 */
	public List<MLArtifactValidationStatus> getArtifactValidationStatus() {
		return artifactValidationStatus;
	}
	/**
	 * @param artifactValidationStatus the artifactValidationStatus to set
	 */
	public void setArtifactValidationStatus(List<MLArtifactValidationStatus> artifactValidationStatus) {
		this.artifactValidationStatus = artifactValidationStatus;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MLModelValidationStatus [" + (taskId != null ? "taskId=" + taskId + ", " : "")
				+ (solutionId != null ? "solutionId=" + solutionId + ", " : "")
				+ (revisionId != null ? "revisionId=" + revisionId + ", " : "")
				+ (status != null ? "status=" + status + ", " : "")
				+ (visibility != null ? "visibility=" + visibility + ", " : "")
				+ (artifactValidationStatus != null ? "artifactValidationStatus=" + artifactValidationStatus : "")
				+ "]";
	}
	
	
	
}
