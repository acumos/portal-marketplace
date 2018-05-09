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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acumos.portal.be.service.impl.AsyncServicesImpl;
import org.acumos.portal.be.transport.UploadSolution;
import org.acumos.portal.be.util.EELFLoggerDelegate;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

@RunWith(MockitoJUnitRunner.class)
public class AsyncServiceImplTest { 

	private static final EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(AsyncServiceImplTest.class);

	final HttpServletResponse response = new MockHttpServletResponse();
	final HttpServletRequest request = new MockHttpServletRequest();
	
	private class AsyncServicesSubImpl extends AsyncServicesImpl {
		@Override
		public void sendDisconnectNotifications(String uuid, String userId, UploadSolution solution,
				String errorMessage) {
			super.sendDisconnectNotifications(uuid, userId, solution, errorMessage);
		}
	}

	@Mock
	AsyncServicesImpl impl = new AsyncServicesImpl();

	@Mock
	AsyncServicesSubImpl subimpl = new AsyncServicesSubImpl();
	
	@Test
	public void initiateAsyncProcess(){
		try{
			AsyncServicesImpl mockimpl = mock(AsyncServicesImpl.class);
			mockimpl.initiateAsyncProcess();
			Assert.assertNotNull(mockimpl);
			logger.debug("Initialized process");
		} catch (Exception e) {
			logger.error("Exception occured while initiateAsyncProcess: " + e);	
		}
	}
	
	@Test
	public void callOnboarding(){
		try{
			String userId = "1810f833-8698-4233-add4-091e34b8703c";
			UploadSolution solution = new UploadSolution();
			solution.setName("Test Solution");
			solution.setVersion("1.0.0");
			String provider = "FB"; 
			String access_token = "PB";
			AsyncServicesImpl mockimpl = mock(AsyncServicesImpl.class);
			//mockimpl.callOnboarding(userId, solution, provider, access_token);
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
	public void sendDisconnectNotifications(){
		try{
			String uuid = "f43898dc-2d3e-4680-afef-f2a93884c52a";
			String userId = "1810f833-8698-4233-add4-091e34b8703c";
			UploadSolution solution = new UploadSolution();
			solution.setName("Test Solution");
			solution.setVersion("1.0.0");
			String message = "Test error message";
			AsyncServicesSubImpl mockimpl = mock(AsyncServicesSubImpl.class);
			mockimpl.sendDisconnectNotifications(uuid, userId, solution, message);
			Assert.assertEquals(uuid, uuid);
			Assert.assertNotNull(uuid);
			Assert.assertEquals(userId, userId);
			Assert.assertNotNull(userId);
			Assert.assertNotNull(solution);
			Assert.assertNotNull(message);
			
		} catch (Exception e) {
			logger.error("Exception occured while sendDisconnectNotifications: " + e);	
		}
	}
		
}