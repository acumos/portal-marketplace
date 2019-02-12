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

	private static final String host = "http://localhost";
	
	private static final String keyTermsCondition = "global.termsCondition";
	private static final String keyCobrandLogo = "global.coBrandLogo";
	private static final String keyContactInfo = "global.footer.contactInfo";
	private static final String keyCarouselTestPicture = "top.test.bgImg";
	
	private static final String mimeJS = "text/javascript";
	private static final String mimePNG = "image/png";
	
	@Test
	public void getTermsConditionTest() {
		String contentString = "{\"description\":\"<p>Test</p>\"}";
		String contentValue = Base64.encodeAsString(contentString);
		
		stubFor(get(urlEqualTo("/ccds/site/content/" + keyTermsCondition))
			.willReturn(aResponse().withStatus(HttpStatus.SC_OK)
					.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())
					.withBody("{" + "\"created\": \"2019-02-01T21:58:49Z\","
						    + "\"modified\": \"2019-02-01T21:58:49Z\","
						    + "\"contentKey\": \"" + keyTermsCondition + "\","
							+ "\"contentValue\": \"" + contentValue + "\","
							+ "\"mimeType\": \"" + mimeJS + "\"}")));
		
		ResponseEntity<JsonResponse<MLPSiteContent>> contentResponse = restTemplate.exchange(
			host + ":" + randomServerPort + "/api-manual/Solution/global/termsCondition",
				HttpMethod.GET, null, new ParameterizedTypeReference<JsonResponse<MLPSiteContent>>() {});
		
		assertNotNull(contentResponse);
		assertEquals(HttpServletResponse.SC_OK, contentResponse.getStatusCode().value());
		MLPSiteContent content = contentResponse.getBody().getResponseBody();
		assertEquals(keyTermsCondition, content.getContentKey());
		assertEquals(contentString, new String(content.getContentValue()));
		assertEquals(mimeJS, content.getMimeType());
	}

	@Test
	public void getContactInfoTest() {
		String contentString = "{\"description\":\"<p>Test</p>\"}";
		String contentValue = Base64.encodeAsString(contentString);
		
		stubFor(get(urlEqualTo("/ccds/site/content/" + keyContactInfo))
			.willReturn(aResponse().withStatus(HttpStatus.SC_OK)
					.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())
					.withBody("{" + "\"created\": \"2019-02-01T21:58:49Z\","
						    + "\"modified\": \"2019-02-01T21:58:49Z\","
						    + "\"contentKey\": \"" + keyContactInfo + "\","
							+ "\"contentValue\": \"" + contentValue + "\","
							+ "\"mimeType\": \"" + mimeJS + "\"}")));
		
		ResponseEntity<JsonResponse<MLPSiteContent>> contentResponse = restTemplate.exchange(
			host + ":" + randomServerPort + "/api-manual/Solution/global/footer/contactinfo",
				HttpMethod.GET, null, new ParameterizedTypeReference<JsonResponse<MLPSiteContent>>() {});
		
		assertNotNull(contentResponse);
		assertEquals(HttpServletResponse.SC_OK, contentResponse.getStatusCode().value());
		MLPSiteContent content = contentResponse.getBody().getResponseBody();
		assertEquals(keyContactInfo, content.getContentKey());
		assertEquals(contentString, new String(content.getContentValue()));
		assertEquals(mimeJS, content.getMimeType());
	}

	@Test
	public void getCobrandLogoTest() {
		String contentString = "Placeholder for an actual image";
		String contentValue = Base64.encodeAsString(contentString);
		
		stubFor(get(urlEqualTo("/ccds/site/content/" + keyCobrandLogo))
			.willReturn(aResponse().withStatus(HttpStatus.SC_OK)
					.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())
					.withBody("{" + "\"created\": \"2019-02-01T21:58:49Z\","
						    + "\"modified\": \"2019-02-01T21:58:49Z\","
						    + "\"contentKey\": \"" + keyCobrandLogo + "\","
							+ "\"contentValue\": \"" + contentValue + "\","
							+ "\"mimeType\": \"" + mimePNG + "\"}")));
		
		ResponseEntity<JsonResponse<MLPSiteContent>> contentResponse = restTemplate.exchange(
			host + ":" + randomServerPort + "/api-manual/Solution/global/coBrandLogo",
				HttpMethod.GET, null, new ParameterizedTypeReference<JsonResponse<MLPSiteContent>>() {});
		
		assertNotNull(contentResponse);
		assertEquals(HttpServletResponse.SC_OK, contentResponse.getStatusCode().value());
		MLPSiteContent content = contentResponse.getBody().getResponseBody();
		assertEquals(keyCobrandLogo, content.getContentKey());
		assertEquals(contentString, new String(content.getContentValue()));
		assertEquals(mimePNG, content.getMimeType());
	}

	@Test
	public void createCobrandLogoTest() {
		String contentString = "Placeholder for an actual image";
		String contentValue = Base64.encodeAsString(contentString);
		MLPSiteContent request = new MLPSiteContent(keyCobrandLogo,
				contentValue.getBytes(), mimePNG);
		JsonRequest<MLPSiteContent> reqObj = new JsonRequest<>();
		reqObj.setBody(request);
		
		stubFor(get(urlEqualTo("/ccds/site/content/" + keyCobrandLogo))
			.willReturn(aResponse().withStatus(HttpStatus.SC_OK)
					.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())));
		
		stubFor(post(urlEqualTo("/ccds/site/content"))
			.willReturn(aResponse().withStatus(HttpStatus.SC_OK)
					.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())));
		
		HttpHeaders headers = new HttpHeaders();
		HttpEntity<JsonRequest<MLPSiteContent>> requestEntity = new HttpEntity<>(reqObj, headers);
		ResponseEntity<JsonResponse<Object>> contentResponse = restTemplate.exchange(
			host + ":" + randomServerPort + "/api-manual/Solution/global/coBrandLogo",
				HttpMethod.POST, requestEntity, new ParameterizedTypeReference<JsonResponse<Object>>() {});
		
		assertNotNull(contentResponse);
		assertEquals(HttpServletResponse.SC_OK, contentResponse.getStatusCode().value());
	}


	@Test
	public void updateCobrandLogoTest() {
		String contentString = "Placeholder for an actual image";
		String contentValue = Base64.encodeAsString(contentString);
		MLPSiteContent request = new MLPSiteContent(keyCobrandLogo,
				contentValue.getBytes(), mimePNG);
		JsonRequest<MLPSiteContent> reqObj = new JsonRequest<>();
		reqObj.setBody(request);
		
		stubFor(get(urlEqualTo("/ccds/site/content/" + keyCobrandLogo))
			.willReturn(aResponse().withStatus(HttpStatus.SC_OK)
					.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())
					.withBody("{" + "\"created\": \"2019-02-01T21:58:49Z\","
						    + "\"modified\": \"2019-02-01T21:58:49Z\","
						    + "\"contentKey\": \"" + keyCobrandLogo + "\","
							+ "\"contentValue\": \"" + contentValue + "\","
							+ "\"mimeType\": \"" + mimePNG + "\"}")));
		
		stubFor(put(urlEqualTo("/ccds/site/content/" + keyCobrandLogo))
			.willReturn(aResponse().withStatus(HttpStatus.SC_OK)
					.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())));
		
		HttpHeaders headers = new HttpHeaders();
		HttpEntity<JsonRequest<MLPSiteContent>> requestEntity = new HttpEntity<>(reqObj, headers);
		ResponseEntity<JsonResponse<Object>> contentResponse = restTemplate.exchange(
			host + ":" + randomServerPort + "/api-manual/Solution/global/coBrandLogo",
				HttpMethod.POST, requestEntity, new ParameterizedTypeReference<JsonResponse<Object>>() {});
		
		assertNotNull(contentResponse);
		assertEquals(HttpServletResponse.SC_OK, contentResponse.getStatusCode().value());
	}

	@Test
	public void getCarouselPictureTest() {
		String contentString = "Placeholder for an actual image";
		String contentValue = Base64.encodeAsString(contentString);
		
		stubFor(get(urlEqualTo("/ccds/site/content/" + keyCarouselTestPicture))
			.willReturn(aResponse().withStatus(HttpStatus.SC_OK)
					.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())
					.withBody("{" + "\"created\": \"2019-02-01T21:58:49Z\","
						    + "\"modified\": \"2019-02-01T21:58:49Z\","
						    + "\"contentKey\": \"" + keyCarouselTestPicture + "\","
							+ "\"contentValue\": \"" + contentValue + "\","
							+ "\"mimeType\": \"" + mimePNG + "\"}")));
		
		ResponseEntity<JsonResponse<MLPSiteContent>> contentResponse = restTemplate.exchange(
			host + ":" + randomServerPort + "/api-manual/Solution/carouselImages/" + keyCarouselTestPicture,
				HttpMethod.GET, null, new ParameterizedTypeReference<JsonResponse<MLPSiteContent>>() {});
		
		assertNotNull(contentResponse);
		assertEquals(HttpServletResponse.SC_OK, contentResponse.getStatusCode().value());
		MLPSiteContent content = contentResponse.getBody().getResponseBody();
		assertEquals(keyCarouselTestPicture, content.getContentKey());
		assertEquals(contentString, new String(content.getContentValue()));
		assertEquals(mimePNG, content.getMimeType());
	}

	@Test
	public void createCarouselPictureTest() {
		String contentString = "Placeholder for an actual image";
		String contentValue = Base64.encodeAsString(contentString);
		MLPSiteContent request = new MLPSiteContent(keyCarouselTestPicture,
				contentValue.getBytes(), mimePNG);
		JsonRequest<MLPSiteContent> reqObj = new JsonRequest<>();
		reqObj.setBody(request);
		
		stubFor(get(urlEqualTo("/ccds/site/content/" + keyCarouselTestPicture))
			.willReturn(aResponse().withStatus(HttpStatus.SC_OK)
					.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())));
		
		stubFor(post(urlEqualTo("/ccds/site/content"))
			.willReturn(aResponse().withStatus(HttpStatus.SC_OK)
					.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())));
		
		HttpHeaders headers = new HttpHeaders();
		HttpEntity<JsonRequest<MLPSiteContent>> requestEntity = new HttpEntity<>(reqObj, headers);
		ResponseEntity<JsonResponse<Object>> contentResponse = restTemplate.exchange(
			host + ":" + randomServerPort + "/api-manual/Solution/carouselImages",
				HttpMethod.POST, requestEntity, new ParameterizedTypeReference<JsonResponse<Object>>() {});
		
		assertNotNull(contentResponse);
		assertEquals(HttpServletResponse.SC_OK, contentResponse.getStatusCode().value());
	}


	@Test
	public void updateCarouselPictureTest() {
		String contentString = "Placeholder for an actual image";
		String contentValue = Base64.encodeAsString(contentString);
		MLPSiteContent request = new MLPSiteContent(keyCarouselTestPicture,
				contentValue.getBytes(), mimePNG);
		JsonRequest<MLPSiteContent> reqObj = new JsonRequest<>();
		reqObj.setBody(request);
		
		stubFor(get(urlEqualTo("/ccds/site/content/" + keyCarouselTestPicture))
			.willReturn(aResponse().withStatus(HttpStatus.SC_OK)
					.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())
					.withBody("{" + "\"created\": \"2019-02-01T21:58:49Z\","
						    + "\"modified\": \"2019-02-01T21:58:49Z\","
						    + "\"contentKey\": \"" + keyCarouselTestPicture + "\","
							+ "\"contentValue\": \"" + contentValue + "\","
							+ "\"mimeType\": \"" + mimePNG + "\"}")));
		
		stubFor(put(urlEqualTo("/ccds/site/content/" + keyCarouselTestPicture))
			.willReturn(aResponse().withStatus(HttpStatus.SC_OK)
					.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())));
		
		HttpHeaders headers = new HttpHeaders();
		HttpEntity<JsonRequest<MLPSiteContent>> requestEntity = new HttpEntity<>(reqObj, headers);
		ResponseEntity<JsonResponse<Object>> contentResponse = restTemplate.exchange(
			host + ":" + randomServerPort + "/api-manual/Solution/carouselImages",
				HttpMethod.POST, requestEntity, new ParameterizedTypeReference<JsonResponse<Object>>() {});
		
		assertNotNull(contentResponse);
		assertEquals(HttpServletResponse.SC_OK, contentResponse.getStatusCode().value());
	}
	
	@Test
	public void deleteCarouselPictureTest() {
		stubFor(delete(urlEqualTo("/ccds/site/content/" + keyCarouselTestPicture))
				.willReturn(aResponse().withStatus(HttpStatus.SC_OK)
						.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())));
		
		ResponseEntity<JsonResponse<Object>> contentResponse = restTemplate.exchange(
				host + ":" + randomServerPort + "/api-manual/Solution/carouselImages/" + keyCarouselTestPicture,
					HttpMethod.DELETE, null, new ParameterizedTypeReference<JsonResponse<Object>>() {});
		
		assertNotNull(contentResponse);
		assertEquals(HttpServletResponse.SC_OK, contentResponse.getStatusCode().value());	
	}
	
	@Test
	public void deleteCarouselPictureFailTest() {
		stubFor(delete(urlEqualTo("/ccds/site/content/" + keyCarouselTestPicture))
				.willReturn(aResponse().withStatus(HttpStatus.SC_BAD_REQUEST)
						.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())));
		
		ResponseEntity<JsonResponse<Object>> contentResponse = restTemplate.exchange(
				host + ":" + randomServerPort + "/api-manual/Solution/carouselImages/" + keyCarouselTestPicture,
					HttpMethod.DELETE, null, new ParameterizedTypeReference<JsonResponse<Object>>() {});
		
		assertNotNull(contentResponse);
		assertEquals("Exception Occurred Updating Carousel Picture", contentResponse.getBody().getResponseDetail());	
	}
}
