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
package org.acumos.portal.be.service;

import java.util.List;
import java.util.Map;

import org.acumos.cds.domain.MLPRightToUse;
import org.acumos.cds.domain.MLPSolution;
import org.acumos.cds.domain.MLPUser;
import org.acumos.cds.transport.RestPageRequest;
import org.acumos.cds.transport.RestPageResponse;
import org.acumos.licensemanager.exceptions.RightToUseException;
import org.acumos.portal.be.common.JsonRequest;
import org.acumos.portal.be.common.exception.AcumosServiceException;
import org.acumos.portal.be.transport.RtuUser;
import org.acumos.portal.be.transport.User;

public interface LicensingService {
	
	MLPSolution getMLPSolutions(long rtuId) throws AcumosServiceException;
	
	RestPageResponse<MLPSolution> getMLPSolutionBySolutionName(Map<String, Object> solutoinNameParameter, boolean flag, RestPageRequest restPageRequest)  throws AcumosServiceException;
	
	List<MLPUser> getMLPUsersAssociatedWithRtuId(long rtuId) throws AcumosServiceException;
	
	List<RtuUser> getAllActiveUsers() throws AcumosServiceException;
	
	List<MLPRightToUse> getRtusByReference(String rtuReferenceId) throws AcumosServiceException;

	List<MLPRightToUse> createRtuUser(String rtuId, String solutionId, List<String> userList) throws Exception;
}
