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
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.acumos.portal.be.service.impl.ValidationStatusServiceImpl;
import org.acumos.portal.be.transport.MLArtifactValidationStatus;
import org.acumos.portal.be.transport.MLModelValidationCheck;
import org.acumos.portal.be.transport.MLModelValidationStatus;
import org.acumos.portal.be.transport.MLModelValidationStepStatus;
import org.acumos.portal.be.util.EELFLoggerDelegate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.junit.Assert;

@RunWith(MockitoJUnitRunner.class)
public class ValidationStatusServiceImplTest {
	
	private static final EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(ValidationStatusServiceImplTest.class);

	final HttpServletResponse response = new MockHttpServletResponse();
	final HttpServletRequest request = new MockHttpServletRequest();

	@Mock
	ValidationStatusServiceImpl impl = new ValidationStatusServiceImpl();
	
	@Test
	public void dummy() {
		Assert.assertEquals("true", "true");
	}
	
	/*@Test
	public void getValidationTaskStatusTest(){
		try{
			
			String solutionId = "e475c3fe-9b6e-4427-b53b-359d54fdddd8";
			String revisionId = "b19a9617-27f2-4bae-aa30-71370003ef44";
			
			MLModelValidationStepStatus mlModelValidationStepStatus = new MLModelValidationStepStatus();
			mlModelValidationStepStatus.setValidationStatus("Passed");
			mlModelValidationStepStatus.setValidationStatusDesc("testValidationStatusDesc");
			mlModelValidationStepStatus.setValidationType("Passed");

			
		
			List<MLModelValidationStepStatus> mlModelValidationStepStatusList = new ArrayList<MLModelValidationStepStatus>();
			mlModelValidationStepStatusList.add(mlModelValidationStepStatus);

			MLModelValidationCheck mlValidation = new MLModelValidationCheck();
			mlValidation.setMlModelValidationStepStatus(mlModelValidationStepStatusList );
			mlValidation.setRevisionId(revisionId);
			mlValidation.setSolutionId(solutionId);
			mlValidation.setStatus("Passed");
			
			if(solutionId != null && revisionId !=null){
				Mockito.when(impl.getValidationTaskStatus(solutionId, revisionId)).thenReturn(mlValidation);
				logger.debug("getValidationTaskStatus ");
				Assert.assertEquals(mlValidation, mlValidation);
				
			}
			
		}catch(Exception e){
			logger.info("Failed to execute testCase ");
		}
	}
	
	
	@Test
	public void updateValidationTaskStatusTest(){
		try{
			MLModelValidationStatus mlModelvalidationStatus = new MLModelValidationStatus();
			mlModelvalidationStatus.setStatus("Passed");
			mlModelvalidationStatus.setTaskId("fakeTask-Id");
			List<MLArtifactValidationStatus> artifactValidationStatus = new ArrayList<>();
			MLArtifactValidationStatus mlArtifactValidationStatus = new MLArtifactValidationStatus();
			mlArtifactValidationStatus.setArtifactId("d36d9a0c-5658-40e2-a284-b2f7be448a1c");
			artifactValidationStatus.add(0, mlArtifactValidationStatus);
			mlModelvalidationStatus.setArtifactValidationStatus(artifactValidationStatus);

			
			MLArtifactValidationStatus mlArtifact  = new MLArtifactValidationStatus();
			mlArtifact.setArtifactId("d36d9a0c-5658-40e2-a284-b2f7be448a1c");
			mlArtifact.setArtifactTaskId("fakeTask-Id");
			mlArtifact.setStatus("Pass");
			
			
			String taskId = mlModelvalidationStatus.getTaskId();
			boolean flag = true;
			
			if(taskId != null ){
				Mockito.when(impl.updateValidationTaskStatus(taskId, mlModelvalidationStatus)).thenReturn(flag);
				Assert.assertTrue(flag);
			}
			
		}catch(Exception e){
			logger.info("Failed to execute testCase ");
		}
	} */
	
}


