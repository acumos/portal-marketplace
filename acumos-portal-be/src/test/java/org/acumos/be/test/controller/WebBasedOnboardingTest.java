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
import java.util.concurrent.Future;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acumos.portal.be.common.JsonRequest;
import org.acumos.portal.be.common.JsonResponse;
import org.acumos.portal.be.common.RestPageResponseBE;
import org.acumos.portal.be.controller.MarketPlaceCatalogServiceController;
import org.acumos.portal.be.controller.WebBasedOnboardingController;
import org.acumos.portal.be.service.AsyncServices;
import org.acumos.portal.be.transport.MLSolution;
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

}
