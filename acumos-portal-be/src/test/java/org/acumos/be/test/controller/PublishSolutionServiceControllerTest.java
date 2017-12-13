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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.acumos.portal.be.common.JsonResponse;
import org.acumos.portal.be.controller.PublishSolutionServiceController;
import org.acumos.portal.be.transport.MLSolution;
import org.junit.Ignore;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
/**
 * 
 * 
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class PublishSolutionServiceControllerTest {

	private static Logger logger = LoggerFactory.getLogger(PublishSolutionServiceControllerTest.class);

	final HttpServletResponse response = new MockHttpServletResponse();
	final HttpServletRequest request = new MockHttpServletRequest();

	
	@Mock
	PublishSolutionServiceController publishController = new PublishSolutionServiceController();
	
	@Test
	public void publishSolutionTest() {
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
			
			String userId = "12121";
			String solutionId = mlsolution.getSolutionId();
			String visibility = "2";
			
			JsonResponse<Object> value = new JsonResponse<>();
			value.setResponseBody(mlsolution);
			Mockito.when(publishController.publishSolution(request, solutionId, visibility, userId,response)).thenReturn(value );
			logger.info("Successfully published the solutions : ", value.getResponseBody());

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
			mlsolution.setDescription("Test data");
			mlsolution.setOwnerId("41058105-67f4-4461-a192-f4cb7fdafd34");
			mlsolution.setAccessType("PB");
			mlsolution.setActive(true);
			mlsolution.setModelType("CL");
			mlsolution.setTookitType("DS");
			
			String userId = "12121";
			String solutionId = mlsolution.getSolutionId();
			String visibility = "2";
			
			JsonResponse<Object> value = new JsonResponse<>();
			value.setResponseBody(mlsolution);
			Mockito.when(publishController.unpublishSolution(request, solutionId, visibility,
					userId, response)).thenReturn(value);
			logger.info("Successfully unpublisheded the solutions : ", value.getResponseBody());

		} catch (Exception e) {
			logger.error("Error while unpublishSolutionTest", e);
		}
	}

}
