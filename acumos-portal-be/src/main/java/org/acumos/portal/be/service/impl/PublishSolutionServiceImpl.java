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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.acumos.portal.be.common.CommonConstants;
import org.acumos.portal.be.service.PublishSolutionService;
import org.acumos.portal.be.util.PortalUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import org.acumos.cds.client.ICommonDataServiceRestClient;
import org.acumos.cds.domain.MLPCatalog;
import org.acumos.cds.domain.MLPPublishRequest;
import org.acumos.cds.domain.MLPSolution;
import org.acumos.cds.domain.MLPSolutionRevision;
import org.acumos.cds.transport.RestPageRequest;
import org.acumos.cds.transport.RestPageResponse;

@Service
public class PublishSolutionServiceImpl extends AbstractServiceImpl implements PublishSolutionService {

	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());	
	
	@Autowired
	private Environment env;

	public PublishSolutionServiceImpl() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public String publishSolution(String solutionId, String visibility, String userId, String revisionId, String catalogId, UUID trackingId) {
		log.debug("publishModelBySolution ={}", solutionId);
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		MLPSolution mlpSolution2 = null;
		
		String publishStatus = ""; 
		try{
			mlpSolution2 = dataServiceRestClient.getSolution(solutionId);
			if(mlpSolution2 != null && mlpSolution2.getUserId().equalsIgnoreCase(userId)) {
				//Invoke the Validation API if the validation with Backend is enabled.
				if(!PortalUtils.isEmptyOrNullString(env.getProperty("portal.feature.enablePublication")) && env.getProperty("portal.feature.enablePublication").equalsIgnoreCase("true")) {
					MLPSolutionRevision mlpSolutionRevision = dataServiceRestClient.getSolutionRevision(solutionId, revisionId);
					if(mlpSolutionRevision != null) {
						//Check if validation is required
						MLPCatalog catalog = dataServiceRestClient.getCatalog(catalogId);
						//If the request is for public then only go for admin approval. Else publish the revision.
						if(!PortalUtils.isEmptyOrNullString(visibility) && visibility.equalsIgnoreCase(CommonConstants.PUBLIC) && (!catalog.isSelfPublish())) {
							MLPPublishRequest publishRequest = new MLPPublishRequest();
							publishRequest.setSolutionId(solutionId);
							publishRequest.setRevisionId(revisionId);
							publishRequest.setCatalogId(catalogId);
							publishRequest.setRequestUserId(userId);
							//Get Status Code from CDS and then populate 
							publishRequest.setStatusCode(CommonConstants.PUBLISH_REQUEST_PENDING);
							
							//Create separate service for creating request and use single service all over the code
							publishRequest = dataServiceRestClient.createPublishRequest(publishRequest);
							
							log.info("publish request has been created for solution {} with request Id as {}  ", solutionId, publishRequest.getRequestId());
							// Change the return type to send the message that request has been created 
							publishStatus = "Solution "+mlpSolution2.getName()+" Pending for Publisher Approval";
						} else {
							dataServiceRestClient.addSolutionToCatalog(solutionId, catalogId);
							publishStatus = "Solution "+mlpSolution2.getName()+" Published Successfully";
						}
					}
				} else {
					dataServiceRestClient.addSolutionToCatalog(solutionId, catalogId);
					publishStatus = "Solution "+mlpSolution2.getName()+" Published Successfully";
				}
			}
		} catch (Exception e) {
			publishStatus = "Failed to publish the solution";
			log.error("Exception Occurred while Publishing Solution ={}", e);
		}
		return publishStatus;
	}

	
	
	
	@Override
	public boolean unpublishSolution(String solutionId, String catalogId, String userId) {
		//TODO: Need to revisit the un-publish the solution revision. Currently this service is not being used in portal.
		log.debug("unpublishModelBySolutionId ={}", solutionId);
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		MLPSolution mlpSolution = new MLPSolution();
		/*mlpSolution.setAccessTypeCode(accessType);*/
		mlpSolution.setSolutionId(solutionId);
		mlpSolution.setUserId(userId);
		
		MLPSolution mlpSolution2 = null;
		
		//TODO version needs to be noted as we need to only publish specific version		
		boolean unpublished = false; 
		try{
			//Unpublish the Solution
			mlpSolution2 = dataServiceRestClient.getSolution(solutionId);
			if(mlpSolution2 != null && mlpSolution2.getUserId().equalsIgnoreCase(userId)) {
				dataServiceRestClient.dropSolutionFromCatalog(solutionId, catalogId);
				unpublished = true;
			}
			
		} catch (Exception e) {
			unpublished = false;
			log.error("Exception Occurred while UnPublishing Solution ={}", e);
		}
		
		return unpublished;
	}

	@Override
	public boolean checkUniqueSolName(String solutionId) {
		log.debug("checkUniqueSolName ={}", solutionId);
		ICommonDataServiceRestClient dataServiceRestClient = getClient();

		MLPSolution solution = dataServiceRestClient.getSolution(solutionId);
		String[] name = { solution.getName() };

		Map<String, String> queryParameters = new HashMap<>();
		//Fetch the maximum possible records. Need an api that could return the exact match of names along with other nested filter criteria
		RestPageResponse<MLPSolution> searchSolResp = dataServiceRestClient.findPortalSolutions(name, null, true, null,
				null, null, null, null, new RestPageRequest(0, 10000, queryParameters));
		List<MLPSolution> searchSolList = searchSolResp.getContent();

		//removing the same solutionId from the list
		List<MLPSolution> filteredSolList1 = searchSolList.stream()
				.filter(searchSol -> !searchSol.getSolutionId().equalsIgnoreCase(solution.getSolutionId()))
				.collect(Collectors.toList());
		
		//Consider only those records that have exact match with the solution name
		List<MLPSolution> filteredSolList = filteredSolList1.stream()
				.filter(searchSol -> searchSol.getName().equalsIgnoreCase(solution.getName()))
				.collect(Collectors.toList());

		if (!filteredSolList.isEmpty()) {
			return false;
		}

		return true;
	}

}
