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

/**
 * 
 *
 */
package org.acumos.be.test.transport;

import java.util.ArrayList;
import java.util.List;

import org.acumos.portal.be.transport.MLArtifactValidationStatus;
import org.acumos.portal.be.transport.MLModelValidationStatus;
import org.junit.Assert;
import org.junit.Test;

public class MLModelValidationStatusTest {
	
	@Test	
	public void testMLModelValidationStatusParameter(){
		
		String taskId = "2425";
	    String solutionId = "02a87750-7ba3-4ea7-8c20-c1286930f57c";
	    String status = "active";
	    String revisionId = "sfsf";
	    String visibility = "sfsfs";
	    List<MLArtifactValidationStatus> mlModelValidationStatusList = new ArrayList<MLArtifactValidationStatus>();
	    MLArtifactValidationStatus mlModelValidationStepStatus = new MLArtifactValidationStatus();
	    mlModelValidationStepStatus.setArtifactId("fsfsfr3353e");
	    mlModelValidationStepStatus.setArtifactTaskId("4343");
	    mlModelValidationStepStatus.setStatus("active");
	    mlModelValidationStepStatus.setValidationTaskType("fsfs");
	    
	    mlModelValidationStatusList.add(mlModelValidationStepStatus);
	    MLModelValidationStatus mlModelValidationCheck = new  MLModelValidationStatus();
	    mlModelValidationCheck.setRevisionId(revisionId);
	    mlModelValidationCheck.setSolutionId(solutionId);
	    mlModelValidationCheck.setStatus(status);
	    mlModelValidationCheck.setTaskId(taskId);
	    mlModelValidationCheck.setVisibility(visibility);
	    mlModelValidationCheck.setArtifactValidationStatus(mlModelValidationStatusList);
	    
		
		Assert.assertEquals(taskId, mlModelValidationCheck.getTaskId());
		Assert.assertEquals(solutionId, mlModelValidationCheck.getSolutionId());
		Assert.assertEquals(status, mlModelValidationCheck.getStatus());
		Assert.assertEquals(revisionId, mlModelValidationCheck.getRevisionId());
		Assert.assertEquals(visibility, mlModelValidationCheck.getVisibility());
		Assert.assertEquals(mlModelValidationStatusList, mlModelValidationCheck.getArtifactValidationStatus());
	}
}
