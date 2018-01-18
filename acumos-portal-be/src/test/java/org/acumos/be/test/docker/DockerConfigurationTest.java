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
package org.acumos.be.test.docker;

import org.acumos.portal.be.docker.DockerConfiguration;
import org.junit.Assert;
import org.junit.Test;

public class DockerConfigurationTest {

	@Test
	public void DockerConfigurationParam() {
		String config="config";
		String apiVersion = "1.23";
		String host = "localhost";
		Integer port = 2375;
		String registryUrl = "https://index.docker.io/v1/";
		boolean socket = false;
		String cmdExecFactory = "com.github.dockerjava.netty.NettyDockerCmdExecFactory";
		Integer maxTotalConnections = 100;
		Integer maxPerRouteConnections = 100;
		String registryUsername="registryUsername";
		String registryPassword="registryPassword";
		String imagetagPrefix="imagePrefix";
		String registryEmail="testmail";
		Integer requestTimeout=1000;
		boolean tlsVerify=false;
		String certPath="pathValue";
		
		DockerConfiguration dockerConfiguration= new DockerConfiguration();
		dockerConfiguration.setApiVersion(apiVersion);
		dockerConfiguration.setHost(host);
		dockerConfiguration.setPort(port);
		dockerConfiguration.setRegistryUrl(registryUrl);
		dockerConfiguration.setCmdExecFactory(cmdExecFactory);
		dockerConfiguration.setMaxTotalConnections(maxTotalConnections);
		dockerConfiguration.setMaxPerRouteConnections(maxPerRouteConnections);
		dockerConfiguration.setSocket(socket);
		dockerConfiguration.setRegistryUsername(registryUsername);
		dockerConfiguration.setRegistryPassword(registryPassword);
		dockerConfiguration.setImagetagPrefix(imagetagPrefix);
		dockerConfiguration.setRegistryEmail(registryEmail);
		dockerConfiguration.setRequestTimeout(requestTimeout);
		dockerConfiguration.setTlsVerify(tlsVerify);
		dockerConfiguration.setCertPath(certPath);
		dockerConfiguration.setConfig(config);
		
		Assert.assertNotNull(dockerConfiguration);
		Assert.assertEquals(config, dockerConfiguration.getConfig());
		Assert.assertEquals(apiVersion, dockerConfiguration.getApiVersion());
		Assert.assertEquals(host, dockerConfiguration.getHost());
		Assert.assertEquals(port, dockerConfiguration.getPort());
		Assert.assertEquals(registryUrl, dockerConfiguration.getRegistryUrl());
		Assert.assertEquals(cmdExecFactory, dockerConfiguration.getCmdExecFactory());
		Assert.assertEquals(socket, dockerConfiguration.isSocket());
		Assert.assertEquals(maxTotalConnections, dockerConfiguration.getMaxTotalConnections());
		Assert.assertEquals(maxPerRouteConnections, dockerConfiguration.getMaxPerRouteConnections());
		Assert.assertEquals(registryUsername, dockerConfiguration.getRegistryUsername());
		Assert.assertEquals(registryPassword, dockerConfiguration.getRegistryPassword());
		Assert.assertEquals(imagetagPrefix, dockerConfiguration.getImagetagPrefix());
		Assert.assertEquals(registryEmail, dockerConfiguration.getRegistryEmail());
		Assert.assertEquals(requestTimeout, dockerConfiguration.getRequestTimeout());
		Assert.assertEquals(tlsVerify, dockerConfiguration.isTlsVerify());
		Assert.assertEquals(certPath, dockerConfiguration.getCertPath());
		
		/*Assert.assertNotNull(dockerConfiguration);
		try {
			Assert.assertNotNull(dockerConfiguration.toUrl());
		} catch (AcumosServiceException e) {
		}*/
	}
}
