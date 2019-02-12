package org.acumos.be.test.service.impl;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acumos.cds.domain.MLPSiteContent;
import org.acumos.portal.be.common.ConfigConstants;
import org.acumos.portal.be.service.impl.SiteContentServiceImpl;
import org.acumos.portal.be.util.EELFLoggerDelegate;
import org.apache.http.HttpStatus;
import org.glassfish.jersey.internal.util.Base64;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;

import com.github.tomakehurst.wiremock.junit.WireMockRule;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = org.acumos.portal.be.Application.class, webEnvironment = WebEnvironment.RANDOM_PORT, properties = {
		ConfigConstants.cdms_client_url + "=http://localhost:8000/ccds",
		ConfigConstants.cdms_client_username + "=ccds_test", ConfigConstants.cdms_client_password + "=ccds_test" })
public class SiteContentServiceImplTest {

	private static final EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(SiteContentServiceImplTest.class);
	
	@Rule
	public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().port(8000));

	@Autowired
	SiteContentServiceImpl siteContentService;

	final HttpServletResponse response = new MockHttpServletResponse();
	final HttpServletRequest request = new MockHttpServletRequest();
	
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
		
		MLPSiteContent content = siteContentService.getTermsCondition();
		assertEquals(contentString, new String(content.getContentValue()));
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
		
		MLPSiteContent content = siteContentService.getContactInfo();
		assertEquals(contentString, new String(content.getContentValue()));
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
		
		MLPSiteContent content = siteContentService.getCobrandLogo();
		assertEquals(contentString, new String(content.getContentValue()));
	}

	@Test
	public void createCobrandLogoTest() {
		String contentString = "Placeholder for an actual image";
		String contentValue = Base64.encodeAsString(contentString);
		MLPSiteContent request = new MLPSiteContent(keyCobrandLogo,
				contentValue.getBytes(), mimePNG);
		
		stubFor(get(urlEqualTo("/ccds/site/content/" + keyCobrandLogo))
			.willReturn(aResponse().withStatus(HttpStatus.SC_OK)
					.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())));
		
		stubFor(post(urlEqualTo("/ccds/site/content"))
			.willReturn(aResponse().withStatus(HttpStatus.SC_OK)
					.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())));
		
		siteContentService.setCobrandLogo(request);
	}

	@Test
	public void updateCobrandLogoTest() {
		String contentString = "Placeholder for an actual image";
		String contentValue = Base64.encodeAsString(contentString);
		MLPSiteContent request = new MLPSiteContent(keyCobrandLogo,
				contentValue.getBytes(), mimePNG);

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
		
		siteContentService.setCobrandLogo(request);
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
		
		MLPSiteContent content = siteContentService.getCarouselPicture(keyCarouselTestPicture);
		assertEquals(contentString, new String(content.getContentValue()));
	}

	@Test
	public void createCarouselPictureTest() {
		String contentString = "Placeholder for an actual image";
		String contentValue = Base64.encodeAsString(contentString);
		MLPSiteContent request = new MLPSiteContent(keyCarouselTestPicture,
				contentValue.getBytes(), mimePNG);
		
		stubFor(get(urlEqualTo("/ccds/site/content/" + keyCarouselTestPicture))
			.willReturn(aResponse().withStatus(HttpStatus.SC_OK)
					.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())));
		
		stubFor(post(urlEqualTo("/ccds/site/content"))
			.willReturn(aResponse().withStatus(HttpStatus.SC_OK)
					.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())));

		siteContentService.setCarouselPicture(request);
	}

	@Test
	public void updateCarouselPictureTest() {
		String contentString = "Placeholder for an actual image";
		String contentValue = Base64.encodeAsString(contentString);
		MLPSiteContent request = new MLPSiteContent(keyCarouselTestPicture,
				contentValue.getBytes(), mimePNG);
		
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
		
		siteContentService.setCarouselPicture(request);
	}

	@Test
	public void deleteCarouselPictureTest() {
		stubFor(delete(urlEqualTo("/ccds/site/content/" + keyCarouselTestPicture))
			.willReturn(aResponse().withStatus(HttpStatus.SC_OK)
					.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())));
		
		siteContentService.deleteCarouselPicture(keyCarouselTestPicture);
	}
}
