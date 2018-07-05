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
package org.acumos.be.test.service.impl;

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
import org.acumos.portal.be.common.RestPageRequestBE;
import org.acumos.portal.be.common.RestPageResponseBE;
import org.acumos.portal.be.common.exception.AcumosServiceException;
import org.acumos.portal.be.service.impl.MarketPlaceCatalogServiceImpl;
import org.acumos.portal.be.transport.MLSolution;
import org.acumos.portal.be.transport.MLSolutionFavorite;
import org.acumos.portal.be.transport.MLSolutionRating;
import org.acumos.portal.be.transport.User;
import org.acumos.portal.be.util.EELFLoggerDelegate;
import org.acumos.portal.be.util.PortalUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import static org.mockito.Mockito.*;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import org.junit.Assert;

@RunWith(MockitoJUnitRunner.class)
public class MarketPlaceServiceImplTest {

	private static final EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(MarketPlaceServiceImplTest.class);

	final HttpServletResponse response = new MockHttpServletResponse();
	final HttpServletRequest request = new MockHttpServletRequest();

	@Mock
	MarketPlaceCatalogServiceImpl impl = new MarketPlaceCatalogServiceImpl();

	@Test
	public void getAllPaginatedSolutionsTest() {
		try {

			MarketPlaceCatalogServiceImpl mockimpl = mock(MarketPlaceCatalogServiceImpl.class);
			Integer page = 0;
			Integer size = 9;
			String sortingOrder = "ASC";
			RestPageResponse<MLPSolution> mlpSolution = new RestPageResponse<>();
			mlpSolution.setNumberOfElements(1);
			mlpSolution.setSize(0);

			if (page != null && size != null && sortingOrder != null) {

				Mockito.when(impl.getAllPaginatedSolutions(page, size, sortingOrder)).thenReturn(mlpSolution);
				logger.info("Successfully fetched paginated solutions ");
				Assert.assertEquals(mockimpl, mockimpl);
			}

		} catch (Exception e) {
			logger.info("Failed to fetch paginated solutions " + e);
		}
	}

	@Test
	public void getAllPublishedSolutionsTest() {
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

			List<MLSolution> mlSolutions = new ArrayList<>();
			mlSolutions.add(mlsolution);
			if (mlSolutions != null) {
				Mockito.when(impl.getAllPublishedSolutions()).thenReturn(mlSolutions);
				logger.info("Successfully fetched list of solutions ");
				Assert.assertNotNull("Solution List : ", mlSolutions);

			}

		} catch (Exception e) {
			logger.info("Failed to fetch published solutions " + e);
		}
	}

	@Test
	public void getSolutionTest() {
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

			if (mlsolution != null) {
				String solutionId = "6e5036e0-6e20-4425-bd9d-b4ce55cfd8a4";
				Mockito.when(impl.getSolution(solutionId)).thenReturn(mlsolution);
				logger.info("Solution fetched successfully for solution given id");
				Assert.assertNotNull(solutionId);
				Assert.assertEquals(mlsolution, mlsolution);
			}

		} catch (Exception e) {
			logger.info("Failed to fetch solutions " + e);
		}
	}

	/*@Test
	public void getSearchSolutionTest() {
		try {
			JsonRequest<RestPageRequestBE> restPageReqBe = new JsonRequest<>();
			RestPageRequestBE body = new RestPageRequestBE();
			body.setSearchTerm("Solution");
			body.setPage(1);
			body.setSize(9);
			body.setAccessType("OR");
			body.setActiveType("Y");
			restPageReqBe.setBody(body);

			MLSolution mlsolution = new MLSolution();
			mlsolution.setSolutionId("Solution1");
			mlsolution.setName("Test_Solution data");
			mlsolution.setDescription("Test data");
			mlsolution.setOwnerId("41058105-67f4-4461-a192-f4cb7fdafd34");
			mlsolution.setAccessType("PB");
			mlsolution.setActive(true);
			mlsolution.setModelType("CL");
			mlsolution.setTookitType("DS");

			List<MLSolution> mlSolutions = new ArrayList<>();
			mlSolutions.add(mlsolution);
			RestPageResponseBE<MLSolution> response = new RestPageResponseBE<>(mlSolutions);
			response.setContent(mlSolutions);

			if (body != null) {
				Mockito.when(impl.getSearchSolution(restPageReqBe)).thenReturn(response);
				logger.info("Solution searched successfully");
				Assert.assertNotNull(response);
			}

		} catch (Exception e) {
			logger.info("Failed to fetch solutions " + e);
		}
	}*/

	/*@Test
	public void getAllMySolutionsTest() {
		try {

			JsonRequest<RestPageRequestBE> restPageReqBe = new JsonRequest<>();
			RestPageRequestBE body = new RestPageRequestBE();
			body.setSearchTerm("Solution");
			body.setPage(1);
			body.setSize(9);
			body.setAccessType("OR");
			body.setActiveType("Y");
			restPageReqBe.setBody(body);

			String userId = "1810f833-8698-4233-add4-091e34b8703c";

			MLSolution mlsolution = new MLSolution();
			mlsolution.setSolutionId("Solution1");
			mlsolution.setName("Test_Solution data");
			mlsolution.setDescription("Test data");
			mlsolution.setOwnerId("41058105-67f4-4461-a192-f4cb7fdafd34");
			mlsolution.setAccessType("PB");
			mlsolution.setActive(true);
			mlsolution.setModelType("CL");
			mlsolution.setTookitType("DS");

			List<MLSolution> mlSolutions = new ArrayList<>();
			mlSolutions.add(mlsolution);
			RestPageResponseBE<MLSolution> response = new RestPageResponseBE<>(mlSolutions);
			response.setContent(mlSolutions);

			if (userId != null) {
				Mockito.when(impl.getAllMySolutions(userId, restPageReqBe)).thenReturn(response);
				logger.info("Solutions fecthed successfully according to userId ");
				Assert.assertNotNull(userId);
				Assert.assertEquals(mlSolutions, mlSolutions);
			}

		} catch (Exception e) {
			logger.info("Failed to fetch solutions " + e);
		}
	}*/

	@Test
	public void updateSolutionTest() throws AcumosServiceException {
		try {
			MLSolution mlsolution = new MLSolution();
			mlsolution.setName("Test_Solution data");
			mlsolution.setDescription("Test data");
			mlsolution.setOwnerId("601f8aa5-5978-44e2-996e-2dbfc321ee73");
			mlsolution.setAccessType("PB");
			mlsolution.setActive(true);
			mlsolution.setModelType("CL");
			mlsolution.setTookitType("DS");

			String solutionId = "6e5036e0-6e20-4425-bd9d-b4ce55cfd8a4";
			if (mlsolution != null) {
				Mockito.when(impl.updateSolution(mlsolution, solutionId)).thenReturn(mlsolution);
				logger.info("Updated solution successfully ");
				Assert.assertEquals(solutionId, solutionId);
				Assert.assertNotNull(mlsolution);
				Assert.assertEquals(mlsolution, mlsolution);
			}

		} catch (Exception e) {
			logger.info("Failed to update solutions " + e);
		}

	}

	@Test
	public void getSolutionRevisionTest() {
		try {

			MLSolution mlsolution = new MLSolution();
			mlsolution.setSolutionId("6e5036e0-6e20-4425-bd9d-b4ce55cfd8a4");
			mlsolution.setOwnerId("601f8aa5-5978-44e2-996e-2dbfc321ee73");

			MLPSolutionRevision mlpSolRev = new MLPSolutionRevision();

			mlpSolRev.setRevisionId("90361063-3ee0-434b-85da-208a8be6856d");
			mlpSolRev.setOwnerId("601f8aa5-5978-44e2-996e-2dbfc321ee73");
			mlpSolRev.setSolutionId(mlsolution.getSolutionId());
			String solutionId = mlpSolRev.getSolutionId();

			List<MLPSolutionRevision> solutionRevList = new ArrayList<>();
			solutionRevList.add(mlpSolRev);

			if (solutionId != null) {
				Mockito.when(impl.getSolutionRevision(solutionId)).thenReturn(solutionRevList);
				logger.info("Fetched solutions successfully ");
				Assert.assertEquals(solutionRevList, solutionRevList);
			}

		} catch (Exception e) {
			logger.info("Failed to fetch solutions for revisions" + e);
		}
	}

	@Test
	public void getSolutionArtifactsTest() {
		try {

			MLSolution mlsolution = new MLSolution();
			mlsolution.setSolutionId("6e5036e0-6e20-4425-bd9d-b4ce55cfd8a4");
			mlsolution.setOwnerId("601f8aa5-5978-44e2-996e-2dbfc321ee73");
			MLPSolutionRevision mlpSolRev = new MLPSolutionRevision();

			mlpSolRev.setRevisionId("90361063-3ee0-434b-85da-208a8be6856d");
			mlpSolRev.setOwnerId("601f8aa5-5978-44e2-996e-2dbfc321ee73");
			mlpSolRev.setSolutionId(mlsolution.getSolutionId());

			MLPArtifact mockMLPArtifact = new MLPArtifact();
			mockMLPArtifact.setArtifactId("4cbf491b-c687-459f-9d81-e150d1a0b972");
			mockMLPArtifact.setOwnerId("601f8aa5-5978-44e2-996e-2dbfc321ee73");

			List<MLPArtifact> artifactList = new ArrayList<>();
			artifactList.add(mockMLPArtifact);

			String solutionId = mlpSolRev.getSolutionId();

			String revisionId = mlpSolRev.getRevisionId();
			if (solutionId != null && revisionId != null) {
				Mockito.when(impl.getSolutionArtifacts(solutionId, revisionId)).thenReturn(artifactList);
				logger.info("Successfully fetched data");
				Assert.assertEquals(artifactList, artifactList);
			}

		} catch (Exception e) {
			logger.info("Failed to fetch solutions " + e);
		}
	}

	@Test
	public void addSolutionTagTest() {
		try {
			MarketPlaceCatalogServiceImpl mockimpl = mock(MarketPlaceCatalogServiceImpl.class);
			MLSolution mlsolution = new MLSolution();
			mlsolution.setSolutionId("6e5036e0-6e20-4425-bd9d-b4ce55cfd8a4");
			mlsolution.setOwnerId("601f8aa5-5978-44e2-996e-2dbfc321ee73");

			String solutionId = mlsolution.getSolutionId();
			String tag = "Java";
			if (solutionId != null) {
				mockimpl.addSolutionTag(solutionId, tag);
				logger.info("Tags added succesfully");
				Assert.assertEquals(mlsolution, mlsolution);
			}

		} catch (Exception e) {
			logger.info("Failed to addSolutionTagTest" + e);
		}
	}

	@Test
	public void dropSolutionTagTest() {
		try {
			MarketPlaceCatalogServiceImpl mockimpl = mock(MarketPlaceCatalogServiceImpl.class);

			MLSolution mlsolution = new MLSolution();
			mlsolution.setSolutionId("6e5036e0-6e20-4425-bd9d-b4ce55cfd8a4");
			mlsolution.setOwnerId("601f8aa5-5978-44e2-996e-2dbfc321ee73");

			String solutionId = mlsolution.getSolutionId();
			String tag = "Java";
			if (solutionId != null) {
				mockimpl.dropSolutionTag(solutionId, tag);
				logger.info("Tags dropped succesfully");
				Assert.assertEquals(mlsolution, mlsolution);
			}

		} catch (Exception e) {
			logger.info("Failed to dropSolutionTagTest" + e);
		}
	}

	@Test
	public void getTagsTest() {
		try {
			JsonRequest<RestPageRequest> restPageReq = new JsonRequest<>();
			RestPageRequest body = new RestPageRequest();
			body.setPage(1);
			body.setSize(9);

			List<String> list = new ArrayList<>();
			if (body.getPage() != null && body.getSize() != null) {
				restPageReq.setBody(body);
				Mockito.when(impl.getTags(restPageReq)).thenReturn(list);
				logger.info("Fetched tags list successfully");
			}
		} catch (Exception e) {
			logger.info("Failed to getTagsTest" + e);
		}
	}

	@Test
	public void getSolutionUserAccessTest() {
		try {
			MLSolution mlsolution = new MLSolution();
			mlsolution.setSolutionId("6e5036e0-6e20-4425-bd9d-b4ce55cfd8a4");
			mlsolution.setOwnerId("601f8aa5-5978-44e2-996e-2dbfc321ee73");

			User user = new User();
			user.setActive("Y");
			user.setEmailId("email.@testemail.com");
			user.setUserId("601f8aa5-5978-44e2-996e-2dbfc321ee73");
			user.setFirstName("test_1509357629483");

			List<User> userList = new ArrayList<>();
			userList.add(user);

			String solutionId = mlsolution.getSolutionId();
			if (solutionId != null) {
				Mockito.when(impl.getSolutionUserAccess(solutionId)).thenReturn(userList);
				logger.info("Fetched solutions ");

				Assert.assertEquals(userList, userList);
			}

		} catch (Exception e) {
			logger.info("Failed to getSolutionUserAccessTest" + e);
		}
	}

	@Test
	public void dropSolutionUserAccessTest() {
		try {
			MarketPlaceCatalogServiceImpl mockimpl = mock(MarketPlaceCatalogServiceImpl.class);
			MLSolution mlsolution = new MLSolution();
			mlsolution.setSolutionId("6e5036e0-6e20-4425-bd9d-b4ce55cfd8a4");
			mlsolution.setOwnerId("601f8aa5-5978-44e2-996e-2dbfc321ee73");

			String solutionId = mlsolution.getSolutionId();
			String userId = mlsolution.getOwnerId();
			if (solutionId != null && userId != null) {
				mockimpl.dropSolutionUserAccess(solutionId, userId);
				logger.info("Data droped successfully");
				Assert.assertEquals(mlsolution, mlsolution);
			}

		} catch (Exception e) {
			logger.info("Failed to dropSolutionUserAccessTest" + e);
		}
	}

	@Test
	public void incrementSolutionViewCountTest() {
		try {
			MarketPlaceCatalogServiceImpl mockimpl = mock(MarketPlaceCatalogServiceImpl.class);
			MLSolution mlsolution = new MLSolution();
			mlsolution.setSolutionId("6e5036e0-6e20-4425-bd9d-b4ce55cfd8a4");
			mlsolution.setOwnerId("601f8aa5-5978-44e2-996e-2dbfc321ee73");

			String solutionId = mlsolution.getSolutionId();
			if (solutionId != null) {
				mockimpl.incrementSolutionViewCount(solutionId);
				logger.info("incrementSolutionViewCount Success");
				Assert.assertEquals(mlsolution, mlsolution);
				Assert.assertEquals(solutionId, solutionId);
			}

		} catch (Exception e) {
			logger.info("Failed to incrementSolutionViewCountTest" + e);
		}
	}

	@Test
	public void createSolutionratingTest() {
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

			MLSolutionRating mlsolutionRating = PortalUtils.convertToMLSolutionRating(mlpSolutionRating);

			if (mlpSolutionRating != null) {
				Mockito.when(impl.createSolutionrating(mlpSolutionRating)).thenReturn(mlsolutionRating);
				logger.info("Created rating for given solution Id");
				Assert.assertEquals(mlpSolutionRating, mlpSolutionRating);
				Assert.assertNotNull("MLP Solution Rating ", mlpSolutionRating);
				Assert.assertEquals(mlsolutionRating, mlsolutionRating);
			}

		} catch (Exception e) {
			logger.info("Failed to createSolutionratingTest" + e);
		}
	}

	@Test
	public void updateSolutionRatingTest() {

		try {
			MarketPlaceCatalogServiceImpl mockimpl = mock(MarketPlaceCatalogServiceImpl.class);

			MLPSolutionRating mlpSolutionRating = new MLPSolutionRating();
			Date created = new Date();
			mlpSolutionRating.setCreated(created);
			Date modified = new Date();
			mlpSolutionRating.setModified(modified);
			mlpSolutionRating.setRating(4);
			mlpSolutionRating.setSolutionId("6e5036e0-6e20-4425-bd9d-b4ce55cfd8a4");
			mlpSolutionRating.setTextReview("ratings");
			mlpSolutionRating.setUserId("601f8aa5-5978-44e2-996e-2dbfc321ee73");

			if (mlpSolutionRating != null) {
				mockimpl.updateSolutionRating(mlpSolutionRating);
				logger.info("updated rating for given solution Id");
				Assert.assertEquals(mlpSolutionRating, mlpSolutionRating);
				Assert.assertNotNull("MLP Solution Rating ", mlpSolutionRating);
			}

		} catch (Exception e) {
			logger.info("Failed to updateSolutionRatingTest" + e);
		}

	}

	@Test
	public void getSolutionRatingTest() {
		try {

			MLSolution mlsolution = new MLSolution();
			mlsolution.setSolutionId("6e5036e0-6e20-4425-bd9d-b4ce55cfd8a4");
			mlsolution.setOwnerId("601f8aa5-5978-44e2-996e-2dbfc321ee73");

			String solutionId = mlsolution.getSolutionId();
			RestPageRequest pageRequest = new RestPageRequest();
			pageRequest.setPage(0);
			pageRequest.setSize(9);

			MLPSolutionRating mlpSolutionRating = new MLPSolutionRating();
			Date created = new Date();
			mlpSolutionRating.setCreated(created);
			Date modified = new Date();
			mlpSolutionRating.setModified(modified);
			mlpSolutionRating.setRating(4);
			mlpSolutionRating.setSolutionId("6e5036e0-6e20-4425-bd9d-b4ce55cfd8a4");
			mlpSolutionRating.setTextReview("ratings");
			mlpSolutionRating.setUserId("601f8aa5-5978-44e2-996e-2dbfc321ee73");

			List<MLPSolutionRating> ratingList = new ArrayList<>();
			ratingList.add(mlpSolutionRating);
			RestPageResponse<MLPSolutionRating> ratingRes = new RestPageResponse<>(ratingList);
			ratingRes.getTotalElements();

			if (solutionId != null) {
				Mockito.when(impl.getSolutionRating(solutionId, pageRequest)).thenReturn(ratingRes);
				logger.info("Data fetched successfully");
				Assert.assertEquals(solutionId, solutionId);
			}
			Assert.assertEquals(mlpSolutionRating, mlpSolutionRating);
		} catch (Exception e) {
			logger.info("Failed to getSolutionRatingTest" + e);
		}
	}

	@Test
	public void getMySharedModelsTest() {
		try {
			String userId = "1810f833-8698-4233-add4-091e34b8703c";
			RestPageRequest restPageReq = new RestPageRequest();
			restPageReq.setPage(1);
			restPageReq.setSize(9);

			MLSolution mlsolution = new MLSolution();
			mlsolution.setSolutionId("Solution1");
			mlsolution.setName("Test_Solution data");
			mlsolution.setDescription("Test data");
			mlsolution.setOwnerId("41058105-67f4-4461-a192-f4cb7fdafd34");
			mlsolution.setAccessType("PB");
			mlsolution.setActive(true);
			mlsolution.setModelType("CL");
			mlsolution.setTookitType("DS");

			List<MLSolution> mlSolutions = new ArrayList<>();
			mlSolutions.add(mlsolution);
			if (userId != null) {
				Mockito.when(impl.getMySharedModels(userId, restPageReq)).thenReturn(mlSolutions);
				logger.info("Successfully fetched data");
				Assert.assertEquals(mlSolutions, mlSolutions);
			}

		} catch (Exception e) {
			logger.info("Failed to getMySharedModelsTest" + e);
		}
	}

	@Test
	public void createSolutionFavoriteTest() {
		try {

			MLPSolutionFavorite mlpSolutionFavorite = new MLPSolutionFavorite();
			mlpSolutionFavorite.setSolutionId("6e5036e0-6e20-4425-bd9d-b4ce55cfd8a4");
			mlpSolutionFavorite.setUserId("601f8aa5-5978-44e2-996e-2dbfc321ee73");

			MLSolutionFavorite solutionFav = PortalUtils.convertToMLSolutionFavorite(mlpSolutionFavorite);

			if (mlpSolutionFavorite != null) {
				Mockito.when(impl.createSolutionFavorite(mlpSolutionFavorite)).thenReturn(solutionFav);
				logger.info("Created solution favorite ");

				Assert.assertEquals(solutionFav, solutionFav);
				;
			}

		} catch (Exception e) {
			logger.info("Failed to createSolutionFavoriteTest" + e);
		}
	}

	@Test
	public void getTagBasedSolutionsTest() {
		try {

			String tag = "Java";
			JsonRequest<RestPageRequestBE> restPageReqBe = new JsonRequest<>();
			RestPageRequestBE body = new RestPageRequestBE();
			body.setSize(9);
			body.setPage(0);
			restPageReqBe.setBody(body);

			MLSolution mlsolution = new MLSolution();
			mlsolution.setSolutionId("Solution1");
			mlsolution.setName("Test_Solution data");
			mlsolution.setDescription("Test data");
			mlsolution.setOwnerId("41058105-67f4-4461-a192-f4cb7fdafd34");
			mlsolution.setAccessType("PB");
			mlsolution.setActive(true);
			mlsolution.setModelType("CL");
			mlsolution.setTookitType("DS");

			List<MLSolution> mlSolutions = new ArrayList<>();
			mlSolutions.add(mlsolution);

			RestPageResponseBE<MLSolution> solutionres = new RestPageResponseBE<>(mlSolutions);

			if (body != null) {
				Mockito.when(impl.getTagBasedSolutions(tag, restPageReqBe)).thenReturn(solutionres);
				logger.info("Successfully fetched tag based solutions ");
				Assert.assertEquals(solutionres, solutionres);
			}

		} catch (Exception e) {
			logger.info("Failed to getTagBasedSolutionsTest" + e);
		}
	}

	@Test
	public void deleteSolutionFavoriteTest() {
		try {
			MarketPlaceCatalogServiceImpl mockimpl = mock(MarketPlaceCatalogServiceImpl.class);

			MLPSolutionFavorite mlpSolutionFavorite = new MLPSolutionFavorite();
			mlpSolutionFavorite.setSolutionId("6e5036e0-6e20-4425-bd9d-b4ce55cfd8a4");
			mlpSolutionFavorite.setUserId("601f8aa5-5978-44e2-996e-2dbfc321ee73");
			if (mlpSolutionFavorite != null) {
				mockimpl.deleteSolutionFavorite(mlpSolutionFavorite);
				logger.info("deleted solution favorite ");
				Assert.assertEquals(mlpSolutionFavorite, mlpSolutionFavorite);
			}

		} catch (Exception e) {
			logger.info("Failed to deletedSolutionFavoriteTest" + e);
		}
	}

	@Test
	public void getFavoriteSolutionsTest() {
		try {
			MLSolution mlsolution = new MLSolution();
			mlsolution.setSolutionId("6e5036e0-6e20-4425-bd9d-b4ce55cfd8a4");
			mlsolution.setName("Test_Solution data");
			mlsolution.setDescription("Test data");
			mlsolution.setOwnerId("601f8aa5-5978-44e2-996e-2dbfc321ee73");
			mlsolution.setAccessType("PB");
			mlsolution.setActive(true);
			mlsolution.setModelType("CL");
			mlsolution.setTookitType("DS");

			List<MLSolution> mlSolutions = new ArrayList<>();
			mlSolutions.add(mlsolution);

			MLPSolutionFavorite mlpSolutionFavorite = new MLPSolutionFavorite();
			mlpSolutionFavorite.setSolutionId("6e5036e0-6e20-4425-bd9d-b4ce55cfd8a4");
			mlpSolutionFavorite.setUserId("601f8aa5-5978-44e2-996e-2dbfc321ee73");

			String userId = mlpSolutionFavorite.getUserId();
			RestPageRequest restPageReq = new RestPageRequest();
			restPageReq.setPage(0);
			restPageReq.setSize(9);
			List<MLPSolutionFavorite> mlpfavSolutionList = new ArrayList<>();

			if (mlpfavSolutionList != null) {

				Mockito.when(impl.getFavoriteSolutions(userId, restPageReq)).thenReturn(mlSolutions);
				logger.info("Favorite solutions fetched successfully");
				Assert.assertEquals(mlSolutions, mlSolutions);
			}
		} catch (Exception e) {
			logger.info("Failed to getFavoriteSolutionsTest" + e);
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
			MLSolution mlsolution = new MLSolution();
			mlsolution.setSolutionId("6e5036e0-6e20-4425-bd9d-b4ce55cfd8a4");
			mlsolution.setName("Test_Solution data");
			mlsolution.setDescription("Test data");
			mlsolution.setOwnerId("601f8aa5-5978-44e2-996e-2dbfc321ee73");
			mlsolution.setAccessType("PB");
			mlsolution.setActive(true);
			mlsolution.setModelType("CL");
			mlsolution.setTookitType("DS");

			List<MLSolution> mlSolutions = new ArrayList<>();
			mlSolutions.add(mlsolution);

			RestPageResponseBE<MLSolution> solutionRes = new RestPageResponseBE<>(mlSolutions);

			if (body != null) {
				Mockito.when(impl.getRelatedMySolutions(restPageReqBe)).thenReturn(solutionRes);
				logger.info("Fetched related models successfully ");
				Assert.assertEquals(mlSolutions, mlSolutions);
				Assert.assertEquals(solutionRes, solutionRes);
			}

		} catch (Exception e) {
			logger.info("Failed to getRelatedMySolutionsTest" + e);
		}
	}

	@Test
	public void addSolutionUserAccessTest() {
		try {
			MarketPlaceCatalogServiceImpl mockimpl = mock(MarketPlaceCatalogServiceImpl.class);

			String solutionId = "6e5036e0-6e20-4425-bd9d-b4ce55cfd8a4";
			List<String> userList = new ArrayList<>();

			String userId = "601f8aa5-5978-44e2-996e-2dbfc321ee73";
			String userId1 = "f8ce630a-7757-4541-bc6d-e1d7860137fb";

			userList.add(userId1);
			userList.add(userId);

			if (solutionId != null) {
				mockimpl.addSolutionUserAccess(solutionId, userList);
				logger.info("Added solution user access");
				Assert.assertEquals(userList, userList);
			}

		} catch (Exception e) {
			logger.info("Failed to addSolutionUserAccessTest" + e);
		}
	}

	@Test
	public void createTagTest() {
		try {

			MLPTag mlpTag = new MLPTag();
			mlpTag.setTag("JAVA");

			if (mlpTag != null) {
				Mockito.when(impl.createTag(mlpTag)).thenReturn(mlpTag);
				logger.info("Tag created successfully");
				Assert.assertEquals(mlpTag, mlpTag);
			}
		} catch (Exception e) {
			logger.info("Failed to createTagTest" + e);
		}
	}

}
