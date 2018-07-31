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

package org.acumos.portal.be.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import org.acumos.cds.client.CommonDataServiceRestClientImpl;
import org.acumos.cds.client.ICommonDataServiceRestClient;
import org.acumos.nexus.client.NexusArtifactClient;
import org.acumos.nexus.client.RepositoryLocation;
import org.acumos.portal.be.util.PortalUtils;

public abstract class AbstractServiceImpl {

	@Autowired
	private Environment env;
	
	public ICommonDataServiceRestClient getClient() {
		ICommonDataServiceRestClient client = new CommonDataServiceRestClientImpl(env.getProperty("cdms.client.url"), env.getProperty("cdms.client.username"), env.getProperty("cdms.client.password"));
		return client;
	}
	
	public void setEnvironment (Environment env1){
		env = env1;
	}
	
	public NexusArtifactClient getNexusClient() {
		RepositoryLocation repositoryLocation = new RepositoryLocation();
		repositoryLocation.setId("1");
		repositoryLocation.setUrl(env.getProperty("nexus.url"));
		repositoryLocation.setUsername(env.getProperty("nexus.username"));
		repositoryLocation.setPassword(env.getProperty("nexus.password"));

		if (!PortalUtils.isEmptyOrNullString(env.getProperty("nexus.proxy"))) {
				repositoryLocation.setProxy(env.getProperty("nexus.proxy"));
		}

		NexusArtifactClient artifactClient = new NexusArtifactClient(repositoryLocation);
		return artifactClient;
	}
}
