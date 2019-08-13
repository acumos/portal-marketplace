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

import java.util.List;

public class ElkRestoreSnapshotRequest {

//	@ApiModelProperty(required = true, value = "ElasticStack repository name", example = "logstash")
	private String repositoryName;

//	@ApiModelProperty(value = "RestoreSnapshot is required")
	private List<RestoreSnapshot> restoreSnapshots;

//	@ApiModelProperty(required = true, value = "Value numeric values, ideal value is between 1 to 3", example = "1")
	private String nodeTimeout;

	public String getRepositoryName() {
		return repositoryName;
	}

	public void setRepositoryName(String repositoryName) {
		this.repositoryName = repositoryName;
	}

	public List<RestoreSnapshot> getRestoreSnapshots() {
		return restoreSnapshots;
	}

	public void setRestoreSnapshots(List<RestoreSnapshot> restoreSnapshots) {
		this.restoreSnapshots = restoreSnapshots;
	}

	public String getNodeTimeout() {
		return nodeTimeout;
	}

	public void setNodeTimeout(String nodeTimeout) {
		this.nodeTimeout = nodeTimeout;
	}

}
