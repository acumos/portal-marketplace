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

import org.acumos.portal.be.transport.MLSolutionFavorite;
import org.junit.Assert;
import org.junit.Test;

public class MLSolutionFavoriteTest {

	@Test	
	public void testMLSolutionFavoriteParameter(){
		
		String solutionID = "02a87750-7ba3-4ea7-8c20-c1286930f57c";
		String userID = "83d5259f-48b7-4fe1-9fd6-d1166f8f3691";

		MLSolutionFavorite mlSolutionFavorite = new  MLSolutionFavorite();

		mlSolutionFavorite.setSolutionID(solutionID);
		mlSolutionFavorite.setUserID(userID);		

		Assert.assertEquals(solutionID, mlSolutionFavorite.getSolutionID());
		Assert.assertEquals(userID, mlSolutionFavorite.getUserID());


	}

}
