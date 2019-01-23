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
package org.acumos.be.test.transport;

import java.time.Instant;

import org.acumos.portal.be.transport.MLNotification;
import org.junit.Assert;
import org.junit.Test;

public class MLNotificationTest {

	@Test	
	public void testMLModelValidationStatusParameter(){
		
		String notificationId = "32f";
	    String title = "sample";
	    String message = "hello";
	    String url = "http://test.com";
	    Instant start = Instant.now();
	    Instant end = Instant.now();
	    int count = 10;
	    
	    MLNotification mlNotification = new  MLNotification();
	    mlNotification.setCount(count);
	    mlNotification.setEnd(end);
	    mlNotification.setMessage(message);
	    mlNotification.setNotificationId(notificationId);
	    mlNotification.setStart(start);
	    mlNotification.setTitle(title);
	    mlNotification.setUrl(url);
		
		Assert.assertEquals(count, mlNotification.getCount());
		Assert.assertEquals(end, mlNotification.getEnd());
		Assert.assertEquals(message, mlNotification.getMessage());
		Assert.assertEquals(notificationId, mlNotification.getNotificationId());
		Assert.assertEquals(start, mlNotification.getStart());
		Assert.assertEquals(title, mlNotification.getTitle());
		Assert.assertEquals(url, mlNotification.getUrl());
		
	}
}
