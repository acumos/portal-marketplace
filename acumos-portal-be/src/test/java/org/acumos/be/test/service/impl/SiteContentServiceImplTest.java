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
	private SiteContentServiceImpl siteContentService;

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
								+ "\"mimeType\": \"text/javascript\"}")));

		MLPSiteContent content = siteContentService.getTermsConditions();
		assertEquals(contentString, new String(content.getContentValue()));
	}

	@Test
	public void createTermsConditionsTest() {
		String contentString = "{\"description\":\"<p>Test</p>\"}";
		String contentValue = Base64.encodeAsString(contentString);
		MLPSiteContent request = new MLPSiteContent(SiteContentServiceImpl.KEY_TERMS_CONDITIONS,
				contentValue.getBytes(), "text/javascript");

		stubFor(get(urlEqualTo("/ccds/site/content/" + SiteContentServiceImpl.KEY_TERMS_CONDITIONS))
				.willReturn(aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type",
						MediaType.APPLICATION_JSON.toString())));

		stubFor(post(urlEqualTo("/ccds/site/content")).willReturn(aResponse().withStatus(HttpStatus.SC_OK)
				.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())));

		siteContentService.setTermsConditions(request);
	}

	@Test
	public void updateTermsConditionsTest() {
		String contentString = "{\"description\":\"<p>Test</p>\"}";
		String contentValue = Base64.encodeAsString(contentString);
		MLPSiteContent request = new MLPSiteContent(SiteContentServiceImpl.KEY_TERMS_CONDITIONS,
				contentValue.getBytes(), "text/javascript");

		stubFor(get(urlEqualTo("/ccds/site/content/" + SiteContentServiceImpl.KEY_TERMS_CONDITIONS))
				.willReturn(aResponse().withStatus(HttpStatus.SC_OK)
						.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())
						.withBody("{\"created\": \"2019-02-01T21:58:49Z\"," + "\"modified\": \"2019-02-01T21:58:49Z\","
								+ "\"contentKey\": \"" + SiteContentServiceImpl.KEY_TERMS_CONDITIONS + "\","
								+ "\"contentValue\": \"" + contentValue + "\","
								+ "\"mimeType\": \"text/javascript\"}")));

		stubFor(put(urlEqualTo("/ccds/site/content/" + SiteContentServiceImpl.KEY_TERMS_CONDITIONS))
				.willReturn(aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type",
						MediaType.APPLICATION_JSON.toString())));

		siteContentService.setTermsConditions(request);
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
								+ "\"mimeType\": \"text/javascript\"}")));

		MLPSiteContent content = siteContentService.getOnboardingOverview();
		assertEquals(contentString, new String(content.getContentValue()));
	}

	@Test
	public void createOnboardingOverviewTest() {
		String contentString = "{\"description\":\"<p>Test</p>\"}";
		String contentValue = Base64.encodeAsString(contentString);
		MLPSiteContent request = new MLPSiteContent(SiteContentServiceImpl.KEY_ONBOARDING_OVERVIEW,
				contentValue.getBytes(), "text/javascript");

		stubFor(get(urlEqualTo("/ccds/site/content/" + SiteContentServiceImpl.KEY_ONBOARDING_OVERVIEW))
				.willReturn(aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type",
						MediaType.APPLICATION_JSON.toString())));

		stubFor(post(urlEqualTo("/ccds/site/content")).willReturn(aResponse().withStatus(HttpStatus.SC_OK)
				.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())));

		siteContentService.setOnboardingOverview(request);
	}

	@Test
	public void updateOnboardingOverviewTest() {
		String contentString = "{\"description\":\"<p>Test</p>\"}";
		String contentValue = Base64.encodeAsString(contentString);
		MLPSiteContent request = new MLPSiteContent(SiteContentServiceImpl.KEY_ONBOARDING_OVERVIEW,
				contentValue.getBytes(), "text/javascript");

		stubFor(get(urlEqualTo("/ccds/site/content/" + SiteContentServiceImpl.KEY_ONBOARDING_OVERVIEW))
				.willReturn(aResponse().withStatus(HttpStatus.SC_OK)
						.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())
						.withBody("{\"created\": \"2019-02-01T21:58:49Z\"," + "\"modified\": \"2019-02-01T21:58:49Z\","
								+ "\"contentKey\": \"" + SiteContentServiceImpl.KEY_ONBOARDING_OVERVIEW + "\","
								+ "\"contentValue\": \"" + contentValue + "\","
								+ "\"mimeType\": \"text/javascript\"}")));

		stubFor(put(urlEqualTo("/ccds/site/content/" + SiteContentServiceImpl.KEY_ONBOARDING_OVERVIEW))
				.willReturn(aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type",
						MediaType.APPLICATION_JSON.toString())));

		siteContentService.setOnboardingOverview(request);
	}

	@Test
	public void getContactInfoTest() {
		String contentString = "{\"description\":\"<p>Test</p>\"}";
		String contentValue = Base64.encodeAsString(contentString);

		stubFor(get(urlEqualTo("/ccds/site/content/" + SiteContentServiceImpl.KEY_CONTACT_INFO)).willReturn(aResponse()
				.withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())
				.withBody("{\"created\": \"2019-02-01T21:58:49Z\"," + "\"modified\": \"2019-02-01T21:58:49Z\","
						+ "\"contentKey\": \"" + SiteContentServiceImpl.KEY_CONTACT_INFO + "\","
						+ "\"contentValue\": \"" + contentValue + "\"," + "\"mimeType\": \"text/javascript\"}")));

		MLPSiteContent content = siteContentService.getContactInfo();
		assertEquals(contentString, new String(content.getContentValue()));
	}

	@Test
	public void createContactInfoTest() {
		String contentString = "{\"description\":\"<p>Test</p>\"}";
		String contentValue = Base64.encodeAsString(contentString);
		MLPSiteContent request = new MLPSiteContent(SiteContentServiceImpl.KEY_CONTACT_INFO, contentValue.getBytes(),
				"text/javascript");

		stubFor(get(urlEqualTo("/ccds/site/content/" + SiteContentServiceImpl.KEY_CONTACT_INFO)).willReturn(aResponse()
				.withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())));

		stubFor(post(urlEqualTo("/ccds/site/content")).willReturn(aResponse().withStatus(HttpStatus.SC_OK)
				.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())));

		siteContentService.setContactInfo(request);
	}

	@Test
	public void updateContactInfoTest() {
		String contentString = "{\"description\":\"<p>Test</p>\"}";
		String contentValue = Base64.encodeAsString(contentString);
		MLPSiteContent request = new MLPSiteContent(SiteContentServiceImpl.KEY_CONTACT_INFO, contentValue.getBytes(),
				"text/javascript");

		stubFor(get(urlEqualTo("/ccds/site/content/" + SiteContentServiceImpl.KEY_CONTACT_INFO)).willReturn(aResponse()
				.withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())
				.withBody("{\"created\": \"2019-02-01T21:58:49Z\"," + "\"modified\": \"2019-02-01T21:58:49Z\","
						+ "\"contentKey\": \"" + SiteContentServiceImpl.KEY_CONTACT_INFO + "\","
						+ "\"contentValue\": \"" + contentValue + "\"," + "\"mimeType\": \"text/javascript\"}")));

		stubFor(put(urlEqualTo("/ccds/site/content/" + SiteContentServiceImpl.KEY_CONTACT_INFO)).willReturn(aResponse()
				.withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())));

		siteContentService.setContactInfo(request);
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

		MLPSiteContent content = siteContentService.getCobrandLogo();
		assertEquals(contentString, new String(content.getContentValue()));
	}

	@Test
	public void createCobrandLogoTest() {
		String contentString = "Placeholder for an actual image";
		String contentValue = Base64.encodeAsString(contentString);
		MLPSiteContent request = new MLPSiteContent(SiteContentServiceImpl.KEY_COBRAND_LOGO, contentValue.getBytes(),
				"image/png");

		stubFor(get(urlEqualTo("/ccds/site/content/" + SiteContentServiceImpl.KEY_COBRAND_LOGO)).willReturn(aResponse()
				.withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())));

		stubFor(post(urlEqualTo("/ccds/site/content")).willReturn(aResponse().withStatus(HttpStatus.SC_OK)
				.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())));

		siteContentService.setCobrandLogo(request);
	}

	@Test
	public void updateCobrandLogoTest() {
		String contentString = "Placeholder for an actual image";
		String contentValue = Base64.encodeAsString(contentString);
		MLPSiteContent request = new MLPSiteContent(SiteContentServiceImpl.KEY_COBRAND_LOGO, contentValue.getBytes(),
				"image/png");

		stubFor(get(urlEqualTo("/ccds/site/content/" + SiteContentServiceImpl.KEY_COBRAND_LOGO)).willReturn(aResponse()
				.withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())
				.withBody("{\"created\": \"2019-02-01T21:58:49Z\"," + "\"modified\": \"2019-02-01T21:58:49Z\","
						+ "\"contentKey\": \"" + SiteContentServiceImpl.KEY_COBRAND_LOGO + "\","
						+ "\"contentValue\": \"" + contentValue + "\"," + "\"mimeType\": \"image/png\"}")));

		stubFor(put(urlEqualTo("/ccds/site/content/" + SiteContentServiceImpl.KEY_COBRAND_LOGO)).willReturn(aResponse()
				.withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())));

		siteContentService.setCobrandLogo(request);
	}

	@Test
	public void deleteCobrandLogoTest() {
		stubFor(delete(urlEqualTo("/ccds/site/content/" + SiteContentServiceImpl.KEY_COBRAND_LOGO))
				.willReturn(aResponse().withStatus(HttpStatus.SC_OK).withHeader("Content-Type",
						MediaType.APPLICATION_JSON.toString())));

		siteContentService.deleteCobrandLogo();
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

		MLPSiteContent content = siteContentService.getCarouselPicture("top.test.bgImg");
		assertEquals(contentString, new String(content.getContentValue()));
	}

	@Test
	public void createCarouselPictureTest() {
		String contentString = "Placeholder for an actual image";
		String contentValue = Base64.encodeAsString(contentString);
		MLPSiteContent request = new MLPSiteContent("top.test.bgImg", contentValue.getBytes(), "image/png");

		stubFor(get(urlEqualTo("/ccds/site/content/top.test.bgImg")).willReturn(aResponse().withStatus(HttpStatus.SC_OK)
				.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())));

		stubFor(post(urlEqualTo("/ccds/site/content")).willReturn(aResponse().withStatus(HttpStatus.SC_OK)
				.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())));

		siteContentService.setCarouselPicture(request);
	}

	@Test
	public void updateCarouselPictureTest() {
		String contentString = "Placeholder for an actual image";
		String contentValue = Base64.encodeAsString(contentString);
		MLPSiteContent request = new MLPSiteContent("top.test.bgImg", contentValue.getBytes(), "image/png");

		stubFor(get(urlEqualTo("/ccds/site/content/top.test.bgImg")).willReturn(aResponse().withStatus(HttpStatus.SC_OK)
				.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())
				.withBody("{\"created\": \"2019-02-01T21:58:49Z\"," + "\"modified\": \"2019-02-01T21:58:49Z\","
						+ "\"contentKey\": \"top.test.bgImg\"," + "\"contentValue\": \"" + contentValue + "\","
						+ "\"mimeType\": \"image/png\"}")));

		stubFor(put(urlEqualTo("/ccds/site/content/top.test.bgImg")).willReturn(aResponse().withStatus(HttpStatus.SC_OK)
				.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())));

		siteContentService.setCarouselPicture(request);
	}

	@Test
	public void deleteCarouselPictureTest() {
		stubFor(delete(urlEqualTo("/ccds/site/content/top.test.bgImg")).willReturn(aResponse()
				.withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())));

		siteContentService.deleteCarouselPicture("top.test.bgImg");
	}
}
