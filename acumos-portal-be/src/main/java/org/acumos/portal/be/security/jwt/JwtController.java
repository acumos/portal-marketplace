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
package org.acumos.portal.be.security.jwt;

import java.lang.invoke.MethodHandles;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acumos.portal.be.APINames;
import org.acumos.portal.be.common.JSONTags;
import org.acumos.portal.be.common.JsonRequest;
import org.acumos.portal.be.common.JsonResponse;
import org.acumos.portal.be.common.exception.MalformedException;
import org.acumos.portal.be.controller.AbstractController;
import org.acumos.portal.be.controller.AuthServiceController;
import org.acumos.portal.be.security.jwt.JwtTokenUtil;
import org.acumos.portal.be.service.UserService;
import org.acumos.portal.be.transport.AbstractResponseObject;
import org.acumos.portal.be.transport.MLSolution;
import org.acumos.portal.be.transport.ResponseVO;
import org.acumos.portal.be.transport.User;
import org.acumos.portal.be.util.PortalUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
/*import org.springframework.mobile.device.Device;*/
import org.acumos.cds.domain.MLPUser;

import io.swagger.annotations.ApiOperation;

/**
 * 
 *
 */

@Controller
@RequestMapping("/" + APINames.AUTH)
public class JwtController extends AbstractController {
	
	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());	

	
	@Autowired
	private UserService userService;
	
	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	public JwtController() {
		// TODO Auto-generated constructor stub
	}

	
	/**
	 * @param request
	 * 			HttpServletRequest
	 * @param user
	 * 			User's request to login on the Platform
	 * @param response
	 * 			HttpServletResponse
	 * @return
	 * 			Returns JWT if User is Authenticated else resturns Failure with status code and error message
	 */
	
}
