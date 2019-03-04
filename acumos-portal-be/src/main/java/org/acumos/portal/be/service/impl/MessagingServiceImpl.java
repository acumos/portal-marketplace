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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.acumos.cds.client.ICommonDataServiceRestClient;
import org.acumos.cds.domain.MLPTask;
import org.acumos.cds.domain.MLPTaskStepResult;
import org.acumos.cds.transport.RestPageRequest;
import org.acumos.cds.transport.RestPageResponse;
import org.acumos.portal.be.service.MessagingService;
import org.acumos.portal.be.transport.MLStepResult;
import org.acumos.portal.be.util.EELFLoggerDelegate;
import org.acumos.portal.be.util.JsonUtils;
import org.acumos.portal.be.util.PortalUtils;
import org.springframework.stereotype.Service;

@Service
public class MessagingServiceImpl extends AbstractServiceImpl implements MessagingService {

	private static final EELFLoggerDelegate log = EELFLoggerDelegate.getLogger(MessagingServiceImpl.class);

	@Override
	public List<MLStepResult> callOnBoardingStatusList(String userId, String trackingId) {

		List<MLStepResult> messageStatus = new ArrayList<>();
		log.debug(EELFLoggerDelegate.debugLogger, "callOnBoardingStatus");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		List<MLPTask> tasks = findTasksByTrackingId(trackingId);
		if (!PortalUtils.isEmptyList(tasks)) {
			for (MLPTask task : tasks) {
				log.debug(EELFLoggerDelegate.debugLogger, "callOnBoardingStatus:TaskId=" + task.getTaskId());
				for (MLPTaskStepResult step : dataServiceRestClient.getTaskStepResults(task.getTaskId())) {
					messageStatus.add(PortalUtils.convertToMLStepResult(task, step));
				}
			}
			log.debug(EELFLoggerDelegate.debugLogger, "callOnBoardingStatus:messageStatus.length=" + messageStatus.size());
		}
		return messageStatus;
	}

	@Override
	public MLPTask createTask(MLPTask task) {
		log.debug(EELFLoggerDelegate.debugLogger, "createTask : " + JsonUtils.serializer().toPrettyString(task));
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		return dataServiceRestClient.createTask(task);
	}

	@Override
	public void updateTask(MLPTask task) {
		log.debug(EELFLoggerDelegate.debugLogger, "updateTask ");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		dataServiceRestClient.updateTask(task);
	}

	@Override
	public void deleteTask(long taskId) {
		log.debug(EELFLoggerDelegate.debugLogger, "deleteTask ");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		dataServiceRestClient.deleteTask(taskId);
	}

	@Override
	public List<MLPTask> findTasksByTrackingId(String trackingId) {
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		List<MLPTask> tasks = null;

		RestPageRequest pageRequest = new RestPageRequest();
		Map<String, Object> queryParam = new HashMap<String, Object>();
		queryParam.put("trackingId", trackingId);
		// To fetch the step results while on-boarding solution. By default CDS
		// sends 20 records. Where as convert to ONAP produces more that 20 step
		// results
		// Setting a random value of 200 to fetch all the step results
		pageRequest.setPage(0);
		pageRequest.setSize(200);
		RestPageResponse<MLPTask> pageResponse = dataServiceRestClient.searchTasks(queryParam, false, pageRequest);
		if (!PortalUtils.isEmptyList(pageResponse.getContent())) {
			tasks = pageResponse.getContent();
		}
		
		if(tasks!=null) {
			log.debug(EELFLoggerDelegate.debugLogger, "callOnBoardingStatus:findTasksByTrackingId() : tasks size=" + tasks.size());
		}

		return tasks;
	}

	@Override
	public MLPTaskStepResult createStepResult(MLPTaskStepResult stepResult) {
		log.debug(EELFLoggerDelegate.debugLogger,
				"createStepResult : " + JsonUtils.serializer().toPrettyString(stepResult));
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		MLPTaskStepResult result = dataServiceRestClient.createTaskStepResult(stepResult);
		return result;
	}

	@Override
	public void updateStepResult(MLPTaskStepResult stepResult) {
		log.debug(EELFLoggerDelegate.debugLogger, "updateStepResult`");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		dataServiceRestClient.updateTaskStepResult(stepResult);
	}

	@Override
	public void deleteStepResult(Long stepResultId) {
		log.debug(EELFLoggerDelegate.debugLogger, "deleteStepResult`");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		dataServiceRestClient.deleteTaskStepResult(stepResultId);
	}

	@Override
	public List<MLPTaskStepResult> findStepresultBySolutionId(String solutionId, String revisionId) {
		log.debug(EELFLoggerDelegate.debugLogger, "findStepresultBySolutionId ={}", solutionId);
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		List<MLPTaskStepResult> mlpStepResultList = new ArrayList<>();
		Map<String, Object> queryParams = new HashMap<>();
		// queryParams.put("solutionId", solutionId);
		RestPageRequest pageRequest = new RestPageRequest();
		pageRequest.setPage(0);
		pageRequest.setSize(100);
		queryParams.put("solutionId", solutionId);
		queryParams.put("revisionId", revisionId);
		RestPageResponse<MLPTask> taskList = dataServiceRestClient.searchTasks(queryParams, false, pageRequest);
		if (!PortalUtils.isEmptyList(taskList.getContent())) {
			for (MLPTask task : taskList.getContent()) {
				mlpStepResultList.addAll(dataServiceRestClient.getTaskStepResults(task.getTaskId()));
			}
		}
		return mlpStepResultList;
	}
}
