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

import org.acumos.cds.domain.MLPCatalog;

public class MLNewPeerSub {
	
	private String ownerId;
	private Long refreshInterval;
	private MLPCatalog catalog;
	
	public String getOwnerId() {
		return ownerId;
	}
	
	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}
	
	public Long getRefreshInterval() {
		return refreshInterval;
	}
	
	public void setRefreshInterval(Long refreshInterval) {
		this.refreshInterval = refreshInterval;
	}
	
	public MLPCatalog getCatalog() {
		return catalog;
	}
	
	public void setCatalog(MLPCatalog catalog) {
		this.catalog = catalog;
	}

	@Override
	public String toString() {
		return "MLNewPeerSub [ownerId=" + ownerId + ", refreshInterval=" + refreshInterval + ", catalog=" + catalog
				+ "]";
	}
	
}
