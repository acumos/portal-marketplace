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
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.time.Instant;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acumos.cds.domain.MLPArtifact;
import org.acumos.cds.domain.MLPSiteConfig;
import org.acumos.cds.domain.MLPSolutionRevision;
import org.acumos.portal.be.controller.PushAndPullSolutionServiceController;
import org.acumos.portal.be.service.AdminService;
import org.acumos.portal.be.service.PushAndPullSolutionService;
import org.acumos.portal.be.transport.MLSolution;
import org.acumos.portal.be.transport.User;
import org.junit.Assert;
import org.junit.Before;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

@RunWith(MockitoJUnitRunner.class)
public class PushAndPullSolutionServiceControllerTest {
	
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());	
	final HttpServletResponse response = new MockHttpServletResponse();
	final HttpServletRequest request = new MockHttpServletRequest();
	
	@Mock
	AdminService adminService;
	@Mock
	private PushAndPullSolutionService pushAndPullSolutionService;
	
	private MockMvc mockMvc;
	
	@InjectMocks
	private PushAndPullSolutionServiceController pushPullController;
	
	@Before
	public void setUp() throws Exception {
		mockMvc = standaloneSetup(pushPullController).build();

	}
	
	@Test
	public void downloadSolutionArtifactTest(){
		try{
			
			User user = getUser();
			MLSolution mlsolution = getMLSolution();
			
			MLPArtifact mockMLPArtifact =  getMLPArtifact();
			
			MLPSolutionRevision mlpSolRev = getMLPSolutionRevision();
			
			String userId = user.getUserId();
			String artifactId = mockMLPArtifact.getArtifactId();
			String revisionId = mlpSolRev.getRevisionId();
			String solutionId = mlsolution.getSolutionId();
			
			InputStream resource = null;
			
			Mockito.when(pushAndPullSolutionService.getFileNameByArtifactId(artifactId)).thenReturn(mockMLPArtifact.getName());
			Mockito.when(pushAndPullSolutionService.downloadModelArtifact(artifactId)).thenReturn(resource);
			pushPullController.downloadSolutionArtifact(solutionId, artifactId, revisionId, userId, request, response);
			Assert.assertNotNull(solutionId);
			Assert.assertNotNull(artifactId); 
			Assert.assertNotNull(revisionId);
			Assert.assertNotNull(userId);
			logger.error("Successfully downloaded solution artifacts");
			
		}catch (Exception e) {
			logger.error("Error while downloadSolutionArtifactTest", e);
		}
	}
	
	@Test
	public void uploadModelTest(){
		try{
			User user = getUser();
			String userId = user.getUserId();
			MLPSiteConfig mlPSiteConfig = new MLPSiteConfig();
			mlPSiteConfig.setConfigKey("configKey");
			mlPSiteConfig.setConfigValue("configValue");
			mlPSiteConfig.setCreated(Instant.now());
			mlPSiteConfig.setUserId(userId);
			MultipartFile dfsdf = null;
			MultipartFile file = dfsdf ;
			Mockito.when(adminService.getSiteConfig("site_config")).thenReturn(mlPSiteConfig);
			//PushAndPullSolutionServiceController mockController = mock(PushAndPullSolutionServiceController.class);
			pushPullController.uploadModel(file, userId, true, request, response);
			Assert.assertNotNull(userId);
			logger.error("Successfully uploaded models");
		}catch (Exception e) {
			logger.error("Error while uploadModelTest", e);
		}
	}
	
	public String getNexusPropUrlTest(String key) {
		String nexusUrl = getPropertyValue(key);
		return nexusUrl;
	}

	private String getPropertyValue(String key) {
		return "nexus.url";
	}
	
	private User getUser(){
		User user = new User();
		user.setUserId("8cbeccd0-ed84-42c3-8d9a-06d5629dc7bb");
		user.setFirstName("UserFirstName");
		user.setLastName("UserLastName");
		user.setUsername("User1");
		user.setEmailId("user1@emial.com");
		user.setActive("Y");
		user.setPassword("password");
		return user;
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
	
	private MLPSolutionRevision getMLPSolutionRevision(){
		MLPSolutionRevision mlpSolRev = new MLPSolutionRevision();
		MLSolution mlsolution = getMLSolution();
		mlpSolRev.setRevisionId("REV2");
		mlpSolRev.setUserId("41058105-67f4-4461-a192-f4cb7fdafd34");
		mlpSolRev.setVersion("v.0.0");
		mlpSolRev.setSolutionId(mlsolution.getSolutionId());
		return mlpSolRev;
	}
	
	private MLPArtifact getMLPArtifact(){
		MLPArtifact mockMLPArtifact = new MLPArtifact();
		mockMLPArtifact.setArtifactId("4cbf491b-c687-459f-9d81-e150d1a0b972");
		mockMLPArtifact.setArtifactTypeCode("MI");
		mockMLPArtifact.setDescription("Test data");
		mockMLPArtifact.setName("Test Artifact data");
		mockMLPArtifact.setUserId("8cbeccd0-ed84-42c3-8d9a-06d5629dc7bb");
		return mockMLPArtifact;
		
	}
}
