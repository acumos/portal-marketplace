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

import java.util.HashMap;
import java.util.Map;

import org.acumos.cds.domain.MLPUser;
import org.acumos.portal.be.common.ConfigConstants;
import org.acumos.portal.be.common.exception.AcumosServiceException;
import org.acumos.portal.be.service.NotificationService;
import org.acumos.portal.be.transport.NotificationRequestObject;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = org.acumos.portal.be.Application.class,
webEnvironment = WebEnvironment.RANDOM_PORT,
properties = {
		ConfigConstants.portal_feature_email + "=true",
		ConfigConstants.portal_feature_email_from +"=support@acumos.org",
		ConfigConstants.portal_feature_email_service + "=smtp",
		ConfigConstants.spring_mail_host + "=localhost",
		ConfigConstants.spring_mail_port + "=10000",
		ConfigConstants.spring_mail_username + "=Test@test.com",
		ConfigConstants.spring_mail_password + "=Test",
		ConfigConstants.spring_mail_smtp_starttls_enable + "=true",
		ConfigConstants.spring_mail_smtp_auth + "=false",
		ConfigConstants.spring_mail_debug + "=true",
		ConfigConstants.spring_mail_transport_protocol + "=smtp",
		ConfigConstants.spring_mail_template_folder_path + "=/fmtemplates/",
		ConfigConstants.cdms_client_url + "=http://localhost:8000/ccds",
		ConfigConstants.cdms_client_username + "=ccds_test",
		ConfigConstants.cdms_client_password + "=ccds_test"
})


public class MailServiceImplTest {
	
	@Autowired
	private NotificationService notificationService;

	private GreenMail smtpServer;

	@Rule
	public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().port(8000));
	
	
    @Before
    public void setUp() throws Exception {
        smtpServer = new GreenMail(new ServerSetup(10000, null, "smtp"));
        smtpServer.start();
    }
 
    @After
    public void tearDown() throws Exception {
        smtpServer.stop();
    }
    
    @Test
    public void sendmail() {
    	
    	MLPUser user = new MLPUser();
    	user.setActive(true);
    	user.setEmail("Test@acumos.com");
    	user.setFirstName("Test");
    	user.setLastName("User");
    	user.setUserId("1234");

        stubFor(get(urlEqualTo("/ccds/user/1234"))
        		.willReturn(aResponse()
        		.withStatus(HttpStatus.SC_OK)
        		.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())
        		.withBody("{" + 
        				"  \"created\": 1517261961000," + 
        				"  \"modified\": 1517261961000," + 
        				"  \"userId\": \"1234\"," + 
        				"  \"firstName\": \"Test\"," + 
        				"  \"middleName\": null," + 
        				"  \"lastName\": \"User\"," + 
        				"  \"orgName\": null," + 
        				"  \"email\": \"Test@acumos.com\"," + 
        				"  \"loginName\": \"Test\"," + 
        				"  \"loginHash\": null," + 
        				"  \"loginPassExpire\": null," + 
        				"  \"authToken\": \"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJmYXJoZWVuIiwicm9sZSI6W3sicGVybWlzc2lvbkxpc3QiOm51bGwsInJvbGVDb3VudCI6MCwicm9sZUlkIjoiMTIzNDU2NzgtYWJjZC05MGFiLWNkZWYtMTIzNDU2Nzg5MGFiIiwibmFtZSI6Ik1MUCBTeXN0ZW0gVXNlciIsImFjdGl2ZSI6ZmFsc2UsImNyZWF0ZWQiOjE1MTI1OTY3OTEwMDAsIm1vZGlmaWVkIjpudWxsfV0sImNyZWF0ZWQiOjE1MTUwODgyMTMzNzAsImV4cCI6MTUxNTY5MzAxMywibWxwdXNlciI6eyJjcmVhdGVkIjoxNTE1MDg2MTYwMDAwLCJtb2RpZmllZCI6MTUxNTA4NjE2MDAwMCwidXNlcklkIjoiMDA5ZTlmZDctNTI3ZS00YWE0LTg2ZGEtM2ExNzI5ZTJhZmVlIiwiZmlyc3ROYW1lIjoiRmFyaGVlbiIsIm1pZGRsZU5hbWUiOm51bGwsImxhc3ROYW1lIjoiQ2VmYWx1Iiwib3JnTmFtZSI6bnVsbCwiZW1haWwiOiJmbWFzb29kQHJlc2VhcmNoLmF0dC5jb20iLCJsb2dpbk5hbWUiOiJmYXJoZWVuIiwibG9naW5IYXNoIjpudWxsLCJsb2dpblBhc3NFeHBpcmUiOm51bGwsImF1dGhUb2tlbiI6bnVsbCwiYWN0aXZlIjp0cnVlLCJsYXN0TG9naW4iOm51bGwsInBpY3R1cmUiOm51bGx9fQ.ed_yyLs69wrMejGQ65EFOKUYSTn3GklVV_m8PPbQ--B268DBhm-RxyP6deZdpu_U-Jtx_IQnTGk1urgF1y5zPw\"," + 
        				"  \"active\": true," + 
        				"  \"lastLogin\": null" + 
        				"}")));
        
        
        stubFor(get(urlEqualTo("/ccds/notif/notifpref/user/1234"))
        		.willReturn(aResponse()
        		.withStatus(HttpStatus.SC_OK)
        		.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())
        		.withBody("[{" + 
        				"    \"msgSeverityCode\": \"HI\"," + 
        				"    \"notfDelvMechCode\": \"EM\"," + 
        				"    \"userId\": \"1234\"," + 
        				"    \"userNotifPrefId\": 0" + 
        				"  }]")));
        
        
        NotificationRequestObject mailRequest = new NotificationRequestObject();
        mailRequest.setMessageType("ONBD_SUCCESS");
        mailRequest.setSeverity("HI");
        mailRequest.setSubject("New Solution Onboaded");
        mailRequest.setUserId("1234");
        Map<String, String> mailBody = new HashMap<String, String>();
        mailBody.put("solutionName", "Test Solution 1");
        mailRequest.setNotificationData(mailBody);
        try {
			notificationService.sendUserNotification(mailRequest);
		} catch (AcumosServiceException e) {
			Assert.assertFalse("Exception while sending the email Notification", true);
		}
    }
	
	
}