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

import java.lang.invoke.MethodHandles;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acumos.portal.be.logging.ONAPLogAdapter;
import org.acumos.portal.be.logging.ONAPLogConstants;
import org.acumos.portal.be.security.jwt.JwtTokenUtil;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
* Adds request details to the mapped diagnostic context (MDC) so they can be
* logged. <BR>
* http://www.devgrok.com/2017/04/adding-mdc-headers-to-every-spring-mvc.html
*/
@Component
public class LoggingHandlerInterceptor  extends HandlerInterceptorAdapter {	
	
	@Autowired
	private JwtTokenUtil tokenUtils;
	
	/**
	 * Invokes LogAdapter. Unfortunately ONAP use different conventions for key naming.
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {	
		
		 this.setUserName(request);		     	
		 ONAPLogAdapter logAdapter = new ONAPLogAdapter(LoggerFactory.getLogger(MethodHandles.lookup().lookupClass()));
	 	 logAdapter.entering(request);	 	 
		 return true;		
	}	
	
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		
		final ONAPLogAdapter adapter = new ONAPLogAdapter(LoggerFactory.getLogger((MethodHandles.lookup().lookupClass())));
        adapter.exiting();	
	}
	
	
	/**
	 * Set the UserName in Request
	 * @param request
	 *            incoming HTTP request.	 
	 */
	private void setUserName(HttpServletRequest request) {
		String authToken = request.getHeader("jwtToken");
		 if(authToken == null )
		    authToken = request.getHeader("Authorization");
		 if(authToken == null)
		    authToken = request.getParameter("jwtToken");
		 String userName = null;
		 if(authToken !=null) {
			 String authTokenClean = authToken.replace("Bearer ", "");
		     userName = this.tokenUtils.getUsernameFromToken(authTokenClean);
		     request.setAttribute(ONAPLogConstants.MDCs.USER,userName);		    
		 }
	}
}