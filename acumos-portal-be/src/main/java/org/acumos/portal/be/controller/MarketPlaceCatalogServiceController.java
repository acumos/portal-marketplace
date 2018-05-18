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
package org.acumos.portal.be.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acumos.cds.MessageSeverityCode;
import org.acumos.cds.domain.MLPArtifact;
import org.acumos.cds.domain.MLPNotification;
import org.acumos.cds.domain.MLPSolution;
import org.acumos.cds.domain.MLPSolutionFavorite;
import org.acumos.cds.domain.MLPSolutionRating;
import org.acumos.cds.domain.MLPSolutionRevision;
import org.acumos.cds.domain.MLPSolutionWeb;
import org.acumos.cds.domain.MLPTag;
import org.acumos.cds.domain.MLPUser;
import org.acumos.cds.transport.RestPageRequest;
import org.acumos.cds.transport.RestPageResponse;
import org.acumos.portal.be.APINames;
import org.acumos.portal.be.common.JSONTags;
import org.acumos.portal.be.common.JsonRequest;
import org.acumos.portal.be.common.JsonResponse;
import org.acumos.portal.be.common.RestPageRequestBE;
import org.acumos.portal.be.common.RestPageResponseBE;
import org.acumos.portal.be.common.exception.AcumosServiceException;
import org.acumos.portal.be.service.MarketPlaceCatalogService;
import org.acumos.portal.be.service.NotificationService;
import org.acumos.portal.be.service.PushAndPullSolutionService;
import org.acumos.portal.be.service.UserService;
import org.acumos.portal.be.transport.MLSolution;
import org.acumos.portal.be.transport.MLSolutionRating;
import org.acumos.portal.be.transport.RestPageRequestPortal;
import org.acumos.portal.be.transport.User;
import org.acumos.portal.be.util.EELFLoggerDelegate;
import org.acumos.portal.be.util.PortalUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.ApiOperation;

@Controller
@RequestMapping("/")
public class MarketPlaceCatalogServiceController extends AbstractController {

	private static final EELFLoggerDelegate log = EELFLoggerDelegate
			.getLogger(MarketPlaceCatalogServiceController.class);

	@Autowired
	private MarketPlaceCatalogService catalogService;

	@Autowired
	private NotificationService notificationService;

	@Autowired
	private UserService userService;

	@Autowired
	private PushAndPullSolutionService pushAndPullSolutionService;

	/**
	 * 
	 */
	public MarketPlaceCatalogServiceController() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 * @param restPageReq
	 *            containing request parameters like page, size, searchTerm,
	 *            modelType and modelToolkitType
	 * @return Paginated Response with List of the MLP Solutions
	 */
	@ApiOperation(value = "Gets a list of Published Solutions for Market Place Catalog.", response = RestPageResponseBE.class)
	@RequestMapping(value = { APINames.SOLUTIONS }, method = RequestMethod.POST, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<RestPageResponseBE<MLSolution>> getSolutionsList(HttpServletRequest request,
			@RequestBody JsonRequest<RestPageRequestBE> restPageReq, HttpServletResponse response) {
		log.debug(EELFLoggerDelegate.debugLogger, "getSolutionsList");
		RestPageResponseBE<MLSolution> mlSolutions = null;
		JsonResponse<RestPageResponseBE<MLSolution>> data = new JsonResponse<>();
		try {
			if (restPageReq != null) {
				mlSolutions = catalogService.getSearchSolution(restPageReq);
			}
			if (mlSolutions != null) {
				data.setResponseBody(mlSolutions);
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
				data.setResponseDetail("Solutions fetched Successfully");
			}
		} catch (AcumosServiceException e) {
			data.setErrorCode(e.getErrorCode());
			data.setResponseDetail(e.getMessage());
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred Fetching Solutions for Market Place Catalog",
					e);
		}
		return data;
	}

	@ApiOperation(value = "Gets a Solution Detail for the given SolutionId. Same API can be used for both Solution Owner view as well as General user. API will return isOwner as true if the user is owner of the solution", response = MLSolution.class)
	@RequestMapping(value = { APINames.SOLUTIONS_DETAILS }, method = RequestMethod.GET, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<MLSolution> getSolutionsDetails(HttpServletRequest request,
			@PathVariable("solutionId") String solutionId, HttpServletResponse response) {
		MLSolution solutionDetail = null;
		JsonResponse<MLSolution> data = new JsonResponse<>();
		try {
			solutionDetail = catalogService.getSolution(solutionId);
			if (solutionDetail != null) {
				data.setResponseBody(solutionDetail);
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
				data.setResponseDetail("Solutions fetched Successfully");
				log.debug(EELFLoggerDelegate.debugLogger, "getSolutionsDetails :  ", solutionDetail);
			}
			response.setStatus(HttpServletResponse.SC_OK);
		} catch (AcumosServiceException e) {
			data.setErrorCode(e.getErrorCode());
			data.setResponseDetail(e.getMessage());
			log.error(EELFLoggerDelegate.errorLogger,
					"Exception Occurred Fetching Solutions Detail for solutionId :" + "solutionId", e);
		}
		return data;
	}

	@ApiOperation(value = "Gets a All Solutions for the User for Manage Models Screen.", response = MLSolution.class, responseContainer = "List")
	@RequestMapping(value = { APINames.MANAGE_MY_SOLUTIONS }, method = RequestMethod.POST, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<RestPageResponseBE<MLSolution>> getAllMySolutions(HttpServletRequest request,
			@PathVariable("userId") String userId, @RequestBody JsonRequest<RestPageRequestBE> restPageReq,
			HttpServletResponse response) {
		// List<MLSolution> mlSolutions = null;
		RestPageResponseBE<MLSolution> mlSolutions = null;
		// JsonResponse<List<MLSolution>> data = null;
		JsonResponse<RestPageResponseBE<MLSolution>> data = new JsonResponse<>();
		try {
			mlSolutions = catalogService.getAllMySolutions(userId, restPageReq);
			if (mlSolutions != null) {
				data.setResponseBody(mlSolutions);
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
				data.setResponseDetail("Solutions fetched Successfully");
				log.debug(EELFLoggerDelegate.debugLogger, "getMySolutions: size is {} ", mlSolutions.getSize());
			}
			// response.setStatus(HttpServletResponse.SC_OK);
		} catch (AcumosServiceException e) {
			data.setErrorCode(e.getErrorCode());
			data.setResponseDetail(e.getMessage());
			log.error(EELFLoggerDelegate.errorLogger,
					"Exception Occurred Fetching Solutions for a User for Manage My Models", e);
		}
		return data;
	}

	@ApiOperation(value = "Get search solution according to queryparamters sent.", response = MLSolution.class, responseContainer = "List")
	@RequestMapping(value = { APINames.SEARCH_SOLUTION }, method = RequestMethod.GET, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<List<MLSolution>> getSearchSolutions(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "search", required = true) String search) {
		List<MLSolution> mlSolutions = null;
		JsonResponse<List<MLSolution>> data = new JsonResponse<>();
		try {
			mlSolutions = catalogService.getSearchSolution(search);
			if (mlSolutions != null) {
				data.setResponseBody(mlSolutions);
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
				data.setResponseDetail("Solutions fetched Successfully");
				log.debug(EELFLoggerDelegate.debugLogger, "getSolutionsList: size is {} ", mlSolutions.size());
			}
		} catch (AcumosServiceException e) {
			data.setErrorCode(e.getErrorCode());
			data.setResponseDetail(e.getMessage());
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred Fetching Solutions for Market Place Catalog",
					e);
		}
		return data;
	}

	/**
	 * @param mlSolution
	 *            Solution
	 * @return List of Paginated ML Solutions in JSON format.
	 */
	@ApiOperation(value = "Gets a list of Paginated Solutions for Market Place Catalog.", response = MLSolution.class, responseContainer = "List")
	@RequestMapping(value = { APINames.PAGINATED_SOLUTIONS }, method = RequestMethod.POST, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<RestPageResponse<MLPSolution>> getPaginatedList(
			@RequestBody JsonRequest<MLSolution> mlSolution) {
		RestPageResponse<MLPSolution> paginatedSolution = null;
		JsonResponse<RestPageResponse<MLPSolution>> data = new JsonResponse<>();
		try {
			Integer page = mlSolution.getBody().getPageNo();
			Integer size = mlSolution.getBody().getSize();
			String sortingOrder = mlSolution.getBody().getSortingOrder();
			paginatedSolution = catalogService.getAllPaginatedSolutions(page, size, sortingOrder);
			if (paginatedSolution != null) {
				data.setResponseBody(paginatedSolution);
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
				data.setResponseDetail("Solutions fetched Successfully");
				log.debug(EELFLoggerDelegate.debugLogger, "getSolutionsList: size is {} ", paginatedSolution.getSize());
			}
		} catch (AcumosServiceException e) {
			data.setErrorCode(e.getErrorCode());
			data.setResponseDetail(e.getMessage());
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred Fetching Solutions for Market Place Catalog",
					e);
		}
		return data;
	}

	@ApiOperation(value = "Updates a given Solution for a provided SolutionId.", response = MLSolution.class)
	@RequestMapping(value = { APINames.SOLUTIONS_UPDATE }, method = RequestMethod.PUT, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<MLSolution> updateSolutionDetails(HttpServletRequest request, HttpServletResponse response,
			@PathVariable("solutionId") String solutionId, @RequestBody JsonRequest<MLSolution> mlSolution) {
		log.debug(EELFLoggerDelegate.debugLogger, "updateSolutionDetails={}", solutionId);
		MLSolution solutionDetail = null;
		JsonResponse<MLSolution> data = new JsonResponse<>();
		try {
			if (mlSolution.getBody() != null) {
				catalogService.updateSolution(mlSolution.getBody(), solutionId);
				data.setResponseBody(solutionDetail);
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
				data.setResponseDetail("Solutions updated Successfully");
				response.setStatus(HttpServletResponse.SC_OK);
			} else
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_EXCEPTION);
		} catch (AcumosServiceException e) {
			data.setErrorCode(e.getErrorCode());
			data.setResponseDetail(e.getMessage());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred while updateSolutionDetails()", e);
		}
		return data;
	}

	/**
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 * @param solutionId
	 *            solution ID
	 * @return List of Published ML Solutions in JSON format.
	 */
	@ApiOperation(value = "Gets a list of Solution Revision from the Catalog of the local Acumos Instance .", response = MLPSolutionRevision.class, responseContainer = "List")
	@RequestMapping(value = { APINames.SOLUTIONS_REVISIONS }, method = RequestMethod.GET, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<List<MLPSolutionRevision>> getSolutionsRevisionList(HttpServletRequest request,
			HttpServletResponse response, @PathVariable("solutionId") String solutionId) {
		JsonResponse<List<MLPSolutionRevision>> data = new JsonResponse<List<MLPSolutionRevision>>();
		List<MLPSolutionRevision> peerCatalogSolutionRevisions = null;
		try {
			peerCatalogSolutionRevisions = catalogService.getSolutionRevision(solutionId);
			if (peerCatalogSolutionRevisions != null) {
				data.setResponseBody(peerCatalogSolutionRevisions);
				data.setResponseCode(String.valueOf(HttpServletResponse.SC_OK));
				data.setResponseDetail(JSONTags.TAG_STATUS_SUCCESS);
				data.setStatus(true);
				response.setStatus(HttpServletResponse.SC_OK);
				log.debug(EELFLoggerDelegate.debugLogger, "getSolutionsRevisionList: size is {} ",
						peerCatalogSolutionRevisions.size());
			}
		} catch (AcumosServiceException e) {
			data.setErrorCode(e.getErrorCode());
			data.setResponseDetail(e.getMessage());
			data.setStatus(false);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			log.error(EELFLoggerDelegate.errorLogger,
					"Exception Occurred Fetching Solution Revisions for Market Place Catalog", e);
		}
		return data;
	}

	/**
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 * @param solutionId
	 *            solution ID
	 * @param revisionId
	 *            revision ID
	 * @return List of Published ML Solutions in JSON format.
	 */
	@ApiOperation(value = "Gets a list of Solution Revision Artifacts from the Catalog of the local Acumos Instance .", response = MLPArtifact.class, responseContainer = "List")
	@RequestMapping(value = {
			APINames.SOLUTIONS_REVISIONS_ARTIFACTS }, method = RequestMethod.GET, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<List<MLPArtifact>> getSolutionsRevisionArtifactList(HttpServletRequest request,
			HttpServletResponse response, @PathVariable("solutionId") String solutionId,
			@PathVariable("revisionId") String revisionId) {
		JsonResponse<List<MLPArtifact>> data = new JsonResponse<List<MLPArtifact>>();
		List<MLPArtifact> peerSolutionArtifacts = null;
		try {
			peerSolutionArtifacts = catalogService.getSolutionArtifacts(solutionId, revisionId);
			if (peerSolutionArtifacts != null) {
				/*
				 * //re-encode the artifact uri { UriComponentsBuilder uriBuilder =
				 * UriComponentsBuilder.fromHttpUrl(request.getRequestURL().toString()); for
				 * (MLPArtifact artifact: peerSolutionArtifacts) {
				 * artifact.setUri(uriBuilder.replacePath("/artifacts/" +
				 * artifact.getArtifactId() + "/download") .toUriString()); } }
				 */
				data.setResponseBody(peerSolutionArtifacts);
				data.setResponseCode(String.valueOf(HttpServletResponse.SC_OK));
				data.setResponseDetail(JSONTags.TAG_STATUS_SUCCESS);
				data.setStatus(true);
				response.setStatus(HttpServletResponse.SC_OK);
				log.debug(EELFLoggerDelegate.debugLogger, "getSolutionsRevisionArtifactList: size is {} ",
						peerSolutionArtifacts.size());
			}
		} catch (AcumosServiceException e) {
			data.setResponseCode(String.valueOf(HttpServletResponse.SC_BAD_REQUEST));
			data.setErrorCode(e.getErrorCode());
			data.setResponseDetail(e.getMessage());
			data.setStatus(false);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			log.error(EELFLoggerDelegate.errorLogger,
					"Exception Occurred Fetching Solution Revisions Artifacts for Market Place Catalog", e);
		}
		return data;
	}

	@ApiOperation(value = "Add tag for a provided SolutionId.", response = MLSolution.class)
	@RequestMapping(value = { APINames.ADD_TAG }, method = RequestMethod.PUT, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<MLSolution> addSolutionTag(HttpServletRequest request,
			@PathVariable("solutionId") String solutionId, @PathVariable("tag") String tag,
			HttpServletResponse response) {
		log.debug(EELFLoggerDelegate.debugLogger, "addSolutionTag={}", solutionId);
		JsonResponse<MLSolution> data = new JsonResponse<>();
		try {
			if (!PortalUtils.isEmptyOrNullString(solutionId) && !PortalUtils.isEmptyOrNullString(tag)) {
				catalogService.addSolutionTag(solutionId, tag);
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
				data.setResponseDetail("Solutions updated Successfully");
				response.setStatus(HttpServletResponse.SC_OK);
			} else
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_EXCEPTION);
		} catch (AcumosServiceException e) {
			data.setErrorCode(e.getErrorCode());
			data.setResponseDetail(e.getMessage());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred while updateSolutionDetails()", e);
		}
		return data;
	}

	@ApiOperation(value = "Updates a given Solution for a provided SolutionId.", response = MLSolution.class)
	@RequestMapping(value = { APINames.DROP_TAG }, method = RequestMethod.DELETE, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<MLSolution> dropSolutionTag(HttpServletRequest request,
			@PathVariable("solutionId") String solutionId, @PathVariable("tag") String tag,
			HttpServletResponse response) {
		log.debug(EELFLoggerDelegate.debugLogger, "addSolutionTag={}", solutionId);
		JsonResponse<MLSolution> data = new JsonResponse<>();
		try {
			if (!PortalUtils.isEmptyOrNullString(solutionId) && !PortalUtils.isEmptyOrNullString(tag)) {
				catalogService.dropSolutionTag(solutionId, tag);
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
				data.setResponseDetail("Solutions updated Successfully");
				response.setStatus(HttpServletResponse.SC_OK);
			} else
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_EXCEPTION);
		} catch (AcumosServiceException e) {
			data.setErrorCode(e.getErrorCode());
			data.setResponseDetail(e.getMessage());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred while updateSolutionDetails()", e);
		}
		return data;
	}

	/**
	 * 
	 * @param restPageReq
	 *            rest page request
	 * @return Rest page response
	 */
	@ApiOperation(value = "Gets a list of tags for Market Place Catalog.", response = RestPageResponseBE.class)
	@RequestMapping(value = { APINames.TAGS }, method = RequestMethod.PUT, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<RestPageResponseBE> getTagsList(@RequestBody JsonRequest<RestPageRequest> restPageReq) {
		log.debug(EELFLoggerDelegate.debugLogger, "getSolutionsList");
		List<String> mlTagsList = new ArrayList<>();
		JsonResponse<RestPageResponseBE> data = new JsonResponse<>();
		try {
			mlTagsList = catalogService.getTags(restPageReq);
			if (mlTagsList != null) {
				List test = new ArrayList<>();
				RestPageResponseBE responseBody = new RestPageResponseBE<>(test);
				responseBody.setTags(mlTagsList);
				data.setResponseBody(responseBody);
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
				data.setResponseDetail("Tags fetched Successfully");
			} else {
				data.setErrorCode(JSONTags.TAG_ERROR_CODE);
				data.setResponseDetail("Exception Occurred Fetching tags for Market Place Catalog");
				log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred Fetching tags for Market Place Catalog");
			}
		} catch (AcumosServiceException e) {
			data.setErrorCode(e.getErrorCode());
			data.setResponseDetail(e.getMessage());
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred Fetching tags for Market Place Catalog", e);
		}
		return data;
	}

	@ApiOperation(value = "Gets a All Solutions for the User for Manage Models Screen.", response = MLSolution.class, responseContainer = "List")
	@RequestMapping(value = { APINames.SEARCH_SOLUTION_TAGS }, method = RequestMethod.POST, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<RestPageResponseBE<MLSolution>> getTagsSolutions(@PathVariable("tags") String tags,
			@RequestBody JsonRequest<RestPageRequestBE> restPageReq) {
		// List<MLSolution> mlSolutions = null;
		RestPageResponseBE<MLSolution> mlSolutions = null;
		// JsonResponse<List<MLSolution>> data = null;
		JsonResponse<RestPageResponseBE<MLSolution>> data = new JsonResponse<>();
		try {
			mlSolutions = catalogService.getTagBasedSolutions(tags, restPageReq);
			if (mlSolutions != null) {
				data.setResponseBody(mlSolutions);
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
				data.setResponseDetail("Solutions fetched Successfully");
				log.debug(EELFLoggerDelegate.debugLogger, "getMySolutions: size is {} ", mlSolutions.getSize());
			}
			// response.setStatus(HttpServletResponse.SC_OK);
		} catch (AcumosServiceException e) {
			data.setErrorCode(e.getErrorCode());
			data.setResponseDetail(e.getMessage());
			log.error(EELFLoggerDelegate.errorLogger,
					"Exception Occurred Fetching Solutions for a User for Manage My Models", e);
		}
		return data;
	}

	@ApiOperation(value = "Gets a user access Detail for the given SolutionId.", response = User.class)
	@RequestMapping(value = { APINames.SOLUTION_USER_ACCESS }, method = RequestMethod.GET, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<RestPageResponseBE> getSolutionUserAccess(HttpServletRequest request,
			@PathVariable("solutionId") String solutionId, HttpServletResponse response) {
		List<User> userList = new ArrayList<>();
		JsonResponse<RestPageResponseBE> data = new JsonResponse<>();
		try {
			if (!PortalUtils.isEmptyOrNullString(solutionId)) {
				userList = catalogService.getSolutionUserAccess(solutionId);
				if (userList != null) {
					List test = new ArrayList<>();
					RestPageResponseBE responseBody = new RestPageResponseBE<>(test);
					responseBody.setUserList(userList);
					data.setResponseBody(responseBody);
					data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
					data.setResponseDetail("Users for solution fetched Successfully");
					log.debug(EELFLoggerDelegate.debugLogger, "getSolutionUserAccess :  ", userList);
				} else {
					data.setErrorCode(JSONTags.TAG_ERROR_CODE_FAILURE);
					data.setResponseDetail("Error occured while fetching Users for solution");
					log.error(EELFLoggerDelegate.errorLogger,
							"Error Occurred Fetching Users for solution :" + solutionId);
				}
			} else {
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_FAILURE);
				data.setResponseDetail("solutionId not present");
			}

		} catch (AcumosServiceException e) {
			data.setErrorCode(e.getErrorCode());
			data.setResponseDetail(e.getMessage());
			log.error(EELFLoggerDelegate.errorLogger,
					"Exception Occurred Fetching Solutions Detail for solutionId :" + "solutionId", e);
		}
		return data;
	}

	@ApiOperation(value = "Adds  user access Detail for the given SolutionId.")
	@RequestMapping(value = {
			APINames.SOLUTION_USER_ACCESS_ADD }, method = RequestMethod.POST, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<User> addSolutionUserAccess(HttpServletRequest request,
			@PathVariable("solutionId") String solutionId, @RequestBody JsonRequest<List<String>> userId,
			HttpServletResponse response) {
		JsonResponse<User> data = new JsonResponse<>();
		List<User> userList = new ArrayList<>();
		boolean exist = false;
		try {
			List<String> userIdList = userId.getBody();
			if (!PortalUtils.isEmptyOrNullString(solutionId)) {
				userList = catalogService.getSolutionUserAccess(solutionId);
				if (userList != null) {
					for (User user : userList) {
						if (user.getUserId().equals(userId)) {
							exist = true;
							break;
						}
					}
				}
			}

			if (!exist) {
				catalogService.addSolutionUserAccess(solutionId, userIdList);

				// code to create notification
				for (String userID : userIdList) {
					MLPNotification notification = new MLPNotification();
					String notifMsg = null;
					MLSolution solutionDetail = catalogService.getSolution(solutionId);
					MLPUser mlpUser = userService.findUserByUserId(userID);
					notifMsg = solutionDetail.getName() + " shared with " + mlpUser.getLoginName();				
					notification.setMessage(notifMsg);
					notification.setTitle(notifMsg);
					notification.setMsgSeverityCode(MessageSeverityCode.ME.toString());
					notificationService.generateNotification(notification, mlpUser.getUserId());
				}
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
				data.setResponseDetail("Users access for solution added Successfully");
				log.debug(EELFLoggerDelegate.debugLogger, "addSolutionUserAccess :  ");
			} else {
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_FAILURE);
				data.setResponseDetail("User already assigned for solution");
				log.error(EELFLoggerDelegate.errorLogger, "Error User already assigned for solution :" + solutionId);
			}
		} catch (AcumosServiceException e) {
			data.setErrorCode(e.getErrorCode());
			data.setResponseDetail(e.getMessage());
			log.error(EELFLoggerDelegate.errorLogger,
					"Exception Occurred while addSolutionUserAccess() :" + "solutionId", e);
		}
		return data;
	}

	@ApiOperation(value = "Adds  user access Detail for the given SolutionId.", response = User.class)
	@RequestMapping(value = {
			APINames.SOLUTION_USER_ACCESS_DELETE }, method = RequestMethod.DELETE, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<User> dropSolutionUserAccess(HttpServletRequest request,
			@PathVariable("solutionId") String solutionId, @PathVariable("userId") String userId,
			HttpServletResponse response) {
		JsonResponse<User> data = new JsonResponse<>();
		try {
			if (!PortalUtils.isEmptyOrNullString(solutionId) && !PortalUtils.isEmptyOrNullString(userId)) {
				catalogService.dropSolutionUserAccess(solutionId, userId);
				// code to create notification
				MLPNotification notification = new MLPNotification();
				String notificationMsg = null;
				MLSolution solutionDetail = catalogService.getSolution(solutionId);
				MLPUser user = userService.findUserByUserId(userId);				
				notificationMsg = solutionDetail.getName() + " unshared with " + user.getLoginName();				
				notification.setMessage(notificationMsg);
				notification.setTitle(notificationMsg);
				notification.setMsgSeverityCode(MessageSeverityCode.ME.toString());
				notificationService.generateNotification(notification, userId);
				
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
				data.setResponseDetail("Users access for solution droped Successfully");
				log.debug(EELFLoggerDelegate.debugLogger, "dropSolutionUserAccess :  ");
			} else {
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_FAILURE);
				data.setResponseDetail("Failure solutionId/userId not present");
				log.error(EELFLoggerDelegate.errorLogger,
						"Exception Occurred Fetching Users for solution :" + solutionId);
			}
		} catch (AcumosServiceException e) {
			data.setErrorCode(e.getErrorCode());
			data.setResponseDetail(e.getMessage());
			log.error(EELFLoggerDelegate.errorLogger,
					"Exception Occurred while dropSolutionUserAccess() :" + "solutionId", e);
		}
		return data;
	}

	@ApiOperation(value = "Update solution view count", response = MLSolution.class)
	@RequestMapping(value = { APINames.UPDATE_VIEW_COUNT }, method = RequestMethod.PUT, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<MLSolution> incrementSolutionViewCount(HttpServletRequest request,
			@PathVariable("solutionId") String solutionId, HttpServletResponse response) {
		MLSolution solutionDetail = null;
		JsonResponse<MLSolution> data = new JsonResponse<>();
		try {
			catalogService.incrementSolutionViewCount(solutionId);
			// code to create notification	
			MLSolution solution = catalogService.getSolution(solutionId);
			int viewCount = solution.getViewCount();
			if (viewCount != 0 && viewCount % 10 == 0) {
				MLPNotification notification = new MLPNotification();
				String notificationMsg = null;
				notificationMsg = "View count for " + solution.getName() + " increased by 10";
				notification.setMessage(notificationMsg);
				notification.setTitle(notificationMsg);
				notification.setMsgSeverityCode(MessageSeverityCode.ME.toString());
				notificationService.generateNotification(notification, solution.getOwnerId());
			}
			data.setResponseBody(solution);
			data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
			data.setResponseDetail("Solutions fetched Successfully");
			log.debug(EELFLoggerDelegate.debugLogger, "incrementSolutionViewCount :  ");
		} catch (AcumosServiceException e) {
			data.setErrorCode(e.getErrorCode());
			data.setResponseDetail(e.getMessage());
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred incrementSolutionViewCount :" + "solutionId",
					e);
		}
		return data;
	}

	@ApiOperation(value = "Create ratings for solution", response = MLSolution.class)
	@RequestMapping(value = { APINames.CREATE_RATING }, method = RequestMethod.POST, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<MLSolution> createSolutionRating(HttpServletRequest request,
			@RequestBody JsonRequest<MLPSolutionRating> mlpSolutionRating, HttpServletResponse response) {
		JsonResponse<MLSolution> data = new JsonResponse<>();
		try {
			catalogService.createSolutionrating(mlpSolutionRating.getBody());
			// code to create notification
			MLPNotification notification = new MLPNotification();
			String notificationMsg = null;
			MLSolution solution = catalogService.getSolution(mlpSolutionRating.getBody().getSolutionId());
			notificationMsg = "Ratings updated for " + solution.getName();
			notification.setMessage(notificationMsg);
			notification.setTitle(notificationMsg);
			notification.setMsgSeverityCode(MessageSeverityCode.ME.toString());
			notificationService.generateNotification(notification, solution.getOwnerId());
			
			data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
			data.setResponseDetail("Successfully updated solution rating");
			log.debug(EELFLoggerDelegate.debugLogger, "createSolutionRating :  ");
		} catch (AcumosServiceException e) {
			data.setErrorCode(e.getErrorCode());
			data.setResponseDetail(e.getMessage());
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred createSolutionRating :", e);
		}
		return data;
	}

	@ApiOperation(value = "Update solution ratings", response = MLSolution.class)
	@RequestMapping(value = { APINames.UPDATE_RATING }, method = RequestMethod.PUT, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<MLSolution> updateSolutionRating(HttpServletRequest request,
			@RequestBody JsonRequest<MLPSolutionRating> mlpSolutionRating, HttpServletResponse response) {
		JsonResponse<MLSolution> data = new JsonResponse<>();
		try {
			catalogService.updateSolutionRating(mlpSolutionRating.getBody());
			// code to create notification
			MLPNotification notification = new MLPNotification();
			String notificationMsg = null;
			MLSolution solution = catalogService.getSolution(mlpSolutionRating.getBody().getSolutionId());
			notificationMsg = "Ratings updated for " + solution.getName();
			notification.setMessage(notificationMsg);
			notification.setTitle(notificationMsg);
			notification.setMsgSeverityCode(MessageSeverityCode.ME.toString());
			notificationService.generateNotification(notification, solution.getOwnerId());
			
			data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
			data.setResponseDetail("Solutions fetched Successfully");
			log.debug(EELFLoggerDelegate.debugLogger, "updateSolutionRating :  ");
		} catch (AcumosServiceException e) {
			data.setErrorCode(e.getErrorCode());
			data.setResponseDetail(e.getMessage());
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred updateSolutionRating :", e);
		}
		return data;
	}

	@ApiOperation(value = "Gets models shared for the given userId.", response = MLSolution.class)
	@RequestMapping(value = {
			APINames.SHARED_MODELS_FOR_USER }, method = RequestMethod.GET, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<List<MLSolution>> getMySharedModels(HttpServletRequest request,
			@PathVariable("userId") String userId, HttpServletResponse response) {
		List<MLSolution> modelList = new ArrayList<>();
		JsonResponse<List<MLSolution>> data = new JsonResponse<>();
		try {
			if (!PortalUtils.isEmptyOrNullString(userId)) {
				RestPageRequest restPageReq = new RestPageRequest();
				modelList = catalogService.getMySharedModels(userId, restPageReq);
				if (modelList != null) {
					data.setResponseBody(modelList);
					data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
					data.setResponseDetail("Models shared with user fetched Successfully");
					log.debug(EELFLoggerDelegate.debugLogger, "getMySharedModels :  ", modelList);
				} else {
					data.setErrorCode(JSONTags.TAG_ERROR_CODE_FAILURE);
					data.setResponseDetail("No any model shared for userId : " + userId);
					log.error(EELFLoggerDelegate.errorLogger, "No any model shared for userId : " + userId);
				}
			} else {
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_EXCEPTION);
				data.setResponseDetail("userId not found");
			}

		} catch (AcumosServiceException e) {
			data.setErrorCode(e.getErrorCode());
			data.setResponseDetail(e.getMessage());
			log.error(EELFLoggerDelegate.errorLogger,
					"Exception occured while fetching models shared with userId :" + userId, e);
		}
		return data;
	}

	@ApiOperation(value = "Create favorite for solution", response = MLSolution.class)
	@RequestMapping(value = { APINames.CREATE_FAVORITE }, method = RequestMethod.POST, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<MLSolution> createSolutionFavorite(HttpServletRequest request,
			@RequestBody JsonRequest<MLPSolutionFavorite> mlpSolutionFavorite, HttpServletResponse response) {
		JsonResponse<MLSolution> data = new JsonResponse<>();
		try {
			catalogService.createSolutionFavorite(mlpSolutionFavorite.getBody());
			// code to create notification
			MLPNotification notification = new MLPNotification();
			String favorite = null;
			MLSolution solution = catalogService.getSolution(mlpSolutionFavorite.getBody().getSolutionId());
			favorite = "Favorite created for " + solution.getName();
			notification.setMessage(favorite);
			notification.setTitle(favorite);
			notification.setMsgSeverityCode(MessageSeverityCode.ME.toString());
			notificationService.generateNotification(notification, solution.getOwnerId());
			
			data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
			data.setResponseDetail("Successfully created solution favorite");
			log.debug(EELFLoggerDelegate.debugLogger, "createSolutionFavorite :  ");
		} catch (AcumosServiceException e) {
			data.setErrorCode(e.getErrorCode());
			data.setResponseDetail(e.getMessage());
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred createSolutionFavorite :", e);
		}
		return data;
	}

	@ApiOperation(value = "Delete favorite for solution", response = MLSolution.class)
	@RequestMapping(value = { APINames.DELETE_FAVORITE }, method = RequestMethod.POST, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<MLSolution> deleteSolutionFavorite(HttpServletRequest request,
			@RequestBody JsonRequest<MLPSolutionFavorite> mlpSolutionFavorite, HttpServletResponse response) {
		JsonResponse<MLSolution> data = new JsonResponse<>();
		try {
			catalogService.deleteSolutionFavorite(mlpSolutionFavorite.getBody());
			// code to create notification
			MLPNotification notification = new MLPNotification();
			String favorite = null;
			MLSolution solution = catalogService.getSolution(mlpSolutionFavorite.getBody().getSolutionId());
			favorite = "Favorite deleted for " + solution.getName();		
			notification.setMessage(favorite);
			notification.setTitle(favorite);
			notification.setMsgSeverityCode(MessageSeverityCode.ME.toString());
			notificationService.generateNotification(notification, solution.getOwnerId());
			
			data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
			data.setResponseDetail("Successfully deleted solution favorite");
			log.debug(EELFLoggerDelegate.debugLogger, "deleteSolutionFavorite :  ");
		} catch (AcumosServiceException e) {
			data.setErrorCode(e.getErrorCode());
			data.setResponseDetail(e.getMessage());
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred deleteSolutionFavorite :", e);
		}
		return data;
	}

	@ApiOperation(value = "get a list of favorite solutions for particuler userID", response = MLSolution.class)
	@RequestMapping(value = {
			APINames.USER_FAVORITE_SOLUTIONS }, method = RequestMethod.GET, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<List<MLSolution>> getFavoriteSolutions(HttpServletRequest request,
			@PathVariable("userId") String userId, HttpServletResponse response) {
		JsonResponse<List<MLSolution>> data = new JsonResponse<>();
		try {
			RestPageRequest restPageReq = new RestPageRequest();
			List<MLSolution> mlSolutionList = catalogService.getFavoriteSolutions(userId, restPageReq);
			if (mlSolutionList != null) {
				data.setResponseBody(mlSolutionList);
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
				data.setResponseDetail("Favorite solutions  fetched Successfully");
				log.debug(EELFLoggerDelegate.debugLogger, "getFavoriteSolutions: size is {} ", mlSolutionList.size());
			} else {
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_FAILURE);
				data.setResponseDetail("No favorite solutions exist for user : " + userId);
				log.debug(EELFLoggerDelegate.debugLogger, "No favorite solutions exist for user : " + userId);
			}
		} catch (AcumosServiceException e) {
			data.setErrorCode(e.getErrorCode());
			data.setResponseDetail(e.getMessage());
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred while getFavoriteSolutions", e);
		}
		return data;
	}

	@ApiOperation(value = "Get all related Solutions for the modelTypeId for Model Detail Screen.", response = MLSolution.class, responseContainer = "List")
	@RequestMapping(value = { APINames.RELATED_MY_SOLUTIONS }, method = RequestMethod.POST, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<RestPageResponseBE<MLSolution>> getRelatedMySolutions(
			@RequestBody JsonRequest<RestPageRequestBE> restPageReq) {
		// List<MLSolution> mlSolutions = null;
		RestPageResponseBE<MLSolution> mlSolutions = null;
		// JsonResponse<List<MLSolution>> data = null;
		JsonResponse<RestPageResponseBE<MLSolution>> data = new JsonResponse<>();
		try {
			mlSolutions = catalogService.getRelatedMySolutions(restPageReq);
			if (mlSolutions != null) {
				data.setResponseBody(mlSolutions);
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
				data.setResponseDetail("Solutions fetched Successfully");
				log.debug(EELFLoggerDelegate.debugLogger, "getRelatedMySolutions: size is {} ", mlSolutions.getSize());
			}
			// response.setStatus(HttpServletResponse.SC_OK);
		} catch (AcumosServiceException e) {
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred while getRelatedMySolutions", e);
			data.setErrorCode(e.getErrorCode());
			data.setResponseDetail(e.getMessage());
		}
		return data;
	}

	@ApiOperation(value = "API to read Image Artifact of the Machine Learning Solution", response = InputStream.class, responseContainer = "List", code = 200)
	@RequestMapping(value = { APINames.READ_SIGNATURE_TAB }, method = RequestMethod.GET, produces = APPLICATION_JSON)
	@ResponseBody
	public String readArtifactSolutions(@PathVariable("artifactId") String artifactId, HttpServletRequest request,
			HttpServletResponse response) {
		InputStream resource = null;
		String outputString = "";
		try {

			String artifactFileName = pushAndPullSolutionService.getFileNameByArtifactId(artifactId);
			response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
			response.setHeader("Pragma", "no-cache");
			response.setHeader("Expires", "0");
			response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
			response.setHeader("x-filename", artifactFileName);
			response.setHeader("Content-Disposition", "attachment; filename=\"" + artifactFileName + "\"");
			response.setStatus(HttpServletResponse.SC_OK);

			resource = pushAndPullSolutionService.downloadModelArtifact(artifactId);
			try {
				outputString = IOUtils.toString(resource, "UTF-8");
			} catch (IOException e) {
				throw new AcumosServiceException(AcumosServiceException.ErrorCode.IO_EXCEPTION, e.getMessage());
			}
		} catch (AcumosServiceException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			log.error(EELFLoggerDelegate.errorLogger,
					"Exception Occurred reading a artifact for a Solution in Market Place serive", e);
		}
		return outputString;
	}
	
	@ApiOperation(value = "Get ratings for a solution Id", response = MLSolution.class, responseContainer = "List")
    	@RequestMapping(value = { APINames.GET_SOLUTION_RATING }, method = RequestMethod.POST, produces = APPLICATION_JSON)
    	@ResponseBody
    	public JsonResponse<RestPageResponse<MLSolutionRating>> getSolutionRatings(@PathVariable String solutionId,
			@RequestBody JsonRequest<RestPageRequest> pageRequest) {
		RestPageResponse<MLPSolutionRating> mlpSolutionRating = null;
		List<MLSolutionRating> mlSolutionRatingList = new ArrayList<MLSolutionRating>();
		JsonResponse<RestPageResponse<MLSolutionRating>> data = new JsonResponse<>();
		try {
			RestPageRequest restpageRequest = pageRequest.getBody();
			mlpSolutionRating = catalogService.getSolutionRating(solutionId, restpageRequest);
			MLPUser mlpUser = null;
			for (MLPSolutionRating rating : mlpSolutionRating.getContent()) {
				mlpUser = userService.findUserByUserId(rating.getUserId());
				MLSolutionRating mlSolRating = PortalUtils.convertToMLSolutionRating(rating);
				mlSolRating.setUserName(mlpUser.getFirstName().concat(" ").concat(mlpUser.getLastName()));
				mlSolutionRatingList.add(mlSolRating);
			}

			RestPageResponse<MLSolutionRating> mlSolutionRating = PortalUtils.convertToMLSolutionRatingRestPageResponse(mlSolutionRatingList, mlpSolutionRating);

			if (mlSolutionRating != null) {
				data.setResponseBody(mlSolutionRating);
				data.setStatusCode(200);
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
				data.setResponseDetail("Solutions fetched Successfully");
				log.debug(EELFLoggerDelegate.debugLogger, "getSolutionRatings: size is {} ",
						mlSolutionRating.getSize());
			}
		} catch (AcumosServiceException e) {
			data.setErrorCode(JSONTags.TAG_ERROR_CODE);
			data.setResponseDetail("Exception Occurred Fetching Ratings for Solutions");
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred Fetching Ratings for Solutions", e);
		}
		return data;
	}

	@ApiOperation(value = "Create Tag", response = MLPTag.class)
	@RequestMapping(value = { APINames.CREATE_TAG }, method = RequestMethod.POST, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<MLPTag> createTag(HttpServletRequest request, @RequestBody JsonRequest<MLPTag> mlpTag,
			HttpServletResponse response) {
		JsonResponse<MLPTag> data = new JsonResponse<>();
		try {
			MLPTag tag = catalogService.createTag(mlpTag.getBody());
			data.setResponseBody(tag);
			data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
			data.setResponseDetail("Tags created Successfully");
			log.debug(EELFLoggerDelegate.debugLogger, "createTag :  ");
		} catch (AcumosServiceException e) {
			data.setErrorCode(JSONTags.TAG_ERROR_CODE);
			data.setResponseDetail("Exception occured while createTag");
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred createTag :", e);
		}
		return data;
	}

	/**
	 * @param request
	 *            HttpServletRequest
	 * @param userId
	 *            user ID
	 * @param response
	 *            HttpServletResponse
	 * @return List of Paginated ML Solutions in JSON format.
	 */
	@ApiOperation(value = "Gets solution count.", response = MLSolution.class, responseContainer = "List")
	@RequestMapping(value = { APINames.SOLUTIONS_COUNT }, method = RequestMethod.GET, produces = APPLICATION_JSON)
	@ResponseBody
	public RestPageResponseBE<MLSolution> getSolutionCount(HttpServletRequest request,
			@PathVariable("userId") String userId, HttpServletResponse response) {

		JsonResponse<RestPageResponseBE<MLSolution>> data = new JsonResponse<>();
		RestPageResponseBE<MLSolution> mlSolutions = null;
		try {
			mlSolutions = catalogService.getSolutionCount(userId);
			data.setResponseBody(mlSolutions);
			data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
			data.setResponseDetail("count fetched Successfully");
			log.debug(EELFLoggerDelegate.debugLogger, "count fetched Successfully :  ");
		} catch (Exception e) {
			data.setErrorCode(e.getLocalizedMessage());
			data.setResponseDetail(e.getMessage());
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred Fetching Solutions count", e);
		}
		return mlSolutions;
	}

	@ApiOperation(value = "Get ratings for a solution by user", response = MLSolution.class, responseContainer = "List")
	@RequestMapping(value = {
			APINames.GET_SOLUTION_RATING_USER }, method = RequestMethod.POST, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<MLPSolutionRating> getUserRatings(HttpServletRequest request,
			@PathVariable("solutionId") String solutionId, @PathVariable("userId") String userId,
			HttpServletResponse response) {
		JsonResponse<MLPSolutionRating> data = new JsonResponse<>();
		try {
			MLPSolutionRating mlSolutionRating = catalogService.getUserRatings(solutionId, userId);
			if (mlSolutionRating != null) {
				data.setResponseBody(mlSolutionRating);
				data.setStatusCode(200);
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
				data.setResponseDetail("Ratings fetched Successfully");
				log.debug(EELFLoggerDelegate.debugLogger, "getUserRatings:  {} ", mlSolutionRating);
			}
		} catch (Exception e) {
			data.setErrorCode(JSONTags.TAG_ERROR_CODE);
			data.setResponseDetail("Exception Occurred Fetching Ratings for Solutions");
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred Fetching Ratings for Solutions", e);
		}
		return data;
	}

	@ApiOperation(value = "findPortalSolutions", response = MLSolution.class, responseContainer = "List")
	@RequestMapping(value = { APINames.PORTAL_SOLUTIONS }, method = RequestMethod.POST, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<RestPageResponseBE<MLSolution>> findPortalSolutions(HttpServletRequest request,
			@RequestBody JsonRequest<RestPageRequestPortal> restPageReqPortal, HttpServletResponse response) {
		JsonResponse<RestPageResponseBE<MLSolution>> data = new JsonResponse<>();
		RestPageResponseBE<MLSolution> mlSolutions = null;
		try {
			mlSolutions = catalogService.findPortalSolutions(restPageReqPortal.getBody());
			if (mlSolutions != null) {
				data.setResponseBody(mlSolutions);
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
				data.setResponseDetail("Solutions fetched Successfully");
				log.debug(EELFLoggerDelegate.debugLogger, "findPortalSolutions: size is {} ", mlSolutions.getSize());
			}
		} catch (Exception e) {
			data.setErrorCode(JSONTags.TAG_ERROR_CODE);
			data.setResponseDetail(e.getMessage());
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred Fetching Solutions", e);
		}
		return data;
	}

	@ApiOperation(value = "Get solutions shared for userId", response = User.class)
	@RequestMapping(value = {
			APINames.USER_ACCESS_SOLUTIONS }, method = RequestMethod.POST, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<RestPageResponse<MLPSolution>> getUserAccessSolutions(@PathVariable("userId") String userId,
			@RequestBody JsonRequest<RestPageRequest> pageRequest) {
		RestPageResponse<MLPSolution> mlSolutions = null;
		JsonResponse<RestPageResponse<MLPSolution>> data = new JsonResponse<>();
		if (userId != null && pageRequest != null) {
			mlSolutions = catalogService.getUserAccessSolutions(userId, pageRequest.getBody());
		}
		if (mlSolutions != null) {
			data.setResponseBody(mlSolutions);
			data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
			data.setResponseDetail("solution for user fetched Successfully");
			log.debug(EELFLoggerDelegate.debugLogger, "getUserAccessSolutions :  ", mlSolutions);
		} else {
			data.setErrorCode(JSONTags.TAG_ERROR_CODE_FAILURE);
			data.setResponseDetail("Error occured while fetching solutions for user");
			log.error(EELFLoggerDelegate.errorLogger, "Error Occurred Fetching solutions for user :" + userId);
		}
		return data;
	}
	
	@ApiOperation(value = "Get avg ratings for a solution Id", response = MLPSolutionWeb.class, responseContainer = "List")
	@RequestMapping(value = { APINames.GET_AVG_SOLUTION_RATING }, method = RequestMethod.GET, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<MLPSolutionWeb> getAvgRatingsForSol(@PathVariable String solutionId) {
		JsonResponse<MLPSolutionWeb> data = new JsonResponse<>();
		try {
			MLPSolutionWeb  solutionStats  = catalogService.getSolutionWebMetadata(solutionId);
			if (solutionStats != null) {
				data.setResponseBody(solutionStats);
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
				data.setResponseDetail("Solution ratings fetched Successfully");
				log.debug(EELFLoggerDelegate.debugLogger, "getAvgRatingsForSol: {} ");
			}else {
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_FAILURE);
				data.setResponseDetail("No ratings found for solution :"+ solutionId);
				log.error(EELFLoggerDelegate.errorLogger, "Error Occurred Fetching ratings for model :" + solutionId);
			}
		} catch (Exception e) {
			data.setErrorCode(JSONTags.TAG_ERROR_CODE);
			data.setResponseDetail("Exception Occurred while getAvgRatingsForSol");
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred while getAvgRatingsForSol", e);
		}
		return data;
	}
	
}
