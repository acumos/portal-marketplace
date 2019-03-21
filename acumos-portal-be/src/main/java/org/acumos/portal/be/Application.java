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
 */
package org.acumos.portal.be;

import java.lang.invoke.MethodHandles;
import java.util.concurrent.Future;

import javax.annotation.Resource;

import org.acumos.portal.be.service.AsyncServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.boot.CommandLineRunner;

import com.fasterxml.jackson.databind.ObjectMapper;

@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
//@EnableAspectJAutoProxy(proxyTargetClass=true)
@EnableAspectJAutoProxy
@SpringBootApplication
public class Application implements ApplicationContextAware, CommandLineRunner {

	private final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());	

	public static final String CONFIG_ENV_VAR_NAME = "SPRING_APPLICATION_JSON";
	
	@Resource 
	AsyncServices services;

	public static void main(String[] args) throws Exception {
		final String springApplicationJson = System.getenv(CONFIG_ENV_VAR_NAME);
		if (springApplicationJson != null && springApplicationJson.contains("{")) {
			final ObjectMapper mapper = new ObjectMapper();
			// ensure it's valid
			mapper.readTree(springApplicationJson);
			logger.info("main: successfully parsed configuration from environment {}", CONFIG_ENV_VAR_NAME);
		} else {
			logger.warn("main: no configuration found in environment {}", CONFIG_ENV_VAR_NAME);
		}
        SpringApplication.run(Application.class, args);
	}
	
	@Override
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		((ConfigurableEnvironment) context.getEnvironment()).setActiveProfiles("src");
	}

	@Override
	public void run(String... arg0) throws Exception {
		// TODO Auto-generated method stub
		Future<String> process1 = services.initiateAsyncProcess();
		Future<String> process2 = services.initiateAsyncProcess();
		Future<String> process3 = services.initiateAsyncProcess();
		
		// Wait until They are all Done
		// If all are not Done. Pause 2s for next re-check
		while(!(process1.isDone() && process2.isDone() && process3.isDone())){
			Thread.sleep(2000);
		}
		logger.info("All Processes are DONE!");
		// Log results
		logger.info("Process 1: " + process1.get());
		logger.info("Process 2: " + process2.get());
		logger.info("Process 3: " + process3.get());
	}	
}
