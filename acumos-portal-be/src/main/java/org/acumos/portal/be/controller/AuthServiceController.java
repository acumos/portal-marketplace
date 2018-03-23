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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;

import org.acumos.cds.domain.MLPRole;
import org.acumos.cds.domain.MLPUser;
import org.acumos.portal.be.APINames;
import org.acumos.portal.be.common.JSONTags;
import org.acumos.portal.be.common.JsonRequest;
import org.acumos.portal.be.common.JsonResponse;
import org.acumos.portal.be.common.exception.MalformedException;
import org.acumos.portal.be.security.jwt.JwtTokenUtil;
import org.acumos.portal.be.service.UserService;
import org.acumos.portal.be.transport.AbstractResponseObject;
import org.acumos.portal.be.transport.ResponseVO;
import org.acumos.portal.be.transport.User;
import org.acumos.portal.be.util.EELFLoggerDelegate;
import org.acumos.portal.be.util.PortalUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.annotations.ApiOperation;

@Controller
@RequestMapping("/" + APINames.AUTH)
public class AuthServiceController extends AbstractController {

	private static final EELFLoggerDelegate log = EELFLoggerDelegate.getLogger(AuthServiceController.class);

	@Autowired
	private UserService userService;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	Environment env;

	final String GOOGLE_USER_INFO_URL = "https://www.googleapis.com/oauth2/v3/userinfo";

	public AuthServiceController() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param request
	 *            HttpServletRequest
	 * @param user
	 *            User's request to login on the Platform
	 * @param response
	 *            HttpServletResponse
	 * @return Returns JWT if User is Authenticated else resturns Failure with
	 *         status code and error message
	 */
	@ApiOperation(value = "Allows User to login to the Platform using emailId or username.  Returns Success & JWT Token if Account created successfully; else an error message is returned.", response = User.class)
	@RequestMapping(value = { APINames.LOGIN }, method = RequestMethod.POST, produces = APPLICATION_JSON)
	@ResponseBody
	public AbstractResponseObject login(HttpServletRequest request, @RequestBody JsonRequest<User> user,
			HttpServletResponse response) {
		log.debug(EELFLoggerDelegate.debugLogger, "login={}", user);
		AbstractResponseObject responseObject = null;
		User validUser = null;
		boolean isValid = false;
		boolean active = false;
		// Check if the UserName or emailId is null or not.
		if (PortalUtils.isEmptyOrNullString(user.getBody().getEmailId())
				&& PortalUtils.isEmptyOrNullString(user.getBody().getUsername())) {
			log.debug(EELFLoggerDelegate.errorLogger, "Invalid Parameters");
			responseObject = new ResponseVO(HttpServletResponse.SC_BAD_REQUEST, "Login Failed");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		} else {
			try {

				MLPUser mlpUser = null;
				if (!PortalUtils.isEmptyOrNullString(user.getBody().getUsername())) {
					mlpUser = userService.login(user.getBody().getUsername(), user.getBody().getPassword());
					if (mlpUser.isActive()) {
						active = true;
						isValid = true;
					} else if (!mlpUser.isActive()) {
						active = false;
						isValid = false;
					}
				} else if (!PortalUtils.isEmptyOrNullString(user.getBody().getEmailId())) {
					mlpUser = userService.findUserByEmail(user.getBody().getEmailId());
				}

				if (isValid) {
					// TODO Check Account Status and respond with appropriate error if Account is
					// inactive
					validUser = PortalUtils.convertToMLPuser(mlpUser);
					responseObject = new User(validUser);
					// check password expire date
					Date todaysDate = new Date();
					responseObject.setLoginPassExpire(false);
					if (mlpUser.getLoginPassExpire() != null) {
						if (mlpUser.getLoginPassExpire().compareTo(todaysDate) <= 0) {
							responseObject.setLoginPassExpire(true);
						}
					}
					response.setStatus(HttpServletResponse.SC_OK);
				} else if (!active) {
					responseObject = new ResponseVO(HttpServletResponse.SC_UNAUTHORIZED, "Login Inactive");
					response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				} else if (!isValid) {
					responseObject = new ResponseVO(HttpServletResponse.SC_UNAUTHORIZED, "Login Failed");
					response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				}
			} catch (Exception e) {
				responseObject = new ResponseVO(HttpServletResponse.SC_UNAUTHORIZED, "Login Failed");
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred while login()", e);
			}
		}

		return responseObject;
	}

	/**
	 * @param request
	 *            HttpServletRequest
	 * @param user
	 *            User who is logging out
	 * @param response
	 *            HttpServletResponse
	 * @return Returns Status Code and Message serialized as JSON
	 */
	@ApiOperation(value = "Allows Users to logout to the Platform .  Returns Success & JWT Token if Account created successfully; else an error message is returned.", response = ResponseVO.class)
	@RequestMapping(value = { APINames.LOGOUT }, method = RequestMethod.POST, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<Object> logout(HttpServletRequest request, @RequestBody JsonRequest<User> user,
			HttpServletResponse response) {
		log.debug(EELFLoggerDelegate.debugLogger, "logout={}", user.getBody());
		JsonResponse<Object> responseObject = null;
		// TODO Need to add code to invalidate JWT Token

		return responseObject;
	}

	@ApiOperation(value = "Allows User to login to the Platform using emailId or username.  Returns Success & JWT Token if Account created successfully; else an error message is returned.", response = User.class)
	@RequestMapping(value = { APINames.JWTTOKEN }, method = RequestMethod.POST, produces = APPLICATION_JSON)
	@ResponseBody
	public AbstractResponseObject jwtLogin(HttpServletRequest request, @RequestBody JsonRequest<User> user,
			HttpServletResponse response , @RequestHeader(value="provider", required=false) String provider) {
		log.debug(EELFLoggerDelegate.debugLogger, "login={}", user);
		AbstractResponseObject responseObject = new AbstractResponseObject();
		User userObj = null;
		String jwtToken = null;
		boolean isValid = false;
		MLPUser mlpUser = null;
		List<MLPRole> userAssignedRolesList = new ArrayList<>();
		// Check if the UserName or emailId is null or not.
		if (PortalUtils.isEmptyOrNullString(user.getBody().getUsername()) && PortalUtils.isEmptyOrNullString(user.getBody().getEmailId())) {
			log.debug(EELFLoggerDelegate.errorLogger, "Invalid Parameters");
			responseObject = new ResponseVO(HttpServletResponse.SC_BAD_REQUEST, "Login Failed");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		} else {
			try {
				
				if (!PortalUtils.isEmptyOrNullString(user.getBody().getUsername()) && PortalUtils.isEmptyOrNullString(provider)) {
					try {
						mlpUser = userService.login(user.getBody().getUsername(), user.getBody().getPassword());
						mlpUser.setLastLogin(new Date(System.currentTimeMillis()));
						userAssignedRolesList = userService.getUserRole(mlpUser.getUserId());
						isValid = true;
					} catch (Exception e) {

						responseObject = new ResponseVO(HttpServletResponse.SC_BAD_GATEWAY,
								"Login  Failed. Invalid Password.");
						response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
						log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred while login()", e);
					}
				} else 
					if (!PortalUtils.isEmptyOrNullString(user.getBody().getEmailId()) && !PortalUtils.isEmptyOrNullString(provider) && "LFCAS".equals(provider)) {
						 mlpUser = userService.findUserByEmail(user.getBody().getEmailId()); 
						 mlpUser.setLastLogin(new Date(System.currentTimeMillis()));
						 userAssignedRolesList = userService.getUserRole(mlpUser.getUserId());
						 isValid = true; 
					}

					if (!mlpUser.isActive()) {
						isValid = false;
						responseObject = new ResponseVO(HttpServletResponse.SC_PRECONDITION_FAILED, "Inactive user");
						response.setStatus(HttpServletResponse.SC_PRECONDITION_FAILED);
						return responseObject;
					}
				

					if (isValid) {
					// convert to user
						userObj = PortalUtils.convertToMLPuser(mlpUser);
						// responseObject = new User(userObj);
						// check password expire date
						Date todaysDate = new Date();
						responseObject.setLoginPassExpire(false);
						if (mlpUser.getLoginPassExpire() != null) {
							if (mlpUser.getLoginPassExpire().compareTo(todaysDate) <= 0) {
								responseObject.setLoginPassExpire(true);

						}
					}
					response.setStatus(HttpServletResponse.SC_OK);

					// JWT token authentication
					// JwtTokenUtil jwtTokenUtil = new JwtTokenUtil();

					if (userObj.getJwttoken() == null) {
						// Generate token
						jwtToken = jwtTokenUtil.generateToken(mlpUser, null);
						responseObject.setJwtToken(jwtToken);
						mlpUser.setAuthToken(jwtToken);
						try {
							if (mlpUser != null) {
								userObj.setJwttoken(jwtToken);
								userObj.setActive("Y");
								userService.updateUser(userObj);
							}
						} catch (Exception e) {

						}
						userAssignedRolesList = userService.getUserRole(mlpUser.getUserId());
					} else {
						responseObject.setJwtToken(userObj.getJwttoken());
						if (jwtTokenUtil.isTokenExpired(userObj.getJwttoken())) {
							mlpUser.setAuthToken(null);
							userObj.setJwttoken(jwtToken);
							userService.updateUser(userObj);

							jwtToken = jwtTokenUtil.generateToken(mlpUser, null);
							responseObject.setJwtToken(jwtToken);
							mlpUser.setAuthToken(jwtToken);
							try {
								if (mlpUser != null) {
									userObj.setActive("Y");
									userObj.setJwttoken(jwtToken);
									userService.updateUser(userObj);
									userAssignedRolesList = userService.getUserRole(mlpUser.getUserId());
								}
							} catch (Exception e) {

							}
						} else {
							jwtToken = userObj.getJwttoken();
							if (jwtTokenUtil.validateToken(jwtToken, mlpUser)) {
								mlpUser.setAuthToken(null);
								userObj.setJwttoken(jwtToken);
								userService.updateUser(userObj);

								jwtToken = jwtTokenUtil.generateToken(mlpUser, null);
								responseObject.setJwtToken(jwtToken);
								mlpUser.setAuthToken(jwtToken);
								try {
									if (mlpUser != null) {
										userObj.setActive("Y");
										userObj.setJwttoken(jwtToken);
										userService.updateUser(userObj);
										userAssignedRolesList = userService.getUserRole(mlpUser.getUserId());
									}
								} catch (Exception e) {
								}
							} else {
								responseObject = new ResponseVO(HttpServletResponse.SC_UNAUTHORIZED,
										"Token Validation Failed");
								response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
							}
						}
					}
				} else {
					responseObject = new ResponseVO(HttpServletResponse.SC_UNAUTHORIZED, "Login Failed");
					response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				}
			} catch (Exception e) {

				if (response.getStatus() == HttpServletResponse.SC_UNAUTHORIZED) {
					responseObject = new ResponseVO(HttpServletResponse.SC_UNAUTHORIZED, "Login Failed");
					response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
					log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred while login()", e);
				}
			}
		}

		responseObject.setUserAssignedRolesList(userAssignedRolesList);
		return responseObject;
	}

	@ApiOperation(value = "Validate the jwt Token for third party access", response = JsonResponse.class)
	@RequestMapping(value = { APINames.JWTTOKENVALIDATION }, method = RequestMethod.POST, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<Object> validateToken(HttpServletRequest request, HttpServletResponse response,
			@RequestBody JsonRequest<User> userObj, @RequestHeader(value="provider", required=false) String provider) throws MalformedException {

		log.debug(EELFLoggerDelegate.debugLogger, "Validate the jwt Token for third party access={}");

		JsonResponse<Object> responseVO = new JsonResponse<Object>();

		if (PortalUtils.isEmptyOrNullString(userObj.getBody().getJwtToken())) {
			responseVO.setErrorCode(JSONTags.TAG_ERROR_CODE);
			responseVO.setStatusCode(HttpServletResponse.SC_UNAUTHORIZED);
			responseVO.setResponseDetail("Token Validation Failed");
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return responseVO;
		}

		// String provider = httpRequest.getHeader("provider");
		String jwtToken = userObj.getBody().getJwtToken();

		if (!StringUtils.isEmpty(provider) && provider.equals("google")) {
			try {
				CloseableHttpClient httpclient = HttpClients.createDefault();
				HttpGet getProfile = new HttpGet(GOOGLE_USER_INFO_URL);

				String proxyHost = env.getProperty("proxy.host");
				String proxyPortString = env.getProperty("proxy.port");
				String proxyProtocol = env.getProperty("proxy.protocol");
				if (!StringUtils.isEmpty(proxyHost) && proxyPortString != null && !StringUtils.isEmpty(proxyProtocol)) {
					Integer proxyPort = Integer.parseInt(proxyPortString);
					HttpHost proxy = new HttpHost(proxyHost, proxyPort, proxyProtocol);

					RequestConfig config = RequestConfig.custom().setProxy(proxy).build();
					getProfile.setConfig(config);
				}

				getProfile.setHeader(HttpHeaders.AUTHORIZATION, jwtToken);
				CloseableHttpResponse resp = null;

				resp = httpclient.execute(getProfile);

				HttpEntity respEntity = resp.getEntity();
				String result = PortalUtils.convertStreamToString(respEntity.getContent());

				ObjectMapper mapper = new ObjectMapper();
				@SuppressWarnings("unchecked")
				Map<String, Object> profile = mapper.readValue(result, Map.class);
				String email = (String) profile.get("email");
				MLPUser mlpUser = userService.findUserByEmail(email);
				if (mlpUser != null) {
					responseVO.setStatus(true);
					responseVO.setResponseDetail("Valid Token");
					responseVO.setResponseBody(mlpUser.getUserId());
					responseVO.setStatusCode(HttpServletResponse.SC_OK);
				}
			} catch (IOException e) {
				responseVO.setErrorCode(JSONTags.TAG_ERROR_CODE);
				responseVO.setStatusCode(HttpServletResponse.SC_UNAUTHORIZED);
				responseVO.setResponseDetail("Token Validation Failed");
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				e.printStackTrace();
			}
		} else {
			MLPUser user = null;
			jwtToken = jwtToken.replace("Bearer ", "");
			String userName = jwtTokenUtil.getUsernameFromToken(jwtToken);

			if (userName == null) {
				responseVO.setErrorCode(JSONTags.TAG_ERROR_CODE);
				responseVO.setStatusCode(HttpServletResponse.SC_UNAUTHORIZED);
				responseVO.setResponseDetail("Token Validation Failed");
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				return responseVO;
			}

			Map<String, String> queryParams = new HashMap<>();
			queryParams.put("loginName", userName);
			user = userService.findUserByUsername(userName);

			if (user == null) {
				responseVO.setErrorCode(JSONTags.TAG_ERROR_CODE);
				responseVO.setStatusCode(HttpServletResponse.SC_UNAUTHORIZED);
				responseVO.setResponseDetail("Token Validation Failed");
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				return responseVO;
			}

			if (jwtTokenUtil.validateToken(jwtToken, user)) {
				responseVO.setStatus(true);
				responseVO.setResponseDetail("Valid Token");
				responseVO.setResponseBody(user.getUserId());
				responseVO.setStatusCode(HttpServletResponse.SC_OK);
			} else {
				responseVO.setErrorCode(JSONTags.TAG_ERROR_CODE);
				responseVO.setStatusCode(HttpServletResponse.SC_UNAUTHORIZED);
				responseVO.setResponseDetail("Validation Failed");
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			}
		}
		return responseVO;
	}
}
