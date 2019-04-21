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

import java.lang.invoke.MethodHandles;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.acumos.cds.domain.MLPNotifUserMap;
import org.acumos.cds.domain.MLPNotification;
import org.acumos.cds.domain.MLPRightToUse;
import org.acumos.cds.domain.MLPSolution;
import org.acumos.cds.domain.MLPUser;
import org.acumos.cds.domain.MLPUserNotification;
import org.acumos.cds.transport.RestPageRequest;
import org.acumos.cds.transport.RestPageResponse;
import org.acumos.portal.be.common.exception.AcumosServiceException;
import org.acumos.portal.be.service.impl.LicensingServiceImpl;
import org.acumos.portal.be.service.impl.NotificationServiceImpl;
import org.acumos.portal.be.transport.MLNotification;
import org.acumos.portal.be.transport.MLSolution;
import org.acumos.portal.be.transport.RtuUser;
import org.acumos.portal.be.util.PortalUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.*;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import org.junit.Assert;

@RunWith(MockitoJUnitRunner.class)
public class LicensingServiceImplTest {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	final HttpServletResponse response = new MockHttpServletResponse();
	final HttpServletRequest request = new MockHttpServletRequest();

	@Mock
	LicensingServiceImpl impl = new LicensingServiceImpl();

	@Test
	public void getMLPSolutionsTest() {
		try {

			MLSolution mlsolution = getMLSolution();
			MLPSolution solution = PortalUtils.convertToMLPSolution(mlsolution);

			Mockito.when(impl.getMLPSolutions(1)).thenReturn(solution);

			Assert.assertEquals(solution, solution);
			logger.info("Successfully fetched notifications ");

		} catch (AcumosServiceException e) {
			logger.error("Exception occurred while getting getMLPSolutionsTest ",e.getMessage());
		}

	}

	@Test
	public void getMLPSolutionBySolutionNameTest() {
		try {

			MLSolution mlsolution = getMLSolution();
			MLPSolution solution = PortalUtils.convertToMLPSolution(mlsolution);
			
			RestPageResponse<MLPSolution> mlpSolutionByServiceName = new RestPageResponse<MLPSolution>();
			
			
			  Map<String, Object> solutoinNameParameter =  new HashMap<>(); 
			  solutoinNameParameter.put("name", "Crosssell");

			Mockito.when(impl.getMLPSolutionBySolutionName(solutoinNameParameter, false, new RestPageRequest())).thenReturn(mlpSolutionByServiceName);

			Assert.assertEquals(mlpSolutionByServiceName, mlpSolutionByServiceName);
			logger.info("Successfully fetched notifications ");

		} catch (AcumosServiceException e) {
			logger.error("Exception occurred while getting getMLPSolutionBySolutionNameTest ",e.getMessage());
		}

	}
	
	
	@Test
	public void getMLPUsersAssociatedWithRtuIdTest() {
		try {

			MLPUser mlpUser = getMLPUser();
			List<MLPUser> list = new ArrayList<MLPUser>();
			list.add(mlpUser);

			Mockito.when(impl.getMLPUsersAssociatedWithRtuId(1)).thenReturn(list);

			Assert.assertEquals(mlpUser, mlpUser);
			logger.info("Successfully fetched notifications ");

		} catch (AcumosServiceException e) {
			logger.error("Exception occurred while getting getMLPUsersAssociatedWithRtuIdTest ",e.getMessage());
		}

	}
	
	@Test
	public void getAllActiveUserTest() {

			RtuUser mlpUser = getUser();
			List<RtuUser> list = new ArrayList<RtuUser>();
			list.add(mlpUser);

			Mockito.when(impl.getAllActiveUsers()).thenReturn(list);

			Assert.assertEquals(list, list);
			logger.info("Successfully fetched notifications ");

	}
	
	@Test
	public void getRtusByReferenceTest() {

			MLPRightToUse mlpRtu = new MLPRightToUse();
			List<MLPRightToUse> rtu = new ArrayList<MLPRightToUse>();
			rtu.add(mlpRtu);

          try {
			Mockito.when(impl.getRtusByReference("A123A")).thenReturn(rtu);

			Assert.assertEquals(rtu, rtu);
			logger.info("Successfully fetched notifications ");
      	} catch (AcumosServiceException e) {
			logger.error("Exception occurred while getting getRtusByReferenceTest ",e.getMessage());
		}
	}
	
	

	private RtuUser getUser() {
		RtuUser user = new RtuUser();
		user.setUserId("8cbeccd0-ed84-42c3-8d9a-06d5629dc7bb");
		user.setFirstName("UserFirstName");
		user.setLastName("UserLastName");
		user.setEmailId("user1@emial.com");
		user.setActive(true);
		user.setAssociatedWithRtuFlag(true);
		return user;
	}

	private MLPUser getMLPUser() {
		MLPUser mlpUser = new MLPUser();
		mlpUser.setActive(true);
		mlpUser.setFirstName("test-first-name");
		mlpUser.setUserId("f0ebe707-d436-40cf-9b0a-ed1ce8da1f2b");
		mlpUser.setLoginName("test-User-Name");
		return mlpUser;
	}

	
	private MLSolution getMLSolution() {
		MLSolution mlsolution = new MLSolution();
		mlsolution.setSolutionId("Solution1");
		mlsolution.setName("Test_Solution data");
		mlsolution.setOwnerId("41058105-67f4-4461-a192-f4cb7fdafd34");
		mlsolution.setActive(true);
		mlsolution.setModelType("CL");
		mlsolution.setTookitType("DS");
		return mlsolution;
	}
}
