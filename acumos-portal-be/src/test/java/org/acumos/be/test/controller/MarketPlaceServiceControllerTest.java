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

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acumos.cds.domain.MLPSolution;
import org.acumos.cds.domain.MLPSolutionFavorite;
import org.acumos.cds.domain.MLPSolutionRating;
import org.acumos.cds.domain.MLPSolutionRevision;
import org.acumos.cds.domain.MLPTag;
import org.acumos.cds.domain.MLPUser;
import org.acumos.cds.transport.RestPageRequest;
import org.acumos.cds.transport.RestPageResponse;
import org.acumos.portal.be.common.JsonRequest;
import org.acumos.portal.be.common.JsonResponse;
import org.acumos.portal.be.common.RestPageRequestBE;
import org.acumos.portal.be.common.RestPageResponseBE;
import org.acumos.portal.be.common.exception.AcumosServiceException;
import org.acumos.portal.be.controller.MarketPlaceCatalogServiceController;
import org.acumos.portal.be.service.MarketPlaceCatalogService;
import org.acumos.portal.be.service.NotificationService;
import org.acumos.portal.be.service.PushAndPullSolutionService;
import org.acumos.portal.be.service.UserService;
import org.acumos.portal.be.service.impl.MarketPlaceCatalogServiceImpl;
import org.acumos.portal.be.transport.MLArtifact;
import org.acumos.portal.be.transport.MLSolution;
import org.acumos.portal.be.transport.MLSolutionRating;
import org.acumos.portal.be.transport.MLSolutionWeb;
import org.acumos.portal.be.transport.RestPageRequestPortal;
import org.acumos.portal.be.transport.User;
import org.acumos.portal.be.util.PortalUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;


@RunWith(MockitoJUnitRunner.class)
public class MarketPlaceServiceControllerTest {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	final HttpServletResponse response = new MockHttpServletResponse();
	final HttpServletRequest request = new MockHttpServletRequest();

	@Mock
	private UserService userService;

	@Mock
	private MarketPlaceCatalogServiceImpl service;

	@Mock
	private MarketPlaceCatalogService marketPlaceCatalogService;
	@Mock
	private PushAndPullSolutionService pushAndPullSolutionService;

	private MockMvc mockMvc;

	@Mock
	private Environment env;
	
	@Mock
	private NotificationService notificationService;

	@InjectMocks
	private MarketPlaceCatalogServiceController marketPlaceController;
	
	@Rule
    public ExpectedException thrown = ExpectedException.none();

	@Before
	public void setUp() throws Exception {
		mockMvc = standaloneSetup(marketPlaceController).build();

	}

	@Test
	public void getSolutionsDetailsTest() {
		try {

			MLSolution mlsolution = getMLSolution();
			Assert.assertNotNull(mlsolution);
			JsonResponse<MLSolution> value = new JsonResponse<>();
			value.setResponseBody(mlsolution);

			String solutionId = mlsolution.getSolutionId();
			String revisionId = "5d893b98-d131-4657-a890-978cac70456c";
			String loginUserId = "41058105-67f4-4461-a192-f4cb7fdafd34";
			Assert.assertNotNull(solutionId);
			Mockito.when(service.getSolution(solutionId, revisionId, loginUserId)).thenReturn(mlsolution);
			value = marketPlaceController.getSolutionsDetails(request, solutionId, revisionId, response);
			logger.info("Solution Details : " + value.getResponseBody());
			Assert.assertNotNull(value);
		} catch (Exception e) {
			logger.error("Failed to execute getSolutionDetails testcase", e);
		}
	}

	@Test
	public void getSolutionImageTest() {
		try {
			String picString = "Placeholder for an image";
			byte[] picture = picString.getBytes();
			Assert.assertNotNull(picture);
			MLSolution mlsolution = getMLSolution();
			Assert.assertNotNull(mlsolution);
			ResponseEntity<byte[]> value = null;

			String solutionId = mlsolution.getSolutionId();
			Assert.assertNotNull(solutionId);
			Mockito.when(service.getSolution(solutionId)).thenReturn(mlsolution);
			Mockito.when(service.getSolutionPicture(solutionId)).thenReturn(picture);
			value = marketPlaceController.getSolutionImage(solutionId, null);
			Assert.assertNotNull(value);
			logger.info("Solution Image : " + new String(value.getBody()));
			Assert.assertEquals(picString, new String(value.getBody()));
		} catch (Exception e) {
			logger.error("Failed to execute getSolutionImage testcase", e);
		}
	}

	@Test
	public void getSolutionImage404Test() {
		try {
			MLSolution mlsolution = getMLSolution();
			Assert.assertNotNull(mlsolution);
			ResponseEntity<byte[]> value = null;

			String solutionId = mlsolution.getSolutionId();
			Assert.assertNotNull(solutionId);
			Mockito.when(service.getSolution(solutionId)).thenReturn(mlsolution);
			Mockito.when(service.getSolutionPicture(solutionId)).thenReturn(null);
			value = marketPlaceController.getSolutionImage(solutionId, null);
			Assert.assertNotNull(value);
			logger.info("Solution Image : " + value.getStatusCode());
			Assert.assertEquals(HttpStatus.NOT_FOUND, value.getStatusCode());
		} catch (Exception e) {
			logger.error("Failed to execute getSolutionImage404 testcase", e);
		}
	}
	
	@Test
	public void getSolutionImage304Test() {
		try {
			MLSolution mlsolution = getMLSolution();
			Assert.assertNotNull(mlsolution);
			ResponseEntity<byte[]> value = null;

			String solutionId = mlsolution.getSolutionId();
			Assert.assertNotNull(solutionId);
			
			String ifModifiedSince = DateTimeFormatter.RFC_1123_DATE_TIME.format(mlsolution.getModified());
			Mockito.when(service.getSolution(solutionId)).thenReturn(mlsolution);
			Mockito.when(service.getSolutionPicture(solutionId)).thenReturn(null);
			value = marketPlaceController.getSolutionImage(solutionId, ifModifiedSince);
			Assert.assertNotNull(value);
			logger.info("Solution Image : " + value.getStatusCode());
			Assert.assertEquals(HttpStatus.NOT_MODIFIED, value.getStatusCode());
		} catch (Exception e) {
			logger.error("Failed to execute getSolutionImage304 testcase", e);
		}
	}

	@Test
	public void getPaginatedListTest() throws AcumosServiceException {
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
			RestPageResponse<MLPSolution> responseBody = new RestPageResponse<>(solutionList, PageRequest.of(0, 1), 1);
			Assert.assertNotNull(responseBody);
			JsonResponse<RestPageResponse<MLPSolution>> value = new JsonResponse<>();
			value.setResponseBody(responseBody);
			Mockito.when(marketPlaceCatalogService.getAllPaginatedSolutions(Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(responseBody);
			value = marketPlaceController.getPaginatedList(mlSolutionReq);
			logger.info("Solutions are paginated : " + value.getResponseBody());
			Assert.assertNotNull(value);
			Mockito.when(marketPlaceCatalogService.getAllPaginatedSolutions(Mockito.any(),Mockito.any(),Mockito.any())).thenThrow(AcumosServiceException.class);
			value = marketPlaceController.getPaginatedList(mlSolutionReq);
	}

	@Test
	public void updateSolutionDetailsTest() throws AcumosServiceException {
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
			Mockito.when(marketPlaceCatalogService.checkUniqueSolName(Mockito.anyString(),Mockito.anyString())).thenReturn(true);
			Mockito.when(marketPlaceCatalogService.updateSolution(mlSolutionRes.getBody(), solutionId)).thenReturn(mlsolution);
			solutionres = marketPlaceController.updateSolutionDetails(request, response, solutionId, mlSolutionRes);
			logger.info("Succseefully updated solution details : " + solutionres.getResponseBody());
			Assert.assertNotNull(solutionres);
			Mockito.when(marketPlaceCatalogService.checkUniqueSolName(Mockito.anyString(),Mockito.anyString())).thenReturn(false);
			solutionres = marketPlaceController.updateSolutionDetails(request, response, solutionId, mlSolutionRes);
			Mockito.when(marketPlaceCatalogService.updateSolution(mlSolutionRes.getBody(), solutionId)).thenThrow(AcumosServiceException.class);
			//Mockito.when(marketPlaceCatalogService.checkUniqueSolName(Mockito.anyString(),Mockito.anyString())).thenThrow(AcumosServiceException.class);
			Mockito.when(marketPlaceCatalogService.checkUniqueSolName(Mockito.anyString(),Mockito.anyString())).thenReturn(true);
			solutionres = marketPlaceController.updateSolutionDetails(request, response, solutionId, mlSolutionRes);
	}

	@Test
	public void deleteSolutionArtifactsTest() throws AcumosServiceException, URISyntaxException {
		
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
			Mockito.when(marketPlaceCatalogService.checkUniqueSolName(Mockito.anyString(),Mockito.anyString())).thenReturn(true);
			solutionres = marketPlaceController.deleteSolutionArtifacts(request, response, solutionId, "somerevid",
					mlSolutionRes);
			logger.info("Succseefully delete Solution Artifacts : " + solutionres.getResponseBody());
			Assert.assertNotNull(solutionres);
			Mockito.when(marketPlaceCatalogService.checkUniqueSolName(Mockito.anyString(),Mockito.anyString())).thenReturn(false);
			solutionres = marketPlaceController.deleteSolutionArtifacts(request, response, solutionId, "somerevid",
					mlSolutionRes);
			Assert.assertNotNull(solutionres);
			Mockito.when(marketPlaceCatalogService.checkUniqueSolName(Mockito.anyString(),Mockito.anyString())).thenReturn(true);
			Mockito.when(marketPlaceCatalogService.deleteSolutionArtifacts(mlsolution, solutionId, "somerevid")).thenThrow(AcumosServiceException.class);
			solutionres = marketPlaceController.deleteSolutionArtifacts(request, response, solutionId, "somerevid",
					mlSolutionRes);
			mlSolutionRes.setBody(null);
			solutionres = marketPlaceController.deleteSolutionArtifacts(request, response, solutionId, "somerevid",
					mlSolutionRes);
	}

	@Test
	public void getSolutionsRevisionListTest() throws AcumosServiceException {
			MLSolution mlsolution = getMLSolution();
			Assert.assertNotNull(mlsolution);
			MLPSolutionRevision mlpSolRev = new MLPSolutionRevision();

			mlpSolRev.setRevisionId("REV2");
			mlpSolRev.setUserId(mlsolution.getOwnerId());
			mlpSolRev.setVersion("v.0.0");
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
			Mockito.when(marketPlaceCatalogService.getSolutionRevision(solutionId)).thenThrow(AcumosServiceException.class);
			revisionRes = marketPlaceController.getSolutionsRevisionList(request, response, solutionId);
	}

	@Test
	public void getSolutionsRevisionArtifactListTest() throws AcumosServiceException {
			MLSolution mlsolution = getMLSolution();
			Assert.assertNotNull(mlsolution);
			MLPSolutionRevision mlpSolRev = new MLPSolutionRevision();

			mlpSolRev.setRevisionId("REV2");
			mlpSolRev.setUserId(mlsolution.getOwnerId());
			mlpSolRev.setVersion("v.0.0");
			mlpSolRev.setSolutionId(mlsolution.getSolutionId());
			Assert.assertNotNull(mlpSolRev);
			MLArtifact mockMLArtifact = new MLArtifact();
			mockMLArtifact.setArtifactId("A1");
			mockMLArtifact.setArtifactType("MI");
			mockMLArtifact.setDescription("Test data");
			mockMLArtifact.setName("Test Artifact data");
			mockMLArtifact.setOwnerId(mlsolution.getOwnerId());
			Assert.assertNotNull(mockMLArtifact);
			String solutionId = mlpSolRev.getSolutionId();
			Assert.assertNotNull(solutionId);
			List<MLPSolutionRevision> revisionList = new ArrayList<MLPSolutionRevision>();
			revisionList.add(mlpSolRev);
			Assert.assertNotNull(revisionList);
			List<MLArtifact> artifactList = new ArrayList<MLArtifact>();
			artifactList.add(mockMLArtifact);
			Assert.assertNotNull(artifactList);
			JsonResponse<List<MLPSolutionRevision>> revisionRes = new JsonResponse<>();
			revisionRes.setResponseBody(revisionList);
			Assert.assertNotNull(revisionRes);
			JsonResponse<List<MLArtifact>> artifactRes = new JsonResponse<>();
			artifactRes.setResponseBody(artifactList);
			String revisionId = mlpSolRev.getRevisionId();
			//Mockito.when(service.getSolutionArtifacts(solutionId, revisionId)).thenReturn(artifactList);
			artifactRes = marketPlaceController.getSolutionsRevisionArtifactList(request, response, solutionId,
					revisionId);
			logger.info("Artifact List : " + artifactRes.getResponseBody());
			Assert.assertNotNull(artifactRes);
			Mockito.when(marketPlaceCatalogService.getSolutionArtifacts(solutionId, revisionId)).thenThrow(AcumosServiceException.class);
			artifactRes = marketPlaceController.getSolutionsRevisionArtifactList(request, response, solutionId,
					revisionId);
	}

	@Test
	public void addSolutionTagTest() throws AcumosServiceException {
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
			solRes=marketPlaceController.addSolutionTag(request, solutionId, tag, response);
			logger.info("Successfully added tags : " + solRes.getResponseBody());
			Assert.assertNotNull(solRes);
			//Mockito.when(marketPlaceCatalogService.addSolutionTag(solutionId, tag)).thenThrow(AcumosServiceException.class);
			solRes=marketPlaceController.addSolutionTag(request, solutionId, tag, response);
	}

	@Test
	public void addPublisherTest() {
		try {
			JsonResponse<String> solRes = new JsonResponse<>();
			Assert.assertNotNull(solRes);
			String solutionId = "49f1882d-3f83-47eb-9ed0-ec955db48163";
			String revisionId = "790b449e-de72-49c1-b819-e0353efd6f25";
			String publisher = "AcumosTest";
			Assert.assertNotNull(solutionId);
			Assert.assertNotNull(revisionId);
			Assert.assertNotNull(publisher);
			solRes.setResponseBody(publisher);
			Mockito.when(marketPlaceController.addPublisher(request, solutionId, revisionId, publisher, response))
					.thenReturn(solRes);
			logger.info("Successfully added Publisher : " + solRes.getResponseBody());
			Assert.assertNotNull(solRes);
		} catch (Exception e) {
			logger.error("Failed to execute addSolutionTag testcase", e);
		}
	}

	@Test
	public void getPublisherTest() {
		try {
			JsonResponse<String> solRes = new JsonResponse<>();
			Assert.assertNotNull(solRes);
			String solutionId = "49f1882d-3f83-47eb-9ed0-ec955db48163";
			String revisionId = "790b449e-de72-49c1-b819-e0353efd6f25";
			String publisher = "AcumosTest";
			Assert.assertNotNull(solutionId);
			Assert.assertNotNull(revisionId);
			Assert.assertNotNull(publisher);
			Mockito.when(marketPlaceController.getPublisher(request, solutionId, revisionId, response))
					.thenReturn(solRes);
			logger.info("Successfully added Publisher : " + solRes.getResponseBody());
			Assert.assertNotNull(solRes);
		} catch (Exception e) {
			logger.error("Failed to execute addSolutionTag testcase", e);
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
			// Mockito.when(marketPlaceController.dropSolutionTag(request,
			// solutionId, tag, response)).thenReturn(solRes);
			logger.info("Successfully dropped  tags : " + solRes.getResponseBody());
			Assert.assertNotNull(solRes);
		} catch (Exception e) {
			logger.error("Failed to execute dropSolutionTag testcase", e);
		}

	}

	@Test
	public void getTagsListTest() throws AcumosServiceException {
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
			List<String> mlTagsList = new ArrayList<String>();
			mlTagsList.add(mlpTag.getTag());
			Mockito.when(service.getTags(restPageReq)).thenReturn(mlTagsList);
			value = marketPlaceController.getTagsList(restPageReq);
			logger.info("Get Tag List : " + value.getResponseBody());
			Assert.assertNotNull(value);
			Mockito.when(marketPlaceCatalogService.getTags(restPageReq)).thenReturn(null);
			value = marketPlaceController.getTagsList(restPageReq);
			Mockito.when(marketPlaceCatalogService.getTags(restPageReq)).thenThrow(AcumosServiceException.class);
			value = marketPlaceController.getTagsList(restPageReq);
	}
	
	@Test
	public void createUserTagTest() {
		JsonRequest<RestPageRequestBE> restPageReq = new JsonRequest<>();
		RestPageRequestBE body = new RestPageRequestBE();
		List<String> tagList=new ArrayList<>();
		tagList.add("sometag");
		List<String> dropTagList=new ArrayList<>();
		dropTagList.add("dropsometag");
		body.setTagList(tagList);
		body.setDropTagList(dropTagList);
		restPageReq.setBody(body);
		marketPlaceController.createUserTag("someuserid", restPageReq);
	}

	@Test
	public void getTagsSolutionsTest() throws AcumosServiceException {
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
			responseBody = new RestPageResponseBE<>(solutionList);

			JsonResponse<RestPageResponseBE<MLSolution>> value = new JsonResponse<>();
			value.setResponseBody(responseBody);

			Mockito.when(marketPlaceCatalogService.getTagBasedSolutions(tag, restPageReq)).thenReturn(responseBody);
			value = marketPlaceController.getTagsSolutions(tag, restPageReq);
			logger.info("Tag for solution  " + value.getResponseBody());
			Assert.assertNotNull(value);
			Mockito.when(marketPlaceCatalogService.getTagBasedSolutions(tag, restPageReq)).thenThrow(AcumosServiceException.class);
			value = marketPlaceController.getTagsSolutions(tag, restPageReq);
	}

	@Test
	public void dropSolutionUserAccessTest() throws AcumosServiceException {
			MLSolution mlsolution = getMLSolution();
			Assert.assertNotNull(mlsolution);
			String solutionId = mlsolution.getSolutionId();
			String userId = mlsolution.getOwnerId();
			Assert.assertNotNull(solutionId);
			Assert.assertNotNull(userId);
			JsonResponse<User> value = new JsonResponse<>();

			Mockito.when(marketPlaceCatalogService.getSolution(solutionId)).thenReturn(mlsolution);
			MLPUser user = getMLPUser();
			Mockito.when(userService.findUserByUserId(userId)).thenReturn(user);
			// userService.findUserByUserId(userId)
			value = marketPlaceController.dropSolutionUserAccess(request, solutionId, userId, response);
			Assert.assertNotNull(value);
			Mockito.when(marketPlaceCatalogService.getSolution(solutionId)).thenThrow(AcumosServiceException.class);
			value = marketPlaceController.dropSolutionUserAccess(request, solutionId, userId, response);
	}

	@Test
	public void incrementSolutionViewCountTest() throws AcumosServiceException {
			MLSolution mlsolution = getMLSolution();
			mlsolution.setTookitType("DS");
			mlsolution.setViewCount(20);
			Assert.assertNotNull(mlsolution);
			String solutionId = mlsolution.getSolutionId();
			Assert.assertNotNull(solutionId);
			Assert.assertEquals(solutionId, mlsolution.getSolutionId());
			JsonResponse<MLSolution> value = new JsonResponse<>();
			when(marketPlaceCatalogService.getSolution(solutionId)).thenReturn(mlsolution);
			value=marketPlaceController.incrementSolutionViewCount(request, solutionId, response);
			Assert.assertNotNull(value);
			when(marketPlaceCatalogService.getSolution(solutionId)).thenThrow(AcumosServiceException.class);
			value=marketPlaceController.incrementSolutionViewCount(request, solutionId, response);
	}

	@Test
	public void createRatingTest() throws AcumosServiceException {
			MLSolution mlsolution = getMLSolution();
			MLPSolutionRating mlpSolutionRating = new MLPSolutionRating();
			Instant created = Instant.now();
			mlpSolutionRating.setCreated(created);
			Instant modified = Instant.now();
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
			Mockito.when(marketPlaceCatalogService.getSolution(mlpSolutionRatingREs.getBody().getSolutionId())).thenReturn(mlsolution);
			value = marketPlaceController.createSolutionRating(request, mlpSolutionRatingREs, response);
			Assert.assertNotNull(value);
			Mockito.when(marketPlaceCatalogService.getSolution(mlpSolutionRatingREs.getBody().getSolutionId())).thenThrow(AcumosServiceException.class);
			value = marketPlaceController.createSolutionRating(request, mlpSolutionRatingREs, response);
			
	}

	@Test
	public void updateRatingTest() throws AcumosServiceException {
			MLSolution mlsolution = getMLSolution();
			MLPSolutionRating mlpSolutionRating = new MLPSolutionRating();
			Instant created = Instant.now();
			mlpSolutionRating.setCreated(created);
			Instant modified = Instant.now();
			mlpSolutionRating.setModified(modified);
			mlpSolutionRating.setRating(2);
			mlpSolutionRating.setSolutionId("6e5036e0-6e20-4425-bd9d-b4ce55cfd8a4");
			mlpSolutionRating.setTextReview("ratings");
			mlpSolutionRating.setUserId("601f8aa5-5978-44e2-996e-2dbfc321ee73");
			Assert.assertNotNull(mlpSolutionRating);
			JsonRequest<MLPSolutionRating> mlpSolutionRatingREs = new JsonRequest<>();
			mlpSolutionRatingREs.setBody(mlpSolutionRating);
			Assert.assertNotNull(mlpSolutionRatingREs);
			Mockito.when(marketPlaceCatalogService.getSolution(Mockito.any())).thenReturn(mlsolution);
			JsonResponse<MLSolution> value = marketPlaceController.updateSolutionRating(request, mlpSolutionRatingREs,
					response);
			Assert.assertNotNull(value);
			Mockito.when(marketPlaceCatalogService.getSolution(Mockito.any())).thenThrow(AcumosServiceException.class);
			value = marketPlaceController.updateSolutionRating(request, mlpSolutionRatingREs,
					response);
		
	}

	@Test
	public void getsSolutionRatingsTest() throws AcumosServiceException {
			MLPSolution mlsolution = PortalUtils.convertToMLPSolution(getMLSolution());
			Assert.assertNotNull(mlsolution);
			String solutionId = mlsolution.getSolutionId();
			Assert.assertNotNull(solutionId);
			RestPageRequest pageRequest = new RestPageRequest();
			pageRequest.setPage(0);
			pageRequest.setSize(9);
			JsonRequest<RestPageRequest> rest = new JsonRequest<>();
			rest.setBody(pageRequest);
			MLPSolutionRating mlpSolutionRating=new MLPSolutionRating();
			mlpSolutionRating.setSolutionId("somesolid");
			mlpSolutionRating.setUserId("someuserid");
			List<MLPSolutionRating> mlpSolRatingList=new ArrayList<>();
			mlpSolRatingList.add(mlpSolutionRating);
			MLPUser user=getMLPUser();
			RestPageResponse<MLPSolutionRating> mlSolutionRating = new RestPageResponse<MLPSolutionRating>(mlpSolRatingList,PageRequest.of(0, 1), 1);
			JsonResponse<RestPageResponse<MLSolutionRating>> value = new JsonResponse<>();
			Mockito.when(userService.findUserByUserId(Mockito.any())).thenReturn(user);
			Mockito.when(marketPlaceCatalogService.getSolutionRating(solutionId, pageRequest)).thenReturn(mlSolutionRating);
			value = marketPlaceController.getSolutionRatings(solutionId, rest);
			Assert.assertNotNull(value);
			Mockito.when(marketPlaceCatalogService.getSolutionRating(solutionId, pageRequest)).thenThrow(AcumosServiceException.class);
			value = marketPlaceController.getSolutionRatings(solutionId, rest);
			
	}

	@Test
	public void getMysharedModelsTest() throws AcumosServiceException {
			MLSolution mlsolution = getMLSolution();
			String userId = mlsolution.getOwnerId();
			JsonResponse<List<MLSolution>> value = new JsonResponse<>();
			RestPageRequest restPageReq = new RestPageRequest();
			restPageReq.setPage(0);
			restPageReq.setSize(9);
			List<MLSolution> modelList = new ArrayList<>();
			modelList.add(mlsolution);
			value.setResponseBody(modelList);
			Mockito.when(service.getMySharedModels(Mockito.any(),Mockito.any())).thenReturn(modelList);
			value = marketPlaceController.getMySharedModels(request, userId, response);
			Assert.assertNotNull(value);
			Mockito.when(marketPlaceCatalogService.getMySharedModels(userId, restPageReq)).thenReturn(null);
			value = marketPlaceController.getMySharedModels(request, userId, response);
			Mockito.when(marketPlaceCatalogService.getMySharedModels(userId, restPageReq)).thenThrow(AcumosServiceException.class);
			value = marketPlaceController.getMySharedModels(request, userId, response);
			
	}

	@Test
	public void createSolutionFavoriteTest() throws AcumosServiceException {
			MLSolution mlsolution = getMLSolution();
			MLPSolutionFavorite mlpSolutionFavorite = new MLPSolutionFavorite();
			mlpSolutionFavorite.setSolutionId(mlsolution.getSolutionId());
			mlpSolutionFavorite.setUserId(mlsolution.getOwnerId());
			Assert.assertNotNull(mlpSolutionFavorite);
			JsonRequest<MLPSolutionFavorite> mlpSolutionFavoriteRes = new JsonRequest<>();
			mlpSolutionFavoriteRes.setBody(mlpSolutionFavorite);
			Assert.assertNotNull(mlpSolutionFavoriteRes);
			JsonResponse<MLSolution> value = new JsonResponse<>();
			Mockito.when(marketPlaceCatalogService.getSolution(mlpSolutionFavoriteRes.getBody().getSolutionId())).thenReturn(mlsolution);
			value = marketPlaceController.createSolutionFavorite(request, mlpSolutionFavoriteRes, response);
			Assert.assertNotNull(value);
			Mockito.when(marketPlaceCatalogService.getSolution(mlpSolutionFavoriteRes.getBody().getSolutionId())).thenThrow(AcumosServiceException.class);
			value = marketPlaceController.createSolutionFavorite(request, mlpSolutionFavoriteRes, response);
			
		
	}

	@Test
	public void deleteSolutionFavoriteTest() throws AcumosServiceException {
			MLSolution mlsolution = getMLSolution();
			MLPSolutionFavorite mlpSolutionFavorite = new MLPSolutionFavorite();
			mlpSolutionFavorite.setSolutionId("6e5036e0-6e20-4425-bd9d-b4ce55cfd8a4");
			mlpSolutionFavorite.setUserId("601f8aa5-5978-44e2-996e-2dbfc321ee73");
			Assert.assertNotNull(mlpSolutionFavorite);
			JsonRequest<MLPSolutionFavorite> mlpSolutionFavoriteRes = new JsonRequest<>();
			mlpSolutionFavoriteRes.setBody(mlpSolutionFavorite);
			Assert.assertNotNull(mlpSolutionFavoriteRes);
			JsonResponse<MLSolution> value = new JsonResponse<>();
			Mockito.when(marketPlaceCatalogService.getSolution(mlpSolutionFavoriteRes.getBody().getSolutionId())).thenReturn(mlsolution);
			value = marketPlaceController.deleteSolutionFavorite(request, mlpSolutionFavoriteRes, response);
			Assert.assertNotNull(value);
			Mockito.when(marketPlaceCatalogService.getSolution(mlpSolutionFavoriteRes.getBody().getSolutionId())).thenThrow(AcumosServiceException.class);
			value = marketPlaceController.deleteSolutionFavorite(request, mlpSolutionFavoriteRes, response);
			
	}

	@Test
	public void getFavoriteSolutionsTest() throws AcumosServiceException {
			MLSolution mlsolution = getMLSolution();
			MLPSolutionFavorite mlpSolutionFavorite = new MLPSolutionFavorite();
			mlpSolutionFavorite.setSolutionId(mlsolution.getSolutionId());
			mlpSolutionFavorite.setUserId(mlsolution.getOwnerId());
			List<MLSolution> mlSolutionList = new ArrayList<>();
			mlSolutionList.add(mlsolution);
			Assert.assertNotNull(mlpSolutionFavorite);
			String userId = mlpSolutionFavorite.getUserId();
			Assert.assertNotNull(userId);
			Assert.assertEquals(userId, mlpSolutionFavorite.getUserId());
			JsonResponse<List<MLSolution>> value = new JsonResponse<>();
			RestPageRequest restPageReq = new RestPageRequest();
			restPageReq.setPage(0);
			restPageReq.setSize(9);
			Mockito.when(marketPlaceCatalogService.getFavoriteSolutions(Mockito.any(), Mockito.any())).thenReturn(mlSolutionList);
			value = marketPlaceController.getFavoriteSolutions(request, userId, response);
			Assert.assertNotNull(value);
			Mockito.when(marketPlaceCatalogService.getFavoriteSolutions(Mockito.any(), Mockito.any())).thenReturn(null);
			value = marketPlaceController.getFavoriteSolutions(request, userId, response);
			Mockito.when(marketPlaceCatalogService.getFavoriteSolutions(Mockito.any(), Mockito.any())).thenThrow(AcumosServiceException.class);
			value = marketPlaceController.getFavoriteSolutions(request, userId, response);
			
	}

	@Test
	public void getRelatedMySolutionsTest() throws AcumosServiceException {
			MLSolution mlsolution = getMLSolution();
			JsonRequest<RestPageRequestBE> restPageReqBe = new JsonRequest<>();
			List<MLSolution> mlSolutionList = new ArrayList<MLSolution>();
			mlSolutionList.add(mlsolution);
			RestPageResponseBE<MLSolution> mlSolutionsRest = new RestPageResponseBE<>(mlSolutionList);
			mlSolutionsRest = new RestPageResponseBE<>(mlSolutionList);
			RestPageRequestBE body = new RestPageRequestBE();
			body.setPage(0);
			body.setSize(9);
			restPageReqBe.setBody(body);
			JsonResponse<RestPageResponseBE<MLSolution>> value = new JsonResponse<>();
			Mockito.when(marketPlaceCatalogService.getRelatedMySolutions(restPageReqBe)).thenReturn(mlSolutionsRest);
			value = marketPlaceController.getRelatedMySolutions(restPageReqBe);
			Assert.assertNotNull(value);
			Mockito.when(marketPlaceCatalogService.getRelatedMySolutions(restPageReqBe)).thenThrow(AcumosServiceException.class);
			value = marketPlaceController.getRelatedMySolutions(restPageReqBe);
			
	}

	@Test
	public void readArtifactSolutionsTest() {
			String artifactId = "4cbf491b-c687-459f-9d81-e150d1a0b972";
			String value = "Artifact read successfully";
			InputStream resource=new ByteArrayInputStream(value.getBytes(StandardCharsets.UTF_8));
			Mockito.when(pushAndPullSolutionService.getFileNameByArtifactId(artifactId)).thenReturn(value);
			Mockito.when(pushAndPullSolutionService.downloadModelArtifact(artifactId)).thenReturn(resource);
			marketPlaceController.readArtifactSolutions(artifactId, request, response);
			Assert.assertNotNull(artifactId);
	
	}

	@Test
	public void getSolutionUserAccessTest() throws AcumosServiceException {
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
			Mockito.when(marketPlaceCatalogService.getSolutionUserAccess(solutionId)).thenReturn(userList);
			data = marketPlaceController.getSolutionUserAccess(request, solutionId, response);
			logger.info("RevisionList " + data.getResponseBody());
			Assert.assertNotNull(data);
			Mockito.when(marketPlaceCatalogService.getSolutionUserAccess(solutionId)).thenReturn(null);
			data = marketPlaceController.getSolutionUserAccess(request, solutionId, response);
			Mockito.when(marketPlaceCatalogService.getSolutionUserAccess(solutionId)).thenThrow(AcumosServiceException.class);
			data = marketPlaceController.getSolutionUserAccess(request, solutionId, response);
			
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
			data = marketPlaceController.addSolutionUserAccess(request, solutionId, userIdList, response);
			// logger.info("RevisionList " + data.getResponseBody());
			Assert.assertNotNull(data);
		} catch (Exception e) {
			logger.error("Failed to execute addSolutionUserAccess testcase", e);
		}
	}

	@Test
	public void createTagTest() throws AcumosServiceException {
			MLPTag mlpTag = new MLPTag();
			mlpTag.setTag("abc");
			JsonRequest<MLPTag> tags = new JsonRequest<MLPTag>();
			tags.setBody(mlpTag);
			JsonResponse<MLPTag> data = new JsonResponse<MLPTag>();
			data.setResponseDetail("Tags created Successfully");
			Mockito.when(service.createTag(tags.getBody())).thenReturn(mlpTag);
			data = marketPlaceController.createTag(request, tags, response);
			Assert.assertNotNull(data);
			Mockito.when(marketPlaceCatalogService.createTag(tags.getBody())).thenThrow(AcumosServiceException.class);
			data = marketPlaceController.createTag(request, tags, response);
		
	}

	@Test
	public void getUserRatingsTest() {
			JsonResponse<MLPSolutionRating> data = new JsonResponse<>();
			MLPSolutionRating mlSolutionRating = new MLPSolutionRating();
			mlSolutionRating.setRating(4);
			mlSolutionRating.setSolutionId("6e5036e0-6e20-4425-bd9d-b4ce55cfd8a4");
			mlSolutionRating.setUserId("1213505-67f4-4461-a192-f4cb7fdafd34");
			String solutionId = "6e5036e0-6e20-4425-bd9d-b4ce55cfd8a4";
			String userId = "1213505-67f4-4461-a192-f4cb7fdafd34";
			data.setResponseDetail("Ratings fetched Successfully");
			Mockito.when(service.getUserRatings(solutionId, userId)).thenReturn(mlSolutionRating);
			data = marketPlaceController.getUserRatings(request, solutionId, userId, response);
			Assert.assertNotNull(data);
			
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
			Mockito.when(service.searchSolutionsByKeyword(restPageReqPortal.getBody())).thenReturn(mlSolutionsRest);
			Assert.assertNotNull(mlSolutionsRest);
			data = marketPlaceController.findPortalSolutions(request, restPageReqPortal, response);
			Assert.assertNotNull(data);
		} catch (Exception e) {
			logger.error("Failed to execute findPortalSolutions testcase", e);

		}
	}

	@Test
	public void getAvgRatingsForSolTest() {
		MLSolutionWeb mlSolutionWeb =new MLSolutionWeb();
		mlSolutionWeb.setDownloadCount(20L);
		mlSolutionWeb.setFeatured(true);
		mlSolutionWeb.setSolutionId("somsolid");
		mlSolutionWeb.setRatingCount(22L);
		JsonRequest<MLSolutionWeb> request=new JsonRequest<>();
		request.setBody(mlSolutionWeb);
		when(marketPlaceCatalogService.getSolutionWebMetadata("somesolid")).thenReturn(mlSolutionWeb);
		JsonResponse<MLSolutionWeb> res=marketPlaceController.getAvgRatingsForSol("somesolid");
		Assert.assertNotNull(res);
		when(marketPlaceCatalogService.getSolutionWebMetadata("somesolid")).thenReturn(null);
		res=marketPlaceController.getAvgRatingsForSol("somesolid");
		
	}
	
	@Test
	public void fetchProtoFileTest() throws AcumosServiceException {
		when(marketPlaceCatalogService.getProtoUrl(Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any())).thenReturn("someurl");
		String res=marketPlaceController.fetchProtoFile("somsolid", "ver1.0");
		Assert.assertNotNull(res);
		when(marketPlaceCatalogService.getProtoUrl(Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any())).thenThrow(AcumosServiceException.class);
		res=marketPlaceController.fetchProtoFile("somsolid", "ver1.0");
		
	}
	
	@Test
	public void fetchLicenseFileTest() throws AcumosServiceException {
		when(marketPlaceCatalogService.getLicenseUrl(Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any())).thenReturn("someurl");
		String res=marketPlaceController.fetchLicenseFile("somsolid", "ver1.0");
		Assert.assertNotNull(res);
		when(marketPlaceCatalogService.getLicenseUrl(Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any())).thenThrow(AcumosServiceException.class);
		res=marketPlaceController.fetchLicenseFile("somsolid", "ver1.0");
		
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
		RestPageResponse<MLPSolution> responseBody = new RestPageResponse<>(solutionList, PageRequest.of(0, 1), 1);
		Assert.assertNotNull(responseBody);
		String userId = "1213505-67f4-4461-a192-f4cb7fdafd34";
		Mockito.when(service.getUserAccessSolutions(userId, new RestPageRequest(0, 99))).thenReturn(responseBody);
		JsonRequest<RestPageRequest> restPageReq = new JsonRequest<>();
		JsonResponse<RestPageResponse<MLPSolution>> data = marketPlaceController.getUserAccessSolutions(userId,
				restPageReq);
		Assert.assertNotNull(data);
	}

	private MLSolution getMLSolution() {
		MLSolution mlsolution = new MLSolution();
		mlsolution.setSolutionId("Solution1");
		mlsolution.setName("Test_Solution data");
		mlsolution.setOwnerId("41058105-67f4-4461-a192-f4cb7fdafd34");
		mlsolution.setActive(true);
		mlsolution.setModelType("CL");
		mlsolution.setTookitType("DS");
		mlsolution.setModified(Instant.now());
		return mlsolution;
	}

	private MLPUser getMLPUser() {
		MLPUser mlpUser = new MLPUser();
		mlpUser.setActive(true);
		mlpUser.setFirstName("test-first-name");
		mlpUser.setUserId("f0ebe707-d436-40cf-9b0a-ed1ce8da1f2b");
		mlpUser.setLoginName("test-User-Name");
		mlpUser.setFirstName("firstname");
		mlpUser.setLastName("lastName");
		return mlpUser;
	}

	@Test
	public void getCloudEnabledList() {
		JsonResponse<String> responseVO = marketPlaceController.getCloudEnabledList(request, response);
		Assert.assertNotNull(responseVO);
	}
}
