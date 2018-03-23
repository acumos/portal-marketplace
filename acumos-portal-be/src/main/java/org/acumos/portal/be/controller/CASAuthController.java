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

package org.acumos.portal.be.controller;

import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.acumos.cds.client.HttpComponentsClientHttpRequestFactoryBasicAuth;
import org.acumos.cds.domain.MLPRole;
import org.acumos.cds.domain.MLPUser;
import org.acumos.portal.be.APINames;
import org.acumos.portal.be.common.JSONTags;
import org.acumos.portal.be.common.JsonResponse;
import org.acumos.portal.be.common.ServiceResponse;
import org.acumos.portal.be.common.exception.UserServiceException;
import org.acumos.portal.be.service.UserRoleService;
import org.acumos.portal.be.service.UserService;
import org.acumos.portal.be.transport.MLRole;
import org.acumos.portal.be.transport.User;
import org.acumos.portal.be.transport.UserMasterObject;
import org.acumos.portal.be.util.EELFLoggerDelegate;
import org.acumos.portal.be.util.PortalUtils;
import org.apache.http.HttpHost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import io.swagger.annotations.ApiOperation;

@Controller
@RequestMapping(APINames.CAS)
public class CASAuthController extends AbstractController {

    @Autowired
    private Environment env;
    
	@Autowired
	private UserService userService;
	
	@Autowired
	private UserRoleService userRoleService;

    private static final EELFLoggerDelegate log = EELFLoggerDelegate.getLogger(CASAuthController.class);


    public CASAuthController() {
        // TODO Auto-generated constructor stub
    }


    @ApiOperation(value = "Get CAS login Enables ot not", response = JsonResponse.class)
    @RequestMapping(value = {"/enabled"}, method = RequestMethod.GET, produces = APPLICATION_JSON)
    @ResponseBody
	public JsonResponse<String> getDocurl(HttpServletRequest request, HttpServletResponse response) {
		
		String docUrl = env.getProperty("portal.feature.cas_enabled", "false");
		JsonResponse<String> responseVO = new JsonResponse<String>();
		responseVO.setResponseBody(docUrl);
		responseVO.setStatus(true);
		responseVO.setResponseDetail("Success");
		responseVO.setStatusCode(HttpServletResponse.SC_OK);
		return responseVO;
	}


    /**
	 * @param theHttpResponse
	 *            HttpServletResponse
	 * @param ticket
	 *            Ticket from CAS
	 * @param service
	 *            Service from CAS
	 * @return data 
	 *            JsonResponse<Object>
	 */
	@ApiOperation(value = "Returns the UserObject from CAS", response = JsonResponse.class)
	@RequestMapping(value = { "serviceValidate" }, method = RequestMethod.GET, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<Object> serviceValidate(
			HttpServletResponse theHttpResponse,
			@RequestParam(value = "ticket") String ticket
			, @RequestParam(value = "service") String service) {
		
		log.debug(EELFLoggerDelegate.debugLogger, "ticket=" + ticket + " service=" + service);
		JsonResponse<Object> jsonResponse = new JsonResponse<>();
		
		Map<String, Object> queryParams = new HashMap<String, Object>();
		queryParams.put("ticket", ticket);
		queryParams.put("service", service);
		queryParams.put("format", "JSON");
		
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(env.getProperty("cas.service.validate.url"));
		if (queryParams != null && queryParams.size() > 0) {
			for (Map.Entry<String, ? extends Object> entry : queryParams.entrySet()) {
				Object value = null;
				if (entry.getValue() instanceof Date)
					value = ((Date) entry.getValue()).getTime();
				else
					value = entry.getValue().toString();
				builder.queryParam(entry.getKey(), value);
			}
		}
		
		URI uri = builder.build().encode().toUri();
		ResponseEntity<String> response = null;
		try {
			
			RestTemplate restTemplate = getRestTemplate(uri.toString());
			
			response = restTemplate.exchange(uri, HttpMethod.GET, null, String.class);
			
			StringReader reader = new StringReader(response.getBody());
			JAXBContext jaxbContext = JAXBContext.newInstance(ServiceResponse.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			
			ServiceResponse resp = (ServiceResponse) jaxbUnmarshaller.unmarshal(reader);
			
			if(!PortalUtils.isEmptyOrNullString(resp.getAuthenticationFailure())) {
				jsonResponse.setErrorCode(JSONTags.TAG_ERROR_CODE);
				jsonResponse.setStatusCode(400);
				theHttpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				jsonResponse.setResponseDetail("Error occured while authentication user");
				log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred while serviceValidate()", resp.getAuthenticationFailure());
				return jsonResponse;
			} else {
				
				boolean isUserExists = false;
				String loginUser =  null;
				String userEmail =  null;
				String firstName =  null;
				String lastName =  null;
				if(resp.getAuthenticationSuccess()!= null) {
					loginUser = resp.getAuthenticationSuccess().getUser();
					userEmail = resp.getAuthenticationSuccess().getAttributes().getMail();
					firstName = resp.getAuthenticationSuccess().getAttributes().getProfile_name_first();
					lastName = resp.getAuthenticationSuccess().getAttributes().getProfile_name_last();
				} else {
					jsonResponse.setErrorCode(JSONTags.TAG_ERROR_CODE);
					jsonResponse.setStatusCode(400);
					theHttpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
					jsonResponse.setResponseDetail("Error occured while authentication user");
					log.error(EELFLoggerDelegate.errorLogger, "Cannot fetch user credentials from response ", resp.getAuthenticationSuccess());
					return jsonResponse;
				}

				User user = new User();
				MLPUser mlpUser = userService.findUserByEmail(userEmail);
				if (mlpUser != null) {
					isUserExists = true;
				}
				if (mlpUser == null) {
					mlpUser = userService.findUserByUsername(loginUser);
					if (mlpUser != null) {
						isUserExists = true;
					}
				}
				
				if(!isUserExists) {
					UserMasterObject usermasterobject = new UserMasterObject();
		        	usermasterobject.setActive(true);
		        	usermasterobject.setFirstName(firstName);
		        	usermasterobject.setLastName(lastName);
		        	usermasterobject.setEmailId(userEmail);
		        	usermasterobject.setUsername(loginUser);
		        	user = PortalUtils.convertUserMasterIntoMLPUser(usermasterobject);
		        	user = userService.save(user);
		        	
		        	//create role for user
					if(user != null && !PortalUtils.isEmptyOrNullString(user.getUserId())){
						createRole(user.getUserId());
						user.setUserAssignedRolesList(userService.getUserRole(user.getUserId())); 
					}
					
				} else {
					user = PortalUtils.convertToMLPuser(mlpUser);
				}
				jsonResponse.setStatusCode(200);
				jsonResponse.setResponseDetail("Validation status updated Successfully");
				//JsonUtils.serializer().toPrettyString(response.getBody());
				jsonResponse.setContent(user);
			}
			
			
		}
		catch (Exception x) {
			x.printStackTrace();
			jsonResponse.setErrorCode(JSONTags.TAG_ERROR_CODE);
			jsonResponse.setStatusCode(400);
			jsonResponse.setResponseDetail("Cas Service Validation Failed" + x.getMessage());
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred while serviceValidate()", x);
		}
		finally {
			log.info(EELFLoggerDelegate.debugLogger, uri + " response " + response);
		}		
		return jsonResponse;		
	}
	
	private void createRole(String userId) {
		List<MLPRole> userAssignedRolesList = new ArrayList<>();
		try {
			userAssignedRolesList = userService.getUserRole(userId);
			Boolean defaultRoleCreated = false;
			String defaultRoleId = null;
			if (userAssignedRolesList.isEmpty()) {

				List<MLRole> allRoles = userRoleService.getAllRoles();
				if (!allRoles.isEmpty()) {
					for (MLRole role : allRoles) {
						if (role.getName().equals("MLP System User")) {
							defaultRoleCreated = true;
							defaultRoleId = role.getRoleId();
						}
					}
				}
				// If default role is not created, then create
				if (!defaultRoleCreated) {
					MLRole role = new MLRole();
					role.setName("MLP System User");
					role.setActive(true);
					MLPRole mlpRole = userRoleService.createRole(role);
					defaultRoleId = mlpRole.getRoleId();
				}
				// Assign default role to user
				if (userId != null && defaultRoleId != null) {
					userRoleService.addUserRole(userId, defaultRoleId);
				}

			} else {
				log.error(EELFLoggerDelegate.errorLogger, "User already have an role");
			}
		} catch (UserServiceException e) {
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred while createRole :", e);
		} catch (Exception e) {
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred while createRole :", e);
		}
	}
	
	public RestTemplate getRestTemplate(String webapiUrl) {
		
		RestTemplate restTemplate = new RestTemplate();
		
		if (webapiUrl == null)
			throw new IllegalArgumentException("Null URL not permitted");

		URL url = null;
		try {
			url = new URL(webapiUrl);
		} catch (MalformedURLException ex) {
			throw new RuntimeException("Failed to parse URL", ex);
		}
		final HttpHost httpHost = new HttpHost(url.getHost(), url.getPort());
		CloseableHttpClient httpClient = null;
		httpClient = HttpClientBuilder.create().build();
		HttpComponentsClientHttpRequestFactoryBasicAuth requestFactory = new HttpComponentsClientHttpRequestFactoryBasicAuth(
				httpHost);
		requestFactory.setHttpClient(httpClient);
		restTemplate.setRequestFactory(requestFactory);
		return restTemplate;
	}
}
