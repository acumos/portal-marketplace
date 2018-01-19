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
		String[] modelTypeCodes = { "CL", "DS" };
		String[] accessTypeCodes = { "PR", "PB" };
		String[] validationStatusCodes = { "PS" };
		String[] tags = { "tag1" };
		RestPageRequest pageRequest = new RestPageRequest();
		String sortBy = "MD";
		String sortById = "5d33dee4-7833-413f-b56c-f90eaa0e2f09";

		RestPageRequestPortal portal = new RestPageRequestPortal();
		portal.setAccessTypeCodes(accessTypeCodes);
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

		Assert.assertNotNull(portal);
		Assert.assertEquals(nameKeyword, portal.getNameKeyword());
		Assert.assertEquals(descriptionKeyword, portal.getDescriptionKeyword());
		Assert.assertEquals(authorKeyword, portal.getAuthorKeyword());
		Assert.assertEquals(active, portal.isActive());
		Assert.assertNotNull(portal.getSortBy());
		Assert.assertNotNull(portal.getSortById());
		Assert.assertNotNull(portal.toString());
		Assert.assertNotNull(portal.getModelTypeCodes());
		Assert.assertNotNull(portal.getAccessTypeCodes());
		Assert.assertNotNull(portal.getValidationStatusCodes());
		Assert.assertNotNull(portal.getTags());
		Assert.assertEquals(pageRequest, portal.getPageRequest());
		
	}
}
