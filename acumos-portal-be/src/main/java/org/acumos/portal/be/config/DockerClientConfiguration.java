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

package org.acumos.portal.be.config;


import org.acumos.portal.be.docker.DockerConfiguration;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import ch.qos.logback.classic.Level;

@Configuration
public class DockerClientConfiguration
{
	
	@Autowired
	private Environment environment;

	@Bean
	public DockerConfiguration dockerConfiguration()
	{
		
		ch.qos.logback.classic.Logger rootLogger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
		rootLogger.setLevel(Level.toLevel("info"));
		DockerConfiguration config = new DockerConfiguration();
		config.setConfig(environment.getProperty("docker.config", config.getConfig()));
		config.setApiVersion(environment.getProperty("docker.api.version", config.getApiVersion()));
		config.setHost(environment.getProperty("docker.host", config.getHost()));
		config.setPort(Integer.parseInt(environment.getProperty("docker.port", String.valueOf(config.getPort()))));
		boolean tlsVerify = Boolean.parseBoolean(environment.getProperty("docker.tls.verify", String.valueOf(config.isTlsVerify())));
		if (tlsVerify)
		{
			config.setTlsVerify(true);
			config.setCertPath(environment.getProperty("docker.tls.certPath"));
		}
		if (environment.containsProperty("docker.registry.url"))
		{
			config.setRegistryUrl(environment.getProperty("docker.registry.url", config.getRegistryUrl()));
			config.setRegistryUsername(environment.getProperty("docker.registry.username", config.getRegistryUsername()));
			config.setRegistryPassword(environment.getProperty("docker.registry.password", config.getRegistryPassword()));
			config.setRegistryEmail(environment.getProperty("docker.registry.email", config.getRegistryEmail()));
			config.setImagetagPrefix(environment.getProperty("docker.imagetag.prefix", config.getImagetagPrefix()));
		}
		config.setCmdExecFactory(environment.getProperty("docker.cmdExecFactory", config.getCmdExecFactory()));
		config.setMaxTotalConnections(Integer.parseInt(environment.getProperty("docker.max_total_connections", String.valueOf(config.getMaxTotalConnections()))));
		config.setMaxPerRouteConnections(Integer.parseInt(environment.getProperty("docker.max_per_route_connections", String.valueOf(config.getMaxPerRouteConnections()))));
		System.err.println("docker host:"+config.getHost());
		return config;
	}
}
