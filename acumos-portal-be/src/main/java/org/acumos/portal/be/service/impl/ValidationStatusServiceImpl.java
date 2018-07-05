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

/**
 * 
 */
package org.acumos.portal.be.service.impl;

import org.acumos.portal.be.common.CommonConstants;
import org.acumos.portal.be.service.NotificationService;
import org.acumos.portal.be.service.ValidationStatusService;
import org.acumos.portal.be.transport.MLArtifactValidationStatus;
import org.acumos.portal.be.transport.MLModelValidationCheck;
import org.acumos.portal.be.transport.MLModelValidationStatus;
import org.acumos.portal.be.transport.MLModelValidationStepStatus;
import org.acumos.portal.be.transport.MLNotification;
import org.acumos.portal.be.util.EELFLoggerDelegate;
import org.acumos.portal.be.util.JsonUtils;
import org.acumos.portal.be.util.PortalUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

import org.acumos.cds.ValidationStatusCode;
import org.acumos.cds.ValidationTypeCode;
import org.acumos.cds.client.ICommonDataServiceRestClient;
import org.acumos.cds.domain.MLPArtifact;
import org.acumos.cds.domain.MLPNotification;
import org.acumos.cds.domain.MLPSolution;
import org.acumos.cds.domain.MLPSolutionRevision;
import org.acumos.cds.domain.MLPSolutionValidation;

@Service
public class ValidationStatusServiceImpl extends AbstractServiceImpl implements ValidationStatusService {

	private static final EELFLoggerDelegate log = EELFLoggerDelegate.getLogger(ValidationStatusServiceImpl.class);
	
	@Autowired
	private Environment env;
	
	@Autowired
	private NotificationService notificationService;
	
	private final ReadWriteLock rwLock = new ReentrantReadWriteLock();
	private final Lock readLock = rwLock.readLock();
    private final Lock writeLock = rwLock.writeLock();
    private final Object updateMonitor = new Object();
    
	/**
	 * 
	 */
	public ValidationStatusServiceImpl() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.acumos.portal.be.service.ValidationStatusService#updateValidationTaskStatus(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public boolean updateValidationTaskStatus(String taskId, MLModelValidationStatus newMLModelValidationStatus) {
		if(newMLModelValidationStatus != null) {
			log.debug(EELFLoggerDelegate.debugLogger, "updateValidationTaskStatus ={}", newMLModelValidationStatus.toString());
		}
		
		ICommonDataServiceRestClient client = getClient();
		
		//TODO Need to add synchronized updates 
		synchronized (this) {
			List<MLPSolutionValidation> prevMLPSolutionValidations = null;
			try {
				prevMLPSolutionValidations = client.getSolutionValidations(newMLModelValidationStatus.getSolutionId(), 
						newMLModelValidationStatus.getRevisionId());
			} catch (Exception e) {
				log.debug(EELFLoggerDelegate.debugLogger, "updateValidationTaskStatus = No existing records for validation status for solutionId : {} and revisionId {}",
						newMLModelValidationStatus.getSolutionId(), newMLModelValidationStatus.getRevisionId() );
			}
					
			//Updated the existing Validation Status object and update in the DB 
			if (!PortalUtils.isEmptyList(prevMLPSolutionValidations)) {
				
				for (MLPSolutionValidation mlpSolutionValidation : prevMLPSolutionValidations) {
					// Get First object which is exactly what we needed.
					if (mlpSolutionValidation != null) {
						if(mlpSolutionValidation.getSolutionId().equalsIgnoreCase(newMLModelValidationStatus.getSolutionId()) && mlpSolutionValidation.getRevisionId().equalsIgnoreCase(newMLModelValidationStatus.getRevisionId())) {
							List<MLArtifactValidationStatus> mlArtifactValidations  = null;
							if(!PortalUtils.isEmptyOrNullString(mlpSolutionValidation.getDetail())) {
								mlArtifactValidations = JsonUtils.serializer().fromJson(
										mlpSolutionValidation.getDetail(), new TypeReference<List<MLArtifactValidationStatus>>() {});
							} else {
								mlArtifactValidations = new ArrayList<>();
							}
							
							if(PortalUtils.isEmptyList(mlArtifactValidations)) {
								mlArtifactValidations = new ArrayList<>();
							}
							List<MLArtifactValidationStatus> newMLArtifactValidationStatus = newMLModelValidationStatus.getArtifactValidationStatus();
							if(!PortalUtils.isEmptyList(newMLArtifactValidationStatus)) {
								for(MLArtifactValidationStatus newStatus : newMLArtifactValidationStatus) {
									boolean isExists = false;
									for(MLArtifactValidationStatus validationStatus : mlArtifactValidations) {
										if(validationStatus.getArtifactTaskId().equalsIgnoreCase(newStatus.getArtifactTaskId())) {
											isExists = true;
											if(!validationStatus.getStatus().equalsIgnoreCase(newStatus.getStatus())) {
												validationStatus.setStatus(newStatus.getStatus());
											}
										}
									}
									if(!isExists) {
										mlArtifactValidations.add(newStatus);
									}
								}
								mlpSolutionValidation.setDetail(JsonUtils.serializer().toString(mlArtifactValidations));
							}
							
							log.debug(EELFLoggerDelegate.debugLogger, "updateValidationTaskStatus ={}", JsonUtils.serializer().toPrettyString(mlpSolutionValidation));
							client.updateSolutionValidation(mlpSolutionValidation);
							
							//After updating determine if the Model is eligible for publishing
							
							determinStatusAndPublishModel(client, newMLModelValidationStatus, JsonUtils.serializer().fromJson(
									mlpSolutionValidation.getDetail(), new TypeReference<List<MLArtifactValidationStatus>>() {}), newMLModelValidationStatus.getVisibility());
							break;
						}
						
					}
				}
			} else {
				MLPSolutionValidation newMLArtifactValidationStatus = PortalUtils.convertMLPSolutionValidation(newMLModelValidationStatus);
				client.createSolutionValidation(newMLArtifactValidationStatus);
				//client.updateSolutionValidation(newMlArtifactValidationStatus);
				determinStatusAndPublishModel(client, newMLModelValidationStatus, JsonUtils.serializer().fromJson(
						newMLArtifactValidationStatus.getDetail(), new TypeReference<List<MLArtifactValidationStatus>>() {}), newMLModelValidationStatus.getVisibility());
			}
		}
		return true;
	}
	
	private void determinStatusAndPublishModel(ICommonDataServiceRestClient client, MLModelValidationStatus mlModelValidationStatus, List<MLArtifactValidationStatus> mlArtifactValidations, String visibility) {
		
		log.debug(EELFLoggerDelegate.debugLogger, "Enter determinStatusAndPublishModel");
		
		List<MLPArtifact> artifacts = client.getSolutionRevisionArtifacts(mlModelValidationStatus.getSolutionId(), mlModelValidationStatus.getRevisionId());
		List<MLModelValidationStepStatus> mlModelValidationStepStatus = new ArrayList<>();
		List<MLArtifactValidationStatus> mlArtifactValidationStatusForSS = new ArrayList<>();
		List<MLArtifactValidationStatus> mlArtifactValidationStatusForTA = new ArrayList<>();
		List<MLArtifactValidationStatus> mlArtifactValidationStatusForLC = new ArrayList<>();

		// Collect all status for Security Scan
		mlArtifactValidationStatusForSS = mlArtifactValidations
				.stream().filter(mlArtifactValidationStatus -> mlArtifactValidationStatus
						.getValidationTaskType().equalsIgnoreCase(ValidationTypeCode.SS.toString()))
				.collect(Collectors.toList());

		mlArtifactValidationStatusForTA = mlArtifactValidations
				.stream().filter(mlArtifactValidationStatus -> mlArtifactValidationStatus
						.getValidationTaskType().equalsIgnoreCase(ValidationTypeCode.TA.toString()))
				.collect(Collectors.toList());

		mlArtifactValidationStatusForLC = mlArtifactValidations
				.stream().filter(mlArtifactValidationStatus -> mlArtifactValidationStatus
						.getValidationTaskType().equalsIgnoreCase(ValidationTypeCode.LC.toString()))
				.collect(Collectors.toList());

		//Security Scan
		if(!PortalUtils.isEmptyList(mlArtifactValidationStatusForSS)) {
			mlModelValidationStepStatus.add(getMLModelValidationStepStatus(mlArtifactValidationStatusForSS, ValidationTypeCode.SS.toString(), artifacts));
		}
		//Text Analysis
		if(!PortalUtils.isEmptyList(mlArtifactValidationStatusForTA)) {
			mlModelValidationStepStatus.add(getMLModelValidationStepStatus(mlArtifactValidationStatusForTA, ValidationTypeCode.TA.toString(), artifacts));
		}
		//License Check
		if(!PortalUtils.isEmptyList(mlArtifactValidationStatusForLC)) {
			mlModelValidationStepStatus.add(getMLModelValidationStepStatus(mlArtifactValidationStatusForLC, ValidationTypeCode.LC.toString(), artifacts));
		}
		boolean isAllPassed = false;
		if(!PortalUtils.isEmptyList(mlModelValidationStepStatus)) {
			for (MLModelValidationStepStatus validationStepStatus : mlModelValidationStepStatus) {
				if(validationStepStatus != null && validationStepStatus.getValidationStatus() != null && !validationStepStatus.getValidationStatus().equalsIgnoreCase(ValidationStatusCode.PS.toString())) {
					isAllPassed = false;
					break;
				} else {
					isAllPassed = true;
				}
			}
		}
		if(isAllPassed) {
			MLPSolution mlpSolution = client.getSolution(mlModelValidationStatus.getSolutionId());
			if(mlpSolution != null ) {
				MLPSolutionRevision mlpSolutionRevision = client.getSolutionRevision(mlModelValidationStatus.getSolutionId(), mlModelValidationStatus.getRevisionId());
				mlpSolutionRevision.setAccessTypeCode(visibility);
				mlpSolutionRevision.setValidationStatusCode(CommonConstants.STATUS_PASSED);
				client.updateSolutionRevision(mlpSolutionRevision);
				log.debug(EELFLoggerDelegate.debugLogger, "determinStatusAndPublishModel =Model Published successfully: {}", mlpSolution.toString());
				/*try {
					MLPSolution solutionDetail = client.getSolution(mlModelValidationStatus.getSolutionId());
					String notification = null;
					if (visibility.equals("PB"))
						notification = solutionDetail.getName() + " published to public marketplace";
					else if (visibility.equals("OR"))
						notification = solutionDetail.getName() + " published to company marketplace";
					else
						notification = solutionDetail.getName() + " published to marketplace";
					generateNotification(notification,mlpSolution.getOwnerId());
				} catch (Exception e) {
					log.debug(EELFLoggerDelegate.debugLogger, "determinStatusAndPublishModel = Exception occurred while generating User Notification for validation status of solutionId : {} and revisionId {}",
							mlModelValidationStatus.getSolutionId(), mlModelValidationStatus.getRevisionId() );
				}*/
				
			}
		}
		log.debug(EELFLoggerDelegate.debugLogger, "Exit determinStatusAndPublishModel");
	}

	@Override
	public MLModelValidationCheck getValidationTaskStatus(String solutionId, String revisionId) {
		log.debug(EELFLoggerDelegate.debugLogger, "getValidationTaskStatus =solutionId: {}",
				solutionId + ", revisionId:" + revisionId);
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		List<MLPSolutionValidation> mlpSolutionValidations = dataServiceRestClient.getSolutionValidations(solutionId,
				revisionId);
		MLModelValidationCheck mlModelValidationCheck = null;
		if (!PortalUtils.isEmptyList(mlpSolutionValidations)) {
			for (MLPSolutionValidation mlpSolutionValidation : mlpSolutionValidations) {
				// Get First object which is exactly what we needed.
				if (mlpSolutionValidation != null) {
					mlModelValidationCheck = new MLModelValidationCheck();
					mlModelValidationCheck.setRevisionId(mlpSolutionValidation.getRevisionId());
					mlModelValidationCheck.setSolutionId(mlpSolutionValidation.getSolutionId());
					mlModelValidationCheck.setTaskId(mlpSolutionValidation.getTaskId());
					List<MLModelValidationStepStatus> mlModelValidationStepStatus = new ArrayList<>();
					// Get consolidated status of each Validation Step and log the failed message
					// description only if the validation has failed.
					List<MLPArtifact> artifacts = dataServiceRestClient.getSolutionRevisionArtifacts(solutionId,
							revisionId);
					List<MLArtifactValidationStatus> mlArtifactValidations = JsonUtils.serializer().fromJson(
							mlpSolutionValidation.getDetail(), new TypeReference<List<MLArtifactValidationStatus>>() {});

					List<MLArtifactValidationStatus> mlArtifactValidationStatusForSS = new ArrayList<>();
					List<MLArtifactValidationStatus> mlArtifactValidationStatusForTA = new ArrayList<>();
					List<MLArtifactValidationStatus> mlArtifactValidationStatusForLC = new ArrayList<>();
					//List<MLArtifactValidationStatus> mlArtifactValidationStatusForOQ = new ArrayList<>();

					// Collect all status for Security Scan
					mlArtifactValidationStatusForSS = mlArtifactValidations
							.stream().filter(mlArtifactValidationStatus -> mlArtifactValidationStatus
									.getValidationTaskType().equalsIgnoreCase(ValidationTypeCode.SS.toString()))
							.collect(Collectors.toList());

					mlArtifactValidationStatusForTA = mlArtifactValidations
							.stream().filter(mlArtifactValidationStatus -> mlArtifactValidationStatus
									.getValidationTaskType().equalsIgnoreCase(ValidationTypeCode.TA.toString()))
							.collect(Collectors.toList());

					mlArtifactValidationStatusForLC = mlArtifactValidations
							.stream().filter(mlArtifactValidationStatus -> mlArtifactValidationStatus
									.getValidationTaskType().equalsIgnoreCase(ValidationTypeCode.LC.toString()))
							.collect(Collectors.toList());

					/*mlArtifactValidationStatusForOQ = mlArtifactValidations
							.stream().filter(mlArtifactValidationStatus -> mlArtifactValidationStatus
									.getValidationTaskType().equalsIgnoreCase(ValidationTypeCode.OQ.toString()))
							.collect(Collectors.toList());*/

					//Security Scan
					if(!PortalUtils.isEmptyList(mlArtifactValidationStatusForSS)) {
						mlModelValidationStepStatus.add(getMLModelValidationStepStatus(mlArtifactValidationStatusForSS, ValidationTypeCode.SS.toString(), artifacts));
					}
					//Text Analysis
					if(!PortalUtils.isEmptyList(mlArtifactValidationStatusForTA)) {
						mlModelValidationStepStatus.add(getMLModelValidationStepStatus(mlArtifactValidationStatusForTA, ValidationTypeCode.TA.toString(), artifacts));
					}
					//License Check
					if(!PortalUtils.isEmptyList(mlArtifactValidationStatusForLC)) {
						mlModelValidationStepStatus.add(getMLModelValidationStepStatus(mlArtifactValidationStatusForLC, ValidationTypeCode.LC.toString(), artifacts));
					}
					//OSS Quantification
					/*if(!PortalUtils.isEmptyList(mlArtifactValidationStatusForOQ)) {
						mlModelValidationStepStatus.add(getMLModelValidationStepStatus(mlArtifactValidationStatusForOQ, ValidationTypeCode.OQ.toString(), artifacts));
					}*/
					mlModelValidationCheck.setMlModelValidationStepStatus(mlModelValidationStepStatus);
					break;
				}
			}
		}
		if(mlModelValidationCheck != null ) {
			log.debug(EELFLoggerDelegate.debugLogger, "getValidationTaskStatus =MLModelValidationCheck: " +
					mlModelValidationCheck.toString());
		}
		return mlModelValidationCheck;
	}

	private MLModelValidationStepStatus getMLModelValidationStepStatus(List<MLArtifactValidationStatus> artifactValidationStatusList, String validationStep, List<MLPArtifact> artifacts) {
		MLModelValidationStepStatus mlModelValidationStepStatus = null;
		// Check SS status
		if (!PortalUtils.isEmptyList(artifactValidationStatusList) 
				&& artifactValidationStatusList.size() == artifacts.size()) {
			// All Artifacts has been through the validation process. lets verify the status
			// of all and determine consolidated status
			int count = 0;
			boolean isAllSuccess = false;
			boolean hasSomeInProgress = false;
			boolean hasSomeFailed = false;
			boolean hasSomeSubmitted = false;
			mlModelValidationStepStatus = new MLModelValidationStepStatus();
			StringBuilder failedArtifacts = null;
			
			for(MLArtifactValidationStatus artifactValidationStatus : artifactValidationStatusList) {
				
				/*if(artifactValidationStatus.getValidationTaskType().equalsIgnoreCase(validationStep) && artifactValidationStatus.getStatus().equalsIgnoreCase(ValidationStatusCode.PS.toString())) {
					
				}*/
				if(artifactValidationStatus.getValidationTaskType().equalsIgnoreCase(validationStep) && artifactValidationStatus.getStatus().equalsIgnoreCase(ValidationStatusCode.IP.toString())) {
					hasSomeInProgress = true;
				}
				if(artifactValidationStatus.getValidationTaskType().equalsIgnoreCase(validationStep) && artifactValidationStatus.getStatus().equalsIgnoreCase(ValidationStatusCode.FA.toString())) {
					if(failedArtifacts == null) {
						failedArtifacts = new StringBuilder();
					}
					for(MLPArtifact artifact : artifacts) {
						if(artifactValidationStatus.getArtifactId().equalsIgnoreCase(artifact.getArtifactId())) {
							failedArtifacts.append("Artifact ").append(artifact.getName())
											.append(" has failed ").append(validationStep).append(" validation;");
						}
					}
					hasSomeFailed = true;
				}
				if(artifactValidationStatus.getValidationTaskType().equalsIgnoreCase(validationStep) && artifactValidationStatus.getStatus().equalsIgnoreCase(ValidationStatusCode.SB.toString())) {
					hasSomeSubmitted = true;
					
				}
				count++;
			}
			
			mlModelValidationStepStatus.setValidationType(validationStep);
			if(count == artifacts.size()) {
				if(hasSomeFailed) {
					mlModelValidationStepStatus.setValidationStatus(ValidationStatusCode.FA.toString());
					if(failedArtifacts != null && !PortalUtils.isEmptyOrNullString(failedArtifacts.toString())) {
						mlModelValidationStepStatus.setValidationStatusDesc(failedArtifacts.toString());
					}
				} else if(hasSomeInProgress) {
					mlModelValidationStepStatus.setValidationStatus(ValidationStatusCode.IP.toString());
				} else if(hasSomeSubmitted) {
					mlModelValidationStepStatus.setValidationStatus(ValidationStatusCode.SB.toString());
				} else {
					mlModelValidationStepStatus.setValidationStatus(ValidationStatusCode.PS.toString());
				}
			}
		}
		return mlModelValidationStepStatus;
	}
	
	
	private MLModelValidationStepStatus getSLModelValidationStepStatus(List<MLArtifactValidationStatus> artifactValidationStatusList, String validationStep, List<MLPArtifact> artifacts) {
		MLModelValidationStepStatus mlModelValidationStepStatus = null;
		// Check SS status
		if (!PortalUtils.isEmptyList(artifactValidationStatusList)) {
			// All Artifacts has been through the validation process. lets verify the status
			// of all and determine consolidated status
			int count = 0;
			boolean isAllSuccess = false;
			boolean hasSomeInProgress = false;
			boolean hasSomeFailed = false;
			boolean hasSomeSubmitted = false;
			mlModelValidationStepStatus = new MLModelValidationStepStatus();
			StringBuilder failedArtifacts = null;
			
			for(MLArtifactValidationStatus artifactValidationStatus : artifactValidationStatusList) {
				
				/*if(artifactValidationStatus.getValidationTaskType().equalsIgnoreCase(validationStep) && artifactValidationStatus.getStatus().equalsIgnoreCase(ValidationStatusCode.PS.toString())) {
					
				}*/
				if(artifactValidationStatus.getValidationTaskType().equalsIgnoreCase(validationStep) && artifactValidationStatus.getStatus().equalsIgnoreCase(ValidationStatusCode.IP.toString())) {
					hasSomeInProgress = true;
				}
				if(artifactValidationStatus.getValidationTaskType().equalsIgnoreCase(validationStep) && artifactValidationStatus.getStatus().equalsIgnoreCase(ValidationStatusCode.FA.toString())) {
					if(failedArtifacts == null) {
						failedArtifacts = new StringBuilder();
					}
					for(MLPArtifact artifact : artifacts) {
						if(artifactValidationStatus.getArtifactId().equalsIgnoreCase(artifact.getArtifactId())) {
							failedArtifacts.append("Artifact ").append(artifact.getName())
											.append(" has failed ").append(validationStep).append(" validation;");
						}
					}
					hasSomeFailed = true;
				}
				if(artifactValidationStatus.getValidationTaskType().equalsIgnoreCase(validationStep) && artifactValidationStatus.getStatus().equalsIgnoreCase(ValidationStatusCode.SB.toString())) {
					hasSomeSubmitted = true;
					
				}
				count++;
			}
			
			mlModelValidationStepStatus.setValidationType(validationStep);
			
			if(hasSomeFailed) {
				mlModelValidationStepStatus.setValidationStatus(ValidationStatusCode.FA.toString());
				if(failedArtifacts != null && !PortalUtils.isEmptyOrNullString(failedArtifacts.toString())) {
					mlModelValidationStepStatus.setValidationStatusDesc(failedArtifacts.toString());
				}
			} else if(hasSomeInProgress) {
				mlModelValidationStepStatus.setValidationStatus(ValidationStatusCode.IP.toString());
			} else if(hasSomeSubmitted) {
				mlModelValidationStepStatus.setValidationStatus(ValidationStatusCode.SB.toString());
			} else {
				mlModelValidationStepStatus.setValidationStatus(ValidationStatusCode.PS.toString());
			}
			
		}
		return mlModelValidationStepStatus;
	}
	
	void generateNotification(String msg, String userId) {
		MLPNotification notification = new MLPNotification();
		try {
			if (msg != null) {
				notification.setTitle(msg);
				notification.setMessage(msg);
				Date startDate = new Date();
				Date endDate = new Date(startDate.getTime() + (1000 * 60 * 60 * 24));
				notification.setStart(startDate);
				notification.setEnd(endDate);
				MLNotification mlNotification = notificationService.createNotification(notification);
				notificationService.addNotificationUser(mlNotification.getNotificationId(), userId);
			}
		} catch (Exception e) {
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred while getNotifications", e);
		}
	}
	
	
	
	@Override
	public MLModelValidationCheck getSolutionValidationTaskStatus(String solutionId, String revisionId) {
		log.debug(EELFLoggerDelegate.debugLogger, "getValidationTaskStatus =solutionId: {}",
				solutionId + ", revisionId:" + revisionId);
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		List<MLPSolutionValidation> mlpSolutionValidations = dataServiceRestClient.getSolutionValidations(solutionId,
				revisionId);
		MLModelValidationCheck mlModelValidationCheck = null;
		if (!PortalUtils.isEmptyList(mlpSolutionValidations)) {
			for (MLPSolutionValidation mlpSolutionValidation : mlpSolutionValidations) {
				// Get First object which is exactly what we needed.
				if (mlpSolutionValidation != null) {
					mlModelValidationCheck = new MLModelValidationCheck();
					mlModelValidationCheck.setRevisionId(mlpSolutionValidation.getRevisionId());
					mlModelValidationCheck.setSolutionId(mlpSolutionValidation.getSolutionId());
					mlModelValidationCheck.setTaskId(mlpSolutionValidation.getTaskId());
					List<MLModelValidationStepStatus> mlModelValidationStepStatus = new ArrayList<>();
					// Get consolidated status of each Validation Step and log the failed message
					// description only if the validation has failed.
					List<MLPArtifact> artifacts = dataServiceRestClient.getSolutionRevisionArtifacts(solutionId,
							revisionId);
					List<MLArtifactValidationStatus> mlArtifactValidations = JsonUtils.serializer().fromJson(
							mlpSolutionValidation.getDetail(), new TypeReference<List<MLArtifactValidationStatus>>() {});

					List<MLArtifactValidationStatus> mlArtifactValidationStatusForSS = new ArrayList<>();
					List<MLArtifactValidationStatus> mlArtifactValidationStatusForTA = new ArrayList<>();
					List<MLArtifactValidationStatus> mlArtifactValidationStatusForLC = new ArrayList<>();
					//List<MLArtifactValidationStatus> mlArtifactValidationStatusForOQ = new ArrayList<>();

					// Collect all status for Security Scan
					mlArtifactValidationStatusForSS = mlArtifactValidations
							.stream().filter(mlArtifactValidationStatus -> mlArtifactValidationStatus
									.getValidationTaskType().equalsIgnoreCase(ValidationTypeCode.SS.toString()))
							.collect(Collectors.toList());

					mlArtifactValidationStatusForTA = mlArtifactValidations
							.stream().filter(mlArtifactValidationStatus -> mlArtifactValidationStatus
									.getValidationTaskType().equalsIgnoreCase(ValidationTypeCode.TA.toString()))
							.collect(Collectors.toList());

					mlArtifactValidationStatusForLC = mlArtifactValidations
							.stream().filter(mlArtifactValidationStatus -> mlArtifactValidationStatus
									.getValidationTaskType().equalsIgnoreCase(ValidationTypeCode.LC.toString()))
							.collect(Collectors.toList());

					/*mlArtifactValidationStatusForOQ = mlArtifactValidations
							.stream().filter(mlArtifactValidationStatus -> mlArtifactValidationStatus
									.getValidationTaskType().equalsIgnoreCase(ValidationTypeCode.OQ.toString()))
							.collect(Collectors.toList());*/

					//Security Scan
					if(!PortalUtils.isEmptyList(mlArtifactValidationStatusForSS)) {
						mlModelValidationStepStatus.add(getSLModelValidationStepStatus(mlArtifactValidationStatusForSS, ValidationTypeCode.SS.toString(), artifacts));
					}
					//Text Analysis
					if(!PortalUtils.isEmptyList(mlArtifactValidationStatusForTA)) {
						mlModelValidationStepStatus.add(getSLModelValidationStepStatus(mlArtifactValidationStatusForTA, ValidationTypeCode.TA.toString(), artifacts));
					}
					//License Check
					if(!PortalUtils.isEmptyList(mlArtifactValidationStatusForLC)) {
						mlModelValidationStepStatus.add(getSLModelValidationStepStatus(mlArtifactValidationStatusForLC, ValidationTypeCode.LC.toString(), artifacts));
					}
					//OSS Quantification
					/*if(!PortalUtils.isEmptyList(mlArtifactValidationStatusForOQ)) {
						mlModelValidationStepStatus.add(getMLModelValidationStepStatus(mlArtifactValidationStatusForOQ, ValidationTypeCode.OQ.toString(), artifacts));
					}*/
					mlModelValidationCheck.setMlModelValidationStepStatus(mlModelValidationStepStatus);
					break;
				}
			}
		}
		if(mlModelValidationCheck != null ) {
			log.debug(EELFLoggerDelegate.debugLogger, "getValidationTaskStatus =MLModelValidationCheck: " +
					mlModelValidationCheck.toString());
		}
		return mlModelValidationCheck;
	}
}
