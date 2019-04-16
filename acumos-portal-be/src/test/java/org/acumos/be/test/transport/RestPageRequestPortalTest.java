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

package org.acumos.be.test.transport;

import org.acumos.cds.transport.RestPageRequest;
import org.acumos.portal.be.transport.RestPageRequestPortal;
import org.junit.Assert;
import org.junit.Test;

public class RestPageRequestPortalTest {

	@Test
	public void testRestPageRequestPortalParameter() {

		String[] nameKeyword = {"solution1"};
		String[] descriptionKeyword = {"artificial inte"};
		String authorKeyword = "23c7a5a3-aa43-4f79-9bf6-fa42a0bae527";
		boolean active = true;
		boolean published = true;
		String[] modelTypeCodes = { "CL", "DS" };
		String[] validationStatusCodes = { "PS" };
		String[] tags = { "tag1" };
		RestPageRequest pageRequest = new RestPageRequest();
		String sortBy = "MD";
		String sortById = "5d33dee4-7833-413f-b56c-f90eaa0e2f09";
		String[] catalogIds = { "1234", "4321" };

		RestPageRequestPortal portal = new RestPageRequestPortal();
		portal.setPublished(published);
		portal.setNameKeyword(nameKeyword);
		portal.setActive(active);
		portal.setAuthorKeyword(authorKeyword);
		portal.setDescriptionKeyword(descriptionKeyword);
		portal.setModelTypeCodes(modelTypeCodes);
		portal.setSortBy(sortBy);
		portal.setSortById(sortById);
		portal.setTags(tags);
		portal.setPageRequest(pageRequest);
		portal.setValidationStatusCodes(validationStatusCodes);
		portal.setCatalogIds(catalogIds);

		Assert.assertNotNull(portal);
		Assert.assertEquals(nameKeyword, portal.getNameKeyword());
		Assert.assertEquals(descriptionKeyword, portal.getDescriptionKeyword());
		Assert.assertEquals(authorKeyword, portal.getAuthorKeyword());
		Assert.assertEquals(active, portal.isActive());
		Assert.assertEquals(published, portal.isPublished());
		Assert.assertNotNull(portal.getSortBy());
		Assert.assertNotNull(portal.getSortById());
		Assert.assertNotNull(portal.toString());
		Assert.assertNotNull(portal.getModelTypeCodes());
		Assert.assertNotNull(portal.getValidationStatusCodes());
		Assert.assertNotNull(portal.getTags());
		Assert.assertEquals(pageRequest, portal.getPageRequest());
		Assert.assertEquals(catalogIds, portal.getCatalogIds());
		
	}
}
