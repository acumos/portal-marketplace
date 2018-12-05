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

package org.acumos.portal.be.common;

import org.acumos.portal.be.config.GatewayClientConfiguration;
import org.acumos.portal.be.util.EELFLoggerDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Unique entry point for building clients: peer access clients, cds clients
 */
@Component("clients")
@Scope("singleton")
public class Clients {
	
	private final EELFLoggerDelegate log = EELFLoggerDelegate.getLogger(Clients.class);

	@Autowired
	private ApplicationContext appCtx = null;
	
	@Autowired
	private GatewayClientConfiguration gatewayClientConfiguration = null;
	
	@Autowired
	private Environment env;
	
	public void	setApplicationContext(ApplicationContext theAppContext) {
		this.appCtx = theAppContext;
	}
		
	public Clients() {
		log.info(EELFLoggerDelegate.debugLogger, "Clients::new");
	}	

	/**
	 * Build a client for the given local uri
	 */
	public GatewayClient getGatewayClient() {
		final String gatewayUrlKey = "gateway.url";
        final String gatewayUrl = env.getProperty(gatewayUrlKey);
        if (gatewayUrl == null || gatewayUrl.isEmpty())
            throw new IllegalArgumentException("getGatewayClient: failed to find config " + gatewayUrlKey);
        return new GatewayClient(gatewayUrl, gatewayClientConfiguration.buildClient());
	}	
}
