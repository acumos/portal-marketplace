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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acumos.cds.domain.MLPStepResult;
import org.acumos.cds.domain.MLPStepStatus;
import org.acumos.cds.domain.MLPStepType;
import org.acumos.portal.be.common.JsonRequest;
import org.acumos.portal.be.common.JsonResponse;
import org.acumos.portal.be.common.RestPageResponseBE;
import org.acumos.portal.be.controller.MarketPlaceCatalogServiceController;
import org.acumos.portal.be.controller.WebBasedOnboardingController;
import org.acumos.portal.be.service.AsyncServices;
import org.acumos.portal.be.service.MessagingService;
import org.acumos.portal.be.service.impl.MessagingServiceImpl;
import org.acumos.portal.be.transport.MLSolution;
import org.acumos.portal.be.transport.MLStepResult;
import org.acumos.portal.be.transport.UploadSolution;
import org.acumos.portal.be.util.EELFLoggerDelegate;
import org.apache.http.HttpResponse;
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
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(MockitoJUnitRunner.class)
public class WebBasedOnboardingTest {
	
	
	private static final EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(WebBasedOnboardingTest.class);

	final HttpServletResponse response = new MockHttpServletResponse();
	final HttpServletRequest request = new MockHttpServletRequest();
	
	@InjectMocks
	private WebBasedOnboardingController webBasedController;
	@Mock
	MessagingService messagingService;
	@Mock
	MessagingServiceImpl messagingServiceImpl;
	
	
	
	private MockMvc mockMvc;
	
	@Mock
	private AsyncServices asyncService;
	
	@Before
	public void setUp() throws Exception {
		mockMvc = standaloneSetup(webBasedController).build();

	}
	
	@Test
	public void testAddToCatalog() {

		try {

			MLSolution mlSolution = new MLSolution();
			mlSolution.setTookitType("CP");
			mlSolution.setTookitTypeName("Composite Solution");
			mlSolution.setSolutionId("6e5036e0-6e20-4425-bd9d-b4ce55cfd8a4");
			mlSolution.setDescription("Solution description");
			mlSolution.setAccessType("OR");
			mlSolution.setActive(true);
			Date date = new Date();
			mlSolution.setCreated(date);
			mlSolution.setDownloadCount(23);
			mlSolution.setLoginName("testerT1");
			mlSolution.setModelType("CL");
			mlSolution.setName("Solution name");
			mlSolution.setOwnerId("601f8aa5-5978-44e2-996e-2dbfc321ee73");
			mlSolution.setRatingCount((int) Math.round(3.2));
			Assert.assertNotNull(mlSolution);
			
			UploadSolution uploadSolution = new UploadSolution();
			uploadSolution.setName(mlSolution.getName());
			uploadSolution.setVersion("1.0.0v");
			Assert.assertNotNull(uploadSolution);
			
			JsonResponse<RestPageResponseBE<MLSolution>> data = new JsonResponse<>();
			List<MLSolution> content = new ArrayList<MLSolution>();
			content.add(mlSolution);
			RestPageResponseBE<MLSolution> responseBody = new RestPageResponseBE<MLSolution>(content );
			responseBody.getContent();
			data.setResponseBody(responseBody);
			JsonRequest<UploadSolution> restPageReq = new JsonRequest<UploadSolution>();
			restPageReq.setBody(uploadSolution);
			restPageReq.getBody();
			String userId = "601f8aa5-5978-44e2-996e-2dbfc321ee73";
			Assert.assertEquals(mlSolution.getOwnerId(), userId);
			JsonResponse<RestPageResponseBE<MLSolution>> value = new JsonResponse<RestPageResponseBE<MLSolution>>();
			value.setResponseBody(responseBody);
			UploadSolution solution = restPageReq.getBody();
			String provider = "abc";
			String access_token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzdXJ5YSIsInJvbGUiOlt7InBlcm1pc3Npb25MaXN0IjpudWxsLCJyb2xlQ291bnQiOjAsInJvbGVJZCI6IjEyMzQ1Njc4LWFiY2QtOTBhYi1jZGVmLTEyMzQ1Njc4OTBhYiIsIm5hbWUiOiJNTFAgU3lzdGVtIFVzZXIiLCJhY3RpdmUiOmZhbHNlLCJjcmVhdGVkIjoxNTE1NDEzMTM2MDAwLCJtb2RpZmllZCI6bnVsbH1dLCJjcmVhdGVkIjoxNTE1NDE2NzY4MzAyLCJleHAiOjE1MTYwMjE1NjgsIm1scHVzZXIiOnsiY3JlYXRlZCI6MTUxNTQxNjc1NTAwMCwibW9kaWZpZWQiOjE1MTU0MTY3NTUwMDAsInVzZXJJZCI6IjkxODY4MTA3LTc2NDktNGU4OS1hMTNjLWZhMzNhODYyODJiNSIsImZpcnN0TmFtZSI6InN1cnlha2FudCIsIm1pZGRsZU5hbWUiOm51bGwsImxhc3ROYW1lIjoiaW5nYWxlIiwib3JnTmFtZSI6bnVsbCwiZW1haWwiOiJzdXJ5YUB0ZWNobS5jb20iLCJsb2dpbk5hbWUiOiJzdXJ5YSIsImxvZ2luSGFzaCI6bnVsbCwibG9naW5QYXNzRXhwaXJlIjpudWxsLCJhdXRoVG9rZW4iOm51bGwsImFjdGl2ZSI6dHJ1ZSwibGFzdExvZ2luIjpudWxsLCJwaWN0dXJlIjpudWxsfX0.eTg1PbhDtoUtLI0oRaRMN7qMBrVHnqJQb_e5AATB55D1uUJIkWuTTU-YP-YNrdqYDzCpljo2WB7ILIQsNZ4ekA";
			Future<HttpResponse> future = null;
			//Mockito.when(asyncService.callOnboarding(userId, solution, provider, access_token)).thenReturn(future);
			value = webBasedController.addToCatalog(null, null, restPageReq, userId);
			logger.equals(value);
			logger.info("successfully added the toolkit to catalog ");
			Assert.assertNotNull(value);
		} catch (Exception e) {
			logger.error("Error while adding to catalog ", e);
			
		}

	}
	
	@Test
	public void createStepResultTest(){
		MLPStepResult mlpStepResult = new MLPStepResult();
		mlpStepResult.setStepResultId((long) 1);
		mlpStepResult.setStepCode("OB");
		mlpStepResult.setStatusCode("SU");
		mlpStepResult.setName("TestStepResult");
		mlpStepResult.setTrackingId("1234wer346576");
		mlpStepResult.setSolutionId("6e5036e0-6e20-4425-bd9d-b4ce55cfd8a4");
		mlpStepResult.setRevisionId("90361063-3ee0-434b-85da-208a8be6856d");
		mlpStepResult.setArtifactId("d36d9a0c-5658-40e2-a284-b2f7be448a1c");
		mlpStepResult.setUserId("41058105-67f4-4461-a192-f4cb7fdafd34");
		JsonResponse<MLPStepResult> mlpRes = new JsonResponse<>();
		when(messagingService.createStepResult(mlpStepResult)).thenReturn(mlpStepResult);
		mlpRes = webBasedController.createStepResult(mlpStepResult, response);
		
		//Negative scenario 
		MLPStepResult mlpNeg = new MLPStepResult();
		mlpNeg.setStepResultId(null);
		mlpNeg.setStepCode(null);
		mlpNeg.setStatusCode(null);
		mlpNeg.setName(null);
		mlpNeg.setTrackingId(null);
		mlpNeg.setSolutionId(null);
		mlpNeg.setRevisionId(null);
		mlpNeg.setArtifactId(null);
		mlpNeg.setUserId(null);
		
		mlpRes = webBasedController.createStepResult(mlpNeg, response);
	}

	@Test
	public void updateStepResultTest(){
		MLPStepResult mlpStepResult = new MLPStepResult();
		mlpStepResult.setStepResultId((long) 1);
		mlpStepResult.setStepCode("OB");
		mlpStepResult.setStatusCode("SU");
		mlpStepResult.setName("TestStepResult");
		mlpStepResult.setTrackingId("1234wer346576");
		mlpStepResult.setSolutionId("6e5036e0-6e20-4425-bd9d-b4ce55cfd8a4");
		mlpStepResult.setRevisionId("90361063-3ee0-434b-85da-208a8be6856d");
		mlpStepResult.setArtifactId("d36d9a0c-5658-40e2-a284-b2f7be448a1c");
		mlpStepResult.setUserId("41058105-67f4-4461-a192-f4cb7fdafd34");
		JsonResponse<MLPStepResult> mlpRes = new JsonResponse<>();
		MessagingServiceImpl mockImpl = mock(MessagingServiceImpl.class);
		mockImpl.updateStepResult(mlpStepResult);
		mlpRes = webBasedController.updateStepResult(mlpStepResult, response);
		
		//Negative scenario 
		MLPStepResult mlpNeg = new MLPStepResult();
		mlpNeg.setStepResultId(null);
		mlpNeg.setStepCode(null);
		mlpNeg.setStatusCode(null);
		mlpNeg.setName(null);
		mlpNeg.setTrackingId(null);
		mlpNeg.setSolutionId(null);
		mlpNeg.setRevisionId(null);
		mlpNeg.setArtifactId(null);
		mlpNeg.setUserId(null);
		
		mlpRes = webBasedController.updateStepResult(mlpNeg, response);
	}
	
	@Test
	public void deleteStepResult(){

		MLPStepResult mlpStepResult = new MLPStepResult();
		mlpStepResult.setStepResultId((long) 1);
		mlpStepResult.setStepCode("OB");
		mlpStepResult.setStatusCode("SU");
		mlpStepResult.setName("TestStepResult");
		mlpStepResult.setTrackingId("1234wer346576");
		mlpStepResult.setSolutionId("6e5036e0-6e20-4425-bd9d-b4ce55cfd8a4");
		mlpStepResult.setRevisionId("90361063-3ee0-434b-85da-208a8be6856d");
		mlpStepResult.setArtifactId("d36d9a0c-5658-40e2-a284-b2f7be448a1c");
		mlpStepResult.setUserId("41058105-67f4-4461-a192-f4cb7fdafd34");
		JsonResponse<MLPStepResult> mlpRes = new JsonResponse<>();
		MessagingServiceImpl mockImpl = mock(MessagingServiceImpl.class);
		
		long id = mlpStepResult.getStepResultId();
		mockImpl.deleteStepResult(id);
		mlpRes = webBasedController.deleteStepResult(request, id, response);
		
	
	}
	
	@Test
	public void getStepStatusesTest(){
		MLPStepStatus mlpStepStatus = new MLPStepStatus();
		mlpStepStatus.setCode("SU");
		mlpStepStatus.setName("Succeeded");
		MLPStepStatus mlpStepStatus1 = new MLPStepStatus();
		mlpStepStatus1.setCode("ST");
		mlpStepStatus1.setName("Started");
		MLPStepStatus mlpStepStatus2 = new MLPStepStatus();
		mlpStepStatus2.setCode("FA");
		mlpStepStatus2.setName("Failed");
		List<MLPStepStatus> stepList = new ArrayList<MLPStepStatus>();
		stepList.add(mlpStepStatus);
		stepList.add(mlpStepStatus1);
		stepList.add(mlpStepStatus2);
		when(messagingService.getStepStatuses()).thenReturn(stepList);
		webBasedController.getStepStatuses(request, response);
		
	}
	
	@Test
	public void getStepType(){
		MLPStepType mlpStepType = new MLPStepType();
		mlpStepType.setCode("OB");
		mlpStepType.setName("Onboarding");
		MLPStepType mlpStepType1 = new MLPStepType();
		mlpStepType1.setCode("VL");
		mlpStepType1.setName("Validation");
		List<MLPStepType> typeList = new ArrayList<MLPStepType>();
		typeList.add(mlpStepType);
		typeList.add(mlpStepType1);
		when(messagingService.getStepTypes()).thenReturn(typeList);
		webBasedController.getStepTypes(request, response);
	}

	@Test
	public void findStepresultBySolutionIdTest(){
		MLPStepResult mlpStepResult = new MLPStepResult();
		mlpStepResult.setStepResultId((long) 1);
		mlpStepResult.setStepCode("OB");
		mlpStepResult.setStatusCode("SU");
		mlpStepResult.setName("TestStepResult");
		mlpStepResult.setTrackingId("1234wer346576");
		mlpStepResult.setSolutionId("6e5036e0-6e20-4425-bd9d-b4ce55cfd8a4");
		mlpStepResult.setRevisionId("90361063-3ee0-434b-85da-208a8be6856d");
		mlpStepResult.setArtifactId("d36d9a0c-5658-40e2-a284-b2f7be448a1c");
		mlpStepResult.setUserId("41058105-67f4-4461-a192-f4cb7fdafd34");
		List<MLPStepResult> stepResultList = new ArrayList<MLPStepResult>();
		stepResultList.add(mlpStepResult);
		String solutionId = mlpStepResult.getSolutionId();
		String revisionId = mlpStepResult.getRevisionId();
		when(messagingService.findStepresultBySolutionId(solutionId, revisionId)).thenReturn(stepResultList);
		webBasedController.findStepresultBySolutionId(solutionId, revisionId);
	}
	
	@Test
	public void messagingStatusTest(){
		MLStepResult mlStepResult = new MLStepResult();
		mlStepResult.setStepResultId((long) 1);
		mlStepResult.setStepCode("OB");
		mlStepResult.setStatusCode("SU");
		mlStepResult.setName("TestStepResult");
		mlStepResult.setTrackingId("1234wer346576");
		mlStepResult.setSolutionId("6e5036e0-6e20-4425-bd9d-b4ce55cfd8a4");
		mlStepResult.setRevisionId("90361063-3ee0-434b-85da-208a8be6856d");
		mlStepResult.setArtifactId("d36d9a0c-5658-40e2-a284-b2f7be448a1c");
		mlStepResult.setUserId("41058105-67f4-4461-a192-f4cb7fdafd34");
		List<MLStepResult> stepResultList = new ArrayList<MLStepResult>();
		stepResultList.add(mlStepResult);
		String userId = mlStepResult.getUserId();
		String trackingId = mlStepResult.getTrackingId();
		when(messagingService.callOnBoardingStatusList(userId, trackingId)).thenReturn(stepResultList);
		webBasedController.messagingStatus(userId, trackingId);
		
	}
}
