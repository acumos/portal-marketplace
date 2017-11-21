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

public class DockerConfiguration
{
	private String config;

	private String apiVersion = "1.23";

	private String host = "localhost";

	private Integer port = 2375;

	private String registryUsername;

	private String registryPassword;
	
	private String imagetagPrefix;

	private String registryUrl = "https://index.docker.io/v1/";

	private String registryEmail;

	private Integer requestTimeout;

	private boolean tlsVerify;

	private String certPath;

	private boolean socket = false;

	private String cmdExecFactory = "com.github.dockerjava.netty.NettyDockerCmdExecFactory";

	private Integer maxTotalConnections = 100;

	private Integer maxPerRouteConnections = 100;

	public String getConfig()
	{
		return config;
	}

	public void setConfig(String config)
	{
		this.config = config;
	}

	public String toUrl() throws AcumosServiceException
	{
		if (this.host == null)
			throw new AcumosServiceException("host is required");
		if (this.port == null)
			throw new AcumosServiceException("port is required");
		return ((this.socket) ? "unix" : "tcp") + "://" + host + ":" + port;
	}

	public String getApiVersion()
	{
		return apiVersion;
	}

	public void setApiVersion(String apiVersion)
	{
		this.apiVersion = apiVersion;
	}

	public String getHost()
	{
		return host;
	}

	public void setHost(String host)
	{
		this.host = host;
	}

	public Integer getPort()
	{
		return port;
	}

	public void setPort(Integer port)
	{
		this.port = port;
	}

	public String getRegistryUsername()
	{
		return registryUsername;
	}

	public void setRegistryUsername(String registryUsername)
	{
		this.registryUsername = registryUsername;
	}

	public String getRegistryPassword()
	{
		return registryPassword;
	}

	public void setRegistryPassword(String registryPassword)
	{
		this.registryPassword = registryPassword;
	}

	/**
	 * @return the imagetagPrefix
	 */
	public String getImagetagPrefix() {
		return imagetagPrefix;
	}

	/**
	 * @param imagetagPrefix the imagetagPrefix to set
	 */
	public void setImagetagPrefix(String imagetagPrefix) {
		this.imagetagPrefix = imagetagPrefix;
	}

	public String getRegistryUrl()
	{
		return registryUrl;
	}

	public void setRegistryUrl(String registryUrl)
	{
		this.registryUrl = registryUrl;
	}

	public String getRegistryEmail()
	{
		return registryEmail;
	}

	public void setRegistryEmail(String registryEmail)
	{
		this.registryEmail = registryEmail;
	}

	public Integer getRequestTimeout()
	{
		return requestTimeout;
	}

	public void setRequestTimeout(Integer requestTimeout)
	{
		this.requestTimeout = requestTimeout;
	}

	public boolean isTlsVerify()
	{
		return tlsVerify;
	}

	public void setTlsVerify(boolean tlsVerify)
	{
		this.tlsVerify = tlsVerify;
	}

	public String getCertPath()
	{
		return certPath;
	}

	public void setCertPath(String certPath)
	{
		this.certPath = certPath;
	}

	public boolean isSocket()
	{
		return socket;
	}

	public void setSocket(boolean socket)
	{
		this.socket = socket;
	}

	public String getCmdExecFactory()
	{
		return cmdExecFactory;
	}

	public void setCmdExecFactory(String cmdExecFactory)
	{
		this.cmdExecFactory = cmdExecFactory;
	}

	public Integer getMaxTotalConnections()
	{
		return maxTotalConnections;
	}

	public void setMaxTotalConnections(Integer maxTotalConnections)
	{
		this.maxTotalConnections = maxTotalConnections;
	}

	public Integer getMaxPerRouteConnections()
	{
		return maxPerRouteConnections;
	}

	public void setMaxPerRouteConnections(Integer maxPerRouteConnections)
	{
		this.maxPerRouteConnections = maxPerRouteConnections;
	}
}
