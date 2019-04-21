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

import java.util.ArrayList;
import java.util.List;

import org.acumos.cds.domain.MLPSolution;
import org.acumos.cds.transport.RestPageResponse;
import org.acumos.portal.be.transport.MLSolution;
import org.acumos.portal.be.transport.RightToUseDetails;
import org.acumos.portal.be.transport.RtuUser;
import org.acumos.portal.be.util.PortalUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.data.domain.PageRequest;

public class RightToUseDetailsTest {

	@Test	
	public void testOauthUserParameter(){
		
		MLSolution mlsolution = getMLSolution();
		MLPSolution solution = PortalUtils.convertToMLPSolution(mlsolution);
		List<MLPSolution> solutionList = new ArrayList<MLPSolution>();
		solutionList.add(solution);

		RtuUser user = getUser();
		List<RtuUser> rtuUser = new ArrayList<RtuUser>();
		rtuUser.add(user);
		
		RestPageResponse<MLPSolution> solutionsByName = new RestPageResponse<>(solutionList, PageRequest.of(0, 1), 1);
	
		
		RightToUseDetails rightToUseDetails= new RightToUseDetails();
		rightToUseDetails.setMlpSolutionAssociatedWithRtuId(solutionList);
		rightToUseDetails.setRtuUsers(rtuUser);
		rightToUseDetails.setSolutionsByName(solutionsByName);
		
		
		Assert.assertEquals(solutionList, rightToUseDetails.getMlpSolutionAssociatedWithRtuId());
		Assert.assertEquals(rtuUser, rightToUseDetails.getRtuUsers());
		Assert.assertEquals(solutionsByName, rightToUseDetails.getSolutionsByName());
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
	
	private RtuUser getUser(){
		RtuUser user = new RtuUser();
		user.setUserId("8cbeccd0-ed84-42c3-8d9a-06d5629dc7bb");
		user.setFirstName("UserFirstName");
		user.setLastName("UserLastName");
		user.setEmailId("user1@emial.com");
		user.setActive(true);
		user.setAssociatedWithRtuFlag(true);
		return user;
	}
}
