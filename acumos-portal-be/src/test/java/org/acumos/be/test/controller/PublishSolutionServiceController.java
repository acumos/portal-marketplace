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

import java.util.List;

import org.acumos.portal.be.transport.MLRole;
import org.acumos.portal.be.transport.MLSolution;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@RunWith(MockitoJUnitRunner.class)
public class PublishSolutionServiceController {


	@Autowired
	private MockMvc mockMvc;
	
	@Mock
	private RestTemplate restTemplate;
	
	
	@Test
	public void testPublishSolutionServiceController(){
		

		MLSolution mlsolution = new MLSolution();
		mlsolution.setSolutionId("Solution1");
		mlsolution.setName("Test_Solution data");
		mlsolution.setDescription("Test data");
		mlsolution.setOwnerId("41058105-67f4-4461-a192-f4cb7fdafd34");
		mlsolution.setAccessType("PB");
		mlsolution.setActive(true);
		mlsolution.setModelType("CL");
		mlsolution.setTookitType("DS");
		
		
		HttpStatus status = HttpStatus.OK;
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
		
		
		ResponseEntity<MLSolution> myEntity = new ResponseEntity<MLSolution>(mlsolution, headers, status);
		Mockito.when(restTemplate.exchange(Matchers.eq("/publish/"+mlsolution.getSolutionId()), Matchers.eq(HttpMethod.PUT),
				Matchers.<HttpEntity<MLSolution>> any(), Matchers.<ParameterizedTypeReference<MLSolution>> any()))
				.thenReturn(myEntity);

		System.out.println("Publishes a given SolutionId for userId with selected visibility" + myEntity.getBody());
		
		
		ResponseEntity<MLSolution> myEntity1 = new ResponseEntity<MLSolution>(mlsolution, headers, status);
		Mockito.when(restTemplate.exchange(Matchers.eq("/publish/"+mlsolution.getSolutionId()), Matchers.eq(HttpMethod.PUT),
				Matchers.<HttpEntity<MLSolution>> any(), Matchers.<ParameterizedTypeReference<MLSolution>> any()))
				.thenReturn(myEntity1);

		System.out.println("Unpublishes a given SolutionId for userId with selected visibility" + myEntity1.getBody());
		
		
	}
	
	
}
