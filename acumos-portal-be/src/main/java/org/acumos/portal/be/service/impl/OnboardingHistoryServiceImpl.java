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

package org.acumos.portal.be.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.acumos.cds.client.ICommonDataServiceRestClient;
import org.acumos.cds.domain.MLPSolution;
import org.acumos.cds.domain.MLPTask;
import org.acumos.cds.domain.MLPTaskStepResult;
import org.acumos.cds.transport.RestPageRequest;
import org.acumos.cds.transport.RestPageResponse;
import org.acumos.portal.be.common.PagableResponse;
import org.acumos.portal.be.service.OnboardingHistoryService;
import org.acumos.portal.be.transport.MLSolution;
import org.acumos.portal.be.transport.MLStepResult;
import org.acumos.portal.be.transport.MLTask;
import org.acumos.portal.be.transport.RestPageRequestPortal;
import org.acumos.portal.be.util.EELFLoggerDelegate;
import org.acumos.portal.be.util.PortalUtils;
import org.springframework.stereotype.Service;

@Service
public class OnboardingHistoryServiceImpl extends AbstractServiceImpl implements OnboardingHistoryService {

	private static final EELFLoggerDelegate log = EELFLoggerDelegate.getLogger(OnboardingHistoryServiceImpl.class);

	@Override
	public PagableResponse<List<MLTask>> getTasks(RestPageRequestPortal pageRequestPortal, String userId) {
		log.debug(EELFLoggerDelegate.debugLogger, "getTasks");
		PagableResponse<List<MLTask>> response = new PagableResponse<>();
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		RestPageResponse<MLPTask> pageResponse = findTasksByUserId(pageRequestPortal, userId);
		List<MLTask> mlTaskList = new ArrayList<MLTask>();
		MLSolution mlSolution = new MLSolution();

		for (MLPTask task : pageResponse) {
			if (task.getSolutionId() != null) {
				MLPSolution mlpSolution = dataServiceRestClient.getSolution(task.getSolutionId());
				mlSolution = PortalUtils.convertToMLSolution(mlpSolution);
			}
			MLTask mlTask = PortalUtils.convertToMLTask(task);
			mlTask.setModelName(mlSolution.getName());
			mlTaskList.add(mlTask);
		}

		Collections.sort(mlTaskList, Comparator.comparing(MLTask::getCreatedtDate).reversed());

		response.setResponseBody(mlTaskList);
		response.setSize(pageResponse.getSize());
		response.setTotalElements(pageResponse.getTotalElements());
		response.setTotalPages(pageResponse.getTotalPages());
		return response;
	}

	@Override
	public List<MLStepResult> getStepResults(Long taskId) {
		log.debug(EELFLoggerDelegate.debugLogger, "getStepResults");
		List<MLStepResult> mlStepResultList = new ArrayList<MLStepResult>();
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		List<MLPTaskStepResult> results = dataServiceRestClient.getTaskStepResults(taskId);

		for (MLPTaskStepResult mlPTaskStepResult : results) {
			MLStepResult mlStepResult = PortalUtils.convertToMLStepResults(mlPTaskStepResult);
			mlStepResultList.add(mlStepResult);
		}

		return mlStepResultList;
	}

	private RestPageResponse<MLPTask> findTasksByUserId(RestPageRequestPortal pageReqPortal, String userId) {
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		Map<String, Object> queryParam = new HashMap<String, Object>();
		queryParam.put("userId", userId);
		if (pageReqPortal.getTaskStatus() != null) {
			Object statusCode = pageReqPortal.getTaskStatus();
			queryParam.put("statusCode", statusCode);
		}
		RestPageResponse<MLPTask> pageResponse = dataServiceRestClient.searchTasks(queryParam, false,
				pageReqPortal.getPageRequest());
		return pageResponse;
	}
}