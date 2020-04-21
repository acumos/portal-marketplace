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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acumos.cds.domain.MLPLicenseProfileTemplate;
import org.acumos.licensemanager.profilevalidator.exceptions.LicenseProfileException;
import org.acumos.portal.be.common.CredentialsService;
import org.acumos.portal.be.common.JsonRequest;
import org.acumos.portal.be.common.JsonResponse;
import org.acumos.portal.be.common.exception.AcumosServiceException;
import org.acumos.portal.be.controller.LicensingServiceController;
import org.acumos.portal.be.service.LicensingService;
import org.acumos.portal.be.service.MarketPlaceCatalogService;
import org.acumos.portal.be.service.PushAndPullSolutionService;
import org.acumos.portal.be.util.PortalConstants;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.env.Environment;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@RunWith(MockitoJUnitRunner.class)
public class LicensingServiceControllerTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	@InjectMocks
	LicensingServiceController licensingServiceController;
	@Mock
	LicensingService licensingService;
	@Mock
	MarketPlaceCatalogService service;
	@Mock
	PushAndPullSolutionService pushAndPullSolutionService;
	@Mock
	CredentialsService credentialService;
	@Mock
	Environment env;
	
	final HttpServletResponse response = new MockHttpServletResponse();
	final HttpServletRequest request = new MockHttpServletRequest();
	
	String JsonFileData="{ \"keyword\": \"Vendor-A-OSS\",\"licenseName\": \"Vendor A Open Source Software License\"," + 
			"  \"copyright\": {\"company\": \"Vendor A\"},\"softwareType\": \"Machine Learning Model\"," + 
			"  \"companyName\": \"Vendor A\",\"contact\": {\"email\": \"support@Vendor-A.com\"}," + 
			"  \"additionalInfo\": \"http://Vendor-A.com/licenses/Vendor-A-OSS\"}";
	String userId = "8cbeccd0-ed84-42c3-8d9a-06d5629dc7bb";
	String solutionId = "4cbf491b-c687-459f-9d81-e150d1a0b972";
	String revisionId = "2grtccd0-ed84-42c3-8d9a-06d5629dc7bb";
	String versionId = "41058105-67f4-4461-a192-f4cb7fdafd34";
 
	@Test
	public void uploadLicense() throws LicenseProfileException, AcumosServiceException, IOException {	
		MultipartFile file = new MockMultipartFile(PortalConstants.LICENSE_FILENAME,PortalConstants.LICENSE_FILENAME,"", JsonFileData.getBytes());
			Assert.assertNotNull(userId);
			Assert.assertNotNull(solutionId);
			Assert.assertNotNull(revisionId);
			Assert.assertNotNull(versionId);
			when(credentialService.getLoggedInUserId()).thenReturn(userId);
			when(licensingService.validate(JsonFileData)).thenReturn("SUCCESS");
			when(pushAndPullSolutionService.uploadLicense(file, userId, solutionId, revisionId, versionId)).thenReturn(false);
			
			licensingServiceController.uploadLicense(file, userId, solutionId, revisionId, versionId, request, response);			
			service.getLicenseUrl(solutionId, versionId, PortalConstants.LICENSE_ARTIFACT_TYPE, PortalConstants.LICENSE_FILENAME_PREFIX);
			
	}
	
	@Test
	public void getTemplates() throws LicenseProfileException, AcumosServiceException {
		MLPLicenseProfileTemplate licenseProfileTemplate=getLicenseTemplate();
		List<MLPLicenseProfileTemplate> licenseProfileTemplateList=new ArrayList<>();
		licenseProfileTemplateList.add(licenseProfileTemplate);
		when(licensingService.getTemplates()).thenReturn(licenseProfileTemplateList);
		JsonResponse<List<MLPLicenseProfileTemplate>> templateResponseSuccess=licensingServiceController.getTemplates(request, response);
		assertNotNull(templateResponseSuccess);
		assertEquals(licenseProfileTemplateList, templateResponseSuccess.getResponseBody());
		
		when(licensingService.getTemplates()).thenReturn(null);
		JsonResponse<List<MLPLicenseProfileTemplate>> templateResponseFail=licensingServiceController.getTemplates(request, response);
		assertNull(templateResponseFail.getResponseBody());
		when(licensingService.getTemplates()).thenThrow(LicenseProfileException.class);
		licensingServiceController.getTemplates(request, response);
		
	}
	
	@Test
	public void getTemplate() throws LicenseProfileException, AcumosServiceException {
		MLPLicenseProfileTemplate licenseProfileTemplate=getLicenseTemplate();
		when(licensingService.getTemplate(licenseProfileTemplate.getTemplateId())).thenReturn(licenseProfileTemplate);
		JsonResponse<MLPLicenseProfileTemplate> templateResponseSuccess=licensingServiceController.getTemplate(request,licenseProfileTemplate.getTemplateId(), response);
		assertNotNull(templateResponseSuccess);
		assertEquals(licenseProfileTemplate, templateResponseSuccess.getResponseBody());
		
		when(licensingService.getTemplate(licenseProfileTemplate.getTemplateId())).thenReturn(null);
		JsonResponse<MLPLicenseProfileTemplate> templateResponseFail=licensingServiceController.getTemplate(request,licenseProfileTemplate.getTemplateId(), response);
		assertNull(templateResponseFail.getResponseBody());
		
		when(licensingService.getTemplate(licenseProfileTemplate.getTemplateId())).thenThrow(LicenseProfileException.class);
		licensingServiceController.getTemplate(request,licenseProfileTemplate.getTemplateId(), response);
		
	}
	
	@Test
	public void createTemplate() {
		MLPLicenseProfileTemplate licenseProfileTemplate=getLicenseTemplate();
		JsonRequest<MLPLicenseProfileTemplate> jsonRequest=new JsonRequest<>();
		jsonRequest.setBody(licenseProfileTemplate);
		when(licensingService.createLicenseProfileTemplate(Mockito.any())).thenReturn(licenseProfileTemplate);
		JsonResponse<MLPLicenseProfileTemplate> templateResponseSuccess=licensingServiceController.createTemplate(request,jsonRequest , response);
		assertNotNull(templateResponseSuccess);
		when(licensingService.createLicenseProfileTemplate(Mockito.any())).thenReturn(null);
		JsonResponse<MLPLicenseProfileTemplate> templateResponseFail=licensingServiceController.createTemplate(request,jsonRequest , response);
		assertNull(templateResponseFail.getResponseBody());
		when(licensingService.createLicenseProfileTemplate(Mockito.any())).thenThrow(Exception.class);
		licensingServiceController.createTemplate(request, jsonRequest, response);
	}
	
	@Test
	public void updateTemplate() {
		MLPLicenseProfileTemplate licenseProfileTemplate=getLicenseTemplate();
		JsonRequest<MLPLicenseProfileTemplate> jsonRequest=new JsonRequest<>();
		jsonRequest.setBody(licenseProfileTemplate);
		licensingServiceController.updateTemplate(request,jsonRequest , response);
	}
	@Test
	public void createJsonFile() throws IOException {
		
		licensingServiceController.createJsonFile(JsonFileData, userId, solutionId, revisionId, versionId, request, response);
	}
	@Test
	public void getDocurl() {
		when(env.getProperty("license_profile.url", "")).thenReturn("http://localhost.com");
		licensingServiceController.getDocurl(request, response);
	}
	private MLPLicenseProfileTemplate getLicenseTemplate() {
		MLPLicenseProfileTemplate licenseProfileTemplate=new MLPLicenseProfileTemplate();
		long tempalteId=101L;
		licenseProfileTemplate.setTemplate("My Licence");
		licenseProfileTemplate.setTemplateName("My Sample Test template");
		licenseProfileTemplate.setTemplateId(tempalteId);
		return licenseProfileTemplate;
	}
}
