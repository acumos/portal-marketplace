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
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.acumos.cds.CodeNameType;
import org.acumos.cds.client.ICommonDataServiceRestClient;
import org.acumos.cds.domain.MLPArtifact;
import org.acumos.cds.domain.MLPCodeNamePair;
import org.acumos.cds.domain.MLPSolution;
import org.acumos.cds.domain.MLPSolutionRevision;
import org.acumos.cds.domain.MLPUser;
import org.acumos.cds.transport.RestPageRequest;
import org.acumos.cds.transport.RestPageResponse;
import org.acumos.portal.be.common.JsonRequest;
import org.acumos.portal.be.common.RestPageRequestBE;
import org.acumos.portal.be.common.exception.AcumosServiceException;
import org.acumos.portal.be.service.OnboardingDockerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class OnboardingDockerServiceImpl  extends AbstractServiceImpl implements OnboardingDockerService {

	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	public Map<String, String> getToolkitTypeDetails() {
		
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		List<MLPCodeNamePair> typeCodeList = dataServiceRestClient.getCodeNamePairs(CodeNameType.TOOLKIT_TYPE);
		Map<String, String> toolkitTypeDetails = new HashMap<>();
		if (!typeCodeList.isEmpty()) {
			for (MLPCodeNamePair codeNamePair : typeCodeList) {
				toolkitTypeDetails.put(codeNamePair.getName(), codeNamePair.getCode());
			}
		}
		return toolkitTypeDetails;
	}
	
	public String getToolkitTypeCode(String toolkitTypeName) {
		Map<String, String> toolkitTypeDetails = new HashMap<>();
		String typeCode = toolkitTypeDetails.get(toolkitTypeName);
		return typeCode;
	}
	
	@Override
	public RestPageResponse<MLPSolution> getRelatedSolution(JsonRequest<RestPageRequestBE> restPageReqBe)throws AcumosServiceException {
		// TODO Auto-generated method stub
		log.debug( "getRealtedSolution");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		
		Map<String, String> queryParameters = new HashMap<>();
		queryParameters.put("created", "DESC");	
		String userId= restPageReqBe.getBody().getUserId();
		RestPageResponse<MLPSolution> mlpSolutionsRest = null;
		List<MLPSolution> content = null;
		RestPageResponse<MLPSolution> mlpSolutionsRestFilter =  null;
		
		log.debug("getSearchedSolutions: searching Solutions with searcTerm:", restPageReqBe.getBody().getSearchTerm());
		mlpSolutionsRest = dataServiceRestClient.findSolutionsBySearchTerm(
				restPageReqBe.getBody().getSearchTerm(),
				new RestPageRequest(restPageReqBe.getBody().getPage(), restPageReqBe.getBody().getSize(), queryParameters));
		if(mlpSolutionsRest != null){
			content = mlpSolutionsRest.getContent().stream().filter(x -> userId.equals(x.getUserId())).collect(Collectors.toList());
			mlpSolutionsRestFilter =  new RestPageResponse<>(content);
		}	
		
		return mlpSolutionsRestFilter;
	}

	@Override
	public String getLatestArtifactsUrl(List<MLPSolutionRevision> solutionRevisions)throws AcumosServiceException {
		
		List<MLPArtifact> mlpSolutionRevisions = null;
		String artifactsUrl = null;
		MLPSolutionRevision latestRevision = null;
		
		if(solutionRevisions.size() > 0)
		  latestRevision = solutionRevisions.stream()
					  .sorted(Comparator.comparing(MLPSolutionRevision::getCreated).reversed())
					  .collect(Collectors.toList()).get(0);
		
		if(latestRevision != null ) {			
			ICommonDataServiceRestClient dataServiceRestClient = getClient();
			mlpSolutionRevisions = dataServiceRestClient.getSolutionRevisionArtifacts(latestRevision.getSolutionId(), latestRevision.getRevisionId());
						
			if(mlpSolutionRevisions.size()>0){				
				int index =0;
				for(MLPArtifact mlpArtifact : mlpSolutionRevisions){
				
					mlpArtifact = mlpSolutionRevisions.stream().sorted(Comparator.comparing(MLPArtifact::getCreated).reversed())
						  .collect(Collectors.toList()).get(index);
					if(mlpArtifact.getArtifactTypeCode().equals("DI")){
						artifactsUrl = mlpArtifact.getUri();
						return artifactsUrl;
					}					
				index++;
				}				
			}				
		}
		
		return artifactsUrl;
	}
	
}
