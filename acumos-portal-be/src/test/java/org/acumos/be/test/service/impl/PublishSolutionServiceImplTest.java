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

import org.acumos.cds.client.ICommonDataServiceRestClient;
import org.acumos.portal.be.service.impl.PublishSolutionServiceImpl;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

public class PublishSolutionServiceImplTest {

	private static Logger logger = LoggerFactory.getLogger(PublishSolutionServiceImplTest.class);
 
	@Mock
	Environment env;
 
	@Mock
	AdminServiceImplTest test;
	
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	 
	private final String url = "http://localhost:8002/ccds/";
	private final String user = "ccds_client";
	private final String pass = "ccds_client";

	private ICommonDataServiceRestClient cmnDataService;
	
	@Test
	public void publishSolution(){
		try{			
		 
		String solutionId = "025884aa-3364-40ee-a343-4bcf3e89f48d"; 
		String accessType = "PR";
		String userId = "1810f833-8698-4233-add4-091e34b8703c";
		
		when(env.getProperty("cdms.client.url")).thenReturn("http://localhost:8002/ccds/");
		when(env.getProperty("cdms.client.username")).thenReturn("ccds_client");
		when(env.getProperty("cdms.client.password")).thenReturn("ccds_client");
		PublishSolutionServiceImpl impl = new PublishSolutionServiceImpl();
		impl.setEnvironment(env);
		impl.publishSolution(solutionId, accessType, userId);
		
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
			
			when(env.getProperty("cdms.client.url")).thenReturn("http://localhost:8002/ccds/");
			when(env.getProperty("cdms.client.username")).thenReturn("ccds_client");
			when(env.getProperty("cdms.client.password")).thenReturn("ccds_client");
			PublishSolutionServiceImpl impl = new PublishSolutionServiceImpl();
			impl.setEnvironment(env);
			impl.unpublishSolution(solutionId, accessType, userId);
		} catch (Exception e) {
			logger.info("Exception occured while unpublishSolution: " + e);			 
		}
	}
}
