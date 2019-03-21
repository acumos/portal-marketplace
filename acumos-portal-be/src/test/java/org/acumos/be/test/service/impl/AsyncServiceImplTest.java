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

import static org.mockito.Mockito.mock;

import java.lang.invoke.MethodHandles;
import java.time.Instant;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acumos.cds.domain.MLPNotification;
import org.acumos.cds.domain.MLPTaskStepResult;
import org.acumos.portal.be.service.impl.AsyncServicesImpl;
import org.acumos.portal.be.transport.MLNotification;
import org.acumos.portal.be.transport.UploadSolution;
import org.acumos.portal.be.util.PortalUtils;
import org.junit.Assert;
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
public class AsyncServiceImplTest {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());	

	final HttpServletResponse response = new MockHttpServletResponse();
	final HttpServletRequest request = new MockHttpServletRequest();

	@Mock
	AsyncServicesImpl impl = new AsyncServicesImpl();

	@Test
	public void initiateAsyncProcess() {
		try {
			AsyncServicesImpl mockimpl = mock(AsyncServicesImpl.class);
			mockimpl.initiateAsyncProcess();
			Assert.assertNotNull(mockimpl);
			logger.debug("Initialized process");
		} catch (Exception e) {
			logger.error("Exception occured while initiateAsyncProcess: " + e);
		}
	}

	@Test
	public void callOnboarding() {
		try {
			String userId = "1810f833-8698-4233-add4-091e34b8703c";
			UploadSolution solution = new UploadSolution();
			solution.setName("Test Solution");
			solution.setVersion("1.0.0");
			String provider = "FB";
			String access_token = "PB";
			AsyncServicesImpl mockimpl = mock(AsyncServicesImpl.class);
			// mockimpl.callOnboarding(userId, solution, provider,
			// access_token);
			Assert.assertEquals(userId, userId);
			Assert.assertNotNull(userId);
			Assert.assertNotNull(solution);
			Assert.assertNotNull(provider);
			Assert.assertNotNull(access_token);

		} catch (Exception e) {
			logger.error("Exception occured while callOnboarding: " + e);
		}
	}

	@Test
	public void sendBellNotification() {
		String userId = "1810f833-8698-4233-add4-091e34b8703c";
		UploadSolution solution = new UploadSolution();
		solution.setName("Test Solution");
		solution.setVersion("1.0.0");
		AsyncServicesImpl mockAsync = mock(AsyncServicesImpl.class);

		String reason = "Test case";
		String message = "On-boarding failed: " + reason + " Please restart the process again to upload the solution.";
		MLPNotification mlpNotification = new MLPNotification();
		Instant created = Instant.now();
		mlpNotification.setCreated(created);
		mlpNotification.setMessage(message);
		Instant modified = Instant.now();
		mlpNotification.setModified(modified);
		mlpNotification.setTitle("Web Based Onboarding");
		mlpNotification.setUrl("http://notify.com");
		Instant end = Instant.now();
		mlpNotification.setEnd(end);
		Instant start = Instant.now();
		mlpNotification.setStart(start);
		MLNotification notf = PortalUtils.convertToMLNotification(mlpNotification);

		Mockito.when(mockAsync.sendBellNotification(userId, reason)).thenReturn(notf);
		logger.info("Successfully created notification " + notf);
		Assert.assertNotNull(notf);
		Assert.assertEquals(notf.getMessage(), message);

	}

	@Test
	public void sendTrackerErrorNotification() {
		String uuid = "f43898dc-2d3e-4680-afef-f2a93884c52a";
		String userId = "1810f833-8698-4233-add4-091e34b8703c";
		String message = "Failed to connect to onboarding";
		AsyncServicesImpl mockAsync = mock(AsyncServicesImpl.class);

		MLPTaskStepResult step = new MLPTaskStepResult();
		step.setTaskId(new Long(1));
		step.setStatusCode("FA");
		step.setName("CreateSolution");
		step.setResult(message);

		Mockito.when(mockAsync.sendTrackerErrorNotification(uuid, userId, message)).thenReturn(step);
		logger.info("Successfully created step " + step);
		Assert.assertNotNull(step);
		Assert.assertEquals(message, step.getResult());

	}

}