/*-
 * ===============LICENSE_START=======================================================
 * Acumos
 * ===================================================================================
 * Copyright (C) 2019 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
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

public class K8DeployRequest {
	String userId;
	String solutionId;
	String revisionId;
	String envId;
	
	public K8DeployRequest(){}

	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}
	/**
	 * @param solutionId the solutionId to set
	 */
	public void setUserId(String userId) {
		this.userId = userId;
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
	 * @param solutionId the solutionId to set
	 */
	public void setRevisionId(String revisionId) {
		this.revisionId = revisionId;
	}
	/**
	 * @return the envId
	 */
	public String getEnvId() {
		return envId;
	}
	/**
	 * @param solutionId the solutionId to set
	 */
	public void setEnvId(String envId) {
		this.envId = envId;
	}
}
