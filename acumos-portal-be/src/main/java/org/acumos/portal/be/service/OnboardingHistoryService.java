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
import org.acumos.portal.be.common.PagableResponse;
import org.acumos.portal.be.transport.MLStepResult;
import org.acumos.portal.be.transport.MLTask;
import org.acumos.portal.be.transport.RestPageRequestPortal;

public interface OnboardingHistoryService {
	
	/**
	 * Searches for all Tasks for a User
	 * 
	 * @param pageRequestPortal
	 *                          Page and sort criteria
	 * @param userId
	 *                          UserID
	 * @return List of MLTask, which may be empty.
	 */
	
	public PagableResponse<List<MLTask>> getTasks(RestPageRequestPortal pageRequestPortal , String userId);
	
	/**
	 * Searches for all StepResults for a User
	 * 
	 * @param taskId
	 *                          TaskID
	 * @return List of MLStepResult, which may be empty.
	 */
	
	public List<MLStepResult> getStepResults(Long taskId);
	
}
