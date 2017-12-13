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

import org.acumos.cds.domain.MLPArtifact;
import org.acumos.cds.domain.MLPSolutionRevision;
import org.acumos.portal.be.controller.PushAndPullSolutionServiceController;
import org.acumos.portal.be.transport.MLSolution;
import org.acumos.portal.be.transport.User;
import org.acumos.portal.be.util.EELFLoggerDelegate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.multipart.MultipartFile;
import org.junit.Assert;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PushAndPullSolutionServiceControllerTest {
	
	private static final EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(PushAndPullSolutionServiceControllerTest.class);
	final HttpServletResponse response = new MockHttpServletResponse();
	final HttpServletRequest request = new MockHttpServletRequest();
	
	
	@Mock
	PushAndPullSolutionServiceController pushPullController = new PushAndPullSolutionServiceController();

	
	
	@Test
	public void downloadSolutionArtifactTest(){
		try{
			User user = new User();
			user.setUserId("8cbeccd0-ed84-42c3-8d9a-06d5629dc7bb");
			user.setFirstName("UserFirstName");
			user.setLastName("UserLastName");
			user.setUsername("User1");
			user.setEmailId("user1@emial.com");
			user.setActive("Y");
			user.setPassword("password");
			
			
			MLSolution mlsolution = new MLSolution();
			mlsolution.setSolutionId("Solution1");
			mlsolution.setName("Test_Solution data");
			mlsolution.setDescription("Test data");
			mlsolution.setOwnerId(user.getUserId());
			mlsolution.setAccessType("PB");
			mlsolution.setActive(true);
			mlsolution.setModelType("CL");
			mlsolution.setTookitType("DS");
			
			MLPSolutionRevision mlpSolRev = new MLPSolutionRevision();

			mlpSolRev.setRevisionId("REV2");
			mlpSolRev.setOwnerId("41058105-67f4-4461-a192-f4cb7fdafd34");
			mlpSolRev.setVersion("v.0.0");
			mlpSolRev.setDescription("test data for revision");
			mlpSolRev.setSolutionId(mlsolution.getSolutionId());

			MLPArtifact mockMLPArtifact = new MLPArtifact();
			mockMLPArtifact.setArtifactId("4cbf491b-c687-459f-9d81-e150d1a0b972");
			mockMLPArtifact.setArtifactTypeCode("MI");
			mockMLPArtifact.setDescription("Test data");
			mockMLPArtifact.setName("Test Artifact data");
			mockMLPArtifact.setOwnerId("8cbeccd0-ed84-42c3-8d9a-06d5629dc7bb");
			
			String userId = user.getUserId();
			String artifactId = mockMLPArtifact.getArtifactId();
			String revisionId = mlpSolRev.getRevisionId();
			String solutionId = mlsolution.getSolutionId();
			
			PushAndPullSolutionServiceController mockController = mock(PushAndPullSolutionServiceController.class);
			
			mockController.downloadSolutionArtifact(solutionId, artifactId, revisionId, userId, request, response);
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
			User user = new User();
			user.setUserId("8cbeccd0-ed84-42c3-8d9a-06d5629dc7bb");
			user.setFirstName("UserFirstName");
			user.setLastName("UserLastName");
			user.setUsername("User1");
			user.setEmailId("user1@emial.com");
			user.setActive("Y");
			user.setPassword("password");
			
			
			MLSolution mlsolution = new MLSolution();
			mlsolution.setSolutionId("Solution1");
			mlsolution.setName("Test_Solution data");
			mlsolution.setDescription("Test data");
			mlsolution.setOwnerId(user.getUserId());
			mlsolution.setAccessType("PB");
			mlsolution.setActive(true);
			mlsolution.setModelType("CL");
			mlsolution.setTookitType("DS");
			
			MLPSolutionRevision mlpSolRev = new MLPSolutionRevision();

			mlpSolRev.setRevisionId("REV2");
			mlpSolRev.setOwnerId("41058105-67f4-4461-a192-f4cb7fdafd34");
			mlpSolRev.setVersion("v.0.0");
			mlpSolRev.setDescription("test data for revision");
			mlpSolRev.setSolutionId(mlsolution.getSolutionId());

			MLPArtifact mockMLPArtifact = new MLPArtifact();
			mockMLPArtifact.setArtifactId("4cbf491b-c687-459f-9d81-e150d1a0b972");
			mockMLPArtifact.setArtifactTypeCode("MI");
			mockMLPArtifact.setDescription("Test data");
			mockMLPArtifact.setName("Test Artifact data");
			mockMLPArtifact.setOwnerId("8cbeccd0-ed84-42c3-8d9a-06d5629dc7bb");
			
			String userId = user.getUserId();
			MultipartFile dfsdf = null;
			MultipartFile file = dfsdf ;
			PushAndPullSolutionServiceController mockController = mock(PushAndPullSolutionServiceController.class);
			mockController.uploadModel(file, userId, request, response);
			Assert.assertNotNull(userId);
			logger.error("Successfully uploaded models");
		}catch (Exception e) {
			logger.error("Error while uploadModelTest", e);
		}
	}
	
}
