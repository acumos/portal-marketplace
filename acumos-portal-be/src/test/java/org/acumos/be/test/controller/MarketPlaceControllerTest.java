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

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.http.Fault;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
// import org.acumos.be.test.config.UploadConfig;
import org.acumos.be.test.security.WithMLMockUser;
import org.acumos.cds.domain.MLPDocument;
import org.acumos.cds.transport.RestPageRequest;
import org.acumos.portal.be.common.ConfigConstants;
import org.acumos.portal.be.common.JsonRequest;
import org.acumos.portal.be.common.JsonResponse;
import org.acumos.portal.be.common.RestPageResponseBE;
import org.acumos.portal.be.transport.Author;
import org.acumos.portal.be.transport.MLSolution;
import org.acumos.portal.be.transport.RestPageRequestPortal;
import org.acumos.portal.be.transport.RevisionDescription;
import org.apache.commons.io.FilenameUtils;
import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.client.MockMvcClientHttpRequestFactory;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = org.acumos.portal.be.Application.class,
		webEnvironment = WebEnvironment.RANDOM_PORT,
		properties = {ConfigConstants.portal_feature_email + "=true",
				ConfigConstants.portal_feature_email_from + "=support@acumos.org",
				ConfigConstants.portal_feature_email_service + "=smtp",
				ConfigConstants.spring_mail_host + "=localhost",
				ConfigConstants.portal_feature_catalog_pagesize + "=1000",
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
				ConfigConstants.cdms_client_password + "=ccds_test",
				ConfigConstants.portal_feature_sv_enabled + "=false",
				"nexus.url=http://localhost:8000/repository/repo_acumos_model_maven/", "nexus.username=foo",
				"nexus.password=bar", "nexus.groupId=com.artifact", "document.size=100000"})
@AutoConfigureMockMvc
@EnableAutoConfiguration
@ContextConfiguration()
@WithMLMockUser
public class MarketPlaceControllerTest {

	@Rule
	public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().port(8000));

	/*
	 * @Rule public WireMockRule wireMockRule2 = new WireMockRule(wireMockConfig().port(8084));
	 */

	private RestTemplate restTemplate;

	@LocalServerPort
	int randomServerPort;

	@Autowired
	private ObjectMapper objectMapper;


	private final String host = "http://localhost";

	@Autowired
	private WebApplicationContext webAppContext;
	private MockMvc mvc;

	@Before
	public void setup() {

		MockitoAnnotations.initMocks(this);
		mvc = MockMvcBuilders.webAppContextSetup(webAppContext).build();
		MockMvcClientHttpRequestFactory requestFactory = new MockMvcClientHttpRequestFactory(mvc);
		restTemplate = new RestTemplate(requestFactory);
	}

	@Test
	public void getAuthorsTest() {

		String solutionId = "b7b9bb9c-980c-4a18-b7bf-545bbd9173ab";
		String revisionId = "4f5079b9-49e8-48a3-8fcb-c006e96c4c10";

		stubFor(get(urlEqualTo(
				"/ccds/solution/b7b9bb9c-980c-4a18-b7bf-545bbd9173ab/revision/4f5079b9-49e8-48a3-8fcb-c006e96c4c10"))
						.willReturn(aResponse().withStatus(HttpStatus.SC_OK)
								.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())
								.withBody("{" + "\"created\": 1535603923000," + "\"modified\": 1536363779000,"
										+ "\"revisionId\": \"4f5079b9-49e8-48a3-8fcb-c006e96c4c10\","
										+ "\"version\": \"1\"," + "\"description\": null," + "\"metadata\": null,"
										+ "\"origin\": null," + "\"accessTypeCode\": \"PB\","
										+ "\"validationStatusCode\": \"PS\","
										+ "\"authors\": [{\"name\": \"TestAuthor\", \"contact\": \"testauthor@gmail.com\"}],"
										+ "\"publisher\": \"Acumos\","
										+ "\"solutionId\": \"b7b9bb9c-980c-4a18-b7bf-545bbd9173ab\","
										+ "\"userId\": \"bc961e2a-9506-4cf5-bbdb-009558b79e29\"," + "\"sourceId\": null"
										+ "}")));

		ResponseEntity<JsonResponse<List<Author>>> authorResponse = restTemplate.exchange(
				host + ":" + randomServerPort + "/solution/" + solutionId + "/revision/" + revisionId
						+ "/authors",
				HttpMethod.GET, null, new ParameterizedTypeReference<JsonResponse<List<Author>>>() {
				});

		assertNotNull(authorResponse);
		assertEquals(HttpServletResponse.SC_OK, authorResponse.getStatusCode().value());
		List<Author> authorList = authorResponse.getBody().getResponseBody();
		assertEquals("TestAuthor", authorList.get(0).getName());
		assertEquals("testauthor@gmail.com", authorList.get(0).getContact());
	}

	@Test
	public void getAuthorsFailTest() {

		String solutionId = "b7b9bb9c-980c-4a18-b7bf-545bbd9173ab";
		String revisionId = "4f5079b9-49e8-48a3-8fcb-c006e96c4c10";

		stubFor(get(urlEqualTo(
				"/ccds/solution/b7b9bb9c-980c-4a18-b7bf-545bbd9173ab/revision/4f5079b9-49e8-48a3-8fcb-c006e96c4c10"))
						.willReturn(aResponse().withStatus(HttpStatus.SC_BAD_REQUEST)
								.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())
								.withFault(Fault.MALFORMED_RESPONSE_CHUNK)));

		try {
			ResponseEntity<JsonResponse<List<Author>>> authorResponse = restTemplate.exchange(
					host + ":" + randomServerPort + "/solution/" + solutionId + "/revision/" + revisionId
							+ "/authors",
					HttpMethod.GET, null, new ParameterizedTypeReference<JsonResponse<List<Author>>>() {
					});
		} catch (HttpClientErrorException e) {
			assertEquals(HttpServletResponse.SC_BAD_REQUEST, e.getStatusCode().value());
		}
	}

	@Test
	public void addAuthorsFailTest() {

		String solutionId = "b7b9bb9c-980c-4a18-b7bf-545bbd9173ab";
		String revisionId = "4f5079b9-49e8-48a3-8fcb-c006e96c4c10";

		JsonRequest<Author> reqObj = new JsonRequest<>();
		Author author = new Author();
		author.setName("TestAuthor1");
		author.setContact("testauthor1@gmail.com");
		reqObj.setBody(author);

		stubFor(put(urlEqualTo(
				"/ccds/solution/b7b9bb9c-980c-4a18-b7bf-545bbd9173ab/revision/4f5079b9-49e8-48a3-8fcb-c006e96c4c10"))
						.willReturn(aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type",
								MediaType.APPLICATION_JSON.toString())));

		stubFor(get(urlEqualTo(
				"/ccds/solution/b7b9bb9c-980c-4a18-b7bf-545bbd9173ab/revision/4f5079b9-49e8-48a3-8fcb-c006e96c4c10"))
						.willReturn(aResponse().withStatus(HttpStatus.SC_OK)
								.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())
								.withBody("{" + "\"created\": 1535603923000," + "\"modified\": 1536363779000,"
										+ "\"revisionId\": \"4f5079b9-49e8-48a3-8fcb-c006e96c4c10\","
										+ "\"version\": \"1\"," + "\"description\": null," + "\"metadata\": null,"
										+ "\"origin\": null," + "\"accessTypeCode\": \"PB\","
										+ "\"validationStatusCode\": \"PS\","
										+ "\"authors\": [{\"name\": \"TestAuthor\", \"contact\": \"testauthor@gmail.com\"}, {\"name\": \"TestAuthor1\", \"contact\": \"testauthor1@gmail.com\"}],"
										+ "\"publisher\": \"Acumos\","
										+ "\"solutionId\": \"b7b9bb9c-980c-4a18-b7bf-545bbd9173ab\","
										+ "\"userId\": \"bc961e2a-9506-4cf5-bbdb-009558b79e29\"," + "\"sourceId\": null"
										+ "}")));

		HttpHeaders headers = new HttpHeaders();
		HttpEntity<JsonRequest<Author>> requestEntity = new HttpEntity<>(reqObj, headers);
		try {
			ResponseEntity<JsonResponse<List<Author>>> authorResponse = restTemplate.exchange(
					host + ":" + randomServerPort + "/solution/" + solutionId + "/revision/" + revisionId
							+ "/authors",
					HttpMethod.PUT, requestEntity,
					new ParameterizedTypeReference<JsonResponse<List<Author>>>() {
					});
		} catch (HttpClientErrorException e) {
			assertEquals(HttpServletResponse.SC_BAD_REQUEST, e.getStatusCode().value());
		}
	}

	@Test
	public void addAuthorsTest() {

		String solutionId = "b7b9bb9c-980c-4a18-b7bf-545bbd9173ab";
		String revisionId = "4f5079b9-49e8-48a3-8fcb-c006e96c4c10";

		JsonRequest<Author> reqObj = new JsonRequest<>();
		Author author = new Author();
		author.setName("TestAuthor1");
		author.setContact("testauthor1@gmail.com");
		reqObj.setBody(author);

		stubFor(put(urlEqualTo(
				"/ccds/solution/b7b9bb9c-980c-4a18-b7bf-545bbd9173ab/revision/4f5079b9-49e8-48a3-8fcb-c006e96c4c10"))
						.willReturn(aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type",
								MediaType.APPLICATION_JSON.toString())));

		stubFor(get(urlEqualTo(
				"/ccds/solution/b7b9bb9c-980c-4a18-b7bf-545bbd9173ab/revision/4f5079b9-49e8-48a3-8fcb-c006e96c4c10"))
						.willReturn(aResponse().withStatus(HttpStatus.SC_OK)
								.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())
								.withBody("{" + "\"created\": 1535603923000," + "\"modified\": 1536363779000,"
										+ "\"revisionId\": \"4f5079b9-49e8-48a3-8fcb-c006e96c4c10\","
										+ "\"version\": \"1\"," + "\"description\": null," + "\"metadata\": null,"
										+ "\"origin\": null," + "\"accessTypeCode\": \"PB\","
										+ "\"validationStatusCode\": \"PS\","
										+ "\"authors\": [{\"name\": \"TestAuthor\", \"contact\": \"testauthor@gmail.com\"}],"
										+ "\"publisher\": \"Acumos\","
										+ "\"solutionId\": \"b7b9bb9c-980c-4a18-b7bf-545bbd9173ab\","
										+ "\"userId\": \"bc961e2a-9506-4cf5-bbdb-009558b79e29\"," + "\"sourceId\": null"
										+ "}")));

		HttpHeaders headers = new HttpHeaders();
		HttpEntity<JsonRequest<Author>> requestEntity = new HttpEntity<>(reqObj, headers);

		ResponseEntity<JsonResponse<List<Author>>> authorResponse = restTemplate.exchange(
				host + ":" + randomServerPort + "/solution/" + solutionId + "/revision/" + revisionId
						+ "/authors",
				HttpMethod.PUT, requestEntity,
				new ParameterizedTypeReference<JsonResponse<List<Author>>>() {
				});

		assertNotNull(authorResponse);
		assertEquals(HttpServletResponse.SC_OK, authorResponse.getStatusCode().value());
		List<Author> authorList = authorResponse.getBody().getResponseBody();
		assertEquals("TestAuthor", authorList.get(0).getName());
		assertEquals("testauthor@gmail.com", authorList.get(0).getContact());
	}

	@Test
	public void removeAuthorsTest() {

		String solutionId = "b7b9bb9c-980c-4a18-b7bf-545bbd9173ab";
		String revisionId = "4f5079b9-49e8-48a3-8fcb-c006e96c4c10";

		JsonRequest<Author> reqObj = new JsonRequest<>();
		Author author = new Author();
		author.setName("TestAuthor");
		author.setContact("testauthor@gmail.com");
		reqObj.setBody(author);

		stubFor(put(urlEqualTo(
				"/ccds/solution/b7b9bb9c-980c-4a18-b7bf-545bbd9173ab/revision/4f5079b9-49e8-48a3-8fcb-c006e96c4c10"))
						.willReturn(aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type",
								MediaType.APPLICATION_JSON.toString())));

		stubFor(get(urlEqualTo(
				"/ccds/solution/b7b9bb9c-980c-4a18-b7bf-545bbd9173ab/revision/4f5079b9-49e8-48a3-8fcb-c006e96c4c10"))
						.willReturn(aResponse().withStatus(HttpStatus.SC_OK)
								.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())
								.withBody("{" + "\"created\": 1535603923000," + "\"modified\": 1536363779000,"
										+ "\"revisionId\": \"4f5079b9-49e8-48a3-8fcb-c006e96c4c10\","
										+ "\"version\": \"1\"," + "\"description\": null," + "\"metadata\": null,"
										+ "\"origin\": null," + "\"accessTypeCode\": \"PB\","
										+ "\"validationStatusCode\": \"PS\","
										+ "\"authors\": [{\"name\": \"TestAuthor\", \"contact\": \"testauthor@gmail.com\"}],"
										+ "\"publisher\": \"Acumos\","
										+ "\"solutionId\": \"b7b9bb9c-980c-4a18-b7bf-545bbd9173ab\","
										+ "\"userId\": \"bc961e2a-9506-4cf5-bbdb-009558b79e29\"," + "\"sourceId\": null"
										+ "}")));

		HttpHeaders headers = new HttpHeaders();
		HttpEntity<JsonRequest<Author>> requestEntity = new HttpEntity<>(reqObj, headers);

		ResponseEntity<JsonResponse<List<Author>>> authorResponse = restTemplate.exchange(
				host + ":" + randomServerPort + "/solution/" + solutionId + "/revision/" + revisionId
						+ "/removeAuthor",
				HttpMethod.PUT, requestEntity,
				new ParameterizedTypeReference<JsonResponse<List<Author>>>() {
				});

		assertNotNull(authorResponse);
		assertEquals(HttpServletResponse.SC_OK, authorResponse.getStatusCode().value());
		List<Author> authorList = authorResponse.getBody().getResponseBody();
		assertEquals(0, authorList.size());
	}

	@Test
	public void getDescriptionTest() {
		String revisionId = "4f5079b9-49e8-48a3-8fcb-c006e96c4c10";
		String catalogId = "4321-4321-4321-4321-4321";
		String ccdsRevCatDescrPath =
				String.format("/ccds/revision/%s/catalog/%s/descr", revisionId, catalogId);

		stubFor(get(urlEqualTo(ccdsRevCatDescrPath)).willReturn(aResponse().withStatus(HttpStatus.SC_OK)
				.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())
				.withBody("{\r\n" + "\"catalogId\": \"4321-4321-4321-4321-4321\","
						+ "\"created\": \"2018-09-10T16:00:39.629Z\"," + "\"description\": \"TestDescription\","
						+ "\"modified\": \"2018-09-10T16:00:39.629Z\","
						+ "\"revisionId\": \"4f5079b9-49e8-48a3-8fcb-c006e96c4c10\"" + "}")));

		ResponseEntity<JsonResponse<RevisionDescription>> descriptionResponse = restTemplate.exchange(
				host + ":" + randomServerPort + "/solution/revision/" + revisionId + "/" + catalogId
						+ "/description",
				HttpMethod.GET, null, new ParameterizedTypeReference<JsonResponse<RevisionDescription>>() {
				});

		assertNotNull(descriptionResponse);
		assertEquals(HttpServletResponse.SC_OK, descriptionResponse.getStatusCode().value());
		RevisionDescription description = descriptionResponse.getBody().getResponseBody();
		assertEquals("TestDescription", description.getDescription());
	}

	@Test
	public void getDescriptionFailTest() {

		String solutionId = "b7b9bb9c-980c-4a18-b7bf-545bbd9173ab";
		String revisionId = "4f5079b9-49e8-48a3-8fcb-c006e96c4c10";
		String catalogId = "4321-4321-4321-4321-4321";
		String ccdsRevCatDescrPath =
				String.format("/ccds/revision/%s/catalog/%s/descr", revisionId, catalogId);

		stubFor(get(urlEqualTo(ccdsRevCatDescrPath))
				.willReturn(aResponse().withStatus(HttpStatus.SC_BAD_REQUEST).withHeader("Content-Type",
						MediaType.APPLICATION_JSON.toString())));

		try {
			ResponseEntity<JsonResponse<RevisionDescription>> descriptionResponse = restTemplate.exchange(
					host + ":" + randomServerPort + "/solution/revision/" + revisionId + "/" + catalogId
							+ "/description",
					HttpMethod.GET, null,
					new ParameterizedTypeReference<JsonResponse<RevisionDescription>>() {
					});
		} catch (HttpClientErrorException e) {
			assertEquals(HttpServletResponse.SC_BAD_REQUEST, e.getStatusCode().value());
		}
	}

	@Test
	public void addUpdateDescriptionTest() {

		String solutionId = "b7b9bb9c-980c-4a18-b7bf-545bbd9173ab";
		String revisionId = "4f5079b9-49e8-48a3-8fcb-c006e96c4c10";
		String catalogId = "4321-4321-4321-4321-4321";
		String ccdsRevCatDescrPath =
				String.format("/ccds/revision/%s/catalog/%s/descr", revisionId, catalogId);
		RevisionDescription newRevisionDescription = new RevisionDescription();
		newRevisionDescription.setDescription("New Description");
		JsonRequest<RevisionDescription> reqObj = new JsonRequest<>();
		reqObj.setBody(newRevisionDescription);

		stubFor(get(urlEqualTo(ccdsRevCatDescrPath))
				.willReturn(aResponse().withStatus(HttpStatus.SC_BAD_REQUEST).withHeader("Content-Type",
						MediaType.APPLICATION_JSON.toString())));

		stubFor(post(urlEqualTo(ccdsRevCatDescrPath)).willReturn(aResponse()
				.withStatus(HttpStatus.SC_OK)
				.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())
				.withBody("{\r\n" + "\"catalogId\": \"4321-4321-4321-4321-4321\","
						+ "\"created\": \"2018-09-10T16:00:39.629Z\"," + "\"description\": \"New Description\","
						+ "\"modified\": \"2018-09-10T16:00:39.629Z\","
						+ "\"revisionId\": \"4f5079b9-49e8-48a3-8fcb-c006e96c4c10\"" + "}")));

		HttpHeaders headers = new HttpHeaders();
		HttpEntity<JsonRequest<RevisionDescription>> requestEntity = new HttpEntity<>(reqObj, headers);

		ResponseEntity<JsonResponse<RevisionDescription>> descriptionResponse = restTemplate.exchange(
				host + ":" + randomServerPort + "/solution/revision/" + solutionId + "/" + revisionId + "/"
						+ catalogId + "/description",
				HttpMethod.POST, requestEntity,
				new ParameterizedTypeReference<JsonResponse<RevisionDescription>>() {
				});

		assertNotNull(descriptionResponse);
		assertEquals(HttpServletResponse.SC_OK, descriptionResponse.getStatusCode().value());
		RevisionDescription description = descriptionResponse.getBody().getResponseBody();
		assertEquals("New Description", description.getDescription());
	}

	@Test
	public void findPortalSolutionTest() {

		JsonRequest<RestPageRequestPortal> reqObj = new JsonRequest<>();
		RestPageRequestPortal restpagerequestPortal = new RestPageRequestPortal();
		restpagerequestPortal.setActive(true);
		restpagerequestPortal.setPublished(true);
		restpagerequestPortal.setSortBy("MR");
		Map<String, String> fieldToDirectionMap = new HashMap<>();
		RestPageRequest pageRequest = new RestPageRequest();
		pageRequest.setSize(9);
		pageRequest.setPage(0);
		fieldToDirectionMap.put("modified", "DESC");
		pageRequest.setFieldToDirectionMap(fieldToDirectionMap);
		restpagerequestPortal.setPageRequest(pageRequest);
		reqObj.setBody(restpagerequestPortal);

		stubFor(get(urlEqualTo(
				"/ccds/solution/search/portal/kwtag?active=true&page=0&size=9&sort=modified,DESC"))
						.willReturn(aResponse().withStatus(HttpStatus.SC_OK)
								.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString()).withBody(
										"{\"content\":[{\"created\":1535603044000,\"modified\":1536350829000,\"solutionId\":\"f226cc60-c2ec-4c2b-b05c-4a521f77e077\",\"name\":\"TestSolution\",\"metadata\":null,\"active\":true,\"modelTypeCode\":\"CL\",\"toolkitTypeCode\":\"TF\",\"origin\":null,\"userId\":\"bc961e2a-9506-4cf5-bbdb-009558b79e29\",\"sourceId\":null,\"tags\":[{\"tag\":\"Test\"}],\"viewCount\":12,\"downloadCount\":0,\"lastDownload\":1536364233000,\"ratingCount\":0,\"ratingAverageTenths\":0,\"featured\":false}],\"last\":true,\"totalPages\":1,\"totalElements\":1,\"size\":9,\"number\":0,\"sort\":[{\"direction\":\"DESC\",\"property\":\"modified\",\"ignoreCase\":false,\"nullHandling\":\"NATIVE\",\"ascending\":false,\"descending\":true}],\"numberOfElements\":1,\"first\":true}")));

		stubFor(get(urlEqualTo("/ccds/solution/f226cc60-c2ec-4c2b-b05c-4a521f77e077"))
				.willReturn(aResponse().withStatus(HttpStatus.SC_OK)
						.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString()).withBody(
								"{\"created\":1535603044000,\"modified\":1536350829000,\"solutionId\":\"f226cc60-c2ec-4c2b-b05c-4a521f77e077\",\"name\":\"TestSolution\",\"metadata\":null,\"active\":true,\"modelTypeCode\":\"CL\",\"toolkitTypeCode\":\"TF\",\"origin\":null,\"userId\":\"bc961e2a-9506-4cf5-bbdb-009558b79e29\",\"sourceId\":null,\"tags\":[{\"tag\":\"Test\"}],\"viewCount\":12,\"downloadCount\":0,\"lastDownload\":1536364233000,\"ratingCount\":0,\"ratingAverageTenths\":0,\"featured\":false}")));

		stubFor(get(urlEqualTo("/ccds/solution/f226cc60-c2ec-4c2b-b05c-4a521f77e077/tag"))
				.willReturn(aResponse().withStatus(HttpStatus.SC_OK)
						.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())
						.withBody("[{\"tag\":\"Test\"}]")));

		stubFor(get(urlEqualTo("/ccds/user/bc961e2a-9506-4cf5-bbdb-009558b79e29"))
				.willReturn(aResponse().withStatus(HttpStatus.SC_OK)
						.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString()).withBody(
								"{\"created\":1535602889000,\"modified\":1536623387000,\"userId\":\"bc961e2a-9506-4cf5-bbdb-009558b79e29\",\"firstName\":\"Test\",\"middleName\":null,\"lastName\":\"User\",\"orgName\":null,\"email\":\"testUser@gmail.com\",\"loginName\":\"test\",\"loginHash\":null,\"loginPassExpire\":null,\"authToken\":\"\",\"active\":true,\"lastLogin\":1536623387000,\"loginFailCount\":null,\"loginFailDate\":null,\"picture\":null,\"apiToken\":\"30d19b719c1d44ae84d92dcc87f5a1ad\",\"verifyTokenHash\":null,\"verifyExpiration\":null,\"tags\":[]}")));

		stubFor(get(urlEqualTo("/ccds/access/solution/f226cc60-c2ec-4c2b-b05c-4a521f77e077/user"))
				.willReturn(aResponse().withStatus(HttpStatus.SC_OK)
						.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString()).withBody("[]")));

		stubFor(get(urlEqualTo("/ccds/solution/f226cc60-c2ec-4c2b-b05c-4a521f77e077/revision"))
				.willReturn(aResponse().withStatus(HttpStatus.SC_OK)
						.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString()).withBody(
								"[{\"created\":1535603252000,\"modified\":1536354698000,\"onboarded\":1535603047000,\"revisionId\":\"02c5f263-c612-4bd2-abaa-d12ccc0d2476\",\"version\":\"2\",\"description\":null,\"metadata\":null,\"origin\":null,\"accessTypeCode\":\"OR\",\"validationStatusCode\":\"PS\",\"authors\":[],\"publisher\":\"Acumos\",\"solutionId\":\"f226cc60-c2ec-4c2b-b05c-4a521f77e077\",\"userId\":\"bc961e2a-9506-4cf5-bbdb-009558b79e29\",\"sourceId\":null},{\"created\":1535603044000,\"modified\":1535603044000,\"onboarded\":1535603044000,\"revisionId\":\"f6b577a1-1849-4965-b77e-2ea11ab0b327\",\"version\":\"1\",\"description\":null,\"metadata\":null,\"origin\":null,\"accessTypeCode\":\"PB\",\"validationStatusCode\":\"IP\",\"authors\":[],\"publisher\":null,\"solutionId\":\"f226cc60-c2ec-4c2b-b05c-4a521f77e077\",\"userId\":\"bc961e2a-9506-4cf5-bbdb-009558b79e29\",\"sourceId\":null}]")));

		stubFor(get(urlEqualTo(
				"/ccds/thread/solution/f226cc60-c2ec-4c2b-b05c-4a521f77e077/revision/02c5f263-c612-4bd2-abaa-d12ccc0d2476/comment/count"))
						.willReturn(aResponse().withStatus(HttpStatus.SC_OK)
								.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())
								.withBody("{\"count\":0}")));

		stubFor(get(urlEqualTo(
				"/ccds/task/search?solutionId=f226cc60-c2ec-4c2b-b05c-4a521f77e077&_j=a&page=0&size=5&sort=created,DESC"))
						.willReturn(aResponse().withStatus(HttpStatus.SC_OK)
								.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString()).withBody(
										"{\"content\":[{\"taskId\":5,\"trackingId\":\"fd1ea3aa-5a91-454a-9f8b-87c674e25417\",\"taskCode\":\"OB\",\"solutionId\":\"f226cc60-c2ec-4c2b-b05c-4a521f77e077\",\"revisionId\":\"02c5f263-c612-4bd2-abaa-d12ccc0d2476\",\"userId\":\"bc961e2a-9506-4cf5-bbdb-009558b79e29\",\"name\":\"AddDockerImage\",\"statusCode\":\"SU\",\"created\":1535603254000,\"modified\":1535603254000}],\"last\":false,\"totalPages\":30,\"totalElements\":30,\"size\":1,\"number\":0,\"sort\":[{\"direction\":\"DESC\",\"property\":\"startDate\",\"ignoreCase\":false,\"nullHandling\":\"NATIVE\",\"ascending\":false,\"descending\":true}],\"numberOfElements\":1,\"first\":true}")));

		stubFor(get(urlEqualTo("/ccds/task/5/stepresult")).willReturn(aResponse()
				.withStatus(HttpStatus.SC_OK)
				.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString()).withBody(
						"[{\"stepResultId\":28,\"taskId\":5,\"name\":\"AddDockerImage\",\"statusCode\":\"SU\",\"result\":\"Add Artifact - image for solution - f226cc60-c2ec-4c2b-b05c-4a521f77e077 Successful\",\"startDate\":1535603254000,\"endDate\":1535603254000}]")));

		stubFor(get(urlEqualTo(
				"/ccds/pubreq/search?revisionId=02c5f263-c612-4bd2-abaa-d12ccc0d2476&solutionId=f226cc60-c2ec-4c2b-b05c-4a521f77e077&_j=a&statusCode=PE&page=0&size=1"))
						.willReturn(aResponse().withStatus(HttpStatus.SC_OK)
								.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString()).withBody(
										"{\"content\":[],\"last\":true,\"totalPages\":0,\"totalElements\":0,\"size\":1,\"number\":0,\"sort\":null,\"numberOfElements\":0,\"first\":true}")));

		stubFor(get(urlEqualTo("/ccds/catalog/solution/f226cc60-c2ec-4c2b-b05c-4a521f77e077"))
				.willReturn(aResponse().withStatus(HttpStatus.SC_OK)
						.withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
						.withBody("[{\"created\": \"2019-04-05T20:47:03Z\","
								+ "\"modified\": \"2019-04-05T20:47:03Z\","
								+ "\"catalogId\": \"12345678-abcd-90ab-cdef-1234567890ab\","
								+ "\"accessTypeCode\": \"PB\"," + "\"selfPublish\": false,"
								+ "\"name\": \"Test catalog\"," + "\"publisher\": \"Acumos\","
								+ "\"description\": null," + "\"origin\": null,"
								+ "\"url\": \"http://localhost\"}]")));

		stubFor(get(urlEqualTo(
				"/ccds/pubreq/search?solutionId=f226cc60-c2ec-4c2b-b05c-4a521f77e077&_j=a&page=0&size=1"))
						.willReturn(aResponse().withStatus(HttpStatus.SC_OK)
								.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString()).withBody(
										"{\"content\":[],\"last\":true,\"totalPages\":0,\"totalElements\":0,\"size\":1,\"number\":0,\"sort\":null,\"numberOfElements\":0,\"first\":true}")));

		HttpHeaders headers = new HttpHeaders();
		HttpEntity<JsonRequest<RestPageRequestPortal>> requestEntity =
				new HttpEntity<>(reqObj, headers);

		ResponseEntity<JsonResponse<RestPageResponseBE<MLSolution>>> solutionResponse =
				restTemplate.exchange(host + ":" + randomServerPort + "/portal/solutions", HttpMethod.POST,
						requestEntity,
						new ParameterizedTypeReference<JsonResponse<RestPageResponseBE<MLSolution>>>() {
						});

		assertNotNull(solutionResponse);
		assertEquals(HttpServletResponse.SC_OK, solutionResponse.getStatusCode().value());
		List<MLSolution> mlSolutionList = solutionResponse.getBody().getResponseBody().getContent();
		assertEquals("f226cc60-c2ec-4c2b-b05c-4a521f77e077", mlSolutionList.get(0).getSolutionId());
	}

	@Test
	public void findPublicPortalSolutionTest() {

		JsonRequest<RestPageRequestPortal> reqObj = new JsonRequest<>();
		RestPageRequestPortal restpagerequestPortal = new RestPageRequestPortal();
		restpagerequestPortal.setActive(true);
		restpagerequestPortal.setPublished(true);
		restpagerequestPortal.setSortBy("MR");
		Map<String, String> fieldToDirectionMap = new HashMap<>();
		RestPageRequest pageRequest = new RestPageRequest();
		pageRequest.setSize(9);
		pageRequest.setPage(0);
		fieldToDirectionMap.put("modified", "DESC");
		pageRequest.setFieldToDirectionMap(fieldToDirectionMap);
		restpagerequestPortal.setPageRequest(pageRequest);
		reqObj.setBody(restpagerequestPortal);

		stubFor(get(urlEqualTo(
				"/ccds/solution/search/portal/kwtag?ctlg=12345678-abcd-90ab-cdef-1234567890ab&active=true&page=0&size=9&sort=modified,DESC"))
						.willReturn(aResponse().withStatus(HttpStatus.SC_OK)
								.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString()).withBody(
										"{\"content\":[{\"created\":1535603044000,\"modified\":1536350829000,\"solutionId\":\"f226cc60-c2ec-4c2b-b05c-4a521f77e077\",\"name\":\"TestSolution\",\"metadata\":null,\"active\":true,\"modelTypeCode\":\"CL\",\"toolkitTypeCode\":\"TF\",\"origin\":null,\"userId\":\"bc961e2a-9506-4cf5-bbdb-009558b79e29\",\"sourceId\":null,\"tags\":[{\"tag\":\"Test\"}],\"viewCount\":12,\"downloadCount\":0,\"lastDownload\":1536364233000,\"ratingCount\":0,\"ratingAverageTenths\":0,\"featured\":false}],\"last\":true,\"totalPages\":1,\"totalElements\":1,\"size\":9,\"number\":0,\"sort\":[{\"direction\":\"DESC\",\"property\":\"modified\",\"ignoreCase\":false,\"nullHandling\":\"NATIVE\",\"ascending\":false,\"descending\":true}],\"numberOfElements\":1,\"first\":true}")));

		stubFor(get(urlEqualTo("/ccds/solution/f226cc60-c2ec-4c2b-b05c-4a521f77e077"))
				.willReturn(aResponse().withStatus(HttpStatus.SC_OK)
						.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString()).withBody(
								"{\"created\":1535603044000,\"modified\":1536350829000,\"solutionId\":\"f226cc60-c2ec-4c2b-b05c-4a521f77e077\",\"name\":\"TestSolution\",\"metadata\":null,\"active\":true,\"modelTypeCode\":\"CL\",\"toolkitTypeCode\":\"TF\",\"origin\":null,\"userId\":\"bc961e2a-9506-4cf5-bbdb-009558b79e29\",\"sourceId\":null,\"tags\":[{\"tag\":\"Test\"}],\"viewCount\":12,\"downloadCount\":0,\"lastDownload\":1536364233000,\"ratingCount\":0,\"ratingAverageTenths\":0,\"featured\":false}")));

		stubFor(get(urlEqualTo("/ccds/solution/f226cc60-c2ec-4c2b-b05c-4a521f77e077/tag"))
				.willReturn(aResponse().withStatus(HttpStatus.SC_OK)
						.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())
						.withBody("[{\"tag\":\"Test\"}]")));

		stubFor(get(urlEqualTo("/ccds/user/bc961e2a-9506-4cf5-bbdb-009558b79e29"))
				.willReturn(aResponse().withStatus(HttpStatus.SC_OK)
						.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString()).withBody(
								"{\"created\":1535602889000,\"modified\":1536623387000,\"userId\":\"bc961e2a-9506-4cf5-bbdb-009558b79e29\",\"firstName\":\"Test\",\"middleName\":null,\"lastName\":\"User\",\"orgName\":null,\"email\":\"testUser@gmail.com\",\"loginName\":\"test\",\"loginHash\":null,\"loginPassExpire\":null,\"authToken\":\"\",\"active\":true,\"lastLogin\":1536623387000,\"loginFailCount\":null,\"loginFailDate\":null,\"picture\":null,\"apiToken\":\"30d19b719c1d44ae84d92dcc87f5a1ad\",\"verifyTokenHash\":null,\"verifyExpiration\":null,\"tags\":[]}")));

		stubFor(get(urlEqualTo("/ccds/access/solution/f226cc60-c2ec-4c2b-b05c-4a521f77e077/user"))
				.willReturn(aResponse().withStatus(HttpStatus.SC_OK)
						.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString()).withBody("[]")));

		stubFor(get(urlEqualTo("/ccds/solution/f226cc60-c2ec-4c2b-b05c-4a521f77e077/revision"))
				.willReturn(aResponse().withStatus(HttpStatus.SC_OK)
						.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString()).withBody(
								"[{\"created\":1535603252000,\"modified\":1536354698000,\"onboarded\":1535603047000,\"revisionId\":\"02c5f263-c612-4bd2-abaa-d12ccc0d2476\",\"version\":\"2\",\"description\":null,\"metadata\":null,\"origin\":null,\"accessTypeCode\":\"OR\",\"validationStatusCode\":\"PS\",\"authors\":[],\"publisher\":\"Acumos\",\"solutionId\":\"f226cc60-c2ec-4c2b-b05c-4a521f77e077\",\"userId\":\"bc961e2a-9506-4cf5-bbdb-009558b79e29\",\"sourceId\":null},{\"created\":1535603044000,\"modified\":1535603044000,\"onboarded\":1535603044000,\"revisionId\":\"f6b577a1-1849-4965-b77e-2ea11ab0b327\",\"version\":\"1\",\"description\":null,\"metadata\":null,\"origin\":null,\"accessTypeCode\":\"PB\",\"validationStatusCode\":\"IP\",\"authors\":[],\"publisher\":null,\"solutionId\":\"f226cc60-c2ec-4c2b-b05c-4a521f77e077\",\"userId\":\"bc961e2a-9506-4cf5-bbdb-009558b79e29\",\"sourceId\":null}]")));

		stubFor(get(urlEqualTo(
				"/ccds/thread/solution/f226cc60-c2ec-4c2b-b05c-4a521f77e077/revision/02c5f263-c612-4bd2-abaa-d12ccc0d2476/comment/count"))
						.willReturn(aResponse().withStatus(HttpStatus.SC_OK)
								.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())
								.withBody("{\"count\":0}")));

		stubFor(get(urlEqualTo(
				"/ccds/task/search?solutionId=f226cc60-c2ec-4c2b-b05c-4a521f77e077&_j=a&page=0&size=5&sort=created,DESC"))
						.willReturn(aResponse().withStatus(HttpStatus.SC_OK)
								.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString()).withBody(
										"{\"content\":[{\"taskId\":5,\"trackingId\":\"fd1ea3aa-5a91-454a-9f8b-87c674e25417\",\"taskCode\":\"OB\",\"solutionId\":\"f226cc60-c2ec-4c2b-b05c-4a521f77e077\",\"revisionId\":\"02c5f263-c612-4bd2-abaa-d12ccc0d2476\",\"userId\":\"bc961e2a-9506-4cf5-bbdb-009558b79e29\",\"name\":\"AddDockerImage\",\"statusCode\":\"SU\",\"created\":1535603254000,\"modified\":1535603254000}],\"last\":false,\"totalPages\":30,\"totalElements\":30,\"size\":1,\"number\":0,\"sort\":[{\"direction\":\"DESC\",\"property\":\"startDate\",\"ignoreCase\":false,\"nullHandling\":\"NATIVE\",\"ascending\":false,\"descending\":true}],\"numberOfElements\":1,\"first\":true}")));

		stubFor(get(urlEqualTo("/ccds/task/5/stepresult")).willReturn(aResponse()
				.withStatus(HttpStatus.SC_OK)
				.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString()).withBody(
						"[{\"stepResultId\":28,\"taskId\":5,\"name\":\"AddDockerImage\",\"statusCode\":\"SU\",\"result\":\"Add Artifact - image for solution - f226cc60-c2ec-4c2b-b05c-4a521f77e077 Successful\",\"startDate\":1535603254000,\"endDate\":1535603254000}]")));

		stubFor(get(urlEqualTo(
				"/ccds/pubreq/search?revisionId=02c5f263-c612-4bd2-abaa-d12ccc0d2476&solutionId=f226cc60-c2ec-4c2b-b05c-4a521f77e077&_j=a&statusCode=PE&page=0&size=1"))
						.willReturn(aResponse().withStatus(HttpStatus.SC_OK)
								.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString()).withBody(
										"{\"content\":[],\"last\":true,\"totalPages\":0,\"totalElements\":0,\"size\":1,\"number\":0,\"sort\":null,\"numberOfElements\":0,\"first\":true}")));

		stubFor(get(urlEqualTo("/ccds/catalog/solution/f226cc60-c2ec-4c2b-b05c-4a521f77e077"))
				.willReturn(aResponse().withStatus(HttpStatus.SC_OK)
						.withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
						.withBody("[{\"created\": \"2019-04-05T20:47:03Z\","
								+ "\"modified\": \"2019-04-05T20:47:03Z\","
								+ "\"catalogId\": \"12345678-abcd-90ab-cdef-1234567890ab\","
								+ "\"accessTypeCode\": \"PB\"," + "\"selfPublish\": false,"
								+ "\"name\": \"Test catalog\"," + "\"publisher\": \"Acumos\","
								+ "\"description\": null," + "\"origin\": null,"
								+ "\"url\": \"http://localhost\"}]")));

		stubFor(get(urlEqualTo("/ccds/catalog/search?accessTypeCode=PB&_j=a&page=0&size=1000"))
				.willReturn(aResponse().withStatus(HttpStatus.SC_OK)
						.withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
						.withBody("{\"content\":[" + "{\"accessTypeCode\": \"PB\","
								+ "\"catalogId\": \"12345678-abcd-90ab-cdef-1234567890ab\","
								+ "\"created\": \"2018-12-16T12:34:56.789Z\","
								+ "\"description\": \"A catalog of test models\","
								+ "\"modified\": \"2018-12-16T12:34:56.789Z\"," + "\"name\": \"Test Catalog\","
								+ "\"origin\": \"http://test.acumos.org/api\"," + "\"publisher\": \"Acumos\","
								+ "\"url\": \"http://test.company.com/api\"}]," + "\"last\":true,"
								+ "\"totalPages\":1," + "\"totalElements\":1," + "\"size\":9," + "\"number\":0,"
								+ "\"sort\":[{\"direction\":\"DESC\"," + "\"property\":\"modified\","
								+ "\"ignoreCase\":false," + "\"nullHandling\":\"NATIVE\"," + "\"ascending\":false,"
								+ "\"descending\":true}]," + "\"numberOfElements\":1," + "\"first\":true}")));

		stubFor(get(urlEqualTo("/ccds/catalog/12345678-abcd-90ab-cdef-1234567890ab/solution/count"))
				.willReturn(aResponse().withStatus(HttpStatus.SC_OK)
						.withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE).withBody("1")));

		stubFor(get(urlEqualTo(
				"/ccds/pubreq/search?solutionId=f226cc60-c2ec-4c2b-b05c-4a521f77e077&_j=a&page=0&size=1"))
						.willReturn(aResponse().withStatus(HttpStatus.SC_OK)
								.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString()).withBody(
										"{\"content\":[],\"last\":true,\"totalPages\":0,\"totalElements\":0,\"size\":1,\"number\":0,\"sort\":null,\"numberOfElements\":0,\"first\":true}")));

		HttpHeaders headers = new HttpHeaders();
		HttpEntity<JsonRequest<RestPageRequestPortal>> requestEntity =
				new HttpEntity<>(reqObj, headers);

		ResponseEntity<JsonResponse<RestPageResponseBE<MLSolution>>> solutionResponse =
				restTemplate.exchange(host + ":" + randomServerPort + "/portal/solutions/public",
						HttpMethod.POST, requestEntity,
						new ParameterizedTypeReference<JsonResponse<RestPageResponseBE<MLSolution>>>() {
						});

		assertNotNull(solutionResponse);
		assertEquals(HttpServletResponse.SC_OK, solutionResponse.getStatusCode().value());
		List<MLSolution> mlSolutionList = solutionResponse.getBody().getResponseBody().getContent();
		assertEquals("f226cc60-c2ec-4c2b-b05c-4a521f77e077", mlSolutionList.get(0).getSolutionId());
	}

	@Test
	public void findPortalUserSolutionTest() {

		JsonRequest<RestPageRequestPortal> reqObj = new JsonRequest<>();
		RestPageRequestPortal restpagerequestPortal = new RestPageRequestPortal();
		restpagerequestPortal.setActive(true);
		restpagerequestPortal.setPublished(true);
		restpagerequestPortal.setSortBy("MR");
		restpagerequestPortal.setUserId("bc961e2a-9506-4cf5-bbdb-009558b79e29");
		Map<String, String> fieldToDirectionMap = new HashMap<>();
		RestPageRequest pageRequest = new RestPageRequest();
		pageRequest.setSize(9);
		pageRequest.setPage(0);
		fieldToDirectionMap.put("modified", "DESC");
		pageRequest.setFieldToDirectionMap(fieldToDirectionMap);
		restpagerequestPortal.setPageRequest(pageRequest);
		reqObj.setBody(restpagerequestPortal);

		stubFor(get(urlEqualTo(
				"/ccds/solution/search/user?active=true&publ=true&user=bc961e2a-9506-4cf5-bbdb-009558b79e29&page=0&size=9&sort=modified,DESC"))
						.willReturn(aResponse().withStatus(HttpStatus.SC_OK)
								.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString()).withBody(
										"{\"content\":[{\"created\":1535603044000,\"modified\":1536350829000,\"solutionId\":\"f226cc60-c2ec-4c2b-b05c-4a521f77e077\",\"name\":\"TestSolution\",\"metadata\":null,\"active\":true,\"modelTypeCode\":\"CL\",\"toolkitTypeCode\":\"TF\",\"origin\":null,\"userId\":\"bc961e2a-9506-4cf5-bbdb-009558b79e29\",\"sourceId\":null,\"tags\":[{\"tag\":\"Test\"}],\"viewCount\":12,\"downloadCount\":0,\"lastDownload\":1536364233000,\"ratingCount\":0,\"ratingAverageTenths\":0,\"featured\":false}],\"last\":true,\"totalPages\":1,\"totalElements\":1,\"size\":9,\"number\":0,\"sort\":[{\"direction\":\"DESC\",\"property\":\"modified\",\"ignoreCase\":false,\"nullHandling\":\"NATIVE\",\"ascending\":false,\"descending\":true}],\"numberOfElements\":1,\"first\":true}")));

		stubFor(get(urlEqualTo("/ccds/solution/f226cc60-c2ec-4c2b-b05c-4a521f77e077"))
				.willReturn(aResponse().withStatus(HttpStatus.SC_OK)
						.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString()).withBody(
								"{\"created\":1535603044000,\"modified\":1536350829000,\"solutionId\":\"f226cc60-c2ec-4c2b-b05c-4a521f77e077\",\"name\":\"TestSolution\",\"metadata\":null,\"active\":true,\"modelTypeCode\":\"CL\",\"toolkitTypeCode\":\"TF\",\"origin\":null,\"userId\":\"bc961e2a-9506-4cf5-bbdb-009558b79e29\",\"sourceId\":null,\"tags\":[{\"tag\":\"Test\"}],\"viewCount\":12,\"downloadCount\":0,\"lastDownload\":1536364233000,\"ratingCount\":0,\"ratingAverageTenths\":0,\"featured\":false}")));

		stubFor(get(urlEqualTo("/ccds/solution/f226cc60-c2ec-4c2b-b05c-4a521f77e077/tag"))
				.willReturn(aResponse().withStatus(HttpStatus.SC_OK)
						.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())
						.withBody("[{\"tag\":\"Test\"}]")));

		stubFor(get(urlEqualTo("/ccds/user/bc961e2a-9506-4cf5-bbdb-009558b79e29"))
				.willReturn(aResponse().withStatus(HttpStatus.SC_OK)
						.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString()).withBody(
								"{\"created\":1535602889000,\"modified\":1536623387000,\"userId\":\"bc961e2a-9506-4cf5-bbdb-009558b79e29\",\"firstName\":\"Test\",\"middleName\":null,\"lastName\":\"User\",\"orgName\":null,\"email\":\"testUser@gmail.com\",\"loginName\":\"test\",\"loginHash\":null,\"loginPassExpire\":null,\"authToken\":\"\",\"active\":true,\"lastLogin\":1536623387000,\"loginFailCount\":null,\"loginFailDate\":null,\"picture\":null,\"apiToken\":\"30d19b719c1d44ae84d92dcc87f5a1ad\",\"verifyTokenHash\":null,\"verifyExpiration\":null,\"tags\":[]}")));

		stubFor(get(urlEqualTo("/ccds/access/solution/f226cc60-c2ec-4c2b-b05c-4a521f77e077/user"))
				.willReturn(aResponse().withStatus(HttpStatus.SC_OK)
						.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString()).withBody("[]")));

		stubFor(get(urlEqualTo("/ccds/solution/f226cc60-c2ec-4c2b-b05c-4a521f77e077/revision"))
				.willReturn(aResponse().withStatus(HttpStatus.SC_OK)
						.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString()).withBody(
								"[{\"created\":1535603252000,\"modified\":1536354698000,\"onboarded\":1535603047000,\"revisionId\":\"02c5f263-c612-4bd2-abaa-d12ccc0d2476\",\"version\":\"2\",\"description\":null,\"metadata\":null,\"origin\":null,\"accessTypeCode\":\"OR\",\"validationStatusCode\":\"PS\",\"authors\":[],\"publisher\":\"Acumos\",\"solutionId\":\"f226cc60-c2ec-4c2b-b05c-4a521f77e077\",\"userId\":\"bc961e2a-9506-4cf5-bbdb-009558b79e29\",\"sourceId\":null},{\"created\":1535603044000,\"modified\":1535603044000,\"onboarded\":1535603044000,\"revisionId\":\"f6b577a1-1849-4965-b77e-2ea11ab0b327\",\"version\":\"1\",\"description\":null,\"metadata\":null,\"origin\":null,\"accessTypeCode\":\"PB\",\"validationStatusCode\":\"IP\",\"authors\":[],\"publisher\":null,\"solutionId\":\"f226cc60-c2ec-4c2b-b05c-4a521f77e077\",\"userId\":\"bc961e2a-9506-4cf5-bbdb-009558b79e29\",\"sourceId\":null}]")));

		stubFor(get(urlEqualTo(
				"/ccds/thread/solution/f226cc60-c2ec-4c2b-b05c-4a521f77e077/revision/02c5f263-c612-4bd2-abaa-d12ccc0d2476/comment/count"))
						.willReturn(aResponse().withStatus(HttpStatus.SC_OK)
								.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())
								.withBody("{\"count\":0}")));

		stubFor(get(urlEqualTo(
				"/ccds/task/search?solutionId=f226cc60-c2ec-4c2b-b05c-4a521f77e077&_j=a&page=0&size=5&sort=created,DESC"))
						.willReturn(aResponse().withStatus(HttpStatus.SC_OK)
								.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString()).withBody(
										"{\"content\":[{\"taskId\":5,\"trackingId\":\"fd1ea3aa-5a91-454a-9f8b-87c674e25417\",\"taskCode\":\"OB\",\"solutionId\":\"f226cc60-c2ec-4c2b-b05c-4a521f77e077\",\"revisionId\":\"02c5f263-c612-4bd2-abaa-d12ccc0d2476\",\"userId\":\"bc961e2a-9506-4cf5-bbdb-009558b79e29\",\"name\":\"AddDockerImage\",\"statusCode\":\"SU\",\"created\":1535603254000,\"modified\":1535603254000}],\"last\":false,\"totalPages\":30,\"totalElements\":30,\"size\":1,\"number\":0,\"sort\":[{\"direction\":\"DESC\",\"property\":\"startDate\",\"ignoreCase\":false,\"nullHandling\":\"NATIVE\",\"ascending\":false,\"descending\":true}],\"numberOfElements\":1,\"first\":true}")));

		stubFor(get(urlEqualTo("/ccds/task/5/stepresult")).willReturn(aResponse()
				.withStatus(HttpStatus.SC_OK)
				.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString()).withBody(
						"[{\"stepResultId\":28,\"taskId\":5,\"name\":\"AddDockerImage\",\"statusCode\":\"SU\",\"result\":\"Add Artifact - image for solution - f226cc60-c2ec-4c2b-b05c-4a521f77e077 Successful\",\"startDate\":1535603254000,\"endDate\":1535603254000}]")));

		stubFor(get(urlEqualTo(
				"/ccds/pubreq/search?revisionId=02c5f263-c612-4bd2-abaa-d12ccc0d2476&solutionId=f226cc60-c2ec-4c2b-b05c-4a521f77e077&_j=a&statusCode=PE&page=0&size=1"))
						.willReturn(aResponse().withStatus(HttpStatus.SC_OK)
								.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString()).withBody(
										"{\"content\":[],\"last\":true,\"totalPages\":0,\"totalElements\":0,\"size\":1,\"number\":0,\"sort\":null,\"numberOfElements\":0,\"first\":true}")));

		stubFor(get(urlEqualTo("/ccds/catalog/solution/f226cc60-c2ec-4c2b-b05c-4a521f77e077"))
				.willReturn(aResponse().withStatus(HttpStatus.SC_OK)
						.withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
						.withBody("[{\"created\": \"2019-04-05T20:47:03Z\","
								+ "\"modified\": \"2019-04-05T20:47:03Z\","
								+ "\"catalogId\": \"12345678-abcd-90ab-cdef-1234567890ab\","
								+ "\"accessTypeCode\": \"PB\"," + "\"selfPublish\": false,"
								+ "\"name\": \"Test catalog\"," + "\"publisher\": \"Acumos\","
								+ "\"description\": null," + "\"origin\": null,"
								+ "\"url\": \"http://localhost\"}]")));

		stubFor(get(urlEqualTo(
				"/ccds/pubreq/search?solutionId=f226cc60-c2ec-4c2b-b05c-4a521f77e077&_j=a&page=0&size=1"))
						.willReturn(aResponse().withStatus(HttpStatus.SC_OK)
								.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString()).withBody(
										"{\"content\":[],\"last\":true,\"totalPages\":0,\"totalElements\":0,\"size\":1,\"number\":0,\"sort\":null,\"numberOfElements\":0,\"first\":true}")));

		HttpHeaders headers = new HttpHeaders();
		HttpEntity<JsonRequest<RestPageRequestPortal>> requestEntity =
				new HttpEntity<>(reqObj, headers);

		ResponseEntity<JsonResponse<RestPageResponseBE<MLSolution>>> solutionResponse = restTemplate
				.exchange(host + ":" + randomServerPort + "/user/solutions", HttpMethod.POST, requestEntity,
						new ParameterizedTypeReference<JsonResponse<RestPageResponseBE<MLSolution>>>() {
						});

		assertNotNull(solutionResponse);
		assertEquals(HttpServletResponse.SC_OK, solutionResponse.getStatusCode().value());

		List<MLSolution> mlSolutionList = solutionResponse.getBody().getResponseBody().getContent();
		assertEquals("f226cc60-c2ec-4c2b-b05c-4a521f77e077", mlSolutionList.get(0).getSolutionId());

	}

	@Test
	public void searchSolutionByKwAndTagsTest() {

		JsonRequest<RestPageRequestPortal> reqObj = new JsonRequest<>();
		RestPageRequestPortal restpagerequestPortal = new RestPageRequestPortal();
		restpagerequestPortal.setNameKeyword(new String[] {"Test"});
		restpagerequestPortal.setActive(true);
		restpagerequestPortal.setPublished(true);
		restpagerequestPortal.setSortBy("MR");
		restpagerequestPortal.setUserId("bc961e2a-9506-4cf5-bbdb-009558b79e29");
		Map<String, String> fieldToDirectionMap = new HashMap<>();
		RestPageRequest pageRequest = new RestPageRequest();
		pageRequest.setSize(9);
		pageRequest.setPage(0);
		fieldToDirectionMap.put("modified", "DESC");
		pageRequest.setFieldToDirectionMap(fieldToDirectionMap);
		restpagerequestPortal.setPageRequest(pageRequest);
		reqObj.setBody(restpagerequestPortal);

		stubFor(get(urlEqualTo(
				"/ccds/solution/search/portal/kwtag?active=true&kw=Test&page=0&size=9&sort=modified,DESC"))
						.willReturn(aResponse().withStatus(HttpStatus.SC_OK)
								.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString()).withBody(
										"{\"content\":[{\"created\":1535603044000,\"modified\":1536350829000,\"solutionId\":\"f226cc60-c2ec-4c2b-b05c-4a521f77e077\",\"name\":\"TestSolution\",\"metadata\":null,\"active\":true,\"modelTypeCode\":\"CL\",\"toolkitTypeCode\":\"TF\",\"origin\":null,\"userId\":\"bc961e2a-9506-4cf5-bbdb-009558b79e29\",\"sourceId\":null,\"tags\":[{\"tag\":\"Test\"}],\"viewCount\":12,\"downloadCount\":0,\"lastDownload\":1536364233000,\"ratingCount\":0,\"ratingAverageTenths\":0,\"featured\":false}],\"last\":true,\"totalPages\":1,\"totalElements\":1,\"size\":9,\"number\":0,\"sort\":[{\"direction\":\"DESC\",\"property\":\"modified\",\"ignoreCase\":false,\"nullHandling\":\"NATIVE\",\"ascending\":false,\"descending\":true}],\"numberOfElements\":1,\"first\":true}")));

		stubFor(get(urlEqualTo("/ccds/solution/f226cc60-c2ec-4c2b-b05c-4a521f77e077"))
				.willReturn(aResponse().withStatus(HttpStatus.SC_OK)
						.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString()).withBody(
								"{\"created\":1535603044000,\"modified\":1536350829000,\"solutionId\":\"f226cc60-c2ec-4c2b-b05c-4a521f77e077\",\"name\":\"TestSolution\",\"metadata\":null,\"active\":true,\"modelTypeCode\":\"CL\",\"toolkitTypeCode\":\"TF\",\"origin\":null,\"userId\":\"bc961e2a-9506-4cf5-bbdb-009558b79e29\",\"sourceId\":null,\"tags\":[{\"tag\":\"Test\"}],\"viewCount\":12,\"downloadCount\":0,\"lastDownload\":1536364233000,\"ratingCount\":0,\"ratingAverageTenths\":0,\"featured\":false}")));

		stubFor(get(urlEqualTo("/ccds/solution/f226cc60-c2ec-4c2b-b05c-4a521f77e077/tag"))
				.willReturn(aResponse().withStatus(HttpStatus.SC_OK)
						.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())
						.withBody("[{\"tag\":\"Test\"}]")));

		stubFor(get(urlEqualTo("/ccds/user/bc961e2a-9506-4cf5-bbdb-009558b79e29"))
				.willReturn(aResponse().withStatus(HttpStatus.SC_OK)
						.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString()).withBody(
								"{\"created\":1535602889000,\"modified\":1536623387000,\"userId\":\"bc961e2a-9506-4cf5-bbdb-009558b79e29\",\"firstName\":\"Test\",\"middleName\":null,\"lastName\":\"User\",\"orgName\":null,\"email\":\"testUser@gmail.com\",\"loginName\":\"test\",\"loginHash\":null,\"loginPassExpire\":null,\"authToken\":\"\",\"active\":true,\"lastLogin\":1536623387000,\"loginFailCount\":null,\"loginFailDate\":null,\"picture\":null,\"apiToken\":\"30d19b719c1d44ae84d92dcc87f5a1ad\",\"verifyTokenHash\":null,\"verifyExpiration\":null,\"tags\":[]}")));

		stubFor(get(urlEqualTo("/ccds/access/solution/f226cc60-c2ec-4c2b-b05c-4a521f77e077/user"))
				.willReturn(aResponse().withStatus(HttpStatus.SC_OK)
						.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString()).withBody("[]")));

		stubFor(get(urlEqualTo("/ccds/solution/f226cc60-c2ec-4c2b-b05c-4a521f77e077/revision"))
				.willReturn(aResponse().withStatus(HttpStatus.SC_OK)
						.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString()).withBody(
								"[{\"created\":1535603252000,\"modified\":1536354698000,\"onboarded\":1535603047000,\"revisionId\":\"02c5f263-c612-4bd2-abaa-d12ccc0d2476\",\"version\":\"2\",\"description\":null,\"metadata\":null,\"origin\":null,\"accessTypeCode\":\"OR\",\"validationStatusCode\":\"PS\",\"authors\":[],\"publisher\":\"Acumos\",\"solutionId\":\"f226cc60-c2ec-4c2b-b05c-4a521f77e077\",\"userId\":\"bc961e2a-9506-4cf5-bbdb-009558b79e29\",\"sourceId\":null},{\"created\":1535603044000,\"modified\":1535603044000,\"onboarded\":1535603044000,\"revisionId\":\"f6b577a1-1849-4965-b77e-2ea11ab0b327\",\"version\":\"1\",\"description\":null,\"metadata\":null,\"origin\":null,\"accessTypeCode\":\"PB\",\"validationStatusCode\":\"IP\",\"authors\":[],\"publisher\":null,\"solutionId\":\"f226cc60-c2ec-4c2b-b05c-4a521f77e077\",\"userId\":\"bc961e2a-9506-4cf5-bbdb-009558b79e29\",\"sourceId\":null}]")));

		stubFor(get(urlEqualTo(
				"/ccds/thread/solution/f226cc60-c2ec-4c2b-b05c-4a521f77e077/revision/02c5f263-c612-4bd2-abaa-d12ccc0d2476/comment/count"))
						.willReturn(aResponse().withStatus(HttpStatus.SC_OK)
								.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())
								.withBody("{\"count\":0}")));

		stubFor(get(urlEqualTo(
				"/ccds/task/search?solutionId=f226cc60-c2ec-4c2b-b05c-4a521f77e077&_j=a&page=0&size=5&sort=created,DESC"))
						.willReturn(aResponse().withStatus(HttpStatus.SC_OK)
								.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString()).withBody(
										"{\"content\":[{\"taskId\":5,\"trackingId\":\"fd1ea3aa-5a91-454a-9f8b-87c674e25417\",\"taskCode\":\"OB\",\"solutionId\":\"f226cc60-c2ec-4c2b-b05c-4a521f77e077\",\"revisionId\":\"02c5f263-c612-4bd2-abaa-d12ccc0d2476\",\"userId\":\"bc961e2a-9506-4cf5-bbdb-009558b79e29\",\"name\":\"AddDockerImage\",\"statusCode\":\"SU\",\"created\":1535603254000,\"modified\":1535603254000}],\"last\":false,\"totalPages\":30,\"totalElements\":30,\"size\":1,\"number\":0,\"sort\":[{\"direction\":\"DESC\",\"property\":\"startDate\",\"ignoreCase\":false,\"nullHandling\":\"NATIVE\",\"ascending\":false,\"descending\":true}],\"numberOfElements\":1,\"first\":true}")));

		stubFor(get(urlEqualTo("/ccds/task/5/stepresult")).willReturn(aResponse()
				.withStatus(HttpStatus.SC_OK)
				.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString()).withBody(
						"[{\"stepResultId\":28,\"taskId\":5,\"name\":\"AddDockerImage\",\"statusCode\":\"SU\",\"result\":\"Add Artifact - image for solution - f226cc60-c2ec-4c2b-b05c-4a521f77e077 Successful\",\"startDate\":1535603254000,\"endDate\":1535603254000}]")));

		stubFor(get(urlEqualTo(
				"/ccds/pubreq/search?solutionId=f226cc60-c2ec-4c2b-b05c-4a521f77e077&_j=a&page=0&size=1"))
						.willReturn(aResponse().withStatus(HttpStatus.SC_OK)
								.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString()).withBody(
										"{\"content\":[],\"last\":true,\"totalPages\":0,\"totalElements\":0,\"size\":1,\"number\":0,\"sort\":null,\"numberOfElements\":0,\"first\":true}")));

		stubFor(get(urlEqualTo("/ccds/catalog/solution/f226cc60-c2ec-4c2b-b05c-4a521f77e077"))
				.willReturn(aResponse().withStatus(HttpStatus.SC_OK)
						.withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
						.withBody("[{\"created\": \"2019-04-05T20:47:03Z\","
								+ "\"modified\": \"2019-04-05T20:47:03Z\","
								+ "\"catalogId\": \"12345678-abcd-90ab-cdef-1234567890ab\","
								+ "\"accessTypeCode\": \"PB\"," + "\"selfPublish\": false,"
								+ "\"name\": \"Test catalog\"," + "\"publisher\": \"Acumos\","
								+ "\"description\": null," + "\"origin\": null,"
								+ "\"url\": \"http://localhost\"}]")));

		HttpHeaders headers = new HttpHeaders();
		HttpEntity<JsonRequest<RestPageRequestPortal>> requestEntity =
				new HttpEntity<>(reqObj, headers);

		ResponseEntity<JsonResponse<RestPageResponseBE<MLSolution>>> solutionResponse =
				restTemplate.exchange(host + ":" + randomServerPort + "/searchSolutionBykeyword",
						HttpMethod.POST, requestEntity,
						new ParameterizedTypeReference<JsonResponse<RestPageResponseBE<MLSolution>>>() {
						});

		assertNotNull(solutionResponse);
		assertEquals(HttpServletResponse.SC_OK, solutionResponse.getStatusCode().value());

		List<MLSolution> mlSolutionList = solutionResponse.getBody().getResponseBody().getContent();
		assertEquals("f226cc60-c2ec-4c2b-b05c-4a521f77e077", mlSolutionList.get(0).getSolutionId());

	}

	@Test
	public void addDocumentTest() {

		String solutionId = "b7b9bb9c-980c-4a18-b7bf-545bbd9173ab";
		String revisionId = "4f5079b9-49e8-48a3-8fcb-c006e96c4c10";
		String catalogId = "4321-4321-4321-4321-4321";
		String ccdsRevCatDocPath =
				String.format("/ccds/revision/%s/catalog/%s/document", revisionId, catalogId);
		String fileName = "upload-test-file";

		stubFor(get(urlEqualTo(ccdsRevCatDocPath)).willReturn(aResponse().withStatus(HttpStatus.SC_OK)
				.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString()).withBody("[]")));

		stubFor(put(urlEqualTo(
				"/repository/repo_acumos_model_maven/com/artifact/" + solutionId + "/" + revisionId + "/"
						+ fileName + "/" + catalogId + "/" + fileName + "-" + catalogId + ".txt"))
								.willReturn(aResponse().withStatus(HttpStatus.SC_OK)));

		stubFor(post(urlEqualTo("/ccds/document")).willReturn(aResponse().withStatus(HttpStatus.SC_OK)
				.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString()).withBody(
						"{\"documentId\":\"787c9461-4288-4091-8d39-5ce1a4e04e34\",\"name\":\"upload-test-file4958107523126401268.txt\",\"version\":null,\"uri\":\"com/artifact/b7b9bb9c-980c-4a18-b7bf-545bbd9173ab/4f5079b9-49e8-48a3-8fcb-c006e96c4c10/upload-test-file4958107523126401268/PB/upload-test-file4958107523126401268-PB.txt\",\"size\":32,\"userId\":\"bc961e2a-9506-4cf5-bbdb-009558b79e29\"}")));

		stubFor(post(urlEqualTo(ccdsRevCatDocPath + "/787c9461-4288-4091-8d39-5ce1a4e04e34"))
				.willReturn(aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type",
						MediaType.APPLICATION_JSON.toString())));

		MLPDocument document = null;
		try {

			MockMultipartFile mockMultiPartFile =
					new MockMultipartFile("file", fileName + ".txt", "text/plain", "success".getBytes());

					final MvcResult mvcResult = mvc
					.perform(MockMvcRequestBuilders.multipart(
							"/solution/" + solutionId + "/revision/" + revisionId
					+ "/" + catalogId + "/document")
													.file(mockMultiPartFile))
													.andExpect(MockMvcResultMatchers.request().asyncStarted()).andReturn();
										;

				 	ResultActions result =	mvc.perform(asyncDispatch(mvcResult)).andExpect(status().isOk())
											.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));

						String contentAsString = result.andReturn().getResponse().getContentAsString();
						JsonResponse<MLPDocument> documentResponse = objectMapper.readValue(contentAsString,
							new TypeReference<JsonResponse<MLPDocument>>(){});
							document = documentResponse.getResponseBody();

		} catch (Exception e) {
			Assert.fail("Expect document to be returned: " + e.getMessage());
		}
		assertNotNull(document);
		assertEquals("787c9461-4288-4091-8d39-5ce1a4e04e34", document.getDocumentId());
	}


	@Test
	public void getDocumentTest() {

		String solutionId = "b7b9bb9c-980c-4a18-b7bf-545bbd9173ab";
		String revisionId = "4f5079b9-49e8-48a3-8fcb-c006e96c4c10";
		String catalogId = "4321-4321-4321-4321-4321";
		String ccdsRevCatDocPath =
				String.format("/ccds/revision/%s/catalog/%s/document", revisionId, catalogId);

		Path tempFile = null;
		try {
			tempFile = Files.createTempFile("upload-test-file", ".txt");
			Files.write(tempFile, "some test content...\nline1\nline2".getBytes());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
		}
		File file = tempFile.toFile();
		MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
		body.add("file", new FileSystemResource(file));
		String fileName = FilenameUtils.getBaseName(file.getName());

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

		stubFor(get(urlEqualTo(ccdsRevCatDocPath)).willReturn(aResponse().withStatus(HttpStatus.SC_OK)
				.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString()).withBody("[]")));

		stubFor(put(urlEqualTo(
				"/repository/repo_acumos_model_maven/com/artifact/" + solutionId + "/" + revisionId + "/"
						+ fileName + "/" + catalogId + "/" + fileName + "-" + catalogId + ".txt"))
								.willReturn(aResponse().withStatus(HttpStatus.SC_OK)));

		stubFor(post(urlEqualTo("/ccds/document")).willReturn(aResponse().withStatus(HttpStatus.SC_OK)
				.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString()).withBody(
						"{\"documentId\":\"787c9461-4288-4091-8d39-5ce1a4e04e34\",\"name\":\"upload-test-file4958107523126401268.txt\",\"version\":null,\"uri\":\"com/artifact/b7b9bb9c-980c-4a18-b7bf-545bbd9173ab/4f5079b9-49e8-48a3-8fcb-c006e96c4c10/upload-test-file4958107523126401268/PB/upload-test-file4958107523126401268-PB.txt\",\"size\":32,\"userId\":\"bc961e2a-9506-4cf5-bbdb-009558b79e29\"}")));

		stubFor(post(urlEqualTo(ccdsRevCatDocPath + "/787c9461-4288-4091-8d39-5ce1a4e04e34"))
				.willReturn(aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type",
						MediaType.APPLICATION_JSON.toString())));
		// /solution/{solutionId}/revision/{revisionId}/{accessType}/document
		ResponseEntity<JsonResponse<List<MLPDocument>>> documentResponse = null;
		try {
			documentResponse = restTemplate.exchange(
					host + ":" + randomServerPort + "/solution/" + solutionId + "/revision/" + revisionId
							+ "/" + catalogId + "/document",
					HttpMethod.GET, null, new ParameterizedTypeReference<JsonResponse<List<MLPDocument>>>() {
					});
		} catch (HttpStatusCodeException e) {
			e.printStackTrace();
		}
		List<MLPDocument> documentList = documentResponse.getBody().getResponseBody();
		assertNotNull(documentList);
		assertEquals(HttpServletResponse.SC_OK, documentResponse.getStatusCode().value());
	}


	@Test
	public void getPreferedTagListTest() {
		stubFor(get(urlEqualTo("/ccds/tag")).willReturn(aResponse().withStatus(HttpStatus.SC_OK)
				.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())
				.withBody("{\r\n" + "  \"content\": [\r\n" + "    {\r\n" + "      \"tag\": \"Test\"\r\n"
						+ "    }\r\n" + "  ],\r\n" + "  \"last\": true,\r\n" + "  \"totalPages\": 1,\r\n"
						+ "  \"totalElements\": 1,\r\n" + "  \"size\": 100,\r\n" + "  \"number\": 0,\r\n"
						+ "  \"sort\": null,\r\n" + "  \"numberOfElements\": 1,\r\n" + "  \"first\": true\r\n"
						+ "}")));

		stubFor(
				get(urlEqualTo("/ccds/user/bc961e2a-9506-4cf5-bbdb-009558b79e29"))
						.willReturn(aResponse().withStatus(HttpStatus.SC_OK)
								.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())
								.withBody("{\r\n" + "  \"created\": 1538588099000,\r\n"
										+ "  \"modified\": 1538674913000,\r\n"
										+ "  \"userId\": \"bc961e2a-9506-4cf5-bbdb-009558b79e29\",\r\n"
										+ "  \"firstName\": \"Test\",\r\n" + "  \"middleName\": null,\r\n"
										+ "  \"lastName\": \"User\",\r\n" + "  \"orgName\": null,\r\n"
										+ "  \"email\": \"test@gmail.com\",\r\n" + "  \"loginName\": \"test\",\r\n"
										+ "  \"loginHash\": null,\r\n" + "  \"loginPassExpire\": null,\r\n"
										+ "  \"active\": true,\r\n" + "  \"lastLogin\": 1538674913000,\r\n"
										+ "  \"loginFailCount\": null,\r\n" + "  \"loginFailDate\": null,\r\n"
										+ "  \"picture\": null,\r\n"
										+ "  \"apiToken\": \"013556751e28443d9997cbbaf992e39b\",\r\n"
										+ "  \"verifyTokenHash\": null,\r\n" + "  \"verifyExpiration\": null,\r\n"
										+ "  \"tags\": [\r\n" + "    {\r\n" + "      \"tag\": \"Test\"\r\n"
										+ "    }\r\n" + "  ]\r\n" + "}")));

		RestPageRequest reqObj = new RestPageRequest();
		JsonRequest<RestPageRequest> jsonRequest = new JsonRequest<>();
		jsonRequest.setBody(reqObj);
		HttpHeaders headers = new HttpHeaders();
		HttpEntity<JsonRequest<RestPageRequest>> requestEntity = new HttpEntity<>(jsonRequest, headers);

		ResponseEntity<JsonResponse<RestPageResponseBE>> preferedTagsList = restTemplate.exchange(
				host + ":" + randomServerPort + "/preferredTags/bc961e2a-9506-4cf5-bbdb-009558b79e29",
				HttpMethod.PUT, requestEntity,
				new ParameterizedTypeReference<JsonResponse<RestPageResponseBE>>() {
				});

		assertNotNull(preferedTagsList);
		assertEquals(HttpServletResponse.SC_OK, preferedTagsList.getStatusCode().value());
	}

}
