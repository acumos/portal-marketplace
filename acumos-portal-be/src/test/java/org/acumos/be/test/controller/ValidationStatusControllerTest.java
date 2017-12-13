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
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acumos.portal.be.common.JsonResponse;
import org.acumos.portal.be.controller.ValidationStatusController;
import org.acumos.portal.be.transport.MLArtifactValidationStatus;
import org.acumos.portal.be.transport.MLModelValidationCheck;
import org.acumos.portal.be.transport.MLModelValidationStatus;
import org.acumos.portal.be.transport.MLModelValidationStepStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.client.RestTemplate;
/**
 * 
 * 
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class ValidationStatusControllerTest {

	private static Logger logger = LoggerFactory.getLogger(ValidationStatusControllerTest.class);

	final HttpServletResponse response = new MockHttpServletResponse();
	final HttpServletRequest request = new MockHttpServletRequest();
	
	@Mock
	ValidationStatusController validationController = new ValidationStatusController();

	@Test
	public void testUpdateValidationTaskStatus() {
		try {

			MLModelValidationStatus mlModelvalidationStatus = new MLModelValidationStatus();
			mlModelvalidationStatus.setStatus("Passed");
			mlModelvalidationStatus.setTaskId("fakeTask-Id");
			List<MLArtifactValidationStatus> artifactValidationStatus = new ArrayList<>();
			MLArtifactValidationStatus mlArtifactValidationStatus = new MLArtifactValidationStatus();
			mlArtifactValidationStatus.setArtifactId("4cbf491b-c687-459f-9d81-e150d1a0b972");
			artifactValidationStatus.add(0, mlArtifactValidationStatus);
			mlModelvalidationStatus.setArtifactValidationStatus(artifactValidationStatus);

			
			String taskId = mlModelvalidationStatus.getTaskId();
			
			JsonResponse<Object> value = new JsonResponse<>();
			value.setResponseBody(mlArtifactValidationStatus);
			value.getResponseBody();
	
			JsonResponse<Object> value1 = validationController.updateValidationTaskStatus(request, taskId, mlModelvalidationStatus, response);


			logger.info("successfully updated validationTaskStatus");
			logger.equals(value.getResponseBody());
		} catch (Exception e) {
			
			logger.error("Error while updating validation status", e);
		}
	}

	@Test
	public void testGetValidationTaskStatus() {
		try {

			MLModelValidationStatus mlModelvalidationStatus = new MLModelValidationStatus();
			mlModelvalidationStatus.setStatus("Passed");
			mlModelvalidationStatus.setTaskId("fakeTask-Id");
			List<MLArtifactValidationStatus> artifactValidationStatus = new ArrayList<>();
			MLArtifactValidationStatus mlArtifactValidationStatus = new MLArtifactValidationStatus();
			mlArtifactValidationStatus.setArtifactId("4cbf491b-c687-459f-9d81-e150d1a0b972");
			artifactValidationStatus.add(0, mlArtifactValidationStatus);
			mlModelvalidationStatus.setArtifactValidationStatus(artifactValidationStatus);

			MLModelValidationStepStatus mlModelValidationStepStatus = new MLModelValidationStepStatus();
			mlModelValidationStepStatus.setValidationStatus("Passed");
			mlModelValidationStepStatus.setValidationStatusDesc("testValidationStatusDesc");
			mlModelValidationStepStatus.setValidationType("Passed");

			
			String solutionId = "6e5036e0-6e20-4425-bd9d-b4ce55cfd8a4";
			String revisionId = "601f8aa5-5978-44e2-996e-2dbfc321ee73";

			JsonResponse<MLModelValidationCheck> value = new JsonResponse<>();
			MLModelValidationCheck responseBody = new MLModelValidationCheck();
			List<MLModelValidationStepStatus> mlModelValidationStepStatusList = new ArrayList<MLModelValidationStepStatus>();
			mlModelValidationStepStatusList.add(mlModelValidationStepStatus);
			responseBody.setMlModelValidationStepStatus(mlModelValidationStepStatusList);
			value.setResponseBody(responseBody);
			value.getResponseBody();
			Mockito.when(validationController.getValidationTaskStatus(request, solutionId, revisionId, response))
					.thenReturn(value);
			logger.equals(value.getResponseBody());
			logger.info("successfully fetched getValidationTaskStatus ");
		} catch (Exception e) {
			
			logger.error("Error while updating validation status", e);
		}

	}
	
}
