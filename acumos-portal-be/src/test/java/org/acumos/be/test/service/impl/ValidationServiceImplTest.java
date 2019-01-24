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

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.acumos.be.test.controller.UserServiceControllerTest;
import org.acumos.cds.client.CommonDataServiceRestClientImpl;
import org.acumos.cds.client.ICommonDataServiceRestClient;
import org.acumos.portal.be.service.impl.AbstractServiceImpl;
import org.acumos.portal.be.service.impl.ValidationStatusServiceImpl;
import org.acumos.portal.be.transport.MLArtifactValidationStatus;
import org.acumos.portal.be.transport.MLModelValidationStatus;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;


public class ValidationServiceImplTest {
	
	private static Logger logger = LoggerFactory.getLogger(UserServiceControllerTest.class);

	@Mock
	Environment env;

	@Mock
	AdminServiceImplTest test;

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	private final String url = "http://localhost:8002/ccds";
	private final String user = "ccds_client";
	private final String pass = "ccds_client";

	private ICommonDataServiceRestClient cmnDataService;

	private AbstractServiceImpl abstractImpl;

	
	@Before
	public void createClient() throws Exception {
		cmnDataService = CommonDataServiceRestClientImpl.getInstance(url.toString(), user, pass);
	}

	@Test
	public void dummy() {
		Assert.assertEquals("true", "true");
	}
	
	/*@Test
	public void getValidationTaskStatusTest(){
		try{
			when(env.getProperty("cdms.client.url")).thenReturn("http://localhost:8002/ccds");
			when(env.getProperty("cdms.client.username")).thenReturn("ccds_client");
			when(env.getProperty("cdms.client.password")).thenReturn("ccds_client");
			ValidationStatusServiceImpl impl = new ValidationStatusServiceImpl();
			impl.setEnvironment(env);
			
			String solutionId = "e475c3fe-9b6e-4427-b53b-359d54fdddd8";
			String revisionId = "b19a9617-27f2-4bae-aa30-71370003ef44";
			
			if(solutionId != null && revisionId !=null){
				impl.getValidationTaskStatus(solutionId, revisionId);
			}
			
		}catch(Exception e){
			logger.info("Failed to execute testCase ");
		}
	}
	
	
	@Test
	public void updateValidationTaskStatusTest(){
		try{
			when(env.getProperty("cdms.client.url")).thenReturn("http://localhost:8002/ccds");
			when(env.getProperty("cdms.client.username")).thenReturn("ccds_client");
			when(env.getProperty("cdms.client.password")).thenReturn("ccds_client");
			ValidationStatusServiceImpl impl = new ValidationStatusServiceImpl();
			impl.setEnvironment(env);
			
 
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
			
			if(taskId != null ){
				impl.updateValidationTaskStatus(taskId, mlModelvalidationStatus);
			}
			
		}catch(Exception e){
			logger.info("Failed to execute testCase ");
		}
	} */

}


