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

public class MLCatalog extends MLPCatalog {
	
	private long solutionCount;
	private boolean favorite;
	
	/* default constructor */
	public MLCatalog() {}
	
	public MLCatalog(MLPCatalog that) {
		super(that);
	}

	public long getSolutionCount() {
		return solutionCount;
	}

	public void setSolutionCount(long solutionCount) {
		this.solutionCount = solutionCount;
	}

	public boolean isFavorite() {
		return favorite;
	}

	public void setFavorite(boolean favorite) {
		this.favorite = favorite;
	}

	@Override
	public String toString() {
		return "MLCatalog [accessTypeCode=" + getAccessTypeCode()
				+ ", selfPublish=" + isSelfPublish() + ", catalogId=" + getCatalogId() + ", name="
				+ getName() + ", description=" + getDescription() + ", origin=" + getOrigin()
				+ ", publisher=" + getPublisher() + ", url=" + getUrl() + ", solutionCount=" + solutionCount + ", created=" + getCreated() + ", favorite=" + favorite + ", modified="
				+ getModified() + "]";
	}
	
	public MLPCatalog toMLPCatalog() {
		MLPCatalog that = new MLPCatalog();
		that.setAccessTypeCode(getAccessTypeCode());
		that.setSelfPublish(isSelfPublish());
		that.setCatalogId(getCatalogId());
		that.setName(getName());
		that.setDescription(getDescription());
		that.setOrigin(getOrigin());
		that.setPublisher(getPublisher());
		that.setUrl(getUrl());
		that.setCreated(getCreated());
		that.setModified(getModified());
		return that;
	}
	
}
