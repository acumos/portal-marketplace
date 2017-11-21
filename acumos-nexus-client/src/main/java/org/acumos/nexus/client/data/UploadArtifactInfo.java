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

package org.acumos.nexus.client.data;

/**
 * Class to hold information about the Uploaded Artifact.
 *
 */
public class UploadArtifactInfo {
	private String groupId;
	private String artifactId;
	private String version;
	private String packaging;
	private String artifactMvnPath;
	private long contentlength;
	
	public UploadArtifactInfo(String groupId, String artifactId, String version, String packaging,
			String artifactMvnPath, long contentlength) {
		super();
		this.groupId = groupId;
		this.artifactId = artifactId;
		this.version = version;
		this.packaging = packaging;
		this.artifactMvnPath = artifactMvnPath;
		this.contentlength = contentlength;
	}
	/**
	 * @return the groupId
	 */
	public String getGroupId() {
		return groupId;
	}
	/**
	 * @param groupId the groupId to set
	 */
	public void setGroupId(String groupId) {
		this.groupId = groupId;
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
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}
	/**
	 * @param version the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}
	/**
	 * @return the packaging
	 */
	public String getPackaging() {
		return packaging;
	}
	/**
	 * @param packaging the packaging to set
	 */
	public void setPackaging(String packaging) {
		this.packaging = packaging;
	}
	/**
	 * @return the artifactMvnPath
	 */
	public String getArtifactMvnPath() {
		return artifactMvnPath;
	}
	/**
	 * @param artifactMvnPath the artifactMvnPath to set
	 */
	public void setArtifactMvnPath(String artifactMvnPath) {
		this.artifactMvnPath = artifactMvnPath;
	}
	/**
	 * @return the contentlength
	 */
	public long getContentlength() {
		return this.contentlength;
	}
	/**
	 * @param contentlength the contentlength to set
	 */
	public void setContentlength(long contentlength) {
		contentlength = contentlength;
	}
	
	
}
