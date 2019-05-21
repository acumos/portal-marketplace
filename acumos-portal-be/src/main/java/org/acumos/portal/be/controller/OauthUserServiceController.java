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
package org.acumos.portal.be.controller;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acumos.cds.domain.MLPRole;
import org.acumos.cds.domain.MLPUser;
import org.acumos.portal.be.APINames;
import org.acumos.portal.be.common.exception.UserServiceException;
import org.acumos.portal.be.security.jwt.JwtTokenUtil;
import org.acumos.portal.be.service.OauthUserService;
import org.acumos.portal.be.service.UserRoleService;
import org.acumos.portal.be.service.UserService;
import org.acumos.portal.be.transport.AbstractResponseObject;
import org.acumos.portal.be.transport.MLRole;
import org.acumos.portal.be.transport.OauthUser;
import org.acumos.portal.be.transport.ResponseVO;
import org.acumos.portal.be.transport.User;
import org.acumos.portal.be.transport.UserMasterObject;
import org.acumos.portal.be.util.PortalUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.ApiOperation;

@Controller
@RequestMapping(APINames.OAUTH_LOGIN)
public class OauthUserServiceController extends AbstractController {
	
	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());	

	@Autowired
	private OauthUserService oauthUserService;
	@Autowired
	private UserService userService;
	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	@Autowired
	private UserRoleService userRoleService;

	public OauthUserServiceController() {
		// TODO Auto-generated constructor stub
	}

	
	/**
	 * @param request
	 * 			HttpServletRequest
	 * @param userMasterObject
	 * 			User to be created on the Platform
	 * @param response
	 * 			artifactService
	 * @return
	 * 			Returns Status Code and Message serialized as JSON
	 */
	@ApiOperation(value = "Creates a User Account on the Platform.  Returns Success if Account created successfully; else an error message is returned.", response = ResponseVO.class)
	@RequestMapping(value = {APINames.ACCOUNT_SIGNUP}, method = RequestMethod.POST, produces = APPLICATION_JSON)
	@ResponseBody
	public Object createUser(HttpServletRequest request, @RequestBody UserMasterObject userMasterObject, HttpServletResponse response) {
		log.debug("createUser={}", userMasterObject);
		Object responseVO = null;
		try {
			if(userMasterObject == null) {
				log.debug("createUser: Invalid Parameters");
				responseVO = new ResponseVO(HttpServletResponse.SC_BAD_REQUEST, "Login Failed");
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			}
			boolean isUserExists = false;
			
			if((userService.findUserByEmail(userMasterObject.getEmailId()))!=null && (userService.findUserByUsername(userMasterObject.getUsername())!=null)){
				
					isUserExists = true;
			}
			//1. Verify that account with UserName and EmailId does not exists 
			if(!isUserExists) { 
				
				User user=PortalUtils.convertUserMasterIntoMLPUser(userMasterObject);
				user=userService.save(user);
				
				// Add default role for user
				Boolean defaultRoleCreated = false;
				String defaultRoleId=null;
				List<MLRole> userRoles = userRoleService.getAllRoles();
				for(MLRole role : userRoles)
				{
					if(role.getName().equals("MLP System User")){
						defaultRoleCreated=true;
						defaultRoleId=role.getRoleId();
					}
				}
				//If default role is not created, then create
				if(!defaultRoleCreated){
				MLRole role = new MLRole();
				role.setName("MLP System User");
				MLPRole mlpRole = userRoleService.createRole(role);
				defaultRoleId=mlpRole.getRoleId();
				}
				//Assign default role to user
				if (user.getUserId() != null && defaultRoleId != null) {
					userRoleService.addUserRole(user.getUserId(), defaultRoleId);
				}
				
				/*
				 *  If the user does not exist in c_USER, add it in c_USER and also add it in c_USER_LOGIN_PROVIDER
				 */
				userMasterObject.setUserId(user.getUserId()); // Set the Forign key here
				OauthUser oauthUser=PortalUtils.convertUserMasterIntoOauthUser(userMasterObject);
				oauthUserService.save(oauthUser);
				
				responseVO = new ResponseVO(HttpServletResponse.SC_CREATED, "Success");
				response.setStatus(HttpServletResponse.SC_CREATED);
			} else {
				responseVO = new ResponseVO(new Integer(100),"Reset_Content");
				response.setStatus(HttpServletResponse.SC_CREATED);
			}
			
		} 
		catch (UserServiceException e) {
			responseVO = new ResponseVO(HttpServletResponse.SC_BAD_REQUEST, "Failed");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			log.error("Exception Occurred while createUser()", e);
		}
		catch (Exception e) {
			responseVO = new ResponseVO(HttpServletResponse.SC_BAD_REQUEST, "Failed");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			log.error("Exception Occurred while createUser()", e);
		}
		return responseVO;
	}
	
	@ApiOperation(value = "Allows User to login to the Platform using emailId or username.  Returns Success & JWT Token if Account created successfully; else an error message is returned.", response = User.class)
	@RequestMapping(value = {}, method = RequestMethod.POST, produces = APPLICATION_JSON)
	@ResponseBody
	public AbstractResponseObject login(HttpServletRequest request, @RequestBody User user, HttpServletResponse response) {
		log.debug("login={}", user);
		AbstractResponseObject responseObject = null;
		List<MLPRole> userAssignedRolesList = new ArrayList<>();
		User validUser = null;
		String jwtToken = null;
		//Check if the UserName or emailId is null or not.
		if(PortalUtils.isEmptyOrNullString(user.getEmailId())) {
			log.debug("Invalid Parameters");
			responseObject = new ResponseVO(HttpServletResponse.SC_BAD_REQUEST, "Login Failed");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		} else {
			try {
				boolean isValid = false;
				MLPUser mlpUser = null;
				if(!PortalUtils.isEmptyOrNullString(user.getEmailId())) {
					mlpUser = userService.findUserByEmail(user.getEmailId());
					isValid = true;
				}
				
				if(isValid) {
					userAssignedRolesList = userService.getUserRole(mlpUser.getUserId());
					//TODO Check Account Status and respond with appropriate error if Account is inactive
					validUser = PortalUtils.convertToMLPuser(mlpUser);
					responseObject = new User(validUser);
					response.setStatus(HttpServletResponse.SC_OK);
					if (validUser.getJwttoken() == null) {
						// Generate token
						jwtToken = jwtTokenUtil.generateToken(mlpUser, null);
						responseObject.setJwtToken(jwtToken);
						mlpUser.setAuthToken(jwtToken);
						try {
							if (mlpUser != null) {
								validUser.setJwttoken(jwtToken);
								validUser.setActive("Y");
								userService.updateUser(validUser);
							}
						} catch (Exception e) {
							
						}

					} else {
						responseObject.setJwtToken(validUser.getJwttoken());
						if (jwtTokenUtil.isTokenExpired(validUser.getJwttoken())) {
							mlpUser.setAuthToken(null);
							validUser.setJwttoken(jwtToken);
							userService.updateUser(validUser);

							jwtToken = jwtTokenUtil.generateToken(mlpUser, null);
							responseObject.setJwtToken(jwtToken);
							mlpUser.setAuthToken(jwtToken);
							try {
								if (mlpUser != null) {
									validUser.setActive("Y");
									validUser.setJwttoken(jwtToken);
									userService.updateUser(validUser);
								}
							} catch (Exception e) {
								
							}
						} else {
							jwtToken = validUser.getJwttoken();
							if (jwtTokenUtil.validateToken(jwtToken, mlpUser)) {
								mlpUser.setAuthToken(null);
								validUser.setJwttoken(jwtToken);
								userService.updateUser(validUser);

								jwtToken = jwtTokenUtil.generateToken(mlpUser, null);
								responseObject.setJwtToken(jwtToken);
								mlpUser.setAuthToken(jwtToken);
								try {
									if (mlpUser != null) {
										validUser.setActive("Y");
										validUser.setJwttoken(jwtToken);
										userService.updateUser(validUser);
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
							} else {
								responseObject = new ResponseVO(HttpServletResponse.SC_UNAUTHORIZED, "Token Validation Failed");
								response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
							}
						}
					}
				} else {
					responseObject = new ResponseVO(HttpServletResponse.SC_UNAUTHORIZED, "Login Failed");
					response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				}
			} catch (Exception e) {
				responseObject = new ResponseVO(HttpServletResponse.SC_UNAUTHORIZED, "Login Failed");
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				log.error("Exception Occurred while login()", e);
			}
		}
		responseObject.setUserAssignedRolesList(userAssignedRolesList);
		return responseObject;
	}
	
	@ApiOperation(value = "Fetches username from authorization header.", response = String.class)
	@RequestMapping(value = APINames.USERNAME, method = RequestMethod.GET, produces = APPLICATION_JSON)
	@ResponseBody
	public void getUsernameFromAuth(HttpServletRequest request, HttpServletResponse response) {
		log.debug("getUsernameFromAuth");
		try {
			String auth = request.getHeader("Authorization");
			if (!PortalUtils.isEmptyOrNullString(auth)) {
				String token = auth.replace("Bearer ", "");
				String username = jwtTokenUtil.getUsernameFromToken(token);
				response.addHeader("authuser", username);
				response.setStatus(HttpServletResponse.SC_OK);
			} else {
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			}
		} catch (Exception e) {
			log.error("Exception occurred during getUsernameFromAuth", e);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}
	
/*	
	@ApiOperation(value = "Get Github access Token")
	@RequestMapping(value = {}, method = RequestMethod.GET, produces = APPLICATION_JSON)
	@ResponseBody
	public AbstractResponseObject getGitHubAccessToken(HttpServletRequest request, HttpServletResponse response) {
		log.debug("getGitHubAccessToken={}", request.getHeader("code"));
		AbstractResponseObject responseObject = null;
		
		String code = request.getHeader("code");
		//Check if the UserName or emailId is null or not.
		if(PortalUtils.isEmptyOrNullString(code)) {
			log.debug("Invalid Parameters");
			responseObject = new ResponseVO(HttpServletResponse.SC_BAD_REQUEST, "Login Failed");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		} else {
			try {
				
					//TODO Check Account Status and respond with appropriate error if Account is inactive
				String token = oauthUserService.getGitHubToken(code);
					responseObject = new User(validUser);
					response.setStatus(HttpServletResponse.SC_OK);
				
			} catch (Exception e) {
				responseObject = new ResponseVO(HttpServletResponse.SC_UNAUTHORIZED, "Login Failed");
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				log.error("Exception Occurred while login()", e);
			}
		}
		
		return responseObject;
	}*/
	
}
