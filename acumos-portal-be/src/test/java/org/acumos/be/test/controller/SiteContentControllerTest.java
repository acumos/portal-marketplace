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

	@Test
	public void getTermsConditionsTest() {
		String contentString = "{\"description\":\"<p>Test</p>\"}";
		String contentValue = Base64.encodeAsString(contentString);

		stubFor(get(urlEqualTo("/ccds/site/content/" + SiteContentServiceImpl.KEY_TERMS_CONDITIONS))
				.willReturn(aResponse().withStatus(HttpStatus.SC_OK)
						.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())
						.withBody("{\"created\": \"2019-02-01T21:58:49Z\"," + "\"modified\": \"2019-02-01T21:58:49Z\","
								+ "\"contentKey\": \"" + SiteContentServiceImpl.KEY_TERMS_CONDITIONS + "\","
								+ "\"contentValue\": \"" + contentValue + "\","
								+ "\"mimeType\": \"application/json\"}")));

		ResponseEntity<JsonResponse<MLPSiteContent>> contentResponse = restTemplate.exchange(
				"http://localhost:" + randomServerPort + "/site/content/global/termsConditions", HttpMethod.GET, null,
				new ParameterizedTypeReference<JsonResponse<MLPSiteContent>>() {
				});

		assertNotNull(contentResponse);
		assertEquals(HttpServletResponse.SC_OK, contentResponse.getStatusCode().value());
		MLPSiteContent content = contentResponse.getBody().getResponseBody();
		assertEquals(SiteContentServiceImpl.KEY_TERMS_CONDITIONS, content.getContentKey());
		assertEquals(contentString, new String(content.getContentValue()));
		assertEquals("application/json", content.getMimeType());
	}

	@Test
	public void createTermsConditionsTest() {
		String contentString = "{\"description\":\"<p>Test</p>\"}";
		String contentValue = Base64.encodeAsString(contentString);
		MLPSiteContent request = new MLPSiteContent(SiteContentServiceImpl.KEY_TERMS_CONDITIONS,
				contentValue.getBytes(), "application/json");
		JsonRequest<MLPSiteContent> reqObj = new JsonRequest<>();
		reqObj.setBody(request);

		stubFor(get(urlEqualTo("/ccds/site/content/" + SiteContentServiceImpl.KEY_TERMS_CONDITIONS))
				.willReturn(aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type",
						MediaType.APPLICATION_JSON.toString())));

		stubFor(post(urlEqualTo("/ccds/site/content")).willReturn(aResponse().withStatus(HttpStatus.SC_OK)
				.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())));

		HttpHeaders headers = new HttpHeaders();
		HttpEntity<JsonRequest<MLPSiteContent>> requestEntity = new HttpEntity<>(reqObj, headers);
		ResponseEntity<JsonResponse<Object>> contentResponse = restTemplate.exchange(
				"http://localhost:" + randomServerPort + "/site/content/global/termsConditions", HttpMethod.POST,
				requestEntity, new ParameterizedTypeReference<JsonResponse<Object>>() {
				});

		assertNotNull(contentResponse);
		assertEquals(HttpServletResponse.SC_OK, contentResponse.getStatusCode().value());
	}

	@Test
	public void updateTermsConditionsTest() {
		String contentString = "{\"description\":\"<p>Test</p>\"}";
		String contentValue = Base64.encodeAsString(contentString);
		MLPSiteContent request = new MLPSiteContent(SiteContentServiceImpl.KEY_TERMS_CONDITIONS,
				contentValue.getBytes(), "application/json");
		JsonRequest<MLPSiteContent> reqObj = new JsonRequest<>();
		reqObj.setBody(request);

		stubFor(get(urlEqualTo("/ccds/site/content/" + SiteContentServiceImpl.KEY_TERMS_CONDITIONS))
				.willReturn(aResponse().withStatus(HttpStatus.SC_OK)
						.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())
						.withBody("{\"created\": \"2019-02-01T21:58:49Z\"," + "\"modified\": \"2019-02-01T21:58:49Z\","
								+ "\"contentKey\": \"" + SiteContentServiceImpl.KEY_TERMS_CONDITIONS + "\","
								+ "\"contentValue\": \"" + contentValue + "\"," + "\"mimeType\": \"application/json\"}")));

		stubFor(put(urlEqualTo("/ccds/site/content/" + SiteContentServiceImpl.KEY_TERMS_CONDITIONS))
				.willReturn(aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type",
						MediaType.APPLICATION_JSON.toString())));

		HttpHeaders headers = new HttpHeaders();
		HttpEntity<JsonRequest<MLPSiteContent>> requestEntity = new HttpEntity<>(reqObj, headers);
		ResponseEntity<JsonResponse<Object>> contentResponse = restTemplate.exchange(
				"http://localhost:" + randomServerPort + "/site/content/global/termsConditions", HttpMethod.POST,
				requestEntity, new ParameterizedTypeReference<JsonResponse<Object>>() {
				});

		assertNotNull(contentResponse);
		assertEquals(HttpServletResponse.SC_OK, contentResponse.getStatusCode().value());
	}

	@Test
	public void getOnboardingOverviewTest() {
		String contentString = "{\"description\":\"<p>Test</p>\"}";
		String contentValue = Base64.encodeAsString(contentString);

		stubFor(get(urlEqualTo("/ccds/site/content/" + SiteContentServiceImpl.KEY_ONBOARDING_OVERVIEW))
				.willReturn(aResponse().withStatus(HttpStatus.SC_OK)
						.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())
						.withBody("{\"created\": \"2019-02-01T21:58:49Z\"," + "\"modified\": \"2019-02-01T21:58:49Z\","
								+ "\"contentKey\": \"" + SiteContentServiceImpl.KEY_ONBOARDING_OVERVIEW + "\","
								+ "\"contentValue\": \"" + contentValue + "\","
								+ "\"mimeType\": \"application/json\"}")));

		ResponseEntity<JsonResponse<MLPSiteContent>> contentResponse = restTemplate.exchange(
				"http://localhost:" + randomServerPort + "/site/content/global/onboarding/overview", HttpMethod.GET, null,
				new ParameterizedTypeReference<JsonResponse<MLPSiteContent>>() {
				});

		assertNotNull(contentResponse);
		assertEquals(HttpServletResponse.SC_OK, contentResponse.getStatusCode().value());
		MLPSiteContent content = contentResponse.getBody().getResponseBody();
		assertEquals(SiteContentServiceImpl.KEY_ONBOARDING_OVERVIEW, content.getContentKey());
		assertEquals(contentString, new String(content.getContentValue()));
		assertEquals("application/json", content.getMimeType());
	}

	@Test
	public void createOnboardingOverviewTest() {
		String contentString = "{\"description\":\"<p>Test</p>\"}";
		String contentValue = Base64.encodeAsString(contentString);
		MLPSiteContent request = new MLPSiteContent(SiteContentServiceImpl.KEY_ONBOARDING_OVERVIEW,
				contentValue.getBytes(), "application/json");
		JsonRequest<MLPSiteContent> reqObj = new JsonRequest<>();
		reqObj.setBody(request);

		stubFor(get(urlEqualTo("/ccds/site/content/" + SiteContentServiceImpl.KEY_ONBOARDING_OVERVIEW))
				.willReturn(aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type",
						MediaType.APPLICATION_JSON.toString())));

		stubFor(post(urlEqualTo("/ccds/site/content")).willReturn(aResponse().withStatus(HttpStatus.SC_OK)
				.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())));

		HttpHeaders headers = new HttpHeaders();
		HttpEntity<JsonRequest<MLPSiteContent>> requestEntity = new HttpEntity<>(reqObj, headers);
		ResponseEntity<JsonResponse<Object>> contentResponse = restTemplate.exchange(
				"http://localhost:" + randomServerPort + "/site/content/global/onboarding/overview", HttpMethod.POST,
				requestEntity, new ParameterizedTypeReference<JsonResponse<Object>>() {
				});

		assertNotNull(contentResponse);
		assertEquals(HttpServletResponse.SC_OK, contentResponse.getStatusCode().value());
	}

	@Test
	public void updateOnboardingOverviewTest() {
		String contentString = "{\"description\":\"<p>Test</p>\"}";
		String contentValue = Base64.encodeAsString(contentString);
		MLPSiteContent request = new MLPSiteContent(SiteContentServiceImpl.KEY_ONBOARDING_OVERVIEW,
				contentValue.getBytes(), "application/json");
		JsonRequest<MLPSiteContent> reqObj = new JsonRequest<>();
		reqObj.setBody(request);

		stubFor(get(urlEqualTo("/ccds/site/content/" + SiteContentServiceImpl.KEY_ONBOARDING_OVERVIEW))
				.willReturn(aResponse().withStatus(HttpStatus.SC_OK)
						.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())
						.withBody("{\"created\": \"2019-02-01T21:58:49Z\"," + "\"modified\": \"2019-02-01T21:58:49Z\","
								+ "\"contentKey\": \"" + SiteContentServiceImpl.KEY_ONBOARDING_OVERVIEW + "\","
								+ "\"contentValue\": \"" + contentValue + "\"," + "\"mimeType\": \"application/json\"}")));

		stubFor(put(urlEqualTo("/ccds/site/content/" + SiteContentServiceImpl.KEY_ONBOARDING_OVERVIEW))
				.willReturn(aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type",
						MediaType.APPLICATION_JSON.toString())));

		HttpHeaders headers = new HttpHeaders();
		HttpEntity<JsonRequest<MLPSiteContent>> requestEntity = new HttpEntity<>(reqObj, headers);
		ResponseEntity<JsonResponse<Object>> contentResponse = restTemplate.exchange(
				"http://localhost:" + randomServerPort + "/site/content/global/onboarding/overview", HttpMethod.POST,
				requestEntity, new ParameterizedTypeReference<JsonResponse<Object>>() {
				});

		assertNotNull(contentResponse);
		assertEquals(HttpServletResponse.SC_OK, contentResponse.getStatusCode().value());
	}

	@Test
	public void getContactInfoTest() {
		String contentString = "{\"description\":\"<p>Test</p>\"}";
		String contentValue = Base64.encodeAsString(contentString);

		stubFor(get(urlEqualTo("/ccds/site/content/" + SiteContentServiceImpl.KEY_CONTACT_INFO)).willReturn(aResponse()
				.withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())
				.withBody("{\"created\": \"2019-02-01T21:58:49Z\"," + "\"modified\": \"2019-02-01T21:58:49Z\","
						+ "\"contentKey\": \"" + SiteContentServiceImpl.KEY_CONTACT_INFO + "\","
						+ "\"contentValue\": \"" + contentValue + "\"," + "\"mimeType\": \"application/json\"}")));

		ResponseEntity<JsonResponse<MLPSiteContent>> contentResponse = restTemplate.exchange(
				"http://localhost:" + randomServerPort + "/site/content/global/footer/contactinfo", HttpMethod.GET,
				null, new ParameterizedTypeReference<JsonResponse<MLPSiteContent>>() {
				});

		assertNotNull(contentResponse);
		assertEquals(HttpServletResponse.SC_OK, contentResponse.getStatusCode().value());
		MLPSiteContent content = contentResponse.getBody().getResponseBody();
		assertEquals(SiteContentServiceImpl.KEY_CONTACT_INFO, content.getContentKey());
		assertEquals(contentString, new String(content.getContentValue()));
		assertEquals("application/json", content.getMimeType());
	}

	@Test
	public void createContactInfoTest() {
		String contentString = "{\"description\":\"<p>Test</p>\"}";
		String contentValue = Base64.encodeAsString(contentString);
		MLPSiteContent request = new MLPSiteContent(SiteContentServiceImpl.KEY_CONTACT_INFO, contentValue.getBytes(),
				"application/json");
		JsonRequest<MLPSiteContent> reqObj = new JsonRequest<>();
		reqObj.setBody(request);

		stubFor(get(urlEqualTo("/ccds/site/content/" + SiteContentServiceImpl.KEY_CONTACT_INFO)).willReturn(aResponse()
				.withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())));

		stubFor(post(urlEqualTo("/ccds/site/content")).willReturn(aResponse().withStatus(HttpStatus.SC_OK)
				.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())));

		HttpHeaders headers = new HttpHeaders();
		HttpEntity<JsonRequest<MLPSiteContent>> requestEntity = new HttpEntity<>(reqObj, headers);
		ResponseEntity<JsonResponse<Object>> contentResponse = restTemplate.exchange(
				"http://localhost:" + randomServerPort + "/site/content/global/footer/contactinfo", HttpMethod.POST,
				requestEntity, new ParameterizedTypeReference<JsonResponse<Object>>() {
				});

		assertNotNull(contentResponse);
		assertEquals(HttpServletResponse.SC_OK, contentResponse.getStatusCode().value());
	}

	@Test
	public void updateContactInfoTest() {
		String contentString = "{\"description\":\"<p>Test</p>\"}";
		String contentValue = Base64.encodeAsString(contentString);
		MLPSiteContent request = new MLPSiteContent(SiteContentServiceImpl.KEY_CONTACT_INFO, contentValue.getBytes(),
				"application/json");
		JsonRequest<MLPSiteContent> reqObj = new JsonRequest<>();
		reqObj.setBody(request);

		stubFor(get(urlEqualTo("/ccds/site/content/" + SiteContentServiceImpl.KEY_CONTACT_INFO)).willReturn(aResponse()
				.withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())
				.withBody("{\"created\": \"2019-02-01T21:58:49Z\"," + "\"modified\": \"2019-02-01T21:58:49Z\","
						+ "\"contentKey\": \"" + SiteContentServiceImpl.KEY_CONTACT_INFO + "\","
						+ "\"contentValue\": \"" + contentValue + "\"," + "\"mimeType\": \"application/json\"}")));

		stubFor(put(urlEqualTo("/ccds/site/content/" + SiteContentServiceImpl.KEY_CONTACT_INFO)).willReturn(aResponse()
				.withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())));

		HttpHeaders headers = new HttpHeaders();
		HttpEntity<JsonRequest<MLPSiteContent>> requestEntity = new HttpEntity<>(reqObj, headers);
		ResponseEntity<JsonResponse<Object>> contentResponse = restTemplate.exchange(
				"http://localhost:" + randomServerPort + "/site/content/global/footer/contactinfo", HttpMethod.POST,
				requestEntity, new ParameterizedTypeReference<JsonResponse<Object>>() {
				});

		assertNotNull(contentResponse);
		assertEquals(HttpServletResponse.SC_OK, contentResponse.getStatusCode().value());
	}

	@Test
	public void getCobrandLogoTest() {
		String contentString = "Placeholder for an actual image";
		String contentValue = Base64.encodeAsString(contentString);

		stubFor(get(urlEqualTo("/ccds/site/content/" + SiteContentServiceImpl.KEY_COBRAND_LOGO)).willReturn(aResponse()
				.withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())
				.withBody("{\"created\": \"2019-02-01T21:58:49Z\"," + "\"modified\": \"2019-02-01T21:58:49Z\","
						+ "\"contentKey\": \"" + SiteContentServiceImpl.KEY_COBRAND_LOGO + "\","
						+ "\"contentValue\": \"" + contentValue + "\"," + "\"mimeType\": \"image/png\"}")));

		ResponseEntity<byte[]> contentResponse = restTemplate.exchange(
				"http://localhost:" + randomServerPort + "/site/content/global/coBrandLogo", HttpMethod.GET, null,
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

		stubFor(get(urlEqualTo("/ccds/site/content/" + SiteContentServiceImpl.KEY_COBRAND_LOGO)).willReturn(aResponse()
				.withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())));

		stubFor(post(urlEqualTo("/ccds/site/content")).willReturn(aResponse().withStatus(HttpStatus.SC_OK)
				.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())));

		HttpHeaders headers = new HttpHeaders();
		HttpEntity<JsonRequest<MLPSiteContent>> requestEntity = new HttpEntity<>(reqObj, headers);
		ResponseEntity<JsonResponse<Object>> contentResponse = restTemplate.exchange(
				"http://localhost:" + randomServerPort + "/site/content/global/coBrandLogo", HttpMethod.POST,
				requestEntity, new ParameterizedTypeReference<JsonResponse<Object>>() {
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

		stubFor(get(urlEqualTo("/ccds/site/content/" + SiteContentServiceImpl.KEY_COBRAND_LOGO)).willReturn(aResponse()
				.withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())
				.withBody("{\"created\": \"2019-02-01T21:58:49Z\"," + "\"modified\": \"2019-02-01T21:58:49Z\","
						+ "\"contentKey\": \"" + SiteContentServiceImpl.KEY_COBRAND_LOGO + "\","
						+ "\"contentValue\": \"" + contentValue + "\"," + "\"mimeType\": \"image/png\"}")));

		stubFor(put(urlEqualTo("/ccds/site/content/" + SiteContentServiceImpl.KEY_COBRAND_LOGO)).willReturn(aResponse()
				.withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())));

		HttpHeaders headers = new HttpHeaders();
		HttpEntity<JsonRequest<MLPSiteContent>> requestEntity = new HttpEntity<>(reqObj, headers);
		ResponseEntity<JsonResponse<Object>> contentResponse = restTemplate.exchange(
				"http://localhost:" + randomServerPort + "/site/content/global/coBrandLogo", HttpMethod.POST,
				requestEntity, new ParameterizedTypeReference<JsonResponse<Object>>() {
				});

		assertNotNull(contentResponse);
		assertEquals(HttpServletResponse.SC_OK, contentResponse.getStatusCode().value());
	}

	@Test
	public void deleteCobrandLogoTest() {
		stubFor(delete(urlEqualTo("/ccds/site/content/" + SiteContentServiceImpl.KEY_COBRAND_LOGO))
				.willReturn(aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type",
						MediaType.APPLICATION_JSON.toString())));

		ResponseEntity<JsonResponse<Object>> contentResponse = restTemplate.exchange(
				"http://localhost:" + randomServerPort + "/site/content/global/coBrandLogo", HttpMethod.DELETE, null,
				new ParameterizedTypeReference<JsonResponse<Object>>() {
				});

		assertNotNull(contentResponse);
		assertEquals(HttpServletResponse.SC_OK, contentResponse.getStatusCode().value());
	}

	@Test
	public void deleteCobrandLogoFailTest() {
		stubFor(delete(urlEqualTo("/ccds/site/content/" + SiteContentServiceImpl.KEY_COBRAND_LOGO))
				.willReturn(aResponse().withStatus(HttpStatus.SC_BAD_REQUEST).withHeader("Content-Type",
						MediaType.APPLICATION_JSON.toString())));

		ResponseEntity<JsonResponse<Object>> contentResponse = restTemplate.exchange(
				"http://localhost:" + randomServerPort + "/site/content/global/coBrandLogo", HttpMethod.DELETE, null,
				new ParameterizedTypeReference<JsonResponse<Object>>() {
				});

		assertNotNull(contentResponse);
		assertEquals("Exception Occurred Deleting Cobrand Logo", contentResponse.getBody().getResponseDetail());
	}

	@Test
	public void getCarouselPictureTest() {
		String contentString = "Placeholder for an actual image";
		String contentValue = Base64.encodeAsString(contentString);

		stubFor(get(urlEqualTo("/ccds/site/content/top.test.bgImg")).willReturn(aResponse().withStatus(HttpStatus.SC_OK)
				.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())
				.withBody("{\"created\": \"2019-02-01T21:58:49Z\"," + "\"modified\": \"2019-02-01T21:58:49Z\","
						+ "\"contentKey\": \"top.test.bgImg\"," + "\"contentValue\": \"" + contentValue + "\","
						+ "\"mimeType\": \"image/png\"}")));

		ResponseEntity<byte[]> contentResponse = restTemplate.exchange(
				"http://localhost:" + randomServerPort + "/site/content/carouselImages/top.test.bgImg", HttpMethod.GET,
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
		MLPSiteContent request = new MLPSiteContent("top.test.bgImg", contentValue.getBytes(), "image/png");
		JsonRequest<MLPSiteContent> reqObj = new JsonRequest<>();
		reqObj.setBody(request);

		stubFor(get(urlEqualTo("/ccds/site/content/top.test.bgImg")).willReturn(aResponse().withStatus(HttpStatus.SC_OK)
				.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())));

		stubFor(post(urlEqualTo("/ccds/site/content")).willReturn(aResponse().withStatus(HttpStatus.SC_OK)
				.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())));

		HttpHeaders headers = new HttpHeaders();
		HttpEntity<JsonRequest<MLPSiteContent>> requestEntity = new HttpEntity<>(reqObj, headers);
		ResponseEntity<JsonResponse<Object>> contentResponse = restTemplate.exchange(
				"http://localhost:" + randomServerPort + "/site/content/carouselImages", HttpMethod.POST, requestEntity,
				new ParameterizedTypeReference<JsonResponse<Object>>() {
				});

		assertNotNull(contentResponse);
		assertEquals(HttpServletResponse.SC_OK, contentResponse.getStatusCode().value());
	}

	@Test
	public void updateCarouselPictureTest() {
		String contentString = "Placeholder for an actual image";
		String contentValue = Base64.encodeAsString(contentString);
		MLPSiteContent request = new MLPSiteContent("top.test.bgImg", contentValue.getBytes(), "image/png");
		JsonRequest<MLPSiteContent> reqObj = new JsonRequest<>();
		reqObj.setBody(request);

		stubFor(get(urlEqualTo("/ccds/site/content/top.test.bgImg")).willReturn(aResponse().withStatus(HttpStatus.SC_OK)
				.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())
				.withBody("{\"created\": \"2019-02-01T21:58:49Z\"," + "\"modified\": \"2019-02-01T21:58:49Z\","
						+ "\"contentKey\": \"top.test.bgImg\"," + "\"contentValue\": \"" + contentValue + "\","
						+ "\"mimeType\": \"image/png\"}")));

		stubFor(put(urlEqualTo("/ccds/site/content/top.test.bgImg")).willReturn(aResponse().withStatus(HttpStatus.SC_OK)
				.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())));

		HttpHeaders headers = new HttpHeaders();
		HttpEntity<JsonRequest<MLPSiteContent>> requestEntity = new HttpEntity<>(reqObj, headers);
		ResponseEntity<JsonResponse<Object>> contentResponse = restTemplate.exchange(
				"http://localhost:" + randomServerPort + "/site/content/carouselImages", HttpMethod.POST, requestEntity,
				new ParameterizedTypeReference<JsonResponse<Object>>() {
				});

		assertNotNull(contentResponse);
		assertEquals(HttpServletResponse.SC_OK, contentResponse.getStatusCode().value());
	}

	@Test
	public void deleteCarouselPictureTest() {
		stubFor(delete(urlEqualTo("/ccds/site/content/top.test.bgImg")).willReturn(aResponse()
				.withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())));

		ResponseEntity<JsonResponse<Object>> contentResponse = restTemplate.exchange(
				"http://localhost:" + randomServerPort + "/site/content/carouselImages/top.test.bgImg",
				HttpMethod.DELETE, null, new ParameterizedTypeReference<JsonResponse<Object>>() {
				});

		assertNotNull(contentResponse);
		assertEquals(HttpServletResponse.SC_OK, contentResponse.getStatusCode().value());
	}

	@Test
	public void deleteCarouselPictureFailTest() {
		stubFor(delete(urlEqualTo("/ccds/site/content/top.test.bgImg"))
				.willReturn(aResponse().withStatus(HttpStatus.SC_BAD_REQUEST).withHeader("Content-Type",
						MediaType.APPLICATION_JSON.toString())));

		ResponseEntity<JsonResponse<Object>> contentResponse = restTemplate.exchange(
				"http://localhost:" + randomServerPort + "/site/content/carouselImages/top.test.bgImg",
				HttpMethod.DELETE, null, new ParameterizedTypeReference<JsonResponse<Object>>() {
				});

		assertNotNull(contentResponse);
		assertEquals("Exception Occurred Updating Carousel Picture", contentResponse.getBody().getResponseDetail());
	}
}
