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

import java.lang.invoke.MethodHandles;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acumos.portal.be.common.JsonResponse;
import org.acumos.portal.be.controller.PublishSolutionServiceController;
import org.acumos.portal.be.service.MarketPlaceCatalogService;
import org.acumos.portal.be.service.PublishSolutionService;
import org.acumos.portal.be.service.impl.PublishSolutionServiceImpl;
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
public class PublishSolutionServiceControllerTest {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());	

	final HttpServletResponse response = new MockHttpServletResponse();
	final HttpServletRequest request = new MockHttpServletRequest();

	
	@InjectMocks
	PublishSolutionServiceController publishController;
	
	@Mock
	PublishSolutionServiceImpl publishImpl;
	
	@Mock
	PublishSolutionService publishSolutionService;
	
	@Mock
	private MarketPlaceCatalogService catalogService;
	
	@Test
	public void publishSolutionTest() {
		try {
			MLSolution mlsolution = new MLSolution();
			mlsolution.setSolutionId("Solution1");
			mlsolution.setName("Test_Solution data");
			mlsolution.setOwnerId("41058105-67f4-4461-a192-f4cb7fdafd34");
			mlsolution.setActive(true);
			mlsolution.setModelType("CL");
			mlsolution.setTookitType("DS");
			mlsolution.setValidationStatusCode("PS");
			Assert.assertNotNull(mlsolution);
			String userId = "12121";
			String solutionId = mlsolution.getSolutionId();
			String revisionId = "30107769-d6b1-4758-821c-08023fe82f8f";
			String visibility = "2";
			UUID trackingId = UUID.randomUUID();
			Assert.assertNotNull(userId);
			Assert.assertNotNull(solutionId);
			Assert.assertNotNull(visibility);
			JsonResponse<Object> value = new JsonResponse<>();
			value.setResponseBody(mlsolution);
			String accessType = "PB";
			String catalogId = "1234-1234-1234-1234-1234";
			Mockito.when(publishSolutionService.publishSolution(solutionId, accessType , userId, revisionId, catalogId, trackingId)).thenReturn("Successfully published the solutions");
			Mockito.when(catalogService.getSolution(mlsolution.getSolutionId())).thenReturn(mlsolution);
			value = publishController.publishSolution(request, solutionId, visibility, userId, revisionId, catalogId, response);
			logger.info("Successfully published the solutions : ", value.getResponseBody());
			Assert.assertNotNull(value);
			accessType = "OR";
			Mockito.when(publishSolutionService.publishSolution(solutionId, accessType , userId, revisionId, catalogId, trackingId)).thenReturn("Successfully published the solutions");
			Mockito.when(catalogService.getSolution(mlsolution.getSolutionId())).thenReturn(mlsolution);
			value = publishController.publishSolution(request, solutionId, visibility, userId, revisionId, catalogId, response);
			logger.info("Successfully published the solutions : ", value.getResponseBody());
			Assert.assertNotNull(value);
			accessType = "PR";
			Mockito.when(publishSolutionService.publishSolution(solutionId, accessType , userId, revisionId, catalogId, trackingId)).thenReturn("Successfully published the solutions");
			Mockito.when(catalogService.getSolution(mlsolution.getSolutionId())).thenReturn(mlsolution);
			value = publishController.publishSolution(request, solutionId, visibility, userId, revisionId, catalogId, response);
			logger.info("Successfully published the solutions : ", value.getResponseBody());
			Assert.assertNotNull(value);
		} catch (Exception e) {
			logger.error("Error while publishSolutionTest", e);
		}
	}

	@Test
	public void unpublishSolutionTest() {
		try {
			MLSolution mlsolution = new MLSolution();
			mlsolution.setSolutionId("Solution1");
			mlsolution.setName("Test_Solution data");
			mlsolution.setOwnerId("41058105-67f4-4461-a192-f4cb7fdafd34");
			mlsolution.setActive(true);
			mlsolution.setModelType("CL");
			mlsolution.setTookitType("DS");
			Assert.assertNotNull(mlsolution);
			String userId = "12121";
			String solutionId = mlsolution.getSolutionId();
			String visibility = "2";
			Assert.assertNotNull(userId);
			Assert.assertNotNull(solutionId);
			Assert.assertNotNull(visibility);
			JsonResponse<Object> value = new JsonResponse<>();
			value.setResponseBody(mlsolution);
			String accessType = "PB";
			
			
			Mockito.when(publishSolutionService.unpublishSolution(solutionId, accessType, userId)).thenReturn(true);
			value = publishController.unpublishSolution(request, solutionId, visibility,userId, response);
			logger.info("Successfully unpublisheded the solutions : ", value.getResponseBody());
			Assert.assertNotNull(value);
			accessType = "OR";
			Mockito.when(publishSolutionService.unpublishSolution(solutionId, accessType, userId)).thenReturn(true);
			value = publishController.unpublishSolution(request, solutionId, visibility,userId, response);
			logger.info("Successfully unpublisheded the solutions : ", value.getResponseBody());
			Assert.assertNotNull(value);
			accessType = "PR";
			Mockito.when(publishSolutionService.unpublishSolution(solutionId, accessType, userId)).thenReturn(true);
			value = publishController.unpublishSolution(request, solutionId, visibility,userId, response);
			logger.info("Successfully unpublisheded the solutions : ", value.getResponseBody());
			Assert.assertNotNull(value);
			
			boolean unpublished = publishSolutionService.unpublishSolution(solutionId, accessType, userId);
			value = publishController.unpublishSolution(request, solutionId, visibility,userId, response);
			Assert.assertNotNull(value);
		} catch (Exception e) {
			logger.error("Error while unpublishSolutionTest", e);
		}
	}

}