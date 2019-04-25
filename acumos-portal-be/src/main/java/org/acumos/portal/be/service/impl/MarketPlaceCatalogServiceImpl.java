/*-
 * ===============LICENSE_START=======================================================
 * Acumos 
 * ===================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
 * Modifications Copyright (C) 2019 Nordix Foundation.
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
import java.lang.invoke.MethodHandles;
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
import org.acumos.cds.CodeNameType;
import org.acumos.cds.client.ICommonDataServiceRestClient;
import org.acumos.cds.domain.MLPArtifact;
import org.acumos.cds.domain.MLPCatalog;
import org.acumos.cds.domain.MLPCodeNamePair;
import org.acumos.cds.domain.MLPDocument;
import org.acumos.cds.domain.MLPRevCatDescription;
import org.acumos.cds.domain.MLPSiteConfig;
import org.acumos.cds.domain.MLPSolution;
import org.acumos.cds.domain.MLPSolutionFavorite;
import org.acumos.cds.domain.MLPSolutionRating;
import org.acumos.cds.domain.MLPSolutionRevision;
import org.acumos.cds.domain.MLPTaskStepResult;
import org.acumos.cds.domain.MLPTag;
import org.acumos.cds.domain.MLPTask;
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
import org.acumos.portal.be.docker.DockerClientFactory;
import org.acumos.portal.be.docker.DockerConfiguration;
import org.acumos.portal.be.docker.cmd.DeleteImageCommand;
import org.acumos.portal.be.service.MarketPlaceCatalogService;
import org.acumos.portal.be.service.AdminService;
import org.acumos.portal.be.service.UserService;
import org.acumos.portal.be.transport.Author;
import org.acumos.portal.be.transport.MLSolution;
import org.acumos.portal.be.transport.MLSolutionFavorite;
import org.acumos.portal.be.transport.MLSolutionRating;
import org.acumos.portal.be.transport.MLSolutionWeb;
import org.acumos.portal.be.transport.RestPageRequestPortal;
import org.acumos.portal.be.transport.RevisionDescription;
import org.acumos.portal.be.transport.User;
import org.acumos.portal.be.util.JsonUtils;
import org.acumos.portal.be.util.PortalUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.maven.wagon.ConnectionException;
import org.apache.maven.wagon.ResourceDoesNotExistException;
import org.apache.maven.wagon.TransferFailedException;
import org.apache.maven.wagon.authentication.AuthenticationException;
import org.apache.maven.wagon.authorization.AuthorizationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.multipart.MultipartFile;

import com.github.dockerjava.api.DockerClient;

/**
 * Service to Support Market Place Catalog and Manage models modules
 */
@Service
public class MarketPlaceCatalogServiceImpl extends AbstractServiceImpl implements MarketPlaceCatalogService {

	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Autowired
	private Environment env;

	@Autowired
	private UserService userService;
	
	@Autowired
	private AdminService adminService;

	@Autowired
	private DockerConfiguration dockerConfiguration;

	private static final String STEP_STATUS_FAILED = "FA";

	/*
	 * No
	 */
	public MarketPlaceCatalogServiceImpl() {

	}

	@Override
	public RestPageResponse<MLPSolution> getAllPaginatedSolutions(Integer page, Integer size, String sortingOrder)
			throws AcumosServiceException {
		log.debug("getAllPaginatedSolutions");
		RestPageResponse<MLPSolution> mlpSolutionsPaged = null;
		List<MLSolution> mlSolutions = null;
		try {
			ICommonDataServiceRestClient dataServiceRestClient = getClient();
			// TODO: revisit this code to pass query parameters to CCDS Service
			Map<String, Object> queryParameters = new HashMap<>();
			queryParameters.put("active", "Y"); // Fetch all active solutions
			queryParameters.put("accessTypeCode", CommonConstants.PUBLIC);
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
		log.debug("getAllPublishedSolutions");
		List<MLSolution> mlSolutions = null;
		try {
			ICommonDataServiceRestClient dataServiceRestClient = getClient();
			Map<String, Object> queryParameters = new HashMap<>();
			// queryParameters.put("active", "Y"); //Fetch all active solutions
			queryParameters.put("accessTypeCode", CommonConstants.PUBLIC);
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
		log.debug("getSolution");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		MLSolution mlSolution = null;
		try {
			MLPSolution mlpSolution = dataServiceRestClient.getSolution(solutionId);
			if (mlpSolution != null) {
				mlSolution = PortalUtils.convertToMLSolution(mlpSolution);
				List<MLPCodeNamePair> toolkitTypeList = dataServiceRestClient
						.getCodeNamePairs(CodeNameType.TOOLKIT_TYPE);
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
				// Set co-owners list for model
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
					log.error("No co-owner for SolutionId={}", mlSolution.getSolutionId());
				}

				List<String> co_owners_Id = new ArrayList<String>();
				if (users != null) {
					co_owners_Id = users.stream().map(User::getUserId).collect(Collectors.toList());
				}
				List<MLPSolutionRevision> revisionList = dataServiceRestClient.getSolutionRevisions(solutionId);
				if (!PortalUtils.isEmptyList(revisionList)) {
					// filter the private versions if loggedIn User is not the
					// owner of solution
				
					if (loginUserId != null) {
						// if logged In user is owner/co-owner then add private revisions
						if (loginUserId.equals(mlpSolution.getUserId()) || co_owners_Id.contains(loginUserId)
								|| userService.isPublisherRole(loginUserId)) {
							mlSolution.setRevisions(revisionList);
						} else {
							List<MLPCatalog> catalogList = dataServiceRestClient.getSolutionCatalogs(solutionId);
							// if user is logged in but he not the
							// owner/co-owner then only show if published to catalog
							if (!PortalUtils.isEmptyList(catalogList)) {
								mlSolution.setRevisions(revisionList);
							}
						}
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
		log.debug("getSearchedSolutions");
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
		log.debug("updateSolution");
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
				log.error("Could not fetch tag list for update solution: " + e.getMessage());
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
		log.debug("deleteSolutionArtifacts");
		try {
			ICommonDataServiceRestClient dataServiceRestClient = getClient();

			// fetch the solution to populate the solution picture so that it
			// does not get wiped off
			// Check if Image is present in the object. if not then fetch the
			// solution image and then populate it

			MLPSolution solution = PortalUtils.convertToMLPSolution(mlSolution);
			try {
				List<MLPTag> taglist = dataServiceRestClient.getSolutionTags(solutionId);
				HashSet<MLPTag> tags = new HashSet<MLPTag>(taglist.size());
				for (MLPTag tag : taglist)
					tags.add(tag);
				solution.setTags(tags);
			} catch (HttpStatusCodeException e) {
				log.error("Could not fetch tag list for delete solution artifacts: " + e.getMessage());
			} finally {
				// start
				List<MLPCatalog> catalogs = dataServiceRestClient.getSolutionCatalogs(solutionId);
				if (catalogs != null) {
					for (MLPCatalog catalog : catalogs) {
						dataServiceRestClient.dropSolutionFromCatalog(solutionId, catalog.getCatalogId());
					}
				}

				if (revisionId != null) {
					List<MLPArtifact> mlpArtifactsList = dataServiceRestClient.getSolutionRevisionArtifacts(solutionId,
							revisionId);

					
					for (MLPArtifact mlp : mlpArtifactsList) {
						boolean deleteNexus = false;
						// Delete the file from the Nexus
						log.info("mlp.getUri ----->>" + mlp.getUri());
						log.info("mlp.getArtifactTypeCode ----->>" + mlp.getArtifactTypeCode());

						if ("DI".equals(mlp.getArtifactTypeCode())) {
							DockerClient dockerClient = DockerClientFactory.getDockerClient(dockerConfiguration);
							DeleteImageCommand deleteImg = new DeleteImageCommand(mlp.getUri());
							deleteImg.setClient(dockerClient);
							deleteImg.execute();
							deleteNexus = true;
						} else {
							// Delete the file from the Nexus
							String nexusUrl = env.getProperty("nexus.url");
							String nexusUserName = env.getProperty("nexus.username");
							String nexusPd = env.getProperty("nexus.password");
							log.info("nexusUrl ----->>" + nexusUrl);
							log.info("nexusUserName ----->>" + nexusUserName);
							log.info("nexusPd ----->>" + nexusPd);
							NexusArtifactClient nexusArtifactClient = nexusArtifactClient(nexusUrl, nexusUserName,
									nexusPd);
							nexusArtifactClient.deleteArtifact(mlp.getUri());
							deleteNexus = true;
						}

						if (deleteNexus) {
							
							String artifactId = mlp.getArtifactId();
							// Delete SolutionRevisionArtifact
							dataServiceRestClient.dropSolutionRevisionArtifact(solutionId, revisionId, artifactId);
							log.debug(" Successfully Deleted the SolutionRevisionArtifact ");
							// Delete Artifact from CDS
							dataServiceRestClient.deleteArtifact(artifactId);
							log.debug(" Successfully Deleted the CDump Artifact ");
						} else {
							throw new AcumosServiceException(AcumosServiceException.ErrorCode.INVALID_PARAMETER,
									"Unable to delete  solution from Database");
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
		log.debug("getSolutionRevision`");
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
		log.debug("getSolutionArtifacts`");
		List<MLPArtifact> mlpSolutionRevisions = null;
		List<MLPArtifact> artifactList = new ArrayList<MLPArtifact>();
		try {
			ICommonDataServiceRestClient dataServiceRestClient = getClient();
			mlpSolutionRevisions = dataServiceRestClient.getSolutionRevisionArtifacts(solutionId, revisionId);
			if (mlpSolutionRevisions != null) {
				for (MLPArtifact artifact : mlpSolutionRevisions) {
					String[] st = artifact.getUri().split("/");
					String name = st[st.length - 1];
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
		log.debug("addSolutionTag`");
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
		log.debug("addSolutionTag`");
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
			// check if there are more tags then default size of 20, if yes then
			// get all again
			if (mlpTagsList.getSize() < mlpTagsList.getTotalElements()) {
				restPageReq.getBody().setPage(0);
				restPageReq.getBody().setSize((int) mlpTagsList.getTotalElements());
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
	public List<Map<String, String>> getPreferredTagsList(JsonRequest<RestPageRequest> restPageReq, String userId)
			throws AcumosServiceException {
		List<Map<String, String>> prefTags = new ArrayList<>();
		try {
			Long startTime = System.currentTimeMillis();
			// System.out.println(startTime);
			List<String> userTagsList = new ArrayList<>();
			ICommonDataServiceRestClient dataServiceRestClient = getClient();
			List<String> mlTagsList = getTags(restPageReq);
			MLPUser userDetails = dataServiceRestClient.getUser(userId);
			Set<MLPTag> userTagSet = userDetails.getTags();
			for (MLPTag userTags : userTagSet) {
				userTagsList.add(userTags.getTag());
				Map<String, String> map = new HashMap<>();
				map.put("tagName", userTags.getTag());
				map.put("preferred", "Yes");
				prefTags.add(map);
			}
			for (String tag : mlTagsList) {
				Map<String, String> map = new HashMap<>();
				map.put("tagName", tag);
				// Simplifying the code
				if (!userTagsList.contains(tag)) {
					map.put("preferred", "No");
					prefTags.add(map);
				}

			}
			Long endTime = System.currentTimeMillis();
			log.debug("getPreferredTagsList total time took " + (endTime - startTime));
		} catch (IllegalArgumentException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INVALID_PARAMETER, e.getMessage());
		} catch (HttpClientErrorException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
		}
		return prefTags;
	}

	@Override
	public void createUserTag(String userId, List<String> tagList, List<String> dropTagList)
			throws AcumosServiceException {
		try {
			log.debug("createUserTag");
			ICommonDataServiceRestClient dataServiceRestClient = getClient();
			if (dropTagList.size() != 0) {
				for (String dropTag : dropTagList) {
					dataServiceRestClient.dropUserTag(userId, dropTag);
				}
			}
			if (tagList.size() != 0) {
				for (String tag : tagList) {
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
		log.debug("getSolutionUserAccess");
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
		log.debug("addSolutionUserAccess");
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
			log.debug("incrementSolutionViewCount");
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
			log.debug("createSolutionrating");
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
			log.debug("updateSolutionRating");
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
			log.debug("getSolutionRating");
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
		log.debug("getSolutionUserAccess");
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
		
		List<MLSolution> contentML = new ArrayList<>();
		RestPageResponseBE<MLSolution> mlSolutionsRest = new RestPageResponseBE<>(contentML);
	
		try {
			if (!PortalUtils.isEmptyOrNullString(tag)) {
				log.debug("getTagSearchedSolutions: searching Solutions with tags:", tag);
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
			log.debug("createSolutionFavorite");
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
			log.debug("deleteSolutionFavorite");
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
			log.debug("getFavoriteSolutions : ");
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
		log.debug("getRealtedMySolutions");
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
					 * if(restPageReqBe.getBody().getPage()!=null) {
					 * pageRequest.setPage(pageSize); } else { //default to 0
					 * pageRequest.setPage(0); }
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
						log.debug("getSearchedSolutions: searching Solutions with searcTerm:",
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
					 * if(!PortalUtils.isEmptyOrNullString(restPageReqBe.getBody
					 * (). getAccessType())) { filteredMLPSolutionsTemp =
					 * filteredMLPSolutionsTemp.stream().filter(mlpSolution ->
					 * (PortalUtils.isEmptyOrNullString(mlpSolution.
					 * getAccessTypeCode())
					 * ||(!PortalUtils.isEmptyOrNullString(mlpSolution.
					 * getAccessTypeCode()) &&
					 * restPageReqBe.getBody().getAccessType().contains(
					 * mlpSolution. getAccessTypeCode())))
					 * ).collect(Collectors.toList()); }
					 * 
					 * if(!PortalUtils.isEmptyOrNullString(restPageReqBe.getBody
					 * (). getModelToolkitType())) { filteredMLPSolutionsTemp =
					 * filteredMLPSolutionsTemp.stream().filter(mlpSolution ->
					 * (PortalUtils.isEmptyOrNullString(mlpSolution.
					 * getToolkitTypeCode())
					 * ||(!PortalUtils.isEmptyOrNullString(mlpSolution.
					 * getToolkitTypeCode()) &&
					 * restPageReqBe.getBody().getModelToolkitType().contains(
					 * mlpSolution.getToolkitTypeCode())))).collect(Collectors.
					 * toList()); }
					 */

					if (!PortalUtils.isEmptyOrNullString(restPageReqBe.getBody().getModelType())) {
						filteredMLPSolutionsTemp = filteredMLPSolutionsTemp.stream().filter(mlpSolution -> (PortalUtils
								.isEmptyOrNullString(mlpSolution.getModelTypeCode())
								|| (!PortalUtils.isEmptyOrNullString(mlpSolution.getModelTypeCode()) && restPageReqBe
										.getBody().getModelType().contains(mlpSolution.getModelTypeCode()))))
								.collect(Collectors.toList());
					}

					/*
					 * if(!PortalUtils.isEmptyOrNullString(restPageReqBe.getBody
					 * (). getActiveType())) { Boolean isActive =false;
					 * if(restPageReqBe.getBody().getActiveType().
					 * equalsIgnoreCase( "Y")){ isActive = true; }else
					 * if(restPageReqBe.getBody().getActiveType().
					 * equalsIgnoreCase( "N")){ isActive = false; }
					 * filteredMLPSolutionsTemp =
					 * filteredMLPSolutionsTemp.stream().filter(mlpSolution ->
					 * Boolean.compare(restPageReqBe.getBody().getActiveType().
					 * equalsIgnoreCase("Y"),
					 * mlpSolution.isActive())==0).collect(Collectors.toList());
					 * }
					 */

					/*
					 * for(int k=0;k<filteredMLPSolutionsTemp.size()&&
					 * index<9;k++){ filteredMLPSolutions.add(index,
					 * filteredMLPSolutionsTemp.get(k)); index++;
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
							if (!PortalUtils.isEmptyOrNullString(userFirstName)) {
								userName = userFirstName;
								if (!PortalUtils.isEmptyOrNullString(userLastName)) {
									userName = userName + " " + userLastName;
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
				// mlpSolutionsRest.setNumberOfElements(filteredMLPSolutions.size());
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
			log.debug("addSolutionUserAccess");
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
			log.debug("createTag");
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
		log.debug("addSolutionUserAccess");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		MLPSolutionRating rating = dataServiceRestClient.getSolutionRating(solutionId, userId);
		return rating;
	}

	@Override
	public RestPageResponseBE<MLSolution> findPortalSolutions(RestPageRequestPortal pageReqPortal,
			Set<MLPTag> preferredTags) {
		log.debug("findPortalSolutions(pageReqPortal, prefTags");

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

		return searchSolutionsByKeyword(pageReqPortal);
	}

	@Override
	public RestPageResponseBE<MLSolution> searchSolutionsByKeyword(RestPageRequestPortal pageReqPortal) {
		log.debug("findPortalSolutions");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();

		RestPageResponse<MLPSolution> response = dataServiceRestClient.findPublishedSolutionsByKwAndTags(
				pageReqPortal.getNameKeyword(), pageReqPortal.isActive(), pageReqPortal.getOwnerIds(),
				pageReqPortal.getModelTypeCodes(), pageReqPortal.getTags(), null, pageReqPortal.getCatalogIds(), pageReqPortal.getPageRequest());

		List<MLSolution> content = new ArrayList<>();
		RestPageResponseBE<MLSolution> mlSolutionsRest = new RestPageResponseBE<>(content);

		if (response.getContent() != null) {
			mlSolutionsRest = fetchDetailsForSolutions(response.getContent(), pageReqPortal);
			mlSolutionsRest.setPageCount(response.getTotalPages());
			mlSolutionsRest.setTotalElements((int) response.getTotalElements());
		}
		return mlSolutionsRest;
	}

	private RestPageResponseBE<MLSolution> fetchDetailsForSolutions(List<MLPSolution> mlpSolList,
			RestPageRequestPortal pageReqPortal) {
		log.debug("fetchDetailsForSolution");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		List<MLSolution> content = new ArrayList<>();
		RestPageResponseBE<MLSolution> mlSolutionsRest = new RestPageResponseBE<>(content);
		Set<String> filteredTagSet = new HashSet<>();
		List<MLPSolution> filteredSolList = new ArrayList<>();

		// List<MLPSolution> mlpSolList = response.getContent();
		filteredSolList.addAll(mlpSolList);

		for (MLPSolution mlpSol : filteredSolList) {

			// CDS does not return the picture in list of solution. So we need
			// to fetch the solution separately and then populate the picture
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
				List<MLPUser> mlpUsersList = dataServiceRestClient.getSolutionAccessUsers(mlSolution.getSolutionId());
				if (!PortalUtils.isEmptyList(mlpUsersList)) {
					users = new ArrayList<>();
					for (MLPUser mlpusers : mlpUsersList) {
						User user = PortalUtils.convertToMLPuser(mlpusers);
						user.setJwttoken(null);
						user.setPassword(null);
						users.add(user);
					}
				}
				mlSolution.setOwnerListForSol(users);
			} catch (Exception e) {
				log.error("No co-owner for SolutionId={}", mlSolution.getSolutionId());
			}

			// To categorize the solution on display fetch latest revision and
			// add the access type code
			MLPSolutionRevision revision = getLatestSolRevision(mlpSol.getSolutionId());
			if (revision != null) {
				mlSolution.setLatestRevisionId(revision.getRevisionId());
				if (PortalUtils.isEmptyOrNullString(revision.getPublisher())) {
					MLPSiteConfig siteConfig = adminService.getSiteConfig("site_config");
					if (siteConfig != null && !PortalUtils.isEmptyOrNullString(siteConfig.getConfigValue())) {
						Map<String, Object> mapSiteConfig = JsonUtils.serializer().mapFromJson(siteConfig.getConfigValue());
						List<Map<String, String>> fields = (List<Map<String, String>>) mapSiteConfig.get("fields");
						for (Map<String, String> field : fields) {
							if (field.get("name").equals("siteInstanceName")) {
								mlSolution.setPublisher(field.get("data"));
								break;
							}
						}
					}
				} else {
					mlSolution.setPublisher(revision.getPublisher());
				}
				List<Author> authors = PortalUtils.convertToAuthor(revision.getAuthors());
				mlSolution.setAuthors(authors);
				long Count = dataServiceRestClient.getSolutionRevisionCommentCount(mlpSol.getSolutionId(),
						revision.getRevisionId());
				mlSolution.setCommentsCount(Count);
			}
			
			List<MLPCatalog> catalogs = dataServiceRestClient.getSolutionCatalogs(mlpSol.getSolutionId());
			if (!PortalUtils.isEmptyList(catalogs)) {
				for (MLPCatalog catalog : catalogs) {
					if (catalog != null && !PortalUtils.isEmptyOrNullString(catalog.getName())) {
						mlSolution.setCatalogName(catalog.getName());
						break;
					}
				}
			}

			// get latest step Result for solution
			Boolean onboardingStatusFailed = false;
			Map<String, Object> stepResultCriteria = new HashMap<String, Object>();
			stepResultCriteria.put("solutionId", mlpSol.getSolutionId());

			Map<String, String> queryParameters = new HashMap<>();
			queryParameters.put("created", "DESC");
			// Fetch latest step result for the solution to get the tracking id
			RestPageResponse<MLPTask> taskResponse = dataServiceRestClient.searchTasks(stepResultCriteria, false,
					new RestPageRequest(0, 1, queryParameters));
			if (!PortalUtils.isEmptyList(taskResponse.getContent())) {
				MLPTask task = taskResponse.getContent().get(0);
				List<MLPTaskStepResult> stepResultList = dataServiceRestClient.getTaskStepResults(task.getTaskId());
				String errorStatusDetails = null;
				if (!PortalUtils.isEmptyList(stepResultList)) {
					// check if any of the step result is Failed
					for (MLPTaskStepResult step : stepResultList) {
						if (STEP_STATUS_FAILED.equals(step.getStatusCode())) {
							onboardingStatusFailed = true;
							errorStatusDetails = step.getResult();
							break;
						}
					}
				}
				mlSolution.setOnboardingStatusFailed(onboardingStatusFailed);
				if (errorStatusDetails != null) {
					mlSolution.setErrorDetails(errorStatusDetails);
				}
			}
			// Search for pending Approvals
			if (mlSolution.getSolutionId() != null && mlSolution.getLatestRevisionId() != null) {
				boolean pendingApproval = dataServiceRestClient.isPublishRequestPending(mlSolution.getSolutionId(),
						mlSolution.getLatestRevisionId());
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
		log.debug("findUserSolutions");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		RestPageResponse<MLPSolution> response = dataServiceRestClient.findUserSolutions(pageReqPortal.isActive(),
				pageReqPortal.isPublished(), pageReqPortal.getUserId(), pageReqPortal.getNameKeyword(),
				pageReqPortal.getDescriptionKeyword(), pageReqPortal.getModelTypeCodes(), pageReqPortal.getTags(),
				pageReqPortal.getPageRequest());

		List<MLSolution> content = new ArrayList<>();
		RestPageResponseBE<MLSolution> mlSolutionsRest = new RestPageResponseBE<>(content);

		if (response.getContent() != null) {
			mlSolutionsRest = fetchDetailsForSolutions(response.getContent(), pageReqPortal);
			mlSolutionsRest.setPageCount(response.getTotalPages());
			mlSolutionsRest.setTotalElements((int) response.getTotalElements());
		}

		return mlSolutionsRest;
	}

	private MLPSolutionRevision getLatestSolRevision(String solutionId) {
		log.debug("getLatestSolRevision");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();

		MLPSolutionRevision revision = null;
		List<MLPSolutionRevision> revisions = dataServiceRestClient.getSolutionRevisions(solutionId);
		if (revisions != null) {
			// Sort revision according to created date
			Collections.sort(revisions, new Comparator<MLPSolutionRevision>() {
				public int compare(MLPSolutionRevision m1, MLPSolutionRevision m2) {
					return m2.getCreated().compareTo(m1.getCreated());
				}
			});
		}
		// for deleted solutions no access type code is required from the front
		// end. So assign the latest version
		if (!PortalUtils.isEmptyList(revisions)) {
			revision = revisions.get(0);
		}
		return revision;
	}

	@Override
	public RestPageResponse<MLPSolution> getUserAccessSolutions(String userId, RestPageRequest pageRequest) {
		log.debug("findPortalSolutions");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		RestPageResponse<MLPSolution> mlSolutions = dataServiceRestClient.getUserAccessSolutions(userId, pageRequest);
		return mlSolutions;
	}

	@Override
	public MLSolutionWeb getSolutionWebMetadata(String solutionId) {
		log.debug("getSolutionWebMetadata");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		MLPSolution sol = dataServiceRestClient.getSolution(solutionId);
		MLSolution mlSolution = PortalUtils.convertToMLSolution(sol);
		MLSolutionWeb mlSolutionweb = new MLSolutionWeb();
		mlSolutionweb.setRatingAverageTenths(mlSolution.getSolutionRatingAvg());
		return mlSolutionweb;
	}

	@Override
	public List<Author> getSolutionRevisionAuthors(String solutionId, String revisionId) {
		log.debug("getSolutionRevisionAuthors");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		MLPSolutionRevision revision = dataServiceRestClient.getSolutionRevision(solutionId, revisionId);
		AuthorTransport[] authorTransport = revision.getAuthors();
		List<Author> authors = PortalUtils.convertToAuthor(authorTransport);
		return authors;
	}

	@Override
	public List<Author> addSolutionRevisionAuthors(String solutionId, String revisionId, Author author)
			throws AcumosServiceException {
		log.debug("addSolutionRevisionAuthors");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		AuthorTransport newAuthor = new AuthorTransport(author.getName(), author.getContact());
		MLPSolutionRevision revision = dataServiceRestClient.getSolutionRevision(solutionId, revisionId);
		AuthorTransport[] authorTransport = revision.getAuthors();
		for (AuthorTransport authorT : authorTransport) {
			if (newAuthor.equals(authorT)) {
				throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR,
						"Author already exists in the list.");
			}
		}
		ArrayList<AuthorTransport> authorTransportList = new ArrayList<AuthorTransport>(Arrays.asList(authorTransport));
		authorTransportList.add(newAuthor);

		List<Author> updatedAuthor = new ArrayList<>();
		try {
			updatedAuthor = updateRevisionAuthors(revision, authorTransportList);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR,
					"Internal Server Error");
		}

		return updatedAuthor;
	}

	@Override
	public List<Author> removeSolutionRevisionAuthors(String solutionId, String revisionId, Author author) {
		log.debug("removeSolutionRevisionAuthors");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		AuthorTransport removeAuthor = new AuthorTransport(author.getName(), author.getContact());
		MLPSolutionRevision revision = dataServiceRestClient.getSolutionRevision(solutionId, revisionId);
		AuthorTransport[] authorTransport = revision.getAuthors();

		ArrayList<AuthorTransport> authorTransportList = new ArrayList<AuthorTransport>();
		for (AuthorTransport authorT : authorTransport) {
			if (!removeAuthor.equals(authorT)) {
				authorTransportList.add(authorT);
			}
		}

		return updateRevisionAuthors(revision, authorTransportList);
	}

	@Override
	public String getSolutionRevisionPublisher(String solutionId, String revisionId) throws AcumosServiceException {
		log.debug("getSolutionRevisionPublisher");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		MLPSolutionRevision revision = dataServiceRestClient.getSolutionRevision(solutionId, revisionId);
		return revision.getPublisher();

	}

	@Override
	public void addSolutionRevisionPublisher(String solutionId, String revisionId, String newPublisher)
			throws AcumosServiceException {
		log.debug("addSolutionRevisionPublisher");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		MLPSolutionRevision revision = dataServiceRestClient.getSolutionRevision(solutionId, revisionId);

		if (!newPublisher.isEmpty() && !newPublisher.equals("") && newPublisher != null)
			revision.setPublisher(newPublisher);
		dataServiceRestClient.updateSolutionRevision(revision);

	}

	private List<Author> updateRevisionAuthors(MLPSolutionRevision revision,
			ArrayList<AuthorTransport> authorTransportList) {
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		AuthorTransport[] authorData = authorTransportList.toArray(new AuthorTransport[0]);
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

			log.error(" Exception in getPayload() ", ex);
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.OBJECT_STREAM_EXCEPTION, ex.getMessage());

		}
		return outputStream;
	}

	@Override
	public String getProtoUrl(String solutionId, String version, String artifactType, String fileExtension)
			throws AcumosServiceException {
		log.debug("getProtoUrl() : Begin");

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
				log.debug(" SolutionRevisonId for Version :  {} ", solutionRevisionId);
			}
		} catch (NoSuchElementException | NullPointerException e) {
			log.error("Error : Exception in getProtoUrl() : Failed to fetch the Solution Revision Id", e);
			throw new NoSuchElementException("Failed to fetch the Solution Revision Id of the solutionId for the user");
		}

		if (null != solutionRevisionId) {
			// 3. Get the list of Artifact for the SolutionId and
			// SolutionRevisionId.
			mlpArtifactList = getSolutionArtifacts(solutionId, solutionRevisionId);
			String nexusURI = "";
			if (null != mlpArtifactList && !mlpArtifactList.isEmpty()) {
				try {
					nexusURI = mlpArtifactList.stream()
							.filter(mlpArt -> mlpArt.getArtifactTypeCode().equalsIgnoreCase(artifactType)).findFirst()
							.get().getUri();
					for (MLPArtifact mlpArt : mlpArtifactList) {
						if (null != fileExtension) {
							if (mlpArt.getArtifactTypeCode().equalsIgnoreCase(artifactType)
									&& mlpArt.getName().contains(fileExtension)) {
								nexusURI = mlpArt.getUri();
								break;
							}
						}
					}

					log.debug(" Nexus URI :  {} ", nexusURI);

					if (null != nexusURI) {
						byteArrayOutputStream = getPayload(nexusURI);
						log.debug(" Response in String Format :  {} ", byteArrayOutputStream.toString());
						result = byteArrayOutputStream.toString();
					}
				} catch (NoSuchElementException | NullPointerException e) {
					log.error("Error : Exception in getProtoUrl() : Failed to fetch the artifact URI for artifactType",
							e);
					throw new NoSuchElementException(
							"Could not search the artifact URI for artifactType " + artifactType);
				} finally {
					try {
						if (byteArrayOutputStream != null) {
							byteArrayOutputStream.close();
						}
					} catch (IOException e) {
						log.error("Error : Exception in getProtoUrl() : Failed to close the byteArrayOutputStream", e);
						throw new AcumosServiceException(AcumosServiceException.ErrorCode.IO_EXCEPTION, e.getMessage());
					}
				}
			}
		}
		log.debug("getProtoUrl() : End");

		return result;
	}

	@Override
	public String getLicenseUrl(String solutionId, String version, String artifactType, String fileNamePrefix)
			throws AcumosServiceException {
		log.debug("getLicenseUrl() : Begin");

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
				log.debug(" SolutionRevisonId for Version :  {} ", solutionRevisionId);
			}
		} catch (NoSuchElementException | NullPointerException e) {
			log.error("Error : Exception in getLicenseUrl() : Failed to fetch the Solution Revision Id", e);
			throw new NoSuchElementException("Failed to fetch the Solution Revision Id of the solutionId for the user");
		}

		if (null != solutionRevisionId) {
			// 3. Get the list of Artifact for the SolutionId and
			// SolutionRevisionId.
			mlpArtifactList = getSolutionArtifacts(solutionId, solutionRevisionId);
			String nexusURI = "";
			if (null != mlpArtifactList && !mlpArtifactList.isEmpty()) {
				try {
					nexusURI = mlpArtifactList.stream()
							.filter(mlpArt -> mlpArt.getArtifactTypeCode().equalsIgnoreCase(artifactType) && (mlpArt.getName().contains(fileNamePrefix) || mlpArt.getName().contains("licence"))).findFirst()
							.get().getUri();

					log.debug(" Nexus URI :  {} ", nexusURI);

					if (null != nexusURI) {
						byteArrayOutputStream = getPayload(nexusURI);
						log.debug(" Response in String Format :  {} ", byteArrayOutputStream.toString());
						result = byteArrayOutputStream.toString();
					}
				} catch (NoSuchElementException | NullPointerException e) {
					log.error("Error : Exception in getLicenseUrl() : Failed to fetch the artifact URI for artifactType",
							e);
					throw new NoSuchElementException(
							"Could not search the artifact URI for artifactType " + artifactType);
				} finally {
					try {
						if (byteArrayOutputStream != null) {
							byteArrayOutputStream.close();
						}
					} catch (IOException e) {
						log.error("Error : Exception in getLicenseUrl() : Failed to close the byteArrayOutputStream", e);
						throw new AcumosServiceException(AcumosServiceException.ErrorCode.IO_EXCEPTION, e.getMessage());
					}
				}
			}
		}
		log.debug("getLicenseUrl() : End");

		return result;
	}

	@Override
	public boolean checkUniqueSolName(String solutionId, String solName) {
		log.debug("checkUniqueSolName ={}", solutionId);
		ICommonDataServiceRestClient dataServiceRestClient = getClient();

		// Check only if user tries to change the name or publish the solution
		// from private to public /org
		MLPSolution oldSolution = dataServiceRestClient.getSolution(solutionId);
		if (!solName.equalsIgnoreCase(oldSolution.getName())) {
			String[] name = { solName };

			Map<String, String> queryParameters = new HashMap<>();
			// Fetch the maximum possible records. Need an api that could return
			// the exact match of names along with other nested filter criteria
			RestPageResponse<MLPSolution> searchSolResp = dataServiceRestClient.findPortalSolutions(name, null, true,
					null, null, null, null, null, new RestPageRequest(0, 10000, queryParameters));
			List<MLPSolution> searchSolList = searchSolResp.getContent();

			// removing the same solutionId from the list
			List<MLPSolution> filteredSolList1 = searchSolList.stream()
					.filter(searchSol -> !searchSol.getSolutionId().equalsIgnoreCase(solutionId))
					.collect(Collectors.toList());

			// Consider only those records that have exact match with the
			// solution name
			List<MLPSolution> filteredSolList = filteredSolList1.stream()
					.filter(searchSol -> searchSol.getName().equalsIgnoreCase(solName)).collect(Collectors.toList());

			if (!filteredSolList.isEmpty()) {
				return false;
			}
		}

		return true;
	}

	@Override
	public MLPDocument addRevisionDocument(String solutionId, String revisionId, String catalogId, String userId,
			MultipartFile file) throws AcumosServiceException {

		long size = file.getSize();
		String name = FilenameUtils.getBaseName(file.getOriginalFilename());
		String extension = FilenameUtils.getExtension(file.getOriginalFilename());
		ICommonDataServiceRestClient dataServiceRestClient = getClient();

		if (PortalUtils.isEmptyOrNullString(extension))
			throw new IllegalArgumentException("Incorrect file extension.");

		// Check if docuemtn already exists with the same name
		List<MLPDocument> documents = dataServiceRestClient.getRevisionCatalogDocuments(revisionId, catalogId);
		for (MLPDocument doc : documents) {
			if (doc.getName().equalsIgnoreCase(name)) {
				log.error("Document Already exists with the same name.");
				throw new AcumosServiceException(AcumosServiceException.ErrorCode.IO_EXCEPTION,
						"Document Already exists with the same name.");
			}
		}

		// first try to upload the file to nexus. If successful then only create
		// the c_document record in db
		NexusArtifactClient nexusClient = getNexusClient();
		UploadArtifactInfo uploadInfo = null;
		MLPDocument document = null;
		try {
			try {
				uploadInfo = nexusClient.uploadArtifact(getNexusGroupId(solutionId, revisionId), name, catalogId,
						extension, size, file.getInputStream());
			} catch (ConnectionException | IOException | AuthenticationException | AuthorizationException
					| TransferFailedException | ResourceDoesNotExistException e) {
				log.error("Failed to upload the document", e);
				throw new AcumosServiceException(AcumosServiceException.ErrorCode.IO_EXCEPTION, e.getMessage());
			}

			if (uploadInfo != null) {
				/*
				 * document = new MLPDocument(null, name,
				 * uploadInfo.getArtifactMvnPath(), (int) size,
				 * "1628acd3-37d6-4c53-a722-0396d0590235");
				 */
				document = new MLPDocument();
				document.setName(file.getOriginalFilename());
				document.setUri(uploadInfo.getArtifactMvnPath());
				document.setSize((int) size);
				document.setUserId(userId);
				document = dataServiceRestClient.createDocument(document);

				dataServiceRestClient.addRevisionCatalogDocument(revisionId, catalogId, document.getDocumentId());
			} else {
				log.error("Cannot upload the Document to the specified path");
				throw new AcumosServiceException(AcumosServiceException.ErrorCode.IO_EXCEPTION,
						"Cannot upload the Document to the specified path");
			}
		} catch (Exception e) {
			log.error("Exception during addRevisionDocument ={}", e);
			throw new AcumosServiceException(e.getMessage());
		}
		return document;
	}

	@Override
	public MLPDocument removeRevisionDocument(String solutionId, String revisionId, String catalogId, String userId,
			String documentId) throws AcumosServiceException {

		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		Boolean isSharedDoc = Boolean.FALSE;
		List<MLPDocument> documentList = new ArrayList<MLPDocument>();

		MLPDocument document = dataServiceRestClient.getDocument(documentId);
		if (document == null) {
			log.error("Failed to fetch document for revisionId : " + revisionId);
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.IO_EXCEPTION,
					"Failed to fetch document for revisionId : " + revisionId);
		}

		List<MLPSolutionRevision> revisions = dataServiceRestClient.getSolutionRevisions(solutionId);
		for (MLPSolutionRevision revision : revisions) {
			try {
				List<MLPDocument> filteredDocList = new ArrayList<MLPDocument>();
				List<MLPDocument> revDocList = dataServiceRestClient
						.getRevisionCatalogDocuments(revision.getRevisionId(), catalogId);
				if (!PortalUtils.isEmptyList(revDocList)) {
					filteredDocList = revDocList.stream()
							.filter(revDoc -> documentId.equalsIgnoreCase(revDoc.getDocumentId())
									&& !(revision.getRevisionId().equalsIgnoreCase(revisionId)))
							.collect(Collectors.toList());
				}

				if (!PortalUtils.isEmptyList(filteredDocList)) {
					documentList.addAll(filteredDocList);
				}
			} catch (Exception e) {
				// Log error and Do Nothing
				log.error("Failed to fetch document for revisionId : " + revision.getRevisionId(), e);
			}
		}

		if (!PortalUtils.isEmptyList(documentList)) {
			isSharedDoc = Boolean.TRUE;
		}

		NexusArtifactClient nexusClient = getNexusClient();
		if (!isSharedDoc) {
			try {
				nexusClient.deleteArtifact(document.getUri());
			} catch (URISyntaxException e) {
				log.error("Failed to delete the document from Nexus with documentId : " + document.getDocumentId(), e);
				throw new AcumosServiceException(AcumosServiceException.ErrorCode.IO_EXCEPTION, e.getMessage());
			}
		}

		// Remove the mapping between revision and solution with the access type
		// code
		dataServiceRestClient.dropRevisionCatalogDocument(revisionId, catalogId, documentId);

		// If not a shared doc then remove the document record from DB also.
		if (!isSharedDoc) {
			dataServiceRestClient.deleteDocument(documentId);
		}
		return document;
	}

	private String getNexusGroupId(String solutionId, String revisionId) {
		String group = env.getProperty("nexus.groupId");
		if (PortalUtils.isEmptyOrNullString(group))
			throw new IllegalArgumentException("Missing property value for nexus groupId.");
		// This will created the nexus file upload path as
		// groupId/solutionId/revisionId. Ex..
		// "org/acumos/solutionId/revisionId".
		return String.join(".", group, solutionId, revisionId);
	}

	@Override
	public List<MLPDocument> getRevisionDocument(String solutionId, String revisionId, String catalogId, String string)
			throws AcumosServiceException {
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		List<MLPDocument> documents = dataServiceRestClient.getRevisionCatalogDocuments(revisionId, catalogId);
		return documents;
	}

	@Override
	public List<MLPDocument> copyRevisionDocuments(String solutionId, String revisionId, String catalogId,
			String userId, String fromRevisionId) throws AcumosServiceException {

		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		List<MLPDocument> revDocList = dataServiceRestClient.getRevisionCatalogDocuments(fromRevisionId, catalogId);

		for (MLPDocument revDocument : revDocList) {
			dataServiceRestClient.addRevisionCatalogDocument(revisionId, catalogId, revDocument.getDocumentId());
		}

		return revDocList;
	}

	@Override
	public RevisionDescription getRevisionDescription(String revisionId, String catalogId)
			throws AcumosServiceException {
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		MLPRevCatDescription description = dataServiceRestClient.getRevCatDescription(revisionId, catalogId);

		if (description == null) {
			log.error("No description Found.");
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.IO_EXCEPTION, "No description Found.");
		}
		return PortalUtils.convertToRevisionDescription(description);
	}

	@Override
	public RevisionDescription addUpdateRevisionDescription(String revisionId, String catalogId,
			RevisionDescription description) throws AcumosServiceException {

		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		
		if (PortalUtils.isEmptyOrNullString(description.getDescription())) {
			log.error("Description is Empty");
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.IO_EXCEPTION, "Description is Empty");
		}

		Boolean isDescriptionExists = Boolean.FALSE;
		RevisionDescription revisionDescription = null;
		try {
			revisionDescription = getRevisionDescription(revisionId, catalogId);
			if (revisionDescription != null)
				isDescriptionExists = Boolean.TRUE;
		} catch (Exception e) {
			// Do nothing. Create a new description if cannot find the existing
			// description
		}

		MLPRevCatDescription mlpRevDesc = new MLPRevCatDescription();
		mlpRevDesc.setRevisionId(revisionId);
		mlpRevDesc.setCatalogId(catalogId);
		if (isDescriptionExists) {
			// Update the existing Description
			mlpRevDesc.setDescription(description.getDescription());
			dataServiceRestClient.updateRevCatDescription(mlpRevDesc);
		} else {
			// Create a new description in db
			mlpRevDesc.setDescription(description.getDescription());
			mlpRevDesc = dataServiceRestClient.createRevCatDescription(mlpRevDesc);
		}

		if (mlpRevDesc != null)
			description = PortalUtils.convertToRevisionDescription(mlpRevDesc);

		return description;
	}

	@Override
	public byte[] getSolutionPicture(String solutionId) {
		log.debug("getSolutionPicture");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		return dataServiceRestClient.getSolutionPicture(solutionId);
	}

	@Override
	public void updateSolutionPicture(String solutionId, byte[] image) {
		log.debug("updateSolutionPicture");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		dataServiceRestClient.saveSolutionPicture(solutionId, image);
	}

	@Override
	public MLSolution getSolution(String solutionId, String revisionId, String loginUserId)
			throws AcumosServiceException {
		log.debug("getSolution");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		MLSolution mlSolution = null;
		try {
			MLPSolution mlpSolution = dataServiceRestClient.getSolution(solutionId);
			MLPSolutionRevision mlpSolutionRevision = dataServiceRestClient.getSolutionRevision(solutionId, revisionId);
			if (mlpSolution != null) {
				boolean isPublished = !PortalUtils.isEmptyList(dataServiceRestClient.getSolutionCatalogs(solutionId));
				if (mlpSolutionRevision != null) {
					if ((PortalUtils.isEmptyOrNullString(loginUserId) && isPublished)
							|| (!PortalUtils.isEmptyOrNullString(loginUserId)
									&& (isPublished || loginUserId.equals(mlpSolution.getUserId())))) {
						mlSolution = PortalUtils.convertToMLSolution(mlpSolution);
						List<MLPCodeNamePair> toolkitTypeList = dataServiceRestClient
								.getCodeNamePairs(CodeNameType.TOOLKIT_TYPE);
						if (toolkitTypeList.size() > 0) {
							for (MLPCodeNamePair toolkitType : toolkitTypeList) {
								if (toolkitType.getCode() != null) {
									if (toolkitType.getCode()
											.equalsIgnoreCase(mlpSolution.getToolkitTypeCode())) {
										mlSolution.setTookitTypeName(toolkitType.getName());
										break;
									}
								}
							}
						}
						List<MLPCodeNamePair> modelTypeList = dataServiceRestClient
								.getCodeNamePairs(CodeNameType.MODEL_TYPE);
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
						// Set co-owners list for model
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
							log.error("No co-owner for SolutionId={}", mlSolution.getSolutionId());
						}

						List<String> co_owners_Id = new ArrayList<String>();
						if (users != null) {
							co_owners_Id = users.stream().map(User::getUserId).collect(Collectors.toList());
						}
						List<MLPSolutionRevision> revisionList = dataServiceRestClient.getSolutionRevisions(solutionId);
						if (!PortalUtils.isEmptyList(revisionList)) {
							// filter the private versions if loggedIn User is not the
							// owner of solution
							if (loginUserId != null) {
								// if logged In user is owner/co-owner then show all revisions
								if (loginUserId.equals(mlpSolution.getUserId()) || co_owners_Id.contains(loginUserId)
										|| userService.isPublisherRole(loginUserId)) {
									mlSolution.setRevisions(revisionList);
								} else {
									List<MLPCatalog> catalogList = dataServiceRestClient.getSolutionCatalogs(solutionId);
									// if user is logged in but he not the
									// owner/co-owner then only show if published to catalog
									if (!PortalUtils.isEmptyList(catalogList)) {
										mlSolution.setRevisions(revisionList);
									}
								}
							}
						}
					} else {
						throw new AcumosServiceException(AcumosServiceException.ErrorCode.ACCESS_DENIED,
								"Invalid User");
					}
				} else {
					log.debug("getSolution :  Revison Id is null for the solution Id :" + mlpSolution.getSolutionId());
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

    public RestPageResponse<MLPSolution> getMLPSolutionBySolutionName(Map<String, Object> solutoinNameParameter, boolean flag, RestPageRequest restPageRequest) throws AcumosServiceException {
        log.debug("getMLPSolutionBySolutionName");
        RestPageResponse<MLPSolution> mlpSolutionByServiceName = null;
        try {
            ICommonDataServiceRestClient dataServiceRestClient = getClient();
            mlpSolutionByServiceName = dataServiceRestClient.searchSolutions(solutoinNameParameter, false, new RestPageRequest());;
        } catch (IllegalArgumentException e) {
            throw new AcumosServiceException(AcumosServiceException.ErrorCode.INVALID_PARAMETER, e.getMessage());
        } catch (HttpClientErrorException e) {
            throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
        }
        return mlpSolutionByServiceName;
    }
}
