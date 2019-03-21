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

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.HttpHeaders;

import org.acumos.portal.be.service.OauthUserService;
import org.acumos.portal.be.transport.OauthUser;
import org.acumos.portal.be.util.PortalUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.acumos.cds.client.ICommonDataServiceRestClient;
import org.acumos.cds.domain.MLPUserLoginProvider;



@Service
public class OauthUserServiceImpl extends AbstractServiceImpl implements OauthUserService {

	
	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());	

	@Autowired
	private Environment env;
	
	private String GITHUB_TOKEN_URL = "https://github.com/login/oauth/access_token";

	@Override
	public OauthUser save(OauthUser oauthUser) {
		log.debug("OauthUserServiceImpl: save Oauth user={}", oauthUser);
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		//Lets Create a User Account
		MLPUserLoginProvider mlpOauthUser = new MLPUserLoginProvider();
		mlpOauthUser.setUserId(oauthUser.getUserId());
		mlpOauthUser.setProviderCode(oauthUser.getProviderCd());
		mlpOauthUser.setProviderUserId(oauthUser.getProviderUserId());
		mlpOauthUser.setRank(oauthUser.getRank());
		mlpOauthUser.setDisplayName(oauthUser.getDisplayName());
		mlpOauthUser.setProfileUrl(oauthUser.getProfileURL());
		mlpOauthUser.setImageUrl(oauthUser.getImageURL());
		mlpOauthUser.setSecret(oauthUser.getSecret());
		mlpOauthUser.setAccessToken(oauthUser.getAccessToken());
		mlpOauthUser.setRefreshToken(oauthUser.getRefreshToken());
		//Need to fix it once we have setter method
		//mlpOauthUser.setExpired(oauthUser.getExpireTime());
		mlpOauthUser.setCreated(oauthUser.getCreatedDate());
		mlpOauthUser.setModified(oauthUser.getModifiedDate());
		
		mlpOauthUser = dataServiceRestClient.createUserLoginProvider(mlpOauthUser);		
		if(mlpOauthUser!=null) {		
		oauthUser = PortalUtils.convertToOathUser(mlpOauthUser);
		}else {
			log.debug("Error in creating UserLoginProvider, OauthUserServiceImpl: save Oauth user={}", oauthUser);
			oauthUser=null; //let us set the return to null
		}
		
		return oauthUser;
	}

	@Override
	public List<OauthUser> getAllUser() {
		
		
		return null;
		

	}
	
	
	
	@Override
	public boolean delete() {
		return false;
		// TODO Auto-generated method stub

	}

		

	@Override
	public MLPUserLoginProvider login(String emailId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MLPUserLoginProvider findUserByEmail(String emailId) {
		log.debug("OauthUserServiceImpl:findUserByEmail ={}", emailId);
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		MLPUserLoginProvider oauthUser = null;
		
		List<MLPUserLoginProvider> mlpUserLoginProviderList = dataServiceRestClient.getUserLoginProviders(emailId);
		for(MLPUserLoginProvider user : mlpUserLoginProviderList) {
			if(user != null) {
				if(!PortalUtils.isEmptyOrNullString(user.getProviderUserId()) && user.getProviderUserId().equalsIgnoreCase(emailId)) {
					oauthUser = user;
					break;
				}
					
			}
		}
		
		return oauthUser;
	}

	@Override
	public MLPUserLoginProvider findUserByUsername(String username) {
		// TODO Auto-generated method stub
		return null;
	}

	/*@Override
	public String getGitHubToken(String code) throws ClientProtocolException, IOException {
		HttpPost tokenPost = new HttpPost(GITHUB_TOKEN_URL);
		CloseableHttpClient httpclient = HttpClients.createDefault();
		tokenPost.setHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");
		tokenPost.setHeader(HttpHeaders.ACCEPT, "application/json");
		String payload = "code="+code+"&scope=&client_id=be9d253aac96d4c9627a"
				+ "&client_secret=57ca76e5f5efd1f19c1d342aa719d25b068a2514&grant_type=authorization_code";
		tokenPost.setEntity(new StringEntity(payload, "UTF-8"));
		CloseableHttpResponse resp = httpclient.execute(tokenPost);
		
		HttpEntity respEntity = resp.getEntity();
		String result = PortalUtils.convertStreamToString(respEntity.getContent());

		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> responseToken = mapper.readValue(result, Map.class);
		String access_token = (String) responseToken.get("access_token");
		if(access_token != null) {
			Map<String, String> token_map = new HashMap<String, String>();
		}
		return access_token;
	}*/

}
