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

import java.util.HashMap;
import java.util.Map;

import org.acumos.cds.domain.MLPCatalog_;
import org.acumos.cds.transport.RestPageRequest;
import org.acumos.portal.be.util.PortalUtils;

public class CatalogSearchRequest {
	
	private String accessTypeCode;
	private String selfPublish;
	private String description;
	private String name;
	private String origin;
	private String publisher;
	private String url;
	private boolean isOr;
	private RestPageRequest pageRequest;
	
	/* Default constructor */
	public CatalogSearchRequest() {}
	
	public String getAccessTypeCode() {
		return accessTypeCode;
	}

	public void setAccessTypeCode(String accessTypeCode) {
		this.accessTypeCode = accessTypeCode;
	}

	public String getSelfPublish() {
		return selfPublish;
	}

	public void setSelfPublish(String selfPublish) {
		this.selfPublish = selfPublish;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public boolean isOr() {
		return isOr;
	}

	public void setOr(boolean isOr) {
		this.isOr = isOr;
	}

	public RestPageRequest getPageRequest() {
		return pageRequest;
	}

	public void setPageRequest(RestPageRequest pageRequest) {
		this.pageRequest = pageRequest;
	}

	@Override
	public String toString() {
		return "CatalogRestPageRequest [accessTypeCode=" + accessTypeCode + ", selfPublish=" + selfPublish
				+ ", description=" + description + ", name=" + name + ", origin=" + origin + ", publisher=" + publisher
				+ ", url=" + url + ", isOr=" + isOr + ", pageRequest=" + pageRequest + "]";
	}
	
	public Map<String, Object> paramsMap() {
		HashMap<String, Object> map = new HashMap<>();
		if (!PortalUtils.isEmptyOrNullString(accessTypeCode))
			map.put(MLPCatalog_.ACCESS_TYPE_CODE, accessTypeCode);
		if (!PortalUtils.isEmptyOrNullString(selfPublish))
			map.put(MLPCatalog_.SELF_PUBLISH, selfPublish);
		if (!PortalUtils.isEmptyOrNullString(description))
			map.put(MLPCatalog_.DESCRIPTION, description);
		if (!PortalUtils.isEmptyOrNullString(name))
			map.put(MLPCatalog_.NAME, name);
		if (!PortalUtils.isEmptyOrNullString(origin))
			map.put(MLPCatalog_.ORIGIN, origin);
		if (!PortalUtils.isEmptyOrNullString(publisher))
			map.put(MLPCatalog_.PUBLISHER, publisher);
		if (!PortalUtils.isEmptyOrNullString(url))
			map.put(MLPCatalog_.URL, url);
		return map;
	}
}
