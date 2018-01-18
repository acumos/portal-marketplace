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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.acumos.portal.be.transport.MailData;
import org.junit.Assert;
import org.junit.Test;

public class MailDataTest {  

	@Test
	public void testMailDataParameter(){
		String subject = "Sample data";
	    List<String> to = new ArrayList<String>();
	    to.add("xyz@techm.com");
	    to.add("hhh@techm.com");
	    String from = "abc@techm.com";
	    String template = "sfsfsff";
	    //Map<String, Object> model = new HashMap<String, Object>();
	    
		MailData mailData = new MailData();
		mailData.setSubject(subject);
		mailData.setFrom(from);		
		mailData.setTo(to);
		mailData.setTemplate(template);	
//		mailData.setModel(model);
		
		Assert.assertEquals(subject, mailData.getSubject());
		Assert.assertEquals(from, mailData.getFrom());
		Assert.assertEquals(to, mailData.getTo());
		Assert.assertEquals(template, mailData.getTemplate());
		//Assert.assertNotNull(mailData.getModel());
		
		Assert.assertNotNull(mailData.toString());
	}

}
