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

public class MLRoleFunction {

	public MLRoleFunction() {

	}

	private String roleFunctionId;
	private MLRole mlRole;
	private String name;
	private Instant created;
	private Instant modified;
	public String getRoleFunctionId() {
		return roleFunctionId;
	}
	public void setRoleFunctionId(String roleFunctionId) {
		this.roleFunctionId = roleFunctionId;
	}
	
	public MLRole getMlRole() {
		return mlRole;
	}
	public void setMlRole(MLRole mlRole) {
		this.mlRole = mlRole;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Instant getCreated() {
		return created;
	}
	public void setCreated(Instant created) {
		this.created = created;
	}
	public Instant getModified() {
		return modified;
	}
	public void setModified(Instant modified) {
		this.modified = modified;
	}
	
	
}
