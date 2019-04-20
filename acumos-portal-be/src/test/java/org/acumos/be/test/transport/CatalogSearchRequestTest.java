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

package org.acumos.be.test.transport;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Map;

import org.acumos.cds.domain.MLPCatalog_;
import org.acumos.portal.be.transport.CatalogSearchRequest;
import org.junit.Test;

public class CatalogSearchRequestTest {
	
	@Test
	public void paramsMapTest() {
		String accessTypeCode = "PB";
		String selfPublish = "false";
		String description = "A catalog for testing";
		String name = "Test catalog";
		String origin = "acumos.org";
		String publisher = "Acumos";
		String url ="http://localhost";
		
		CatalogSearchRequest searchRequest = new CatalogSearchRequest();
		searchRequest.setAccessTypeCode(accessTypeCode);
		searchRequest.setSelfPublish(selfPublish);
		searchRequest.setDescription(description);
		searchRequest.setName(name);
		searchRequest.setOrigin(origin);
		searchRequest.setPublisher(publisher);
		searchRequest.setUrl(url);
		
		Map<String, Object> params = searchRequest.paramsMap();
		assertNotNull(params);
		assertEquals(accessTypeCode, params.get(MLPCatalog_.ACCESS_TYPE_CODE));
		assertEquals(selfPublish, params.get(MLPCatalog_.SELF_PUBLISH));
		assertEquals(description, params.get(MLPCatalog_.DESCRIPTION));
		assertEquals(name, params.get(MLPCatalog_.NAME));
		assertEquals(origin, params.get(MLPCatalog_.ORIGIN));
		assertEquals(publisher, params.get(MLPCatalog_.PUBLISHER));
		assertEquals(url, params.get(MLPCatalog_.URL));
	}
	
}
