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
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acumos.cds.client.ICommonDataServiceRestClient;
import org.acumos.cds.domain.MLPCatalog;
import org.acumos.cds.domain.MLPCodeNamePair;
import org.acumos.cds.domain.MLPDocument;
import org.acumos.cds.domain.MLPRole;
import org.acumos.cds.domain.MLPSolution;
import org.acumos.cds.domain.MLPSolutionRevision;
import org.acumos.cds.domain.MLPTag;
import org.acumos.cds.domain.MLPUser;
import org.acumos.cds.transport.RestPageRequest;
import org.acumos.cds.transport.RestPageResponse;
import org.acumos.portal.be.common.exception.AcumosServiceException;
import org.acumos.portal.be.service.impl.MarketPlaceCatalogServiceImpl;
import org.acumos.portal.be.service.impl.UserServiceImpl;
import org.acumos.portal.be.transport.MLSolution;
import org.acumos.portal.be.transport.MLSolutionWeb;
import org.acumos.portal.be.transport.RevisionDescription;
import org.acumos.portal.be.util.PortalUtils;
import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

@RunWith(MockitoJUnitRunner.class)
public class MarketPlaceCatalogServiceImplTest {

	@Rule
	public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().port(8000));

	@Mock
	ICommonDataServiceRestClient dataServiceRestClient;
	@Mock
	Environment env;
	@Mock 
	UserServiceImpl userService;
	@InjectMocks
	private MarketPlaceCatalogServiceImpl catalogService;
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	
	final HttpServletResponse response = new MockHttpServletResponse();
	final HttpServletRequest request = new MockHttpServletRequest();
	private final String url = "http://localhost:8000/ccds";
	private final String user = "ccds_client";
	private final String pass = "ccds_client";

	@Test
	public void addRevisionDescriptionTest() throws AcumosServiceException {
		String revisionId = "1234-1234-1234-1234-1234";
		String catalogId = "4321-4321-4321-4321-4321";
		String descriptionText = "Test";
		String ccdsRevCatDescrPath = String.format("/ccds/revision/%s/catalog/%s/descr", revisionId, catalogId);
		
		//Return null response to create a description
		stubFor(get(urlEqualTo(ccdsRevCatDescrPath)).willReturn(aResponse()
				.withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())));

		//Return null response to create a description
		stubFor(post(urlEqualTo(ccdsRevCatDescrPath))
				.willReturn(aResponse().withBody(
						"{\"created\":null,\"modified\":null,\"revisionId\":\"1234-1234-1234-1234-1234\",\"catalogId\":\"4321-4321-4321-4321-4321\",\"description\":\"Test\"}")
						.withStatus(HttpStatus.SC_OK)
						.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())));

		setCdsProperty();
		RevisionDescription createdDesc = null;

		RevisionDescription description = new RevisionDescription();
		description.setRevisionId(revisionId);
		description.setCatalogId(catalogId);
		description.setDescription(descriptionText);
    	createdDesc = catalogService.addUpdateRevisionDescription(revisionId, catalogId, description);
		
		Assert.assertNotNull(createdDesc);
		Assert.assertEquals(descriptionText, description.getDescription());
	}

	@Test
	public void updateRevisionDescriptionTest() throws AcumosServiceException {
		String revisionId = "1234-1234-1234-1234-1234";
		String catalogId = "4321-4321-4321-4321-4321";
		String descriptionText = "UpdatedTest";
		String ccdsRevCatDescrPath = String.format("/ccds/revision/%s/catalog/%s/descr", revisionId, catalogId);
		
		stubFor(get(urlEqualTo(ccdsRevCatDescrPath)).willReturn(aResponse()
				.withBody("{" + "  \"created\": 1534289584000," + "  \"modified\": 1534289584000,"
						+ "  \"revisionId\": \"dac8ef87-0e66-4406-8374-95c78799b07c\","
						+ "  \"catalogId\":\"4321-4321-4321-4321-4321\"," + "  \"description\": \"Test\"" + "}")
				.withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())));

		stubFor(put(urlEqualTo(ccdsRevCatDescrPath)).willReturn(aResponse()
				.withBody(
						"{\"created\":null,\"modified\":null,\"revisionId\":\"1234-1234-1234-1234-1234\",\"catalogId\":\"4321-4321-4321-4321-4321\",\"description\":\"UpdatedTest\"}")
				.withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())));

		RevisionDescription createdDesc = null;

		RevisionDescription description = new RevisionDescription();
		description.setRevisionId(revisionId);
		description.setCatalogId(catalogId);
		description.setDescription(descriptionText);
		setCdsProperty();
			createdDesc = catalogService.addUpdateRevisionDescription(revisionId, catalogId, description);
		Assert.assertNotNull(createdDesc);
	}
	
	@Test
	public void getAllPaginatedSolutionsTest() throws JsonProcessingException, AcumosServiceException {
		RestPageRequest restPageRequest = new RestPageRequest();
		restPageRequest.setSize(9);
		restPageRequest.setPage(1);
		MLPSolution oldSolution=PortalUtils.convertToMLPSolution(getMLSolution());
		ObjectMapper Obj = new ObjectMapper();
		setCdsProperty();
		List<MLPSolution> solutionList = new ArrayList<MLPSolution>();
		solutionList.add(oldSolution);
		RestPageResponse<MLPSolution> solutionsRes = new RestPageResponse<>(solutionList, PageRequest.of(0, 1), 1);
		String jsonStrRes = Obj.writeValueAsString(solutionsRes);
		Map<String,Object> solutoinNameParameter=new HashMap<>();
		stubFor(get(urlEqualTo("/ccds/solution?page=0&size=10&sort=sort,ASC")).willReturn(
                aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withBody(jsonStrRes)));
		RestPageResponse<MLPSolution> resPass=catalogService.getAllPaginatedSolutions(0, 10, "ASC");
		assertNotNull(resPass);
	}
	
	public void getAllPublishedSolutions(){
		MLSolution mlSolution=new MLSolution();
		mlSolution.setActive(true);
		mlSolution.setCommentId("commentId");
		mlSolution.setCompanyModelCount(10);
		List<MLSolution> list=new ArrayList<>();
		list.add(mlSolution);
		
	}
	@Test
	public void getMLPSolutionBySolutionNameTest() throws JsonProcessingException, AcumosServiceException {
		RestPageRequest restPageRequest = new RestPageRequest();
		restPageRequest.setSize(9);
		restPageRequest.setPage(1);
		MLPSolution oldSolution=PortalUtils.convertToMLPSolution(getMLSolution());
		ObjectMapper Obj = new ObjectMapper();
		setCdsProperty();
		List<MLPSolution> solutionList = new ArrayList<MLPSolution>();
		solutionList.add(oldSolution);
		RestPageResponse<MLPSolution> solutionsRes = new RestPageResponse<>(solutionList, PageRequest.of(0, 1), 1);
		String jsonStrRes = Obj.writeValueAsString(solutionsRes);
		Map<String,Object> solutoinNameParameter=new HashMap<>();
		stubFor(get(urlEqualTo("/ccds/solution/search?_j=a")).willReturn(
                aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withBody(jsonStrRes)));
		RestPageResponse<MLPSolution> resPass=catalogService.getMLPSolutionBySolutionName(solutoinNameParameter, false, restPageRequest);
		assertNotNull(resPass);
	}
	@Test
	public void getSolutionTest() throws JsonProcessingException, AcumosServiceException {
		MLPSolution mlpSolution=new MLPSolution();
		mlpSolution.setSolutionId("somesolid");
		mlpSolution.setName("somename");
		mlpSolution.setToolkitTypeCode("somecode");
		mlpSolution.setUserId("someuser");
		MLPSolutionRevision mlpSolutionRevision=new MLPSolutionRevision();
		mlpSolutionRevision.setPublisher("somepub");
		mlpSolutionRevision.setSolutionId("somesolid");
		mlpSolutionRevision.setRevisionId("somerevid");
		List<MLPCatalog> list=new ArrayList<>();
		MLPCatalog catalog=new MLPCatalog();
		catalog.setCatalogId("somecatid");
		catalog.setPublisher("somepub");
		list.add(catalog);
		List<MLPUser> mlpUsersList=new ArrayList<>();
		MLPUser user=new MLPUser();
		user.setUserId("someuserid");
		user.setFirstName("somename");
		user.setLastName("lastname");
		mlpUsersList.add(user);
		List<MLPRole> roleList=new ArrayList<>();
		MLPRole role=new MLPRole();
		role.setRoleId("someroleid");
		role.setName("PUBLISHER");
		role.setActive(true);
		roleList.add(role);
		List<MLPCodeNamePair> toolkitTypeList=new ArrayList<>();
		MLPCodeNamePair codeNamePair=new MLPCodeNamePair();
		codeNamePair.setCode("somecode");
		codeNamePair.setName("somename");
		toolkitTypeList.add(codeNamePair);
		List<MLPTag> tagList =new ArrayList<>();
		MLPTag tag=new MLPTag();
		tag.setTag("sometag");
		tagList.add(tag);
		ObjectMapper Obj = new ObjectMapper();
		String jsonStr = Obj.writeValueAsString(mlpSolution);
		String cat=Obj.writeValueAsString(list);
		String userList=Obj.writeValueAsString(mlpUsersList);
		String tList=Obj.writeValueAsString(toolkitTypeList);
		String userjson=Obj.writeValueAsString(user);
		String tagjson=Obj.writeValueAsString(tagList);
		setCdsProperty();
		when(userService.isPublisherRole("someuser")).thenReturn(true);
		stubFor(get(urlEqualTo("/ccds/solution/somesolid")).willReturn(
                aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withBody(jsonStr)));
		stubFor(get(urlEqualTo("/ccds/solution/somesolid/revision/somerevid")).willReturn(
                aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withBody("{\"created\":\"2019-10-23T14:49:29.684762100Z\",\"modified\":\"2019-10-23T14:49:29.684762100Z\",\"revisionId\":\"somerevid\",\"authors\":[],\"publisher\":\"somepub\",\"onboarded\":\"2019-10-23T14:49:29.684762100Z\",\"solutionId\":\"somesolid\",\"userId\":\"someuserid\"}")));
		stubFor(get(urlEqualTo("/ccds/catalog/solution/somesolid")).willReturn(
                aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withBody(cat)));
		stubFor(get(urlEqualTo("/ccds/access/solution/somesolid/user")).willReturn(
                aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withBody(userList)));
		stubFor(get(urlEqualTo("/ccds/code/pair/TOOLKIT_TYPE")).willReturn(
                aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withBody(tList)));
		stubFor(get(urlEqualTo("/ccds/code/pair/TOOLKIT_TYPE")).willReturn(
                aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withBody(tList)));
		stubFor(get(urlEqualTo("/ccds/code/pair/MODEL_TYPE")).willReturn(
                aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withBody(tList)));
		stubFor(get(urlEqualTo("/ccds/user/someuser")).willReturn(
                aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withBody(userjson)));
		stubFor(get(urlEqualTo("/ccds/solution/somesolid/tag")).willReturn(
                aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withBody(tagjson)));
		stubFor(get(urlEqualTo("/ccds/solution/somesolid/revision")).willReturn(
                aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withBody("[{\"created\":\"2019-10-23T14:49:29.684762100Z\",\"modified\":\"2019-10-23T14:49:29.684762100Z\",\"revisionId\":\"somerevid\",\"authors\":[],\"publisher\":\"somepub\",\"onboarded\":\"2019-10-23T14:49:29.684762100Z\",\"solutionId\":\"somesolid\",\"userId\":\"someuserid\"}]")));
		
		
		MLSolution mlSol=catalogService.getSolution("somesolid", "somerevid", "someuserid");
		assertNotNull(mlSol);
		MLSolution mlSolutionRes=catalogService.getSolution("somesolid", "someuserid");
		assertNotNull(mlSolutionRes);
	}
	@Test
	public void updateSolutionPictureTest() {
		byte[] pic="MyPic".getBytes();
		setCdsProperty();
		stubFor(put(urlEqualTo("/ccds/solution/somesolid/pic")).willReturn(
                aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)));
		catalogService.updateSolutionPicture("somesolid",pic);
	}
	@Test
	public void getSolutionPictureTest() throws JsonProcessingException{
		byte[] pic="MyPic".getBytes();
		ObjectMapper Obj = new ObjectMapper();
		String jsonStr = Obj.writeValueAsString(pic);
		setCdsProperty();
		stubFor(get(urlEqualTo("/ccds/solution/somesolid/pic")).willReturn(
                aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withBody(jsonStr)));
		byte[] res=catalogService.getSolutionPicture("somesolid");
		assertNotNull(res);
	}
	@Test
	public void copyRevisionDocumentsTest() throws JsonProcessingException, AcumosServiceException {
		List<MLPDocument> res=new ArrayList<>();
		MLPDocument document=new MLPDocument();
		document.setDocumentId("somedocid");
		document.setName("somename");
		res.add(document);
		ObjectMapper Obj = new ObjectMapper();
		String jsonStr = Obj.writeValueAsString(res);
		setCdsProperty();
		stubFor(get(urlEqualTo("/ccds/revision/fromRevisionId/catalog/somecatid/document")).willReturn(
                aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withBody(jsonStr)));
		stubFor(post(urlEqualTo("/ccds/revision/somerevid/catalog/somecatid/document/somedocid")).willReturn(
                aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)));
		List<MLPDocument> resPass=catalogService.copyRevisionDocuments("somesolid", "somerevid", "somecatid", "someuserid", "fromRevisionId");
		assertNotNull(resPass);
	}
	public void removeRevisionDocumentTest() {
		
	}
	@Test
	public void checkUniqueSolNameTest() throws JsonProcessingException {
		MLPSolution oldSolution=PortalUtils.convertToMLPSolution(getMLSolution());
		ObjectMapper Obj = new ObjectMapper();
		String jsonStr = Obj.writeValueAsString(oldSolution);
		setCdsProperty();
		stubFor(get(urlEqualTo("/ccds/solution/somesolid")).willReturn(
                aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withBody(jsonStr)));
		List<MLPSolution> solutionList = new ArrayList<MLPSolution>();
		solutionList.add(oldSolution);
		RestPageResponse<MLPSolution> solutionsRes = new RestPageResponse<>(solutionList, PageRequest.of(0, 1), 1);
		String jsonStrRes = Obj.writeValueAsString(solutionsRes);
		stubFor(get(urlEqualTo("/ccds/solution/search/portal?name=somesolname&active=true&page=0&size=10000")).willReturn(
                aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withBody(jsonStrRes)));
		boolean res=catalogService.checkUniqueSolName("somesolid", "somesolname");
		assertTrue(res);
	}
	public void getLicenseUrl() {
		
	}
	public void getProtoUrl() {
		
	}
	
	public void getPayloadTest() {
		
	}
	@Test
	public void addSolutionRevisionPublisherTest() throws AcumosServiceException {
		setCdsProperty();
		stubFor(get(urlEqualTo("/ccds/solution/somesolid/revision/somerevid")).willReturn(
                aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withBody("{\"created\":\"2019-10-23T14:49:29.684762100Z\",\"revisionId\":\"somerevid\",\"authors\":[],\"publisher\":\"somepublisher\",\"solutionId\":\"somesolid\",\"userId\":\"someuserid\"}")));
		stubFor(put(urlEqualTo("/ccds/solution/somesolid/revision/somerevid")).willReturn(
                aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)));
		
		catalogService.addSolutionRevisionPublisher("somesolid", "somerevid", "somepublisher");
	}
	@Test
	public void getSolutionRevisionPublisherTest() throws JsonProcessingException, AcumosServiceException {
		setCdsProperty();
		stubFor(get(urlEqualTo("/ccds/solution/somesolid/revision/somerevid")).willReturn(
                aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withBody("{\"created\":\"2019-10-23T14:49:29.684762100Z\",\"revisionId\":\"somerevid\",\"authors\":[],\"publisher\":\"somepublisher\",\"solutionId\":\"Somesolid\",\"userId\":\"someuserid\"}")));
		String revision=catalogService.getSolutionRevisionPublisher("somesolid", "somerevid");
		assertNotNull(revision);
	}
	@Test
	public void getSolutionWebMetadataTest() throws JsonProcessingException {
		setCdsProperty();
		MLSolution mlsolution=getMLSolution();
		ObjectMapper Obj = new ObjectMapper();
		String jsonStr=null;
		jsonStr = Obj.writeValueAsString(mlsolution);
		stubFor(get(urlEqualTo("/ccds/solution/Solution1")).willReturn(
                aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withBody(jsonStr)));
		MLSolutionWeb sol=catalogService.getSolutionWebMetadata("Solution1");
		assertNotNull(sol);
	}
	@Test
	public void getUserAccessSolutionsTest() throws JsonProcessingException {
		setCdsProperty();
		RestPageRequest restPageRequest = new RestPageRequest();
		restPageRequest.setSize(9);
		restPageRequest.setPage(1);
		MLSolution mlsolution=getMLSolution();
		MLPSolution solutionByName = PortalUtils.convertToMLPSolution(mlsolution);
		List<MLPSolution> solutionList = new ArrayList<MLPSolution>();
		solutionList.add(solutionByName);
		RestPageResponse<MLPSolution> solutionsRes = new RestPageResponse<>(solutionList, PageRequest.of(0, 1), 1);
		ObjectMapper Obj = new ObjectMapper();
		String jsonStr=null;
		jsonStr = Obj.writeValueAsString(solutionsRes);
		stubFor(get(urlEqualTo("/ccds/access/user/someuserid/solution?page=1&size=9")).willReturn(
                aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withBody(jsonStr)));
		RestPageResponse<MLPSolution> res=catalogService.getUserAccessSolutions("someuserid", restPageRequest);
		assertNotNull(res);
	}
	private void setCdsProperty() {
		when(env.getProperty("cdms.client.url")).thenReturn(url);
		when(env.getProperty("cdms.client.username")).thenReturn(user);
		when(env.getProperty("cdms.client.password")).thenReturn(pass);
	}
	
	private MLSolution getMLSolution() {
		MLSolution mlsolution = new MLSolution();
		mlsolution.setSolutionId("Solution1");
		mlsolution.setName("Test_Solution data");
		mlsolution.setOwnerId("41058105-67f4-4461-a192-f4cb7fdafd34");
		mlsolution.setModelType("CL");
		mlsolution.setTookitType("DS");
		return mlsolution;
	}
}