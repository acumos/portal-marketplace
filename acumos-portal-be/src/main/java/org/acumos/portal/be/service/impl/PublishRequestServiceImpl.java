
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.acumos.cds.CodeNameType;
import org.acumos.cds.client.ICommonDataServiceRestClient;
import org.acumos.cds.domain.MLPCatalog;
import org.acumos.cds.domain.MLPCodeNamePair;
import org.acumos.cds.domain.MLPNotification;
import org.acumos.cds.domain.MLPPublishRequest;
import org.acumos.cds.domain.MLPSolution;
import org.acumos.cds.domain.MLPSolutionRevision;
import org.acumos.cds.domain.MLPUser;

import org.acumos.cds.transport.RestPageRequest;
import org.acumos.cds.transport.RestPageResponse;
import org.acumos.portal.be.common.CommonConstants;
import org.acumos.portal.be.common.PagableResponse;
import org.acumos.portal.be.common.exception.AcumosServiceException;
import org.acumos.portal.be.service.LicensingService;
import org.acumos.portal.be.service.MarketPlaceCatalogService;
import org.acumos.portal.be.service.NotificationService;
import org.acumos.portal.be.service.PublishRequestService;
import org.acumos.portal.be.service.PublishSolutionService;
import org.acumos.portal.be.service.UserService;
import org.acumos.portal.be.transport.MLPublishRequest;
import org.acumos.portal.be.transport.MLSolution;
import org.acumos.portal.be.util.PortalUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;

/**
 * Service to Support Publish Request for Solution Revision
 */
@Service
public class PublishRequestServiceImpl extends AbstractServiceImpl implements PublishRequestService {

	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Autowired
	UserService userService;

	@Autowired
	private PublishSolutionService publishSolutionService;

	@Autowired
	private MarketPlaceCatalogService catalogService;

	@Autowired
	private NotificationService notificationService;

	private static final String MSG_SEVERITY_ME = "ME";

	@Autowired
	private LicensingService licensingService;
	
	@Override
	public MLPublishRequest getPublishRequestById(Long publishRequestId) {
		log.debug("getPublishRequestById");

		ICommonDataServiceRestClient dataServiceRestClient = getClient();

		MLPPublishRequest publishRequest = dataServiceRestClient.getPublishRequest(publishRequestId);
		MLPublishRequest mlPublishRequest = getPublishRequestDetails(publishRequest);

		return mlPublishRequest;
	}

	@Override
	public MLPublishRequest searchPublishRequestByRevId(String revisionId) {
		log.debug("searchPublishRequestByRevId");

		ICommonDataServiceRestClient dataServiceRestClient = getClient();

		Map<String, Object> searchCriteria = new HashMap<String, Object>();
		searchCriteria.put("revisionId", revisionId);

		Map<String, String> queryParameters = new HashMap<>();
		queryParameters.put("created", "DESC");
		// Fetch latest publish request for the revision
		RestPageResponse<MLPPublishRequest> pbRequestResponse = dataServiceRestClient
				.searchPublishRequests(searchCriteria, false, new RestPageRequest(0, 1, queryParameters));
		List<MLPPublishRequest> publishRequestList = pbRequestResponse.getContent();
		if (publishRequestList.size() > 0)
			return getPublishRequestDetails(publishRequestList.get(0));
		else
			return null;
	}

	private MLPublishRequest getPublishRequestDetails(MLPPublishRequest publishRequest) {
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		MLPublishRequest mlPublishRequest = PortalUtils.convertToMLPublishRequest(publishRequest);
		if (!PortalUtils.isEmptyOrNullString(publishRequest.getRequestUserId())) {
			// Get Request User Details and populate in transport object
			MLPUser requestor = userService.findUserByUserId(publishRequest.getRequestUserId());
			if (requestor != null) {
				mlPublishRequest.setRequestorName(requestor.getFirstName() + " " + requestor.getLastName());
			}
		}

		// Get Requested solutionId and populate the publish request transport
		// object
		if (!PortalUtils.isEmptyOrNullString(publishRequest.getSolutionId())) {
			try {
				MLSolution solutionDetail = catalogService.getSolution(publishRequest.getSolutionId());
				mlPublishRequest.setSolutionName(solutionDetail.getName());
			} catch (AcumosServiceException e) {
				// Log the error and do nothing. Continue populating the
				// remaining fields
				log.error("Error in fetching the solution details for request " + publishRequest.getRequestId()
						+ " and Solution Id " + publishRequest.getSolutionId());
			}
		}

		if (!PortalUtils.isEmptyOrNullString(publishRequest.getRevisionId())) {
			MLPSolutionRevision revision = dataServiceRestClient.getSolutionRevision(publishRequest.getSolutionId(),
					publishRequest.getRevisionId());
			mlPublishRequest.setRevisionName(revision.getVersion());
		}

		if (!PortalUtils.isEmptyOrNullString(publishRequest.getStatusCode())) {
			List<MLPCodeNamePair> publishStatusList = dataServiceRestClient
					.getCodeNamePairs(CodeNameType.PUBLISH_REQUEST_STATUS);
			if (publishStatusList.size() > 0) {
				for (MLPCodeNamePair publishStatus : publishStatusList) {
					if (publishStatus.getCode() != null) {
						if (publishStatus.getCode().equalsIgnoreCase(publishRequest.getStatusCode())) {
							mlPublishRequest.setRequestStatusName(publishStatus.getName());
							break;
						}
					}
				}
			}
		}

		if (!PortalUtils.isEmptyOrNullString(publishRequest.getCatalogId())) {
			MLPCatalog catalog = dataServiceRestClient.getCatalog(publishRequest.getCatalogId());
			if (catalog != null) {
				mlPublishRequest.setCatalogId(catalog.getCatalogId());
				mlPublishRequest.setCatalogName(catalog.getName());
				mlPublishRequest.setAccessType(catalog.getAccessTypeCode());
			}
		}
		return mlPublishRequest;
	}

	@Override
	public PagableResponse<List<MLPublishRequest>> getAllPublishRequest(String userId,RestPageRequest requestObj) {
		log.debug("getAllPublishRequest");
		PagableResponse<List<MLPublishRequest>> response = new PagableResponse<>();
		List<MLPublishRequest> publishrequestList = new ArrayList<>();
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		Map<String, String> queryParameters = new HashMap<>();
		queryParameters.put("created", "DESC");
		RestPageResponse<MLPPublishRequest> mlpPublishRequestResponse = dataServiceRestClient
				.getPublishRequests(requestObj);
		RestPageResponse<MLPUser> mlpUserRestResponse=dataServiceRestClient.getUsers(new RestPageRequest(0,1000,queryParameters));
		int totalUserCount=(int)mlpUserRestResponse.getTotalElements();
		List<MLPUser> requestorUserList = new ArrayList<>();
		requestorUserList=mlpUserRestResponse.getContent();
		if(totalUserCount>1000) {
			int iterationCount=(totalUserCount/1000)-1;
			for(int i=0;i<=iterationCount;i++) {
				requestorUserList.addAll(dataServiceRestClient.getUsers(new RestPageRequest(i+1,1000,queryParameters)).getContent());
			}
		}
		int solutionCount=(int)dataServiceRestClient.getSolutionCount();
		List<MLPSolution> mlpSolutions= new ArrayList<>(dataServiceRestClient.getSolutions(new RestPageRequest(0, solutionCount, queryParameters)).getContent());
		//mlpSolutions=dataServiceRestClient.getSolutions(new RestPageRequest(0, solutionCount, queryParameters)).getContent();
		if(solutionCount>1000) {
			int iterationCount=(solutionCount/1000)-1;
			for(int i=0;i<=iterationCount;i++) {
				List<MLPSolution> mlpSolutionList=dataServiceRestClient.getSolutions(new RestPageRequest(i+1, solutionCount, queryParameters)).getContent();
				mlpSolutions.addAll(mlpSolutionList);
			}
		}
		List<MLPCodeNamePair> publishStatusList = dataServiceRestClient
				.getCodeNamePairs(CodeNameType.PUBLISH_REQUEST_STATUS);
		RestPageResponse<MLPCatalog> mlpCatalogRestResposne=dataServiceRestClient.getUserAccessCatalogs(userId, new RestPageRequest(0, 1000, queryParameters));
		int totalCatalogCount=mlpCatalogRestResposne.getNumberOfElements();
		List<MLPCatalog> catalogList= new ArrayList<>();
		catalogList=mlpCatalogRestResposne.getContent();
		if(totalCatalogCount>1000) {
			int iterationCount=(totalCatalogCount/1000)-1;
			for(int i=0;i<=iterationCount;i++) {
				catalogList.addAll(dataServiceRestClient.getUserAccessCatalogs(userId, new RestPageRequest(i+1, 1000, queryParameters)).getContent());
			}
		}
		if (mlpPublishRequestResponse != null) {
			List<MLPPublishRequest> mlpPublishRequestList = mlpPublishRequestResponse.getContent();
			for (MLPPublishRequest mlpPublishRequest : mlpPublishRequestList) {
				MLPublishRequest mlPublishRequest = PortalUtils.convertToMLPublishRequest(mlpPublishRequest);
				Optional<MLPUser> mlpUser=requestorUserList.stream().filter(requestUser->requestUser.getUserId().equalsIgnoreCase(mlpPublishRequest.getRequestUserId())).findFirst();
				mlPublishRequest.setRequestorName(mlpUser.get().getFirstName()+" "+mlpUser.get().getLastName());
				Optional<MLPSolution> mlpSolution=mlpSolutions.stream().filter(solution->solution.getSolutionId().equalsIgnoreCase(mlpPublishRequest.getSolutionId())).findFirst();
				mlPublishRequest.setSolutionName(mlpSolution.get().getName());
				if (!PortalUtils.isEmptyOrNullString(mlpPublishRequest.getRevisionId())) {
					MLPSolutionRevision revision = dataServiceRestClient.getSolutionRevision(mlpPublishRequest.getSolutionId(),
							mlpPublishRequest.getRevisionId());
					mlPublishRequest.setRevisionName(revision.getVersion());
				}
				Optional<MLPCodeNamePair> mlpCodeNamePair=publishStatusList.stream().filter(publishStatus->publishStatus.getCode().equalsIgnoreCase(mlpPublishRequest.getStatusCode())).findFirst();
				mlPublishRequest.setRequestStatusName(mlpCodeNamePair.get().getName());
				Optional<MLPCatalog> mlpCatalog=catalogList.stream().filter(catalog->catalog.getCatalogId().equalsIgnoreCase(mlpPublishRequest.getCatalogId())).findFirst();
				mlPublishRequest.setCatalogId(mlpCatalog.get().getCatalogId());
				mlPublishRequest.setCatalogName(mlpCatalog.get().getName());
				mlPublishRequest.setAccessType(mlpCatalog.get().getAccessTypeCode());
				publishrequestList.add(mlPublishRequest);
			}
		}
		response.setResponseBody(publishrequestList);
		response.setSize(mlpPublishRequestResponse.getSize());
		response.setTotalElements(mlpPublishRequestResponse.getTotalElements());
		response.setTotalPages(mlpPublishRequestResponse.getTotalPages());
		return response;
	}

	@Override
	public MLPublishRequest updatePublishRequest(MLPublishRequest publishRequest) throws AcumosServiceException {
		log.debug("updatePublishRequest");

		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		MLPPublishRequest oldRequest = dataServiceRestClient.getPublishRequest(publishRequest.getPublishRequestId());
		boolean isRequestApproved = false;

		oldRequest.setComment(publishRequest.getComment());
		oldRequest.setReviewUserId(publishRequest.getApproverId());
		oldRequest.setStatusCode(publishRequest.getRequestStatusCode());
		MLPPublishRequest updatedRequest = null;

		try {
			dataServiceRestClient.updatePublishRequest(oldRequest);
			// Update Request returns VOID hence again fetch the publish request
			// to check the status
			updatedRequest = dataServiceRestClient.getPublishRequest(publishRequest.getPublishRequestId());
			if (updatedRequest.getStatusCode().equalsIgnoreCase(CommonConstants.PUBLISH_REQUEST_APPROVED)) {
				isRequestApproved = true;
			}

		} catch (HttpStatusCodeException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR,
					"Error occured while updating the request");
		} catch (Exception e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR,
					"Error occured while updating the request");
		}

		MLPublishRequest updatedPublishRequest = getPublishRequestDetails(updatedRequest);
		// If request is approved then change the status of solution revision
		if (isRequestApproved) {
			dataServiceRestClient.addSolutionToCatalog(updatedRequest.getSolutionId(), updatedRequest.getCatalogId());
			licensingService.licenseAssetRegister(updatedRequest.getSolutionId(), updatedRequest.getRevisionId(), updatedRequest.getRequestUserId());
			generateNotification("Solution " + updatedPublishRequest.getSolutionName() + " Published Successfully",
					updatedPublishRequest.getRequestUserId());
		} else {
			generateNotification(
					"Publish Solution " + updatedPublishRequest.getSolutionName() + " Declined by Publisher",
					updatedPublishRequest.getRequestUserId());
		}

		return updatedPublishRequest;
	}

	private void generateNotification(String message, String userId) {

		MLPNotification notificationObj = new MLPNotification();
		notificationObj.setMsgSeverityCode(MSG_SEVERITY_ME);
		notificationObj.setMessage(message);
		notificationObj.setTitle(message);
		notificationService.generateNotification(notificationObj, userId);
	}

	@Override
	public MLPublishRequest withdrawPublishRequest(long publishRequestId, String loginUserId)
			throws AcumosServiceException {
		log.debug("withdrawPublishRequest");

		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		MLPPublishRequest oldRequest = dataServiceRestClient.getPublishRequest(publishRequestId);
		if (!oldRequest.getRequestUserId().equalsIgnoreCase(loginUserId)) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR,
					"Access declined to withdraw publish request");
		}
		oldRequest.setStatusCode(CommonConstants.PUBLISH_REQUEST_WITHDRAW);
		try {
			dataServiceRestClient.updatePublishRequest(oldRequest);
		} catch (HttpStatusCodeException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR,
					"Error occured while updating the request");
		} catch (Exception e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR,
					"Error occured while updating the request");
		}

		return getPublishRequestDetails(oldRequest);
	}

	@Override
	public MLPublishRequest searchPublishRequestByRevAndCatId(String revisionId, String catalogId) {
		log.debug("searchPublishRequestByRevAndCatId");

		ICommonDataServiceRestClient dataServiceRestClient = getClient();

		Map<String, Object> searchCriteria = new HashMap<String, Object>();
		searchCriteria.put("revisionId", revisionId);

		Map<String, String> queryParameters = new HashMap<>();
		queryParameters.put("created", "DESC");
		// Fetch publish request for the requested revision
		RestPageResponse<MLPPublishRequest> pbRequestResponse = dataServiceRestClient
				.searchPublishRequests(searchCriteria, false, new RestPageRequest(0, 10000, queryParameters));
		List<MLPPublishRequest> publishRequestList = pbRequestResponse.getContent();

		// filter the list for exact match with the requested catalogId
		List<MLPPublishRequest> filteredReqList = publishRequestList.stream()
				.filter(searchReq -> searchReq.getCatalogId().equalsIgnoreCase(catalogId)).collect(Collectors.toList());

		if (filteredReqList.size() > 0)
			return getPublishRequestDetails(filteredReqList.get(0));
		else
			return null;
	}

}
