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
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import org.acumos.portal.be.common.ConfigConstants;
import org.acumos.portal.be.common.exception.AcumosServiceException;
import org.acumos.portal.be.service.MarketPlaceCatalogService;
import org.acumos.portal.be.transport.RevisionDescription;
import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import static com.github.tomakehurst.wiremock.client.WireMock.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = org.acumos.portal.be.Application.class, webEnvironment = WebEnvironment.RANDOM_PORT, properties = {
		ConfigConstants.cdms_client_url + "=http://localhost:8000/ccds",
		ConfigConstants.cdms_client_username + "=ccds_test", ConfigConstants.cdms_client_password + "=ccds_test" })
public class MarketPlaceCatalogServiceImplTest {

	@Rule
	public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().port(8000));

	@Autowired
	private MarketPlaceCatalogService catalogService;

	@Test
	public void addRevisionDescriptionTest() {

		stubFor(get(urlEqualTo("/ccds/code/pair/ACCESS_TYPE")).willReturn(aResponse().withStatus(HttpStatus.SC_OK)
				.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())
				.withBody("[" + "  {" + "    \"code\": \"RS\"," + "    \"name\": \"Restricted\"" + "  }," + "  {"
						+ "    \"code\": \"PR\"," + "    \"name\": \"Private\"" + "  }," + "  {"
						+ "    \"code\": \"PB\"," + "    \"name\": \"Public\"" + "  }," + "  {"
						+ "    \"code\": \"OR\"," + "    \"name\": \"Organization\"" + "  }" + "]")));

		//Return null response to create a description
		stubFor(get(urlEqualTo("/ccds/revision/1234-1234-1234-1234-1234/access/OR/descr")).willReturn(aResponse()
				.withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())));

		//Return null response to create a description
		stubFor(post(urlEqualTo("/ccds/revision/1234-1234-1234-1234-1234/access/OR/descr"))
				.willReturn(aResponse().withBody(
						"{\"created\":null,\"modified\":null,\"revisionId\":\"1234-1234-1234-1234-1234\",\"accessTypeCode\":\"OR\",\"description\":\"Test\"}")
						.withStatus(HttpStatus.SC_OK)
						.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())));

		RevisionDescription createdDesc = null;

		RevisionDescription description = new RevisionDescription();
		description.setAccessTypeCode("OR");
		description.setRevisionId("1234-1234-1234-1234-1234");
		description.setDescription("Test");

		try {
			createdDesc = catalogService.addUpdateRevisionDescription("1234-1234-1234-1234-1234", "OR", description);
		} catch (AcumosServiceException e) {
			Assert.assertFalse("Exception while Add Description", true);
		}
		Assert.assertNotNull(createdDesc);
		Assert.assertEquals("Test", description.getDescription());
	}

	@Test
	public void updateRevisionDescriptionTest() {

		stubFor(get(urlEqualTo("/ccds/code/pair/ACCESS_TYPE")).willReturn(aResponse().withStatus(HttpStatus.SC_OK)
				.withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())
				.withBody("[" + "  {" + "    \"code\": \"RS\"," + "    \"name\": \"Restricted\"" + "  }," + "  {"
						+ "    \"code\": \"PR\"," + "    \"name\": \"Private\"" + "  }," + "  {"
						+ "    \"code\": \"PB\"," + "    \"name\": \"Public\"" + "  }," + "  {"
						+ "    \"code\": \"OR\"," + "    \"name\": \"Organization\"" + "  }" + "]")));

		stubFor(get(urlEqualTo("/ccds/revision/1234-1234-1234-1234-1234/access/OR/descr")).willReturn(aResponse()
				.withBody("{" + "  \"created\": 1534289584000," + "  \"modified\": 1534289584000,"
						+ "  \"revisionId\": \"dac8ef87-0e66-4406-8374-95c78799b07c\","
						+ "  \"accessTypeCode\": \"OR\"," + "  \"description\": \"Test\"" + "}")
				.withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())));

		stubFor(put(urlEqualTo("/ccds/revision/1234-1234-1234-1234-1234/access/OR/descr")).willReturn(aResponse()
				.withBody(
						"{\"created\":null,\"modified\":null,\"revisionId\":\"1234-1234-1234-1234-1234\",\"accessTypeCode\":\"OR\",\"description\":\"UpdatedTest\"}")
				.withStatus(HttpStatus.SC_OK).withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())));

		RevisionDescription createdDesc = null;

		RevisionDescription description = new RevisionDescription();
		description.setAccessTypeCode("OR");
		description.setRevisionId("1234-1234-1234-1234-1234");
		description.setDescription("UpdatedTest");

		try {
			createdDesc = catalogService.addUpdateRevisionDescription("1234-1234-1234-1234-1234", "OR", description);
		} catch (AcumosServiceException e) {
			Assert.assertFalse("Exception while Updating Description Description", true);
		}
		Assert.assertNotNull(createdDesc);
	}
}