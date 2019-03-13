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
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.Assert.*;

import javax.servlet.http.HttpServletResponse;

import org.acumos.cds.domain.MLPSiteContent;
import org.acumos.portal.be.APINames;
import org.acumos.portal.be.common.ConfigConstants;
import org.acumos.portal.be.common.JsonRequest;
import org.acumos.portal.be.common.JsonResponse;
import org.acumos.portal.be.service.impl.SiteContentServiceImpl;
import org.apache.http.HttpStatus;
import org.glassfish.jersey.internal.util.Base64;
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
public class SiteContentControllerTest {

	@Rule
	public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().port(8000));

	private RestTemplate restTemplate = new RestTemplate();

	@LocalServerPort
	int randomServerPort;

	private static final String PATH_TERMS_CONDITIONS = APINames.SITE_PATH + APINames.GET_TERMS_CONDITIONS;
	private static final String PATH_ONBOARDING_OVERVIEW = APINames.SITE_PATH + APINames.GET_ONBOARDING_OVERVIEW;
	private static final String PATH_CONTACT_INFO = APINames.SITE_PATH + APINames.GET_CONTACT_INFO;
	private static final String PATH_COBRAND_LOGO = APINames.SITE_PATH + APINames.GET_COBRAND_LOGO;
	private static final String PATH_GET_CAROUSEL_PICTURE = APINames.SITE_PATH + APINames.UPDATE_CAROUSEL_PICTURE + "/";
	private static final String PATH_SET_CAROUSEL_PICTURE = APINames.SITE_PATH + APINames.UPDATE_CAROUSEL_PICTURE;
	private static final String PATH_DELETE_CAROUSEL_PICTURE = PATH_GET_CAROUSEL_PICTURE;

	private static final String GET_CONTENT = "/ccds/site/content/";
	private static final String SET_CONTENT = "/ccds/site/content";
	private static final String CAROUSEL_TEST_KEY = "carousel.top.test.bgImg";

	@Test
	public void getTermsConditionsTest() {
		String contentString = "{\"description\":\"<p>Test</p>\"}";
		String contentValue = Base64.encodeAsString(contentString);

		stubFor(get(urlEqualTo(GET_CONTENT + SiteContentServiceImpl.KEY_TERMS_CONDITIONS)).willReturn(
				aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
						.withBody("{\"created\": \"2019-02-01T21:58:49Z\"," + "\"modified\": \"2019-02-01T21:58:49Z\","
								+ "\"contentKey\": \"" + SiteContentServiceImpl.KEY_TERMS_CONDITIONS + "\","
								+ "\"contentValue\": \"" + contentValue + "\"," + "\"mimeType\": \""
								+ MediaType.APPLICATION_JSON_VALUE + "\"}")));

		ResponseEntity<JsonResponse<MLPSiteContent>> contentResponse = restTemplate.exchange(
				"http://localhost:" + randomServerPort + PATH_TERMS_CONDITIONS, HttpMethod.GET, null,
				new ParameterizedTypeReference<JsonResponse<MLPSiteContent>>() {
				});

		assertNotNull(contentResponse);
		assertEquals(HttpServletResponse.SC_OK, contentResponse.getStatusCode().value());
		MLPSiteContent content = contentResponse.getBody().getResponseBody();
		assertEquals(SiteContentServiceImpl.KEY_TERMS_CONDITIONS, content.getContentKey());
		assertEquals(contentString, new String(content.getContentValue()));
		assertEquals(MediaType.APPLICATION_JSON_VALUE, content.getMimeType());
	}

	@Test
	public void createTermsConditionsTest() {
		String contentString = "{\"description\":\"<p>Test</p>\"}";
		String contentValue = Base64.encodeAsString(contentString);
		MLPSiteContent request = new MLPSiteContent(SiteContentServiceImpl.KEY_TERMS_CONDITIONS,
				contentValue.getBytes(), MediaType.APPLICATION_JSON_VALUE);
		JsonRequest<MLPSiteContent> reqObj = new JsonRequest<>();
		reqObj.setBody(request);

		stubFor(get(urlEqualTo(GET_CONTENT + SiteContentServiceImpl.KEY_TERMS_CONDITIONS)).willReturn(
				aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)));

		stubFor(post(urlEqualTo(SET_CONTENT)).willReturn(
				aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)));

		HttpHeaders headers = new HttpHeaders();
		HttpEntity<JsonRequest<MLPSiteContent>> requestEntity = new HttpEntity<>(reqObj, headers);
		ResponseEntity<JsonResponse<Object>> contentResponse = restTemplate.exchange(
				"http://localhost:" + randomServerPort + PATH_TERMS_CONDITIONS, HttpMethod.POST, requestEntity,
				new ParameterizedTypeReference<JsonResponse<Object>>() {
				});

		assertNotNull(contentResponse);
		assertEquals(HttpServletResponse.SC_OK, contentResponse.getStatusCode().value());
	}

	@Test
	public void updateTermsConditionsTest() {
		String contentString = "{\"description\":\"<p>Test</p>\"}";
		String contentValue = Base64.encodeAsString(contentString);
		MLPSiteContent request = new MLPSiteContent(SiteContentServiceImpl.KEY_TERMS_CONDITIONS,
				contentValue.getBytes(), MediaType.APPLICATION_JSON_VALUE);
		JsonRequest<MLPSiteContent> reqObj = new JsonRequest<>();
		reqObj.setBody(request);

		stubFor(get(urlEqualTo(GET_CONTENT + SiteContentServiceImpl.KEY_TERMS_CONDITIONS)).willReturn(
				aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
						.withBody("{\"created\": \"2019-02-01T21:58:49Z\"," + "\"modified\": \"2019-02-01T21:58:49Z\","
								+ "\"contentKey\": \"" + SiteContentServiceImpl.KEY_TERMS_CONDITIONS + "\","
								+ "\"contentValue\": \"" + contentValue + "\"," + "\"mimeType\": \""
								+ MediaType.APPLICATION_JSON_VALUE + "\"}")));

		stubFor(put(urlEqualTo(GET_CONTENT + SiteContentServiceImpl.KEY_TERMS_CONDITIONS)).willReturn(
				aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)));

		HttpHeaders headers = new HttpHeaders();
		HttpEntity<JsonRequest<MLPSiteContent>> requestEntity = new HttpEntity<>(reqObj, headers);
		ResponseEntity<JsonResponse<Object>> contentResponse = restTemplate.exchange(
				"http://localhost:" + randomServerPort + PATH_TERMS_CONDITIONS, HttpMethod.POST, requestEntity,
				new ParameterizedTypeReference<JsonResponse<Object>>() {
				});

		assertNotNull(contentResponse);
		assertEquals(HttpServletResponse.SC_OK, contentResponse.getStatusCode().value());
	}

	@Test
	public void getOnboardingOverviewTest() {
		String contentString = "{\"description\":\"<p>Test</p>\"}";
		String contentValue = Base64.encodeAsString(contentString);

		stubFor(get(urlEqualTo(GET_CONTENT + SiteContentServiceImpl.KEY_ONBOARDING_OVERVIEW)).willReturn(
				aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
						.withBody("{\"created\": \"2019-02-01T21:58:49Z\"," + "\"modified\": \"2019-02-01T21:58:49Z\","
								+ "\"contentKey\": \"" + SiteContentServiceImpl.KEY_ONBOARDING_OVERVIEW + "\","
								+ "\"contentValue\": \"" + contentValue + "\"," + "\"mimeType\": \""
								+ MediaType.APPLICATION_JSON_VALUE + "\"}")));

		ResponseEntity<JsonResponse<MLPSiteContent>> contentResponse = restTemplate.exchange(
				"http://localhost:" + randomServerPort + PATH_ONBOARDING_OVERVIEW, HttpMethod.GET, null,
				new ParameterizedTypeReference<JsonResponse<MLPSiteContent>>() {
				});

		assertNotNull(contentResponse);
		assertEquals(HttpServletResponse.SC_OK, contentResponse.getStatusCode().value());
		MLPSiteContent content = contentResponse.getBody().getResponseBody();
		assertEquals(SiteContentServiceImpl.KEY_ONBOARDING_OVERVIEW, content.getContentKey());
		assertEquals(contentString, new String(content.getContentValue()));
		assertEquals(MediaType.APPLICATION_JSON_VALUE, content.getMimeType());
	}

	@Test
	public void createOnboardingOverviewTest() {
		String contentString = "{\"description\":\"<p>Test</p>\"}";
		String contentValue = Base64.encodeAsString(contentString);
		MLPSiteContent request = new MLPSiteContent(SiteContentServiceImpl.KEY_ONBOARDING_OVERVIEW,
				contentValue.getBytes(), MediaType.APPLICATION_JSON_VALUE);
		JsonRequest<MLPSiteContent> reqObj = new JsonRequest<>();
		reqObj.setBody(request);

		stubFor(get(urlEqualTo(GET_CONTENT + SiteContentServiceImpl.KEY_ONBOARDING_OVERVIEW)).willReturn(
				aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)));

		stubFor(post(urlEqualTo(SET_CONTENT)).willReturn(
				aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)));

		HttpHeaders headers = new HttpHeaders();
		HttpEntity<JsonRequest<MLPSiteContent>> requestEntity = new HttpEntity<>(reqObj, headers);
		ResponseEntity<JsonResponse<Object>> contentResponse = restTemplate.exchange(
				"http://localhost:" + randomServerPort + PATH_ONBOARDING_OVERVIEW, HttpMethod.POST, requestEntity,
				new ParameterizedTypeReference<JsonResponse<Object>>() {
				});

		assertNotNull(contentResponse);
		assertEquals(HttpServletResponse.SC_OK, contentResponse.getStatusCode().value());
	}

	@Test
	public void updateOnboardingOverviewTest() {
		String contentString = "{\"description\":\"<p>Test</p>\"}";
		String contentValue = Base64.encodeAsString(contentString);
		MLPSiteContent request = new MLPSiteContent(SiteContentServiceImpl.KEY_ONBOARDING_OVERVIEW,
				contentValue.getBytes(), MediaType.APPLICATION_JSON_VALUE);
		JsonRequest<MLPSiteContent> reqObj = new JsonRequest<>();
		reqObj.setBody(request);

		stubFor(get(urlEqualTo(GET_CONTENT + SiteContentServiceImpl.KEY_ONBOARDING_OVERVIEW)).willReturn(
				aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
						.withBody("{\"created\": \"2019-02-01T21:58:49Z\"," + "\"modified\": \"2019-02-01T21:58:49Z\","
								+ "\"contentKey\": \"" + SiteContentServiceImpl.KEY_ONBOARDING_OVERVIEW + "\","
								+ "\"contentValue\": \"" + contentValue + "\"," + "\"mimeType\": \""
								+ MediaType.APPLICATION_JSON_VALUE + "\"}")));

		stubFor(put(urlEqualTo(GET_CONTENT + SiteContentServiceImpl.KEY_ONBOARDING_OVERVIEW)).willReturn(
				aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)));

		HttpHeaders headers = new HttpHeaders();
		HttpEntity<JsonRequest<MLPSiteContent>> requestEntity = new HttpEntity<>(reqObj, headers);
		ResponseEntity<JsonResponse<Object>> contentResponse = restTemplate.exchange(
				"http://localhost:" + randomServerPort + PATH_ONBOARDING_OVERVIEW, HttpMethod.POST, requestEntity,
				new ParameterizedTypeReference<JsonResponse<Object>>() {
				});

		assertNotNull(contentResponse);
		assertEquals(HttpServletResponse.SC_OK, contentResponse.getStatusCode().value());
	}

	@Test
	public void getContactInfoTest() {
		String contentString = "{\"description\":\"<p>Test</p>\"}";
		String contentValue = Base64.encodeAsString(contentString);

		stubFor(get(urlEqualTo(GET_CONTENT + SiteContentServiceImpl.KEY_CONTACT_INFO)).willReturn(
				aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
						.withBody("{\"created\": \"2019-02-01T21:58:49Z\"," + "\"modified\": \"2019-02-01T21:58:49Z\","
								+ "\"contentKey\": \"" + SiteContentServiceImpl.KEY_CONTACT_INFO + "\","
								+ "\"contentValue\": \"" + contentValue + "\"," + "\"mimeType\": \""
								+ MediaType.APPLICATION_JSON_VALUE + "\"}")));

		ResponseEntity<JsonResponse<MLPSiteContent>> contentResponse = restTemplate.exchange(
				"http://localhost:" + randomServerPort + PATH_CONTACT_INFO, HttpMethod.GET, null,
				new ParameterizedTypeReference<JsonResponse<MLPSiteContent>>() {
				});

		assertNotNull(contentResponse);
		assertEquals(HttpServletResponse.SC_OK, contentResponse.getStatusCode().value());
		MLPSiteContent content = contentResponse.getBody().getResponseBody();
		assertEquals(SiteContentServiceImpl.KEY_CONTACT_INFO, content.getContentKey());
		assertEquals(contentString, new String(content.getContentValue()));
		assertEquals(MediaType.APPLICATION_JSON_VALUE, content.getMimeType());
	}

	@Test
	public void createContactInfoTest() {
		String contentString = "{\"description\":\"<p>Test</p>\"}";
		String contentValue = Base64.encodeAsString(contentString);
		MLPSiteContent request = new MLPSiteContent(SiteContentServiceImpl.KEY_CONTACT_INFO, contentValue.getBytes(),
				MediaType.APPLICATION_JSON_VALUE);
		JsonRequest<MLPSiteContent> reqObj = new JsonRequest<>();
		reqObj.setBody(request);

		stubFor(get(urlEqualTo(GET_CONTENT + SiteContentServiceImpl.KEY_CONTACT_INFO)).willReturn(
				aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)));

		stubFor(post(urlEqualTo(SET_CONTENT)).willReturn(
				aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)));

		HttpHeaders headers = new HttpHeaders();
		HttpEntity<JsonRequest<MLPSiteContent>> requestEntity = new HttpEntity<>(reqObj, headers);
		ResponseEntity<JsonResponse<Object>> contentResponse = restTemplate.exchange(
				"http://localhost:" + randomServerPort + PATH_CONTACT_INFO, HttpMethod.POST, requestEntity,
				new ParameterizedTypeReference<JsonResponse<Object>>() {
				});

		assertNotNull(contentResponse);
		assertEquals(HttpServletResponse.SC_OK, contentResponse.getStatusCode().value());
	}

	@Test
	public void updateContactInfoTest() {
		String contentString = "{\"description\":\"<p>Test</p>\"}";
		String contentValue = Base64.encodeAsString(contentString);
		MLPSiteContent request = new MLPSiteContent(SiteContentServiceImpl.KEY_CONTACT_INFO, contentValue.getBytes(),
				MediaType.APPLICATION_JSON_VALUE);
		JsonRequest<MLPSiteContent> reqObj = new JsonRequest<>();
		reqObj.setBody(request);

		stubFor(get(urlEqualTo(GET_CONTENT + SiteContentServiceImpl.KEY_CONTACT_INFO)).willReturn(
				aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
						.withBody("{\"created\": \"2019-02-01T21:58:49Z\"," + "\"modified\": \"2019-02-01T21:58:49Z\","
								+ "\"contentKey\": \"" + SiteContentServiceImpl.KEY_CONTACT_INFO + "\","
								+ "\"contentValue\": \"" + contentValue + "\"," + "\"mimeType\": \""
								+ MediaType.APPLICATION_JSON_VALUE + "\"}")));

		stubFor(put(urlEqualTo(GET_CONTENT + SiteContentServiceImpl.KEY_CONTACT_INFO)).willReturn(
				aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)));

		HttpHeaders headers = new HttpHeaders();
		HttpEntity<JsonRequest<MLPSiteContent>> requestEntity = new HttpEntity<>(reqObj, headers);
		ResponseEntity<JsonResponse<Object>> contentResponse = restTemplate.exchange(
				"http://localhost:" + randomServerPort + PATH_CONTACT_INFO, HttpMethod.POST, requestEntity,
				new ParameterizedTypeReference<JsonResponse<Object>>() {
				});

		assertNotNull(contentResponse);
		assertEquals(HttpServletResponse.SC_OK, contentResponse.getStatusCode().value());
	}

	@Test
	public void getCobrandLogoTest() {
		String contentString = "Placeholder for an actual image";
		String contentValue = Base64.encodeAsString(contentString);

		stubFor(get(urlEqualTo(GET_CONTENT + SiteContentServiceImpl.KEY_COBRAND_LOGO)).willReturn(
				aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
						.withBody("{\"created\": \"2019-02-01T21:58:49Z\"," + "\"modified\": \"2019-02-01T21:58:49Z\","
								+ "\"contentKey\": \"" + SiteContentServiceImpl.KEY_COBRAND_LOGO + "\","
								+ "\"contentValue\": \"" + contentValue + "\"," + "\"mimeType\": \"image/png\"}")));

		ResponseEntity<byte[]> contentResponse = restTemplate.exchange(
				"http://localhost:" + randomServerPort + PATH_COBRAND_LOGO, HttpMethod.GET, null,
				new ParameterizedTypeReference<byte[]>() {
				});

		assertNotNull(contentResponse);
		assertEquals(HttpServletResponse.SC_OK, contentResponse.getStatusCode().value());
		assertEquals(contentString, new String(contentResponse.getBody()));
	}

	@Test
	public void createCobrandLogoTest() {
		String contentString = "Placeholder for an actual image";
		String contentValue = Base64.encodeAsString(contentString);
		MLPSiteContent request = new MLPSiteContent(SiteContentServiceImpl.KEY_COBRAND_LOGO, contentValue.getBytes(),
				"image/png");
		JsonRequest<MLPSiteContent> reqObj = new JsonRequest<>();
		reqObj.setBody(request);

		stubFor(get(urlEqualTo(GET_CONTENT + SiteContentServiceImpl.KEY_COBRAND_LOGO)).willReturn(
				aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)));

		stubFor(post(urlEqualTo(SET_CONTENT)).willReturn(
				aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)));

		HttpHeaders headers = new HttpHeaders();
		HttpEntity<JsonRequest<MLPSiteContent>> requestEntity = new HttpEntity<>(reqObj, headers);
		ResponseEntity<JsonResponse<Object>> contentResponse = restTemplate.exchange(
				"http://localhost:" + randomServerPort + PATH_COBRAND_LOGO, HttpMethod.POST, requestEntity,
				new ParameterizedTypeReference<JsonResponse<Object>>() {
				});

		assertNotNull(contentResponse);
		assertEquals(HttpServletResponse.SC_OK, contentResponse.getStatusCode().value());
	}

	@Test
	public void updateCobrandLogoTest() {
		String contentString = "Placeholder for an actual image";
		String contentValue = Base64.encodeAsString(contentString);
		MLPSiteContent request = new MLPSiteContent(SiteContentServiceImpl.KEY_COBRAND_LOGO, contentValue.getBytes(),
				"image/png");
		JsonRequest<MLPSiteContent> reqObj = new JsonRequest<>();
		reqObj.setBody(request);

		stubFor(get(urlEqualTo(GET_CONTENT + SiteContentServiceImpl.KEY_COBRAND_LOGO)).willReturn(
				aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
						.withBody("{\"created\": \"2019-02-01T21:58:49Z\"," + "\"modified\": \"2019-02-01T21:58:49Z\","
								+ "\"contentKey\": \"" + SiteContentServiceImpl.KEY_COBRAND_LOGO + "\","
								+ "\"contentValue\": \"" + contentValue + "\"," + "\"mimeType\": \"image/png\"}")));

		stubFor(put(urlEqualTo(GET_CONTENT + SiteContentServiceImpl.KEY_COBRAND_LOGO)).willReturn(
				aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)));

		HttpHeaders headers = new HttpHeaders();
		HttpEntity<JsonRequest<MLPSiteContent>> requestEntity = new HttpEntity<>(reqObj, headers);
		ResponseEntity<JsonResponse<Object>> contentResponse = restTemplate.exchange(
				"http://localhost:" + randomServerPort + PATH_COBRAND_LOGO, HttpMethod.POST, requestEntity,
				new ParameterizedTypeReference<JsonResponse<Object>>() {
				});

		assertNotNull(contentResponse);
		assertEquals(HttpServletResponse.SC_OK, contentResponse.getStatusCode().value());
	}

	@Test
	public void deleteCobrandLogoTest() {
		stubFor(delete(urlEqualTo(GET_CONTENT + SiteContentServiceImpl.KEY_COBRAND_LOGO)).willReturn(
				aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)));

		ResponseEntity<JsonResponse<Object>> contentResponse = restTemplate.exchange(
				"http://localhost:" + randomServerPort + PATH_COBRAND_LOGO, HttpMethod.DELETE, null,
				new ParameterizedTypeReference<JsonResponse<Object>>() {
				});

		assertNotNull(contentResponse);
		assertEquals(HttpServletResponse.SC_OK, contentResponse.getStatusCode().value());
	}

	@Test
	public void deleteCobrandLogoFailTest() {
		stubFor(delete(urlEqualTo(GET_CONTENT + SiteContentServiceImpl.KEY_COBRAND_LOGO)).willReturn(aResponse()
				.withStatus(HttpStatus.SC_BAD_REQUEST).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)));

		ResponseEntity<JsonResponse<Object>> contentResponse = restTemplate.exchange(
				"http://localhost:" + randomServerPort + PATH_COBRAND_LOGO, HttpMethod.DELETE, null,
				new ParameterizedTypeReference<JsonResponse<Object>>() {
				});

		assertNotNull(contentResponse);
		assertEquals("Exception Occurred Deleting Cobrand Logo", contentResponse.getBody().getResponseDetail());
	}

	@Test
	public void getCarouselPictureTest() {
		String contentString = "Placeholder for an actual image";
		String contentValue = Base64.encodeAsString(contentString);

		stubFor(get(urlEqualTo(GET_CONTENT + CAROUSEL_TEST_KEY)).willReturn(
				aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
						.withBody("{\"created\": \"2019-02-01T21:58:49Z\"," + "\"modified\": \"2019-02-01T21:58:49Z\","
								+ "\"contentKey\": \"" + CAROUSEL_TEST_KEY + "\"," + "\"contentValue\": \""
								+ contentValue + "\"," + "\"mimeType\": \"image/png\"}")));

		ResponseEntity<byte[]> contentResponse = restTemplate.exchange(
				"http://localhost:" + randomServerPort + PATH_GET_CAROUSEL_PICTURE + CAROUSEL_TEST_KEY, HttpMethod.GET,
				null, new ParameterizedTypeReference<byte[]>() {
				});

		assertNotNull(contentResponse);
		assertEquals(HttpServletResponse.SC_OK, contentResponse.getStatusCode().value());
		assertEquals(contentString, new String(contentResponse.getBody()));
	}

	@Test
	public void createCarouselPictureTest() {
		String contentString = "Placeholder for an actual image";
		String contentValue = Base64.encodeAsString(contentString);
		MLPSiteContent request = new MLPSiteContent(CAROUSEL_TEST_KEY, contentValue.getBytes(), "image/png");
		JsonRequest<MLPSiteContent> reqObj = new JsonRequest<>();
		reqObj.setBody(request);

		stubFor(get(urlEqualTo(GET_CONTENT + CAROUSEL_TEST_KEY)).willReturn(
				aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)));

		stubFor(post(urlEqualTo(SET_CONTENT)).willReturn(
				aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)));

		HttpHeaders headers = new HttpHeaders();
		HttpEntity<JsonRequest<MLPSiteContent>> requestEntity = new HttpEntity<>(reqObj, headers);
		ResponseEntity<JsonResponse<Object>> contentResponse = restTemplate.exchange(
				"http://localhost:" + randomServerPort + PATH_SET_CAROUSEL_PICTURE, HttpMethod.POST, requestEntity,
				new ParameterizedTypeReference<JsonResponse<Object>>() {
				});

		assertNotNull(contentResponse);
		assertEquals(HttpServletResponse.SC_OK, contentResponse.getStatusCode().value());
	}

	@Test
	public void updateCarouselPictureTest() {
		String contentString = "Placeholder for an actual image";
		String contentValue = Base64.encodeAsString(contentString);
		MLPSiteContent request = new MLPSiteContent(CAROUSEL_TEST_KEY, contentValue.getBytes(), "image/png");
		JsonRequest<MLPSiteContent> reqObj = new JsonRequest<>();
		reqObj.setBody(request);

		stubFor(get(urlEqualTo(GET_CONTENT + CAROUSEL_TEST_KEY)).willReturn(
				aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
						.withBody("{\"created\": \"2019-02-01T21:58:49Z\"," + "\"modified\": \"2019-02-01T21:58:49Z\","
								+ "\"contentKey\": \"" + CAROUSEL_TEST_KEY + "\"," + "\"contentValue\": \""
								+ contentValue + "\"," + "\"mimeType\": \"image/png\"}")));

		stubFor(put(urlEqualTo(GET_CONTENT + CAROUSEL_TEST_KEY)).willReturn(
				aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)));

		HttpHeaders headers = new HttpHeaders();
		HttpEntity<JsonRequest<MLPSiteContent>> requestEntity = new HttpEntity<>(reqObj, headers);
		ResponseEntity<JsonResponse<Object>> contentResponse = restTemplate.exchange(
				"http://localhost:" + randomServerPort + PATH_SET_CAROUSEL_PICTURE, HttpMethod.POST, requestEntity,
				new ParameterizedTypeReference<JsonResponse<Object>>() {
				});

		assertNotNull(contentResponse);
		assertEquals(HttpServletResponse.SC_OK, contentResponse.getStatusCode().value());
	}

	@Test
	public void deleteCarouselPictureTest() {
		stubFor(delete(urlEqualTo(GET_CONTENT + CAROUSEL_TEST_KEY)).willReturn(
				aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)));

		ResponseEntity<JsonResponse<Object>> contentResponse = restTemplate.exchange(
				"http://localhost:" + randomServerPort + PATH_DELETE_CAROUSEL_PICTURE + CAROUSEL_TEST_KEY,
				HttpMethod.DELETE, null, new ParameterizedTypeReference<JsonResponse<Object>>() {
				});

		assertNotNull(contentResponse);
		assertEquals(HttpServletResponse.SC_OK, contentResponse.getStatusCode().value());
	}

	@Test
	public void deleteCarouselPictureFailTest() {
		stubFor(delete(urlEqualTo(GET_CONTENT + CAROUSEL_TEST_KEY)).willReturn(aResponse()
				.withStatus(HttpStatus.SC_BAD_REQUEST).withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)));

		ResponseEntity<JsonResponse<Object>> contentResponse = restTemplate.exchange(
				"http://localhost:" + randomServerPort + PATH_DELETE_CAROUSEL_PICTURE + CAROUSEL_TEST_KEY,
				HttpMethod.DELETE, null, new ParameterizedTypeReference<JsonResponse<Object>>() {
				});

		assertNotNull(contentResponse);
		assertEquals("Exception Occurred Deleting Carousel Picture", contentResponse.getBody().getResponseDetail());
	}
}
