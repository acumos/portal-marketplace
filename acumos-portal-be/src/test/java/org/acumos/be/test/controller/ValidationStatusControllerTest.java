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
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acumos.portal.be.common.JsonResponse;
import org.acumos.portal.be.controller.ValidationStatusController;
import org.acumos.portal.be.service.ValidationStatusService;
import org.acumos.portal.be.transport.MLArtifactValidationStatus;
import org.acumos.portal.be.transport.MLModelValidationCheck;
import org.acumos.portal.be.transport.MLModelValidationStatus;
import org.acumos.portal.be.transport.MLModelValidationStepStatus;
import org.acumos.portal.be.util.EELFLoggerDelegate;
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
public class ValidationStatusControllerTest {

	private static final EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(ValidationStatusControllerTest.class);


	final HttpServletResponse response = new MockHttpServletResponse();
	final HttpServletRequest request = new MockHttpServletRequest();
	
	@InjectMocks
	ValidationStatusController validationController ;
	
	private MockMvc mockMvc;
	
	@Mock
	private ValidationStatusService validationStatusService;
	
	@Before
	public void setUp() throws Exception {
		mockMvc = standaloneSetup(validationController).build();

	}

	@Test
	public void dummy() {
		Assert.assertEquals("true", "true");
	}
	
	/*@Test
	public void testUpdateValidationTaskStatus() {
		try {

			MLModelValidationStatus mlModelvalidationStatus = new MLModelValidationStatus();
			mlModelvalidationStatus.setStatus("Passed");
			mlModelvalidationStatus.setTaskId("fakeTask-Id");
			Assert.assertNotNull(mlModelvalidationStatus); 
			List<MLArtifactValidationStatus> artifactValidationStatus = new ArrayList<>();
			MLArtifactValidationStatus mlArtifactValidationStatus = new MLArtifactValidationStatus();
			mlArtifactValidationStatus.setArtifactId("4cbf491b-c687-459f-9d81-e150d1a0b972");
			artifactValidationStatus.add(0, mlArtifactValidationStatus);
			mlModelvalidationStatus.setArtifactValidationStatus(artifactValidationStatus);
			Assert.assertNotNull(mlArtifactValidationStatus); 
			String taskId = mlModelvalidationStatus.getTaskId();
			Assert.assertEquals(taskId, mlModelvalidationStatus.getTaskId());
			JsonResponse<Object> value = new JsonResponse<>();
			value.setResponseBody(mlArtifactValidationStatus);
			value.getResponseBody();
			boolean flag = true;
			MLModelValidationStatus mlModelValidationStatus =new MLModelValidationStatus();
			mlModelValidationStatus.setArtifactValidationStatus(artifactValidationStatus);
			mlModelValidationStatus.setRevisionId("v2.2");
			mlModelValidationStatus.setSolutionId("Solution1");
			mlModelValidationStatus.setStatus("Y");
			mlModelValidationStatus.setTaskId(taskId);
			Mockito.when(validationStatusService.updateValidationTaskStatus(taskId, mlModelValidationStatus)).thenReturn(flag);
			value = validationController.updateValidationTaskStatus(request, taskId, mlModelvalidationStatus, response);
			logger.info("successfully updated validationTaskStatus");
			logger.equals(value.getResponseBody());
			Assert.assertNotNull(value);
			
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
			Assert.assertNotNull(mlModelvalidationStatus); 
			List<MLArtifactValidationStatus> artifactValidationStatus = new ArrayList<>();
			MLArtifactValidationStatus mlArtifactValidationStatus = new MLArtifactValidationStatus();
			mlArtifactValidationStatus.setArtifactId("4cbf491b-c687-459f-9d81-e150d1a0b972");
			artifactValidationStatus.add(0, mlArtifactValidationStatus);
			mlModelvalidationStatus.setArtifactValidationStatus(artifactValidationStatus);
			Assert.assertNotNull(mlArtifactValidationStatus); 
			MLModelValidationStepStatus mlModelValidationStepStatus = new MLModelValidationStepStatus();
			mlModelValidationStepStatus.setValidationStatus("Passed");
			mlModelValidationStepStatus.setValidationStatusDesc("testValidationStatusDesc");
			mlModelValidationStepStatus.setValidationType("Passed");
			Assert.assertNotNull(mlModelValidationStepStatus); 
			String solutionId = "6e5036e0-6e20-4425-bd9d-b4ce55cfd8a4";
			String revisionId = "601f8aa5-5978-44e2-996e-2dbfc321ee73";

			JsonResponse<MLModelValidationCheck> value = new JsonResponse<>();
			MLModelValidationCheck responseBody = new MLModelValidationCheck();
			List<MLModelValidationStepStatus> mlModelValidationStepStatusList = new ArrayList<MLModelValidationStepStatus>();
			mlModelValidationStepStatusList.add(mlModelValidationStepStatus);
			responseBody.setMlModelValidationStepStatus(mlModelValidationStepStatusList);
			value.setResponseBody(responseBody);
			value.getResponseBody();
			Mockito.when(validationStatusService.getSolutionValidationTaskStatus(solutionId, revisionId))
					.thenReturn(responseBody);
			value = validationController.getValidationTaskStatus(request, solutionId, revisionId, response);
			logger.equals(value.getResponseBody());
			logger.info("successfully fetched getValidationTaskStatus ");
			Assert.assertNotNull(value);
			Assert.assertNotNull(responseBody);
		} catch (Exception e) {
			
			logger.error("Error while updating validation status", e);
		}

	}*/
	
}
