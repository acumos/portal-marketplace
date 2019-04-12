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

import java.time.Instant;

import org.acumos.portal.be.transport.MLCatalog;
import org.junit.Test;

public class MLCatalogTest {

	@Test
	public void MLCatalogParametersTest() {
		String catalogId = "12345678-abcd-90ab-cdef-1234567890ab";
		String accessTypeCode = "PB";
		boolean selfPublish = false;
		String name = "Test catalog";
		String description = "Test desc";
		String publisher = "Test publisher";
		String url = "http://test.localhost.org";
		long solutionCount = 1;
		Instant created = Instant.now();
		Instant modified = Instant.now();
		
		MLCatalog catalog = new MLCatalog();
		catalog.setCatalogId(catalogId);
		catalog.setAccessTypeCode(accessTypeCode);
		catalog.setSelfPublish(selfPublish);
		catalog.setName(name);
		catalog.setDescription(description);
		catalog.setPublisher(publisher);
		catalog.setUrl(url);
		catalog.setSolutionCount(solutionCount);
		catalog.setCreated(created);
		catalog.setModified(modified);
		
		assertEquals(catalogId, catalog.getCatalogId());
		assertEquals(accessTypeCode, catalog.getAccessTypeCode());
		assertEquals(selfPublish, catalog.isSelfPublish());
		assertEquals(name, catalog.getName());
		assertEquals(description, catalog.getDescription());
		assertEquals(publisher, catalog.getPublisher());
		assertEquals(url, catalog.getUrl());
		assertEquals(solutionCount, catalog.getSolutionCount());
		assertEquals(created, catalog.getCreated());
		assertEquals(modified, catalog.getModified());
	}

}
