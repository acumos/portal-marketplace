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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acumos.cds.domain.MLPRole;
import org.acumos.cds.domain.MLPUser;
import org.acumos.portal.be.APINames;
import org.acumos.portal.be.common.JSONTags;
import org.acumos.portal.be.common.JsonRequest;
import org.acumos.portal.be.common.JsonResponse;
import org.acumos.portal.be.common.exception.MalformedException;
import org.acumos.portal.be.common.exception.UserServiceException;
import org.acumos.portal.be.security.jwt.JwtTokenUtil;
import org.acumos.portal.be.security.jwt.TokenValidation;
import org.acumos.portal.be.service.UserRoleService;
import org.acumos.portal.be.service.UserService;
import org.acumos.portal.be.transport.MLRole;
import org.acumos.portal.be.transport.PasswordDTO;
import org.acumos.portal.be.transport.ResponseVO;
import org.acumos.portal.be.transport.User;
import org.acumos.portal.be.util.EELFLoggerDelegate;
import org.acumos.portal.be.util.PortalUtils;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.annotations.ApiOperation;

@Controller
@RequestMapping(APINames.USERS)
public class UserServiceController extends AbstractController {
	private static final EELFLoggerDelegate log = EELFLoggerDelegate.getLogger(UserServiceController.class);

	@Autowired
	private UserService userService;
	
	@Autowired
	private UserRoleService userRoleService;
	
	@Autowired
	JwtTokenUtil jwtTokenUtil;
	
	@Autowired
    private Environment env;

	public UserServiceController() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param request
	 *            HttpServletRequest
	 * @param user
	 *            User to be created on the Platform 
	 * @param response
	 *            artifactService
	 * @return Returns Status Code and Message serialized as JSON
	 * @throws UserServiceException on failure to create user account
	 */
	@ApiOperation(value = "Creates a User Account on the Platform.  Returns Success if Account created successfully; else an error message is returned.", response = ResponseVO.class)
	@RequestMapping(value = { APINames.ACCOUNT_SIGNUP }, method = RequestMethod.POST, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<Object> createUser(HttpServletRequest request, @RequestBody JsonRequest<User> user,
			HttpServletResponse response) throws UserServiceException {
		log.debug(EELFLoggerDelegate.debugLogger, "createUser={}", user.getBody());
		JsonResponse<Object> data = new JsonResponse<>();
		try {
			if (user.getBody() == null) {
				log.debug(EELFLoggerDelegate.errorLogger, "createUser: Invalid Parameters");
				data.setErrorCode(JSONTags.TAG_ERROR_CODE);
				data.setResponseDetail("Login Failed");
			}
			boolean isUserExists = false;
			
				MLPUser mlpUser = userService.findUserByEmail(user.getBody().getEmailId());
				if (mlpUser != null) {
					isUserExists = true;
					data.setErrorCode(JSONTags.TAG_ERROR_CODE_RESET_EMAILID);
					data.setResponseDetail("Reset_EmailId");
				}
				if (mlpUser == null) {
					mlpUser = userService.findUserByUsername(user.getBody().getUsername());
					if (mlpUser != null) {
						isUserExists = true;
						data.setErrorCode(JSONTags.TAG_ERROR_CODE_RESET_USERNAME);
						data.setResponseDetail("Reset_UserName");
					}
				}
				
			
			// 1. Verify that account with UserName and EmailId does not exists
			if (!isUserExists) {
				User userObj = userService.save(user.getBody());
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
				if (userObj.getUserId() != null && defaultRoleId != null) {
					userRoleService.addUserRole(userObj.getUserId(), defaultRoleId);
				}
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
				data.setResponseDetail("Success");
			} 

		}
		catch (Exception e) {
			data.setErrorCode(JSONTags.TAG_ERROR_CODE);
			data.setResponseDetail("Failed");
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred while createUser()", e);
		}
		return data;
	}

	/*
	@ApiOperation(value = "Updates a user Account Password.  Returns successful response after updating the password.", response = JsonResponse.class)
	@RequestMapping(value = { APINames.CHANGE_PASSWORD }, method = RequestMethod.PUT, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<Object> changeUserPassword(HttpServletRequest request,
			@RequestBody JsonRequest<PasswordDTO> passwordDTO, HttpServletResponse response) {
		log.debug(EELFLoggerDelegate.debugLogger, "changeUserPassword={}");
		// Object responseVO = null;
		JsonResponse<Object> responseVO = new JsonResponse<>();
		try {
			if ((passwordDTO == null)
					|| (passwordDTO != null && (PortalUtils.isEmptyOrNullString(passwordDTO.getBody().getNewPassword())
							|| (PortalUtils.isEmptyOrNullString(passwordDTO.getBody().getOldPassword()))))) {
				log.error(EELFLoggerDelegate.errorLogger, "Bad request: NewPassword or OldPassword is empty");
			}
			// TODO As of now it does not check if User Account already exists.
			// Need to first check if the account exists in DB
			userService.changeUserPassword(passwordDTO.getBody().getUserId(), passwordDTO.getBody().getOldPassword(),
					passwordDTO.getBody().getNewPassword());
			responseVO.setStatus(true);
			responseVO.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
			responseVO.setResponseDetail("Success");
			response.setStatus(HttpServletResponse.SC_OK);
		} catch (Exception e) {
			responseVO.setErrorCode(JSONTags.TAG_ERROR_CODE);
			responseVO.setStatus(false);
			responseVO.setResponseDetail("Failed");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred while changeUserPassword()", e);
		}
		return responseVO;
	}
	
	@ApiOperation(value = "Updates a user Account Password.  Returns successful response after updating the password.", response = JsonResponse.class)
	@RequestMapping(value = {APINames.CHANGE_PASSWORD}, method = RequestMethod.PUT, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse changeUserPassword(HttpServletRequest request, @RequestBody PasswordDTO passwordDTO, HttpServletResponse response) {
		log.debug(EELFLoggerDelegate.debugLogger, "changeUserPassword={}");
		//Object responseVO = null;
		JsonResponse responseVO = new JsonResponse<>();
		try {
			if((passwordDTO == null) || (passwordDTO != null && (PortalUtils.isEmptyOrNullString(passwordDTO.getNewPassword()) || 
					(PortalUtils.isEmptyOrNullString(passwordDTO.getOldPassword()))))) {
				log.error(EELFLoggerDelegate.errorLogger, "Bad request: NewPassword or OldPassword is empty");
			}
			//TODO As of now it does not check if User Account already exists. Need to first check if the account exists in DB
			userService.changeUserPassword(passwordDTO.getUserId(), passwordDTO.getOldPassword(), passwordDTO.getNewPassword());
			responseVO.setStatus(true);
			responseVO.setResponseDetail("Success");
			response.setStatus(HttpServletResponse.SC_OK);
		} catch (Exception e) {
			responseVO.setStatus(false);
			responseVO.setResponseDetail("Failed");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred while changeUserPassword()", e);
		}
		return responseVO;
	}*/
	
	@ApiOperation(value = "Update a user details.  Returns successful response after updating the user details.", response = JsonResponse.class)
	@RequestMapping(value = {APINames.UPADATE_USER}, method = RequestMethod.PUT, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<Object> updateUser(HttpServletRequest request, @RequestBody JsonRequest<User> user, HttpServletResponse response) {
		log.debug(EELFLoggerDelegate.debugLogger, "updateUser={}");
		JsonResponse<Object> responseObj = new JsonResponse<>();
		String authToken = "";
		try {
			if (user.getBody() == null) {
				log.debug(EELFLoggerDelegate.errorLogger, "updateUser: Invalid Parameters");
				responseObj.setErrorCode(JSONTags.TAG_ERROR_CODE);
				responseObj.setResponseDetail("Update Failed");
			}

			boolean isUserExists = false;
			try {
				/*if ((!PortalUtils.isEmptyOrNullString(user.getBody().getEmailId()))
						&& (!PortalUtils.isEmptyOrNullString(user.getBody().getUsername()))) {
					MLPUser mlpUser = userService.findUserByEmail(user.getBody().getEmailId());
					if (mlpUser == null) {
						mlpUser = userService.findUserByUsername(user.getBody().getUsername());
					}
					if (mlpUser != null) {
						isUserExists = true;
					}
				}*/
				
				MLPUser mlpUser = null;
				if ((!PortalUtils.isEmptyOrNullString(user.getBody().getEmailId()))) {	
					if (mlpUser == null) {
						mlpUser = userService.findUserByEmail(user.getBody().getEmailId());		
					}
				}
				if(!PortalUtils.isEmptyOrNullString(user.getBody().getUsername())){
					if (mlpUser == null) {
						mlpUser = userService.findUserByUsername(user.getBody().getUsername());
					}
				}
				if(!PortalUtils.isEmptyOrNullString(mlpUser.getAuthToken())){
                    authToken = mlpUser.getAuthToken().toString();
                }
				/*if(!PortalUtils.isEmptyOrNullString(user.getBody().getUserId())){
					if (mlpUser == null) {
						mlpUser = userService.findUserByUsername(user.getBody().getUsername());
					}
				}*/
				if (mlpUser != null) {
					isUserExists = true;
				}
			} catch (Exception e) {
				isUserExists = false;
				log.debug(EELFLoggerDelegate.errorLogger, "updateUser: Invalid Parameters");
				responseObj.setErrorCode(JSONTags.TAG_ERROR_CODE);
				responseObj.setResponseDetail("Update Failed");
			}
			if (isUserExists) {				 
				user.getBody().setJwttoken(authToken);
				userService.updateUser(user.getBody());
				responseObj.setStatus(true);
				responseObj.setResponseDetail("Success");
				responseObj.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
			} else {
				log.debug(EELFLoggerDelegate.errorLogger, "updateUser: Invalid User");
				responseObj.setResponseDetail("Failed");
				responseObj.setErrorCode(JSONTags.TAG_ERROR_CODE_EXCEPTION);
			}

		} catch (Exception e) {
			responseObj.setStatus(false);
			responseObj.setResponseDetail("Failed");
			responseObj.setErrorCode(JSONTags.TAG_ERROR_CODE_EXCEPTION);
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred while changeUserPassword()", e);
		}
		return responseObj;
	}
	
	@ApiOperation(value = "Generate new password.  Returns successful response after generating the password.", response = JsonResponse.class)
	@RequestMapping(value = {APINames.FORGET_PASSWORD}, method = RequestMethod.PUT, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<Object> forgetPassword(HttpServletRequest request, @RequestBody JsonRequest<User> user,
			HttpServletResponse response) {
		log.debug(EELFLoggerDelegate.debugLogger, "forgetPassword={}");
		JsonResponse<Object> responseObj = new JsonResponse<>();
		MLPUser mlpUser = null;
		try {
			if (user.getBody() == null) {
				log.debug(EELFLoggerDelegate.errorLogger, "forgetPassword: Invalid Parameters");
				responseObj.setErrorCode(JSONTags.TAG_ERROR_CODE);
				responseObj.setResponseDetail("forgetPassword Failed");
			}

			// Check emaid ID exist or not
			boolean isUserExists = false;
			try {
				if ((!PortalUtils.isEmptyOrNullString(user.getBody().getEmailId()))) { // &&
																						// (!PortalUtils.isEmptyOrNullString(user.getBody().getUsername()))
					mlpUser = userService.findUserByEmail(user.getBody().getEmailId());
					/*
					 * if (mlpUser == null) { mlpUser =
					 * userService.findUserByUsername(user.getBody().getUsername
					 * ()); }
					 */
					if (mlpUser != null) {
						isUserExists = true;
					}
				}
			} catch (Exception e) {
				isUserExists = false;
				log.debug(EELFLoggerDelegate.errorLogger, "forgetPassword: Invalid Parameters");
				responseObj.setErrorCode(JSONTags.TAG_ERROR_CODE_EXCEPTION);
				responseObj.setResponseDetail("Failed");
			}

			if (isUserExists) {
				userService.forgetPassword(mlpUser);
				responseObj.setStatus(true);
				responseObj.setResponseDetail("Success");
				responseObj.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
			} else {
				log.debug(EELFLoggerDelegate.errorLogger, "forgetPassword: Invalid User");
				responseObj.setResponseDetail("Email id not exist");
				responseObj.setErrorCode(JSONTags.TAG_ERROR_CODE_FAILURE);
			}

		} catch (Exception e) {
			
			responseObj.setStatus(false);
			responseObj.setResponseDetail("Failed");
			responseObj.setErrorCode(JSONTags.TAG_ERROR_CODE_EXCEPTION);
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred while forgetPassword()", e);
		}
		return responseObj;
	}
	
    @ApiOperation(value = "Updates a user Account Password.  Returns successful response after updating the password.", response = JsonResponse.class)
    @RequestMapping(value = {APINames.CHANGE_PASSWORD}, method = RequestMethod.PUT, produces = APPLICATION_JSON)
	@ResponseBody
    public JsonResponse changeUserPassword(HttpServletRequest request, @RequestBody PasswordDTO passwordDTO, HttpServletResponse response) {
        log.debug(EELFLoggerDelegate.debugLogger, "changeUserPassword={}");
        //Object responseVO = null;
        JsonResponse responseVO = new JsonResponse<>();
        try {
            if((passwordDTO == null) || (passwordDTO != null && (PortalUtils.isEmptyOrNullString(passwordDTO.getNewPassword()) || 
                    (PortalUtils.isEmptyOrNullString(passwordDTO.getOldPassword()))))) {
                log.error(EELFLoggerDelegate.errorLogger, "Bad request: NewPassword or OldPassword is empty");
            }
            //TODO As of now it does not check if User Account already exists. Need to first check if the account exists in DB
            boolean changePassword = userService.changeUserPassword(passwordDTO.getUserId(), passwordDTO.getOldPassword(), passwordDTO.getNewPassword());
            if(changePassword){
	            responseVO.setStatus(true);
                responseVO.setResponseDetail("Success");
                responseVO.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
            }else{
            	responseVO.setStatus(false);
                responseVO.setResponseDetail("Old password does not match");
                responseVO.setErrorCode(JSONTags.TAG_ERROR_CODE_OLDPASS_NOTMATCH);
            }
        } catch (Exception e) {
            responseVO.setStatus(false);
            responseVO.setResponseDetail("Failed");
            responseVO.setErrorCode(JSONTags.TAG_ERROR_CODE_EXCEPTION);
            log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred while changeUserPassword()", e);
        }
        return responseVO;
    }
	
	
    @ApiOperation(value = "Get user Account Details. Returns successful response after the data.", response = JsonResponse.class)
    @RequestMapping(value = {APINames.USER_ACCOUNT_DETAILS}, method = RequestMethod.POST, produces = APPLICATION_JSON)
    @ResponseBody
	public JsonResponse<MLPUser> getUserAccountDetails(@RequestBody JsonRequest<User> userDetails) {
		// public JsonResponse getUserAccountDetails() {
		log.debug(EELFLoggerDelegate.debugLogger, "getUserAccountDetails={}");
		// Object responseVO = null;
		JsonResponse<MLPUser> responseVO = new JsonResponse<>();
		try {
			if ((PortalUtils.isEmptyOrNullString(userDetails.getBody().getUserId()))) {
				log.error(EELFLoggerDelegate.errorLogger, "Bad request: UserId or EmailId is empty");
			}
			// TODO As of now it does not check if User Account already exists.
			// Need to first check if the account exists in DB
			if (userDetails.getBody().getUserId() != null) {
				MLPUser responseBody = userService.findUserByUserId(userDetails.getBody().getUserId());
				responseVO.setResponseBody(responseBody);
			}
			responseVO.setStatus(true);
			responseVO.setResponseDetail("Success");
			responseVO.setStatusCode(HttpServletResponse.SC_OK);
		} catch (Exception e) {
			responseVO.setStatus(false);
			responseVO.setResponseDetail("Failed");
			responseVO.setStatusCode(HttpServletResponse.SC_BAD_REQUEST);
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred while getUserAccountDetails()", e);
		}
		return responseVO;
	}  
	
	
    @ApiOperation(value = "Get user Account Details. Returns successful response after the data.", response = JsonResponse.class)
    @RequestMapping(value = {APINames.USER_DETAILS}, method = RequestMethod.GET, produces = APPLICATION_JSON)
    @ResponseBody
	public JsonResponse<List<User>> getAllUsers(HttpServletRequest request, HttpServletResponse response) {
		// public JsonResponse getUserAccountDetails() {
		log.debug(EELFLoggerDelegate.debugLogger, "getAllUsers={}");
		// Object responseVO = null;
		JsonResponse<List<User>> responseVO = new JsonResponse<>();
		try {
			List<User> user = userService.getAllUser();
			responseVO.setResponseBody(user);
			responseVO.setStatus(true);
			responseVO.setResponseDetail("Success");
			responseVO.setStatusCode(HttpServletResponse.SC_OK);
		} catch (Exception e) {
			responseVO.setStatus(false);
			responseVO.setResponseDetail("Failed");
			responseVO.setStatusCode(HttpServletResponse.SC_BAD_REQUEST);
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred while getAllUsers()", e);
		}
		return responseVO;
	} 
	
    @ApiOperation(value = "Get user Role Details.", response = JsonResponse.class)
    @RequestMapping(value = {APINames.USER_ROLE_DETAILS}, method = RequestMethod.GET, produces = APPLICATION_JSON)
    @ResponseBody
	public JsonResponse<List<MLPRole>> getUserRole(@PathVariable("userId") String userId, HttpServletRequest request, HttpServletResponse response) {
		// public JsonResponse getUserAccountDetails() {
		log.debug(EELFLoggerDelegate.debugLogger, "changeUserPassword={}");
		// Object responseVO = null;
		JsonResponse<List<MLPRole>> responseVO = new JsonResponse<>();
		try {
			List<MLPRole> roles = userService.getUserRole(userId);
			responseVO.setResponseBody(roles);
			responseVO.setStatus(true);
			responseVO.setResponseDetail("Success");
			responseVO.setStatusCode(HttpServletResponse.SC_OK);
		} catch (Exception e) {
			responseVO.setStatus(false);
			responseVO.setResponseDetail("Failed");
			responseVO.setStatusCode(HttpServletResponse.SC_BAD_REQUEST);
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred while getUserRole()", e);
		}
		return responseVO;
	} 
	
	
    @ApiOperation(value = "Updates a user Account Image. ")
    @RequestMapping(value = {APINames.UPDATE_USER_IMAGE}, method = RequestMethod.POST, produces = APPLICATION_JSON)
    @ResponseBody
    public JsonResponse updateUserImage(HttpServletRequest request, @RequestParam("userImage") MultipartFile file, @PathVariable("userId") String userId, HttpServletResponse response) {
        log.debug(EELFLoggerDelegate.debugLogger, "updateUserImage={}");
        JsonResponse<MLPUser> responseVO = new JsonResponse<>();
		try {
			if (PortalUtils.isEmptyOrNullString(userId)) {
				log.error(EELFLoggerDelegate.errorLogger, "Bad request: UserId empty");
			}
			if (userId != null) {
				MLPUser mlpUser = userService.findUserByUserId(userId);
				if (mlpUser != null) {
					mlpUser.setPicture(ArrayUtils.toObject(file.getBytes()));
					userService.updateUserImage(mlpUser);
				}
				
			}
			responseVO.setStatus(true);
			responseVO.setResponseDetail("Success");
			responseVO.setStatusCode(HttpServletResponse.SC_OK);
		} catch (Exception e) {
			responseVO.setStatus(false);
			responseVO.setResponseDetail("Failed");
			responseVO.setStatusCode(HttpServletResponse.SC_BAD_REQUEST);
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred while updateUserImage()", e);
		}
		return responseVO;
    }
	
	
    @ApiOperation(value = "Get user Account Image.", response = JsonResponse.class)
    @RequestMapping(value = {APINames.USER_PROFILE_PIC}, method = RequestMethod.GET, produces = APPLICATION_JSON)
    @ResponseBody
	public JsonResponse<byte[]> getUserImage(@PathVariable("userId") String userId) {
		// public JsonResponse getUserAccountDetails() {
		log.debug(EELFLoggerDelegate.debugLogger, "getUserImage={}");
		// Object responseVO = null;
		JsonResponse<byte []> responseVO = new JsonResponse<>();
		try {
			if (PortalUtils.isEmptyOrNullString(userId)) {
				log.error(EELFLoggerDelegate.errorLogger, "Bad request: UserId is empty");
			}
			// TODO As of now it does not check if User Account already exists.
			// Need to first check if the account exists in DB
			if (userId != null) {
				MLPUser mlpUser = userService.findUserByUserId(userId);
				if (mlpUser != null && mlpUser.getPicture() != null) {
					responseVO.setResponseBody(ArrayUtils.toPrimitive(mlpUser.getPicture()));
					responseVO.setStatus(true);
					responseVO.setResponseDetail("Success");
					responseVO.setStatusCode(HttpServletResponse.SC_OK);
			}
				else {
					responseVO.setStatus(false);
					responseVO.setResponseDetail("Failed");
					responseVO.setStatusCode(HttpServletResponse.SC_BAD_REQUEST);
					log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred while getUserImage()");
				}
			}
		} catch (Exception e) {
			responseVO.setStatus(false);
			responseVO.setResponseDetail("Failed");
			responseVO.setStatusCode(HttpServletResponse.SC_BAD_REQUEST);
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred while getUserImage()", e);
		}
		return responseVO;
	}
	
	
    @ApiOperation(value = "Get user Account Details from jwt Token", response = JsonResponse.class)
    @RequestMapping(value = {"/userProfile"}, method = RequestMethod.GET, produces = APPLICATION_JSON)
    @ResponseBody
	public MLPUser userProfile(HttpServletRequest request) throws MalformedException {
		// public JsonResponse getUserAccountDetails() {
		log.debug(EELFLoggerDelegate.debugLogger, "get User account from jwt={}");
		String authorization = request.getHeader("jwtToken");
		
		JsonResponse<MLPUser> responseVO = new JsonResponse<MLPUser>();
		// Object responseVO = null;
		String userToken = authorization.replace("Bearer ", "");
		//Boolean isValid = tokenValidation.tokenRegnerationAndValidation(userToken);
		MLPUser user = null;
			String userName = jwtTokenUtil.getUsernameFromToken(userToken);
			
			Map<String, String> queryParams = new HashMap<>();
			queryParams.put("loginName", userName);
			user = userService.findUserByUsername(userName);
			responseVO.setResponseBody(user);
			responseVO.setStatus(true);
			responseVO.setResponseDetail("Success");
			responseVO.setStatusCode(HttpServletResponse.SC_OK);
		
		return user;
	}
	
	
    @ApiOperation(value = "Get Q and A URL", response = JsonResponse.class)
    @RequestMapping(value = {"/qAUrl"}, method = RequestMethod.GET, produces = APPLICATION_JSON)
    @ResponseBody
	public JsonResponse<String> getQandAurl(HttpServletRequest request, HttpServletResponse response) {
		
		String qandaUrl = env.getProperty("qanda.url", "");
		JsonResponse<String> responseVO = new JsonResponse<String>();
		responseVO.setResponseBody(qandaUrl);
		responseVO.setStatus(true);
		responseVO.setResponseDetail("Success");
		responseVO.setStatusCode(HttpServletResponse.SC_OK);
		return responseVO;
	}

    @ApiOperation(value = "Get Documentation URL", response = JsonResponse.class)
    @RequestMapping(value = {"/docs"}, method = RequestMethod.GET, produces = APPLICATION_JSON)
    @ResponseBody
	public JsonResponse<String> getDocurl(HttpServletRequest request, HttpServletResponse response) {
		
		String docUrl = env.getProperty("doc.url", "");
		JsonResponse<String> responseVO = new JsonResponse<String>();
		responseVO.setResponseBody(docUrl);
		responseVO.setStatus(true);
		responseVO.setResponseDetail("Success");
		responseVO.setStatusCode(HttpServletResponse.SC_OK);
		return responseVO;
	}

	@ApiOperation(value = "Deacivate multiple users", response = JsonResponse.class)
	@RequestMapping(value = {APINames.UPADATE_BULK_USER}, method = RequestMethod.PUT, produces = APPLICATION_JSON)
	@PreAuthorize("hasAuthority(T(org.acumos.portal.be.security.RoleAuthorityConstants).ADMIN)")
	@ResponseBody
	public JsonResponse<Object> updateBulkUsers(HttpServletRequest request, @RequestBody JsonRequest<User> user, HttpServletResponse response) {
		log.debug(EELFLoggerDelegate.debugLogger, "updateUser={}");
		JsonResponse<Object> responseObj = new JsonResponse<>();
		
		try{
			if(user.getBody().getUserIdList() != null){
				for(String userId : user.getBody().getUserIdList())
				{
					MLPUser mlpUser = userService.findUserByUserId(userId);
					if(mlpUser != null){
					  mlpUser.setActive(false);
					  userService.updateBulkUsers(mlpUser);
					}
				}
				responseObj.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
				responseObj.setResponseDetail("Users deactivated succesfuly");
			}else{
				log.debug(EELFLoggerDelegate.errorLogger, "UserId not found");
				responseObj.setErrorCode(JSONTags.TAG_ERROR_CODE_FAILURE);
				responseObj.setResponseDetail("UserId not found");
			}
			
		}catch(Exception e){
			log.debug(EELFLoggerDelegate.errorLogger, "Exception occured while updateBulkUsers");
			responseObj.setErrorCode(JSONTags.TAG_ERROR_CODE_EXCEPTION);
			responseObj.setResponseDetail("Exception occured while updateBulkUsers");
		}
		
		return responseObj;
		
	}
	
	@ApiOperation(value = "Delete multiple users", response = JsonResponse.class)
	@RequestMapping(value = {APINames.DELETE_BULK_USER}, method = RequestMethod.POST, produces = APPLICATION_JSON)
	@PreAuthorize("hasAuthority(T(org.acumos.portal.be.security.RoleAuthorityConstants).ADMIN)")
	@ResponseBody
	public JsonResponse<Object> deleteBulkUsers(HttpServletRequest request, @RequestBody JsonRequest<User> user, HttpServletResponse response) {
		log.debug(EELFLoggerDelegate.debugLogger, "deleteUser={}");
		JsonResponse<Object> responseObj = new JsonResponse<>();
		
		try{
			if(user.getBody().getUserIdList() != null && user.getBody().getBulkUpdate() != null){				
				for(String userId : user.getBody().getUserIdList())
				{
					MLPUser mlpUser = userService.findUserByUserId(userId);
					if (mlpUser != null) {
						User userObj = PortalUtils.convertToMLPuser(mlpUser);
						if (user.getBody().getBulkUpdate().equalsIgnoreCase("delete")) {
							userService.deleteBulkUsers(userId);
						} else if (user.getBody().getBulkUpdate().equalsIgnoreCase("active")) {
							userObj.setActive("Y");
						} else if (user.getBody().getBulkUpdate().equalsIgnoreCase("inactive")) {
							userObj.setActive("N");
						}
						userService.updateUser(userObj);
					}
				}
				log.debug(EELFLoggerDelegate.errorLogger, "User detals updated succesfully");
				responseObj.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
				responseObj.setResponseDetail("Users deleted succesfuly");
			}else{
				log.debug(EELFLoggerDelegate.errorLogger, "UserId not found");
				responseObj.setErrorCode(JSONTags.TAG_ERROR_CODE_FAILURE);
				responseObj.setResponseDetail("UserId not found");
			}		
		}catch(Exception e){
			log.debug(EELFLoggerDelegate.errorLogger, "Exception occured while deleteBulkUsers");
			responseObj.setErrorCode(JSONTags.TAG_ERROR_CODE_EXCEPTION);
			responseObj.setResponseDetail("Exception occured while deleteBulkUsers");
		}	
		return responseObj;		
	}
	
	@ApiOperation(value = "Get user Account Details. Returns successful response after the data.", response = JsonResponse.class)
	   @RequestMapping(value = {APINames.ACTIVE_USER_DETAILS}, method = RequestMethod.GET, produces = APPLICATION_JSON)
	   @ResponseBody
	    public JsonResponse<List<User>> getAllActiveUsers(HttpServletRequest request, HttpServletResponse response,@PathVariable("active") boolean activeFlag) {
	        // public JsonResponse getUserAccountDetails() { 
	        log.debug(EELFLoggerDelegate.debugLogger, "getAllActiveUsers={}");
	        // Object responseVO = null;
	        JsonResponse<List<User>> responseVO = new JsonResponse<>();
	        try {
	            List<User> users = userService.getAllUser();
	            
	            if (activeFlag) {
	                List<User> removeUsers = new ArrayList<>(users.size());
	                for (User user : users) {
	                    if (!user.getActive().equals(String.valueOf(activeFlag))) {
	                        removeUsers.add(user);
	                    }
	                }
	                users.removeAll(removeUsers);
	            }
	            
	            responseVO.setResponseBody(users);
	            responseVO.setStatus(true);
	            responseVO.setResponseDetail("Success");
	            responseVO.setStatusCode(HttpServletResponse.SC_OK);
	            
	        } catch (Exception e) {
	            responseVO.setStatus(false);
	            responseVO.setResponseDetail("Failed");
	            responseVO.setStatusCode(HttpServletResponse.SC_BAD_REQUEST);
	            log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred while getAllActiveUsers()", e);
	        }
	        return responseVO;
	    }
}

