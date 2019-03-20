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
import java.util.UUID;
import java.util.stream.Collectors;

import org.acumos.portal.be.common.CommonConstants;
import org.acumos.portal.be.service.AdminService;
import org.acumos.portal.be.service.PublishSolutionService;
import org.acumos.portal.be.util.EELFLoggerDelegate;
import org.acumos.portal.be.util.PortalUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.acumos.cds.client.ICommonDataServiceRestClient;
import org.acumos.cds.domain.MLPPublishRequest;
import org.acumos.cds.domain.MLPSiteConfig;
import org.acumos.cds.domain.MLPSolution;
import org.acumos.cds.domain.MLPSolutionRevision;
import org.acumos.cds.domain.MLPUser;
import org.acumos.cds.transport.AuthorTransport;
import org.acumos.cds.transport.RestPageRequest;
import org.acumos.cds.transport.RestPageResponse;

@Service
public class PublishSolutionServiceImpl extends AbstractServiceImpl implements PublishSolutionService {

	private static final EELFLoggerDelegate log = EELFLoggerDelegate.getLogger(PublishSolutionServiceImpl.class);
	
	@Autowired
	private Environment env;
	
	@Autowired
	private AdminService adminService;

	public PublishSolutionServiceImpl() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public String publishSolution(String solutionId, String accessType, String userId, String revisionId, UUID trackingId) {
		log.debug(EELFLoggerDelegate.debugLogger, "publishModelBySolution ={}", solutionId);
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
						//If the request is for public then only go for admin approval. Else publish the revision.
						if(!PortalUtils.isEmptyOrNullString(accessType) && accessType.equalsIgnoreCase(CommonConstants.PUBLIC)) {
							MLPPublishRequest publishRequest = new MLPPublishRequest();
							publishRequest.setSolutionId(solutionId);
							publishRequest.setRevisionId(revisionId);
							publishRequest.setRequestUserId(userId);
							//Get Status Code from CDS and then populate 
							publishRequest.setStatusCode(CommonConstants.PUBLISH_REQUEST_PENDING);
							
							//Create separate service for creating request and use single service all over the code
							publishRequest = dataServiceRestClient.createPublishRequest(publishRequest);
							
							log.info(EELFLoggerDelegate.debugLogger, "publish request has been created for solution {} with request Id as {}  ", solutionId, publishRequest.getRequestId());
							// Change the return type to send the message that request has been created 
							publishStatus = "Solution "+mlpSolution2.getName()+" Pending for Publisher Approval";
						} else {
							updateSolution(mlpSolution2.getSolutionId(), revisionId, accessType);
							publishStatus = "Solution "+mlpSolution2.getName()+" Published Successfully";
						}
					}
				} else {
					updateSolution(mlpSolution2.getSolutionId(), revisionId, accessType);
					publishStatus = "Solution "+mlpSolution2.getName()+" Published Successfully";
				}
			}
		} catch (Exception e) {
			publishStatus = "Failed to publish the solution";
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred while Publishing Solution ={}", e);
		}
		return publishStatus;
	}

	@Override
	public void updateSolution(String solutionId, String revisionId, String accessType) {
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		MLPSolutionRevision mlpSolutionRevision = dataServiceRestClient.getSolutionRevision(solutionId, revisionId);
		
		if (mlpSolutionRevision != null) {
			AuthorTransport[] authors = mlpSolutionRevision.getAuthors();
			if (authors == null || authors.length == 0) {
				//Fetch user using ownerId of revision
				MLPUser owner = dataServiceRestClient.getUser(mlpSolutionRevision.getUserId());
				if (owner != null) {
					List<AuthorTransport> lst = new ArrayList<AuthorTransport>();
					AuthorTransport ownerAT = new AuthorTransport();
					//Fill user name into author and save to revision
					ownerAT.setName(owner.getFirstName() + " " + owner.getLastName());
					ownerAT.setContact(owner.getEmail());
					lst.add(ownerAT);
					AuthorTransport[] targetArray = lst.toArray(new AuthorTransport[lst.size()]);
					mlpSolutionRevision.setAuthors(targetArray);
				}
			}
		}
		
		mlpSolutionRevision.setPublisher(getSiteInstanceName());
		mlpSolutionRevision.setAccessTypeCode(accessType);
		dataServiceRestClient.updateSolutionRevision(mlpSolutionRevision);
	}
	
	private String getSiteInstanceName() {
		String siteInstanceName = null;
		ObjectMapper mapper = new ObjectMapper();
		try {
			MLPSiteConfig siteConfig = adminService.getSiteConfig(CommonConstants.SITE_CONFIG);
			String configStr = siteConfig.getConfigValue();

			Map<String, Object> resp = mapper.readValue(configStr, Map.class);
			List<Map<String, String>> fields = (List<Map<String, String>>) resp.get(CommonConstants.FIELDS);
			for(Map<String, String> field : fields) {
				if(field.get(CommonConstants.NAME).equals(CommonConstants.SITE_INSTANCE_NAME)) {
					siteInstanceName = field.get(CommonConstants.DATA);
				}
			}

		} catch (Exception e) {
			log.error(EELFLoggerDelegate.errorLogger, "getSiteInstanceName");
			//Log error and do nothing. Return the null value in site name cannot be found
		} 
		return siteInstanceName;
	}
	
	@Override
	public boolean unpublishSolution(String solutionId, String accessType, String userId) {
		//TODO: Need to revisit the un-publish the solution revision. Currently this service is not being used in portal.
		log.debug(EELFLoggerDelegate.debugLogger, "unpublishModelBySolutionId ={}", solutionId);
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		PortalRestClienttImpl portalRestClienttImpl = null;
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
				portalRestClienttImpl = new PortalRestClienttImpl(env.getProperty("cdms.client.url"), env.getProperty("cdms.client.username"), env.getProperty("cdms.client.password"));
				/*mlpSolution2.setAccessTypeCode(accessType);*/
				portalRestClienttImpl.updateSolution(mlpSolution2);
				unpublished = true;
			}
			
		} catch (Exception e) {
			unpublished = false;
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred while UnPublishing Solution ={}", e);
		}
		
		return unpublished;
	}

	@Override
	public boolean checkUniqueSolName(String solutionId) {
		log.debug(EELFLoggerDelegate.debugLogger, "checkUniqueSolName ={}", solutionId);
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		String[] accessTypeCodes = { CommonConstants.PUBLIC, CommonConstants.ORGANIZATION };

		MLPSolution solution = dataServiceRestClient.getSolution(solutionId);
		String[] name = { solution.getName() };

		Map<String, String> queryParameters = new HashMap<>();
		//Fetch the maximum possible records. Need an api that could return the exact match of names along with other nested filter criteria
		RestPageResponse<MLPSolution> searchSolResp = dataServiceRestClient.findPortalSolutions(name, null, true, null,
				accessTypeCodes, null, null, null, null, new RestPageRequest(0, 10000, queryParameters));
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
