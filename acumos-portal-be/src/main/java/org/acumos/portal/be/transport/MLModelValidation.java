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

public class MLModelValidation {

	private String solutionId;
	private String revisionId;
	private String visibility;
	private String userId;
	private List<MLArtifactValidation> artifactValidations;
	private String callbackUrl;
	
	public MLModelValidation() {
		// TODO Auto-generated constructor stub
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

	/**
	 * @return the artifactValidations
	 */
	public List<MLArtifactValidation> getArtifactValidations() {
		return artifactValidations;
	}

	/**
	 * @param artifactValidations the artifactValidations to set
	 */
	public void setArtifactValidations(List<MLArtifactValidation> artifactValidations) {
		this.artifactValidations = artifactValidations;
	}

	/**
	 * @return the callbackUrl
	 */
	public String getCallbackUrl() {
		return callbackUrl;
	}

	/**
	 * @param callbackUrl the callbackUrl to set
	 */
	public void setCallbackUrl(String callbackUrl) {
		this.callbackUrl = callbackUrl;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MLModelValidation [" + (solutionId != null ? "solutionId=" + solutionId + ", " : "")
				+ (revisionId != null ? "revisionId=" + revisionId + ", " : "")
				+ (visibility != null ? "visibility=" + visibility + ", " : "")
				+ (userId != null ? "userId=" + userId + ", " : "")
				+ (artifactValidations != null ? "artifactValidations=" + artifactValidations + ", " : "")
				+ (callbackUrl != null ? "callbackUrl=" + callbackUrl : "") + "]";
	}
}
