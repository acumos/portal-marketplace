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
package org.acumos.be.test.controller;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acumos.cds.domain.MLPArtifact;
import org.acumos.cds.domain.MLPSolution;
import org.acumos.cds.domain.MLPSolutionFavorite;
import org.acumos.cds.domain.MLPSolutionRating;
import org.acumos.cds.domain.MLPSolutionRevision;
import org.acumos.cds.domain.MLPSolutionWeb;
import org.acumos.cds.domain.MLPTag;
import org.acumos.cds.domain.MLPUser;
import org.acumos.cds.transport.RestPageRequest;
import org.acumos.cds.transport.RestPageResponse;
import org.acumos.portal.be.common.JsonRequest;
import org.acumos.portal.be.common.JsonResponse;
import org.acumos.portal.be.common.RestPageRequestBE;
import org.acumos.portal.be.common.RestPageResponseBE;
import org.acumos.portal.be.common.exception.AcumosServiceException;
import org.acumos.portal.be.common.exception.ProtoServiceException;
import org.acumos.portal.be.controller.MarketPlaceCatalogServiceController;
import org.acumos.portal.be.service.PushAndPullSolutionService;
import org.acumos.portal.be.service.UserService;
import org.acumos.portal.be.service.impl.MarketPlaceCatalogServiceImpl;
import org.acumos.portal.be.transport.MLSolution;
import org.acumos.portal.be.transport.MLSolutionRating;
import org.acumos.portal.be.transport.RestPageRequestPortal;
import org.acumos.portal.be.transport.User;
import org.acumos.portal.be.util.EELFLoggerDelegate;
import org.acumos.portal.be.util.PortalUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(MockitoJUnitRunner.class)
public class MarketPlaceServiceControllerTest {

	private static final EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(MarketPlaceServiceControllerTest.class);

	final HttpServletResponse response = new MockHttpServletResponse();
	final HttpServletRequest request = new MockHttpServletRequest();

	@Mock
	private UserService userService;
	
	@Mock
	private MarketPlaceCatalogServiceImpl service;
	
	@Mock
	private PushAndPullSolutionService pushAndPullSolutionService;
	
	private MockMvc mockMvc;
	
	@InjectMocks
	private MarketPlaceCatalogServiceController marketPlaceController;
	
	@Before
	public void setUp() throws Exception {
		mockMvc = standaloneSetup(marketPlaceController).build();

	}
	
	@Test
	public void getSolutionsListTest() {
		try {
			MLSolution mlsolution = getMLSolution();
			Assert.assertNotNull(mlsolution);
			JsonRequest<RestPageRequestBE> restPageReq = new JsonRequest<>();
			RestPageRequestBE body = new RestPageRequestBE();
			body.setPage(0);
			body.setSize(9);
			JsonResponse<MLSolution> solutionRes = new JsonResponse<>();
			if (body.getPage() != null && body.getSize() != null) {
				solutionRes.setResponseBody(mlsolution);
			}
			restPageReq.setBody(body);
			List<MLSolution> solutionList = new ArrayList<MLSolution>();
			solutionList.add(mlsolution);
			Assert.assertNotNull(solutionList);
			JsonResponse<RestPageResponseBE<MLSolution>> value = new JsonResponse<>();
			RestPageResponseBE<MLSolution> responseBody = new RestPageResponseBE<>(solutionList);
			value.setResponseBody(responseBody);			
			Mockito.when(service.getSearchSolution(restPageReq)).thenReturn(responseBody);
			JsonResponse<RestPageResponseBE<MLSolution>> results =  marketPlaceController.getSolutionsList(request, restPageReq, response);
			logger.info("Solution List : " + value.getResponseBody());
			Assert.assertNotNull(results);
		} catch (Exception e) {
			logger.info("Failed to execute the testcase");
		}
	}

	@Test
	public void getSolutionsDetailsTest() {
		try {

			MLSolution mlsolution = getMLSolution();
			Assert.assertNotNull(mlsolution);
			JsonResponse<MLSolution> value = new JsonResponse<>();
			value.setResponseBody(mlsolution);
			
			String solutionId = mlsolution.getSolutionId();
			Assert.assertNotNull(solutionId);
			Mockito.when(service.getSolution(solutionId)).thenReturn(mlsolution);
			value = marketPlaceController.getSolutionsDetails(request, solutionId, response);
			logger.info("Solution Details : " + value.getResponseBody());
			Assert.assertNotNull(value);
		} catch (Exception e) {
			logger.info("Failed to execute the testcase");
		}
	}

	@Test
	public void getAllMySolutionsTest() {
		try {
			MLSolution mlsolution = getMLSolution();
			Assert.assertNotNull(mlsolution);
			JsonRequest<RestPageRequestBE> restPageReq = new JsonRequest<>();
			RestPageRequestBE body = new RestPageRequestBE();
			body.setPage(0);
			body.setSize(9);
			JsonResponse<MLSolution> solutionRes = new JsonResponse<>();
			if (body.getPage() != null && body.getSize() != null) {
				solutionRes.setResponseBody(mlsolution);
			}
			restPageReq.setBody(body);
			List<MLSolution> solutionList = new ArrayList<MLSolution>();
			solutionList.add(mlsolution);
			Assert.assertNotNull(solutionList);
			String userId = mlsolution.getOwnerId();
			Assert.assertNotNull(userId);
			JsonResponse<RestPageResponseBE<MLSolution>> value = new JsonResponse<>();
			RestPageResponseBE<MLSolution> responseBody = new RestPageResponseBE<>(solutionList);
			responseBody.setContent(solutionList);
			value.setResponseBody(responseBody);
			Mockito.when(service.getAllMySolutions(userId, restPageReq))
					.thenReturn(responseBody);
			value = marketPlaceController.getAllMySolutions(request, userId, restPageReq, response);
			logger.info("Manage my solutions : " + value.getResponseBody());
			Assert.assertNotNull(value);
		} catch (Exception e) {
			logger.info("Failed to execute the testcase");
		}
	}

	@Test
	public void getSearchSolutionsTest() {
		try {
			MLSolution mlsolution = getMLSolution();
			JsonRequest<RestPageRequestBE> restPageReq = new JsonRequest<>();
			RestPageRequestBE body = new RestPageRequestBE();
			body.setPage(0);
			body.setSize(9);
			body.setSearchTerm("Solution1");
			JsonResponse<MLSolution> solutionRes = new JsonResponse<>();
			if (body.getPage() != null && body.getSize() != null && body.getSearchTerm() != null) {
				solutionRes.setResponseBody(mlsolution);
			}
			restPageReq.setBody(body);

			List<MLSolution> solutionList = new ArrayList<MLSolution>();
			solutionList.add(mlsolution);
			Assert.assertNotNull(solutionList);
			String userId = mlsolution.getOwnerId();
			JsonResponse<RestPageResponseBE<MLSolution>> value = new JsonResponse<>();
			RestPageResponseBE<MLSolution> responseBody = new RestPageResponseBE<>(solutionList);
			responseBody.setContent(solutionList);
			value.setResponseBody(responseBody);
			Mockito.when(service.getAllMySolutions(userId, restPageReq))
					.thenReturn(responseBody);
			value = marketPlaceController.getAllMySolutions(request, userId, restPageReq, response);
			logger.info("Solutions are fetched according to search term  : " + value.getResponseBody());
			Assert.assertNotNull(value);
		} catch (Exception e) {
			logger.info("Failed to execute the testcase");
		}
	}

	@Test
	public void getPaginatedListTest() {

		try {
			MLSolution mlsolution = getMLSolution();
			Assert.assertNotNull(mlsolution);
			MLPSolution solution = PortalUtils.convertToMLPSolution(mlsolution);
			Assert.assertNotNull(mlsolution);
			List<MLPSolution> solutionList = new ArrayList<MLPSolution>();
			solutionList.add(solution);
			Assert.assertNotNull(solutionList);
			JsonResponse<List<MLPSolution>> solutionres = new JsonResponse<>();
			solutionres.setResponseBody(solutionList);
			Assert.assertNotNull(solutionres);
			JsonRequest<MLSolution> mlSolutionReq = new JsonRequest<>();
			mlSolutionReq.setBody(mlsolution);
			Assert.assertNotNull(mlSolutionReq);
			RestPageResponse<MLPSolution> responseBody = new RestPageResponse<>(solutionList);
			responseBody.setSize(1);
			Assert.assertNotNull(responseBody);
			JsonResponse<RestPageResponse<MLPSolution>> value = new JsonResponse<>();
			value.setResponseBody(responseBody);
			Mockito.when(service.getAllPaginatedSolutions(0, 9, "ASC")).thenReturn(responseBody);
			value = marketPlaceController.getPaginatedList(mlSolutionReq);
			logger.info("Solutions are paginated : " + value.getResponseBody());
			Assert.assertNotNull(value);
		} catch (Exception e) {
			logger.info("Failed to execute the testcase");
		}

	}

	@Test
	public void updateSolutionDetailsTest() {
		try {
			MLSolution mlsolution = getMLSolution();
			Assert.assertNotNull(mlsolution);
			String solutionId = mlsolution.getSolutionId();
			Assert.assertNotNull(solutionId);
			JsonRequest<MLSolution> mlSolutionRes = new JsonRequest<>();
			mlSolutionRes.setBody(mlsolution);
			Assert.assertNotNull(mlSolutionRes);
			JsonResponse<MLSolution> solutionres = new JsonResponse<>();
			solutionres.setResponseBody(mlsolution);
			Assert.assertNotNull(solutionres);
			Mockito.when(service.updateSolution(mlSolutionRes.getBody(), solutionId))
					.thenReturn(mlsolution);
			solutionres = marketPlaceController.updateSolutionDetails(request, response, solutionId, mlSolutionRes);
			logger.info("Succseefully updated solution details : " + solutionres.getResponseBody());
			Assert.assertNotNull(solutionres);
		} catch (Exception e) {
			logger.info("Failed to execute the testcase");
		}
	}

	@Test
	public void getSolutionsRevisionListTest() {
		try {
			MLSolution mlsolution = getMLSolution();
			Assert.assertNotNull(mlsolution);
			MLPSolutionRevision mlpSolRev = new MLPSolutionRevision();

			mlpSolRev.setRevisionId("REV2");
			mlpSolRev.setOwnerId(mlsolution.getOwnerId());
			mlpSolRev.setVersion("v.0.0");
			mlpSolRev.setDescription("test data for revision");
			mlpSolRev.setSolutionId(mlsolution.getSolutionId());
			Assert.assertNotNull(mlpSolRev);
			String solutionId = mlpSolRev.getSolutionId();
			Assert.assertNotNull(solutionId);
			List<MLPSolutionRevision> revisionList = new ArrayList<MLPSolutionRevision>();
			revisionList.add(mlpSolRev);
			Assert.assertNotNull(revisionList);
			JsonResponse<List<MLPSolutionRevision>> revisionRes = new JsonResponse<>();
			revisionRes.setResponseBody(revisionList);
			Mockito.when(service.getSolutionRevision(solutionId)).thenReturn(revisionList);
			revisionRes = marketPlaceController.getSolutionsRevisionList(request, response, solutionId);
			logger.info("RevisionList " + revisionRes.getResponseBody());
			Assert.assertNotNull(revisionRes);
		} catch (Exception e) {
			logger.info("Failed to execute the testcase");
		}
	}

	@Test
	public void getSolutionsRevisionArtifactListTest() {
		try {
			MLSolution mlsolution = getMLSolution();
			Assert.assertNotNull(mlsolution);
			MLPSolutionRevision mlpSolRev = new MLPSolutionRevision();

			mlpSolRev.setRevisionId("REV2");
			mlpSolRev.setOwnerId(mlsolution.getOwnerId());
			mlpSolRev.setVersion("v.0.0");
			mlpSolRev.setDescription("test data for revision");
			mlpSolRev.setSolutionId(mlsolution.getSolutionId());
			Assert.assertNotNull(mlpSolRev);
			MLPArtifact mockMLPArtifact = new MLPArtifact();
			mockMLPArtifact.setArtifactId("A1");
			mockMLPArtifact.setArtifactTypeCode("MI");
			mockMLPArtifact.setDescription("Test data");
			mockMLPArtifact.setName("Test Artifact data");
			mockMLPArtifact.setOwnerId(mlsolution.getOwnerId());
			Assert.assertNotNull(mockMLPArtifact);
			String solutionId = mlpSolRev.getSolutionId();
			Assert.assertNotNull(solutionId);
			List<MLPSolutionRevision> revisionList = new ArrayList<MLPSolutionRevision>();
			revisionList.add(mlpSolRev);
			Assert.assertNotNull(revisionList);
			List<MLPArtifact> artifactList = new ArrayList<MLPArtifact>();
			artifactList.add(mockMLPArtifact);
			Assert.assertNotNull(artifactList);
			JsonResponse<List<MLPSolutionRevision>> revisionRes = new JsonResponse<>();
			revisionRes.setResponseBody(revisionList);
			Assert.assertNotNull(revisionRes);
			JsonResponse<List<MLPArtifact>> artifactRes = new JsonResponse<>();
			artifactRes.setResponseBody(artifactList);
			String revisionId = mlpSolRev.getRevisionId();
			Mockito.when(service.getSolutionArtifacts(solutionId, revisionId)).thenReturn(artifactList);
			artifactRes = marketPlaceController.getSolutionsRevisionArtifactList(request, response, solutionId, revisionId);
			logger.info("Artifact List : " + artifactRes.getResponseBody());
			Assert.assertNotNull(artifactRes);
		} catch (Exception e) {
			logger.info("Failed to execute the testcase");
		}
	}

	@Test
	public void addSolutionTagTest() {
		try {
			MLSolution mlsolution = getMLSolution();
			Assert.assertNotNull(mlsolution);
			MLPTag mlpTag = new MLPTag();
			mlpTag.setTag("Java");
			Assert.assertNotNull(mlpTag);
			List<MLPTag> tagList = new ArrayList<>();
			tagList.add(mlpTag);
			Assert.assertNotNull(tagList);
			JsonRequest<MLPTag> tagReq = new JsonRequest<>();
			tagReq.setBody(mlpTag);
			Assert.assertNotNull(tagReq);
			JsonResponse<List<MLPTag>> tagRes = new JsonResponse<>();
			tagRes.setResponseBody(tagList);
			Assert.assertNotNull(tagRes);
			JsonResponse<MLSolution> solRes = new JsonResponse<>();
			solRes.setResponseBody(mlsolution);
			Assert.assertNotNull(solRes);
			String tag = mlpTag.getTag();
			String solutionId = mlsolution.getSolutionId();
			Mockito.when(marketPlaceController.addSolutionTag(request, solutionId, tag, response)).thenReturn(solRes);
			logger.info("Successfully added tags : " + solRes.getResponseBody());
			Assert.assertNotNull(solRes);
		} catch (Exception e) {
			logger.info("Failed to execute the testcase");
		}
	}

	@Test
	public void dropSolutionTagTest() {

		try {
			MLSolution mlsolution = getMLSolution();
			Assert.assertNotNull(mlsolution);
			MLPTag mlpTag = new MLPTag();
			mlpTag.setTag("Java");
			Assert.assertNotNull(mlpTag);
			List<MLPTag> tagList = new ArrayList<>();
			tagList.add(mlpTag);
			Assert.assertNotNull(tagList);
			JsonRequest<MLPTag> tagReq = new JsonRequest<>();
			tagReq.setBody(mlpTag);
			Assert.assertNotNull(tagReq);
			JsonResponse<List<MLPTag>> tagRes = new JsonResponse<>();
			tagRes.setResponseBody(tagList);
			Assert.assertNotNull(tagRes);
			JsonResponse<MLSolution> solRes = new JsonResponse<>();
			solRes.setResponseBody(mlsolution);
			Assert.assertNotNull(solRes);
			String tag = mlpTag.getTag();
			String solutionId = mlsolution.getSolutionId();
			solRes = marketPlaceController.dropSolutionTag(request, solutionId, tag, response);
			//Mockito.when(marketPlaceController.dropSolutionTag(request, solutionId, tag, response)).thenReturn(solRes);
			logger.info("Successfully dropped  tags : " + solRes.getResponseBody());
			Assert.assertNotNull(solRes);
		} catch (Exception e) {
			logger.info("Failed to execute the testcase");
		}

	}

	@Test
	public void getTagsListTest() {
		try {
			MLSolution mlsolution = getMLSolution();
			Assert.assertNotNull(mlsolution);
			MLPTag mlpTag = new MLPTag();
			mlpTag.setTag("Java");
			Assert.assertNotNull(mlpTag);
			List<MLPTag> tagList = new ArrayList<>();
			tagList.add(mlpTag);
			Assert.assertNotNull(tagList);
			JsonRequest<MLPTag> tagReq = new JsonRequest<>();
			tagReq.setBody(mlpTag);
			Assert.assertNotNull(tagReq);
			JsonResponse<List<MLPTag>> tagRes = new JsonResponse<>();
			tagRes.setResponseBody(tagList);
			Assert.assertNotNull(tagRes);
			JsonResponse<MLSolution> solRes = new JsonResponse<>();
			solRes.setResponseBody(mlsolution);
			Assert.assertNotNull(solRes);
			JsonRequest<RestPageRequest> restPageReq = new JsonRequest<>();
			RestPageRequest body = new RestPageRequest();
			body.setSize(1);
			if (body.getSize() != null) {

				tagRes.setResponseBody(tagList);
			}

			restPageReq.setBody(body);

			JsonResponse<RestPageResponseBE> value = new JsonResponse<>();
			RestPageResponseBE responseBody = new RestPageResponseBE<>(tagList);
			responseBody.setTags(tagList);
			value.setResponseBody(responseBody);
			List<String> mlTagsList = new ArrayList<String>();			
			mlTagsList.add(mlpTag.getTag());
			Mockito.when(service.getTags(restPageReq)).thenReturn(mlTagsList);
			value =marketPlaceController.getTagsList(restPageReq);
			logger.info("Get Tag List : " + value.getResponseBody());
			Assert.assertNotNull(value);
		} catch (Exception e) {
			logger.info("Failed to execute the testcase");
		}
	}

	@Test
	public void getTagsSolutionsTest() {
		try {
			MLSolution mlsolution = getMLSolution();
			Assert.assertNotNull(mlsolution);
			List<MLSolution> solutionList = new ArrayList<>();
			solutionList.add(mlsolution);
			Assert.assertNotNull(solutionList);
			MLPTag mlpTag = new MLPTag();
			mlpTag.setTag("Java");
			Assert.assertNotNull(mlpTag);
			List<MLPTag> tagList = new ArrayList<>();
			tagList.add(mlpTag);
			Assert.assertNotNull(tagList);
			JsonRequest<MLPTag> tagReq = new JsonRequest<>();
			tagReq.setBody(mlpTag);
			Assert.assertNotNull(tagReq);
			JsonResponse<List<MLPTag>> tagRes = new JsonResponse<>();

			JsonResponse<MLSolution> solRes = new JsonResponse<>();
			solRes.setResponseBody(mlsolution);
			Assert.assertNotNull(solRes);
			String tag = mlpTag.getTag();
			String solutionId = mlsolution.getSolutionId();
			Assert.assertNotNull(solutionId);
			JsonRequest<RestPageRequestBE> restPageReq = new JsonRequest<>();
			RestPageRequestBE body = new RestPageRequestBE();
			body.setSize(1);
			if (body.getSize() != null) {
				tagRes.setResponseBody(tagList);
			}

			restPageReq.setBody(body);

			RestPageResponseBE<MLSolution> responseBody = new RestPageResponseBE<>(solutionList);
			responseBody.setContent(solutionList);

			JsonResponse<RestPageResponseBE<MLSolution>> value = new JsonResponse<>();
			value.setResponseBody(responseBody);

			Mockito.when(service.getTagBasedSolutions(tag, restPageReq)).thenReturn(responseBody);
			value = marketPlaceController.getTagsSolutions(tag, restPageReq);
			logger.info("Tag for solution  " + value.getResponseBody());
			Assert.assertNotNull(value);
		} catch (Exception e) {
			logger.info("Failed to execute the testcase");
		}
	}

	@Test
	public void dropSolutionUserAccessTest() {
		try {
			MLSolution mlsolution = getMLSolution();
			Assert.assertNotNull(mlsolution);
			String solutionId = mlsolution.getSolutionId();
			String userId = mlsolution.getOwnerId();
			Assert.assertNotNull(solutionId);
			Assert.assertNotNull(userId);
			JsonResponse<User> value = new JsonResponse<>();
			
			Mockito.when(service.getSolution(solutionId)).thenReturn(mlsolution);
			MLPUser user = getMLPUser();
			Mockito.when(userService.findUserByUserId(userId)).thenReturn(user);
//			userService.findUserByUserId(userId)
			value = marketPlaceController.dropSolutionUserAccess(request, solutionId, userId, response);
//			Mockito.when(marketPlaceController.dropSolutionUserAccess(request, solutionId, userId, response))
//					.thenReturn(value);
			Assert.assertNotNull(value);
		} catch (Exception e) {
			logger.info("Failed to execute the testcase");

		}
	}

	@Test
	public void incrementSolutionViewCountTest() {
		try {
			MLSolution mlsolution = getMLSolution();
			mlsolution.setTookitType("DS");
			Assert.assertNotNull(mlsolution);
			String solutionId = mlsolution.getSolutionId();
			Assert.assertNotNull(solutionId);
			Assert.assertEquals(solutionId, mlsolution.getSolutionId());
			JsonResponse<MLSolution> value = new JsonResponse<>();
			value.setResponseBody(mlsolution);
			Mockito.when(marketPlaceController.incrementSolutionViewCount(request, solutionId, response))
					.thenReturn(value);
			Assert.assertNotNull(value);
		} catch (Exception e) {
			logger.info("Failed to execute the testcase");

		}
	}

	@Test
	public void createRatingTest() {
		try {
			MLSolution mlsolution = getMLSolution();
			MLPSolutionRating mlpSolutionRating = new MLPSolutionRating();
			Date created = new Date();
			mlpSolutionRating.setCreated(created);
			Date modified = new Date();
			mlpSolutionRating.setModified(modified);
			mlpSolutionRating.setRating(2);
			mlpSolutionRating.setSolutionId("6e5036e0-6e20-4425-bd9d-b4ce55cfd8a4");
			mlpSolutionRating.setTextReview("ratings");
			mlpSolutionRating.setUserId("601f8aa5-5978-44e2-996e-2dbfc321ee73");
			Assert.assertNotNull(mlpSolutionRating);
			JsonRequest<MLPSolutionRating> mlpSolutionRatingREs = new JsonRequest<>();
			mlpSolutionRatingREs.setBody(mlpSolutionRating);
			Assert.assertNotNull(mlpSolutionRatingREs);
			JsonResponse<MLSolution> value = new JsonResponse<>();
			Mockito.when(service.getSolution(mlpSolutionRatingREs.getBody().getSolutionId()))
					.thenReturn(mlsolution);
			value = marketPlaceController.createSolutionRating(request, mlpSolutionRatingREs, response);
			Assert.assertNotNull(value);
		} catch (Exception e) {
			logger.info("Failed to execute the testcase");

		}

	}

	@Test
	public void updateRatingTest() {
		try {
			MLPSolutionRating mlpSolutionRating = new MLPSolutionRating();
			Date created = new Date();
			mlpSolutionRating.setCreated(created);
			Date modified = new Date();
			mlpSolutionRating.setModified(modified);
			mlpSolutionRating.setRating(2);
			mlpSolutionRating.setSolutionId("6e5036e0-6e20-4425-bd9d-b4ce55cfd8a4");
			mlpSolutionRating.setTextReview("ratings");
			mlpSolutionRating.setUserId("601f8aa5-5978-44e2-996e-2dbfc321ee73");
			Assert.assertNotNull(mlpSolutionRating);
			JsonRequest<MLPSolutionRating> mlpSolutionRatingREs = new JsonRequest<>();
			mlpSolutionRatingREs.setBody(mlpSolutionRating);
			Assert.assertNotNull(mlpSolutionRatingREs);
			JsonResponse<MLSolution> value = marketPlaceController.updateSolutionRating(request, mlpSolutionRatingREs, response);
//			Mockito.when(marketPlaceController.updateSolutionRating(request, mlpSolutionRatingREs, response))
//					.thenReturn(value);
			Assert.assertNotNull(value);
		} catch (Exception e) {
			logger.info("Failed to execute the testcase");

		}

	}

	@Test
	public void getsSolutionRatingsTest() {
		try {
			MLSolution mlsolution = getMLSolution();
			Assert.assertNotNull(mlsolution);
			String solutionId = mlsolution.getSolutionId();
			Assert.assertNotNull(solutionId);
			RestPageRequest pageRequest = new RestPageRequest();
			pageRequest.setPage(0);
			pageRequest.setSize(9);
			JsonRequest<RestPageRequest> rest = new JsonRequest<>();
			rest.setBody(pageRequest);
			RestPageResponse<MLPSolutionRating> mlSolutionRating = new RestPageResponse<MLPSolutionRating>();
			JsonResponse<RestPageResponse<MLSolutionRating>> value = new JsonResponse<>();
			
			Mockito.when(service.getSolutionRating(solutionId, pageRequest)).thenReturn(mlSolutionRating);
			value = marketPlaceController.getSolutionRatings(solutionId, rest);
			Assert.assertNotNull(value);
		} catch (Exception e) {
			logger.info("Failed to execute the testcase");

		}
	}

	@Test
	public void getMysharedModelsTest() {
		try {
			MLSolution mlsolution = getMLSolution();
			String userId = mlsolution.getOwnerId();
			JsonResponse<List<MLSolution>> value = new JsonResponse<>();
			RestPageRequest restPageReq = new RestPageRequest();
			restPageReq.setPage(0);
			restPageReq.setSize(9);
			List<MLSolution> modelList = new ArrayList<>();
			modelList.add(mlsolution);
			value.setResponseBody(modelList);
			Mockito.when(service.getMySharedModels(userId, restPageReq)).thenReturn(modelList);
			value = marketPlaceController.getMySharedModels(request, userId, response);
			Assert.assertNotNull(value);
		} catch (Exception e) {
			logger.info("Failed to execute the testcase");

		}
	}

	@Test
	public void createSolutionFavoriteTest() {
		try {
			MLSolution mlsolution = getMLSolution();
			MLPSolutionFavorite mlpSolutionFavorite = new MLPSolutionFavorite();
			mlpSolutionFavorite.setSolutionId(mlsolution.getSolutionId());
			mlpSolutionFavorite.setUserId(mlsolution.getOwnerId());
			Assert.assertNotNull(mlpSolutionFavorite);
			JsonRequest<MLPSolutionFavorite> mlpSolutionFavoriteRes = new JsonRequest<>();
			mlpSolutionFavoriteRes.setBody(mlpSolutionFavorite);
			Assert.assertNotNull(mlpSolutionFavoriteRes);
			JsonResponse<MLSolution> value = new JsonResponse<>();
			Mockito.when(service.getSolution(mlpSolutionFavoriteRes.getBody().getSolutionId())).thenReturn(mlsolution);
			value = marketPlaceController.createSolutionFavorite(request, mlpSolutionFavoriteRes, response);
			Assert.assertNotNull(value);
		} catch (Exception e) {
			logger.info("Failed to execute the testcase");

		}
	}

	@Test
	public void deleteSolutionFavoriteTest() {
		try {
			MLSolution mlsolution = getMLSolution();
			MLPSolutionFavorite mlpSolutionFavorite = new MLPSolutionFavorite();
			mlpSolutionFavorite.setSolutionId("6e5036e0-6e20-4425-bd9d-b4ce55cfd8a4");
			mlpSolutionFavorite.setUserId("601f8aa5-5978-44e2-996e-2dbfc321ee73");
			Assert.assertNotNull(mlpSolutionFavorite);
			JsonRequest<MLPSolutionFavorite> mlpSolutionFavoriteRes = new JsonRequest<>();
			mlpSolutionFavoriteRes.setBody(mlpSolutionFavorite);
			Assert.assertNotNull(mlpSolutionFavoriteRes);
			JsonResponse<MLSolution> value = new JsonResponse<>();
			Mockito.when(service.getSolution(mlpSolutionFavoriteRes.getBody().getSolutionId()))
					.thenReturn(mlsolution);
			value = marketPlaceController.deleteSolutionFavorite(request, mlpSolutionFavoriteRes, response);
			Assert.assertNotNull(value);
		} catch (Exception e) {
			logger.info("Failed to execute the testcase");

		}
	}

	@Test
	public void getFavoriteSolutionsTest() {
		try {
			MLSolution mlsolution = getMLSolution();
			MLPSolutionFavorite mlpSolutionFavorite = new MLPSolutionFavorite();
			mlpSolutionFavorite.setSolutionId(mlsolution.getSolutionId());
			mlpSolutionFavorite.setUserId(mlsolution.getOwnerId());
			List<MLSolution> mlSolutionList = new ArrayList<MLSolution>();
			mlSolutionList.add(mlsolution);
			Assert.assertNotNull(mlpSolutionFavorite);
			String userId = mlpSolutionFavorite.getUserId();
			Assert.assertNotNull(userId);
			Assert.assertEquals(userId, mlpSolutionFavorite.getUserId());
			JsonResponse<List<MLSolution>> value = new JsonResponse<>();
			RestPageRequest restPageReq = new RestPageRequest();
			Mockito.when(service.getFavoriteSolutions(userId,restPageReq)).thenReturn(mlSolutionList);
			value = marketPlaceController.getFavoriteSolutions(request, userId, response);
			Assert.assertNotNull(value);
		} catch (Exception e) {
			logger.info("Failed to execute the testcase");

		}
	}

	@Test
	public void getRelatedMySolutionsTest() {
		try {
			MLSolution mlsolution = getMLSolution();
			JsonRequest<RestPageRequestBE> restPageReqBe = new JsonRequest<>();
			List<MLSolution> mlSolutionList = new ArrayList<MLSolution>();
			mlSolutionList.add(mlsolution);
			RestPageResponseBE<MLSolution> mlSolutionsRest = new RestPageResponseBE<>(mlSolutionList);
			mlSolutionsRest.setContent(mlSolutionList);
			RestPageRequestBE body = new RestPageRequestBE();
			body.setPage(0);
			body.setSize(9);
			restPageReqBe.setBody(body);
			JsonResponse<RestPageResponseBE<MLSolution>> value = new JsonResponse<>();
			Mockito.when(service.getRelatedMySolutions(restPageReqBe)).thenReturn(mlSolutionsRest);
			value =marketPlaceController.getRelatedMySolutions(restPageReqBe);
			Assert.assertNotNull(value);
		} catch (Exception e) {
			logger.info("Failed to execute the testcase");

		}
	}

	@Test
	public void readArtifactSolutionsTest() {
		try {
			String artifactId = "4cbf491b-c687-459f-9d81-e150d1a0b972";
			String value = "Artifact read successfully";
			Mockito.when(pushAndPullSolutionService.getFileNameByArtifactId(artifactId)).thenReturn(value);
			marketPlaceController.readArtifactSolutions(artifactId, request, response);
			Assert.assertNotNull(artifactId);
		} catch (Exception e) {
			logger.info("Failed to execute the testcase");

		}
	}

	@Test
	public void getSolutionUserAccessTest() {
		try {
			
			MLSolution mlsolution = getMLSolution();			
			User user = new User();
			user.setActive("Y");
			user.setFirstName("Surya");
			user.setUserId("41058105-67f4-4461-a192-f4cb7fdafd34");
			user.setEmailId("surya@techm.com");
		
			Assert.assertNotNull(user);
			
			User user2 = new User();
			user2.setActive("Y");
			user2.setFirstName("nitin");
			user2.setUserId("1213505-67f4-4461-a192-f4cb7fdafd34");
			user2.setEmailId("nitin@techm.com");
		
			Assert.assertNotNull(user2);
			
			String solutionId = mlsolution.getSolutionId();
			Assert.assertNotNull(solutionId);
			
			List<User> userList = new ArrayList<User>();
			userList.add(user);
			userList.add(user2);
			Assert.assertNotNull(userList);	
			JsonResponse<RestPageResponseBE> data = new JsonResponse<>();
			List test = new ArrayList<>();
			RestPageResponseBE responseBody = new RestPageResponseBE<>(test);
			responseBody.setUserList(userList);
			data.setResponseBody(responseBody);
			Mockito.when(service.getSolutionUserAccess(solutionId))
					.thenReturn(userList);
			data = marketPlaceController.getSolutionUserAccess(request, solutionId, response);
			logger.info("RevisionList " + data.getResponseBody());
			Assert.assertNotNull(data);
		} catch (Exception e) {
			logger.info("Failed to execute the testcase");
		}
	}
	
	@Test
	public void addSolutionUserAccessTest() {
		try {
			
			MLSolution mlsolution = getMLSolution();
			
			User user = new User();
			user.setActive("Y");
			user.setFirstName("Surya");
			user.setUserId("41058105-67f4-4461-a192-f4cb7fdafd34");
			user.setEmailId("surya@techm.com");
		
			Assert.assertNotNull(user);
			
			User user2 = new User();
			user2.setActive("Y");
			user2.setFirstName("nitin");
			user2.setUserId("1213505-67f4-4461-a192-f4cb7fdafd34");
			user2.setEmailId("nitin@techm.com");
		
			Assert.assertNotNull(user2);
			
			String solutionId = mlsolution.getSolutionId();
			Assert.assertNotNull(solutionId);			
			List<String> userList = new ArrayList<String>();
			userList.add("1213505-67f4-4461-a192-f4cb7fdafd34");
			userList.add("41058105-67f4-4461-a192-f4cb7fdafd34");
			Assert.assertNotNull(userList);	
			List<User> userList1 = new ArrayList<>();
			userList1.add(user);
			userList1.add(user2);
			JsonRequest<List<String>> userIdList = new JsonRequest<List<String>>();
			userIdList.setBody(userList);
			JsonResponse<User> data = new JsonResponse<>();
			data.setResponseDetail("Users access for solution added Successfully");
			Mockito.when(service.getSolutionUserAccess(solutionId)).thenReturn(userList1);
			data = marketPlaceController.addSolutionUserAccess(request, solutionId,userIdList,response );
			//logger.info("RevisionList " + data.getResponseBody());
			Assert.assertNotNull(data);
		} catch (Exception e) {
			logger.info("Failed to execute the testcase");
		}
	}
	
	@Test
	public void createTagTest() {
		try {
			MLPTag mlpTag = new MLPTag();
			mlpTag.setTag("abc");
			JsonRequest<MLPTag> tags = new  JsonRequest<MLPTag>();
			tags.setBody(mlpTag);
			JsonResponse<MLPTag> data = new JsonResponse<MLPTag>();	
			data.setResponseDetail("Tags created Successfully");
			Mockito.when(service.createTag(tags.getBody()))
					.thenReturn(mlpTag);
			data = marketPlaceController.createTag(request, tags, response);
			Assert.assertNotNull(data);
		} catch (Exception e) {
			logger.info("Failed to execute the testcase");

		}
	}
	
	@Test
	public void getSolutionCountTest() {
		try {
			List test = new ArrayList<>();
			RestPageResponseBE<MLSolution> data = new RestPageResponseBE<MLSolution>(test);	
			data.setPrivateModelCount(12);
			data.setPublicModelCount(34);
			data.setCompanyModelCount(45);
			data.setDeletedModelCount(32);
			String userId = "1213505-67f4-4461-a192-f4cb7fdafd34";
			Mockito.when(service.getSolutionCount(userId)).thenReturn(data);
			data = marketPlaceController.getSolutionCount(request, userId, response);
			Assert.assertNotNull(data);
		} catch (Exception e) {
			logger.info("Failed to execute the testcase");

		}
	}
	
	@Test
	public void getUserRatingsTest() {
		try {
			JsonResponse<MLPSolutionRating> data = new JsonResponse<>();
			MLPSolutionRating mlSolutionRating = new MLPSolutionRating();
			mlSolutionRating.setRating(4);
			mlSolutionRating.setSolutionId("6e5036e0-6e20-4425-bd9d-b4ce55cfd8a4");
			mlSolutionRating.setUserId("1213505-67f4-4461-a192-f4cb7fdafd34");
			String solutionId ="6e5036e0-6e20-4425-bd9d-b4ce55cfd8a4";
			String userId = "1213505-67f4-4461-a192-f4cb7fdafd34";
			data.setResponseDetail("Ratings fetched Successfully");
			Mockito.when(service.getUserRatings(solutionId, userId))
					.thenReturn(mlSolutionRating);
			data = marketPlaceController.getUserRatings(request,solutionId, userId, response);
			Assert.assertNotNull(data);
		} catch (Exception e) {
			logger.info("Failed to execute the testcase");

		}
	}
	
	@Test
	public void findPortalSolutionsTest() {
		try {
			MLSolution mlsolution = getMLSolution();
			JsonResponse<RestPageResponseBE<MLSolution>> data = new JsonResponse<RestPageResponseBE<MLSolution>>();
			JsonRequest<RestPageRequestPortal> restPageReqPortal = new JsonRequest<RestPageRequestPortal>();
			List<MLSolution> content = new ArrayList<>();
			content.add(mlsolution);
			RestPageResponseBE<MLSolution> mlSolutionsRest = new RestPageResponseBE<>(content);
			RestPageRequestPortal restPageRequestPortal = new RestPageRequestPortal();
			restPageRequestPortal.setActive(true);
			restPageReqPortal.setBody(restPageRequestPortal);
			data.setResponseDetail("Solutions fetched Successfully");
			Mockito.when(service.findPortalSolutions(restPageReqPortal.getBody()))
					.thenReturn(mlSolutionsRest);
			Assert.assertNotNull(mlSolutionsRest);
			data = marketPlaceController.findPortalSolutions(request,restPageReqPortal, response);
			Assert.assertNotNull(data);
		} catch (Exception e) {
			logger.info("Failed to execute the testcase");

		}
	}
	
	@Test
	public void getUserAccessSolutions() {
		MLSolution mlsolution = getMLSolution();
		Assert.assertNotNull(mlsolution);
		MLPSolution solution = PortalUtils.convertToMLPSolution(mlsolution);
		Assert.assertNotNull(mlsolution);
		List<MLPSolution> solutionList = new ArrayList<MLPSolution>();
		solutionList.add(solution);
		Assert.assertNotNull(solutionList);
		RestPageResponse<MLPSolution> responseBody = new RestPageResponse<>(solutionList);
		responseBody.setSize(1);
		responseBody.setNumberOfElements(1);
		Assert.assertNotNull(responseBody);
		String userId = "1213505-67f4-4461-a192-f4cb7fdafd34";
		Mockito.when(service.getUserAccessSolutions(userId, new RestPageRequest(0, 99))).thenReturn(responseBody);
		JsonRequest<RestPageRequest> restPageReq = new JsonRequest<>();
		JsonResponse<RestPageResponse<MLPSolution>> data = marketPlaceController.getUserAccessSolutions(userId, restPageReq);
		Assert.assertNotNull(data);
	}
	

	@Test
	public void getAvgRatingsForSol() {
		MLPSolutionWeb solutionStats = new MLPSolutionWeb();
		solutionStats.setDownloadCount((long) 4);
		solutionStats.setRatingCount((long) 5);
		solutionStats.setViewCount((long) 10);
		String solutionId = "1213505-67f4-4461-a192-f4cb7fdafd34";
		Mockito.when(service.getSolutionWebMetadata(solutionId)).thenReturn(solutionStats);
		JsonResponse<MLPSolutionWeb> data = marketPlaceController.getAvgRatingsForSol(solutionId);
		Assert.assertNotNull(data);
	}
	
	private MLSolution getMLSolution(){
		MLSolution mlsolution = new MLSolution();
		mlsolution.setSolutionId("Solution1");
		mlsolution.setName("Test_Solution data");
		mlsolution.setDescription("Test data");
		mlsolution.setOwnerId("41058105-67f4-4461-a192-f4cb7fdafd34");
		mlsolution.setAccessType("PB");
		mlsolution.setActive(true);
		mlsolution.setModelType("CL");
		mlsolution.setTookitType("DS");
		return mlsolution;
	}
	private MLPUser getMLPUser(){
		MLPUser mlpUser = new MLPUser();
		mlpUser.setActive(true);
		mlpUser.setFirstName("test-first-name");			
		mlpUser.setUserId("f0ebe707-d436-40cf-9b0a-ed1ce8da1f2b");
		mlpUser.setLoginName("test-User-Name");
		return mlpUser;
	}
	
	public void fetchProtoFile() {
		try {
			String proto = new String();
			String userId = "41058105-67f4-4461-a192-f4cb7fdafd34";
			String solutionId = "1213505-67f4-4461-a192-f4cb7fdafd34";
			String version = "1";
			Mockito.when(service.getProtoUrl(userId, solutionId, version, "MI", "proto")).thenReturn(proto);
			proto = marketPlaceController.fetchProtoFile(userId, solutionId, version);
			Assert.assertNotNull(proto);
		} catch (AcumosServiceException e) {
			logger.info("Failed to execute the testcase");
		} catch (ProtoServiceException e) {
			logger.info("Failed to execute the testcase");
		}
		
	}
}
