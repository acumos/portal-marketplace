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

import org.acumos.cds.client.CommonDataServiceRestClientImpl;
import org.acumos.cds.client.ICommonDataServiceRestClient;
import org.acumos.cds.domain.MLPSolutionValidation;
import org.acumos.cds.domain.MLPStepResult;
import org.acumos.cds.domain.MLPStepStatus;
import org.acumos.cds.domain.MLPStepType;
import org.acumos.cds.transport.RestPageRequest;
import org.acumos.cds.transport.RestPageResponse;
import org.acumos.portal.be.service.MessagingService;
import org.acumos.portal.be.transport.MLStepResult;
import org.acumos.portal.be.util.EELFLoggerDelegate;
import org.acumos.portal.be.util.PortalUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
public class MessagingServiceImpl implements MessagingService{

	private static final EELFLoggerDelegate log = EELFLoggerDelegate.getLogger(MarketPlaceCatalogServiceImpl.class);

	@Autowired
	private Environment env;

	private ICommonDataServiceRestClient getClient() {
		ICommonDataServiceRestClient client = new CommonDataServiceRestClientImpl(env.getProperty("cdms.client.url"),
				env.getProperty("cdms.client.username"), env.getProperty("cdms.client.password"));
		return client;
	}
	
	/*@Override
	public MLStepResult callOnBoardingStatus(String userId, String trackingId) {

		MLStepResult messageStatus = new MLStepResult();
		log.debug(EELFLoggerDelegate.debugLogger, "callOnBoardingStatus");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		
		RestPageRequest pageRequest = new RestPageRequest();
		RestPageResponse<MLPStepResult> pageResponse = dataServiceRestClient.getStepResults(pageRequest);
		//messageStatus = pageResponse.set	 
		
		for(int i=0; i< pageResponse.getContent().size(); i++){
			messageStatus = PortalUtils.convertToMLStepResult(pageResponse.getContent().get(i));
			
		}
		

		return messageStatus;
	}	*/
	
	@Override
	public List<MLStepResult> callOnBoardingStatusList(String userId, String trackingId) {

		List<MLStepResult> messageStatus = new ArrayList<>();
		log.debug(EELFLoggerDelegate.debugLogger, "callOnBoardingStatus");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		
		RestPageRequest pageRequest = new RestPageRequest();
		Map<String,Object> queryParam = new HashMap<String, Object>();
		queryParam.put("trackingId", trackingId);
		RestPageResponse<MLPStepResult> pageResponse = dataServiceRestClient.searchStepResults(queryParam, false, pageRequest);
		
		for(int i=0; i< pageResponse.getContent().size(); i++){
			messageStatus.add(PortalUtils.convertToMLStepResult(pageResponse.getContent().get(i)));
		}
		
		/*List<MLStepResult> messageStatusFilter = new ArrayList<>();
		for(MLStepResult mlStepResult : messageStatus){
			if(mlStepResult.getTrackingId()!=null && mlStepResult.getTrackingId().equals(trackingId)){
				messageStatusFilter.add(mlStepResult);
			}
			
			
		}*/
		return messageStatus;
	}
	
	@Override
	public MLPStepResult createStepResult(MLPStepResult stepResult) {
		log.debug(EELFLoggerDelegate.debugLogger, "createStepResult`");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		MLPStepResult result = dataServiceRestClient.createStepResult(stepResult);	
		return result;
	}

	@Override
	public void updateStepResult(MLPStepResult stepResult) {
		log.debug(EELFLoggerDelegate.debugLogger, "updateStepResult`");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		dataServiceRestClient.updateStepResult(stepResult);	
	}

	@Override
	public void deleteStepResult(Long stepResultId) {
		log.debug(EELFLoggerDelegate.debugLogger, "deleteStepResult`");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		dataServiceRestClient.deleteStepResult(stepResultId);	
	}

	@Override
	public List<MLPStepStatus> getStepStatuses() {
		log.debug(EELFLoggerDelegate.debugLogger, "createStepResult`");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		List<MLPStepStatus> stepStatusesList  = dataServiceRestClient.getStepStatuses();	
		return stepStatusesList;
	}

	@Override
	public List<MLPStepType> getStepTypes() {
		log.debug(EELFLoggerDelegate.debugLogger, "createStepResult`");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		List<MLPStepType> stepStatusesList  = dataServiceRestClient.getStepTypes();	
		return stepStatusesList;
	}
	
	@Override
	public List<MLPStepResult> findStepresultBySolutionId(String solutionId, String revisionId) {
		log.debug(EELFLoggerDelegate.debugLogger, "findStepresultBySolutionId ={}", solutionId);
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		Map<String, Object> queryParams = new HashMap<>();
		//queryParams.put("solutionId", solutionId);
		RestPageRequest pageRequest = new RestPageRequest();
		pageRequest.setPage(0);
		pageRequest.setSize(100);
		List<MLPSolutionValidation> mlPSolutionValidations =  dataServiceRestClient.getSolutionValidations(solutionId, revisionId);
		MLPSolutionValidation mlPSolutionValidation = mlPSolutionValidations.get(0);
		String trackingId = mlPSolutionValidation.getTaskId();
		queryParams.put("solutionId", solutionId);
		queryParams.put("trackingId", trackingId);
		queryParams.put("revisionId", revisionId);
		RestPageResponse<MLPStepResult> stepResultList = dataServiceRestClient.searchStepResults(queryParams, false,
				pageRequest);
		List<MLPStepResult> mlpStepResultList = stepResultList.getContent();
		/*
		 * for(MLPStepResult mlpStepResult : mlpStepResultList) {
		 * if(mlpStepResult != null) {
		 * if(!PortalUtils.isEmptyOrNullString(mlpStepResult.getSolutionId()) &&
		 * mlpStepResult.getSolutionId().equalsIgnoreCase(solutionId)) {
		 * stepResult = mlpStepResult; break; } } }
		 */
		return mlpStepResultList;
	}
}
