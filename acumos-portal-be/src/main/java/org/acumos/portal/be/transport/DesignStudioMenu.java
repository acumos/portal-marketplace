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

public class DesignStudioMenu {

	private boolean workbenchActive;
	private boolean acucomposeActive;
	private List<DesignStudioBlock> blocks;
	
	public boolean isWorkbenchActive() {
		return workbenchActive;
	}
	
	public void setWorkbenchActive(boolean isWorkbenchActive) {
		this.workbenchActive = isWorkbenchActive;
	}
	
	public boolean isAcucomposeActive() {
		return acucomposeActive;
	}
	
	public void setAcucomposeActive(boolean isAcucomposeActive) {
		this.acucomposeActive = isAcucomposeActive;
	}
	
	public List<DesignStudioBlock> getBlocks() {
		return blocks;
	}
	
	public void setBlocks(List<DesignStudioBlock> blocks) {
		this.blocks = blocks;
	}
}
