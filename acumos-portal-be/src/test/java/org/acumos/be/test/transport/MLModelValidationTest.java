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

import org.acumos.portal.be.transport.MLArtifactValidation;
import org.acumos.portal.be.transport.MLModelValidation;
import org.junit.Assert;
import org.junit.Test;

public class MLModelValidationTest {

	@Test	
	public void testMLModelValidationParameter(){
		
		String solutionId = "02a87750-7ba3-4ea7-8c20-c1286930f57c";
	    String revisionId = "sf242";
	    String visibility = "active";
	    String userId = "83d5259f-48b7-4fe1-9fd6-d1166f8f3691";	
	    List<MLArtifactValidation> artifactValidations = new ArrayList<MLArtifactValidation>();
	    MLArtifactValidation mlArtifactValidation = new MLArtifactValidation();
	    mlArtifactValidation.setArtifactId("adad333");
	    mlArtifactValidation.setArtifactName("demo");
	    mlArtifactValidation.setUrl("http://fsdf.com");
	    artifactValidations.add(mlArtifactValidation);
	    String callbackUrl = "http://sample.com";
	    
	    MLModelValidation mlModelValidation = new MLModelValidation();
	    mlModelValidation.setArtifactValidations(artifactValidations);
	    mlModelValidation.setCallbackUrl(callbackUrl);
	    mlModelValidation.setRevisionId(revisionId);
	    mlModelValidation.setSolutionId(solutionId);
	    mlModelValidation.setUserId(userId);
	    mlModelValidation.setVisibility(visibility);
		
		Assert.assertEquals(artifactValidations, mlModelValidation.getArtifactValidations());
		Assert.assertEquals(callbackUrl, mlModelValidation.getCallbackUrl());
		Assert.assertEquals(revisionId, mlModelValidation.getRevisionId());
		Assert.assertEquals(solutionId, mlModelValidation.getSolutionId());
		Assert.assertEquals(userId, mlModelValidation.getUserId());
		Assert.assertEquals(visibility, mlModelValidation.getVisibility());
	}
}
