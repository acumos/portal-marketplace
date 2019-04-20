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

package org.acumos.be.test.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;

import org.acumos.cds.transport.RestPageResponse;
import org.acumos.portal.be.util.PortalUtils;
import org.junit.Test;
import org.springframework.data.domain.PageRequest;

public class PortalUtilsTest {

	@Test
	public void convertRestPageResponseTest() {
		ArrayList<Integer> intList = new ArrayList<>();
		intList.add(1);
		intList.add(2);
		intList.add(3);
		PageRequest pageRequest = PageRequest.of(0, 3);
		int totalElements = 15;
		RestPageResponse<Integer> first = new RestPageResponse<>(intList, pageRequest, totalElements);

		ArrayList<Long> longList = new ArrayList<>();
		for (int i : intList) {
			longList.add((long) i);
		}
		RestPageResponse<Long> second = PortalUtils.convertRestPageResponse(first, longList);
		
		assertNotNull(second);
		assertNotNull(second.getContent());
		assertEquals(first.getNumber(), second.getNumber());
		assertEquals(first.getSize(), second.getSize());
		assertEquals(first.getTotalElements(), second.getTotalElements());
		assertEquals(first.getTotalPages(), second.getTotalPages());
		assertEquals(first.getPageable(), second.getPageable());
		assertEquals(first.isFirst(), second.isFirst());
		assertEquals(first.isLast(), second.isLast());
		assertEquals(first.isEmpty(), second.isEmpty());
		assertEquals(first.getNumberOfElements(), second.getNumberOfElements());
	}
}
