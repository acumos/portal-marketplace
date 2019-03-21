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

import java.lang.invoke.MethodHandles;
import java.util.UUID;

import org.acumos.portal.be.service.impl.PublishSolutionServiceImpl;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(MockitoJUnitRunner.class)
public class PublishSolutionServiceImplTest {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());	

	@Mock
	PublishSolutionServiceImpl impl = new PublishSolutionServiceImpl();

	@Test
	public void publishSolution(){
		try{			

			String solutionId = "025884aa-3364-40ee-a343-4bcf3e89f48d"; 
			String accessType = "PR";
			String userId = "1810f833-8698-4233-add4-091e34b8703c";
			String revisionId = "1810f833-8698-4233-add4-091e34b8703c";
			UUID trackingId = UUID.randomUUID();
			
			boolean flag = true;
			Mockito.when(impl.publishSolution(solutionId, accessType, userId, revisionId, trackingId)).thenReturn("Solution Published Successfully");
			Assert.assertTrue(flag);
			logger.info("Successfully publishSolution");
		} catch (Exception e) {
			logger.info("Exception occured while publishSolution: " + e);			 
		}
	}

	@Test
	public void unpublishSolution(){
		try{
			String solutionId = "025884aa-3364-40ee-a343-4bcf3e89f48d"; 
			String accessType = "PR";
			String userId = "1810f833-8698-4233-add4-091e34b8703c";
			boolean flag = true;
			Mockito.when(impl.unpublishSolution(solutionId, accessType, userId)).thenReturn(flag);
			Assert.assertTrue(flag);
			logger.info("Successfully unpublishSolution");
		} catch (Exception e) {
			logger.info("Exception occured while unpublishSolution: " + e);			 
		}
	}
}