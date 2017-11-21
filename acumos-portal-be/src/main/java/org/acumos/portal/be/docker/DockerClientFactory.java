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

package org.acumos.portal.be.docker;

import org.acumos.portal.be.common.exception.AcumosServiceException;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.DockerCmdExecFactory;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.LocalDirectorySSLConfig;
import com.github.dockerjava.core.SSLConfig;
import com.github.dockerjava.jaxrs.JerseyDockerCmdExecFactory;
import com.github.dockerjava.netty.NettyDockerCmdExecFactory;

/**
 * Methods for communicating with Docker
 */
public final class DockerClientFactory
{
	
	public static DockerClient getDockerClient(DockerConfiguration config) throws AcumosServiceException
	{
		SSLConfig sslConfig;
		if (config.isTlsVerify())
		{
			if (config.getCertPath() == null)
				throw new AcumosServiceException("certPath must be specified in secure mode");
			sslConfig = new LocalDirectorySSLConfig(config.getCertPath());
		} else
		{
			// docker-java requires an implementation of SslConfig interface
			// to be available for DockerCmdExecFactoryImpl
			sslConfig = new NoImplSslConfig();
		}
		DefaultDockerClientConfig.Builder configBuilder = DefaultDockerClientConfig.createDefaultConfigBuilder().withDockerHost(config.toUrl())
				.withApiVersion(config.getApiVersion()).withDockerTlsVerify(config.isTlsVerify()).withRegistryUrl(config.getRegistryUrl())
				.withRegistryUsername(config.getRegistryUsername()).withRegistryPassword(config.getRegistryPassword()).withRegistryEmail(config.getRegistryEmail())
				.withCustomSslConfig(sslConfig);
		configBuilder.withDockerConfig(config.getConfig());
		if (config.getCertPath() != null)
		{
			configBuilder.withDockerCertPath(config.getCertPath());
		}
		String cmdExecFactory = config.getCmdExecFactory();
		DockerCmdExecFactory factory = null;
		if (cmdExecFactory.equals(JerseyDockerCmdExecFactory.class.getName()))
		{
			factory = new JerseyDockerCmdExecFactory();
			((JerseyDockerCmdExecFactory) factory).withReadTimeout(config.getRequestTimeout()).withConnectTimeout(config.getRequestTimeout())
					.withMaxTotalConnections(config.getMaxTotalConnections()).withMaxPerRouteConnections(config.getMaxPerRouteConnections());
		} else if (cmdExecFactory.equals(NettyDockerCmdExecFactory.class.getName()))
		{
			factory = new NettyDockerCmdExecFactory();
			((NettyDockerCmdExecFactory) factory).withConnectTimeout(config.getRequestTimeout());
		} else
		{
			try
			{
				@SuppressWarnings("unchecked")
				Class<DockerCmdExecFactory> clazz = (Class<DockerCmdExecFactory>) Class.forName(cmdExecFactory);
				try
				{
					factory = clazz.newInstance();
				} catch (InstantiationException | IllegalAccessException e)
				{
					throw new IllegalStateException("Unable to craete Instance of DockerCmdExecFactory class: " + cmdExecFactory, e);
				}
			} catch (ClassNotFoundException e)
			{
				throw new IllegalStateException("Unable to resolve DockerCmdExecFactory class: " + cmdExecFactory, e);
			}
		}
		DockerClient dockerClient = DockerClientBuilder.getInstance(configBuilder).withDockerCmdExecFactory(factory).build();
		return dockerClient;
	}
}
