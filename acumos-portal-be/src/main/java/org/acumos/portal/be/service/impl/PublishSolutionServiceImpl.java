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

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.acumos.portal.be.service.PublishSolutionService;
import org.acumos.portal.be.transport.MLArtifactValidation;
import org.acumos.portal.be.transport.MLModelValidation;
import org.acumos.portal.be.util.EELFLoggerDelegate;
import org.acumos.portal.be.util.JsonUtils;
import org.acumos.portal.be.util.PortalUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import org.acumos.cds.ValidationStatusCode;
import org.acumos.cds.client.ICommonDataServiceRestClient;
import org.acumos.cds.domain.MLPArtifact;
import org.acumos.cds.domain.MLPSolution;
import org.acumos.cds.domain.MLPSolutionRevision;


@Service
public class PublishSolutionServiceImpl extends AbstractServiceImpl implements PublishSolutionService {

	private static final EELFLoggerDelegate log = EELFLoggerDelegate.getLogger(PublishSolutionServiceImpl.class);
	
	@Autowired
	private Environment env;

	public PublishSolutionServiceImpl() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public boolean publishSolution(String solutionId, String accessType, String userId) {
		log.debug(EELFLoggerDelegate.debugLogger, "publishModelBySolution ={}", solutionId);
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		PortalRestClienttImpl portalRestClienttImpl = null;
		/*MLPSolution mlpSolution = new MLPSolution();
		mlpSolution.setAccessTypeCode(accessType);
		mlpSolution.setSolutionId(solutionId);
		mlpSolution.setValidationStatusCode(ValidationStatusCode.PS.name());
		mlpSolution.setOwnerId(userId);*/
		
		MLPSolution mlpSolution2 = null;
		
		//TODO version needs to be noted as we need to only publish specific version
		boolean isPublished = false; 
		try{
			mlpSolution2 = dataServiceRestClient.getSolution(solutionId);
			if(mlpSolution2 != null && mlpSolution2.getOwnerId().equalsIgnoreCase(userId)) {
				//Invoke the Validation API if the validation with Backend is enabled.
				if(!PortalUtils.isEmptyOrNullString(env.getProperty("portal.feature.validateModel")) && env.getProperty("portal.feature.validateModel").equalsIgnoreCase("true")) {
					List<MLPSolutionRevision> mlpSolutionRevisions = dataServiceRestClient.getSolutionRevisions(solutionId);
					if(!PortalUtils.isEmptyList(mlpSolutionRevisions)) {
						//Get first element as versioning is still need to be handled.
						MLPSolutionRevision mlpSolutionRevision = mlpSolutionRevisions.get(0);
						List<MLPArtifact> artifacts = dataServiceRestClient.getSolutionRevisionArtifacts(solutionId, mlpSolutionRevision.getRevisionId());
						List<MLArtifactValidation> artifactValidations = null;
						if(!PortalUtils.isEmptyList(artifacts)) {
							MLModelValidation mlModelValidation = new MLModelValidation();
							mlModelValidation.setSolutionId(solutionId);
							mlModelValidation.setRevisionId(mlpSolutionRevision.getRevisionId());
							mlModelValidation.setUserId(userId);
							mlModelValidation.setVisibility(accessType);
							artifactValidations = new ArrayList<>();
							for(MLPArtifact mlpArtifact : artifacts) {
								MLArtifactValidation artifactValidation = new MLArtifactValidation();
								artifactValidation.setArtifactId(mlpArtifact.getArtifactId());
								artifactValidation.setArtifactName(mlpArtifact.getName());
								artifactValidation.setUrl(env.getProperty("nexus.url") + mlpArtifact.getUri());
								artifactValidations.add(artifactValidation);
							}
							mlModelValidation.setArtifactValidations(artifactValidations);
							
							log.info(EELFLoggerDelegate.debugLogger, "Model to be submitted for validation: "+ JsonUtils.serializer().toPrettyString(mlModelValidation));
							UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(env.getProperty("portal.submitValidation.api"));
							portalRestClienttImpl = new PortalRestClienttImpl(env.getProperty("portal.submitValidation.api"), null, null);
							URI validationUriLocation = portalRestClienttImpl.geRestTemplate().postForLocation(builder.build().encode().toUri(), mlModelValidation);
							
							if(validationUriLocation != null) {
								log.info(EELFLoggerDelegate.debugLogger, "Validation API Location URI: " + validationUriLocation.getPath());
							} else {
								String validationResponse = portalRestClienttImpl.geRestTemplate().postForObject(builder.build().encode().toUri(), mlModelValidation, String.class);
								if(!PortalUtils.isEmptyOrNullString(validationResponse)) {
									log.info(EELFLoggerDelegate.debugLogger, "Validation API Response: ", validationResponse);
								}
							}
						}
					}
				} else {
					portalRestClienttImpl = new PortalRestClienttImpl(env.getProperty("cdms.client.url"), env.getProperty("cdms.client.username"), env.getProperty("cdms.client.password"));
					mlpSolution2.setAccessTypeCode(accessType);
					mlpSolution2.setValidationStatusCode(ValidationStatusCode.PS.name());		
					portalRestClienttImpl.updateSolution(mlpSolution2);
					isPublished = true;
				}
			}
		} catch (Exception e) {
			isPublished = false;
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred while Publishing Solution ={}", e);
		}
		return isPublished;
	}
	
	@Override
	public boolean unpublishSolution(String solutionId, String accessType, String userId) {
		log.debug(EELFLoggerDelegate.debugLogger, "unpublishModelBySolutionId ={}", solutionId);
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		PortalRestClienttImpl portalRestClienttImpl = null;
		MLPSolution mlpSolution = new MLPSolution();
		mlpSolution.setAccessTypeCode(accessType);
		mlpSolution.setSolutionId(solutionId);
		mlpSolution.setOwnerId(userId);
		
		MLPSolution mlpSolution2 = null;
		
		//TODO version needs to be noted as we need to only publish specific version		
		boolean unpublished = false; 
		try{
			//Unpublish the Solution
			mlpSolution2 = dataServiceRestClient.getSolution(solutionId);
			if(mlpSolution2 != null && mlpSolution2.getOwnerId().equalsIgnoreCase(userId)) {
				portalRestClienttImpl = new PortalRestClienttImpl(env.getProperty("cdms.client.url"), env.getProperty("cdms.client.username"), env.getProperty("cdms.client.password"));
				mlpSolution2.setAccessTypeCode(accessType);
				portalRestClienttImpl.updateSolution(mlpSolution2);
				unpublished = true;
			}
			
		} catch (Exception e) {
			unpublished = false;
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred while UnPublishing Solution ={}", e);
		}
		
		return unpublished;
	}

}
