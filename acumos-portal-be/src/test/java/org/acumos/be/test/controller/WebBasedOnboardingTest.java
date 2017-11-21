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
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acumos.portal.be.common.JsonRequest;
import org.acumos.portal.be.common.JsonResponse;
import org.acumos.portal.be.common.RestPageResponseBE;
import org.acumos.portal.be.controller.WebBasedOnboardingController;
import org.acumos.portal.be.transport.MLSolution;
import org.acumos.portal.be.transport.UploadSolution;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;


@RunWith(MockitoJUnitRunner.class)
public class WebBasedOnboardingTest {
	
	
	private static Logger logger = LoggerFactory.getLogger(WebBasedOnboardingTest.class);

	final HttpServletResponse response = new MockHttpServletResponse();
	final HttpServletRequest request = new MockHttpServletRequest();
	
	@Mock
	WebBasedOnboardingController webBasedController = new WebBasedOnboardingController();

	@Test
	public void testAddToCatalog() {

		try {

			MLSolution mlSolution = new MLSolution();
			mlSolution.setTookitType("CP");
			mlSolution.setTookitTypeName("Composite Solution");
			mlSolution.setSolutionId("6e5036e0-6e20-4425-bd9d-b4ce55cfd8a4");
			mlSolution.setDescription("Solution description");
			mlSolution.setAccessType("OR");
			mlSolution.setActive(true);
			Date date = new Date();
			mlSolution.setCreated(date);
			mlSolution.setDownloadCount(23);
			mlSolution.setLoginName("testerT1");
			mlSolution.setModelType("CL");
			mlSolution.setName("Solution name");
			mlSolution.setOwnerId("601f8aa5-5978-44e2-996e-2dbfc321ee73");
			mlSolution.setRatingCount((int) Math.round(3.2));

			UploadSolution uploadSolution = new UploadSolution();
			uploadSolution.setName(mlSolution.getName());
			uploadSolution.setVersion("1.0.0v");

			JsonResponse<RestPageResponseBE<MLSolution>> data = new JsonResponse<>();
			List<MLSolution> content = new ArrayList<MLSolution>();
			content.add(mlSolution);
			RestPageResponseBE<MLSolution> responseBody = new RestPageResponseBE<MLSolution>(content );
			responseBody.getContent();
			data.setResponseBody(responseBody);
			JsonRequest<UploadSolution> restPageReq = new JsonRequest<UploadSolution>();
			restPageReq.setBody(uploadSolution);
			restPageReq.getBody();
			String userId = "601f8aa5-5978-44e2-996e-2dbfc321ee73";
			JsonResponse<RestPageResponseBE<MLSolution>> value = new JsonResponse<RestPageResponseBE<MLSolution>>();
			value.setResponseBody(responseBody);
			Mockito.when(webBasedController.addToCatalog(null, null, restPageReq, userId)).thenReturn(value);
			logger.equals(value);
			logger.info("successfully added the toolkit to catalog ");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error while adding to catalog ", e);
			
		}

	}

}
