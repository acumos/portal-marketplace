/*-
 * ===============LICENSE_START=======================================================
 * Acumos
 * ===================================================================================
 * Copyright (C) 2020 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
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
package org.acumos.portal.be.controller;

import java.lang.invoke.MethodHandles;

import javax.servlet.http.HttpServletResponse;

import org.acumos.portal.be.APINames;
import org.acumos.portal.be.common.JsonResponse;
import org.acumos.portal.be.util.SanitizeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.ApiOperation;

@Controller
@RequestMapping("/generic")
public class GenericController extends AbstractController{

	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());	
	
	@Autowired
    private Environment env;
	
    @ApiOperation(value = "Get Application Property", response = JsonResponse.class)
    @RequestMapping(value = {APINames.GET_PROPERTY}, method = RequestMethod.GET, produces = APPLICATION_JSON)
    @ResponseBody
	public JsonResponse<String> getApplicationProperty(@RequestParam(value = "propertyName", required = true) String propertyName) {
    	String property = SanitizeUtils.sanitize(propertyName);
        log.info("Application property name::", property);
		String propertyValue = env.getProperty(property);
        log.info("Application property value::", propertyValue);
		JsonResponse<String> responseVO = new JsonResponse<String>();
		if(propertyValue != null) {
			responseVO.setResponseBody(propertyValue);
		} else {
			responseVO.setResponseBody("");
		}
		responseVO.setStatus(true);
		responseVO.setStatusCode(HttpServletResponse.SC_OK);
		return responseVO;
	}

}
