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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.acumos.cds.AccessTypeCode;
import org.acumos.cds.ValidationStatusCode;
import org.acumos.cds.client.CommonDataServiceRestClientImpl;
import org.acumos.cds.client.ICommonDataServiceRestClient;
import org.acumos.cds.domain.MLPArtifact;
import org.acumos.cds.domain.MLPModelType;
import org.acumos.cds.domain.MLPSolution;
import org.acumos.cds.domain.MLPSolutionFavorite;
import org.acumos.cds.domain.MLPSolutionRating;
import org.acumos.cds.domain.MLPSolutionRevision;
import org.acumos.cds.domain.MLPSolutionWeb;
import org.acumos.cds.domain.MLPTag;
import org.acumos.cds.domain.MLPToolkitType;
import org.acumos.cds.domain.MLPUser;
import org.acumos.cds.transport.RestPageRequest;
import org.acumos.cds.transport.RestPageResponse;
import org.acumos.portal.be.common.JsonRequest;
import org.acumos.portal.be.common.RestPageRequestBE;
import org.acumos.portal.be.common.RestPageResponseBE;
import org.acumos.portal.be.common.exception.AcumosServiceException;
import org.acumos.portal.be.service.MarketPlaceCatalogService;
import org.acumos.portal.be.transport.MLSolution;
import org.acumos.portal.be.transport.MLSolutionFavorite;
import org.acumos.portal.be.transport.MLSolutionRating;
import org.acumos.portal.be.transport.User;
import org.acumos.portal.be.util.EELFLoggerDelegate;
import org.acumos.portal.be.util.PortalUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;


/**
 * @author Ashwin Sharma
 * Service to Support Market Place Catalog & Manage models modules
 */ 
@Service
public class MarketPlaceCatalogServiceImpl implements MarketPlaceCatalogService {

	private static final EELFLoggerDelegate log = EELFLoggerDelegate.getLogger(MarketPlaceCatalogServiceImpl.class);

	@Autowired
	private Environment env;
	
	private ICommonDataServiceRestClient getClient() {
		ICommonDataServiceRestClient client = new CommonDataServiceRestClientImpl(env.getProperty("cdms.client.url"), env.getProperty("cdms.client.username"), env.getProperty("cdms.client.password"));
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
			List<MLPSolution> mlpSolutions = dataServiceRestClient.searchSolutions(queryParameters, false);
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
					Map<String, Object> queryParams = new HashMap<>();
					queryParameters.put("userId", mlpSolution.getOwnerId());
					List<MLPUser> mlpUsers = dataServiceRestClient.searchUsers(queryParams, false);
					for (MLPUser user : mlpUsers) {
						if (user != null) {
							mlSolution.setOwnerName(user.getFirstName());
							mlSolutions.add(mlSolution);
							// Lets loop through other solutions
							break;
						}
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
			List<MLPSolution> mlpSolutions = dataServiceRestClient.searchSolutions(queryParameters, false);
			if (!PortalUtils.isEmptyList(mlpSolutions)) {
				mlSolutions = new ArrayList<>();
				for (MLPSolution mlpSolution : mlpSolutions) {
					MLSolution mlSolution = PortalUtils.convertToMLSolution(mlpSolution);
					// Identify the OwnerName for each solution
					Map<String, Object> queryParams = new HashMap<>();
					queryParameters.put("userId", mlpSolution.getOwnerId());
					List<MLPUser> mlpUsers = dataServiceRestClient.searchUsers(queryParams, false);
					for (MLPUser user : mlpUsers) {
						if (user != null) {
							mlSolution.setOwnerName(user.getFirstName());
							mlSolutions.add(mlSolution);
							// Lets loop through other solutions
							break;
						}
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
						if (toolkitType.getToolkitCode() != null) {
							if (toolkitType.getToolkitCode().equalsIgnoreCase(mlpSolution.getToolkitTypeCode())) {
								mlSolution.setTookitTypeName(toolkitType.getToolkitName());
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

				Map<String, Object> queryParams = new HashMap<>();
				queryParams.put("userId", mlpSolution.getOwnerId());
				List<MLPUser> mlpUsers = dataServiceRestClient.searchUsers(queryParams, false);
				for (MLPUser user : mlpUsers) {
					if (user != null) {
						mlSolution.setOwnerName(user.getFirstName().concat(" " + user.getLastName()));
					}
				}
				/*
				 * CountTransport t =
				 * dataServiceRestClient.getSolutionDownloadCount(solutionId);
				 * if(t!=null){ int downloadCount = (int)t.getCount();
				 * mlSolution.setDownloadCount(downloadCount); }
				 */

				/*
				 * try { RestPageResponse<MLPSolutionRating> ratingListPaged =
				 * dataServiceRestClient.getSolutionRatings(solutionId, null);
				 * List<MLPSolutionRating> ratingList = null; if(ratingListPaged
				 * != null &&
				 * !PortalUtils.isEmptyList(ratingListPaged.getContent())) {
				 * ratingList = ratingListPaged.getContent();
				 * if(ratingList.size()>0){ int solutionRating =
				 * ratingList.get(0).getRating();
				 * mlSolution.setSolutionRating(solutionRating); } } } catch
				 * (Exception e) { log.error(EELFLoggerDelegate.errorLogger,
				 * "No ratings found for SolutionId={}", solutionId); }
				 */

				// code for download count, rating count
				try {
					MLPSolutionWeb solutionStats = dataServiceRestClient
							.getSolutionWebMetadata(mlSolution.getSolutionId());
					mlSolution.setDownloadCount(solutionStats.getDownloadCount().intValue());
					mlSolution.setRatingCount(solutionStats.getRatingCount().intValue());
					mlSolution.setViewCount(solutionStats.getViewCount().intValue());
					mlSolution.setSolutionRating(solutionStats.getRatingAverageTenths().intValue() / 10);
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
			}
		}catch (ArithmeticException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.ARITHMATIC_EXCEPTION, e.getMessage());
		}  catch (IllegalArgumentException e) {
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
				List<MLPSolution> mlpSolutions = dataServiceRestClient.searchSolutions(queryParameters, false);
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
					filteredMLPSolutionsTemp = originalSolutionsList
							.stream().filter(
									mlpSolution -> ((PortalUtils
											.isEmptyOrNullString(mlpSolution.getValidationStatusCode())
											|| (!PortalUtils.isEmptyOrNullString(mlpSolution.getValidationStatusCode())
													&& ValidationStatusCode.PS.toString()
															.equalsIgnoreCase(mlpSolution.getValidationStatusCode())))
											&& (PortalUtils.isEmptyOrNullString(mlpSolution.getAccessTypeCode())
													|| (!PortalUtils
															.isEmptyOrNullString(mlpSolution.getAccessTypeCode())
															&& (AccessTypeCode.PB.toString()
																	.equalsIgnoreCase(mlpSolution.getAccessTypeCode())
																	|| AccessTypeCode.OR.toString().equalsIgnoreCase(
																			mlpSolution.getAccessTypeCode()))))))
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
					 * for(int k=0;k<filteredMLPSolutionsTemp.size()&&
					 * index<9;k++){ filteredMLPSolutions.add(index,
					 * filteredMLPSolutionsTemp.get(k)); index++; }
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
						List<MLPUser> mlpUsers = dataServiceRestClient.searchUsers(queryParams, false);
						for (MLPUser user : mlpUsers) {
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
								/*
								 * CountTransport t = null; try{ t=
								 * dataServiceRestClient.
								 * getSolutionDownloadCount(filteredMLPSolutions
								 * .get(i).getSolutionId()); }catch(Exception
								 * e){
								 * 
								 * }
								 */

								/*
								 * if(t!=null){ int downloadCount =
								 * (int)t.getCount();
								 * mlSolution.setDownloadCount(downloadCount); }
								 */
								/*
								 * try { RestPageResponse<MLPSolutionRating>
								 * ratingListPaged =
								 * dataServiceRestClient.getSolutionRatings(
								 * filteredMLPSolutions.get(i).getSolutionId(),
								 * null); List<MLPSolutionRating> ratingList =
								 * null; if(ratingListPaged != null &&
								 * !PortalUtils.isEmptyList(ratingListPaged.
								 * getContent())) { ratingList =
								 * ratingListPaged.getContent();
								 * if(ratingList.size()>0){ int solutionRating =
								 * ratingList.get(0).getRating();
								 * mlSolution.setSolutionRating(solutionRating);
								 * } } } catch (Exception e) {
								 * log.error(EELFLoggerDelegate.errorLogger,
								 * "No ratings found for SolutionId={}",
								 * filteredMLPSolutions.get(i).getSolutionId());
								 * }
								 */
								/*
								 * List<MLPSolutionTag> tagList =
								 * dataServiceRestClient.getSolutionTags(
								 * filteredMLPSolutions.get(i).getSolutionId());
								 * if (tagList.size() > 0) { for (MLPSolutionTag
								 * tag : tagList) {
								 * tagSetForSolutions.add(tag.getTag()); } }
								 */
								break;
							}
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
							mlSolution.setSolutionRating(solutionStats.getRatingAverageTenths().intValue() / 10);

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
						 * for ( MLPSolutionStats mlpSolutionStats :
						 * mlpSolutionStatsList) { MLPSolution mlpSolution =
						 * dataServiceRestClient.getSolution(mlpSolutionStats.
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

	/*@Override
    public List<MLSolution> getAllMySolutions(String userId, JsonRequest<RestPageRequestBE> restPageReqBe) {*/
	
	@Override
	public RestPageResponseBE<MLSolution> getAllMySolutions(String userId, JsonRequest<RestPageRequestBE> restPageReqBe)
			throws AcumosServiceException {
		log.debug(EELFLoggerDelegate.debugLogger, "getSearchedSolutions");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		Map<String, String> queryParameters = new HashMap<>();;
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
						Map<String, Object> queryParams = new HashMap<>();
						queryParams.put("userId", mlpSol.getOwnerId());
						List<MLPUser> mlpUsers = dataServiceRestClient.searchUsers(queryParams, false);
						for (MLPUser user : mlpUsers) {
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
								/*
								 * CountTransport t = dataServiceRestClient.
								 * getSolutionDownloadCount(filteredMLPSolutions
								 * .get(i).getSolutionId()); if(t!=null){ int
								 * downloadCount = (int)t.getCount();
								 * mlSolution.setDownloadCount(downloadCount); }
								 */
								/*
								 * try { RestPageResponse<MLPSolutionRating>
								 * ratingListPaged =
								 * dataServiceRestClient.getSolutionRatings(
								 * filteredMLPSolutions.get(i).getSolutionId(),
								 * null); List<MLPSolutionRating> ratingList =
								 * null; if(ratingListPaged != null &&
								 * !PortalUtils.isEmptyList(ratingListPaged.
								 * getContent())) { ratingList =
								 * ratingListPaged.getContent();
								 * if(ratingList.size()>0){ int solutionRating =
								 * ratingList.get(0).getRating();
								 * mlSolution.setSolutionRating(solutionRating);
								 * } } } catch (Exception e) {
								 * log.error(EELFLoggerDelegate.errorLogger,
								 * "No ratings found for SolutionId={}",
								 * filteredMLPSolutions.get(i).getSolutionId());
								 * }
								 */

								/*
								 * List<MLPSolutionTag> tagList =
								 * dataServiceRestClient.getSolutionTags(
								 * filteredMLPSolutions.get(i).getSolutionId());
								 * if(tagList.size()>0){ for (MLPSolutionTag tag
								 * : tagList) {
								 * filteredTagSet.add(tag.getTag()); }
								 * mlSolution.setSolutionTagList(tagList); }
								 */
								break;
							}
						}
						try {
							MLPSolutionWeb solutionStats = dataServiceRestClient
									.getSolutionWebMetadata(mlSolution.getSolutionId());
							mlSolution.setDownloadCount(solutionStats.getDownloadCount().intValue());
							mlSolution.setRatingCount(solutionStats.getRatingCount().intValue());
							mlSolution.setViewCount(solutionStats.getViewCount().intValue());
							mlSolution.setSolutionRating(solutionStats.getRatingAverageTenths().intValue() / 10);
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
						 * for ( MLPSolutionStats mlpSolutionStats :
						 * mlpSolutionStatsList) { MLPSolution mlpSolution =
						 * dataServiceRestClient.getSolution(mlpSolutionStats.
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
	
	/*@Override
	public List<MLSolution> searchSolution(String searchTerm){
		log.debug(EELFLoggerDelegate.debugLogger, "searchSolution");
        ICommonDataServiceRestClient dataServiceRestClient = getClient();
        List<MLSolution>  mlSolution = null;
        List<MLPSolution> mlpSolution = dataServiceRestClient.findSolutionsBySearchTerm(searchTerm);
        MLPSolution mlpSolutions = new MLPSolution();
        if(PortalUtils.isEmptyList(mlpSolution)){
        	mlSolution  = new ArrayList<>();
        		MLSolution mlSolutions = PortalUtils.convertToMLSolution(mlpSolutions);
        		mlSolution.add(mlSolutions);
        }
      
		return mlSolution;
	}*/

	

	/*Old Implementation
	 * @Override
	public List<MLSolution> getAllMySolutions(String userId) {
		log.debug(EELFLoggerDelegate.debugLogger, "getAllPublishedSolutions");
		List<MLSolution> mlSolutions = null;
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		Map<String, Object> queryParameters = new HashMap<>();
		queryParameters.put("ownerId", userId); //Fetch all solutions for give User
		//TODO Lets keep it simple by using List for now. Need to modify this to use Pagination by providing page number and result fetch size
		List<MLPSolution> mlpSolutions = dataServiceRestClient.searchSolutions(queryParameters, false);
		if(!PortalUtils.isEmptyList(mlpSolutions)) {
			mlSolutions = new ArrayList<>();
			for ( MLPSolution mlpSolution : mlpSolutions) {
				MLSolution mlSolution = PortalUtils.convertToMLSolution(mlpSolution);
				//Identify the OwnerName for each solution
				Map<String, Object> queryParams = new HashMap<>();
				queryParams.put("userId", mlpSolution.getOwnerId());
				List<MLPUser> mlpUsers =  dataServiceRestClient.searchUsers(queryParams, false);
				for(MLPUser user : mlpUsers) {
					if(user != null) {
						mlSolution.setOwnerName(user.getLoginName());
						mlSolutions.add(mlSolution);
						//Lets loop through other solutions
						break;
					}
				}
			}
		}
		return mlSolutions;
	}*/
	
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
		try {
			ICommonDataServiceRestClient dataServiceRestClient = getClient();
			mlpSolutionRevisions = dataServiceRestClient.getSolutionRevisionArtifacts(solutionId, revisionId);
		} catch (IllegalArgumentException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INVALID_PARAMETER, e.getMessage());
		} catch (HttpClientErrorException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
		}
		return mlpSolutionRevisions;
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
	public RestPageResponseBE<MLSolution> getTagBasedSolutions(String tag,
			JsonRequest<RestPageRequestBE> restPageReqBe) throws AcumosServiceException {

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
						Map<String, Object> queryParams = new HashMap<>();
						queryParams.put("userId", mlpSol.getOwnerId());
						List<MLPUser> mlpUsers = dataServiceRestClient.searchUsers(queryParams, false);
						for (MLPUser user : mlpUsers) {
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
								/*
								 * CountTransport t = dataServiceRestClient.
								 * getSolutionDownloadCount(
								 * filteredMLPSolutions.get(i).getSolutionId());
								 * if(t!=null){ int downloadCount =
								 * (int)t.getCount();
								 * mlSolution.setDownloadCount(downloadCount); }
								 */
								/*
								 * try { RestPageResponse<MLPSolutionRating>
								 * ratingListPaged =
								 * dataServiceRestClient.getSolutionRatings(
								 * filteredMLPSolutions.get(i).getSolutionId(),
								 * null); List<MLPSolutionRating> ratingList =
								 * null; if(ratingListPaged != null &&
								 * !PortalUtils.isEmptyList(ratingListPaged.
								 * getContent())) { ratingList =
								 * ratingListPaged.getContent();
								 * if(ratingList.size()>0){ int solutionRating =
								 * ratingList.get(0).getRating();
								 * mlSolution.setSolutionRating(solutionRating);
								 * } } } catch (Exception e) {
								 * log.error(EELFLoggerDelegate.errorLogger,
								 * "No ratings found for SolutionId={}",
								 * filteredMLPSolutions.get(i).getSolutionId());
								 * }
								 */

								/*
								 * List<MLPSolutionTag> tagList =
								 * dataServiceRestClient.getSolutionTags(
								 * filteredMLPSolutions.get(i).getSolutionId());
								 * if(tagList.size()>0){ for (MLPSolutionTag tag
								 * : tagList) {
								 * filteredTagSet.add(tag.getTag()); }
								 * mlSolution.setSolutionTagList(tagList); }
								 */
								break;
							}
						}
						try {
							MLPSolutionWeb solutionStats = dataServiceRestClient
									.getSolutionWebMetadata(mlSolution.getSolutionId());
							mlSolution.setDownloadCount(solutionStats.getDownloadCount().intValue());
							mlSolution.setRatingCount(solutionStats.getRatingCount().intValue());
							mlSolution.setViewCount(solutionStats.getViewCount().intValue());
							mlSolution.setSolutionRating(solutionStats.getRatingAverageTenths().intValue() / 10);
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

}
	
