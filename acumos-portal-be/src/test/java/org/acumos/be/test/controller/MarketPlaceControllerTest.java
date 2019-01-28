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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.acumos.cds.domain.MLPDocument;
import org.acumos.cds.transport.RestPageRequest;
import org.acumos.portal.be.common.ConfigConstants;
import org.acumos.portal.be.common.JsonRequest;
import org.acumos.portal.be.common.JsonResponse;
import org.acumos.portal.be.common.RestPageRequestBE;
import org.acumos.portal.be.common.RestPageResponseBE;
import org.acumos.portal.be.transport.Author;
import org.acumos.portal.be.transport.MLSolution;
import org.acumos.portal.be.transport.RestPageRequestPortal;
import org.acumos.portal.be.transport.RevisionDescription;
import org.apache.commons.io.FilenameUtils;
import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
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
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import com.github.tomakehurst.wiremock.http.Fault;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = org.acumos.portal.be.Application.class, webEnvironment = WebEnvironment.RANDOM_PORT, properties = {
		ConfigConstants.portal_feature_email + "=true",
		ConfigConstants.portal_feature_email_from + "=support@acumos.org",
		ConfigConstants.portal_feature_email_service + "=smtp", ConfigConstants.spring_mail_host + "=localhost",
		ConfigConstants.spring_mail_port + "=10000", ConfigConstants.spring_mail_username + "=Test@test.com",
		ConfigConstants.spring_mail_password + "=Test", ConfigConstants.spring_mail_smtp_starttls_enable + "=true",
		ConfigConstants.spring_mail_smtp_auth + "=false", ConfigConstants.spring_mail_debug + "=true",
		ConfigConstants.spring_mail_transport_protocol + "=smtp",
		ConfigConstants.spring_mail_template_folder_path + "=/fmtemplates/",
		ConfigConstants.cdms_client_url + "=http://localhost:8000/ccds",
		ConfigConstants.cdms_client_username + "=ccds_test", ConfigConstants.cdms_client_password + "=ccds_test",
		"nexus.url=http://localhost:8000/repository/repo_acumos_model_maven/", "nexus.username=foo",
		"nexus.password=bar", "nexus.groupId=com.artifact", "document.size=100000" })
@EnableAutoConfiguration(exclude = { SecurityAutoConfiguration.class })
public class MarketPlaceControllerTest {

	@Rule
	public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().port(8000));

	/*
	 * @Rule public WireMockRule wireMockRule2 = new
	 * WireMockRule(wireMockConfig().port(8084));
	 */

	private RestTemplate restTemplate = new RestTemplate();

	@LocalServerPort
	int randomServerPort;

	private final String host = "http://localhost";

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
				host + ":" + randomServerPort + "/solution/" + solutionId + "/revision/" + revisionId + "/authors",
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
					host + ":" + randomServerPort + "/solution/" + solutionId + "/revision/" + revisionId + "/authors",
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
					host + ":" + randomServerPort + "/solution/" + solutionId + "/revision/" + revisionId + "/authors",
					HttpMethod.PUT, requestEntity, new ParameterizedTypeReference<JsonResponse<List<Author>>>() {
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
				host + ":" + randomServerPort + "/solution/" + solutionId + "/revision/" + revisionId + "/authors",
				HttpMethod.PUT, requestEntity, new ParameterizedTypeReference<JsonResponse<List<Author>>>() {
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
				host + ":" + randomServerPort + "/solution/" + solutionId + "/revision/" + revisionId + "/removeAuthor",
				HttpMethod.PUT, requestEntity, new ParameterizedTypeReference<JsonResponse<List<Author>>>() {
				});

		assertNotNull(authorResponse);
		assertEquals(HttpServletResponse.SC_OK, authorResponse.getStatusCode().value());
		List<Author> authorList = authorResponse.getBody().getResponseBody();
		assertEquals(0, authorList.size());
	}

	@Test
	public void getDescriptionTest() {

		String solutionId = "b7b9bb9c-980c-4a18-b7bf-545bbd9173ab";
		String revisionId = "4f5079b9-49e8-48a3-8fcb-c006e96c4c10";
		String accessType = "PR";

		stubFor(get(urlEqualTo("/ccds/revision/4f5079b9-49e8-48a3-8fcb-c006e96c4c10/access/PR/descr"))
				.willReturn(aResponse().withStatus(HttpStatus.SC_OK)
						.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())
						.withBody("{\r\n" + "\"accessTypeCode\": \"PR\"," + "\"created\": \"2018-09-10T16:00:39.629Z\","
								+ "\"description\": \"TestDescription\","
								+ "\"modified\": \"2018-09-10T16:00:39.629Z\","
								+ "\"revisionId\": \"4f5079b9-49e8-48a3-8fcb-c006e96c4c10\"" + "}")));

		ResponseEntity<JsonResponse<RevisionDescription>> descriptionResponse = restTemplate.exchange(
				host + ":" + randomServerPort + "/solution/revision/" + revisionId + "/" + accessType + "/description",
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
		String accessType = "PR";

		stubFor(get(urlEqualTo("/ccds/revision/4f5079b9-49e8-48a3-8fcb-c006e96c4c10/access/PR/descr"))
				.willReturn(aResponse().withStatus(HttpStatus.SC_BAD_REQUEST).withHeader("Content-Type",
						MediaType.APPLICATION_JSON.toString())));

		try {
			ResponseEntity<JsonResponse<RevisionDescription>> descriptionResponse = restTemplate.exchange(
					host + ":" + randomServerPort + "/solution/revision/" + revisionId + "/" + accessType
							+ "/description",
					HttpMethod.GET, null, new ParameterizedTypeReference<JsonResponse<RevisionDescription>>() {
					});
		} catch (HttpClientErrorException e) {
			assertEquals(HttpServletResponse.SC_BAD_REQUEST, e.getStatusCode().value());
		}
	}

	@Test
	public void addUpdateDescriptionTest() {

		String solutionId = "b7b9bb9c-980c-4a18-b7bf-545bbd9173ab";
		String revisionId = "4f5079b9-49e8-48a3-8fcb-c006e96c4c10";
		String accessType = "PR";
		RevisionDescription newRevisionDescription = new RevisionDescription();
		newRevisionDescription.setDescription("New Description");
		JsonRequest<RevisionDescription> reqObj = new JsonRequest<>();
		reqObj.setBody(newRevisionDescription);

		stubFor(get(urlEqualTo("/ccds/revision/4f5079b9-49e8-48a3-8fcb-c006e96c4c10/access/PR/descr"))
				.willReturn(aResponse().withStatus(HttpStatus.SC_BAD_REQUEST).withHeader("Content-Type",
						MediaType.APPLICATION_JSON.toString())));

		stubFor(post(urlEqualTo("/ccds/revision/4f5079b9-49e8-48a3-8fcb-c006e96c4c10/access/PR/descr"))
				.willReturn(aResponse().withStatus(HttpStatus.SC_OK)
						.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())
						.withBody("{\r\n" + "\"accessTypeCode\": \"PR\"," + "\"created\": \"2018-09-10T16:00:39.629Z\","
								+ "\"description\": \"New Description\","
								+ "\"modified\": \"2018-09-10T16:00:39.629Z\","
								+ "\"revisionId\": \"4f5079b9-49e8-48a3-8fcb-c006e96c4c10\"" + "}")));

		stubFor(get(urlEqualTo("/ccds/code/pair/ACCESS_TYPE")).willReturn(aResponse().withStatus(HttpStatus.SC_OK)
				.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())
				.withBody("[" + "  {" + "    \"code\": \"RS\"," + "    \"name\": \"Restricted\"" + "  }," + "  {"
						+ "    \"code\": \"PR\"," + "    \"name\": \"Private\"" + "  }," + "  {"
						+ "    \"code\": \"PB\"," + "    \"name\": \"Public\"" + "  }," + "  {"
						+ "    \"code\": \"OR\"," + "    \"name\": \"Organization\"" + "  }" + "]")));

		HttpHeaders headers = new HttpHeaders();
		HttpEntity<JsonRequest<RevisionDescription>> requestEntity = new HttpEntity<>(reqObj, headers);

		ResponseEntity<JsonResponse<RevisionDescription>> descriptionResponse = restTemplate.exchange(
				host + ":" + randomServerPort + "/solution/revision/" + revisionId + "/" + accessType + "/description",
				HttpMethod.POST, requestEntity, new ParameterizedTypeReference<JsonResponse<RevisionDescription>>() {
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
		restpagerequestPortal.setAccessTypeCodes(new String[] { "PB", "OR" });
		restpagerequestPortal.setActive(true);
		restpagerequestPortal.setSortBy("MR");
		Map<String, String> fieldToDirectionMap = new HashMap<>();
		RestPageRequest pageRequest = new RestPageRequest();
		pageRequest.setSize(9);
		pageRequest.setPage(0);
		fieldToDirectionMap.put("modified", "DESC");
		pageRequest.setFieldToDirectionMap(fieldToDirectionMap);
		restpagerequestPortal.setPageRequest(pageRequest);
		reqObj.setBody(restpagerequestPortal);

		stubFor(get(urlEqualTo("/ccds/code/pair/ACCESS_TYPE")).willReturn(aResponse().withStatus(HttpStatus.SC_OK)
				.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())
				.withBody("[" + "  {" + "    \"code\": \"RS\"," + "    \"name\": \"Restricted\"" + "  }," + "  {"
						+ "    \"code\": \"PR\"," + "    \"name\": \"Private\"" + "  }," + "  {"
						+ "    \"code\": \"PB\"," + "    \"name\": \"Public\"" + "  }," + "  {"
						+ "    \"code\": \"OR\"," + "    \"name\": \"Organization\"" + "  }" + "]")));

		stubFor(get(
				urlEqualTo("/ccds/solution/search/portal?atc=PB&atc=OR&active=true&page=0&size=9&sort=modified,DESC"))
						.willReturn(aResponse().withStatus(HttpStatus.SC_OK)
								.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString()).withBody(
										"{\"content\":[{\"created\":1535603044000,\"modified\":1536350829000,\"solutionId\":\"f226cc60-c2ec-4c2b-b05c-4a521f77e077\",\"name\":\"TestSolution\",\"metadata\":null,\"active\":true,\"modelTypeCode\":\"CL\",\"toolkitTypeCode\":\"TF\",\"origin\":null,\"userId\":\"bc961e2a-9506-4cf5-bbdb-009558b79e29\",\"sourceId\":null,\"tags\":[{\"tag\":\"Test\"}],\"viewCount\":12,\"downloadCount\":0,\"lastDownload\":1536364233000,\"ratingCount\":0,\"ratingAverageTenths\":0,\"featured\":false}],\"last\":true,\"totalPages\":1,\"totalElements\":1,\"size\":9,\"number\":0,\"sort\":[{\"direction\":\"DESC\",\"property\":\"modified\",\"ignoreCase\":false,\"nullHandling\":\"NATIVE\",\"ascending\":false,\"descending\":true}],\"numberOfElements\":1,\"first\":true}")));

		stubFor(get(urlEqualTo("/ccds/solution/f226cc60-c2ec-4c2b-b05c-4a521f77e077")).willReturn(aResponse()
				.withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())
				.withBody(
						"{\"created\":1535603044000,\"modified\":1536350829000,\"solutionId\":\"f226cc60-c2ec-4c2b-b05c-4a521f77e077\",\"name\":\"TestSolution\",\"metadata\":null,\"active\":true,\"modelTypeCode\":\"CL\",\"toolkitTypeCode\":\"TF\",\"origin\":null,\"userId\":\"bc961e2a-9506-4cf5-bbdb-009558b79e29\",\"sourceId\":null,\"tags\":[{\"tag\":\"Test\"}],\"viewCount\":12,\"downloadCount\":0,\"lastDownload\":1536364233000,\"ratingCount\":0,\"ratingAverageTenths\":0,\"featured\":false}")));

		stubFor(get(urlEqualTo("/ccds/solution/f226cc60-c2ec-4c2b-b05c-4a521f77e077/tag")).willReturn(aResponse()
				.withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())
				.withBody("[{\"tag\":\"Test\"}]")));

		stubFor(get(urlEqualTo("/ccds/user/bc961e2a-9506-4cf5-bbdb-009558b79e29")).willReturn(aResponse()
				.withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())
				.withBody(
						"{\"created\":1535602889000,\"modified\":1536623387000,\"userId\":\"bc961e2a-9506-4cf5-bbdb-009558b79e29\",\"firstName\":\"Test\",\"middleName\":null,\"lastName\":\"User\",\"orgName\":null,\"email\":\"testUser@gmail.com\",\"loginName\":\"test\",\"loginHash\":null,\"loginPassExpire\":null,\"authToken\":\"\",\"active\":true,\"lastLogin\":1536623387000,\"loginFailCount\":null,\"loginFailDate\":null,\"picture\":null,\"apiToken\":\"30d19b719c1d44ae84d92dcc87f5a1ad\",\"verifyTokenHash\":null,\"verifyExpiration\":null,\"tags\":[]}")));

		stubFor(get(urlEqualTo("/ccds/solution/f226cc60-c2ec-4c2b-b05c-4a521f77e077/user/access"))
				.willReturn(aResponse().withStatus(HttpStatus.SC_OK)
						.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString()).withBody("[]")));

		stubFor(get(urlEqualTo("/ccds/solution/f226cc60-c2ec-4c2b-b05c-4a521f77e077/revision")).willReturn(aResponse()
				.withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())
				.withBody(
						"[{\"created\":1535603252000,\"modified\":1536354698000,\"revisionId\":\"02c5f263-c612-4bd2-abaa-d12ccc0d2476\",\"version\":\"2\",\"description\":null,\"metadata\":null,\"origin\":null,\"accessTypeCode\":\"OR\",\"validationStatusCode\":\"PS\",\"authors\":[],\"publisher\":\"Acumos\",\"solutionId\":\"f226cc60-c2ec-4c2b-b05c-4a521f77e077\",\"userId\":\"bc961e2a-9506-4cf5-bbdb-009558b79e29\",\"sourceId\":null},{\"created\":1535603044000,\"modified\":1535603044000,\"revisionId\":\"f6b577a1-1849-4965-b77e-2ea11ab0b327\",\"version\":\"1\",\"description\":null,\"metadata\":null,\"origin\":null,\"accessTypeCode\":\"PB\",\"validationStatusCode\":\"IP\",\"authors\":[],\"publisher\":null,\"solutionId\":\"f226cc60-c2ec-4c2b-b05c-4a521f77e077\",\"userId\":\"bc961e2a-9506-4cf5-bbdb-009558b79e29\",\"sourceId\":null}]")));

		stubFor(get(urlEqualTo(
				"/ccds/thread/solution/f226cc60-c2ec-4c2b-b05c-4a521f77e077/revision/02c5f263-c612-4bd2-abaa-d12ccc0d2476/comment/count"))
						.willReturn(aResponse().withStatus(HttpStatus.SC_OK)
								.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())
								.withBody("{\"count\":0}")));

		stubFor(get(urlEqualTo(
				"/ccds/stepresult/search?solutionId=f226cc60-c2ec-4c2b-b05c-4a521f77e077&_j=a&page=0&size=1&sort=startDate,DESC"))
						.willReturn(aResponse().withStatus(HttpStatus.SC_OK)
								.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString()).withBody(
										"{\"content\":[{\"stepResultId\":28,\"trackingId\":\"fd1ea3aa-5a91-454a-9f8b-87c674e25417\",\"stepCode\":\"OB\",\"solutionId\":\"f226cc60-c2ec-4c2b-b05c-4a521f77e077\",\"revisionId\":\"02c5f263-c612-4bd2-abaa-d12ccc0d2476\",\"artifactId\":\"255c59b2-42a1-4fae-bb92-33ce1356eb20\",\"userId\":\"bc961e2a-9506-4cf5-bbdb-009558b79e29\",\"name\":\"AddDockerImage\",\"statusCode\":\"SU\",\"result\":\"Add Artifact - image for solution - f226cc60-c2ec-4c2b-b05c-4a521f77e077 Successful\",\"startDate\":1535603254000,\"endDate\":1535603254000}],\"last\":false,\"totalPages\":30,\"totalElements\":30,\"size\":1,\"number\":0,\"sort\":[{\"direction\":\"DESC\",\"property\":\"startDate\",\"ignoreCase\":false,\"nullHandling\":\"NATIVE\",\"ascending\":false,\"descending\":true}],\"numberOfElements\":1,\"first\":true}")));

		stubFor(get(urlEqualTo(
				"/ccds/stepresult/search?trackingId=fd1ea3aa-5a91-454a-9f8b-87c674e25417&_j=a&page=0&size=25"))
						.willReturn(aResponse().withStatus(HttpStatus.SC_OK)
								.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString()).withBody(
										"{\"content\":[{\"stepResultId\":32,\"trackingId\":\"fd1ea3aa-5a91-454a-9f8b-87c674e25417\",\"stepCode\":\"OB\",\"solutionId\":\"f226cc60-c2ec-4c2b-b05c-4a521f77e077\",\"revisionId\":\"02c5f263-c612-4bd2-abaa-d12ccc0d2476\",\"artifactId\":\"121dcb0f-c714-48cf-89c4-588d0e43f05d\",\"userId\":\"bc961e2a-9506-4cf5-bbdb-009558b79e29\",\"name\":\"AddArtifact\",\"statusCode\":\"SU\",\"result\":\"Add Artifact foronboardingLog_fd1ea3aa-5a91-454a-9f8b-87c674e25417.log Successful\",\"startDate\":1535603254000,\"endDate\":1535603254000}],\"last\":true,\"totalPages\":1,\"totalElements\":18,\"size\":25,\"number\":0,\"sort\":null,\"numberOfElements\":18,\"first\":true}")));

		stubFor(get(urlEqualTo(
				"/ccds/pubreq/search?revisionId=02c5f263-c612-4bd2-abaa-d12ccc0d2476&solutionId=f226cc60-c2ec-4c2b-b05c-4a521f77e077&_j=a&statusCode=PE&page=0&size=1"))
						.willReturn(aResponse().withStatus(HttpStatus.SC_OK)
								.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString()).withBody(
										"{\"content\":[],\"last\":true,\"totalPages\":0,\"totalElements\":0,\"size\":1,\"number\":0,\"sort\":null,\"numberOfElements\":0,\"first\":true}")));

		HttpHeaders headers = new HttpHeaders();
		HttpEntity<JsonRequest<RestPageRequestPortal>> requestEntity = new HttpEntity<>(reqObj, headers);

		ResponseEntity<JsonResponse<RestPageResponseBE<MLSolution>>> solutionResponse = restTemplate.exchange(
				host + ":" + randomServerPort + "/portal/solutions", HttpMethod.POST, requestEntity,
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
		restpagerequestPortal.setAccessTypeCodes(new String[] { "PB", "OR" });
		restpagerequestPortal.setActive(true);
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

		stubFor(get(urlEqualTo("/ccds/code/pair/ACCESS_TYPE")).willReturn(aResponse().withStatus(HttpStatus.SC_OK)
				.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())
				.withBody("[" + "  {" + "    \"code\": \"RS\"," + "    \"name\": \"Restricted\"" + "  }," + "  {"
						+ "    \"code\": \"PR\"," + "    \"name\": \"Private\"" + "  }," + "  {"
						+ "    \"code\": \"PB\"," + "    \"name\": \"Public\"" + "  }," + "  {"
						+ "    \"code\": \"OR\"," + "    \"name\": \"Organization\"" + "  }" + "]")));

		stubFor(get(urlEqualTo(
				"/ccds/solution/search/user?atc=PB&atc=OR&active=true&user=bc961e2a-9506-4cf5-bbdb-009558b79e29&page=0&size=9&sort=modified,DESC"))
						.willReturn(aResponse().withStatus(HttpStatus.SC_OK)
								.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString()).withBody(
										"{\"content\":[{\"created\":1535603044000,\"modified\":1536350829000,\"solutionId\":\"f226cc60-c2ec-4c2b-b05c-4a521f77e077\",\"name\":\"TestSolution\",\"metadata\":null,\"active\":true,\"modelTypeCode\":\"CL\",\"toolkitTypeCode\":\"TF\",\"origin\":null,\"userId\":\"bc961e2a-9506-4cf5-bbdb-009558b79e29\",\"sourceId\":null,\"tags\":[{\"tag\":\"Test\"}],\"viewCount\":12,\"downloadCount\":0,\"lastDownload\":1536364233000,\"ratingCount\":0,\"ratingAverageTenths\":0,\"featured\":false}],\"last\":true,\"totalPages\":1,\"totalElements\":1,\"size\":9,\"number\":0,\"sort\":[{\"direction\":\"DESC\",\"property\":\"modified\",\"ignoreCase\":false,\"nullHandling\":\"NATIVE\",\"ascending\":false,\"descending\":true}],\"numberOfElements\":1,\"first\":true}")));

		stubFor(get(urlEqualTo("/ccds/solution/f226cc60-c2ec-4c2b-b05c-4a521f77e077")).willReturn(aResponse()
				.withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())
				.withBody(
						"{\"created\":1535603044000,\"modified\":1536350829000,\"solutionId\":\"f226cc60-c2ec-4c2b-b05c-4a521f77e077\",\"name\":\"TestSolution\",\"metadata\":null,\"active\":true,\"modelTypeCode\":\"CL\",\"toolkitTypeCode\":\"TF\",\"origin\":null,\"userId\":\"bc961e2a-9506-4cf5-bbdb-009558b79e29\",\"sourceId\":null,\"tags\":[{\"tag\":\"Test\"}],\"viewCount\":12,\"downloadCount\":0,\"lastDownload\":1536364233000,\"ratingCount\":0,\"ratingAverageTenths\":0,\"featured\":false}")));

		stubFor(get(urlEqualTo("/ccds/solution/f226cc60-c2ec-4c2b-b05c-4a521f77e077/tag")).willReturn(aResponse()
				.withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())
				.withBody("[{\"tag\":\"Test\"}]")));

		stubFor(get(urlEqualTo("/ccds/user/bc961e2a-9506-4cf5-bbdb-009558b79e29")).willReturn(aResponse()
				.withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())
				.withBody(
						"{\"created\":1535602889000,\"modified\":1536623387000,\"userId\":\"bc961e2a-9506-4cf5-bbdb-009558b79e29\",\"firstName\":\"Test\",\"middleName\":null,\"lastName\":\"User\",\"orgName\":null,\"email\":\"testUser@gmail.com\",\"loginName\":\"test\",\"loginHash\":null,\"loginPassExpire\":null,\"authToken\":\"\",\"active\":true,\"lastLogin\":1536623387000,\"loginFailCount\":null,\"loginFailDate\":null,\"picture\":null,\"apiToken\":\"30d19b719c1d44ae84d92dcc87f5a1ad\",\"verifyTokenHash\":null,\"verifyExpiration\":null,\"tags\":[]}")));

		stubFor(get(urlEqualTo("/ccds/solution/f226cc60-c2ec-4c2b-b05c-4a521f77e077/user/access"))
				.willReturn(aResponse().withStatus(HttpStatus.SC_OK)
						.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString()).withBody("[]")));

		stubFor(get(urlEqualTo("/ccds/solution/f226cc60-c2ec-4c2b-b05c-4a521f77e077/revision")).willReturn(aResponse()
				.withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())
				.withBody(
						"[{\"created\":1535603252000,\"modified\":1536354698000,\"revisionId\":\"02c5f263-c612-4bd2-abaa-d12ccc0d2476\",\"version\":\"2\",\"description\":null,\"metadata\":null,\"origin\":null,\"accessTypeCode\":\"OR\",\"validationStatusCode\":\"PS\",\"authors\":[],\"publisher\":\"Acumos\",\"solutionId\":\"f226cc60-c2ec-4c2b-b05c-4a521f77e077\",\"userId\":\"bc961e2a-9506-4cf5-bbdb-009558b79e29\",\"sourceId\":null},{\"created\":1535603044000,\"modified\":1535603044000,\"revisionId\":\"f6b577a1-1849-4965-b77e-2ea11ab0b327\",\"version\":\"1\",\"description\":null,\"metadata\":null,\"origin\":null,\"accessTypeCode\":\"PB\",\"validationStatusCode\":\"IP\",\"authors\":[],\"publisher\":null,\"solutionId\":\"f226cc60-c2ec-4c2b-b05c-4a521f77e077\",\"userId\":\"bc961e2a-9506-4cf5-bbdb-009558b79e29\",\"sourceId\":null}]")));

		stubFor(get(urlEqualTo(
				"/ccds/thread/solution/f226cc60-c2ec-4c2b-b05c-4a521f77e077/revision/02c5f263-c612-4bd2-abaa-d12ccc0d2476/comment/count"))
						.willReturn(aResponse().withStatus(HttpStatus.SC_OK)
								.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())
								.withBody("{\"count\":0}")));

		stubFor(get(urlEqualTo(
				"/ccds/stepresult/search?solutionId=f226cc60-c2ec-4c2b-b05c-4a521f77e077&_j=a&page=0&size=1&sort=startDate,DESC"))
						.willReturn(aResponse().withStatus(HttpStatus.SC_OK)
								.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString()).withBody(
										"{\"content\":[{\"stepResultId\":28,\"trackingId\":\"fd1ea3aa-5a91-454a-9f8b-87c674e25417\",\"stepCode\":\"OB\",\"solutionId\":\"f226cc60-c2ec-4c2b-b05c-4a521f77e077\",\"revisionId\":\"02c5f263-c612-4bd2-abaa-d12ccc0d2476\",\"artifactId\":\"255c59b2-42a1-4fae-bb92-33ce1356eb20\",\"userId\":\"bc961e2a-9506-4cf5-bbdb-009558b79e29\",\"name\":\"AddDockerImage\",\"statusCode\":\"SU\",\"result\":\"Add Artifact - image for solution - f226cc60-c2ec-4c2b-b05c-4a521f77e077 Successful\",\"startDate\":1535603254000,\"endDate\":1535603254000}],\"last\":false,\"totalPages\":30,\"totalElements\":30,\"size\":1,\"number\":0,\"sort\":[{\"direction\":\"DESC\",\"property\":\"startDate\",\"ignoreCase\":false,\"nullHandling\":\"NATIVE\",\"ascending\":false,\"descending\":true}],\"numberOfElements\":1,\"first\":true}")));

		stubFor(get(urlEqualTo(
				"/ccds/stepresult/search?trackingId=fd1ea3aa-5a91-454a-9f8b-87c674e25417&_j=a&page=0&size=25"))
						.willReturn(aResponse().withStatus(HttpStatus.SC_OK)
								.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString()).withBody(
										"{\"content\":[{\"stepResultId\":32,\"trackingId\":\"fd1ea3aa-5a91-454a-9f8b-87c674e25417\",\"stepCode\":\"OB\",\"solutionId\":\"f226cc60-c2ec-4c2b-b05c-4a521f77e077\",\"revisionId\":\"02c5f263-c612-4bd2-abaa-d12ccc0d2476\",\"artifactId\":\"121dcb0f-c714-48cf-89c4-588d0e43f05d\",\"userId\":\"bc961e2a-9506-4cf5-bbdb-009558b79e29\",\"name\":\"AddArtifact\",\"statusCode\":\"SU\",\"result\":\"Add Artifact foronboardingLog_fd1ea3aa-5a91-454a-9f8b-87c674e25417.log Successful\",\"startDate\":1535603254000,\"endDate\":1535603254000}],\"last\":true,\"totalPages\":1,\"totalElements\":18,\"size\":25,\"number\":0,\"sort\":null,\"numberOfElements\":18,\"first\":true}")));

		stubFor(get(urlEqualTo(
				"/ccds/pubreq/search?revisionId=02c5f263-c612-4bd2-abaa-d12ccc0d2476&solutionId=f226cc60-c2ec-4c2b-b05c-4a521f77e077&_j=a&statusCode=PE&page=0&size=1"))
						.willReturn(aResponse().withStatus(HttpStatus.SC_OK)
								.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString()).withBody(
										"{\"content\":[],\"last\":true,\"totalPages\":0,\"totalElements\":0,\"size\":1,\"number\":0,\"sort\":null,\"numberOfElements\":0,\"first\":true}")));

		HttpHeaders headers = new HttpHeaders();
		HttpEntity<JsonRequest<RestPageRequestPortal>> requestEntity = new HttpEntity<>(reqObj, headers);

		ResponseEntity<JsonResponse<RestPageResponseBE<MLSolution>>> solutionResponse = restTemplate.exchange(
				host + ":" + randomServerPort + "/user/solutions", HttpMethod.POST, requestEntity,
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
		restpagerequestPortal.setAccessTypeCodes(new String[] { "PB", "OR" });
		restpagerequestPortal.setNameKeyword(new String[] { "Test"});
		restpagerequestPortal.setActive(true);
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

		stubFor(get(urlEqualTo("/ccds/code/pair/ACCESS_TYPE")).willReturn(aResponse().withStatus(HttpStatus.SC_OK)
				.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())
				.withBody("[" + "  {" + "    \"code\": \"RS\"," + "    \"name\": \"Restricted\"" + "  }," + "  {"
						+ "    \"code\": \"PR\"," + "    \"name\": \"Private\"" + "  }," + "  {"
						+ "    \"code\": \"PB\"," + "    \"name\": \"Public\"" + "  }," + "  {"
						+ "    \"code\": \"OR\"," + "    \"name\": \"Organization\"" + "  }" + "]")));

		stubFor(get(urlEqualTo(
				"/ccds/solution/search/portal/kwtag?atc=PB&atc=OR&active=true&kw=Test&page=0&size=9&sort=modified,DESC"))
						.willReturn(aResponse().withStatus(HttpStatus.SC_OK)
								.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString()).withBody(
										"{\"content\":[{\"created\":1535603044000,\"modified\":1536350829000,\"solutionId\":\"f226cc60-c2ec-4c2b-b05c-4a521f77e077\",\"name\":\"TestSolution\",\"metadata\":null,\"active\":true,\"modelTypeCode\":\"CL\",\"toolkitTypeCode\":\"TF\",\"origin\":null,\"userId\":\"bc961e2a-9506-4cf5-bbdb-009558b79e29\",\"sourceId\":null,\"tags\":[{\"tag\":\"Test\"}],\"viewCount\":12,\"downloadCount\":0,\"lastDownload\":1536364233000,\"ratingCount\":0,\"ratingAverageTenths\":0,\"featured\":false}],\"last\":true,\"totalPages\":1,\"totalElements\":1,\"size\":9,\"number\":0,\"sort\":[{\"direction\":\"DESC\",\"property\":\"modified\",\"ignoreCase\":false,\"nullHandling\":\"NATIVE\",\"ascending\":false,\"descending\":true}],\"numberOfElements\":1,\"first\":true}")));

		stubFor(get(urlEqualTo("/ccds/solution/f226cc60-c2ec-4c2b-b05c-4a521f77e077")).willReturn(aResponse()
				.withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())
				.withBody(
						"{\"created\":1535603044000,\"modified\":1536350829000,\"solutionId\":\"f226cc60-c2ec-4c2b-b05c-4a521f77e077\",\"name\":\"TestSolution\",\"metadata\":null,\"active\":true,\"modelTypeCode\":\"CL\",\"toolkitTypeCode\":\"TF\",\"origin\":null,\"userId\":\"bc961e2a-9506-4cf5-bbdb-009558b79e29\",\"sourceId\":null,\"tags\":[{\"tag\":\"Test\"}],\"viewCount\":12,\"downloadCount\":0,\"lastDownload\":1536364233000,\"ratingCount\":0,\"ratingAverageTenths\":0,\"featured\":false}")));

		stubFor(get(urlEqualTo("/ccds/solution/f226cc60-c2ec-4c2b-b05c-4a521f77e077/tag")).willReturn(aResponse()
				.withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())
				.withBody("[{\"tag\":\"Test\"}]")));

		stubFor(get(urlEqualTo("/ccds/user/bc961e2a-9506-4cf5-bbdb-009558b79e29")).willReturn(aResponse()
				.withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())
				.withBody(
						"{\"created\":1535602889000,\"modified\":1536623387000,\"userId\":\"bc961e2a-9506-4cf5-bbdb-009558b79e29\",\"firstName\":\"Test\",\"middleName\":null,\"lastName\":\"User\",\"orgName\":null,\"email\":\"testUser@gmail.com\",\"loginName\":\"test\",\"loginHash\":null,\"loginPassExpire\":null,\"authToken\":\"\",\"active\":true,\"lastLogin\":1536623387000,\"loginFailCount\":null,\"loginFailDate\":null,\"picture\":null,\"apiToken\":\"30d19b719c1d44ae84d92dcc87f5a1ad\",\"verifyTokenHash\":null,\"verifyExpiration\":null,\"tags\":[]}")));

		stubFor(get(urlEqualTo("/ccds/solution/f226cc60-c2ec-4c2b-b05c-4a521f77e077/user/access"))
				.willReturn(aResponse().withStatus(HttpStatus.SC_OK)
						.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString()).withBody("[]")));

		stubFor(get(urlEqualTo("/ccds/solution/f226cc60-c2ec-4c2b-b05c-4a521f77e077/revision")).willReturn(aResponse()
				.withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())
				.withBody(
						"[{\"created\":1535603252000,\"modified\":1536354698000,\"revisionId\":\"02c5f263-c612-4bd2-abaa-d12ccc0d2476\",\"version\":\"2\",\"description\":null,\"metadata\":null,\"origin\":null,\"accessTypeCode\":\"OR\",\"validationStatusCode\":\"PS\",\"authors\":[],\"publisher\":\"Acumos\",\"solutionId\":\"f226cc60-c2ec-4c2b-b05c-4a521f77e077\",\"userId\":\"bc961e2a-9506-4cf5-bbdb-009558b79e29\",\"sourceId\":null},{\"created\":1535603044000,\"modified\":1535603044000,\"revisionId\":\"f6b577a1-1849-4965-b77e-2ea11ab0b327\",\"version\":\"1\",\"description\":null,\"metadata\":null,\"origin\":null,\"accessTypeCode\":\"PB\",\"validationStatusCode\":\"IP\",\"authors\":[],\"publisher\":null,\"solutionId\":\"f226cc60-c2ec-4c2b-b05c-4a521f77e077\",\"userId\":\"bc961e2a-9506-4cf5-bbdb-009558b79e29\",\"sourceId\":null}]")));

		stubFor(get(urlEqualTo(
				"/ccds/thread/solution/f226cc60-c2ec-4c2b-b05c-4a521f77e077/revision/02c5f263-c612-4bd2-abaa-d12ccc0d2476/comment/count"))
						.willReturn(aResponse().withStatus(HttpStatus.SC_OK)
								.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())
								.withBody("{\"count\":0}")));

		stubFor(get(urlEqualTo(
				"/ccds/stepresult/search?solutionId=f226cc60-c2ec-4c2b-b05c-4a521f77e077&_j=a&page=0&size=1&sort=startDate,DESC"))
						.willReturn(aResponse().withStatus(HttpStatus.SC_OK)
								.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString()).withBody(
										"{\"content\":[{\"stepResultId\":28,\"trackingId\":\"fd1ea3aa-5a91-454a-9f8b-87c674e25417\",\"stepCode\":\"OB\",\"solutionId\":\"f226cc60-c2ec-4c2b-b05c-4a521f77e077\",\"revisionId\":\"02c5f263-c612-4bd2-abaa-d12ccc0d2476\",\"artifactId\":\"255c59b2-42a1-4fae-bb92-33ce1356eb20\",\"userId\":\"bc961e2a-9506-4cf5-bbdb-009558b79e29\",\"name\":\"AddDockerImage\",\"statusCode\":\"SU\",\"result\":\"Add Artifact - image for solution - f226cc60-c2ec-4c2b-b05c-4a521f77e077 Successful\",\"startDate\":1535603254000,\"endDate\":1535603254000}],\"last\":false,\"totalPages\":30,\"totalElements\":30,\"size\":1,\"number\":0,\"sort\":[{\"direction\":\"DESC\",\"property\":\"startDate\",\"ignoreCase\":false,\"nullHandling\":\"NATIVE\",\"ascending\":false,\"descending\":true}],\"numberOfElements\":1,\"first\":true}")));

		stubFor(get(urlEqualTo(
				"/ccds/stepresult/search?trackingId=fd1ea3aa-5a91-454a-9f8b-87c674e25417&_j=a&page=0&size=25"))
						.willReturn(aResponse().withStatus(HttpStatus.SC_OK)
								.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString()).withBody(
										"{\"content\":[{\"stepResultId\":32,\"trackingId\":\"fd1ea3aa-5a91-454a-9f8b-87c674e25417\",\"stepCode\":\"OB\",\"solutionId\":\"f226cc60-c2ec-4c2b-b05c-4a521f77e077\",\"revisionId\":\"02c5f263-c612-4bd2-abaa-d12ccc0d2476\",\"artifactId\":\"121dcb0f-c714-48cf-89c4-588d0e43f05d\",\"userId\":\"bc961e2a-9506-4cf5-bbdb-009558b79e29\",\"name\":\"AddArtifact\",\"statusCode\":\"SU\",\"result\":\"Add Artifact foronboardingLog_fd1ea3aa-5a91-454a-9f8b-87c674e25417.log Successful\",\"startDate\":1535603254000,\"endDate\":1535603254000}],\"last\":true,\"totalPages\":1,\"totalElements\":18,\"size\":25,\"number\":0,\"sort\":null,\"numberOfElements\":18,\"first\":true}")));

		stubFor(get(urlEqualTo(
				"/ccds/pubreq/search?revisionId=02c5f263-c612-4bd2-abaa-d12ccc0d2476&solutionId=f226cc60-c2ec-4c2b-b05c-4a521f77e077&_j=a&statusCode=PE&page=0&size=1"))
						.willReturn(aResponse().withStatus(HttpStatus.SC_OK)
								.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString()).withBody(
										"{\"content\":[],\"last\":true,\"totalPages\":0,\"totalElements\":0,\"size\":1,\"number\":0,\"sort\":null,\"numberOfElements\":0,\"first\":true}")));

		HttpHeaders headers = new HttpHeaders();
		HttpEntity<JsonRequest<RestPageRequestPortal>> requestEntity = new HttpEntity<>(reqObj, headers);

		ResponseEntity<JsonResponse<RestPageResponseBE<MLSolution>>> solutionResponse = restTemplate.exchange(
				host + ":" + randomServerPort + "/searchSolutionBykeyword", HttpMethod.POST, requestEntity,
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
		String accessType = "PB";

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

		stubFor(get(urlEqualTo("/ccds/revision/4f5079b9-49e8-48a3-8fcb-c006e96c4c10/access/PB/document"))
				.willReturn(aResponse().withStatus(HttpStatus.SC_OK)
						.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString()).withBody("[]")));

		stubFor(put(urlEqualTo("/repository/repo_acumos_model_maven/com/artifact/" + solutionId + "/" + revisionId + "/"
				+ fileName + "/" + accessType + "/" + fileName + "-" + accessType + ".txt"))
						.willReturn(aResponse().withStatus(HttpStatus.SC_OK)));

		stubFor(post(urlEqualTo("/ccds/document")).willReturn(aResponse().withStatus(HttpStatus.SC_OK)
				.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString()).withBody(
						"{\"documentId\":\"787c9461-4288-4091-8d39-5ce1a4e04e34\",\"name\":\"upload-test-file4958107523126401268.txt\",\"version\":null,\"uri\":\"com/artifact/b7b9bb9c-980c-4a18-b7bf-545bbd9173ab/4f5079b9-49e8-48a3-8fcb-c006e96c4c10/upload-test-file4958107523126401268/PB/upload-test-file4958107523126401268-PB.txt\",\"size\":32,\"userId\":\"bc961e2a-9506-4cf5-bbdb-009558b79e29\"}")));

		stubFor(post(urlEqualTo(
				"/ccds/revision/4f5079b9-49e8-48a3-8fcb-c006e96c4c10/access/PB/document/787c9461-4288-4091-8d39-5ce1a4e04e34"))
						.willReturn(aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type",
								MediaType.APPLICATION_JSON.toString())));

		ResponseEntity<JsonResponse<MLPDocument>> documentResponse = null;
		try {
			documentResponse = restTemplate.exchange(
					host + ":" + randomServerPort + "/solution/" + solutionId + "/revision/" + revisionId + "/"
							+ accessType + "/document",
					HttpMethod.POST, requestEntity, new ParameterizedTypeReference<JsonResponse<MLPDocument>>() {
					});
		} catch (HttpStatusCodeException e) {
			Assert.fail();
		}
		MLPDocument document = documentResponse.getBody().getResponseBody();
		assertNotNull(document);
		assertEquals(HttpServletResponse.SC_OK, documentResponse.getStatusCode().value());
		assertEquals("787c9461-4288-4091-8d39-5ce1a4e04e34", document.getDocumentId());
	}


	@Test
	public void getDocumentTest() {

		String solutionId = "b7b9bb9c-980c-4a18-b7bf-545bbd9173ab";
		String revisionId = "4f5079b9-49e8-48a3-8fcb-c006e96c4c10";
		String accessType = "PB";

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

		stubFor(get(urlEqualTo("/ccds/revision/4f5079b9-49e8-48a3-8fcb-c006e96c4c10/access/PB/document"))
				.willReturn(aResponse().withStatus(HttpStatus.SC_OK)
						.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString()).withBody("[]")));

		stubFor(put(urlEqualTo("/repository/repo_acumos_model_maven/com/artifact/" + solutionId + "/" + revisionId + "/"
				+ fileName + "/" + accessType + "/" + fileName + "-" + accessType + ".txt"))
						.willReturn(aResponse().withStatus(HttpStatus.SC_OK)));

		stubFor(post(urlEqualTo("/ccds/document")).willReturn(aResponse().withStatus(HttpStatus.SC_OK)
				.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString()).withBody(
						"{\"documentId\":\"787c9461-4288-4091-8d39-5ce1a4e04e34\",\"name\":\"upload-test-file4958107523126401268.txt\",\"version\":null,\"uri\":\"com/artifact/b7b9bb9c-980c-4a18-b7bf-545bbd9173ab/4f5079b9-49e8-48a3-8fcb-c006e96c4c10/upload-test-file4958107523126401268/PB/upload-test-file4958107523126401268-PB.txt\",\"size\":32,\"userId\":\"bc961e2a-9506-4cf5-bbdb-009558b79e29\"}")));

		stubFor(post(urlEqualTo(
				"/ccds/revision/4f5079b9-49e8-48a3-8fcb-c006e96c4c10/access/PB/document/787c9461-4288-4091-8d39-5ce1a4e04e34"))
						.willReturn(aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type",
								MediaType.APPLICATION_JSON.toString())));
	//  /solution/{solutionId}/revision/{revisionId}/{accessType}/document
		ResponseEntity<JsonResponse<List<MLPDocument>>> documentResponse = null;
		try {
			documentResponse = restTemplate.exchange(
					host + ":" + randomServerPort + "/solution/" + solutionId + "/revision/" + revisionId + "/"
							+ accessType + "/document",
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
		stubFor(get(urlEqualTo("/ccds/tag")).willReturn(aResponse()
				.withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())
				.withBody(
						"{\r\n" + 
						"  \"content\": [\r\n" + 
						"    {\r\n" + 
						"      \"tag\": \"Test\"\r\n" + 
						"    }\r\n" + 
						"  ],\r\n" + 
						"  \"last\": true,\r\n" + 
						"  \"totalPages\": 1,\r\n" + 
						"  \"totalElements\": 1,\r\n" + 
						"  \"size\": 100,\r\n" + 
						"  \"number\": 0,\r\n" + 
						"  \"sort\": null,\r\n" + 
						"  \"numberOfElements\": 1,\r\n" + 
						"  \"first\": true\r\n" + 
						"}")));
		
		stubFor(get(urlEqualTo("/ccds/user/bc961e2a-9506-4cf5-bbdb-009558b79e29")).willReturn(aResponse()
				.withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())
				.withBody(
						"{\r\n" + 
						"  \"created\": 1538588099000,\r\n" + 
						"  \"modified\": 1538674913000,\r\n" + 
						"  \"userId\": \"bc961e2a-9506-4cf5-bbdb-009558b79e29\",\r\n" + 
						"  \"firstName\": \"Test\",\r\n" + 
						"  \"middleName\": null,\r\n" + 
						"  \"lastName\": \"User\",\r\n" + 
						"  \"orgName\": null,\r\n" + 
						"  \"email\": \"test@gmail.com\",\r\n" + 
						"  \"loginName\": \"test\",\r\n" + 
						"  \"loginHash\": null,\r\n" + 
						"  \"loginPassExpire\": null,\r\n" + 
						"  \"active\": true,\r\n" + 
						"  \"lastLogin\": 1538674913000,\r\n" + 
						"  \"loginFailCount\": null,\r\n" + 
						"  \"loginFailDate\": null,\r\n" + 
						"  \"picture\": null,\r\n" + 
						"  \"apiToken\": \"013556751e28443d9997cbbaf992e39b\",\r\n" + 
						"  \"verifyTokenHash\": null,\r\n" + 
						"  \"verifyExpiration\": null,\r\n" + 
						"  \"tags\": [\r\n" + 
						"    {\r\n" + 
						"      \"tag\": \"Test\"\r\n" + 
						"    }\r\n" + 
						"  ]\r\n" + 
						"}")));

		RestPageRequest reqObj = new RestPageRequest();
		JsonRequest<RestPageRequest> jsonRequest = new JsonRequest<>();
		jsonRequest.setBody(reqObj);
		HttpHeaders headers = new HttpHeaders();
		HttpEntity<JsonRequest<RestPageRequest>> requestEntity = new HttpEntity<>(jsonRequest, headers);

		ResponseEntity<JsonResponse<RestPageResponseBE>> preferedTagsList = restTemplate.exchange(
				host + ":" + randomServerPort + "/preferredTags/bc961e2a-9506-4cf5-bbdb-009558b79e29",
				HttpMethod.PUT, requestEntity, new ParameterizedTypeReference<JsonResponse<RestPageResponseBE>>() {
				});

		assertNotNull(preferedTagsList);
		assertEquals(HttpServletResponse.SC_OK, preferedTagsList.getStatusCode().value());
	}
}
