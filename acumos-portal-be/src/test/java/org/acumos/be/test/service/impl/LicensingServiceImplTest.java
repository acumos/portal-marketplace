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

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acumos.cds.domain.MLPLicenseProfileTemplate;
import org.acumos.cds.transport.RestPageResponse;
import org.acumos.licensemanager.profilevalidator.exceptions.LicenseProfileException;
import org.acumos.portal.be.common.exception.AcumosServiceException;
import org.acumos.portal.be.service.impl.LicensingServiceImpl;
import org.apache.http.HttpStatus;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

@RunWith(MockitoJUnitRunner.class)
public class LicensingServiceImplTest {

	@Rule
	public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().port(8000));
	
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	@InjectMocks
	LicensingServiceImpl impl;
	
	@Mock
	Environment env;
	
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	final HttpServletResponse response = new MockHttpServletResponse();
	final HttpServletRequest request = new MockHttpServletRequest();
	private final String url = "http://localhost:8000/ccds";
	private final String user = "ccds_client";
	private final String pass = "ccds_client";
	private final String LICENSE_TEMPLATE_URL = "/ccds/lic/templ?page=0&size=0&sort=priority,DESC";
	private final String GET_LICENSE_TEMPLATE_URL ="/ccds/lic/templ/";
	
	@Test
	public void getTemplates() throws LicenseProfileException, AcumosServiceException {
		
		MLPLicenseProfileTemplate licenseProfileTemplate=new MLPLicenseProfileTemplate();
		List<MLPLicenseProfileTemplate> licenseProfileTemplateList=new ArrayList<>();
		licenseProfileTemplate.setTemplate("My Licence");
		licenseProfileTemplate.setTemplateName("My Sample Test template");
		licenseProfileTemplate.setTemplateId(101L);
		licenseProfileTemplateList.add(licenseProfileTemplate);
	        PageRequest pageRequest = PageRequest.of(0, 3);
		int totalElements = 15;
		RestPageResponse<MLPLicenseProfileTemplate> restResponse = new RestPageResponse<>(licenseProfileTemplateList, pageRequest, totalElements);
		ObjectMapper Obj = new ObjectMapper();
		String jsonStr=null;
		try { 
			jsonStr = Obj.writeValueAsString(restResponse); 
		} 
		catch (IOException e) { 
			logger.error("Exception occurred while parsing rest page response to string ",e.getMessage());
		} 
		stubFor(get(urlEqualTo(LICENSE_TEMPLATE_URL)).willReturn(
                aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withBody(jsonStr)));
		when(env.getProperty("cdms.client.url")).thenReturn(url);
		when(env.getProperty("cdms.client.username")).thenReturn(user);
		when(env.getProperty("cdms.client.password")).thenReturn(pass);
		List<MLPLicenseProfileTemplate> licenseProfileTemplateListSuccess=impl.getTemplates();
		assertNotNull(licenseProfileTemplateListSuccess);
		assertEquals(licenseProfileTemplateList, licenseProfileTemplateListSuccess);
}
	
	@Test
	public void getTemplate() throws LicenseProfileException, AcumosServiceException {
		long templateId=101;
		MLPLicenseProfileTemplate licenseProfileTemplate=new MLPLicenseProfileTemplate();
		licenseProfileTemplate.setTemplate("My Licence");
		licenseProfileTemplate.setTemplateName("My Sample Test template");
		licenseProfileTemplate.setTemplateId(101L);
		ObjectMapper Obj = new ObjectMapper();
		String jsonStr=null;
		try { 
			jsonStr = Obj.writeValueAsString(licenseProfileTemplate); 
		} 
		catch (IOException e) { 
			logger.error("Exception occurred while parsing rest page response to string ",e.getMessage());
		} 
		stubFor(get(urlEqualTo(GET_LICENSE_TEMPLATE_URL+templateId)).willReturn(
                aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withBody(jsonStr)));

		when(env.getProperty("cdms.client.url")).thenReturn(url);
		when(env.getProperty("cdms.client.username")).thenReturn(user);
		when(env.getProperty("cdms.client.password")).thenReturn(pass);
		MLPLicenseProfileTemplate licenseProfileTemplateSuccess=impl.getTemplate(templateId);
		assertNotNull(licenseProfileTemplateSuccess);
		assertEquals(licenseProfileTemplate, licenseProfileTemplateSuccess);
	}
	
	@Test
	public void validateTest() throws LicenseProfileException, AcumosServiceException {
		
		String jsonInput="{\"$schema\": \"https://raw.githubusercontent.com/acumos/license-manager/master/license-manager-client-library/src/main/resources/schema/1.0.0/license-profile.json\",\"keyword\": \"Vendor-A-OSS\",\"licenseName\": \"Vendor A Open Source Software License\"," +
				 "\"copyright\": {\"year\":2019,\"company\":\"VendorA\",\"suffix\":\"AllRights Reserved\"},\"softwareType\": " +
				 "\"Machine Learning Model\",\"companyName\": \"Vendor A\",\"contact\": {\"name\": \"Vendor A Team\",\"URL\": " +
				 "\"Vendor-A.com\",\"email\": \"support@Vendor-A.com\"},\"additionalInfo\": \"Vendor-A.com\"}";
		when(env.getProperty("cdms.client.url")).thenReturn(url);
		when(env.getProperty("cdms.client.username")).thenReturn(user);
		when(env.getProperty("cdms.client.password")).thenReturn(pass);
		String validateResult=impl.validate(jsonInput);
		assertNotNull(validateResult);
	//	assertEquals(validateResult, "SUCCESS");
}

}
