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

package org.acumos.portal.be.aspect;

import org.acumos.portal.be.transport.User;
import org.acumos.portal.be.util.EELFLoggerDelegate;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
/**
*
* Aspect  for General Logging.
*/
@Aspect
@Component
public class PortalLoggingAspect  {
	
	private static final EELFLoggerDelegate log = EELFLoggerDelegate.getLogger(PortalLoggingAspect.class);
		
	@Pointcut("within(@org.springframework.stereotype.Controller *)")
	   public void controller() {
	}
	
	@Pointcut("execution(public * org.acumos.portal.be.controller.AuthServiceController.jwtLogin(..))")
	   protected void loggingSignOnOperation() {
	}	
	
	@Before("controller() &&  loggingSignOnOperation()")
	public void logBefore(JoinPoint joinPoint) throws Throwable {

		for (Object signatureArg : joinPoint.getArgs()) {
			if (signatureArg instanceof org.acumos.portal.be.common.JsonRequest) {
				@SuppressWarnings("unchecked")
				org.acumos.portal.be.common.JsonRequest<User> user = (org.acumos.portal.be.common.JsonRequest<User>) signatureArg;
				String username = user.getBody().getUsername();
				MDC.put("user", username);
				MDC.put("contextName", "Acumos");
				log.info(EELFLoggerDelegate.securityLogger, "User Logging in");
				MDC.remove("user");
				MDC.remove("contextName");				
			}
		}		
	}
		
	@AfterReturning(pointcut = "controller() &&  loggingSignOnOperation()", returning = "result")
	public void logAfter(JoinPoint joinPoint , Object result) {

		for (Object signatureArg : joinPoint.getArgs()) {
			if (signatureArg instanceof org.acumos.portal.be.common.JsonRequest) {
				@SuppressWarnings("unchecked")
				org.acumos.portal.be.common.JsonRequest<User> user = (org.acumos.portal.be.common.JsonRequest<User>) signatureArg;
				String username = user.getBody().getUsername();
				MDC.put("user", username);
				MDC.put("contextName", "Acumos");												
			}
		}		
		if(result instanceof org.acumos.portal.be.transport.ResponseVO) {
			log.info(EELFLoggerDelegate.securityLogger, ((org.acumos.portal.be.transport.ResponseVO)result).getMessage());
		} else if(result instanceof org.acumos.portal.be.transport.AbstractResponseObject){
			log.info(EELFLoggerDelegate.securityLogger, "User Logged Successfully");
		}		
		
		MDC.remove("user");
		MDC.remove("contextName");
	}
	
}
