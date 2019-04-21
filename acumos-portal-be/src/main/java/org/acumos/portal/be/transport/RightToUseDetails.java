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

/**
 * 
 */
package org.acumos.portal.be.transport;

import java.util.List;

import org.acumos.cds.domain.MLPSolution;
import org.acumos.cds.transport.RestPageResponse;
  
/**
 *	RightToUseDetails Class to hold RightToUse Details
 *  
 *  Author: Vasudeva Rao Kallepalli
 */
public class RightToUseDetails {

	/**
	 * 
	 */
	private List<MLPSolution> mlpSolutionAssociatedWithRtuId;
	private List<RtuUser> rtuUsers;
	private RestPageResponse<MLPSolution> solutionsByName;

	/**
	 * 
	 */
	public RightToUseDetails() {
		// TODO Auto-generated constructor stub
	}

	public List<MLPSolution> getMlpSolutionAssociatedWithRtuId() {
		return mlpSolutionAssociatedWithRtuId;
	}

	public void setMlpSolutionAssociatedWithRtuId(List<MLPSolution> mlpSolutionAssociatedWithRtuId) {
		this.mlpSolutionAssociatedWithRtuId = mlpSolutionAssociatedWithRtuId;
	}

	public List<RtuUser> getRtuUsers() {
		return rtuUsers;
	}

	public void setRtuUsers(List<RtuUser> rtuUsers) {
		this.rtuUsers = rtuUsers;
	}

	public RestPageResponse<MLPSolution> getSolutionsByName() {
		return solutionsByName;
	}

	public void setSolutionsByName(RestPageResponse<MLPSolution> solutionsByName) {
		this.solutionsByName = solutionsByName;
	}

	
	
}
