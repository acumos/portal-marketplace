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


package org.acumos.portal.be.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.acumos.cds.domain.MLPSolution;
import org.acumos.cds.domain.MLPSolutionRevision;
import org.acumos.cds.transport.RestPageResponse;
import org.acumos.portal.be.common.JsonRequest;
import org.acumos.portal.be.common.RestPageRequestBE;
import org.acumos.portal.be.common.exception.AcumosServiceException;

public interface OnboardingDockerService {

	public Map<String, String> getToolkitTypeDetails();
	
	public String getToolkitTypeCode(String toolkitTypeName);
	 
	public RestPageResponse<MLPSolution> getRelatedSolution(JsonRequest<RestPageRequestBE> restPageReq)throws AcumosServiceException;

	public String getLatestArtifactsUrl(List<MLPSolutionRevision> solutionRevisions) throws AcumosServiceException;

}
