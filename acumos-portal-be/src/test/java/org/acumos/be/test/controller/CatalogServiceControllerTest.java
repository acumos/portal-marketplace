/*-
 * ===============LICENSE_START=======================================================
 * Acumos
 * ===================================================================================
 * Copyright (C) 2019 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
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
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

import javax.servlet.http.HttpServletResponse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.acumos.cds.domain.MLPCatalog;
import org.acumos.cds.domain.MLPSolution;
import org.acumos.cds.transport.RestPageRequest;
import org.acumos.cds.transport.RestPageResponse;
import org.acumos.portal.be.APINames;
import org.acumos.portal.be.common.ConfigConstants;
import org.acumos.portal.be.common.JsonRequest;
import org.acumos.portal.be.common.JsonResponse;
import org.acumos.portal.be.transport.CatalogSearchRequest;
import org.acumos.portal.be.transport.MLCatalog;
import org.apache.http.HttpStatus;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import com.github.tomakehurst.wiremock.junit.WireMockRule;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = org.acumos.portal.be.Application.class, webEnvironment = WebEnvironment.RANDOM_PORT, properties = {
		ConfigConstants.cdms_client_url + "=http://localhost:8000/ccds",
		ConfigConstants.cdms_client_username + "=ccds_test", ConfigConstants.cdms_client_password + "=ccds_test" })
@EnableAutoConfiguration(exclude = { SecurityAutoConfiguration.class })
public class CatalogServiceControllerTest {

	@Rule
	public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().port(8000));

	private RestTemplate restTemplate = new RestTemplate();

	@LocalServerPort
	int randomServerPort;

	private static final String VARIABLE = "/%s";
	private static final String CCDS_PATH = "/ccds";
	private static final String CATALOG_PATH = "/catalog";
	private static final String CCDS_CATALOG_PATH = CCDS_PATH + CATALOG_PATH;
	private static final String PAGE_REQUEST_PARAMS = "page=0&size=9&sort=modified,DESC";
	private static final String SEARCH_PATH = CCDS_CATALOG_PATH + "/search?selfPublish=false&_j=a&" + PAGE_REQUEST_PARAMS;
	private static final String CATALOG_ID_PATH = CCDS_CATALOG_PATH + VARIABLE;
	private static final String PEER_ACCESS_PATH = CCDS_PATH + "/access/peer" + VARIABLE + CATALOG_PATH;
	private static final String ADD_DROP_PEER_ACCESS_PATH = PEER_ACCESS_PATH + VARIABLE;
	private static final String SOLUTION_PATH = "/solution";
	private static final String SOLUTION_ID_PATH = SOLUTION_PATH + VARIABLE;
	private static final String CATALOG_SOLUTION_COUNT_PATH = CATALOG_ID_PATH + SOLUTION_PATH + "/count";
	private static final String CATALOG_SOLUTION_PATH = CCDS_CATALOG_PATH + SOLUTION_PATH;
	private static final String CATALOG_SOLUTION_ID_PATH = CCDS_CATALOG_PATH + SOLUTION_ID_PATH;
	private static final String ADD_DROP_SOLUTION_PATH = CATALOG_ID_PATH + SOLUTION_ID_PATH;
	private static final String USER_FAVORITE_PATH = "/user" + VARIABLE + "/favorite";
	private static final String GET_USER_FAVORITES_PATH = CCDS_CATALOG_PATH + USER_FAVORITE_PATH;
	private static final String ADD_DROP_FAVORITES_PATH = CATALOG_ID_PATH + USER_FAVORITE_PATH;
	
	@Test
	public void getCatalogsTest() {
		JsonRequest<RestPageRequest> requestJson = new JsonRequest<>();
		requestJson.setBody(getTestRestPageRequest());

		stubFor(get(urlEqualTo(CCDS_CATALOG_PATH + "?" + PAGE_REQUEST_PARAMS)).willReturn(
				aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
						.withBody("{\"content\":[" + "{\"accessTypeCode\": \"PB\","
								+ "\"catalogId\": \"12345678-abcd-90ab-cdef-1234567890ab\","
								+ "\"created\": \"2018-12-16T12:34:56.789Z\","
								+ "\"description\": \"A catalog of test models\","
								+ "\"modified\": \"2018-12-16T12:34:56.789Z\"," + "\"name\": \"Test Catalog\","
								+ "\"origin\": \"http://test.acumos.org/api\"," + "\"publisher\": \"Acumos\","
								+ "\"url\": \"http://test.company.com/api\"}]," + "\"last\":true," + "\"totalPages\":1,"
								+ "\"totalElements\":1," + "\"size\":9," + "\"number\":0,"
								+ "\"sort\":[{\"direction\":\"DESC\"," + "\"property\":\"modified\","
								+ "\"ignoreCase\":false," + "\"nullHandling\":\"NATIVE\"," + "\"ascending\":false,"
								+ "\"descending\":true}]," + "\"numberOfElements\":1," + "\"first\":true}")));

		stubFor(get(urlEqualTo(String.format(CATALOG_SOLUTION_COUNT_PATH, "12345678-abcd-90ab-cdef-1234567890ab")))
				.willReturn(aResponse().withStatus(HttpStatus.SC_OK)
						.withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE).withBody("5")));
		
		HttpHeaders headers = new HttpHeaders();
		HttpEntity<JsonRequest<RestPageRequest>> requestEntity = new HttpEntity<>(requestJson, headers);

		ResponseEntity<JsonResponse<RestPageResponse<MLCatalog>>> respEntity = restTemplate.exchange(
				"http://localhost:" + randomServerPort + APINames.GET_CATALOGS, HttpMethod.POST, requestEntity,
				new ParameterizedTypeReference<JsonResponse<RestPageResponse<MLCatalog>>>() {
				});

		assertNotNull(respEntity);
		assertEquals(HttpServletResponse.SC_OK, respEntity.getStatusCode().value());
		RestPageResponse<MLCatalog> restPageResponse = respEntity.getBody().getResponseBody();
		assertValidRestPageResponse(restPageResponse);
		List<MLCatalog> catalogs = restPageResponse.getContent();
		assertEquals(catalogs.size(), 1);
		MLCatalog catalog = catalogs.get(0);
		assertNotNull(catalog);
		assertFalse(catalog.isFavorite());
	}
	
	@Test
	public void getCatalogsWithUserIdTest() {
		JsonRequest<RestPageRequest> requestJson = new JsonRequest<>();
		requestJson.setBody(getTestRestPageRequest());

		stubFor(get(urlEqualTo(CCDS_CATALOG_PATH + "?" + PAGE_REQUEST_PARAMS)).willReturn(
				aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
						.withBody("{\"content\":[" + "{\"accessTypeCode\": \"PB\","
								+ "\"catalogId\": \"12345678-abcd-90ab-cdef-1234567890ab\","
								+ "\"created\": \"2018-12-16T12:34:56.789Z\","
								+ "\"description\": \"A catalog of test models\","
								+ "\"modified\": \"2018-12-16T12:34:56.789Z\"," + "\"name\": \"Test Catalog\","
								+ "\"origin\": \"http://test.acumos.org/api\"," + "\"publisher\": \"Acumos\","
								+ "\"url\": \"http://test.company.com/api\"}]," + "\"last\":true," + "\"totalPages\":1,"
								+ "\"totalElements\":1," + "\"size\":9," + "\"number\":0,"
								+ "\"sort\":[{\"direction\":\"DESC\"," + "\"property\":\"modified\","
								+ "\"ignoreCase\":false," + "\"nullHandling\":\"NATIVE\"," + "\"ascending\":false,"
								+ "\"descending\":true}]," + "\"numberOfElements\":1," + "\"first\":true}")));

		stubFor(get(urlEqualTo(String.format(CATALOG_SOLUTION_COUNT_PATH, "12345678-abcd-90ab-cdef-1234567890ab")))
				.willReturn(aResponse().withStatus(HttpStatus.SC_OK)
						.withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE).withBody("5")));
		
		stubFor(get(urlEqualTo(String.format(GET_USER_FAVORITES_PATH, "testUser")))
				.willReturn(aResponse().withStatus(HttpStatus.SC_OK)
						.withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE).withBody("[\"12345678-abcd-90ab-cdef-1234567890ab\"]")));

		HttpHeaders headers = new HttpHeaders();
		HttpEntity<JsonRequest<RestPageRequest>> requestEntity = new HttpEntity<>(requestJson, headers);

		ResponseEntity<JsonResponse<RestPageResponse<MLCatalog>>> respEntity = restTemplate.exchange(
				"http://localhost:" + randomServerPort + APINames.GET_CATALOGS + "?userId=testUser", HttpMethod.POST, requestEntity,
				new ParameterizedTypeReference<JsonResponse<RestPageResponse<MLCatalog>>>() {
				});

		assertNotNull(respEntity);
		assertEquals(HttpServletResponse.SC_OK, respEntity.getStatusCode().value());
		RestPageResponse<MLCatalog> restPageResponse = respEntity.getBody().getResponseBody();
		assertValidRestPageResponse(restPageResponse);
		List<MLCatalog> catalogs = restPageResponse.getContent();
		assertEquals(catalogs.size(), 1);
		MLCatalog catalog = catalogs.get(0);
		assertNotNull(catalog);
		assertTrue(catalog.isFavorite());
	}

	@Test
	public void searchCatalogsTest() {
		CatalogSearchRequest catalogRequest = new CatalogSearchRequest();
		catalogRequest.setSelfPublish("false");
		catalogRequest.setPageRequest(getTestRestPageRequest());

		JsonRequest<CatalogSearchRequest> requestJson = new JsonRequest<>();
		requestJson.setBody(catalogRequest);

		stubFor(get(urlEqualTo(SEARCH_PATH)).willReturn(
				aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
						.withBody("{\"content\":[" + "{\"accessTypeCode\": \"PB\","
								+ "\"catalogId\": \"12345678-abcd-90ab-cdef-1234567890ab\","
								+ "\"created\": \"2018-12-16T12:34:56.789Z\","
								+ "\"description\": \"A catalog of test models\","
								+ "\"modified\": \"2018-12-16T12:34:56.789Z\"," + "\"name\": \"Test Catalog\","
								+ "\"origin\": \"http://test.acumos.org/api\"," + "\"publisher\": \"Acumos\","
								+ "\"url\": \"http://test.company.com/api\"}]," + "\"last\":true," + "\"totalPages\":1,"
								+ "\"totalElements\":1," + "\"size\":9," + "\"number\":0,"
								+ "\"sort\":[{\"direction\":\"DESC\"," + "\"property\":\"modified\","
								+ "\"ignoreCase\":false," + "\"nullHandling\":\"NATIVE\"," + "\"ascending\":false,"
								+ "\"descending\":true}]," + "\"numberOfElements\":1," + "\"first\":true}")));
		
		stubFor(get(urlEqualTo(String.format(CATALOG_SOLUTION_COUNT_PATH, "12345678-abcd-90ab-cdef-1234567890ab")))
				.willReturn(aResponse().withStatus(HttpStatus.SC_OK)
						.withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE).withBody("5")));

		HttpHeaders headers = new HttpHeaders();
		HttpEntity<JsonRequest<CatalogSearchRequest>> requestEntity = new HttpEntity<>(requestJson, headers);

		ResponseEntity<JsonResponse<RestPageResponse<MLCatalog>>> respEntity = restTemplate.exchange(
				"http://localhost:" + randomServerPort + APINames.SEARCH_CATALOGS, HttpMethod.POST, requestEntity,
				new ParameterizedTypeReference<JsonResponse<RestPageResponse<MLCatalog>>>() {
				});

		assertNotNull(respEntity);
		assertEquals(HttpServletResponse.SC_OK, respEntity.getStatusCode().value());
		RestPageResponse<MLCatalog> restPageResponse = respEntity.getBody().getResponseBody();
		assertValidRestPageResponse(restPageResponse);
		List<MLCatalog> catalogs = restPageResponse.getContent();
		assertEquals(catalogs.size(), 1);
		MLCatalog catalog = catalogs.get(0);
		assertNotNull(catalog);
	}

	@Test
	public void getCatalogTest() {
		String catalogId = "12345678-abcd-90ab-cdef-1234567890ab";

		stubFor(get(urlEqualTo(String.format(CATALOG_ID_PATH, catalogId))).willReturn(aResponse()
				.withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
				.withBody("{\"accessTypeCode\": \"PB\"," + "\"catalogId\": \"12345678-abcd-90ab-cdef-1234567890ab\","
						+ "\"created\": \"2018-12-16T12:34:56.789Z\","
						+ "\"description\": \"A catalog of test models\","
						+ "\"modified\": \"2018-12-16T12:34:56.789Z\"," + "\"name\": \"Test Catalog\","
						+ "\"origin\": \"http://test.acumos.org/api\"," + "\"publisher\": \"Acumos\","
						+ "\"url\": \"http://test.company.com/api\"}")));

		ResponseEntity<JsonResponse<MLPCatalog>> respEntity = restTemplate.exchange(
				"http://localhost:" + randomServerPort + format(APINames.GET_CATALOG, catalogId), HttpMethod.GET, null,
				new ParameterizedTypeReference<JsonResponse<MLPCatalog>>() {
				});

		assertNotNull(respEntity);
		assertEquals(HttpServletResponse.SC_OK, respEntity.getStatusCode().value());
		MLPCatalog catalog = respEntity.getBody().getResponseBody();
		assertNotNull(catalog);
	}

	@Test
	public void createCatalogTest() {
		MLPCatalog catalog = getTestCatalog(false);
		JsonRequest<MLPCatalog> requestJson = new JsonRequest<>();
		requestJson.setBody(catalog);

		stubFor(post(urlEqualTo(CCDS_CATALOG_PATH)).willReturn(aResponse().withStatus(HttpStatus.SC_OK)
				.withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
				.withBody("{\"accessTypeCode\": \"PB\"," + "\"catalogId\": \"12345678-abcd-90ab-cdef-1234567890ab\","
						+ "\"created\": \"2018-12-16T12:34:56.789Z\","
						+ "\"description\": \"A catalog of test models\","
						+ "\"modified\": \"2018-12-16T12:34:56.789Z\"," + "\"name\": \"Test Catalog\","
						+ "\"origin\": \"http://test.acumos.org/api\"," + "\"publisher\": \"Acumos\","
						+ "\"url\": \"http://test.company.com/api\"}")));

		HttpHeaders headers = new HttpHeaders();
		HttpEntity<JsonRequest<MLPCatalog>> requestEntity = new HttpEntity<>(requestJson, headers);

		ResponseEntity<JsonResponse<MLPCatalog>> respEntity = restTemplate.exchange(
				"http://localhost:" + randomServerPort + APINames.CREATE_CATALOG, HttpMethod.POST, requestEntity,
				new ParameterizedTypeReference<JsonResponse<MLPCatalog>>() {
				});

		assertNotNull(respEntity);
		assertEquals(HttpServletResponse.SC_OK, respEntity.getStatusCode().value());
		MLPCatalog out = respEntity.getBody().getResponseBody();
		assertNotNull(out.getCatalogId());
		assertEquals(catalog.getAccessTypeCode(), out.getAccessTypeCode());
		assertEquals(catalog.getName(), out.getName());
		assertEquals(catalog.getPublisher(), out.getPublisher());
		assertEquals(catalog.getDescription(), out.getDescription());
		assertEquals(catalog.getUrl(), out.getUrl());
		assertEquals(catalog.getOrigin(), out.getOrigin());
		assertEquals(catalog.getCreated(), out.getCreated());
		assertEquals(catalog.getModified(), out.getModified());
	}

	@Test
	public void updateCatalogTest() {
		MLPCatalog catalog = getTestCatalog(true);
		JsonRequest<MLPCatalog> requestJson = new JsonRequest<>();
		requestJson.setBody(catalog);

		stubFor(put(urlEqualTo(String.format(CATALOG_ID_PATH, catalog.getCatalogId()))).willReturn(
				aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)));

		HttpHeaders headers = new HttpHeaders();
		HttpEntity<JsonRequest<MLPCatalog>> requestEntity = new HttpEntity<>(requestJson, headers);

		ResponseEntity<JsonResponse<Object>> respEntity = restTemplate.exchange(
				"http://localhost:" + randomServerPort + APINames.UPDATE_CATALOG, HttpMethod.PUT, requestEntity,
				new ParameterizedTypeReference<JsonResponse<Object>>() {
				});

		assertNotNull(respEntity);
		assertEquals(HttpServletResponse.SC_OK, respEntity.getStatusCode().value());
	}

	@Test
	public void deleteCatalogTest() {
		String catalogId = "12345678-abcd-90ab-cdef-1234567890ab";

		stubFor(delete(urlEqualTo(String.format(CATALOG_ID_PATH, catalogId))).willReturn(
				aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)));

		ResponseEntity<JsonResponse<Object>> respEntity = restTemplate.exchange(
				"http://localhost:" + randomServerPort + format(APINames.DELETE_CATALOG, catalogId), HttpMethod.DELETE,
				null, new ParameterizedTypeReference<JsonResponse<Object>>() {
				});

		assertNotNull(respEntity);
		assertEquals(HttpServletResponse.SC_OK, respEntity.getStatusCode().value());
	}

	@Test
	public void getPeerAccessCatalogIdsTest() {
		String peerId = "1234-1234-1234-1234-1234";
		String catalogId = "12345678-abcd-90ab-cdef-1234567890ab";

		stubFor(get(urlEqualTo(String.format(PEER_ACCESS_PATH, peerId))).willReturn(
				aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
						.withBody("[\"" + catalogId + "\"]")));

		ResponseEntity<JsonResponse<List<String>>> respEntity = restTemplate.exchange(
				"http://localhost:" + randomServerPort + format(APINames.GET_PEER_CATALOG_ACCESS, peerId),
				HttpMethod.GET, null, new ParameterizedTypeReference<JsonResponse<List<String>>>() {
				});

		assertNotNull(respEntity);
		assertEquals(HttpServletResponse.SC_OK, respEntity.getStatusCode().value());
		List<String> catalogIds = respEntity.getBody().getResponseBody();
		assertNotNull(catalogIds);
		assertEquals(catalogIds.size(), 1);
		assertEquals(catalogIds.get(0), catalogId);
	}

	@Test
	public void addPeerCatalogAccessTest() {
		String catalogId = "12345678-abcd-90ab-cdef-1234567890ab";
		String peerId = "1234-1234-1234-1234-1234";

		stubFor(post(urlEqualTo(String.format(ADD_DROP_PEER_ACCESS_PATH, peerId, catalogId))).willReturn(
				aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)));

		ResponseEntity<JsonResponse<Object>> respEntity = restTemplate.exchange(
				"http://localhost:" + randomServerPort + format(APINames.ADD_PEER_CATALOG_ACCESS, catalogId, peerId),
				HttpMethod.POST, null, new ParameterizedTypeReference<JsonResponse<Object>>() {
				});

		assertNotNull(respEntity);
		assertEquals(HttpServletResponse.SC_OK, respEntity.getStatusCode().value());
	}

	@Test
	public void dropPeerCatalogAccessTest() {
		String catalogId = "12345678-abcd-90ab-cdef-1234567890ab";
		String peerId = "1234-1234-1234-1234-1234";

		stubFor(delete(urlEqualTo(String.format(ADD_DROP_PEER_ACCESS_PATH, peerId, catalogId))).willReturn(
				aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)));

		ResponseEntity<JsonResponse<Object>> respEntity = restTemplate.exchange(
				"http://localhost:" + randomServerPort + format(APINames.DROP_PEER_CATALOG_ACCESS, catalogId, peerId),
				HttpMethod.DELETE, null, new ParameterizedTypeReference<JsonResponse<Object>>() {
				});

		assertNotNull(respEntity);
		assertEquals(HttpServletResponse.SC_OK, respEntity.getStatusCode().value());
	}

	@Test
	public void getCatalogSolutionCountTest() {
		String catalogId = "12345678-abcd-90ab-cdef-1234567890ab";
		Long count = new Long(7);

		stubFor(get(urlEqualTo(String.format(CATALOG_SOLUTION_COUNT_PATH, catalogId)))
				.willReturn(aResponse().withStatus(HttpStatus.SC_OK)
						.withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE).withBody(count.toString())));

		ResponseEntity<JsonResponse<Long>> respEntity = restTemplate.exchange(
				"http://localhost:" + randomServerPort + format(APINames.CATALOG_SOLUTION_COUNT, catalogId),
				HttpMethod.GET, null, new ParameterizedTypeReference<JsonResponse<Long>>() {
				});

		assertNotNull(respEntity);
		assertEquals(HttpServletResponse.SC_OK, respEntity.getStatusCode().value());
		Long result = respEntity.getBody().getResponseBody();
		assertEquals(count, result);
	}

	@Test
	public void getSolutionsInCatalogsTest() {
		String[] catalogIds = new String[2];
		catalogIds[0] = "12345678-abcd-90ab-cdef-1234567890ab";
		catalogIds[1] = "09876543-abcd-21ab-cdef-0987654321ab";

		JsonRequest<RestPageRequest> requestJson = new JsonRequest<>();
		requestJson.setBody(getTestRestPageRequest());

		stubFor(get(urlEqualTo(String.format(CATALOG_SOLUTION_PATH + "?ctlg=%s&ctlg=%s&" + PAGE_REQUEST_PARAMS,
				catalogIds[0], catalogIds[1]))).willReturn(aResponse().withStatus(HttpStatus.SC_OK)
						.withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
						.withBody("{\"content\":[" + "{\"created\":1535603044000," + "\"modified\":1536350829000,"
								+ "\"solutionId\":\"f226cc60-c2ec-4c2b-b05c-4a521f77e077\","
								+ "\"name\":\"TestSolution\"," + "\"metadata\":null," + "\"active\":true,"
								+ "\"modelTypeCode\":\"CL\"," + "\"toolkitTypeCode\":\"TF\"," + "\"origin\":null,"
								+ "\"userId\":\"bc961e2a-9506-4cf5-bbdb-009558b79e29\"," + "\"sourceId\":null,"
								+ "\"tags\":[{\"tag\":\"Test\"}]," + "\"viewCount\":12," + "\"downloadCount\":0,"
								+ "\"lastDownload\":1536364233000," + "\"ratingCount\":0,"
								+ "\"ratingAverageTenths\":0," + "\"featured\":false}]," + "\"last\":true,"
								+ "\"totalPages\":1," + "\"totalElements\":1," + "\"size\":9," + "\"number\":0,"
								+ "\"sort\":[{\"direction\":\"DESC\"," + "\"property\":\"modified\","
								+ "\"ignoreCase\":false," + "\"nullHandling\":\"NATIVE\"," + "\"ascending\":false,"
								+ "\"descending\":true}]," + "\"numberOfElements\":1," + "\"first\":true}")));

		HttpHeaders headers = new HttpHeaders();
		HttpEntity<JsonRequest<RestPageRequest>> requestEntity = new HttpEntity<>(requestJson, headers);

		ResponseEntity<JsonResponse<RestPageResponse<MLPSolution>>> respEntity = restTemplate.exchange(
				"http://localhost:" + randomServerPort + APINames.SOLUTIONS_IN_CATALOGS
						+ String.format("?ctlg=%s&ctlg=%s", catalogIds[0], catalogIds[1]),
				HttpMethod.POST, requestEntity,
				new ParameterizedTypeReference<JsonResponse<RestPageResponse<MLPSolution>>>() {
				});

		assertNotNull(respEntity);
		assertEquals(HttpServletResponse.SC_OK, respEntity.getStatusCode().value());
		RestPageResponse<MLPSolution> restPageResponse = respEntity.getBody().getResponseBody();
		assertValidRestPageResponse(restPageResponse);
		List<MLPSolution> solutions = restPageResponse.getContent();
		assertEquals(solutions.size(), 1);
		MLPSolution solution = solutions.get(0);
		assertNotNull(solution);
	}

	@Test
	public void getSolutionCatalogsTest() {
		String solutionId = "12345678-abcd-90ab-cdef-1234567890ab";

		stubFor(get(urlEqualTo(String.format(CATALOG_SOLUTION_ID_PATH, solutionId))).willReturn(aResponse()
				.withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
				.withBody("[{\"created\": \"2019-04-05T20:47:03Z\"," + "\"modified\": \"2019-04-05T20:47:03Z\","
						+ "\"catalogId\": \"12345678-abcd-90ab-cdef-1234567890ab\"," + "\"accessTypeCode\": \"PB\","
						+ "\"selfPublish\": false," + "\"name\": \"Test catalog\"," + "\"publisher\": \"Acumos\","
						+ "\"description\": null," + "\"origin\": null," + "\"url\": \"http://localhost\"}]")));

		ResponseEntity<JsonResponse<List<MLPCatalog>>> respEntity = restTemplate.exchange(
				"http://localhost:" + randomServerPort + format(APINames.GET_SOLUTION_CATALOGS, solutionId),
				HttpMethod.GET, null, new ParameterizedTypeReference<JsonResponse<List<MLPCatalog>>>() {
				});

		assertNotNull(respEntity);
		assertEquals(HttpServletResponse.SC_OK, respEntity.getStatusCode().value());
		List<MLPCatalog> catalogs = respEntity.getBody().getResponseBody();
		assertEquals(catalogs.size(), 1);
		MLPCatalog catalog = catalogs.get(0);
		assertNotNull(catalog);
	}

	@Test
	public void addSolutionToCatalogTest() {
		String solutionId = "f226cc60-c2ec-4c2b-b05c-4a521f77e077";
		String catalogId = "12345678-abcd-90ab-cdef-1234567890ab";

		stubFor(post(urlEqualTo(String.format(ADD_DROP_SOLUTION_PATH, catalogId, solutionId))).willReturn(
				aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)));

		ResponseEntity<JsonResponse<Object>> respEntity = restTemplate.exchange(
				"http://localhost:" + randomServerPort + format(APINames.ADD_CATALOG_SOLUTION, catalogId, solutionId),
				HttpMethod.POST, null, new ParameterizedTypeReference<JsonResponse<Object>>() {
				});

		assertNotNull(respEntity);
		assertEquals(HttpServletResponse.SC_OK, respEntity.getStatusCode().value());
	}

	@Test
	public void dropSolutionFromCatalogTest() {
		String solutionId = "f226cc60-c2ec-4c2b-b05c-4a521f77e077";
		String catalogId = "12345678-abcd-90ab-cdef-1234567890ab";

		stubFor(delete(urlEqualTo(String.format(ADD_DROP_SOLUTION_PATH, catalogId, solutionId))).willReturn(
				aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)));

		ResponseEntity<JsonResponse<Object>> respEntity = restTemplate.exchange(
				"http://localhost:" + randomServerPort + format(APINames.DROP_CATALOG_SOLUTION, catalogId, solutionId),
				HttpMethod.DELETE, null, new ParameterizedTypeReference<JsonResponse<Object>>() {
				});

		assertNotNull(respEntity);
		assertEquals(HttpServletResponse.SC_OK, respEntity.getStatusCode().value());
	}

	@Test
	public void getUserFavoriteCatalogsTest() {
		String userId = "1234-1234-1234-1234-1234";
		String catalogId = "12345678-abcd-90ab-cdef-1234567890ab";

		stubFor(get(urlEqualTo(String.format(GET_USER_FAVORITES_PATH, userId))).willReturn(
				aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
						.withBody("[\"" + catalogId + "\"]")));

		ResponseEntity<JsonResponse<List<String>>> respEntity = restTemplate.exchange(
				"http://localhost:" + randomServerPort + format(APINames.GET_USER_FAVORITE_CATALOGS, userId),
				HttpMethod.GET, null, new ParameterizedTypeReference<JsonResponse<List<String>>>() {
				});

		assertNotNull(respEntity);
		assertEquals(HttpServletResponse.SC_OK, respEntity.getStatusCode().value());
		List<String> catalogIds = respEntity.getBody().getResponseBody();
		assertNotNull(catalogIds);
		assertEquals(catalogIds.size(), 1);
		assertEquals(catalogIds.get(0), catalogId);
	}

	@Test
	public void addUserFavoriteCatalogTest() {
		String catalogId = "12345678-abcd-90ab-cdef-1234567890ab";
		String userId = "1234-1234-1234-1234-1234";

		stubFor(post(urlEqualTo(String.format(ADD_DROP_FAVORITES_PATH, catalogId, userId))).willReturn(
				aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)));

		ResponseEntity<JsonResponse<Object>> respEntity = restTemplate.exchange(
				"http://localhost:" + randomServerPort + format(APINames.ADD_USER_FAVORITE_CATALOG, catalogId, userId),
				HttpMethod.POST, null, new ParameterizedTypeReference<JsonResponse<Object>>() {
				});

		assertNotNull(respEntity);
		assertEquals(HttpServletResponse.SC_OK, respEntity.getStatusCode().value());
	}

	@Test
	public void dropUserFavoriteCatalogTest() {
		String catalogId = "12345678-abcd-90ab-cdef-1234567890ab";
		String userId = "1234-1234-1234-1234-1234";

		stubFor(delete(urlEqualTo(String.format(ADD_DROP_FAVORITES_PATH, catalogId, userId))).willReturn(
				aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)));

		ResponseEntity<JsonResponse<Object>> respEntity = restTemplate.exchange(
				"http://localhost:" + randomServerPort + format(APINames.DROP_USER_FAVORITE_CATALOG, catalogId, userId),
				HttpMethod.DELETE, null, new ParameterizedTypeReference<JsonResponse<Object>>() {
				});

		assertNotNull(respEntity);
		assertEquals(HttpServletResponse.SC_OK, respEntity.getStatusCode().value());
	}

	private MLPCatalog getTestCatalog(boolean hasCatalogId) {
		MLPCatalog catalog = new MLPCatalog();
		if (hasCatalogId) {
			catalog.setCatalogId("12345678-abcd-90ab-cdef-1234567890ab");
		}
		catalog.setAccessTypeCode("PB");
		catalog.setName("Test Catalog");
		catalog.setPublisher("Acumos");
		catalog.setDescription("A catalog of test models");
		catalog.setUrl("http://test.company.com/api");
		catalog.setOrigin("http://test.acumos.org/api");
		catalog.setCreated(Instant.parse("2018-12-16T12:34:56.789Z"));
		catalog.setModified(Instant.parse("2018-12-16T12:34:56.789Z"));
		return catalog;
	}

	private RestPageRequest getTestRestPageRequest() {
		Map<String, String> fieldToDirectionMap = new HashMap<>();
		fieldToDirectionMap.put("modified", "DESC");
		return new RestPageRequest(0, 9, fieldToDirectionMap);
	}

	private void assertValidRestPageResponse(RestPageResponse<?> response) {
		assertNotNull(response);
		assertEquals(response.getNumber(), 0);
		assertEquals(response.getSize(), 9);
		assertNotNull(response.getContent());
	}

	private String format(String path, Object... args) {
		return String.format(path.replaceAll("\\{\\w*\\}", "%s"), args);
	}
}
