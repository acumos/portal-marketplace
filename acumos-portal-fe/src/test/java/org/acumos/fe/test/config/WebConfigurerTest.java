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
package org.acumos.fe.test.config;

import java.io.File;
import java.lang.invoke.MethodHandles;
import java.util.List;

import org.acumos.portal.fe.AcumosPortalApplication;
import org.acumos.portal.fe.config.WebConfigurer;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.embedded.AbstractConfigurableEmbeddedServletContainer;
import org.springframework.boot.web.servlet.ServletContextInitializer;


public class WebConfigurerTest {
	
	 private static final int _DEFAULT_PORT = 8080;

	 private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	 private AbstractConfigurableEmbeddedServletContainer dummyconfigurableEmbeddedServletContainer = new AbstractConfigurableEmbeddedServletContainer() {
		 
		private File root;
		
		@Override
		public void setPort(int port) {
					
		}		

		@Override
		public void setDocumentRoot(File documentRoot) {
			root = documentRoot;			
		}

		@Override
		public void setInitializers(List<? extends ServletContextInitializer> initializers) {
						
		}

		@Override
		public void addInitializers(ServletContextInitializer... initializers) {
						
		}		

		@Override
		public File getDocumentRoot() {
			return root;	
		}		
	};		
	
	@Test
    public void contextLoads() throws Exception {	
		
		logger.info("Started contextLoads()");		
		WebConfigurer webConfigurer = new WebConfigurer();
		webConfigurer.customize(dummyconfigurableEmbeddedServletContainer);
		Assert.assertNotNull(dummyconfigurableEmbeddedServletContainer.getDocumentRoot());
        Assert.assertEquals(dummyconfigurableEmbeddedServletContainer.getPort(), _DEFAULT_PORT);
		logger.info("Finished  contextLoads()");
    }
}
