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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

import org.acumos.cds.AccessTypeCode;
import org.acumos.cds.CodeNameType;
import org.acumos.cds.client.ICommonDataServiceRestClient;
import org.acumos.cds.domain.MLPArtifact;
import org.acumos.cds.domain.MLPCodeNamePair;
import org.acumos.cds.domain.MLPDocument;
import org.acumos.cds.domain.MLPRevisionDescription;
import org.acumos.cds.domain.MLPSolution;
import org.acumos.cds.domain.MLPSolutionFavorite;
import org.acumos.cds.domain.MLPSolutionRating;
import org.acumos.cds.domain.MLPSolutionRevision;
import org.acumos.cds.domain.MLPStepResult;
import org.acumos.cds.domain.MLPTag;
import org.acumos.cds.domain.MLPUser;
import org.acumos.cds.transport.AuthorTransport;
import org.acumos.cds.transport.RestPageRequest;
import org.acumos.cds.transport.RestPageResponse;
import org.acumos.nexus.client.NexusArtifactClient;
import org.acumos.nexus.client.RepositoryLocation;
import org.acumos.nexus.client.data.UploadArtifactInfo;
import org.acumos.portal.be.common.CommonConstants;
import org.acumos.portal.be.common.JsonRequest;
import org.acumos.portal.be.common.RestPageRequestBE;
import org.acumos.portal.be.common.RestPageResponseBE;
import org.acumos.portal.be.common.exception.AcumosServiceException;
import org.acumos.portal.be.service.MarketPlaceCatalogService;
import org.acumos.portal.be.service.NotificationService;
import org.acumos.portal.be.service.UserService;
import org.acumos.portal.be.transport.Author;
import org.acumos.portal.be.transport.MLSolution;
import org.acumos.portal.be.transport.MLSolutionFavorite;
import org.acumos.portal.be.transport.MLSolutionRating;
import org.acumos.portal.be.transport.RestPageRequestPortal;
import org.acumos.portal.be.transport.RevisionDescription;
import org.acumos.portal.be.transport.User;
import org.acumos.portal.be.util.EELFLoggerDelegate;
import org.acumos.portal.be.util.PortalUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.maven.wagon.ConnectionException;
import org.apache.maven.wagon.ResourceDoesNotExistException;
import org.apache.maven.wagon.TransferFailedException;
import org.apache.maven.wagon.authentication.AuthenticationException;
import org.apache.maven.wagon.authorization.AuthorizationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service to Support Market Place Catalog and Manage models modules
 */
@Service
public class MarketPlaceCatalogServiceImpl extends AbstractServiceImpl implements MarketPlaceCatalogService {

	private static final EELFLoggerDelegate log = EELFLoggerDelegate.getLogger(MarketPlaceCatalogServiceImpl.class);

	@Autowired
	private Environment env;

	@Autowired
	private UserService userService;
	
	@Autowired
	private MarketPlaceCatalogService catalogService;
	
	@Autowired
	private NotificationService notificationService;
 
	/*@Autowired
	private NexusArtifactClient nexusArtifactClient;
	*/
	
	private static final String STEP_STATUS_FAILED = "FA";
	
	/*
	 * No
	 */
	public MarketPlaceCatalogServiceImpl() {

	}

	@Override
	public RestPageResponse<MLPSolution> getAllPaginatedSolutions(Integer page, Integer size, String sortingOrder)
			throws AcumosServiceException {
		log.debug(EELFLoggerDelegate.debugLogger, "getAllPaginatedSolutions");
		RestPageResponse<MLPSolution> mlpSolutionsPaged = null;
		List<MLSolution> mlSolutions = null;
		try {
			ICommonDataServiceRestClient dataServiceRestClient = getClient();
			// TODO: revisit this code to pass query parameters to CCDS Service
			Map<String, Object> queryParameters = new HashMap<>();
			queryParameters.put("active", "Y"); // Fetch all active solutions
			queryParameters.put("accessTypeCode", AccessTypeCode.PB.toString());
			List<MLPSolution> mlpSolutions = new ArrayList<MLPSolution>();
			// dataServiceRestClient.searchSolutions(queryParameters, false);
			///////////////////////////////////////
			Map<String, String> sortingOrderMap = null;

			if (!PortalUtils.isEmptyOrNullString(sortingOrder)) {
				sortingOrderMap = new HashMap<>();
				sortingOrderMap.put("sort", sortingOrder);
			}
			RestPageRequest pageRequest = new RestPageRequest(page, size, sortingOrderMap);
			mlpSolutionsPaged = dataServiceRestClient.getSolutions(pageRequest);
			// This logic needs to be corrected. If using MLPSolution then a
			// separate query from UI is needed to get the User Name of the each
			// SOlution
			if (!PortalUtils.isEmptyList(mlpSolutions)) {
				mlSolutions = new ArrayList<>();
				for (MLPSolution mlpSolution : mlpSolutions) {
					MLSolution mlSolution = PortalUtils.convertToMLSolution(mlpSolution);
					// Identify the OwnerName for each solution
					MLPUser user = dataServiceRestClient.getUser(mlpSolution.getUserId());
					if (user != null) {
						mlSolution.setOwnerName(user.getFirstName());
						mlSolutions.add(mlSolution);
					}
				}
			}
		} catch (IllegalArgumentException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INVALID_PARAMETER, e.getMessage());
		} catch (HttpClientErrorException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
		}
		return mlpSolutionsPaged;
	}

	@Override
	public List<MLSolution> getAllPublishedSolutions() throws AcumosServiceException {
		log.debug(EELFLoggerDelegate.debugLogger, "getAllPublishedSolutions");
		List<MLSolution> mlSolutions = null;
		try {
			ICommonDataServiceRestClient dataServiceRestClient = getClient();
			Map<String, Object> queryParameters = new HashMap<>();
			// queryParameters.put("active", "Y"); //Fetch all active solutions
			queryParameters.put("accessTypeCode", AccessTypeCode.PB.toString());
			// TODO Lets keep it simple by using List for now. Need to modify
			// this to use Pagination by providing page number and result fetch
			// size
			List<MLPSolution> mlpSolutions = new ArrayList<MLPSolution>();// dataServiceRestClient.searchSolutions(queryParameters,
																			// false);
			if (!PortalUtils.isEmptyList(mlpSolutions)) {
				mlSolutions = new ArrayList<>();
				for (MLPSolution mlpSolution : mlpSolutions) {
					MLSolution mlSolution = PortalUtils.convertToMLSolution(mlpSolution);
					MLPUser user = dataServiceRestClient.getUser(mlpSolution.getUserId());
					if (user != null) {
						mlSolution.setOwnerName(user.getFirstName());
						mlSolutions.add(mlSolution);
					}
				}
			}
		} catch (IllegalArgumentException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INVALID_PARAMETER, e.getMessage());
		} catch (HttpClientErrorException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
		}
		return mlSolutions;
	}
	
	@Override
	public MLSolution getSolution(String solutionId, String loginUserId) throws AcumosServiceException {
		log.debug(EELFLoggerDelegate.debugLogger, "getSolution");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		MLSolution mlSolution = null;
		try {
			MLPSolution mlpSolution = dataServiceRestClient.getSolution(solutionId);
			if (mlpSolution != null) {
				mlSolution = PortalUtils.convertToMLSolution(mlpSolution);
				List<MLPCodeNamePair> toolkitTypeList = dataServiceRestClient.getCodeNamePairs(CodeNameType.TOOLKIT_TYPE);
				if (toolkitTypeList.size() > 0) {
					for (MLPCodeNamePair toolkitType : toolkitTypeList) {
						if (toolkitType.getCode() != null) {
							if (toolkitType.getCode().equalsIgnoreCase(mlpSolution.getToolkitTypeCode())) {
								mlSolution.setTookitTypeName(toolkitType.getName());
 								break;
 							}
 						}
 					}
				}
				List<MLPCodeNamePair> modelTypeList = dataServiceRestClient.getCodeNamePairs(CodeNameType.MODEL_TYPE);
				if (modelTypeList.size() > 0) {
					for (MLPCodeNamePair modelType : modelTypeList) {
						if (modelType.getCode() != null) {
							if (modelType.getCode().equalsIgnoreCase(mlpSolution.getModelTypeCode())) {
								mlSolution.setModelTypeName(modelType.getName());
 								break;
 							}
 						}
					}
				}

				MLPUser mlpUser = dataServiceRestClient.getUser(mlpSolution.getUserId());
				if (mlpUser != null) {
					mlSolution.setOwnerName(mlpUser.getFirstName().concat(" " + mlpUser.getLastName()));
				}

				List<MLPTag> tagList = dataServiceRestClient.getSolutionTags(solutionId);
				if (tagList.size() > 0) {
					mlSolution.setSolutionTagList(tagList);
				}

				List<User> users = null;
				//Set co-owners list for model
				try {
					
					List<MLPUser> mlpUsersList = dataServiceRestClient
							.getSolutionAccessUsers(mlSolution.getSolutionId());
					if (!PortalUtils.isEmptyList(mlpUsersList)) {
						users = new ArrayList<>();
						for (MLPUser mlpusers : mlpUsersList) {
							User user = PortalUtils.convertToMLPuser(mlpusers);
							users.add(user);
						}
					}
					mlSolution.setOwnerListForSol(users);
				} catch (Exception e) {
					log.error(EELFLoggerDelegate.errorLogger, "No co-owner for SolutionId={}",
							mlSolution.getSolutionId());
				}

				List<String> co_owners_Id = new ArrayList<String>();
				if(users != null) {
					co_owners_Id = users.stream().map(User :: getUserId).collect(Collectors.toList());
				}
				List<MLPSolutionRevision> revisionList = dataServiceRestClient.getSolutionRevisions(solutionId);
				List<MLPSolutionRevision> filterRevisionList = new ArrayList<>();
				if (revisionList.size() > 0) {
					//filter the private versions if loggedIn User is not the owner of solution
					List<String> accessCodes = new ArrayList<String>();
					accessCodes.add(CommonConstants.PUBLIC);
					if (loginUserId != null) {
						//if logged In user is owner/co-owner then add private revisions
						if (loginUserId.equals(mlpSolution.getUserId()) || co_owners_Id.contains(loginUserId) || userService.isPublisherRole(loginUserId)) {
							accessCodes.add(CommonConstants.PRIVATE);
							accessCodes.add(CommonConstants.ORGANIZATION);
						} else {
							//if user is logged in but he not the owner/co-owner then add Company revisions
							accessCodes.add(CommonConstants.ORGANIZATION);
						}
					}

					filterRevisionList = revisionList.stream().filter(revision -> accessCodes.contains(revision.getAccessTypeCode())).collect(Collectors.toList());
					if(filterRevisionList.size() > 0) {
						mlSolution.setRevisions(filterRevisionList);
						//Add the access Type code for the latest revision for the categorization while display
						mlSolution.setAccessType(filterRevisionList.get(0).getAccessTypeCode());
					}
				}
			}
		} catch (ArithmeticException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.ARITHMATIC_EXCEPTION, e.getMessage());
		} catch (IllegalArgumentException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INVALID_PARAMETER, e.getMessage());
		} catch (HttpClientErrorException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
		}
		return mlSolution;
	}

	@Override
	public MLSolution getSolution(String solutionId) throws AcumosServiceException {
		return getSolution(solutionId, null);
	}

	@Override
	public List<MLSolution> getAllSolutions() {

		return null;
	}

	@Override
	public List<MLSolution> getSearchSolution(String search) throws AcumosServiceException {
		log.debug(EELFLoggerDelegate.debugLogger, "getSearchedSolutions");
		List<MLSolution> mlSolutions = null;

		return mlSolutions;
	}


	@Override
	public MLSolution deleteSolution(MLSolution mlSolution) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MLSolution updateSolution(MLSolution mlSolution, String solutionId) throws AcumosServiceException {
		log.debug(EELFLoggerDelegate.debugLogger, "updateSolution");
		try {
			ICommonDataServiceRestClient dataServiceRestClient = getClient();
			MLPSolution solution = PortalUtils.convertToMLPSolution(mlSolution);
			try {
				List<MLPTag> taglist = dataServiceRestClient.getSolutionTags(solutionId);
				HashSet<MLPTag> tags = new HashSet<MLPTag>(taglist.size());
				for (MLPTag tag : taglist)
					tags.add(tag);
				solution.setTags(tags);
			} catch (HttpStatusCodeException e) {
				log.error(EELFLoggerDelegate.errorLogger, "Could not fetch tag list for update solution: " + e.getMessage());
			} finally {				
				dataServiceRestClient.updateSolution(solution);
			}
		} catch (IllegalArgumentException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INVALID_PARAMETER, e.getMessage());
		} catch (HttpClientErrorException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
		}
		return mlSolution;
	}
	
	
	
	@Override
	public MLSolution deleteSolutionArtifacts(MLSolution mlSolution, String solutionId, String revisionId) 
			throws AcumosServiceException, URISyntaxException {
		log.debug(EELFLoggerDelegate.debugLogger, "deleteSolutionArtifacts");
		try {
			ICommonDataServiceRestClient dataServiceRestClient = getClient();

			// fetch the solution to populate the solution picture so that it does not get wiped off
			//Check if Image is present in the object. if not then fetch the solution image and then populate it
			
			MLPSolution solution = PortalUtils.convertToMLPSolution(mlSolution);
			try {
				List<MLPTag> taglist = dataServiceRestClient.getSolutionTags(solutionId);
				HashSet<MLPTag> tags = new HashSet<MLPTag>(taglist.size());
				for (MLPTag tag : taglist)
					tags.add(tag);
				solution.setTags(tags);
			} catch (HttpStatusCodeException e) {
				log.error(EELFLoggerDelegate.errorLogger, "Could not fetch tag list for delete solution artifacts: " + e.getMessage());
			} finally {
				//start
				 
				if(revisionId != null){
					List<MLPArtifact> mlpArtifactsList = dataServiceRestClient.getSolutionRevisionArtifacts(solutionId, revisionId);
								
					MLPArtifact mlpArtifactClone = new MLPArtifact();
					for (MLPArtifact mlp : mlpArtifactsList) {
						boolean deleteNexus = false;
						// Delete the file from the Nexus
						String nexusUrl = env.getProperty("nexus.url");
						String nexusUserName = env.getProperty("nexus.username");
						String nexusPd = env.getProperty("nexus.password");
						NexusArtifactClient nexusArtifactClient = nexusArtifactClient(nexusUrl, nexusUserName, nexusPd);
						nexusArtifactClient.deleteArtifact(mlp.getUri());
						deleteNexus = true;
						
						if(deleteNexus){
		                    String mlpArtifactTypeCode = mlp.getArtifactTypeCode();		                     
							String artifactId = mlp.getArtifactId();
							// Delete SolutionRevisionArtifact
							dataServiceRestClient.dropSolutionRevisionArtifact(solutionId, revisionId, artifactId);
							log.debug(EELFLoggerDelegate.debugLogger, " Successfully Deleted the SolutionRevisionArtifact ");
							// Delete Artifact from CDS						
							dataServiceRestClient.deleteArtifact(artifactId);
							log.debug(EELFLoggerDelegate.debugLogger, " Successfully Deleted the CDump Artifact ");
						}else{
							throw new AcumosServiceException(AcumosServiceException.ErrorCode.INVALID_PARAMETER, "Unable to delete  solution from Database");
						}
						
	                }
				}
 
				// end 
					
				  
				dataServiceRestClient.updateSolution(solution);
			}
		} catch (IllegalArgumentException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INVALID_PARAMETER, e.getMessage());
		} catch (HttpClientErrorException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
		}
		return mlSolution;
	}

	public NexusArtifactClient nexusArtifactClient(String nexusUrl, String nexusUserName, String nexusPd) {

	       log.debug("nexusArtifactClient start");

	       RepositoryLocation repositoryLocation = new RepositoryLocation();

	       repositoryLocation.setId("1");

	       repositoryLocation.setUrl(nexusUrl);

	       repositoryLocation.setUsername(nexusUserName);

	       repositoryLocation.setPassword(nexusPd);

	       NexusArtifactClient nexusArtifactClient = new NexusArtifactClient(repositoryLocation);

	       log.debug("nexusArtifactClient End");

	       return nexusArtifactClient;

	}
	
	@Override
	public List<MLPSolutionRevision> getSolutionRevision(String solutionId) throws AcumosServiceException {
		log.debug(EELFLoggerDelegate.debugLogger, "getSolutionRevision`");
		List<MLPSolutionRevision> mlpSolutionRevisions = null;
		try {
			ICommonDataServiceRestClient dataServiceRestClient = getClient();
			mlpSolutionRevisions = dataServiceRestClient.getSolutionRevisions(solutionId);
		} catch (IllegalArgumentException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INVALID_PARAMETER, e.getMessage());
		} catch (HttpClientErrorException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
		}
		return mlpSolutionRevisions;
	}

	@Override
	public List<MLPArtifact> getSolutionArtifacts(String solutionId, String revisionId) throws AcumosServiceException {
		log.debug(EELFLoggerDelegate.debugLogger, "getSolutionArtifacts`");
		List<MLPArtifact> mlpSolutionRevisions = null;
		List<MLPArtifact> artifactList = new ArrayList<MLPArtifact>();
		try {
			ICommonDataServiceRestClient dataServiceRestClient = getClient();
			mlpSolutionRevisions = dataServiceRestClient.getSolutionRevisionArtifacts(solutionId, revisionId);
			if(mlpSolutionRevisions != null) {
				for (MLPArtifact artifact : mlpSolutionRevisions) {
					String[] st = artifact.getUri().split("/");
					String name = st[st.length-1];
					artifact.setName(name);
					artifactList.add(artifact);
				}
			}
		} catch (IllegalArgumentException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INVALID_PARAMETER, e.getMessage());
		} catch (HttpClientErrorException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
		}
		return artifactList;
	}

	@Override
	public void addSolutionTag(String solutionId, String tag) throws AcumosServiceException {
		log.debug(EELFLoggerDelegate.debugLogger, "addSolutionTag`");
		try {
			ICommonDataServiceRestClient dataServiceRestClient = getClient();
			dataServiceRestClient.addSolutionTag(solutionId, tag);
		} catch (IllegalArgumentException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INVALID_PARAMETER, e.getMessage());
		} catch (HttpClientErrorException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}

	@Override
	public void dropSolutionTag(String solutionId, String tag) throws AcumosServiceException {
		log.debug(EELFLoggerDelegate.debugLogger, "addSolutionTag`");
		try {
			ICommonDataServiceRestClient dataServiceRestClient = getClient();
			dataServiceRestClient.dropSolutionTag(solutionId, tag);
		} catch (IllegalArgumentException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INVALID_PARAMETER, e.getMessage());
		} catch (HttpClientErrorException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}

	@Override
	public List<String> getTags(JsonRequest<RestPageRequest> restPageReq) throws AcumosServiceException {
		List<String> mlTagsList = new ArrayList<>();
		List<MLPTag> tagsList = new ArrayList<MLPTag>();
		try {
			ICommonDataServiceRestClient dataServiceRestClient = getClient();

			RestPageResponse<MLPTag> mlpTagsList = dataServiceRestClient.getTags(null);
			//check if there are more tags then default size of 20, if yes then get all again
			if(mlpTagsList.getSize() < mlpTagsList.getTotalElements()) {
				restPageReq.getBody().setPage(0);
				restPageReq.getBody().setSize((int)mlpTagsList.getTotalElements());
				mlpTagsList = dataServiceRestClient.getTags(restPageReq.getBody());
				
			}
			if (mlpTagsList != null && !PortalUtils.isEmptyList(mlpTagsList.getContent())) {
				tagsList = mlpTagsList.getContent();
			}
			for (MLPTag mlpSolutionTag : tagsList) {
				mlTagsList.add(mlpSolutionTag.getTag());
			}
		} catch (IllegalArgumentException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INVALID_PARAMETER, e.getMessage());
		} catch (HttpClientErrorException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
		}
		return mlTagsList;
	}
	
	@Override
	public List<Map<String, String>> getPreferredTagsList(JsonRequest<RestPageRequest> restPageReq, String userId) throws AcumosServiceException {
		List<Map<String, String>> prefTags = new ArrayList<>();
		try {
			Long startTime = System.currentTimeMillis();
			//System.out.println(startTime);
			List<String> userTagsList = new ArrayList<>();	
			ICommonDataServiceRestClient dataServiceRestClient = getClient();
			List<String> mlTagsList = getTags(restPageReq);
			MLPUser userDetails = dataServiceRestClient.getUser(userId);
			Set<MLPTag> userTagSet = userDetails.getTags();
			for(MLPTag userTags : userTagSet){
				userTagsList.add(userTags.getTag());
				Map<String, String> map = new HashMap<>();
				map.put("tagName", userTags.getTag());
				map.put("preferred", "Yes");
				prefTags.add(map);
			}			 
			for (String tag : mlTagsList) {
				Map<String, String> map = new HashMap<>();
				map.put("tagName", tag);
				//Simplifying the code
					if (!userTagsList.contains(tag)) {
						map.put("preferred", "No");
						prefTags.add(map);
					}
					
			}			
			Long endTime = System.currentTimeMillis();
			log.debug("getPreferredTagsList total time took "+(endTime - startTime));
		} catch (IllegalArgumentException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INVALID_PARAMETER, e.getMessage());
		} catch (HttpClientErrorException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
		}
		return prefTags;
	}

	@Override
	public void createUserTag(String userId, List<String> tagList, List<String> dropTagList) throws AcumosServiceException {
		try {
			log.debug(EELFLoggerDelegate.debugLogger, "createUserTag");
			ICommonDataServiceRestClient dataServiceRestClient = getClient();
			if(dropTagList.size()!=0){
				for(String dropTag : dropTagList){ 
					dataServiceRestClient.dropUserTag(userId, dropTag);
				}
			}
			if(tagList.size()!=0){
				for(String tag : tagList){ 
					dataServiceRestClient.addUserTag(userId, tag);
				}			
			}
		} catch (IllegalArgumentException e) { 
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INVALID_PARAMETER, e.getMessage());
		} catch (HttpClientErrorException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}
	@Override
	public List<User> getSolutionUserAccess(String solutionId) throws AcumosServiceException {
		List<User> users = null;
		log.debug(EELFLoggerDelegate.debugLogger, "getSolutionUserAccess");
		try {
			ICommonDataServiceRestClient dataServiceRestClient = getClient();
			List<MLPUser> mlpUsers = dataServiceRestClient.getSolutionAccessUsers(solutionId);
			if (!PortalUtils.isEmptyList(mlpUsers)) {
				users = new ArrayList<>();
				for (MLPUser mlpusers : mlpUsers) {
					User user = PortalUtils.convertToMLPuser(mlpusers);
					users.add(user);
				}
			}
		} catch (IllegalArgumentException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INVALID_PARAMETER, e.getMessage());
		} catch (HttpClientErrorException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
		}
		return users;
	}

	@Override
	public void dropSolutionUserAccess(String solutionId, String userId) throws AcumosServiceException {
		log.debug(EELFLoggerDelegate.debugLogger, "addSolutionUserAccess");
		try {
			ICommonDataServiceRestClient dataServiceRestClient = getClient();
			dataServiceRestClient.dropSolutionUserAccess(solutionId, userId);
		} catch (IllegalArgumentException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INVALID_PARAMETER, e.getMessage());
		} catch (HttpClientErrorException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}

	@Override
	public void incrementSolutionViewCount(String solutionId) throws AcumosServiceException {
		try {
			log.debug(EELFLoggerDelegate.debugLogger, "incrementSolutionViewCount");
			ICommonDataServiceRestClient dataServiceRestClient = getClient();
			dataServiceRestClient.incrementSolutionViewCount(solutionId);
		} catch (IllegalArgumentException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INVALID_PARAMETER, e.getMessage());
		} catch (HttpClientErrorException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}

	@Override
	public MLSolutionRating createSolutionrating(MLPSolutionRating mlpSolutionRating) throws AcumosServiceException {
		MLSolutionRating mlSolutionRating = null;
		try {
			log.debug(EELFLoggerDelegate.debugLogger, "createSolutionrating");
			ICommonDataServiceRestClient dataServiceRestClient = getClient();
			mlSolutionRating = PortalUtils
					.convertToMLSolutionRating(dataServiceRestClient.createSolutionRating(mlpSolutionRating));
		} catch (IllegalArgumentException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INVALID_PARAMETER, e.getMessage());
		} catch (HttpClientErrorException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
		}
		return mlSolutionRating;

	}

	@Override
	public void updateSolutionRating(MLPSolutionRating mlpSolutionRating) throws AcumosServiceException {
		try {
			log.debug(EELFLoggerDelegate.debugLogger, "updateSolutionRating");
			ICommonDataServiceRestClient dataServiceRestClient = getClient();
			dataServiceRestClient.updateSolutionRating(mlpSolutionRating);
		} catch (IllegalArgumentException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INVALID_PARAMETER, e.getMessage());
		} catch (HttpClientErrorException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}

	@Override
	public RestPageResponse<MLPSolutionRating> getSolutionRating(String solutionId, RestPageRequest pageRequest)
			throws AcumosServiceException {
		RestPageResponse<MLPSolutionRating> mlpSolutionRating = null;
		try {
			log.debug(EELFLoggerDelegate.debugLogger, "getSolutionRating");
			ICommonDataServiceRestClient dataServiceRestClient = getClient();
			mlpSolutionRating = dataServiceRestClient.getSolutionRatings(solutionId, pageRequest);
		} catch (IllegalArgumentException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INVALID_PARAMETER, e.getMessage());
		} catch (HttpClientErrorException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
		}
		return mlpSolutionRating;
	}

	@Override
	public List<MLSolution> getMySharedModels(String userId, RestPageRequest restPageReq)
			throws AcumosServiceException {
		List<MLSolution> mlSolutionList = null;
		log.debug(EELFLoggerDelegate.debugLogger, "getSolutionUserAccess");
		try {
			ICommonDataServiceRestClient dataServiceRestClient = getClient();
			RestPageResponse<MLPSolution> mlpSolutions = dataServiceRestClient.getSolutions(restPageReq);
			if (!PortalUtils.isEmptyList(mlpSolutions.getContent())) {
				mlSolutionList = new ArrayList<>();
				for (MLPSolution mlpSolution : mlpSolutions.getContent()) {
					MLSolution mlSolution = PortalUtils.convertToMLSolution(mlpSolution);
					mlSolutionList.add(mlSolution);
				}
			}
		} catch (IllegalArgumentException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INVALID_PARAMETER, e.getMessage());
		} catch (HttpClientErrorException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
		}
		return mlSolutionList;
	}

	@Override
	public RestPageResponseBE<MLSolution> getTagBasedSolutions(String tag, JsonRequest<RestPageRequestBE> restPageReqBe)
			throws AcumosServiceException {

		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		List<MLPSolution> content = new ArrayList<>();
		List<MLSolution> contentML = new ArrayList<>();
		RestPageResponseBE<MLSolution> mlSolutionsRest = new RestPageResponseBE<>(contentML);
		RestPageResponseBE<MLPSolution> mlpSolutionsRest = new RestPageResponseBE<MLPSolution>(content);
		try {
			if (!PortalUtils.isEmptyOrNullString(tag)) {
				log.debug(EELFLoggerDelegate.debugLogger, "getTagSearchedSolutions: searching Solutions with tags:",
						tag);
				RestPageRequest pageRequest = new RestPageRequest();
				RestPageResponse<MLPSolution> pageResponse = new RestPageResponse<>();
				// RestPageRequest pageRequest = restPageReqBe.getBody();
				pageResponse = dataServiceRestClient.findSolutionsByTag(tag, pageRequest);

				for (MLPSolution mlpSolution : pageResponse.getContent()) {
					MLSolution mlSolution = PortalUtils.convertToMLSolution(mlpSolution);
					// contentML.add(mlSolution);
					mlSolutionsRest.getContent().add(mlSolution);
				}
			}
		} catch (IllegalArgumentException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INVALID_PARAMETER, e.getMessage());
		} catch (HttpClientErrorException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
		}
		return mlSolutionsRest;
	}

	@Override
	public MLSolutionFavorite createSolutionFavorite(MLPSolutionFavorite mlpSolutionFavorite)
			throws AcumosServiceException {
		MLSolutionFavorite mlSolutionFavorite = null;
		try {
			log.debug(EELFLoggerDelegate.debugLogger, "createSolutionFavorite");
			ICommonDataServiceRestClient dataServiceRestClient = getClient();
			mlSolutionFavorite = PortalUtils
					.convertToMLSolutionFavorite(dataServiceRestClient.createSolutionFavorite(mlpSolutionFavorite));
		} catch (IllegalArgumentException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INVALID_PARAMETER, e.getMessage());
		} catch (HttpClientErrorException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
		}
		return mlSolutionFavorite;

	}

	@Override
	public void deleteSolutionFavorite(MLPSolutionFavorite mlpSolutionFavorite) throws AcumosServiceException {
		try {
			log.debug(EELFLoggerDelegate.debugLogger, "deleteSolutionFavorite");
			ICommonDataServiceRestClient dataServiceRestClient = getClient();
			dataServiceRestClient.deleteSolutionFavorite(mlpSolutionFavorite);
		} catch (IllegalArgumentException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INVALID_PARAMETER, e.getMessage());
		} catch (HttpClientErrorException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}

	@Override
	public List<MLSolution> getFavoriteSolutions(String userId, RestPageRequest restPageReq)
			throws AcumosServiceException {
		List<MLSolution> mlSolutionList = new ArrayList<>();
		try {
			log.debug(EELFLoggerDelegate.debugLogger, "getFavoriteSolutions : ");
			ICommonDataServiceRestClient dataServiceRestClient = getClient();
			RestPageResponse<MLPSolution> mlpSolutionsFav = dataServiceRestClient.getFavoriteSolutions(userId,
					restPageReq);

			if (mlpSolutionsFav != null) {
				for (MLPSolution mlpSolution : mlpSolutionsFav.getContent()) {
					MLSolution mlSolution = PortalUtils.convertToMLSolution(mlpSolution);
					mlSolutionList.add(mlSolution);
				}
			}
		} catch (IllegalArgumentException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INVALID_PARAMETER, e.getMessage());
		} catch (HttpClientErrorException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
		}
		return mlSolutionList;

	}

	@Override
	public RestPageResponseBE<MLSolution> getRelatedMySolutions(JsonRequest<RestPageRequestBE> restPageReqBe)
			throws AcumosServiceException {
		log.debug(EELFLoggerDelegate.debugLogger, "getRealtedMySolutions");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		Map<String, String> queryParameters = new HashMap<>();
		RestPageResponse<MLPSolution> mlpSolutionsRest = null;
		List<MLSolution> content = new ArrayList<>();
		RestPageResponseBE<MLSolution> mlSolutionsRest = new RestPageResponseBE<>(content);
		try {
			if (restPageReqBe != null && restPageReqBe.getBody() != null) {
				RestPageRequest pageRequest = new RestPageRequest();

				// ----------------------------------------------------------

				List<MLPSolution> filteredMLPSolutions = new ArrayList<>();
				List<MLPSolution> filteredMLPSolutionsTemp = new ArrayList<>();
				List<MLPSolution> originalSolutionsList = new ArrayList<MLPSolution>();
				int pageSize = 0;
				int index = 0;
				int interateCopy = 0;
				while (restPageReqBe.getBody().getSize().intValue() != filteredMLPSolutions.size()) {
					pageSize = pageSize + restPageReqBe.getBody().getPage();

					/*
					 * if(restPageReqBe.getBody().getPage()!=null) { pageRequest.setPage(pageSize);
					 * } else { //default to 0 pageRequest.setPage(0); }
					 * if(restPageReqBe.getBody().getSize()!=null &&
					 * restPageReqBe.getBody().getSize() > 0) {
					 * pageRequest.setSize(restPageReqBe.getBody().getSize()); }
					 */

					// code addition to display models as per timestamp
					queryParameters.put("created", "DESC");
					if (restPageReqBe.getBody().getPage() != null && restPageReqBe.getBody().getSize() != null) {
						pageRequest = new RestPageRequest(pageSize, restPageReqBe.getBody().getSize(), queryParameters);
					}

					// TODO Need to revisit the Sorting logic once Common Data
					// Service Client

					// 1. Check if searchTerm exists, if yes then use
					// findSolutionsBySearchTerm
					if (!PortalUtils.isEmptyOrNullString(restPageReqBe.getBody().getSearchTerm())) {
						log.debug(EELFLoggerDelegate.debugLogger,
								"getSearchedSolutions: searching Solutions with searcTerm:",
								restPageReqBe.getBody().getSearchTerm());
						mlpSolutionsRest = dataServiceRestClient.findSolutionsBySearchTerm(
								restPageReqBe.getBody().getSearchTerm(),
								new RestPageRequest(pageSize, restPageReqBe.getBody().getSize(), queryParameters));
					} else {
						// 2. If searchTerm does not exists, get all the
						// Solutions

						pageRequest.setFieldToDirectionMap(queryParameters);
						mlpSolutionsRest = dataServiceRestClient.getSolutions(
								new RestPageRequest(pageSize, restPageReqBe.getBody().getSize(), queryParameters));
					}

					// 3. Filter the RestPageResponse to use only Published,
					// ValitionStatuCode as PS and Active Solutions and
					// ModelType/ModelToolkitType

					originalSolutionsList = mlpSolutionsRest.getContent();
					if (mlpSolutionsRest.getContent().size() == 0)
						break;
					filteredMLPSolutionsTemp = originalSolutionsList;
					// filteredMLPSolutionsTemp =
					// originalSolutionsList.stream().filter(mlpSolution ->
					// (!PortalUtils.isEmptyOrNullString(mlpSolution.getOwnerId())
					// &&
					// userId.equalsIgnoreCase(mlpSolution.getOwnerId()))).collect(Collectors.toList());

					/*
					 * if(!PortalUtils.isEmptyOrNullString(restPageReqBe.getBody ().
					 * getAccessType())) { filteredMLPSolutionsTemp =
					 * filteredMLPSolutionsTemp.stream().filter(mlpSolution ->
					 * (PortalUtils.isEmptyOrNullString(mlpSolution. getAccessTypeCode())
					 * ||(!PortalUtils.isEmptyOrNullString(mlpSolution. getAccessTypeCode()) &&
					 * restPageReqBe.getBody().getAccessType().contains( mlpSolution.
					 * getAccessTypeCode()))) ).collect(Collectors.toList()); }
					 * 
					 * if(!PortalUtils.isEmptyOrNullString(restPageReqBe.getBody ().
					 * getModelToolkitType())) { filteredMLPSolutionsTemp =
					 * filteredMLPSolutionsTemp.stream().filter(mlpSolution ->
					 * (PortalUtils.isEmptyOrNullString(mlpSolution. getToolkitTypeCode())
					 * ||(!PortalUtils.isEmptyOrNullString(mlpSolution. getToolkitTypeCode()) &&
					 * restPageReqBe.getBody().getModelToolkitType().contains(
					 * mlpSolution.getToolkitTypeCode())))).collect(Collectors. toList()); }
					 */

					if (!PortalUtils.isEmptyOrNullString(restPageReqBe.getBody().getModelType())) {
						filteredMLPSolutionsTemp = filteredMLPSolutionsTemp.stream().filter(mlpSolution -> (PortalUtils
								.isEmptyOrNullString(mlpSolution.getModelTypeCode())
								|| (!PortalUtils.isEmptyOrNullString(mlpSolution.getModelTypeCode()) && restPageReqBe
										.getBody().getModelType().contains(mlpSolution.getModelTypeCode()))))
								.collect(Collectors.toList());
					}

					/*
					 * if(!PortalUtils.isEmptyOrNullString(restPageReqBe.getBody ().
					 * getActiveType())) { Boolean isActive =false;
					 * if(restPageReqBe.getBody().getActiveType(). equalsIgnoreCase( "Y")){ isActive
					 * = true; }else if(restPageReqBe.getBody().getActiveType(). equalsIgnoreCase(
					 * "N")){ isActive = false; } filteredMLPSolutionsTemp =
					 * filteredMLPSolutionsTemp.stream().filter(mlpSolution ->
					 * Boolean.compare(restPageReqBe.getBody().getActiveType().
					 * equalsIgnoreCase("Y"),
					 * mlpSolution.isActive())==0).collect(Collectors.toList()); }
					 */

					/*
					 * for(int k=0;k<filteredMLPSolutionsTemp.size()&& index<9;k++){
					 * filteredMLPSolutions.add(index, filteredMLPSolutionsTemp.get(k)); index++;
					 * if(filteredMLPSolutionsTemp.size()==index) break; }
					 */

					for (int k = 0; k < filteredMLPSolutionsTemp.size() && index < 9; k++) {
						if (filteredMLPSolutions.size() > 0) {
							for (int j = 0; j < filteredMLPSolutions.size(); j++) {
								boolean checkTemp = false;
								if (filteredMLPSolutionsTemp.get(k).getSolutionId() != null
										&& filteredMLPSolutions.get(j).getSolutionId() != null) {
									if (!filteredMLPSolutionsTemp.get(k).getSolutionId()
											.equalsIgnoreCase(filteredMLPSolutions.get(j).getSolutionId())) {

										for (int n = 0; n < filteredMLPSolutions.size(); n++) {
											if (filteredMLPSolutionsTemp != null && filteredMLPSolutions != null) {
												if (!filteredMLPSolutionsTemp.get(k).getSolutionId().equalsIgnoreCase(
														filteredMLPSolutions.get(n).getSolutionId())) {
													checkTemp = true;
												} else if (filteredMLPSolutionsTemp.get(k).getSolutionId()
														.equalsIgnoreCase(
																filteredMLPSolutions.get(n).getSolutionId())) {
													checkTemp = false;
													break;
												}
											} else if (filteredMLPSolutionsTemp == null
													&& filteredMLPSolutions == null) {
												break;
											}
										}
										if (checkTemp) {
											filteredMLPSolutions.add(index, filteredMLPSolutionsTemp.get(k));
											index++;
										}

									} else if (filteredMLPSolutionsTemp.get(k).getSolutionId()
											.equalsIgnoreCase(filteredMLPSolutions.get(j).getSolutionId())) {
										interateCopy++;
									}
								}
							}
						} else if (filteredMLPSolutions.size() == 0) {
							filteredMLPSolutions.add(index, filteredMLPSolutionsTemp.get(k));
							index++;
						}
					}

					if (mlpSolutionsRest.getContent().size() == filteredMLPSolutions.size())
						break;
					else if (interateCopy == filteredMLPSolutions.size() && interateCopy != 0)
						break;

					pageSize++;
				}
				// ----------------------------------------------------------
				String userFirstName = "";
				String userLastName = "";
				String userName = "";
				Set<String> filteredTagSet = new HashSet<>();

				if (!PortalUtils.isEmptyList(filteredMLPSolutions)) {
					int i = 0;
					for (MLPSolution mlpSol : filteredMLPSolutions) {

						MLSolution mlSolution = PortalUtils.convertToMLSolution(mlpSol);
						// Identify the OwnerName for each solution
						MLPUser user = dataServiceRestClient.getUser(mlpSol.getUserId());
						if (user != null) {

							// Lets loop through other solutions
							userFirstName = user.getFirstName();
							userLastName = user.getLastName();
							if (!PortalUtils.isEmptyOrNullString(user.getFirstName())) {
								userName = userFirstName;
								if (!PortalUtils.isEmptyOrNullString(user.getLastName())) {
									userName = userName + " " + user.getLastName();
								}
							}

							mlSolution.setOwnerName(userName);
						}

						List<MLPTag> tagList = dataServiceRestClient
								.getSolutionTags(filteredMLPSolutions.get(i).getSolutionId());
						if (tagList.size() > 0) {
							for (MLPTag tag : tagList) {
								filteredTagSet.add(tag.getTag());
							}
							mlSolution.setSolutionTagList(tagList);
						}
						content.add(mlSolution);
						i++;
					}
					mlSolutionsRest = new RestPageResponseBE<>(content);
				}
//				mlpSolutionsRest.setNumberOfElements(filteredMLPSolutions.size());
				mlSolutionsRest.setFilteredTagSet(filteredTagSet);
			}
		} catch (IllegalArgumentException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INVALID_PARAMETER, e.getMessage());
		} catch (HttpClientErrorException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
		}
		return mlSolutionsRest;
	}

	@Override
	public void addSolutionUserAccess(String solutionId, List<String> userList) throws AcumosServiceException {
		try {
			log.debug(EELFLoggerDelegate.debugLogger, "addSolutionUserAccess");
			ICommonDataServiceRestClient dataServiceRestClient = getClient();
			for (String userId : userList) {
				dataServiceRestClient.addSolutionUserAccess(solutionId, userId);
			}
		} catch (IllegalArgumentException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INVALID_PARAMETER, e.getMessage());
		} catch (HttpClientErrorException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}

	@Override
	public MLPTag createTag(MLPTag tag) throws AcumosServiceException {
		MLPTag mlpTag = null;
		try {
			log.debug(EELFLoggerDelegate.debugLogger, "createTag");
			ICommonDataServiceRestClient dataServiceRestClient = getClient();
			mlpTag = dataServiceRestClient.createTag(tag);
		} catch (IllegalArgumentException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INVALID_PARAMETER, e.getMessage());
		} catch (HttpClientErrorException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
		}
		return mlpTag;
	}

	@Override
	public MLPSolutionRating getUserRatings(String solutionId, String userId) {
		log.debug(EELFLoggerDelegate.debugLogger, "addSolutionUserAccess");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		MLPSolutionRating rating = dataServiceRestClient.getSolutionRating(solutionId, userId);
		return rating;
	}

	@Override
	public RestPageResponseBE<MLSolution> findPortalSolutions(RestPageRequestPortal pageReqPortal, Set<MLPTag> preferredTags) {
		log.debug(EELFLoggerDelegate.debugLogger, "findPortalSolutions(pageReqPortal, prefTags");

		Set<String> mergedTags = new HashSet<>();
		
			if (pageReqPortal.getTags() != null && pageReqPortal.getTags().length > 0) {
				mergedTags = new HashSet<String>(Arrays.asList(pageReqPortal.getTags()));
			}
			
			if (preferredTags != null && !preferredTags.isEmpty()) {
				for (MLPTag prefTag : preferredTags) {
					mergedTags.add(prefTag.getTag());
				}
			}
		pageReqPortal.setTags(mergedTags.toArray(new String[mergedTags.size()]));
		
		return findPortalSolutions(pageReqPortal);
	}

	@Override
	public RestPageResponseBE<MLSolution> findPortalSolutions(RestPageRequestPortal pageReqPortal) {
		log.debug(EELFLoggerDelegate.debugLogger, "findPortalSolutions");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		String[] accessTypeCodes = pageReqPortal.getAccessTypeCodes();
		RestPageResponse<MLPSolution> response = dataServiceRestClient.findPortalSolutions(
				pageReqPortal.getNameKeyword(), pageReqPortal.getDescriptionKeyword(), pageReqPortal.isActive(),
				pageReqPortal.getOwnerIds(), accessTypeCodes, pageReqPortal.getModelTypeCodes(),
				pageReqPortal.getTags(), null, null, pageReqPortal.getPageRequest());

		List<MLSolution> content = new ArrayList<>();
		RestPageResponseBE<MLSolution> mlSolutionsRest = new RestPageResponseBE<>(content);

		if (response.getContent() != null) {
			mlSolutionsRest = fetchDetailsForSolutions(response.getContent(), pageReqPortal);
			mlSolutionsRest.setPageCount(response.getTotalPages());
			mlSolutionsRest.setTotalElements((int)response.getTotalElements());
		}
		return mlSolutionsRest;
	}

	@Override
	public RestPageResponseBE<MLSolution> searchSolutionsByKeyword(RestPageRequestPortal pageReqPortal) {
		log.debug(EELFLoggerDelegate.debugLogger, "findPortalSolutions");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		String[] accessTypeCodes = pageReqPortal.getAccessTypeCodes();
		
		/*RestPageResponse<MLPSolution> response = dataServiceRestClient.findPortalSolutionsByKw(pageReqPortal.getNameKeyword(), true, null,
				accessTypeCodes, null, null, pageReqPortal.getPageRequest());*/

		RestPageResponse<MLPSolution> response = dataServiceRestClient.findPortalSolutionsByKwAndTags(pageReqPortal.getNameKeyword(), 
				pageReqPortal.isActive(), pageReqPortal.getOwnerIds(),
				accessTypeCodes, pageReqPortal.getModelTypeCodes(), pageReqPortal.getTags(), null, null, pageReqPortal.getPageRequest());
		
		List<MLSolution> content = new ArrayList<>();
		RestPageResponseBE<MLSolution> mlSolutionsRest = new RestPageResponseBE<>(content);

		if (response.getContent() != null) {
			mlSolutionsRest = fetchDetailsForSolutions(response.getContent(), pageReqPortal);
			mlSolutionsRest.setPageCount(response.getTotalPages());
			mlSolutionsRest.setTotalElements((int)response.getTotalElements());
		}
		return mlSolutionsRest;
	}

	private RestPageResponseBE<MLSolution> fetchDetailsForSolutions(List<MLPSolution> mlpSolList, RestPageRequestPortal pageReqPortal) {
		log.debug(EELFLoggerDelegate.debugLogger, "fetchDetailsForSolution");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		List<MLSolution> content = new ArrayList<>();
		RestPageResponseBE<MLSolution> mlSolutionsRest = new RestPageResponseBE<>(content);
		Set<String> filteredTagSet = new HashSet<>();
		List<MLPSolution> filteredSolList = new ArrayList<>();

		//List<MLPSolution> mlpSolList = response.getContent();
		filteredSolList.addAll(mlpSolList);

		for (MLPSolution mlpSol : filteredSolList) {

			//CDS does not return the picture in list of solution. So we need to fetch the solution separately and then populate the picture
			MLPSolution sol = dataServiceRestClient.getSolution(mlpSol.getSolutionId());
			MLSolution mlSolution = PortalUtils.convertToMLSolution(sol);

			// add tags for models
			List<MLPTag> tagList = dataServiceRestClient.getSolutionTags(mlSolution.getSolutionId());
			if (tagList.size() > 0) {
				for (MLPTag tag : tagList) {
					filteredTagSet.add(tag.getTag());
				}
				mlSolution.setSolutionTagList(tagList);
			}

			// set owner name for model
			MLPUser userDetails = userService.findUserByUserId(mlSolution.getOwnerId());
			mlSolution.setOwnerName(userDetails.getFirstName() + " " + userDetails.getLastName());

			// get shared users for model
			try {
				List<User> users = null;
				List<MLPUser> mlpUsersList = dataServiceRestClient
						.getSolutionAccessUsers(mlSolution.getSolutionId());
				if (!PortalUtils.isEmptyList(mlpUsersList)) {
					users = new ArrayList<>();
					for (MLPUser mlpusers : mlpUsersList) {
						User user = PortalUtils.convertToMLPuser(mlpusers);
						users.add(user);
					}
				}
				mlSolution.setOwnerListForSol(users);
			} catch (Exception e) {
				log.error(EELFLoggerDelegate.errorLogger, "No co-owner for SolutionId={}",
						mlSolution.getSolutionId());
			}

			//To categorize the solution on display fetch latest revision and add the access type code 
			MLPSolutionRevision revision = getLatestSolRevision(mlpSol.getSolutionId(), pageReqPortal.getAccessTypeCodes());
			if (revision != null) {
				mlSolution.setAccessType(revision.getAccessTypeCode());
				mlSolution.setLatestRevisionId(revision.getRevisionId());
				mlSolution.setPublisher(revision.getPublisher());
				List<Author> authors = PortalUtils.convertToAuthor(revision.getAuthors());
				mlSolution.setAuthors(authors);
				long Count=dataServiceRestClient.getSolutionRevisionCommentCount(mlpSol.getSolutionId(), revision.getRevisionId());
                		mlSolution.setCommentsCount(Count);
			}

			// get latest step Result for solution
			Boolean onboardingStatusFailed = false;
			MLPStepResult stepResult = null;
			Map<String, Object> stepResultCriteria = new HashMap<String, Object>();
			stepResultCriteria.put("solutionId", mlpSol.getSolutionId());
			
			Map<String, String> queryParameters = new HashMap<>();
			queryParameters.put("startDate", "DESC");
			//Fetch latest step result for the solution to get the tracking id
			RestPageResponse<MLPStepResult> stepResultResponse =  dataServiceRestClient.searchStepResults(stepResultCriteria, false, new RestPageRequest(0, 1, queryParameters));
			List<MLPStepResult> stepResultList = stepResultResponse.getContent();
			String errorStatusDetails = null;
			if (stepResultList != null && !PortalUtils.isEmptyList(stepResultList)) {
				stepResult = stepResultList.get(0);
				String trackingId = stepResult.getTrackingId();
				
				//search all step results with the tracking id 
				Map<String, String> fieldToDirmap = new HashMap<>();
				
				Map<String, Object> trackingResultCriteria = new HashMap<String, Object>();
				trackingResultCriteria.put("trackingId", trackingId);
				RestPageResponse<MLPStepResult> trackingStepResult =  dataServiceRestClient.searchStepResults(trackingResultCriteria, false, new RestPageRequest(0, 25, fieldToDirmap));
				List<MLPStepResult> trackingStepResultList = trackingStepResult.getContent();
				
				
				if (trackingStepResultList != null && !PortalUtils.isEmptyList(trackingStepResultList)) {
					// check if any of the step result is Failed
					for(MLPStepResult step : trackingStepResultList) {
						if(STEP_STATUS_FAILED.equals(step.getStatusCode())) {
							onboardingStatusFailed = true;
							errorStatusDetails=step.getResult();
							break;
						}
					}
				}
			}
			mlSolution.setOnboardingStatusFailed(onboardingStatusFailed);
			if(errorStatusDetails!=null) {
				mlSolution.setErrorDetails(errorStatusDetails);
			}
			//Search for pending Approvals
			if(mlSolution.getSolutionId() !=null && mlSolution.getLatestRevisionId() !=null){
				boolean pendingApproval = dataServiceRestClient.isPublishRequestPending(mlSolution.getSolutionId(), mlSolution.getLatestRevisionId());
				mlSolution.setPendingApproval(pendingApproval);
			}

			content.add(mlSolution);
		}

		mlSolutionsRest = new RestPageResponseBE<>(content);
		mlSolutionsRest.setFilteredTagSet(filteredTagSet);

		return mlSolutionsRest;
	}

	@Override
	public RestPageResponseBE<MLSolution> findUserSolutions(RestPageRequestPortal pageReqPortal) {
		log.debug(EELFLoggerDelegate.debugLogger, "findUserSolutions");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		String[] accessTypeCodes = pageReqPortal.getAccessTypeCodes();
		RestPageResponse<MLPSolution> response = dataServiceRestClient.findUserSolutions(
				pageReqPortal.getNameKeyword(), pageReqPortal.getDescriptionKeyword(), pageReqPortal.isActive(),
				pageReqPortal.getUserId(), accessTypeCodes, pageReqPortal.getModelTypeCodes(),
				pageReqPortal.getTags(), pageReqPortal.getPageRequest());

		List<MLSolution> content = new ArrayList<>();
		RestPageResponseBE<MLSolution> mlSolutionsRest = new RestPageResponseBE<>(content);

		if (response.getContent() != null) {
			mlSolutionsRest = fetchDetailsForSolutions(response.getContent(), pageReqPortal);
			mlSolutionsRest.setPageCount(response.getTotalPages());
			mlSolutionsRest.setTotalElements((int)response.getTotalElements());
		}

		return mlSolutionsRest;
	}

	private MLPSolutionRevision getLatestSolRevision(String solutionId, String[] accessTypeCode) {
		log.debug(EELFLoggerDelegate.debugLogger, "getLatestSolRevision");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		
		//for inactive solutions no accessTypeCode is required
		if(accessTypeCode == null) {
			accessTypeCode = new String[]{};
		}
		List<String> category = Arrays.asList(accessTypeCode);
		
		MLPSolutionRevision revision = null;
		List<MLPSolutionRevision> revisions = dataServiceRestClient.getSolutionRevisions(solutionId);
		if (revisions != null) {
			//Sort revision according to created date
			Collections.sort(revisions, new Comparator<MLPSolutionRevision>() {
				public int compare(MLPSolutionRevision m1, MLPSolutionRevision m2) {
					return m2.getCreated().compareTo(m1.getCreated());
				}
			});
			//fetch the latest revision according to accessTypeCode
			for(MLPSolutionRevision solutionRevision : revisions) {
				if(category.contains(solutionRevision.getAccessTypeCode())) {
					revision = solutionRevision;
					break;
				}
			}
		}
		//for deleted solutions no access type code is required from the front end. So assign the latest version
		if(revision == null && revisions != null && revisions.size() >0) {
			revision = revisions.get(0);
		}
		return revision;
	}

	@Override
	public RestPageResponse<MLPSolution> getUserAccessSolutions(String userId, RestPageRequest pageRequest) {
		log.debug(EELFLoggerDelegate.debugLogger, "findPortalSolutions");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		RestPageResponse<MLPSolution> mlSolutions = dataServiceRestClient.getUserAccessSolutions(userId, pageRequest);
		return mlSolutions;
	}

	/*@Override
	public MLSolutionWeb getSolutionWebMetadata(String solutionId) {
		log.debug(EELFLoggerDelegate.debugLogger, "getSolutionWebMetadata");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		MLPSolutionWeb mlpSolutionweb = dataServiceRestClient.getSolutionWebMetadata(solutionId);
				
		MLSolutionWeb mlSolutionweb = PortalUtils.convertToMLSolutionWeb(mlpSolutionweb);
		float avgRating = mlSolutionweb.getRatingAverageTenths() / 10;
		mlSolutionweb.setRatingAverageTenths(avgRating);
		
		
		return mlSolutionweb;
	}*/

	@Override
	public List<Author> getSolutionRevisionAuthors(String solutionId, String revisionId) {
		log.debug(EELFLoggerDelegate.debugLogger, "getSolutionRevisionAuthors");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		MLPSolutionRevision revision = dataServiceRestClient.getSolutionRevision(solutionId, revisionId);
		AuthorTransport[] authorTransport =  revision.getAuthors();
		List<Author> authors = PortalUtils.convertToAuthor(authorTransport);
		return authors;
	}

	@Override
	public List<Author> addSolutionRevisionAuthors(String solutionId, String revisionId, Author author) throws AcumosServiceException {
		log.debug(EELFLoggerDelegate.debugLogger, "addSolutionRevisionAuthors");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		AuthorTransport newAuthor = new AuthorTransport(author.getName(), author.getContact());
		MLPSolutionRevision revision = dataServiceRestClient.getSolutionRevision(solutionId, revisionId);
		AuthorTransport[] authorTransport =  revision.getAuthors();
		for (AuthorTransport authorT : authorTransport) {
			if (newAuthor.equals(authorT)) {
				throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR, "Author already exists in the list.");
			}
		}
		ArrayList<AuthorTransport> authorTransportList = new ArrayList<AuthorTransport>(Arrays.asList(authorTransport));
		authorTransportList.add(newAuthor);

		List<Author> updatedAuthor = new ArrayList<>();
		try {
			updatedAuthor = updateRevisionAuthors(revision, authorTransportList);
		} catch (Exception e) {
			log.error(EELFLoggerDelegate.errorLogger, e.getMessage(), e);
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR, "Internal Server Error");
		}

		return updatedAuthor;
	}

	@Override
	public List<Author> removeSolutionRevisionAuthors(String solutionId, String revisionId, Author author) {
		log.debug(EELFLoggerDelegate.debugLogger, "removeSolutionRevisionAuthors");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		AuthorTransport removeAuthor = new AuthorTransport(author.getName(), author.getContact());
		MLPSolutionRevision revision = dataServiceRestClient.getSolutionRevision(solutionId, revisionId);
		AuthorTransport[] authorTransport =  revision.getAuthors();
		
		ArrayList<AuthorTransport> authorTransportList = new ArrayList<AuthorTransport>();
		for (AuthorTransport authorT : authorTransport) {
			if (!removeAuthor.equals(authorT)) {
				authorTransportList.add(authorT);
			}
		}

		return updateRevisionAuthors(revision, authorTransportList);
	}

	private List<Author> updateRevisionAuthors(MLPSolutionRevision revision, ArrayList<AuthorTransport> authorTransportList) {
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		AuthorTransport[] authorData= authorTransportList.toArray(new AuthorTransport[0]);
		revision.setAuthors(authorData);
		dataServiceRestClient.updateSolutionRevision(revision);
		List<Author> authors = PortalUtils.convertToAuthor(authorData);
		return authors;
	}

	private ByteArrayOutputStream getPayload(String uri) throws AcumosServiceException {

		NexusArtifactClient artifactClient = getNexusClient();
		
		ByteArrayOutputStream outputStream = null;
		try {
			outputStream = artifactClient.getArtifact(uri);
		} catch (Exception ex) {
			
			log.error(EELFLoggerDelegate.errorLogger, " Exception in getPayload() ", ex);
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.OBJECT_STREAM_EXCEPTION, ex.getMessage());
			
		}
		return outputStream;
	}
	
	@Override
	public String getProtoUrl(String solutionId, String version, String artifactType, String fileExtension) throws AcumosServiceException {
		log.debug(EELFLoggerDelegate.debugLogger, "getProtoUrl() : Begin");

		String result = "";

		List<MLPSolutionRevision> mlpSolutionRevisionList;
		String solutionRevisionId = null;
		ByteArrayOutputStream byteArrayOutputStream = null;
		List<MLPArtifact> mlpArtifactList;
		try {
			// 1. Get the list of SolutionRevision for the solutionId.
			mlpSolutionRevisionList = getSolutionRevision(solutionId);

			// 2. Match the version with the SolutionRevision and get the
			// solutionRevisionId.
			if (null != mlpSolutionRevisionList && !mlpSolutionRevisionList.isEmpty()) {
				solutionRevisionId = mlpSolutionRevisionList.stream().filter(mlp -> mlp.getVersion().equals(version))
						.findFirst().get().getRevisionId();
				log.debug(EELFLoggerDelegate.debugLogger,
						" SolutionRevisonId for Version :  {} ", solutionRevisionId );
			}
		} catch (NoSuchElementException | NullPointerException e) {
			log.error(EELFLoggerDelegate.errorLogger,
					"Error : Exception in getProtoUrl() : Failed to fetch the Solution Revision Id",
					e);
			throw new NoSuchElementException("Failed to fetch the Solution Revision Id of the solutionId for the user");
		} 
		
		if (null != solutionRevisionId) {
			// 3. Get the list of Artifact for the SolutionId and SolutionRevisionId.
			mlpArtifactList = getSolutionArtifacts(solutionId, solutionRevisionId);
			String nexusURI = "";
			if (null != mlpArtifactList && !mlpArtifactList.isEmpty()) {
				try {
					nexusURI = mlpArtifactList.stream()
							.filter(mlpArt -> mlpArt.getArtifactTypeCode().equalsIgnoreCase(artifactType)).findFirst()
							.get().getUri();
					for(MLPArtifact mlpArt : mlpArtifactList){
						if( null != fileExtension ){
							if(mlpArt.getArtifactTypeCode().equalsIgnoreCase(artifactType) && mlpArt.getName().contains(fileExtension)){
								nexusURI = mlpArt.getUri();
								break;
							}
						}
					}

					log.debug(EELFLoggerDelegate.debugLogger, " Nexus URI :  {} ", nexusURI );

					if (null != nexusURI) {
						byteArrayOutputStream = getPayload(nexusURI);
						log.debug(EELFLoggerDelegate.debugLogger,
								" Response in String Format :  {} ", byteArrayOutputStream.toString() );
						result = byteArrayOutputStream.toString();
					}
				} catch (NoSuchElementException | NullPointerException e) {
					log.error(EELFLoggerDelegate.errorLogger,
							"Error : Exception in getProtoUrl() : Failed to fetch the artifact URI for artifactType",
							e);
					throw new NoSuchElementException(
							"Could not search the artifact URI for artifactType " + artifactType);
				} finally {
					try {
						if (byteArrayOutputStream != null) {
							byteArrayOutputStream.close();
						}
					} catch (IOException e) {
						log.error(EELFLoggerDelegate.errorLogger,
								"Error : Exception in getProtoUrl() : Failed to close the byteArrayOutputStream", e);
						throw new AcumosServiceException(AcumosServiceException.ErrorCode.IO_EXCEPTION, e.getMessage());
					}
				}
			}
		}
		log.debug(EELFLoggerDelegate.debugLogger, "getProtoUrl() : End");
		
		return result;
	}
	
	@Override
	public boolean checkUniqueSolName(String solutionId, String solName) {
		log.debug(EELFLoggerDelegate.debugLogger, "checkUniqueSolName ={}", solutionId);
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		String[] accessTypeCodes = { CommonConstants.PUBLIC/*, CommonConstants.ORGANIZATION*/ };

		//Check only if user tries to change the name or publish the solution from private to public /org
		MLPSolution oldSolution = dataServiceRestClient.getSolution(solutionId);
		if(!solName.equalsIgnoreCase(oldSolution.getName())) {
			String[] name = { solName };

			Map<String, String> queryParameters = new HashMap<>();
			//Fetch the maximum possible records. Need an api that could return the exact match of names along with other nested filter criteria
			RestPageResponse<MLPSolution> searchSolResp = dataServiceRestClient.findPortalSolutions(name, null, true, null,
					accessTypeCodes, null, null, null, null, new RestPageRequest(0, 10000, queryParameters));
			List<MLPSolution> searchSolList = searchSolResp.getContent();
	
			//removing the same solutionId from the list
			List<MLPSolution> filteredSolList1 = searchSolList.stream()
					.filter(searchSol -> !searchSol.getSolutionId().equalsIgnoreCase(solutionId))
					.collect(Collectors.toList());
			
			//Consider only those records that have exact match with the solution name
			List<MLPSolution> filteredSolList = filteredSolList1.stream()
					.filter(searchSol -> searchSol.getName().equalsIgnoreCase(solName))
					.collect(Collectors.toList());

			if (!filteredSolList.isEmpty()) {
				return false;
			}
		}

		return true;
	}

	@Override
	public MLPDocument addRevisionDocument(String solutionId, String revisionId, String accessType, String userId,
			MultipartFile file) throws AcumosServiceException {

		long size = file.getSize();
		String name = FilenameUtils.getBaseName(file.getOriginalFilename());
		String extension = FilenameUtils.getExtension(file.getOriginalFilename());
		ICommonDataServiceRestClient dataServiceRestClient = getClient();

		if(PortalUtils.isEmptyOrNullString(extension))
			throw new IllegalArgumentException("Incorrect file extension.");

		//Check if docuemtn already exists with the same name
		List<MLPDocument> documents = dataServiceRestClient.getSolutionRevisionDocuments(revisionId, accessType);
		for (MLPDocument doc : documents) {
			if (doc.getName().equalsIgnoreCase(name)) {
				log.error(EELFLoggerDelegate.errorLogger,
						"Document Already exists with the same name.");
				throw new AcumosServiceException(AcumosServiceException.ErrorCode.IO_EXCEPTION, "Document Already exists with the same name.");
			}
		}

		//first try to upload the file to nexus. If successful then only create the c_document record in db
		NexusArtifactClient nexusClient = getNexusClient();
		UploadArtifactInfo uploadInfo = null;
		MLPDocument document = null;
		try {
			try {
				uploadInfo = nexusClient.uploadArtifact(getNexusGroupId(solutionId, revisionId), name, accessType, extension, size, file.getInputStream());
			} catch (ConnectionException | IOException | AuthenticationException | AuthorizationException | TransferFailedException | ResourceDoesNotExistException e) {
				log.error(EELFLoggerDelegate.errorLogger,
						"Failed to upload the document", e);
				throw new AcumosServiceException(AcumosServiceException.ErrorCode.IO_EXCEPTION, e.getMessage());
			}
			
			if (uploadInfo != null) {
				/*document = new MLPDocument(null, name,
						uploadInfo.getArtifactMvnPath(), (int) size, "1628acd3-37d6-4c53-a722-0396d0590235");*/
				document = new MLPDocument();
				document.setName(file.getOriginalFilename());
				document.setUri(uploadInfo.getArtifactMvnPath());
				document.setSize((int) size);
				document.setUserId(userId);
				document = dataServiceRestClient.createDocument(document);
				
				dataServiceRestClient.addSolutionRevisionDocument(revisionId, accessType, document.getDocumentId());
			} else {
				log.error(EELFLoggerDelegate.errorLogger,
						"Cannot upload the Document to the specified path");
				throw new AcumosServiceException(AcumosServiceException.ErrorCode.IO_EXCEPTION, "Cannot upload the Document to the specified path");
			}
		} catch (Exception e) {
			log.error(EELFLoggerDelegate.errorLogger, "Exception during addRevisionDocument ={}", e);
			throw new AcumosServiceException(e.getMessage());
		}
		return document;
	}

	@Override
	public MLPDocument removeRevisionDocument(String solutionId, String revisionId, String accessType, String userId,
			String documentId) throws AcumosServiceException {

		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		Boolean isSharedDoc = Boolean.FALSE;
		List<MLPDocument> documentList = new ArrayList<MLPDocument>();
		
		MLPDocument document = dataServiceRestClient.getDocument(documentId);
		if(document == null) {
			log.error(EELFLoggerDelegate.errorLogger, "Failed to fetch document for revisionId : " + revisionId);
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.IO_EXCEPTION, "Failed to fetch document for revisionId : " + revisionId);
		}

		List<MLPSolutionRevision> revisions = dataServiceRestClient.getSolutionRevisions(solutionId);
		for(MLPSolutionRevision revision : revisions) {
			try {
				List<MLPDocument> filteredDocList = new ArrayList<MLPDocument>();
				List<MLPDocument> revDocList = dataServiceRestClient.getSolutionRevisionDocuments(revision.getRevisionId(), accessType);
				if(!PortalUtils.isEmptyList(revDocList)) {
					filteredDocList = revDocList.stream().filter(revDoc -> documentId.equalsIgnoreCase(revDoc.getDocumentId()) && !(revision.getRevisionId().equalsIgnoreCase(revisionId))).collect(Collectors.toList());
				}
				
				if(!PortalUtils.isEmptyList(filteredDocList)) {
					documentList.addAll(filteredDocList);
				}
			} catch (Exception e) {
				//Log error and Do Nothing
				log.error(EELFLoggerDelegate.errorLogger, "Failed to fetch document for revisionId : " + revision.getRevisionId(), e);
			}
		}

		if(!PortalUtils.isEmptyList(documentList)) {
			isSharedDoc = Boolean.TRUE;
		}

		NexusArtifactClient nexusClient = getNexusClient();
		if(!isSharedDoc) {
			try {
				nexusClient.deleteArtifact(document.getUri());
			} catch (URISyntaxException e) {
				log.error(EELFLoggerDelegate.errorLogger,
						"Failed to delete the document from Nexus with documentId : " + document.getDocumentId(), e);
				throw new AcumosServiceException(AcumosServiceException.ErrorCode.IO_EXCEPTION, e.getMessage());
			}
		}

		//Remove the mapping between revision and solution with the access type code
		dataServiceRestClient.dropSolutionRevisionDocument(revisionId, accessType, documentId);

		//If not a shared doc then remove the document record from DB also.
		if (!isSharedDoc) {
			dataServiceRestClient.deleteDocument(documentId);
		}
		return document;
	}

	private String getNexusGroupId(String solutionId, String revisionId) {
		String group = env.getProperty("nexus.groupId");
		if(PortalUtils.isEmptyOrNullString(group))
			throw new IllegalArgumentException("Missing property value for nexus groupId.");
		//This will created the nexus file upload path as groupId/solutionId/revisionId. Ex.. "org/acumos/solutionId/revisionId".
		return String.join(".", group, solutionId, revisionId);
	}

	@Override
	public List<MLPDocument> getRevisionDocument(String solutionId, String revisionId, String accessType, String string)
			throws AcumosServiceException {
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		List<MLPDocument> documents = dataServiceRestClient.getSolutionRevisionDocuments(revisionId, accessType);
		return documents;
	}

	@Override
	public List<MLPDocument> copyRevisionDocuments(String solutionId, String revisionId, String accessType,
			String userId, String fromRevisionId) throws AcumosServiceException {

		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		List<MLPDocument> revDocList = dataServiceRestClient.getSolutionRevisionDocuments(fromRevisionId, accessType);

		for(MLPDocument revDocument : revDocList) {
			dataServiceRestClient.addSolutionRevisionDocument(revisionId, accessType, revDocument.getDocumentId());
		}

		return revDocList;
	}

	@Override
	public RevisionDescription getRevisionDescription(String revisionId, String accessType)
			throws AcumosServiceException {
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		MLPRevisionDescription  description = dataServiceRestClient.getRevisionDescription(revisionId, accessType);
		
		if(description == null) {
			log.error(EELFLoggerDelegate.errorLogger, "No description Found.");
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.IO_EXCEPTION, "No description Found.");
		}
		return PortalUtils.convertToRevisionDescription(description);
	}

	@Override
	public RevisionDescription addUpdateRevisionDescription(String revisionId, String accessType,
			RevisionDescription description) throws AcumosServiceException {

		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		String accessCode = null;
		List<MLPCodeNamePair> codeNamePairList = dataServiceRestClient.getCodeNamePairs(CodeNameType.ACCESS_TYPE);
		for (MLPCodeNamePair accessTypeCode : codeNamePairList) {
			if(accessTypeCode.getCode().equals(accessType))
				accessCode = accessTypeCode.getCode();
		}

		if (accessCode == null) {
			log.error(EELFLoggerDelegate.errorLogger, "Cannot Recognize the accessTypeCode");
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.IO_EXCEPTION, "Invalid Access Type Code");
		}
		
		if(PortalUtils.isEmptyOrNullString(description.getDescription())) {
			log.error(EELFLoggerDelegate.errorLogger, "Description is Empty");
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.IO_EXCEPTION, "Description is Empty");
		}

		Boolean isDescriptionExists = Boolean.FALSE;
		RevisionDescription revisionDescription = null;
		try {
			revisionDescription = getRevisionDescription(revisionId, accessCode);
			if(revisionDescription != null)
				isDescriptionExists = Boolean.TRUE;
		}catch(Exception e) {
			//Do nothing. Create a new description if cannot find the existing description
		}

		MLPRevisionDescription mlpRevDesc = new MLPRevisionDescription();
		mlpRevDesc.setRevisionId(revisionId);
		mlpRevDesc.setAccessTypeCode(accessCode);
		if(isDescriptionExists) {
			//Update the existing Description
			mlpRevDesc.setDescription(description.getDescription());
			dataServiceRestClient.updateRevisionDescription(mlpRevDesc);
		} else {
			//Create a new description in db
			mlpRevDesc.setDescription(description.getDescription());
			mlpRevDesc = dataServiceRestClient.createRevisionDescription(mlpRevDesc);
		}

		if(mlpRevDesc!= null)
			description = PortalUtils.convertToRevisionDescription(mlpRevDesc);

		return description;
	}

	@Override
	public byte[] getSolutionPicture(String solutionId) {
		log.debug(EELFLoggerDelegate.debugLogger, "getSolutionPicture");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		return dataServiceRestClient.getSolutionPicture(solutionId);
	}

	@Override
	public void updateSolutionPicture(String solutionId, byte[] image) {
		log.debug(EELFLoggerDelegate.debugLogger, "updateSolutionPicture");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		dataServiceRestClient.saveSolutionPicture(solutionId, image);
	}
	
}
