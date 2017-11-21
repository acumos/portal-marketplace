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

import org.acumos.portal.be.common.RestPageRequestBE;
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
	}
}
