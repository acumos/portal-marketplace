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

import org.acumos.cds.domain.MLPStepResult;
import org.acumos.cds.domain.MLPStepStatus;
import org.acumos.cds.domain.MLPStepType;
import org.acumos.portal.be.transport.MLStepResult;

public interface MessagingService {

	List<MLStepResult> callOnBoardingStatusList(String userId, String trackingId);
	
	MLPStepResult createStepResult(MLPStepResult stepResult);

	void updateStepResult(MLPStepResult stepResult);

	void deleteStepResult(Long stepResultId);

	List<MLPStepStatus> getStepStatuses();

	List<MLPStepType> getStepTypes();

	List<MLPStepResult> findStepresultBySolutionId(String solutionId, String revisionId);
}
