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
import org.acumos.cds.domain.MLPStepResult;
import org.acumos.cds.transport.RestPageRequest;
import org.acumos.cds.transport.RestPageResponse;
import org.acumos.portal.be.service.MessagingService;
import org.acumos.portal.be.transport.MLStepResult;
import org.acumos.portal.be.util.EELFLoggerDelegate;
import org.acumos.portal.be.util.JsonUtils;
import org.acumos.portal.be.util.PortalUtils;
import org.springframework.stereotype.Service;

@Service
public class MessagingServiceImpl extends AbstractServiceImpl implements MessagingService{

	private static final EELFLoggerDelegate log = EELFLoggerDelegate.getLogger(MarketPlaceCatalogServiceImpl.class);

	@Override
	public List<MLStepResult> callOnBoardingStatusList(String userId, String trackingId) {

		List<MLStepResult> messageStatus = new ArrayList<>();
		log.debug(EELFLoggerDelegate.debugLogger, "callOnBoardingStatus");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		
		RestPageRequest pageRequest = new RestPageRequest();
		Map<String,Object> queryParam = new HashMap<String, Object>();
		queryParam.put("trackingId", trackingId);
		//To fetch the step results while on-boarding solution. By default CDS sends 20 records. Where as convert to onap produces more that 20 step results
		// Setting a random value fo 200 to fetch all the step results
		pageRequest.setPage(0);
		pageRequest.setSize(200);
		RestPageResponse<MLPStepResult> pageResponse = dataServiceRestClient.searchStepResults(queryParam, false, pageRequest);
		
		for(int i=0; i< pageResponse.getContent().size(); i++){
			messageStatus.add(PortalUtils.convertToMLStepResult(pageResponse.getContent().get(i)));
		}

		return messageStatus;
	}
	
	@Override
	public MLPStepResult createStepResult(MLPStepResult stepResult) {
		log.debug(EELFLoggerDelegate.debugLogger, "createStepResult : "+ JsonUtils.serializer().toPrettyString(stepResult));
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

	/*@Override
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
	}*/
	
	@Override
	public List<MLPStepResult> findStepresultBySolutionId(String solutionId, String revisionId) {
		log.debug(EELFLoggerDelegate.debugLogger, "findStepresultBySolutionId ={}", solutionId);
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		Map<String, Object> queryParams = new HashMap<>();
		//queryParams.put("solutionId", solutionId);
		RestPageRequest pageRequest = new RestPageRequest();
		pageRequest.setPage(0);
		pageRequest.setSize(100);
		queryParams.put("solutionId", solutionId);
		queryParams.put("revisionId", revisionId);
		RestPageResponse<MLPStepResult> stepResultList = dataServiceRestClient.searchStepResults(queryParams, false,
				pageRequest);
		List<MLPStepResult> mlpStepResultList = stepResultList.getContent();
		return mlpStepResultList;
	}
}
