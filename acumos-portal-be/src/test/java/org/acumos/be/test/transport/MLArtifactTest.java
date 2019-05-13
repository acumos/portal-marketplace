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
 
import java.util.Date;

import org.acumos.portal.be.transport.MLArtifact;
import org.junit.Assert;
import org.junit.Test;

public class MLArtifactTest { 

		
	@Test
	public void testMLArtifactParameter(){
		Long solutionId = 343335l;
		String artifactId = "535355l";
		String version = "1.1";
		String artifactType = "CL";
		String name = "rahul";
		String description = "sfsdgd";
		String artifactUri = "http:fsf/fsf";
		String ownerId = "46464646l"; 
		String metadata = "sfsfsfs";
		Date created = new Date();
		Date modified = new Date();
		MLArtifact mlArtifact = new MLArtifact();
		mlArtifact.setSolutionId(solutionId);
		mlArtifact.setArtifactId(artifactId);
		mlArtifact.setCreated(created);
		mlArtifact.setArtifactUri(artifactUri);
		mlArtifact.setModified(modified);
		mlArtifact.setMetadata(metadata);
		mlArtifact.setArtifactType(artifactType);
		mlArtifact.setName(name);
		mlArtifact.setOwnerId(ownerId);
		mlArtifact.setDescription(description);
		mlArtifact.setVersion(version);
		
		Assert.assertEquals(solutionId, mlArtifact.getSolutionId());
		Assert.assertEquals(artifactId, mlArtifact.getArtifactId());
		Assert.assertEquals(version, mlArtifact.getVersion());
		Assert.assertEquals(artifactType, mlArtifact.getArtifactType());
		Assert.assertEquals(name, mlArtifact.getName());
		Assert.assertEquals(description, mlArtifact.getDescription());
		Assert.assertEquals(artifactUri, mlArtifact.getArtifactUri());
		Assert.assertEquals(ownerId, mlArtifact.getOwnerId());
		Assert.assertEquals(metadata, mlArtifact.getMetadata());
		Assert.assertEquals(created, mlArtifact.getCreated());
		Assert.assertEquals(modified, mlArtifact.getModified());
	}

	
}
