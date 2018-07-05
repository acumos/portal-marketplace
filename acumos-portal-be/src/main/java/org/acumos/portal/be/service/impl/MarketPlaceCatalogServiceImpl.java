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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
import java.util.UUID;
import java.util.stream.Collectors;

import org.acumos.cds.AccessTypeCode;
import org.acumos.cds.ArtifactTypeCode;
import org.acumos.cds.MessageSeverityCode;
import org.acumos.cds.StepStatusCode;
import org.acumos.cds.ValidationStatusCode;
import org.acumos.cds.client.CommonDataServiceRestClientImpl;
import org.acumos.cds.client.ICommonDataServiceRestClient;
import org.acumos.cds.domain.MLPArtifact;
import org.acumos.cds.domain.MLPModelType;
import org.acumos.cds.domain.MLPNotification;
import org.acumos.cds.domain.MLPSolution;
import org.acumos.cds.domain.MLPSolutionFavorite;
import org.acumos.cds.domain.MLPSolutionRating;
import org.acumos.cds.domain.MLPSolutionRevision;
import org.acumos.cds.domain.MLPSolutionWeb;
import org.acumos.cds.domain.MLPStepResult;
import org.acumos.cds.domain.MLPTag;
import org.acumos.cds.domain.MLPToolkitType;
import org.acumos.cds.domain.MLPUser;
import org.acumos.cds.transport.RestPageRequest;
import org.acumos.cds.transport.RestPageResponse;
import org.acumos.nexus.client.NexusArtifactClient;
import org.acumos.nexus.client.RepositoryLocation;
import org.acumos.portal.be.common.CommonConstants;
import org.acumos.portal.be.common.JsonRequest;
import org.acumos.portal.be.common.RestPageRequestBE;
import org.acumos.portal.be.common.RestPageResponseBE;
import org.acumos.portal.be.common.exception.AcumosServiceException;
import org.acumos.portal.be.service.MarketPlaceCatalogService;
import org.acumos.portal.be.service.NotificationService;
import org.acumos.portal.be.service.UserService;
import org.acumos.portal.be.transport.MLSolution;
import org.acumos.portal.be.transport.MLSolutionFavorite;
import org.acumos.portal.be.transport.MLSolutionRating;
import org.acumos.portal.be.transport.RestPageRequestPortal;
import org.acumos.portal.be.transport.User;
import org.acumos.portal.be.util.EELFLoggerDelegate;
import org.acumos.portal.be.util.JsonUtils;
import org.acumos.portal.be.util.PortalUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.maven.wagon.ConnectionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Service to Support Market Place Catalog and Manage models modules
 */
@Service
public class MarketPlaceCatalogServiceImpl implements MarketPlaceCatalogService {

	private static final EELFLoggerDelegate log = EELFLoggerDelegate.getLogger(MarketPlaceCatalogServiceImpl.class);

	@Autowired
	private Environment env;

	@Autowired
	private UserService userService;
	
	@Autowired
	private MarketPlaceCatalogService catalogService;
	
	@Autowired
	private NotificationService notificationService;

	private ICommonDataServiceRestClient getClient() {
		ICommonDataServiceRestClient client = new CommonDataServiceRestClientImpl(env.getProperty("cdms.client.url"),
				env.getProperty("cdms.client.username"), env.getProperty("cdms.client.password"));
		return client;
	}

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
			queryParameters.put("accessTypeCode", AccessTypeCode.PB.toString()); // Assuming
																					// 1
																					// is
																					// public
			queryParameters.put("validationStatusCode", ValidationStatusCode.PS.toString());
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
					MLPUser user = dataServiceRestClient.getUser(mlpSolution.getOwnerId());
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
			queryParameters.put("accessTypeCode", AccessTypeCode.PB.toString()); // Assuming
																					// 1
																					// is
																					// public
			queryParameters.put("validationStatusCode", ValidationStatusCode.PS.toString());
			// TODO Lets keep it simple by using List for now. Need to modify
			// this to use Pagination by providing page number and result fetch
			// size
			List<MLPSolution> mlpSolutions = new ArrayList<MLPSolution>();// dataServiceRestClient.searchSolutions(queryParameters,
																			// false);
			if (!PortalUtils.isEmptyList(mlpSolutions)) {
				mlSolutions = new ArrayList<>();
				for (MLPSolution mlpSolution : mlpSolutions) {
					MLSolution mlSolution = PortalUtils.convertToMLSolution(mlpSolution);
					MLPUser user = dataServiceRestClient.getUser(mlpSolution.getOwnerId());
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
	public MLSolution getSolution(String solutionId) throws AcumosServiceException {
		log.debug(EELFLoggerDelegate.debugLogger, "getSolution");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		MLSolution mlSolution = null;
		try {
			MLPSolution mlpSolution = dataServiceRestClient.getSolution(solutionId);
			if (mlpSolution != null) {
				mlSolution = PortalUtils.convertToMLSolution(mlpSolution);
				List<MLPToolkitType> toolkitTypeList = dataServiceRestClient.getToolkitTypes();
				if (toolkitTypeList.size() > 0) {
					for (MLPToolkitType toolkitType : toolkitTypeList) {
						if (toolkitType.getTypeCode() != null) {
							if (toolkitType.getTypeCode().equalsIgnoreCase(mlpSolution.getToolkitTypeCode())) {
								mlSolution.setTookitTypeName(toolkitType.getTypeName());
								break;
							}
						}
					}
				}
				List<MLPModelType> modelTypeList = dataServiceRestClient.getModelTypes();
				if (modelTypeList.size() > 0) {
					for (MLPModelType modelType : modelTypeList) {
						if (modelType.getTypeCode() != null) {
							if (modelType.getTypeCode().equalsIgnoreCase(mlpSolution.getModelTypeCode())) {
								mlSolution.setModelTypeName(modelType.getTypeName());
								break;
							}
						}
					}
				}

				MLPUser mlpUser = dataServiceRestClient.getUser(mlpSolution.getOwnerId());
				if (mlpUser != null) {
					mlSolution.setOwnerName(mlpUser.getFirstName().concat(" " + mlpUser.getLastName()));
				}
				/*
				 * CountTransport t =
				 * dataServiceRestClient.getSolutionDownloadCount(solutionId); if(t!=null){ int
				 * downloadCount = (int)t.getCount();
				 * mlSolution.setDownloadCount(downloadCount); }
				 */

				/*
				 * try { RestPageResponse<MLPSolutionRating> ratingListPaged =
				 * dataServiceRestClient.getSolutionRatings(solutionId, null);
				 * List<MLPSolutionRating> ratingList = null; if(ratingListPaged != null &&
				 * !PortalUtils.isEmptyList(ratingListPaged.getContent())) { ratingList =
				 * ratingListPaged.getContent(); if(ratingList.size()>0){ int solutionRating =
				 * ratingList.get(0).getRating(); mlSolution.setSolutionRating(solutionRating);
				 * } } } catch (Exception e) { log.error(EELFLoggerDelegate.errorLogger,
				 * "No ratings found for SolutionId={}", solutionId); }
				 */

				// code for download count, rating count
				try {
					MLPSolutionWeb solutionStats = dataServiceRestClient
							.getSolutionWebMetadata(mlSolution.getSolutionId());
					mlSolution.setDownloadCount(solutionStats.getDownloadCount().intValue());
					mlSolution.setRatingCount(solutionStats.getRatingCount().intValue());
					mlSolution.setViewCount(solutionStats.getViewCount().intValue());
					mlSolution.setSolutionRatingAvg(solutionStats.getRatingAverageTenths().intValue() / 10);
				} catch (Exception e) {
					log.error(EELFLoggerDelegate.errorLogger, "No stats found for SolutionId={}",
							mlSolution.getSolutionId());
				}

				List<MLPTag> tagList = dataServiceRestClient.getSolutionTags(solutionId);
				if (tagList.size() > 0) {
					mlSolution.setSolutionTagList(tagList);
				}
				List<MLPSolutionRevision> revisionList = dataServiceRestClient.getSolutionRevisions(solutionId);
				if (revisionList.size() > 0) {
					mlSolution.setRevisions(revisionList);
				}
				//Set co-owners list for model
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
	public List<MLSolution> getAllSolutions() {

		return null;
	}

	@Override
	public List<MLSolution> getSearchSolution(String search) throws AcumosServiceException {
		log.debug(EELFLoggerDelegate.debugLogger, "getSearchedSolutions");
		List<MLSolution> mlSolutions = null;
		try {
			ICommonDataServiceRestClient dataServiceRestClient = getClient();
			Map<String, Object> queryParameters = new HashMap<>();
			MLPSolution mlpSolution = new MLPSolution();
			queryParameters.put("modelTypeCode", mlpSolution.getModelTypeCode());
			queryParameters.put("solutionId", mlpSolution.getSolutionId());
			queryParameters.put("name", mlpSolution.getName());
			queryParameters.put("description", mlpSolution.getDescription());
			queryParameters.put("accessTypeCode", mlpSolution.getAccessTypeCode());
			if (search.equals(queryParameters)) {
				List<MLPSolution> mlpSolutions = new ArrayList<MLPSolution>();// dataServiceRestClient.searchSolutions(queryParameters,
																				// false);
				if (!PortalUtils.isEmptyList(mlpSolutions)) {
					mlSolutions = new ArrayList<>();
					MLSolution mlSolution = PortalUtils.convertToMLSolution(mlpSolution);
					mlSolutions.add(mlSolution);
					log.info("MLSolutions : " + mlpSolutions);
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
	public RestPageResponseBE<MLSolution> getSearchSolution(JsonRequest<RestPageRequestBE> restPageReqBe)
			throws AcumosServiceException {
		log.debug(EELFLoggerDelegate.debugLogger, "getSearchedSolutions");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		Map<String, String> queryParameters = new HashMap<>();
		;
		RestPageResponse<MLPSolution> mlpSolutionsRest = new RestPageResponse<MLPSolution>();
		List<MLSolution> content = new ArrayList<>();
		Set<String> tagSetForSolutions = new HashSet<>();
		RestPageResponseBE<MLSolution> mlSolutionsRest = new RestPageResponseBE<>(content);
		try {
			if (restPageReqBe != null && restPageReqBe.getBody() != null) {
				RestPageRequest pageRequest = new RestPageRequest();

				List<MLPSolution> filteredMLPSolutions = new ArrayList<>();
				List<MLPSolution> filteredMLPSolutionsTemp = new ArrayList<>();
				List<MLPSolution> originalSolutionsList = new ArrayList<MLPSolution>();
				int pageSize = 0;
				int index = 0;
				int interateCopy = 0;
				while (restPageReqBe.getBody().getSize().intValue() != filteredMLPSolutions.size()) {
					pageSize = pageSize + restPageReqBe.getBody().getPage();
					if (restPageReqBe.getBody().getPage() != null) {
						pageRequest.setPage(pageSize);
					} else {
						// default to 0
						pageRequest.setPage(0);
					}
					if (restPageReqBe.getBody().getSize() != null && restPageReqBe.getBody().getSize() > 0) {
						pageRequest.setSize(restPageReqBe.getBody().getSize());
					}

					queryParameters.put("created", "DESC");
					if (restPageReqBe.getBody().getPage() != null && restPageReqBe.getBody().getSize() != null) {
						pageRequest = new RestPageRequest(pageSize, restPageReqBe.getBody().getSize(), queryParameters);
					}
					// TODO Need to revisit the Sorting logic once Common Data
					// Service Client
					/*
					 * if(!PortalUtils.isEmptyOrNullString(restPageReqBe.getBody
					 * ().getSortingOrder())) { queryParameters.put("sort",
					 * restPageReqBe.getBody().getSortingOrder());
					 * pageRequest.setFieldToDirectionMap(queryParameters); }
					 */

					// 1. Check if searchTerm exists, if yes then use
					// findSolutionsBySearchTerm
					if (!PortalUtils.isEmptyOrNullString(restPageReqBe.getBody().getSearchTerm())) {
						log.debug(EELFLoggerDelegate.debugLogger,
								"getSearchedSolutions: searching Solutions with searcTerm:",
								restPageReqBe.getBody().getSearchTerm());
						mlpSolutionsRest = dataServiceRestClient
								.findSolutionsBySearchTerm(restPageReqBe.getBody().getSearchTerm(), pageRequest);
					} else {
						// 2. If searchTerm does not exists, get all the
						// Solutions
						pageRequest.setFieldToDirectionMap(queryParameters);
						mlpSolutionsRest = dataServiceRestClient.getSolutions(pageRequest);
					}

					if (mlpSolutionsRest.getContent().size() == 0 || pageSize > 9)
						break;
					// 3. Filter the RestPageResponse to use only Published,
					// ValitionStatuCode as PS and Active Solutions and
					// ModelType/ModelToolkitType

					originalSolutionsList = mlpSolutionsRest.getContent();
					filteredMLPSolutionsTemp = originalSolutionsList.stream().filter(mlpSolution -> ((PortalUtils
							.isEmptyOrNullString(mlpSolution.getValidationStatusCode())
							|| (!PortalUtils.isEmptyOrNullString(mlpSolution.getValidationStatusCode())
									&& ValidationStatusCode.PS.toString()
											.equalsIgnoreCase(mlpSolution.getValidationStatusCode())))
							&& (PortalUtils.isEmptyOrNullString(mlpSolution.getAccessTypeCode()) || (!PortalUtils
									.isEmptyOrNullString(mlpSolution.getAccessTypeCode())
									&& (AccessTypeCode.PB.toString().equalsIgnoreCase(mlpSolution.getAccessTypeCode())
											|| AccessTypeCode.OR.toString()
													.equalsIgnoreCase(mlpSolution.getAccessTypeCode()))))))
							.collect(Collectors.toList());

					if (!PortalUtils.isEmptyOrNullString(restPageReqBe.getBody().getModelToolkitType())) {
						// queryParameters.put("toolkitTypeCode",
						// restPageReqBe.getBody().getModelToolkitType());
						filteredMLPSolutionsTemp = filteredMLPSolutionsTemp.stream().filter(
								mlpSolution -> ((!PortalUtils.isEmptyOrNullString(mlpSolution.getToolkitTypeCode())
										&& restPageReqBe.getBody().getModelToolkitType()
												.contains(mlpSolution.getToolkitTypeCode()))))
								.collect(Collectors.toList());
					}
					if (!PortalUtils.isEmptyOrNullString(restPageReqBe.getBody().getModelType())) {
						// queryParameters.put("toolkitTypeCode",
						// restPageReqBe.getBody().getModelToolkitType());
						filteredMLPSolutionsTemp = filteredMLPSolutionsTemp.stream().filter(
								mlpSolution -> ((!PortalUtils.isEmptyOrNullString(mlpSolution.getModelTypeCode())
										&& restPageReqBe.getBody().getModelType()
												.contains(mlpSolution.getModelTypeCode()))))
								.collect(Collectors.toList());
					}
					if (!PortalUtils.isEmptyOrNullString(restPageReqBe.getBody().getActiveType())) {
						Boolean isActive = false;
						if (restPageReqBe.getBody().getActiveType().equalsIgnoreCase("Y")) {
							isActive = true;
						} else if (restPageReqBe.getBody().getActiveType().equalsIgnoreCase("N")) {
							isActive = false;
						}
						filteredMLPSolutionsTemp = filteredMLPSolutionsTemp.stream()
								.filter(mlpSolution -> Boolean.compare(
										restPageReqBe.getBody().getActiveType().equalsIgnoreCase("Y"),
										mlpSolution.isActive()) == 0)
								.collect(Collectors.toList());
					}

					/*
					 * for(int k=0;k<filteredMLPSolutionsTemp.size()&& index<9;k++){
					 * filteredMLPSolutions.add(index, filteredMLPSolutionsTemp.get(k)); index++; }
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
												} else {
													checkTemp = false;
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
					if (interateCopy == filteredMLPSolutions.size() && interateCopy != 0)
						break;

					pageSize++;
				}
				// -----------------------------------------------------
				String userFirstName = "";
				String userLastName = "";
				String userName = "";
				// List<MLPSolution> mlpSolutions = originalSolutionsList;

				if (!PortalUtils.isEmptyList(originalSolutionsList)) {
					int i = 0;
					for (MLPSolution mlpSol : filteredMLPSolutions) {

						MLSolution mlSolution = PortalUtils.convertToMLSolution(mlpSol);
						// Identify the OwnerName for each solution
						Map<String, Object> queryParams = new HashMap<>();
						queryParams.put("userId", mlpSol.getOwnerId());
						MLPUser mlpUser = dataServiceRestClient.getUser(mlpSol.getOwnerId());
						if (mlpUser != null) {
							// Lets loop through other solutions
							userFirstName = mlpUser.getFirstName();
							userLastName = mlpUser.getLastName();
							if (!PortalUtils.isEmptyOrNullString(mlpUser.getFirstName())) {
								userName = userFirstName;
								if (!PortalUtils.isEmptyOrNullString(mlpUser.getLastName())) {
									userName = userName + " " + mlpUser.getLastName();
								}
							}
							mlSolution.setOwnerName(userName);
							
						}
						List<MLPTag> tagList = dataServiceRestClient
								.getSolutionTags(filteredMLPSolutions.get(i).getSolutionId());
						if (tagList.size() > 0) {
							for (MLPTag tag : tagList) {
								tagSetForSolutions.add(tag.getTag());
							}
						}
						try {
							MLPSolutionWeb solutionStats = dataServiceRestClient
									.getSolutionWebMetadata(mlSolution.getSolutionId());
							mlSolution.setDownloadCount(solutionStats.getDownloadCount().intValue());
							mlSolution.setRatingCount(solutionStats.getRatingCount().intValue());
							mlSolution.setViewCount(solutionStats.getViewCount().intValue());
							mlSolution.setSolutionRatingAvg(solutionStats.getRatingAverageTenths().intValue() / 10);

						} catch (Exception e) {
							log.error(EELFLoggerDelegate.errorLogger, "No stats found for SolutionId={}",
									mlSolution.getSolutionId());
						}

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

						content.add(mlSolution);
						i++;
					}
					if (restPageReqBe.getBody().getSortBy() != null) {
						if (restPageReqBe.getBody().getSortBy().equalsIgnoreCase("MD")) {
							Collections.sort(content, new Comparator<MLSolution>() {
								public int compare(MLSolution ms1, MLSolution ms2) {
									return (int) (ms2.getDownloadCount() - ms1.getDownloadCount());
								}
							});
						} else if (restPageReqBe.getBody().getSortBy().equalsIgnoreCase("LD")) {
							Collections.sort(content, new Comparator<MLSolution>() {
								public int compare(MLSolution ms1, MLSolution ms2) {
									return (int) (ms1.getDownloadCount() - ms2.getDownloadCount());
								}
							});
						} else if (restPageReqBe.getBody().getSortBy().equalsIgnoreCase("HR")) {
							Collections.sort(content, new Comparator<MLSolution>() {
								public int compare(MLSolution ms1, MLSolution ms2) {
									return (int) (ms2.getViewCount() - ms1.getViewCount());
								}
							});
						} else if (restPageReqBe.getBody().getSortBy().equalsIgnoreCase("ML")) {
							Collections.sort(content, new Comparator<MLSolution>() {
								public int compare(MLSolution ms1, MLSolution ms2) {
									return (int) (ms2.getSolutionRating() - ms1.getSolutionRating());
								}
							});
						} else if (restPageReqBe.getBody().getSortBy().equalsIgnoreCase("FL")) {
							Collections.sort(content, new Comparator<MLSolution>() {
								public int compare(MLSolution ms1, MLSolution ms2) {
									return (int) (ms1.getSolutionRating() - ms2.getSolutionRating());
								}
							});
						}
						/*
						 * for ( MLPSolutionStats mlpSolutionStats : mlpSolutionStatsList) { MLPSolution
						 * mlpSolution = dataServiceRestClient.getSolution(mlpSolutionStats.
						 * getSolutionId()); //mlSolution =
						 * PortalUtils.convertToMLSolution(mlpSolution);
						 * originalSolutionsList.add(mlpSolution); }
						 */
					}

					mlSolutionsRest.setAllTagsSet(tagSetForSolutions);
					mlSolutionsRest.setContent(content);
				}
				mlpSolutionsRest.setNumberOfElements(filteredMLPSolutions.size());
			}
		} catch (IllegalArgumentException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INVALID_PARAMETER, e.getMessage());
		} catch (HttpClientErrorException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
		}
		return mlSolutionsRest;
	}

	// @Override
	public RestPageResponseBE<MLSolution> getFilteredSearchSolution(JsonRequest<RestPageRequestBE> restPageReqBe)
			throws AcumosServiceException {
		log.debug(EELFLoggerDelegate.debugLogger, "getSearchedSolutions");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		Map<String, String> queryParameters = new HashMap<>();
		;
		RestPageResponse<MLPSolution> mlpSolutionsRest = new RestPageResponse<MLPSolution>();
		List<MLSolution> content = new ArrayList<>();
		Set<String> tagSetForSolutions = new HashSet<>();
		RestPageResponseBE<MLSolution> mlSolutionsRest = new RestPageResponseBE<>(content);
		try {
			if (restPageReqBe != null && restPageReqBe.getBody() != null) {
				RestPageRequest pageRequest = new RestPageRequest();

				List<MLPSolution> filteredMLPSolutions = new ArrayList<>();
				List<MLPSolution> filteredMLPSolutionsTemp = new ArrayList<>();
				List<MLPSolution> originalSolutionsList = new ArrayList<MLPSolution>();
				int pageSize = 0;
				int index = 0;
				int interateCopy = 0;
				while (restPageReqBe.getBody().getSize().intValue() != filteredMLPSolutions.size()) {
					pageSize = pageSize + restPageReqBe.getBody().getPage();
					if (restPageReqBe.getBody().getPage() != null) {
						pageRequest.setPage(pageSize);
					} else {
						// default to 0
						pageRequest.setPage(0);
					}
					if (restPageReqBe.getBody().getSize() != null && restPageReqBe.getBody().getSize() > 0) {
						pageRequest.setSize(restPageReqBe.getBody().getSize());
					}

					queryParameters.put("created", "DESC");
					if (restPageReqBe.getBody().getPage() != null && restPageReqBe.getBody().getSize() != null) {
						pageRequest = new RestPageRequest(pageSize, restPageReqBe.getBody().getSize(), queryParameters);
					}
					// TODO Need to revisit the Sorting logic once Common Data
					// Service Client
					/*
					 * if(!PortalUtils.isEmptyOrNullString(restPageReqBe.getBody
					 * ().getSortingOrder())) { queryParameters.put("sort",
					 * restPageReqBe.getBody().getSortingOrder());
					 * pageRequest.setFieldToDirectionMap(queryParameters); }
					 */

					// 1. Check if searchTerm exists, if yes then use
					// findSolutionsBySearchTerm
					if (!PortalUtils.isEmptyOrNullString(restPageReqBe.getBody().getSearchTerm())) {
						log.debug(EELFLoggerDelegate.debugLogger,
								"getSearchedSolutions: searching Solutions with searcTerm:",
								restPageReqBe.getBody().getSearchTerm());
						mlpSolutionsRest = dataServiceRestClient
								.findSolutionsBySearchTerm(restPageReqBe.getBody().getSearchTerm(), pageRequest);
					} else {
						// 2. If searchTerm does not exists, get all the
						// Solutions
						pageRequest.setFieldToDirectionMap(queryParameters);
						mlpSolutionsRest = dataServiceRestClient.getSolutions(pageRequest);
					}

					if (mlpSolutionsRest.getContent().size() == 0 || pageSize > 9)
						break;
					// 3. Filter the RestPageResponse to use only Published,
					// ValitionStatuCode as PS and Active Solutions and
					// ModelType/ModelToolkitType

					originalSolutionsList = mlpSolutionsRest.getContent();
					filteredMLPSolutionsTemp = originalSolutionsList.stream().filter(mlpSolution -> ((PortalUtils
							.isEmptyOrNullString(mlpSolution.getValidationStatusCode())
							|| (!PortalUtils.isEmptyOrNullString(mlpSolution.getValidationStatusCode())
									&& ValidationStatusCode.PS.toString()
											.equalsIgnoreCase(mlpSolution.getValidationStatusCode())))
							&& (PortalUtils.isEmptyOrNullString(mlpSolution.getAccessTypeCode()) || (!PortalUtils
									.isEmptyOrNullString(mlpSolution.getAccessTypeCode())
									&& (AccessTypeCode.PB.toString().equalsIgnoreCase(mlpSolution.getAccessTypeCode())
											|| AccessTypeCode.OR.toString()
													.equalsIgnoreCase(mlpSolution.getAccessTypeCode()))))))
							.collect(Collectors.toList());

					if (!PortalUtils.isEmptyOrNullString(restPageReqBe.getBody().getModelToolkitType())) {
						// queryParameters.put("toolkitTypeCode",
						// restPageReqBe.getBody().getModelToolkitType());
						filteredMLPSolutionsTemp = filteredMLPSolutionsTemp.stream().filter(
								mlpSolution -> ((!PortalUtils.isEmptyOrNullString(mlpSolution.getToolkitTypeCode())
										&& restPageReqBe.getBody().getModelToolkitType()
												.contains(mlpSolution.getToolkitTypeCode()))))
								.collect(Collectors.toList());
					}
					if (!PortalUtils.isEmptyOrNullString(restPageReqBe.getBody().getModelType())) {
						// queryParameters.put("toolkitTypeCode",
						// restPageReqBe.getBody().getModelToolkitType());
						filteredMLPSolutionsTemp = filteredMLPSolutionsTemp.stream().filter(
								mlpSolution -> ((!PortalUtils.isEmptyOrNullString(mlpSolution.getModelTypeCode())
										&& restPageReqBe.getBody().getModelType()
												.contains(mlpSolution.getModelTypeCode()))))
								.collect(Collectors.toList());
					}
					if (!PortalUtils.isEmptyOrNullString(restPageReqBe.getBody().getActiveType())) {
						Boolean isActive = false;
						if (restPageReqBe.getBody().getActiveType().equalsIgnoreCase("Y")) {
							isActive = true;
						} else if (restPageReqBe.getBody().getActiveType().equalsIgnoreCase("N")) {
							isActive = false;
						}
						filteredMLPSolutionsTemp = filteredMLPSolutionsTemp.stream()
								.filter(mlpSolution -> Boolean.compare(
										restPageReqBe.getBody().getActiveType().equalsIgnoreCase("Y"),
										mlpSolution.isActive()) == 0)
								.collect(Collectors.toList());
					}

					/*
					 * for(int k=0;k<filteredMLPSolutionsTemp.size()&& index<9;k++){
					 * filteredMLPSolutions.add(index, filteredMLPSolutionsTemp.get(k)); index++; }
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
												} else {
													checkTemp = false;
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
					if (interateCopy == filteredMLPSolutions.size() && interateCopy != 0)
						break;

					pageSize++;
				}
				// -----------------------------------------------------
				String userFirstName = "";
				String userLastName = "";
				String userName = "";
				// List<MLPSolution> mlpSolutions = originalSolutionsList;

				if (!PortalUtils.isEmptyList(originalSolutionsList)) {
					int i = 0;
					for (MLPSolution mlpSol : filteredMLPSolutions) {

						MLSolution mlSolution = PortalUtils.convertToMLSolution(mlpSol);
						// Identify the OwnerName for each solution
						MLPUser user = dataServiceRestClient.getUser(mlpSol.getOwnerId());
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
								tagSetForSolutions.add(tag.getTag());
							}
						}
						try {
							MLPSolutionWeb solutionStats = dataServiceRestClient
									.getSolutionWebMetadata(mlSolution.getSolutionId());
							mlSolution.setDownloadCount(solutionStats.getDownloadCount().intValue());
							mlSolution.setRatingCount(solutionStats.getRatingCount().intValue());
							mlSolution.setViewCount(solutionStats.getViewCount().intValue());
							mlSolution.setSolutionRatingAvg(solutionStats.getRatingAverageTenths().intValue() / 10);

						} catch (Exception e) {
							log.error(EELFLoggerDelegate.errorLogger, "No stats found for SolutionId={}",
									mlSolution.getSolutionId());
						}
						content.add(mlSolution);
						i++;
					}
					if (restPageReqBe.getBody().getSortBy() != null) {
						if (restPageReqBe.getBody().getSortBy().equalsIgnoreCase("MD")) {
							Collections.sort(content, new Comparator<MLSolution>() {
								public int compare(MLSolution ms1, MLSolution ms2) {
									return (int) (ms2.getDownloadCount() - ms1.getDownloadCount());
								}
							});
						} else if (restPageReqBe.getBody().getSortBy().equalsIgnoreCase("LD")) {
							Collections.sort(content, new Comparator<MLSolution>() {
								public int compare(MLSolution ms1, MLSolution ms2) {
									return (int) (ms1.getDownloadCount() - ms2.getDownloadCount());
								}
							});
						} else if (restPageReqBe.getBody().getSortBy().equalsIgnoreCase("HR")) {
							Collections.sort(content, new Comparator<MLSolution>() {
								public int compare(MLSolution ms1, MLSolution ms2) {
									return (int) (ms2.getViewCount() - ms1.getViewCount());
								}
							});
						} else if (restPageReqBe.getBody().getSortBy().equalsIgnoreCase("ML")) {
							Collections.sort(content, new Comparator<MLSolution>() {
								public int compare(MLSolution ms1, MLSolution ms2) {
									return (int) (ms2.getSolutionRating() - ms1.getSolutionRating());
								}
							});
						} else if (restPageReqBe.getBody().getSortBy().equalsIgnoreCase("FL")) {
							Collections.sort(content, new Comparator<MLSolution>() {
								public int compare(MLSolution ms1, MLSolution ms2) {
									return (int) (ms1.getSolutionRating() - ms2.getSolutionRating());
								}
							});
						}
						/*
						 * for ( MLPSolutionStats mlpSolutionStats : mlpSolutionStatsList) { MLPSolution
						 * mlpSolution = dataServiceRestClient.getSolution(mlpSolutionStats.
						 * getSolutionId()); //mlSolution =
						 * PortalUtils.convertToMLSolution(mlpSolution);
						 * originalSolutionsList.add(mlpSolution); }
						 */
					}

					mlSolutionsRest.setAllTagsSet(tagSetForSolutions);
					mlSolutionsRest.setContent(content);
				}
				mlpSolutionsRest.setNumberOfElements(filteredMLPSolutions.size());
			}
		} catch (IllegalArgumentException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INVALID_PARAMETER, e.getMessage());
		} catch (HttpClientErrorException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
		}
		return mlSolutionsRest;
	}

	@Override
	public MLSolution deleteSolution(MLSolution mlSolution) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * @Override public List<MLSolution> getAllMySolutions(String userId,
	 * JsonRequest<RestPageRequestBE> restPageReqBe) {
	 */

	@Override
	public RestPageResponseBE<MLSolution> getAllMySolutions(String userId, JsonRequest<RestPageRequestBE> restPageReqBe)
			throws AcumosServiceException {
		log.debug(EELFLoggerDelegate.debugLogger, "getSearchedSolutions");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		Map<String, String> queryParameters = new HashMap<>();
		;
		RestPageResponse<MLPSolution> mlpSolutionsRest = null;
		RestPageResponse<MLPSolution> mlpSolutionsShareRest = null;
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
					filteredMLPSolutionsTemp = originalSolutionsList.stream()
							.filter(mlpSolution -> (!PortalUtils.isEmptyOrNullString(mlpSolution.getOwnerId())
									&& userId.equalsIgnoreCase(mlpSolution.getOwnerId())))
							.collect(Collectors.toList());

					mlpSolutionsShareRest = dataServiceRestClient.getUserAccessSolutions(userId, pageRequest);
					if (mlpSolutionsShareRest != null) {
						for (MLPSolution mlpSolution : mlpSolutionsShareRest) {
							filteredMLPSolutionsTemp.add(mlpSolution);
						}
					}

					if (!PortalUtils.isEmptyOrNullString(restPageReqBe.getBody().getAccessType())) {
						filteredMLPSolutionsTemp = filteredMLPSolutionsTemp.stream().filter(mlpSolution -> (PortalUtils
								.isEmptyOrNullString(mlpSolution.getAccessTypeCode())
								|| (!PortalUtils.isEmptyOrNullString(mlpSolution.getAccessTypeCode()) && restPageReqBe
										.getBody().getAccessType().contains(mlpSolution.getAccessTypeCode()))))
								.collect(Collectors.toList());
					}

					if (!PortalUtils.isEmptyOrNullString(restPageReqBe.getBody().getModelToolkitType())) {
						filteredMLPSolutionsTemp = filteredMLPSolutionsTemp.stream().filter(
								mlpSolution -> ((!PortalUtils.isEmptyOrNullString(mlpSolution.getToolkitTypeCode())
										&& restPageReqBe.getBody().getModelToolkitType()
												.contains(mlpSolution.getToolkitTypeCode()))))
								.collect(Collectors.toList());
					}

					if (!PortalUtils.isEmptyOrNullString(restPageReqBe.getBody().getModelType())) {
						filteredMLPSolutionsTemp = filteredMLPSolutionsTemp.stream().filter(
								mlpSolution -> ((!PortalUtils.isEmptyOrNullString(mlpSolution.getModelTypeCode())
										&& restPageReqBe.getBody().getModelType()
												.contains(mlpSolution.getModelTypeCode()))))
								.collect(Collectors.toList());
					}

					if (!PortalUtils.isEmptyOrNullString(restPageReqBe.getBody().getActiveType())) {
						Boolean isActive = false;
						if (restPageReqBe.getBody().getActiveType().equalsIgnoreCase("Y")) {
							isActive = true;
						} else if (restPageReqBe.getBody().getActiveType().equalsIgnoreCase("N")) {
							isActive = false;
						}
						filteredMLPSolutionsTemp = filteredMLPSolutionsTemp.stream()
								.filter(mlpSolution -> Boolean.compare(
										restPageReqBe.getBody().getActiveType().equalsIgnoreCase("Y"),
										mlpSolution.isActive()) == 0)
								.collect(Collectors.toList());
					}

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
						MLPUser mlpUser = dataServiceRestClient.getUser(mlpSol.getOwnerId());
						if (mlpUser != null) {

							// Lets loop through other solutions
							userFirstName = mlpUser.getFirstName();
							userLastName = mlpUser.getLastName();
							if (!PortalUtils.isEmptyOrNullString(mlpUser.getFirstName())) {
								userName = userFirstName;
								if (!PortalUtils.isEmptyOrNullString(mlpUser.getLastName())) {
									userName = userName + " " + mlpUser.getLastName();
								}
							}

							mlSolution.setOwnerName(userName);
						}
						try {
							MLPSolutionWeb solutionStats = dataServiceRestClient
									.getSolutionWebMetadata(mlSolution.getSolutionId());
							mlSolution.setDownloadCount(solutionStats.getDownloadCount().intValue());
							mlSolution.setRatingCount(solutionStats.getRatingCount().intValue());
							mlSolution.setViewCount(solutionStats.getViewCount().intValue());
							mlSolution.setSolutionRating(solutionStats.getRatingCount().intValue());
							mlSolution.setSolutionRatingAvg(solutionStats.getRatingAverageTenths().intValue() / 10);
						} catch (Exception e) {
							log.error(EELFLoggerDelegate.errorLogger, "No stats found for SolutionId={}",
									mlSolution.getSolutionId());
						}
						List<MLPTag> tagList = dataServiceRestClient
								.getSolutionTags(filteredMLPSolutions.get(i).getSolutionId());
						if (tagList.size() > 0) {
							for (MLPTag tag : tagList) {
								filteredTagSet.add(tag.getTag());
							}
							mlSolution.setSolutionTagList(tagList);
						}

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

						content.add(mlSolution);
						i++;
					}

					if (restPageReqBe.getBody().getSortBy() != null) {
						if (restPageReqBe.getBody().getSortBy().equalsIgnoreCase("MD")) {
							Collections.sort(content, new Comparator<MLSolution>() {
								public int compare(MLSolution ms1, MLSolution ms2) {
									return (int) (ms2.getDownloadCount() - ms1.getDownloadCount());
								}
							});
						} else if (restPageReqBe.getBody().getSortBy().equalsIgnoreCase("LD")) {
							Collections.sort(content, new Comparator<MLSolution>() {
								public int compare(MLSolution ms1, MLSolution ms2) {
									return (int) (ms1.getDownloadCount() - ms2.getDownloadCount());
								}
							});
						} else if (restPageReqBe.getBody().getSortBy().equalsIgnoreCase("HR")) {
							Collections.sort(content, new Comparator<MLSolution>() {
								public int compare(MLSolution ms1, MLSolution ms2) {
									return (int) (ms2.getViewCount() - ms1.getViewCount());
								}
							});
						} else if (restPageReqBe.getBody().getSortBy().equalsIgnoreCase("ML")) {
							Collections.sort(content, new Comparator<MLSolution>() {
								public int compare(MLSolution ms1, MLSolution ms2) {
									return (int) (ms2.getSolutionRating() - ms1.getSolutionRating());
								}
							});
						} else if (restPageReqBe.getBody().getSortBy().equalsIgnoreCase("FL")) {
							Collections.sort(content, new Comparator<MLSolution>() {
								public int compare(MLSolution ms1, MLSolution ms2) {
									return (int) (ms1.getSolutionRating() - ms2.getSolutionRating());
								}
							});
						}
						/*
						 * for ( MLPSolutionStats mlpSolutionStats : mlpSolutionStatsList) { MLPSolution
						 * mlpSolution = dataServiceRestClient.getSolution(mlpSolutionStats.
						 * getSolutionId()); //mlSolution =
						 * PortalUtils.convertToMLSolution(mlpSolution);
						 * originalSolutionsList.add(mlpSolution); }
						 */
					}
					mlSolutionsRest.setContent(content);
				}
				mlpSolutionsRest.setNumberOfElements(filteredMLPSolutions.size());
				mlSolutionsRest.setFilteredTagSet(filteredTagSet);
			}
		} catch (IllegalArgumentException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INVALID_PARAMETER, e.getMessage());
		} catch (HttpClientErrorException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
		}
		return mlSolutionsRest;
	}

	/*
	 * @Override public List<MLSolution> searchSolution(String searchTerm){
	 * log.debug(EELFLoggerDelegate.debugLogger, "searchSolution");
	 * ICommonDataServiceRestClient dataServiceRestClient = getClient();
	 * List<MLSolution> mlSolution = null; List<MLPSolution> mlpSolution =
	 * dataServiceRestClient.findSolutionsBySearchTerm(searchTerm); MLPSolution
	 * mlpSolutions = new MLPSolution(); if(PortalUtils.isEmptyList(mlpSolution)){
	 * mlSolution = new ArrayList<>(); MLSolution mlSolutions =
	 * PortalUtils.convertToMLSolution(mlpSolutions); mlSolution.add(mlSolutions); }
	 * 
	 * return mlSolution; }
	 */

	/*
	 * Old Implementation
	 * 
	 * @Override public List<MLSolution> getAllMySolutions(String userId) {
	 * log.debug(EELFLoggerDelegate.debugLogger, "getAllPublishedSolutions");
	 * List<MLSolution> mlSolutions = null; ICommonDataServiceRestClient
	 * dataServiceRestClient = getClient(); Map<String, Object> queryParameters =
	 * new HashMap<>(); queryParameters.put("ownerId", userId); //Fetch all
	 * solutions for give User //TODO Lets keep it simple by using List for now.
	 * Need to modify this to use Pagination by providing page number and result
	 * fetch size List<MLPSolution> mlpSolutions =
	 * dataServiceRestClient.searchSolutions(queryParameters, false);
	 * if(!PortalUtils.isEmptyList(mlpSolutions)) { mlSolutions = new ArrayList<>();
	 * for ( MLPSolution mlpSolution : mlpSolutions) { MLSolution mlSolution =
	 * PortalUtils.convertToMLSolution(mlpSolution); //Identify the OwnerName for
	 * each solution Map<String, Object> queryParams = new HashMap<>();
	 * queryParams.put("userId", mlpSolution.getOwnerId()); List<MLPUser> mlpUsers =
	 * dataServiceRestClient.searchUsers(queryParams, false); for(MLPUser user :
	 * mlpUsers) { if(user != null) { mlSolution.setOwnerName(user.getLoginName());
	 * mlSolutions.add(mlSolution); //Lets loop through other solutions break; } } }
	 * } return mlSolutions; }
	 */

	@Override
	public MLSolution updateSolution(MLSolution mlSolution, String solutionId) throws AcumosServiceException {
		log.debug(EELFLoggerDelegate.debugLogger, "updateSolution");
		try {
			ICommonDataServiceRestClient dataServiceRestClient = getClient();
			dataServiceRestClient.updateSolution(PortalUtils.convertToMLPSolution(mlSolution));
		} catch (IllegalArgumentException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INVALID_PARAMETER, e.getMessage());
		} catch (HttpClientErrorException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
		}
		return mlSolution;
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
						MLPUser user = dataServiceRestClient.getUser(mlpSol.getOwnerId());
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
						try {
							MLPSolutionWeb solutionStats = dataServiceRestClient
									.getSolutionWebMetadata(mlSolution.getSolutionId());
							mlSolution.setDownloadCount(solutionStats.getDownloadCount().intValue());
							mlSolution.setRatingCount(solutionStats.getRatingCount().intValue());
							mlSolution.setViewCount(solutionStats.getViewCount().intValue());
							mlSolution.setSolutionRatingAvg(solutionStats.getRatingAverageTenths().intValue() / 10);
						} catch (Exception e) {
							log.error(EELFLoggerDelegate.errorLogger, "No stats found for SolutionId={}",
									mlSolution.getSolutionId());
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
					mlSolutionsRest.setContent(content);
				}
				mlpSolutionsRest.setNumberOfElements(filteredMLPSolutions.size());
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
	public RestPageResponseBE<MLSolution> getSolutionCount(String userId) {
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		Map<String, String> queryParameters = new HashMap<>();
		queryParameters.put("created", "DESC");
		RestPageResponse<MLPSolution> mlpSolutionsRest = null;
		List<MLPSolution> originalSolutionsList = new ArrayList<MLPSolution>();
		List<MLSolution> content = new ArrayList<>();
		RestPageResponseBE<MLSolution> mlSolutionsRest = new RestPageResponseBE<>(content);
		RestPageResponse<MLPSolution> mlpSolutionsShareRest = null;

		mlpSolutionsRest = dataServiceRestClient.getSolutions(new RestPageRequest(0, 2000, queryParameters));

		originalSolutionsList = mlpSolutionsRest.getContent().stream()
				.filter(mlpSolution -> (!PortalUtils.isEmptyOrNullString(mlpSolution.getOwnerId())
						&& userId.equalsIgnoreCase(mlpSolution.getOwnerId())))
				.collect(Collectors.toList());

		// shared models for user added
		mlpSolutionsShareRest = dataServiceRestClient.getUserAccessSolutions(userId,
				new RestPageRequest(0, 1000, queryParameters));
		if (mlpSolutionsShareRest != null) {
			for (MLPSolution mlpSolution : mlpSolutionsShareRest) {
				originalSolutionsList.add(mlpSolution);
			}
		}

		if (originalSolutionsList != null) {
			int prModelCnt = 0;
			int pbModelCnt = 0;
			int orModelCnt = 0;
			int deletedModelCnt = 0;
			for (MLPSolution mlpsol : originalSolutionsList) {
				if (mlpsol.getAccessTypeCode().equals("PR") && mlpsol.isActive())
					prModelCnt++;
				if (mlpsol.getAccessTypeCode().equals("PB") && mlpsol.isActive())
					pbModelCnt++;
				if (mlpsol.getAccessTypeCode().equals("OR") && mlpsol.isActive())
					orModelCnt++;
				if (!mlpsol.isActive())
					deletedModelCnt++;
			}
			mlSolutionsRest.setPrivateModelCount(prModelCnt);
			mlSolutionsRest.setPublicModelCount(pbModelCnt);
			mlSolutionsRest.setCompanyModelCount(orModelCnt);
			mlSolutionsRest.setDeletedModelCount(deletedModelCnt);
		}
		return mlSolutionsRest;
	}

	@Override
	public MLPSolutionRating getUserRatings(String solutionId, String userId) {
		log.debug(EELFLoggerDelegate.debugLogger, "addSolutionUserAccess");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		MLPSolutionRating rating = dataServiceRestClient.getSolutionRating(solutionId, userId);
		return rating;
	}

	@Override
	public RestPageResponseBE<MLSolution> findPortalSolutions(RestPageRequestPortal pageReqPortal) {
		log.debug(EELFLoggerDelegate.debugLogger, "findPortalSolutions");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		
		RestPageResponse<MLPSolution> response = dataServiceRestClient.findPortalSolutions(
				pageReqPortal.getNameKeyword(), pageReqPortal.getDescriptionKeyword(), pageReqPortal.isActive(),
				pageReqPortal.getOwnerIds(), pageReqPortal.getAccessTypeCodes(), pageReqPortal.getModelTypeCodes(),
				pageReqPortal.getValidationStatusCodes(), pageReqPortal.getTags(), pageReqPortal.getPageRequest());

		List<MLSolution> content = new ArrayList<>();
		RestPageResponseBE<MLSolution> mlSolutionsRest = new RestPageResponseBE<>(content);
		Set<String> filteredTagSet = new HashSet<>();
		List<MLPSolution> filteredSolList = new ArrayList<>();

		if (response.getContent() != null) {
			List<MLPSolution> mlpSolList = response.getContent();
			filteredSolList.addAll(mlpSolList);
			// To show shared models with user in MyModel
			if (pageReqPortal.getOwnerIds() != null) {
				RestPageResponse<MLPSolution> mlpSolutionsShareRest = null;
				String ownerId[]=pageReqPortal.getOwnerIds();
				mlpSolutionsShareRest = dataServiceRestClient.getUserAccessSolutions(ownerId[0],
						new RestPageRequest(0, 1000));
				mlSolutionsRest.setModelsSharedWithUser(mlpSolutionsShareRest.getContent());
				if (mlpSolutionsShareRest != null) {
					List<String> accessTypeCodes = pageReqPortal.getAccessTypeCodes()!= null? new ArrayList<String>(Arrays.asList(pageReqPortal.getAccessTypeCodes())) : null; 
                    for (MLPSolution mlpSolution : mlpSolutionsShareRest.getContent()) {
                        if(accessTypeCodes != null){
                            if(accessTypeCodes.contains(mlpSolution.getAccessTypeCode()) && mlpSolution.isActive()){
                                filteredSolList.add(mlpSolution);
                            }
                        }else {
                            if(!mlpSolution.isActive()){
                                filteredSolList.add(mlpSolution);
                            }
                        }
                    }
				}
			}

			for (MLPSolution mlpSol : filteredSolList) {
				MLSolution mlSolution = PortalUtils.convertToMLSolution(mlpSol);

				// set rating, view, download count for model
				try {
					MLPSolutionWeb solutionStats = dataServiceRestClient
							.getSolutionWebMetadata(mlSolution.getSolutionId());
					mlSolution.setDownloadCount(solutionStats.getDownloadCount().intValue());
					mlSolution.setRatingCount(solutionStats.getRatingCount().intValue());
					mlSolution.setViewCount(solutionStats.getViewCount().intValue());
					mlSolution.setSolutionRating(solutionStats.getRatingCount().intValue());
					mlSolution.setSolutionRatingAvg(solutionStats.getRatingAverageTenths().floatValue() / 10);
				} catch (Exception e) {
					log.error(EELFLoggerDelegate.errorLogger, "No stats found for SolutionId={}",
							mlSolution.getSolutionId());
				}

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
							if(StepStatusCode.FA.toString().equals(step.getStatusCode())) {
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
				content.add(mlSolution);
			}

			if (pageReqPortal.getSortBy() != null) {
				// sort by Most Downloaded
				if (pageReqPortal.getSortBy().equalsIgnoreCase("MD")) {
					Collections.sort(content, new Comparator<MLSolution>() {
						public int compare(MLSolution ms1, MLSolution ms2) {
							return (int) (ms2.getDownloadCount() - ms1.getDownloadCount());
						}
					});
					// sort by Least Downloaded
				} else if (pageReqPortal.getSortBy().equalsIgnoreCase("FD")) {
					Collections.sort(content, new Comparator<MLSolution>() {
						public int compare(MLSolution ms1, MLSolution ms2) {
							return (int) (ms1.getDownloadCount() - ms2.getDownloadCount());
						}
					});
					// sort by Highest Reach
				} else if (pageReqPortal.getSortBy().equalsIgnoreCase("HR")) {
					Collections.sort(content, new Comparator<MLSolution>() {
						public int compare(MLSolution ms1, MLSolution ms2) {
							return (int) (ms2.getViewCount() - ms1.getViewCount());
						}
					});
					// sort by Lowest Reach
				} else if (pageReqPortal.getSortBy().equalsIgnoreCase("LR")) {
					Collections.sort(content, new Comparator<MLSolution>() {
						public int compare(MLSolution ms1, MLSolution ms2) {
							return (int) (ms1.getViewCount() - ms2.getViewCount());
						}
					});
					// sort by Most Like
				} else if (pageReqPortal.getSortBy().equalsIgnoreCase("ML")) {
					Collections.sort(content, new Comparator<MLSolution>() {
						public int compare(MLSolution ms1, MLSolution ms2) {
							return (int) (ms2.getSolutionRating() - ms1.getSolutionRating());
						}
					});
					// sort by Fiewest like
				} else if (pageReqPortal.getSortBy().equalsIgnoreCase("FL")) {
					Collections.sort(content, new Comparator<MLSolution>() {
						public int compare(MLSolution ms1, MLSolution ms2) {
							return (int) (ms1.getSolutionRating() - ms2.getSolutionRating());
						}
					});
				}
				// sort for Older
				else if (pageReqPortal.getSortBy().equalsIgnoreCase("OLD")) {
					Collections.sort(content, new Comparator<MLSolution>() {
						public int compare(MLSolution m1, MLSolution m2) {
							return m1.getModified().compareTo(m2.getModified());
						}
					});
				}
				// sort by Most Recent
				else if (pageReqPortal.getSortBy().equalsIgnoreCase("MR")) {
					Collections.sort(content, new Comparator<MLSolution>() {
						public int compare(MLSolution m1, MLSolution m2) {
							return m2.getModified().compareTo(m1.getModified());
						}
					});
				}
				// sort by Owner Name / Author
				else if (pageReqPortal.getSortBy().equalsIgnoreCase("ownerName")) {
					Collections.sort(content, new Comparator<MLSolution>() {
						public int compare(MLSolution m1, MLSolution m2) {
							return m1.getOwnerName().compareTo(m2.getOwnerName());
						}
					});
				}
			}

			mlSolutionsRest.setContent(content);
			mlSolutionsRest.setFilteredTagSet(filteredTagSet);
			mlSolutionsRest.setPageCount(response.getTotalPages());
            		mlSolutionsRest.setTotalElements((int)response.getTotalElements());
		}

		return mlSolutionsRest;
	}

	@Override
	public RestPageResponse<MLPSolution> getUserAccessSolutions(String userId, RestPageRequest pageRequest) {
		log.debug(EELFLoggerDelegate.debugLogger, "findPortalSolutions");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		RestPageResponse<MLPSolution> mlSolutions = dataServiceRestClient.getUserAccessSolutions(userId, pageRequest);
		return mlSolutions;
	}

	@Override
	public MLPSolutionWeb getSolutionWebMetadata(String solutionId) {
		log.debug(EELFLoggerDelegate.debugLogger, "getSolutionWebMetadata");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		MLPSolutionWeb solutionStats = dataServiceRestClient.getSolutionWebMetadata(solutionId);
		if (solutionStats.getRatingAverageTenths() != null) {
			Long avgRating = solutionStats.getRatingAverageTenths() / 10;
			solutionStats.setRatingAverageTenths(avgRating);
		}
		return solutionStats;
	}


	private ByteArrayOutputStream getPayload(String uri) throws AcumosServiceException {

		RepositoryLocation repositoryLocation = new RepositoryLocation();
	    repositoryLocation.setId("1");

	    repositoryLocation.setUrl(env.getProperty("nexus.url"));
	    repositoryLocation.setUsername("nexus.username");
	    repositoryLocation.setPassword("nexus.password");
	    // if you need a proxy to access the Nexus
	    if (!PortalUtils.isEmptyOrNullString(env.getProperty("nexus.proxy"))) {
				repositoryLocation.setProxy(env.getProperty("nexus.proxy"));
		    }
		NexusArtifactClient artifactClient = new NexusArtifactClient(repositoryLocation);
		
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
		String[] accessTypeCodes = { CommonConstants.PUBLIC, CommonConstants.ORGANIZATION };

		/*MLPSolution solution = dataServiceRestClient.getSolution(solutionId);
		if(solution.getAccessTypeCode().equals(CommonConstants.PUBLIC)){
			accessTypeCodes =new String[] { CommonConstants.ORGANIZATION, CommonConstants.PUBLIC };
		}else if(solution.getAccessTypeCode().equals(CommonConstants.ORGANIZATION)){
			accessTypeCodes = new String[] {CommonConstants.ORGANIZATION, CommonConstants.PUBLIC };
		}else {
			accessTypeCodes= new String[] {CommonConstants.PUBLIC};
			accessTypeCodes= new String[] {CommonConstants.ORGANIZATION};
		}*/
		String[] name = { solName };

		Map<String, String> queryParameters = new HashMap<>();
		//Fetch the maximum possible records. Need an api that could return the exact match of names along with other nested filter criteria
		RestPageResponse<MLPSolution> searchSolResp = dataServiceRestClient.findPortalSolutions(name, null, true, null,
				accessTypeCodes, null, null, null, new RestPageRequest(0, 10000, queryParameters));
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

		return true;
	}
}
