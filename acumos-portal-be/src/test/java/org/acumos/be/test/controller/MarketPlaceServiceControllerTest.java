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
import org.acumos.cds.domain.MLPTag;
import org.acumos.cds.transport.RestPageRequest;
import org.acumos.cds.transport.RestPageResponse;
import org.acumos.portal.be.common.JsonRequest;
import org.acumos.portal.be.common.JsonResponse;
import org.acumos.portal.be.common.RestPageRequestBE;
import org.acumos.portal.be.common.RestPageResponseBE;
import org.acumos.portal.be.controller.MarketPlaceCatalogServiceController;
import org.acumos.portal.be.transport.MLSolution;
import org.acumos.portal.be.transport.User;
import org.acumos.portal.be.util.EELFLoggerDelegate;
import org.acumos.portal.be.util.PortalUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

@RunWith(MockitoJUnitRunner.class)
public class MarketPlaceServiceControllerTest {

	private static final EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(MarketPlaceServiceControllerTest.class);

	final HttpServletResponse response = new MockHttpServletResponse();
	final HttpServletRequest request = new MockHttpServletRequest();

	@Mock
	MarketPlaceCatalogServiceController marketPlaceController = new MarketPlaceCatalogServiceController();

	@Test
	public void getSolutionsListTest() {
		try {
			MLSolution mlsolution = new MLSolution();
			mlsolution.setSolutionId("Solution1");
			mlsolution.setName("Test_Solution data");
			mlsolution.setDescription("Test data");
			mlsolution.setOwnerId("41058105-67f4-4461-a192-f4cb7fdafd34");
			mlsolution.setAccessType("PB");
			mlsolution.setActive(true);
			mlsolution.setModelType("CL");
			mlsolution.setTookitType("DS");
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
			Mockito.when(marketPlaceController.getSolutionsList(request, restPageReq, response)).thenReturn(value);
			logger.info("Solution List : " + value.getResponseBody());
			Assert.assertNotNull(value);
		} catch (Exception e) {
			logger.info("Failed to execute the testcase");
		}
	}

	@Test
	public void getSolutionsDetailsTest() {
		try {

			MLSolution mlsolution = new MLSolution();
			mlsolution.setSolutionId("e475c3fe-9b6e-4427-b53b-359d54fdddd8");
			mlsolution.setName("Test_Solution data");
			mlsolution.setDescription("Test data");
			mlsolution.setOwnerId("41058105-67f4-4461-a192-f4cb7fdafd34");
			mlsolution.setAccessType("PB");
			mlsolution.setActive(true);
			mlsolution.setModelType("CL");
			mlsolution.setTookitType("DS");
			Assert.assertNotNull(mlsolution);
			JsonResponse<MLSolution> value = new JsonResponse<>();
			value.setResponseBody(mlsolution);

			String solutionId = mlsolution.getSolutionId();
			Assert.assertNotNull(solutionId);
			Mockito.when(marketPlaceController.getSolutionsDetails(request, solutionId, response)).thenReturn(value);
			logger.info("Solution Details : " + value.getResponseBody());
			Assert.assertNotNull(value);
		} catch (Exception e) {
			logger.info("Failed to execute the testcase");
		}
	}

	@Test
	public void getAllMySolutionsTest() {
		try {
			MLSolution mlsolution = new MLSolution();
			mlsolution.setSolutionId("Solution1");
			mlsolution.setName("Test_Solution data");
			mlsolution.setDescription("Test data");
			mlsolution.setOwnerId("41058105-67f4-4461-a192-f4cb7fdafd34");
			mlsolution.setAccessType("PB");
			mlsolution.setActive(true);
			mlsolution.setModelType("CL");
			mlsolution.setTookitType("DS");
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
			value.setResponseBody(responseBody);
			Mockito.when(marketPlaceController.getAllMySolutions(request, userId, restPageReq, response))
					.thenReturn(value);
			logger.info("Manage my solutions : " + value.getResponseBody());
			Assert.assertNotNull(value);
		} catch (Exception e) {
			logger.info("Failed to execute the testcase");
		}
	}

	@Test
	public void getSearchSolutionsTest() {
		try {
			MLSolution mlsolution = new MLSolution();
			mlsolution.setSolutionId("Solution1");
			mlsolution.setName("Test_Solution data");
			mlsolution.setDescription("Test data");
			mlsolution.setOwnerId("41058105-67f4-4461-a192-f4cb7fdafd34");
			mlsolution.setAccessType("PB");
			mlsolution.setActive(true);
			mlsolution.setModelType("CL");
			mlsolution.setTookitType("DS");
			Assert.assertNotNull(mlsolution);
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
			value.setResponseBody(responseBody);
			Mockito.when(marketPlaceController.getAllMySolutions(request, userId, restPageReq, response))
					.thenReturn(value);
			logger.info("Solutions are fetched according to search term  : " + value.getResponseBody());
			Assert.assertNotNull(value);
		} catch (Exception e) {
			logger.info("Failed to execute the testcase");
		}
	}

	@Test
	public void getPaginatedListTest() {

		try {
			MLSolution mlsolution = new MLSolution();
			mlsolution.setSolutionId("Solution1");
			mlsolution.setName("Test_Solution data");
			mlsolution.setDescription("Test data");
			mlsolution.setOwnerId("41058105-67f4-4461-a192-f4cb7fdafd34");
			mlsolution.setAccessType("PB");
			mlsolution.setActive(true);
			mlsolution.setModelType("CL");
			mlsolution.setTookitType("DS");
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
			Mockito.when(marketPlaceController.getPaginatedList(mlSolutionReq)).thenReturn(value);
			logger.info("Solutions are paginated : " + value.getResponseBody());
			Assert.assertNotNull(value);
		} catch (Exception e) {
			logger.info("Failed to execute the testcase");
		}

	}

	@Test
	public void updateSolutionDetailsTest() {
		try {
			MLSolution mlsolution = new MLSolution();
			mlsolution.setSolutionId("Solution1");
			mlsolution.setName("Test_Solution data");
			mlsolution.setDescription("Test data");
			mlsolution.setOwnerId("41058105-67f4-4461-a192-f4cb7fdafd34");
			mlsolution.setAccessType("PB");
			mlsolution.setActive(true);
			mlsolution.setModelType("CL");
			mlsolution.setTookitType("DS");
			Assert.assertNotNull(mlsolution);
			String solutionId = mlsolution.getSolutionId();
			Assert.assertNotNull(solutionId);
			JsonRequest<MLSolution> mlSolutionRes = new JsonRequest<>();
			mlSolutionRes.setBody(mlsolution);
			Assert.assertNotNull(mlSolutionRes);
			JsonResponse<MLSolution> solutionres = new JsonResponse<>();
			solutionres.setResponseBody(mlsolution);
			Assert.assertNotNull(solutionres);
			Mockito.when(marketPlaceController.updateSolutionDetails(request, response, solutionId, mlSolutionRes))
					.thenReturn(solutionres);
			logger.info("Succseefully updated solution details : " + solutionres.getResponseBody());
			Assert.assertNotNull(solutionres);
		} catch (Exception e) {
			logger.info("Failed to execute the testcase");
		}
	}

	@Test
	public void getSolutionsRevisionListTest() {
		try {
			MLSolution mlsolution = new MLSolution();
			mlsolution.setSolutionId("Solution1");
			mlsolution.setName("Test_Solution data");
			mlsolution.setDescription("Test data");
			mlsolution.setOwnerId("41058105-67f4-4461-a192-f4cb7fdafd34");
			mlsolution.setAccessType("PB");
			mlsolution.setActive(true);
			mlsolution.setModelType("CL");
			mlsolution.setTookitType("DS");
			Assert.assertNotNull(mlsolution);
			MLPSolutionRevision mlpSolRev = new MLPSolutionRevision();

			mlpSolRev.setRevisionId("REV2");
			mlpSolRev.setOwnerId("41058105-67f4-4461-a192-f4cb7fdafd34");
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
			Assert.assertNotNull(revisionRes);
			Mockito.when(marketPlaceController.getSolutionsRevisionList(request, response, solutionId))
					.thenReturn(revisionRes);
			logger.info("RevisionList " + revisionRes.getResponseBody());
			Assert.assertNotNull(revisionRes);
		} catch (Exception e) {
			logger.info("Failed to execute the testcase");
		}
	}

	@Test
	public void getSolutionsRevisionArtifactListTest() {
		try {
			MLSolution mlsolution = new MLSolution();
			mlsolution.setSolutionId("Solution1");
			mlsolution.setName("Test_Solution data");
			mlsolution.setDescription("Test data");
			mlsolution.setOwnerId("41058105-67f4-4461-a192-f4cb7fdafd34");
			mlsolution.setAccessType("PB");
			mlsolution.setActive(true);
			mlsolution.setModelType("CL");
			mlsolution.setTookitType("DS");
			Assert.assertNotNull(mlsolution);
			MLPSolutionRevision mlpSolRev = new MLPSolutionRevision();

			mlpSolRev.setRevisionId("REV2");
			mlpSolRev.setOwnerId("41058105-67f4-4461-a192-f4cb7fdafd34");
			mlpSolRev.setVersion("v.0.0");
			mlpSolRev.setDescription("test data for revision");
			mlpSolRev.setSolutionId(mlsolution.getSolutionId());
			Assert.assertNotNull(mlpSolRev);
			MLPArtifact mockMLPArtifact = new MLPArtifact();
			mockMLPArtifact.setArtifactId("A1");
			mockMLPArtifact.setArtifactTypeCode("MI");
			mockMLPArtifact.setDescription("Test data");
			mockMLPArtifact.setName("Test Artifact data");
			mockMLPArtifact.setOwnerId("8cbeccd0-ed84-42c3-8d9a-06d5629dc7bb");
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
			Assert.assertNotNull(artifactRes);
			String revisionId = mlpSolRev.getRevisionId();
			Mockito.when(
					marketPlaceController.getSolutionsRevisionArtifactList(request, response, solutionId, revisionId))
					.thenReturn(artifactRes);
			logger.info("Artifact List : " + artifactRes.getResponseBody());
			Assert.assertNotNull(artifactRes);
		} catch (Exception e) {
			logger.info("Failed to execute the testcase");
		}
	}

	@Test
	public void addSolutionTagTest() {
		try {
			MLSolution mlsolution = new MLSolution();
			mlsolution.setSolutionId("Solution1");
			mlsolution.setName("Test_Solution data");
			mlsolution.setDescription("Test data");
			mlsolution.setOwnerId("41058105-67f4-4461-a192-f4cb7fdafd34");
			mlsolution.setAccessType("PB");
			mlsolution.setActive(true);
			mlsolution.setModelType("CL");
			mlsolution.setTookitType("DS");
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
			MLSolution mlsolution = new MLSolution();
			mlsolution.setSolutionId("Solution1");
			mlsolution.setName("Test_Solution data");
			mlsolution.setDescription("Test data");
			mlsolution.setOwnerId("41058105-67f4-4461-a192-f4cb7fdafd34");
			mlsolution.setAccessType("PB");
			mlsolution.setActive(true);
			mlsolution.setModelType("CL");
			mlsolution.setTookitType("DS");
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
			Mockito.when(marketPlaceController.dropSolutionTag(request, solutionId, tag, response)).thenReturn(solRes);
			logger.info("Successfully dropped  tags : " + solRes.getResponseBody());
			Assert.assertNotNull(solRes);
		} catch (Exception e) {
			logger.info("Failed to execute the testcase");
		}

	}

	@Test
	public void getTagsListTest() {
		try {
			MLSolution mlsolution = new MLSolution();
			mlsolution.setSolutionId("Solution1");
			mlsolution.setName("Test_Solution data");
			mlsolution.setDescription("Test data");
			mlsolution.setOwnerId("41058105-67f4-4461-a192-f4cb7fdafd34");
			mlsolution.setAccessType("PB");
			mlsolution.setActive(true);
			mlsolution.setModelType("CL");
			mlsolution.setTookitType("DS");
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
			Mockito.when(marketPlaceController.getTagsList(restPageReq)).thenReturn(value);
			logger.info("Get Tag List : " + value.getResponseBody());
			Assert.assertNotNull(value);
		} catch (Exception e) {
			logger.info("Failed to execute the testcase");
		}
	}

	@Test
	public void getTagsSolutionsTest() {
		try {
			MLSolution mlsolution = new MLSolution();
			mlsolution.setSolutionId("Solution1");
			mlsolution.setName("Test_Solution data");
			mlsolution.setDescription("Test data");
			mlsolution.setOwnerId("41058105-67f4-4461-a192-f4cb7fdafd34");
			mlsolution.setAccessType("PB");
			mlsolution.setActive(true);
			mlsolution.setModelType("CL");
			mlsolution.setTookitType("DS");
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

			Mockito.when(marketPlaceController.getTagsSolutions(tag, restPageReq)).thenReturn(value);
			logger.info("Tag for solution  " + value.getResponseBody());
			Assert.assertNotNull(value);
		} catch (Exception e) {
			logger.info("Failed to execute the testcase");
		}
	}

	@Test
	public void dropSolutionUserAccessTest() {
		try {
			MLSolution mlsolution = new MLSolution();
			mlsolution.setSolutionId("Solution1");
			mlsolution.setName("Test_Solution data");
			mlsolution.setDescription("Test data");
			mlsolution.setOwnerId("41058105-67f4-4461-a192-f4cb7fdafd34");
			mlsolution.setAccessType("PB");
			mlsolution.setActive(true);
			mlsolution.setModelType("CL");
			mlsolution.setTookitType("DS");
			Assert.assertNotNull(mlsolution);
			String solutionId = mlsolution.getSolutionId();
			String userId = mlsolution.getOwnerId();
			Assert.assertNotNull(solutionId);
			Assert.assertNotNull(userId);
			JsonResponse<User> value = new JsonResponse<>();
			Mockito.when(marketPlaceController.dropSolutionUserAccess(request, solutionId, userId, response))
					.thenReturn(value);
			Assert.assertNotNull(value);
		} catch (Exception e) {
			logger.info("Failed to execute the testcase");

		}
	}

	@Test
	public void incrementSolutionViewCountTest() {
		try {
			MLSolution mlsolution = new MLSolution();
			mlsolution.setSolutionId("Solution1");
			mlsolution.setName("Test_Solution data");
			mlsolution.setDescription("Test data");
			mlsolution.setOwnerId("41058105-67f4-4461-a192-f4cb7fdafd34");
			mlsolution.setAccessType("PB");
			mlsolution.setActive(true);
			mlsolution.setModelType("CL");
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
			Mockito.when(marketPlaceController.createSolutionRating(request, mlpSolutionRatingREs, response))
					.thenReturn(value);
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
			JsonResponse<MLSolution> value = new JsonResponse<>();
			Mockito.when(marketPlaceController.updateSolutionRating(request, mlpSolutionRatingREs, response))
					.thenReturn(value);
			Assert.assertNotNull(value);
		} catch (Exception e) {
			logger.info("Failed to execute the testcase");

		}

	}

	@Test
	public void getsSolutionRatingsTest() {
		try {
			MLSolution mlsolution = new MLSolution();
			mlsolution.setSolutionId("6e5036e0-6e20-4425-bd9d-b4ce55cfd8a4");
			mlsolution.setOwnerId("601f8aa5-5978-44e2-996e-2dbfc321ee73");
			Assert.assertNotNull(mlsolution);
			String solutionId = mlsolution.getSolutionId();
			Assert.assertNotNull(solutionId);
			RestPageRequest pageRequest = new RestPageRequest();
			pageRequest.setPage(0);
			pageRequest.setSize(9);
			JsonRequest<RestPageRequest> rest = new JsonRequest<>();
			rest.setBody(pageRequest);

			JsonResponse<RestPageResponse<MLPSolutionRating>> value = new JsonResponse<>();
			
			Mockito.when(marketPlaceController.getSolutionRatings(solutionId, rest)).thenReturn(value);
			Assert.assertNotNull(value);
		} catch (Exception e) {
			logger.info("Failed to execute the testcase");

		}
	}

	@Test
	public void getMysharedModelsTest() {
		try {
			String userId = "1810f833-8698-4233-add4-091e34b8703c";
			JsonResponse<List<MLSolution>> value = new JsonResponse<>();
			Mockito.when(marketPlaceController.getMySharedModels(request, userId, response)).thenReturn(value);
		} catch (Exception e) {
			logger.info("Failed to execute the testcase");

		}
	}

	@Test
	public void createSolutionFavoriteTest() {
		try {
			MLPSolutionFavorite mlpSolutionFavorite = new MLPSolutionFavorite();
			mlpSolutionFavorite.setSolutionId("6e5036e0-6e20-4425-bd9d-b4ce55cfd8a4");
			mlpSolutionFavorite.setUserId("601f8aa5-5978-44e2-996e-2dbfc321ee73");
			Assert.assertNotNull(mlpSolutionFavorite);
			JsonRequest<MLPSolutionFavorite> mlpSolutionFavoriteRes = new JsonRequest<>();
			mlpSolutionFavoriteRes.setBody(mlpSolutionFavorite);
			Assert.assertNotNull(mlpSolutionFavoriteRes);
			JsonResponse<MLSolution> value = new JsonResponse<>();
			Mockito.when(marketPlaceController.createSolutionFavorite(request, mlpSolutionFavoriteRes, response))
					.thenReturn(value);
			Assert.assertNotNull(value);
		} catch (Exception e) {
			logger.info("Failed to execute the testcase");

		}
	}

	@Test
	public void deleteSolutionFavoriteTest() {
		try {
			MLPSolutionFavorite mlpSolutionFavorite = new MLPSolutionFavorite();
			mlpSolutionFavorite.setSolutionId("6e5036e0-6e20-4425-bd9d-b4ce55cfd8a4");
			mlpSolutionFavorite.setUserId("601f8aa5-5978-44e2-996e-2dbfc321ee73");
			Assert.assertNotNull(mlpSolutionFavorite);
			JsonRequest<MLPSolutionFavorite> mlpSolutionFavoriteRes = new JsonRequest<>();
			mlpSolutionFavoriteRes.setBody(mlpSolutionFavorite);
			Assert.assertNotNull(mlpSolutionFavoriteRes);
			JsonResponse<MLSolution> value = new JsonResponse<>();
			Mockito.when(marketPlaceController.deleteSolutionFavorite(request, mlpSolutionFavoriteRes, response))
					.thenReturn(value);
			Assert.assertNotNull(value);
		} catch (Exception e) {
			logger.info("Failed to execute the testcase");

		}
	}

	@Test
	public void getFavoriteSolutionsTest() {
		try {
			MLPSolutionFavorite mlpSolutionFavorite = new MLPSolutionFavorite();
			mlpSolutionFavorite.setSolutionId("6e5036e0-6e20-4425-bd9d-b4ce55cfd8a4");
			mlpSolutionFavorite.setUserId("601f8aa5-5978-44e2-996e-2dbfc321ee73");
			Assert.assertNotNull(mlpSolutionFavorite);
			String userId = mlpSolutionFavorite.getUserId();
			Assert.assertNotNull(userId);
			Assert.assertEquals(userId, mlpSolutionFavorite.getUserId());
			JsonResponse<List<MLSolution>> value = new JsonResponse<>();
			Mockito.when(marketPlaceController.getFavoriteSolutions(request, userId, response)).thenReturn(value);
			Assert.assertNotNull(value);
		} catch (Exception e) {
			logger.info("Failed to execute the testcase");

		}
	}

	@Test
	public void getRelatedMySolutionsTest() {
		try {
			JsonRequest<RestPageRequestBE> restPageReqBe = new JsonRequest<>();
			RestPageRequestBE body = new RestPageRequestBE();
			body.setPage(0);
			body.setSize(9);
			restPageReqBe.setBody(body);
			JsonResponse<RestPageResponseBE<MLSolution>> value = new JsonResponse<>();
			Mockito.when(marketPlaceController.getRelatedMySolutions(restPageReqBe)).thenReturn(value);
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
			Mockito.when(marketPlaceController.readArtifactSolutions(artifactId, request, response)).thenReturn(value);
			Assert.assertNotNull(artifactId);
		} catch (Exception e) {
			logger.info("Failed to execute the testcase");

		}
	}

	@Test
	public void createTagTest() {
		try {
			MLPTag mlpTag = new MLPTag();
			mlpTag.setTag("JAVA");
			Assert.assertNotNull(mlpTag);
			JsonRequest<MLPTag> mlpTagRes = new JsonRequest<>();
			mlpTagRes.setBody(mlpTag);
			Assert.assertNotNull(mlpTagRes);
			JsonResponse<MLPTag> value = new JsonResponse<>();
			value.setResponseBody(mlpTag);
			Mockito.when(marketPlaceController.createTag(request, mlpTagRes, response)).thenReturn(value);
			Assert.assertNotNull(value);
		} catch (Exception e) {
			logger.info("Failed to execute the testcase");

		}
	}
}
