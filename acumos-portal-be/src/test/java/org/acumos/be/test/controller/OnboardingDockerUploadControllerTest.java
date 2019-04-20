package org.acumos.be.test.controller;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acumos.cds.domain.MLPSolution;
import org.acumos.cds.domain.MLPSolutionRevision;
import org.acumos.cds.transport.RestPageResponse;
import org.acumos.portal.be.common.JsonRequest;
import org.acumos.portal.be.common.JsonResponse;
import org.acumos.portal.be.common.RestPageRequestBE;
import org.acumos.portal.be.controller.MarketPlaceCatalogServiceController;
import org.acumos.portal.be.controller.OnboardingDockerUploadController;
import org.acumos.portal.be.service.OnboardingDockerService;
import org.acumos.portal.be.transport.MLSolution;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

@RunWith(MockitoJUnitRunner.class)
public class OnboardingDockerUploadControllerTest {

	
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	final HttpServletResponse response = new MockHttpServletResponse();
	final HttpServletRequest request = new MockHttpServletRequest();

	@Mock
	private OnboardingDockerService onboardingDockerService;

	@InjectMocks
	private OnboardingDockerUploadController onboardingDockerUploadController;
	  
	@InjectMocks 
	private MarketPlaceCatalogServiceController marketPlaceController;
	 
	
	@Test
	public void testGetSearchSolutions() {
		
		try {
			MLPSolution mlpsolution = getMLPSolution();
			JsonRequest<RestPageRequestBE> restPageReqBE = new JsonRequest<>();
			List<MLPSolution> mlpSolutionList = new ArrayList<MLPSolution>();
			mlpSolutionList.add(mlpsolution);
			RestPageResponse<MLPSolution> mlpSolutionsRest = new RestPageResponse<>(mlpSolutionList);
			mlpSolutionsRest = new RestPageResponse<>(mlpSolutionList);
			RestPageRequestBE body = new RestPageRequestBE();
			body.setPage(0);
			body.setSize(9);
			restPageReqBE.setBody(body);
			JsonResponse<RestPageResponse<MLPSolution>> value = new JsonResponse<>();
			Mockito.when(onboardingDockerService.getRelatedSolution(restPageReqBE)).thenReturn(mlpSolutionsRest);
			value = onboardingDockerUploadController.getSearchSolutions(request, response, restPageReqBE);
			Assert.assertNotNull(value);
		} catch (Exception e) {
			logger.error("Failed to execute getSearchSolutions testcase", e);

		}	
	}

	@Test
	public void testGetArtifactsUrl() {
		
		try {
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
			Mockito.when(onboardingDockerService.getLatestArtifactsUrl(revisionRes.getContent())).thenReturn("www.ArtifactsURL.com");
			logger.info("testGetArtifactsUrl " + revisionRes.getResponseBody());
			Assert.assertNotNull(revisionRes);
		} catch (Exception e) {
			logger.error("Failed to execute testGetArtifactsUrl testcase", e);

		}
	}

	private MLPSolution getMLPSolution(){
		MLPSolution mlpsolution = new MLPSolution();
		mlpsolution.setActive(true);
		mlpsolution.setDownloadCount(12l);
		mlpsolution.setFeatured(true);
		mlpsolution.setMetadata("metadata");
		mlpsolution.setModelTypeCode("DI");
		mlpsolution.setName("Test_Solution data");
		mlpsolution.setOrigin("origin");
		mlpsolution.setRatingAverageTenths(10l);
		mlpsolution.setRatingCount(10l);
		mlpsolution.setSolutionId("Solution1");
		mlpsolution.setSourceId("sourceId");
		mlpsolution.setToolkitTypeCode("toolkitcode");
		mlpsolution.setUserId("f0ebe707-d436-40cf-9b0a-ed1ce8da1f2b");
		mlpsolution.setViewCount(10l);
		
		
		return mlpsolution;
	}
	
	
	private MLSolution getMLSolution(){
		MLSolution mlsolution = new MLSolution();
		mlsolution.setSolutionId("Solution1");
		mlsolution.setName("Test_Solution data");
		mlsolution.setOwnerId("41058105-67f4-4461-a192-f4cb7fdafd34");		
		mlsolution.setActive(true);
		mlsolution.setModelType("CL");
		mlsolution.setTookitType("DS");
		return mlsolution;
	}
}
