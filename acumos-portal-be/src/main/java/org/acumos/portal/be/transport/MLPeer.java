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

/**
 *	MLPeer Class to hold the Solutions Peer Information
 */

public class MLPeer {
	
	private String apiUrl;
	private String contact1;
	private Instant created;
	private String description;
	private boolean local;
	private Instant modified;
	private String name;
	private String peerId;
	private boolean self;
	private String statusCode;
	private String subjectName;
	private String webUrl;
	/**
	 * @return the apiUrl
	 */
	public String getApiUrl() {
		return apiUrl;
	}
	/**
	 * @param apiUrl the apiUrl to set
	 */
	public void setApiUrl(String apiUrl) {
		this.apiUrl = apiUrl;
	}
	/**
	 * @return the contact1
	 */
	public String getContact1() {
		return contact1;
	}
	/**
	 * @param contact1 the contact1 to set
	 */
	public void setContact1(String contact1) {
		this.contact1 = contact1;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @return the local
	 */
	public boolean getLocal() {
		return local;
	}
	/**
	 * @param local the local to set
	 */
	public void setLocal(boolean local) {
		this.local = local;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the peerId
	 */
	public String getPeerId() {
		return peerId;
	}
	/**
	 * @param peerId the peerId to set
	 */
	public void setPeerId(String peerId) {
		this.peerId = peerId;
	}
	/**
	 * @return the statusCode
	 */
	public String getStatusCode() {
		return statusCode;
	}
	/**
	 * @param statusCode the statusCode to set
	 */
	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}
	/**
	 * @return the subjectName
	 */
	public String getSubjectName() {
		return subjectName;
	}
	/**
	 * @param subjectName the subjectName to set
	 */
	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}
	/**
	 * @return the created
	 */
	public Instant getCreated() {
		return created;
	}
	/**
	 * @param created the created to set
	 */
	public void setCreated(Instant created) {
		this.created = created;
	}
	/**
	 * @return the modified
	 */
	public Instant getModified() {
		return modified;
	}
	/**
	 * @param modified the modified to set
	 */
	public void setModified(Instant modified) {
		this.modified = modified;
	}
	/**
	 * @return the self
	 */
	public boolean isSelf() {
		return self;
	}
	/**
	 * @param self the self to set
	 */
	public void setSelf(boolean self) {
		this.self = self;
	}
	/**
	 * @return the webUrl
	 */
	public String getWebUrl() {
		return webUrl;
	}
	/**
	 * @param webUrl the webUrl to set
	 */
	public void setWebUrl(String webUrl) {
		this.webUrl = webUrl;
	}
	
	
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MLPeer [apiUrl=");
		builder.append(apiUrl);
		builder.append(", contact1=");
		builder.append(contact1);
		builder.append(", created=");
		builder.append(created);
		builder.append(", description=");
		builder.append(description);
		builder.append(", local=");
		builder.append(local);
		builder.append(", modified=");
		builder.append(modified);
		builder.append(", name=");
		builder.append(name);
		builder.append(", peerId=");
		builder.append(peerId);
		builder.append(", self=");
		builder.append(self);
		builder.append(", statusCode=");
		builder.append(statusCode);
		builder.append(", subjectName=");
		builder.append(subjectName);
		builder.append(", webUrl=");
		builder.append(webUrl);
		builder.append("]");
		return builder.toString();
	}


}
