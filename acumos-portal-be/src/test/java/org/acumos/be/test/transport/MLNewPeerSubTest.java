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

import org.acumos.cds.domain.MLPCatalog;
import org.acumos.portal.be.transport.MLNewPeerSub;
import org.junit.Test;

public class MLNewPeerSubTest {

	@Test
	public void MLNewPeerSubParamtersTest() {
		String ownerId = "Test owner id";
		Long refreshInterval = 3600L;
		
		String accessTypeCode = "PB";
		boolean selfPublish = false;
		String description = "A catalog for testing";
		String name = "Test catalog";
		String origin = "acumos.org";
		String publisher = "Acumos";
		String url ="http://localhost";
		
		MLPCatalog catalog = new MLPCatalog();
		catalog.setAccessTypeCode(accessTypeCode);
		catalog.setSelfPublish(selfPublish);
		catalog.setDescription(description);
		catalog.setName(name);
		catalog.setOrigin(origin);
		catalog.setPublisher(publisher);
		catalog.setUrl(url);
		
		MLNewPeerSub newSub = new MLNewPeerSub();
		newSub.setOwnerId(ownerId);
		newSub.setRefreshInterval(refreshInterval);
		newSub.setCatalog(catalog);
		
		assertNotNull(newSub);
		assertEquals(ownerId, newSub.getOwnerId());
		assertEquals(refreshInterval, newSub.getRefreshInterval());
		assertEquals(catalog, newSub.getCatalog());
	}
	
}
