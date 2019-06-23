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

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
import org.acumos.portal.be.util.PortalUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import me.xdrop.fuzzywuzzy.FuzzySearch;

@Service
public class OnboardingHistoryServiceImpl extends AbstractServiceImpl implements OnboardingHistoryService {

	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Override
	public PagableResponse<List<MLTask>> getTasks(RestPageRequestPortal pageRequestPortal, String userId) {
		log.debug("getTasks");
		String searchTerm = pageRequestPortal.getPageRequest().getFieldToDirectionMap().get("filter");
		pageRequestPortal.getPageRequest().getFieldToDirectionMap().remove("filter");
		PagableResponse<List<MLTask>> response = new PagableResponse<>();
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		RestPageResponse<MLPTask> pageResponse = findTasksByUserId(pageRequestPortal, userId);
		List<MLTask> mlTaskList = new ArrayList<MLTask>();
		MLSolution mlSolution = null;

		for (MLPTask task : pageResponse) {
			mlSolution = new MLSolution();
			if (task.getSolutionId() != null) {
				MLPSolution mlpSolution = dataServiceRestClient.getSolution(task.getSolutionId());
				mlSolution = PortalUtils.convertToMLSolution(mlpSolution);
			}
			MLTask mlTask = PortalUtils.convertToMLTask(task);
			mlTask.setModelName(mlSolution.getName());
			mlTaskList.add(mlTask);
		}

		if(searchTerm != null && searchTerm.length() >0) {
			List<RelevantMLTask> rs = mlTaskList.stream()
				.collect(Collectors.mapping(
					p -> new RelevantMLTask(p, meanScore (searchTerm, p.getModelName())),
					Collectors.toList()));
			Comparator<RelevantMLTask> taskScoreComparator
				      = Comparator.comparingDouble(RelevantMLTask::getScore);
			Collections.sort(rs, taskScoreComparator.reversed());
			mlTaskList = new ArrayList<MLTask>(rs);
					
		} else {
		  Collections.sort(mlTaskList, Comparator.comparing(MLTask::getCreatedtDate).reversed());
		}

		response.setResponseBody(mlTaskList);
		response.setSize(pageResponse.getSize());
		response.setTotalElements(pageResponse.getTotalElements());
		response.setTotalPages(pageResponse.getTotalPages());
		return response;
	}
	
	private double meanScore (String searchName, String name) {
	    return FuzzySearch.ratio(searchName,name);
	}
		
	class RelevantMLTask extends MLTask {

		
		RelevantMLTask(MLTask task, double score) {
			super.setTaskId(task.getTaskId());
			super.setTaskCode(task.getTaskCode());
			super.setStatusCode(task.getStatusCode());
			super.setName(task.getName());
			super.setSolutionId(task.getSolutionId());
			super.setRevisionId(task.getRevisionId());
			super.setCreatedtDate(task.getCreatedtDate());
			super.setModifiedDate(task.getModifiedDate());
			super.setTrackingId(task.getTrackingId());
			super.setUserId(task.getUserId());
			super.setModelName(task.getModelName());
			this.score = score;
		}
		private double score;
		
		public double getScore() {
			return this.score;
		}
	}

	@Override
	public List<MLStepResult> getStepResults(Long taskId) {
		log.debug("getStepResults");
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

	@Override
	public MLTask getMSTaskStatus(String solutionId, String revisionId, String userId) {
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		MLTask mlTask = null;
		Map<String, Object> searchparam = new HashMap<String, Object>();
		searchparam.put("solutionId", solutionId);
		searchparam.put("revisionId", revisionId);
		searchparam.put("userId", userId);
		searchparam.put("taskCode", "MS");

		Map<String, String> queryParameters = new HashMap<>();
		queryParameters.put("created", "DESC");

		RestPageResponse<MLPTask> pageResponse = dataServiceRestClient.searchTasks(searchparam, false,
				new RestPageRequest(0, 10000, queryParameters));
		if (pageResponse.getContent().size() > 0) {
			MLPTask mlpTask = pageResponse.getContent().get(0);
			mlTask = PortalUtils.convertToMLTask(mlpTask);
		}
		return mlTask;
	}
}