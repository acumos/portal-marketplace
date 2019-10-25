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
package org.acumos.be.test.common;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;

import java.util.ArrayList;
import java.util.List;

import org.acumos.portal.be.common.RestPageRequestBE;
import org.junit.Assert;
import org.junit.Test; 

public class RestPageRequestBETest {
  
	@Test
	public void testRestPageRequestBEParameter(){
		
		String accessType = "PB";
		String activeType = "Y";
		String description = "Test Description";
		String modelToolkitType = "RC";
		String modelType = "CL";
		String name = "Test Solution";
		Integer page = 9;
		String searchTerm = "Test";
		Integer size = 0;
		String sortBy = "ASC";
		String sortById = "";
		String sortingOrder = "ASC";
		String userId = "userId";
		List<String> tagList = new ArrayList<String>();
		tagList.add("tag-1");
		tagList.add("tag-2");
		List<String> dropTagList = new ArrayList<String>();
		dropTagList.add("dropTagList-1");
		dropTagList.add("dropTagList-2");
		
		RestPageRequestBE restReq = new RestPageRequestBE();
		restReq.setAccessType(accessType);
		restReq.setActiveType(activeType);
		restReq.setDescription(description);
		restReq.setModelToolkitType(modelToolkitType);
		restReq.setModelType(modelType);
		restReq.setName(name);
		restReq.setPage(page);
		restReq.setSearchTerm(searchTerm);
		restReq.setSize(size);
		restReq.setSortBy(sortBy);
		restReq.setSortById(sortById);
		restReq.setSortingOrder(sortingOrder);
		restReq.setSearchTerm(searchTerm);
		restReq.setUserId(userId);
		restReq.setTagList(tagList);
		restReq.setDropTagList(dropTagList);
		
		
		Assert.assertEquals(accessType, restReq.getAccessType());
		Assert.assertEquals(activeType, restReq.getActiveType());
		Assert.assertEquals(description, restReq.getDescription());
		Assert.assertEquals(modelToolkitType, restReq.getModelToolkitType());
		Assert.assertEquals(modelType, restReq.getModelType());
		Assert.assertEquals(name, restReq.getName());
		Assert.assertEquals(page, restReq.getPage());
		Assert.assertEquals(searchTerm, restReq.getSearchTerm());
		Assert.assertEquals(size, restReq.getSize());
		Assert.assertEquals(sortBy, restReq.getSortBy());
		Assert.assertEquals(sortById, restReq.getSortById());
		Assert.assertEquals(sortingOrder, restReq.getSortingOrder());
		Assert.assertEquals(searchTerm, restReq.getSearchTerm());
		Assert.assertEquals(userId, restReq.getUserId());
		Assert.assertEquals(tagList, restReq.getTagList());
		Assert.assertEquals(dropTagList, restReq.getDropTagList());
		
		Assert.assertNotNull(restReq);
		
	}
}
