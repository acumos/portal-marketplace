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

import org.acumos.portal.be.service.impl.FileSystemStorageService;
import org.acumos.portal.be.util.EELFLoggerDelegate;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.multipart.MultipartFile;
import org.junit.Assert;

import static org.mockito.Mockito.*;

public class FileSystemStorageserviceImplTest {

	private static final EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(FileSystemStorageserviceImplTest.class);

	final HttpServletResponse response = new MockHttpServletResponse();
	final HttpServletRequest request = new MockHttpServletRequest();
	
	@Mock
	FileSystemStorageService impl = new FileSystemStorageService();
	
	@Test
	public void store(){
		MultipartFile file = null ; 
		String userId = "1810f833-8698-4233-add4-091e34b8703c";
		try{
			FileSystemStorageService mockimpl = mock(FileSystemStorageService.class);
			mockimpl.store(file, userId, true);
			Assert.assertEquals(mockimpl, mockimpl);
		} catch (Exception e) {
			logger.error("Exception occured while store: " + e);	
		}
	}
	
	@Test
	public void deleteAll(){
		String userId = "1810f833-8698-4233-add4-091e34b8703c";
		try{
			
			FileSystemStorageService mockimpl = mock(FileSystemStorageService.class);
			mockimpl.deleteAll(userId);
			Assert.assertEquals(mockimpl, mockimpl);
		} catch (Exception e) {
			logger.error("Exception occured while deleteAll: " + e);	
		}
	}
}
