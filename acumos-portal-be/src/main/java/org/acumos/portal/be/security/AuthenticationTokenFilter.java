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

package org.acumos.portal.be.security;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.HttpHeaders;

import org.acumos.portal.be.common.exception.MalformedException;
import org.acumos.portal.be.security.jwt.JwtTokenUtil;
import org.acumos.portal.be.security.jwt.TokenValidation;
import org.acumos.portal.be.service.UserService;
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
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.acumos.cds.domain.MLPRole;
import org.acumos.cds.domain.MLPUser;

public class AuthenticationTokenFilter extends UsernamePasswordAuthenticationFilter {


	private static final EELFLoggerDelegate log = EELFLoggerDelegate.getLogger(AuthenticationTokenFilter.class);
	
	 @Autowired
	 private TokenValidation tokenValidation;
	
	  @Autowired
	  private JwtTokenUtil tokenUtils;

	  @Autowired
	  private UserService userService;
	  
	  @Autowired
	  Environment env;
	  
	  final String GOOGLE_USER_INFO_URL = "https://www.googleapis.com/oauth2/v3/userinfo";
	  
	  @Override
	  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		  HttpServletRequest httpRequest = (HttpServletRequest) request;
		  String authToken = null;
	    authToken = httpRequest.getHeader("jwtToken");
	    if(authToken == null )
	    	authToken = httpRequest.getHeader("Authorization");
	    
	    String provider = httpRequest.getHeader("provider");
	    if(provider == null) {
	    	provider = "";
	    }
	    try {
	    if(authToken != null) {
	    	CloseableHttpClient httpclient = HttpClients.createDefault();
	    	switch (provider) {
	    	case "google" : 
	    		//Start google validation
	    		HttpGet getProfile = new HttpGet(GOOGLE_USER_INFO_URL);
	    		
	    		String proxyHost = env.getProperty("proxy.host");
	    		String proxyPortString = env.getProperty("proxy.port");
	    		String proxyProtocol = env.getProperty("proxy.protocol");
	    		if(!StringUtils.isEmpty(proxyHost) && proxyPortString != null && !StringUtils.isEmpty(proxyProtocol)) {
	    			Integer proxyPort = Integer.parseInt(proxyPortString);
	    			HttpHost proxy = new HttpHost(proxyHost, proxyPort, proxyProtocol);

		            RequestConfig config = RequestConfig.custom()
		                    .setProxy(proxy)
		                    .build();
		            getProfile.setConfig(config);
	    		}
	    		
	    		getProfile.setHeader(HttpHeaders.AUTHORIZATION, authToken);
				CloseableHttpResponse resp = httpclient.execute(getProfile);
				HttpEntity respEntity = resp.getEntity();
				String result = PortalUtils.convertStreamToString(respEntity.getContent());
				
				

				ObjectMapper mapper = new ObjectMapper();
				Map<String, Object> profile = mapper.readValue(result, Map.class);
				String email = (String) profile.get("email");
				MLPUser mlpUser = userService.findUserByEmail(email);
				if(mlpUser != null) {
					List<GrantedAuthority> authorityList = AuthorityUtils.commaSeparatedStringToAuthorityList(getRoleAuthority(mlpUser));
    			    UsernamePasswordAuthenticationToken authentication =  new UsernamePasswordAuthenticationToken(new AuthenticatedUserDetails(mlpUser), authToken, authorityList);
    			    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpRequest));
    			    SecurityContextHolder.getContext().setAuthentication(authentication);
				}
				//end google validation
	    		break;
	    		
	    	default : 
	    		authToken = authToken.replace("Bearer ", "");
	    	    String username = this.tokenUtils.getUsernameFromToken(authToken);

	    	    if (username != null && !(SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken)) {
	    	      MLPUser userDetails = this.userService.findUserByUsername(username);
	    			if (this.tokenValidation.tokenRegnerationAndValidation(authToken)) {
	    				List<GrantedAuthority> authorityList = AuthorityUtils.commaSeparatedStringToAuthorityList(getRoleAuthority(userDetails));
	    			    UsernamePasswordAuthenticationToken authentication =  new UsernamePasswordAuthenticationToken(new AuthenticatedUserDetails(userDetails), authToken, authorityList);
	    			    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpRequest));
	    			    SecurityContextHolder.getContext().setAuthentication(authentication);
	    			} 
	    	
				  } else {
					  log.debug("Cannot Validate Token : " + authToken);
					  //response.setHeader(HttpHeaders.WWW_AUTHENTICATE, "Bearer");
				  }
	    	    break;
		
	    	} 
	    } else {
	    	  log.debug("Cannot Find token in Header : Token Validation Failed");
	    	  //response.setHeader(HttpHeaders.WWW_AUTHENTICATE, "Bearer");
	      }
	    } catch (MalformedException e) {
	    	 log.debug("Exception occured while token validation : Token Validation Failed");
		}

	    chain.doFilter(request, response);
	  }
	  
	  private String getRoleAuthority(MLPUser user) {
		  String authority = "MLP System User";
		  List<MLPRole> roles = userService.getUserRole(user.getUserId());
			if(roles != null) {
				MLPRole role = roles.get(0);
				authority = role.getName();
			}
		  return authority;
	  }
}
