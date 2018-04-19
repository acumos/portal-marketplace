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
package org.acumos.portal.be.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.acumos.portal.be.service.MailJet;
import org.acumos.portal.be.service.MailService;
import org.acumos.portal.be.service.UserService;
import org.acumos.portal.be.transport.MLRole;
import org.acumos.portal.be.transport.MailData;
import org.acumos.portal.be.transport.User;
import org.acumos.portal.be.util.EELFLoggerDelegate;
import org.acumos.portal.be.util.PortalUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;

import org.acumos.cds.client.ICommonDataServiceRestClient;
import org.acumos.cds.domain.MLPPasswordChangeRequest;
import org.acumos.cds.domain.MLPRole;
import org.acumos.cds.domain.MLPUser;
import org.acumos.cds.transport.RestPageRequest;
import org.acumos.cds.transport.RestPageResponse;


@Service
public class UserServiceImpl extends AbstractServiceImpl implements UserService {

    @Autowired
    MailService mailservice;
    
    @Autowired
    MailJet mailJet;

    private static final EELFLoggerDelegate log = EELFLoggerDelegate.getLogger(UserServiceImpl.class);

    @Autowired
    private Environment env;

    @Override
    public User save(User user) {
        log.debug(EELFLoggerDelegate.debugLogger, "save user={}", user);
        ICommonDataServiceRestClient dataServiceRestClient = getClient();
        //Lets Create a User Account
        MLPUser mlpUser = new MLPUser();
        mlpUser.setFirstName(user.getFirstName());
        mlpUser.setLastName(user.getLastName());
        mlpUser.setEmail(user.getEmailId());
        mlpUser.setLoginName(user.getUsername());
        if(!PortalUtils.isEmptyOrNullString(user.getPassword()))
        	mlpUser.setLoginHash(user.getPassword());
        mlpUser.setActive(true);// Default active as true until we enable emails
        log.info(EELFLoggerDelegate.debugLogger, " user={}", mlpUser);
        mlpUser = dataServiceRestClient.createUser(mlpUser);
        user = PortalUtils.convertToMLPuser(mlpUser);
        /*} else {
            log.error(EELFLoggerDelegate.errorLogger, "save user={}", user);
            throw new UserServiceException(HttpServletResponse.SC_RESET_CONTENT, "Failsdreryeyed");
        }*/
        
        
        //Send new user account created notification
        MailData mailData = new MailData();
        mailData.setSubject("New User Account Notification");
        mailData.setFrom("customerservice@acumos.org");
        mailData.setTemplate("accountCreated.ftl");
        List<String> to = new ArrayList<String>();
        to.add(mlpUser.getEmail());
        mailData.setTo(to);
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("user", mlpUser);
        mailData.setModel(model);

        try {
        	if(!PortalUtils.isEmptyOrNullString(env.getProperty("portal.feature.email_service")) 
        		&& env.getProperty("portal.feature.email_service").equalsIgnoreCase("smtp")) {
        	
        		//Use SMTP setup
                mailservice.sendMail(mailData);
        		}else {
        			if(!PortalUtils.isEmptyOrNullString(env.getProperty("portal.feature.email_service")) 
                    		&& env.getProperty("portal.feature.email_service").equalsIgnoreCase("mailjet")) 
            			mailJet.sendMail(mailData);
        		}
            } catch (MailException ex) {
                log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred while Sending Mail to user ={}", ex);
            }
        return user;
    }

    @Override
    public List<User> getAllUser() {

        List<User> user = null;
        List<MLPUser> mlpUser = null;
        log.debug(EELFLoggerDelegate.debugLogger, "getAllUser");
        ICommonDataServiceRestClient dataServiceRestClient = getClient();
        Map<String, Object> queryParameters = new HashMap<>();
        //queryParameters.put("active_yn","Y");
        RestPageRequest pageRequest = new RestPageRequest();
        pageRequest.setPage(0);
        pageRequest.setSize(1000);
        RestPageResponse<MLPUser> userList = dataServiceRestClient.getUsers(pageRequest);
        if (userList != null) {
        	mlpUser = userList.getContent();
		    //List<MLPUser> mlpUser = dataServiceRestClient.getUsers(pageRequest);
	        if(!PortalUtils.isEmptyList(mlpUser)) {
	            user = new ArrayList<>();
	            for(MLPUser mlpusers : mlpUser){
	                User users = PortalUtils.convertToMLPuser(mlpusers);
	                if(users.getUserId() != null){
	                    List<MLPRole> mlprolelist = dataServiceRestClient.getUserRoles(users.getUserId());
	                    users.setUserAssignedRolesList(mlprolelist);
	                   /* for (int i = 0; i < mlprolelist.size(); i++) {
	                        users.setRole(mlprolelist.get(i).getName());
	                        users.setRoleId(mlprolelist.get(i).getRoleId());
	                    }*/
	                    
	               }
	                user.add(users);
	            }
	        }
        }
        return user;
        
    }

    @Override
    public MLPUser findUserByEmail(String emailId) {
        log.debug(EELFLoggerDelegate.debugLogger, "findUserByEmail ={}", emailId);
        ICommonDataServiceRestClient dataServiceRestClient = getClient();
        MLPUser mlpUser = null;
        //TODO WorkAround for emailId as there is no method available for finding user using emailId
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("email",emailId);
        RestPageRequest pageRequest = new RestPageRequest();
        pageRequest.setPage(0);
        pageRequest.setSize(10);
        RestPageResponse<MLPUser> userList = dataServiceRestClient.searchUsers(queryParams, false, pageRequest);
        List<MLPUser> mlpUsers = userList.getContent();
        for(MLPUser user : mlpUsers) {
            if(user != null) {
                if(!PortalUtils.isEmptyOrNullString(user.getEmail()) && user.getEmail().equalsIgnoreCase(emailId)) {
                    mlpUser = user;
                    break;
                }
                    
            }
        }
        
        return mlpUser;
    }


    @Override
    public MLPUser findUserByUsername(String username) {
        log.debug(EELFLoggerDelegate.debugLogger, "findUserByUsername ={}", username);
        ICommonDataServiceRestClient dataServiceRestClient = getClient();
        MLPUser mlpUser = null;
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("loginName",username);
        RestPageRequest pageRequest = new RestPageRequest();
        pageRequest.setPage(0);
        pageRequest.setSize(10);
        RestPageResponse<MLPUser> userList = dataServiceRestClient.searchUsers(queryParams, false, pageRequest);
        List<MLPUser> mlpUsers = userList.getContent();
        for(MLPUser user : mlpUsers) {
            if(user != null) {
                if(!PortalUtils.isEmptyOrNullString(user.getLoginName()) && user.getLoginName().equalsIgnoreCase(username)) {
                    mlpUser = user;
                    break;
                }
                    
            }
        }
        return mlpUser;
    }
    
    @Override
    public boolean delete() {
        return false;
        // TODO Auto-generated method stub

    }

    @Override
    public MLPUser login(String username, String password) {
        log.debug(EELFLoggerDelegate.debugLogger, "login ={}", username);
        ICommonDataServiceRestClient dataServiceRestClient = getClient();
        return dataServiceRestClient.loginUser(username, password);
        //PortalRestClienttImpl portalRestClienttImpl = new PortalRestClienttImpl(env.getProperty("cdms.client.url"), env.getProperty("cdms.client.user.name"), env.getProperty("cdms.client.password"));;
        //LoginTransport login = new LoginTransport(username, password);
        //return portalRestClienttImpl.login(login);
        
    }

    @Override
    public boolean resetUserPassword(String emailId) throws Exception {
        return true;
        
    }


	 @Override
	public boolean changeUserPassword(String userId, String oldPassword, String newPassword) throws Exception {
		log.debug(EELFLoggerDelegate.debugLogger, "changeUserPassword ={}", userId);
		boolean passwordChangeSuccessful = false;
		boolean oldPass = false;
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		MLPUser user = dataServiceRestClient.getUser(userId);
		MLPPasswordChangeRequest changeRequest = new MLPPasswordChangeRequest();
		changeRequest.setOldLoginPass(oldPassword);
		changeRequest.setNewLoginPass(newPassword);

		// First Check if User Exists
		if (user != null) {
			// Lets verify existing password
			try {
				dataServiceRestClient.loginUser(user.getLoginName(), oldPassword);
				oldPass = true;
			} catch (Exception e) {
				oldPass = false;
				log.error(EELFLoggerDelegate.errorLogger, "Old password not matches : changeUserPassword ={}", e);
			}
			// If Successful then try to change the password
			if (oldPass) {
				dataServiceRestClient.updatePassword(user, changeRequest);
				passwordChangeSuccessful = true;
			}
		}

		// If password changed successfully send a password change notification
		// to user
		if (passwordChangeSuccessful) {
			MailData mailData = new MailData();
			mailData.setSubject("Acumos Change Password Notification");
			mailData.setFrom("customerservice@acumos.org");
			mailData.setTemplate("changePass.ftl");
			List<String> to = new ArrayList<String>();
			to.add(user.getEmail());
			mailData.setTo(to);
			Map<String, Object> model = new HashMap<String, Object>();
			model.put("user", user);
			mailData.setModel(model);

			try {
	        	if(!PortalUtils.isEmptyOrNullString(env.getProperty("portal.feature.email_service")) 
	        		&& env.getProperty("portal.feature.email_service").equalsIgnoreCase("smtp")) {
	        	
	        		//Use SMTP setup
	                mailservice.sendMail(mailData);
	        		}else {
	        			if(!PortalUtils.isEmptyOrNullString(env.getProperty("portal.feature.email_service")) 
	                    		&& env.getProperty("portal.feature.email_service").equalsIgnoreCase("mailjet")) 
	            			mailJet.sendMail(mailData);
	        		}
	            } catch (MailException ex) {
	                log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred while Sending Mail to user ={}", ex);
	            }
		}
		return passwordChangeSuccessful;
	}

    @Override
    public void updateUser(User user) {
        log.debug(EELFLoggerDelegate.debugLogger, "updateUser ={}", user);
        ICommonDataServiceRestClient dataServiceRestClient = getClient();
        dataServiceRestClient.updateUser(PortalUtils.convertToMLPUserForUpdate(user));
    }

    @Override
    public void forgetPassword(MLPUser mlpUser) { 
        // Generate password
         String newPassword = RandomStringUtils.random(10, true, true);
         mlpUser.setLoginHash(newPassword);

         // set expire date 24 hours for new password
        ICommonDataServiceRestClient dataServiceRestClient = getClient();
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, 1);
        mlpUser.setLoginPassExpire(cal.getTime());
        mlpUser.setAuthToken(null);

        // Update users password & password expire date
        dataServiceRestClient.updateUser(mlpUser);
        
        //Send mail to user
        MailData mailData = new MailData();
        mailData.setSubject("Acumos Forgot Password");
        mailData.setFrom("customerservice@acumos.org");
        mailData.setTemplate("mailTemplate.ftl");
        List<String> to = new ArrayList<String>();
        to.add(mlpUser.getEmail());
        mailData.setTo(to);
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("user", mlpUser);
        model.put("signature", "Acumos Customer Service");
        mailData.setModel(model);

        try {
        	if(!PortalUtils.isEmptyOrNullString(env.getProperty("portal.feature.email_service")) 
        		&& env.getProperty("portal.feature.email_service").equalsIgnoreCase("smtp")) {
        	
        		//Use SMTP setup
                mailservice.sendMail(mailData);
        		}else {
        			if(!PortalUtils.isEmptyOrNullString(env.getProperty("portal.feature.email_service")) 
                    		&& env.getProperty("portal.feature.email_service").equalsIgnoreCase("mailjet")) 
            			mailJet.sendMail(mailData);
        		}
            } catch (MailException ex) {
                log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred while Sending Mail to user ={}", ex);
            }
        }

	@Override
	public MLPUser findUserByUserId(String userId) {
			log.debug(EELFLoggerDelegate.debugLogger, "findUserByUserId ={}", userId);
	        ICommonDataServiceRestClient dataServiceRestClient = getClient();
	        MLPUser mlpUser = null;
	        Map<String, Object> queryParams = new HashMap<>();
	        queryParams.put("userId",userId);
	        MLPUser user = dataServiceRestClient.getUser(userId);
            if(user != null) {
                mlpUser = user;
            }
	        return mlpUser;
	}
	
    @Override
    public List<MLPRole> getUserRole(String userId) {

        log.debug(EELFLoggerDelegate.debugLogger, "getAllUser");
        ICommonDataServiceRestClient dataServiceRestClient = getClient();
        //queryParameters.put("active_yn","Y");
        List<MLPRole> mlpRoles = dataServiceRestClient.getUserRoles(userId);
        return mlpRoles;
    }
    
    @Override
    public MLRole getRoleCountForUser(RestPageRequest pageRequest) {
        log.debug(EELFLoggerDelegate.debugLogger, "getAllRoles");
        MLRole roleUserMap = new MLRole();
        ICommonDataServiceRestClient dataServiceRestClient = getClient();        
		RestPageResponse<MLPUser> userList = dataServiceRestClient.getUsers(pageRequest);
		RestPageResponse<MLPRole> roleList = dataServiceRestClient.getRoles(pageRequest);
		 
		Map<String, Map<String, String>> roleIdUserCount = new HashMap<>();
		int i=0;
		for (MLPUser mlpUser : userList) {
			
			String userId = mlpUser.getUserId();
			Map<String, String> roleDetails = new HashMap<>();
			List<MLPRole> mlpRoles = dataServiceRestClient.getUserRoles(userId);
			String roleId = mlpRoles.get(i).getRoleId();
			roleDetails.put("roleId",roleId);
			String roleName = mlpRoles.get(i).getName();
			roleDetails.put("roleName",roleName);
			String userCount = mlpRoles.size()+"";
			roleDetails.put("userCount",userCount);
			 
			
			roleIdUserCount.put(roleId, roleDetails);
			//roleUserMap.setRoleIdUserCount(roleIdUserCount);
			i++;
		}
		
        
        return roleUserMap; 
    }
    
    @Override
    public void updateUserImage(MLPUser user) {
    	log.debug(EELFLoggerDelegate.debugLogger, "updateUserImage ={}", user);
        ICommonDataServiceRestClient dataServiceRestClient = getClient();
        dataServiceRestClient.updateUser(user);
    }

	@Override
	public void updateBulkUsers(MLPUser mlpUser) {
		log.debug(EELFLoggerDelegate.debugLogger, "updateUserImage ={}", mlpUser);
        ICommonDataServiceRestClient dataServiceRestClient = getClient();
        dataServiceRestClient.updateUser(mlpUser);
	}
	
	@Override
	public void deleteBulkUsers(String mlpUser) {
		log.debug(EELFLoggerDelegate.debugLogger, "updateUserImage ={}", mlpUser);
        ICommonDataServiceRestClient dataServiceRestClient = getClient();
        dataServiceRestClient.deleteUser(mlpUser);
	}

    @Override
    public void generatePassword(MLPUser mlpUser) { 
        // Generate password
         String newPassword = RandomStringUtils.random(10, true, true);
         mlpUser.setLoginHash(newPassword);

         // set expire date 24 hours for new password
        ICommonDataServiceRestClient dataServiceRestClient = getClient();
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, 1);
        mlpUser.setLoginPassExpire(cal.getTime());
        mlpUser.setAuthToken(null);

        // Update users password & password expire date
        dataServiceRestClient.updateUser(mlpUser);
        
        //Send mail to user
        MailData mailData = new MailData();
        mailData.setSubject("Acumos New User Password");
        mailData.setFrom("customerservice@acumos.org");
        mailData.setTemplate("mailTemplate.ftl");
        List<String> to = new ArrayList<String>();
        to.add(mlpUser.getEmail());
        mailData.setTo(to);
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("user", mlpUser);
        model.put("signature", "Acumos Customer Service");
        mailData.setModel(model);

        try {
        	if(!PortalUtils.isEmptyOrNullString(env.getProperty("portal.feature.email_service")) 
        		&& env.getProperty("portal.feature.email_service").equalsIgnoreCase("smtp")) {
        	
        		//Use SMTP setup
                mailservice.sendMail(mailData);
        		}else {
        			if(!PortalUtils.isEmptyOrNullString(env.getProperty("portal.feature.email_service")) 
                    		&& env.getProperty("portal.feature.email_service").equalsIgnoreCase("mailjet")) 
            			mailJet.sendMail(mailData);
        		}
            } catch (MailException ex) {
                log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred while Sending Mail to user ={}", ex);
            }
        }
}

