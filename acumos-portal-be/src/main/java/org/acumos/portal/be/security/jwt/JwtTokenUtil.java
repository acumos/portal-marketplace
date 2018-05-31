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

package org.acumos.portal.be.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import org.acumos.portal.be.security.AuthenticationTokenFilter;
import org.acumos.portal.be.service.UserRoleService;
import org.acumos.portal.be.transport.MLRole;
import org.acumos.portal.be.transport.MLRoleFunction;
import org.acumos.portal.be.util.EELFLoggerDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
/*import org.springframework.mobile.device.Device;*/
import org.springframework.stereotype.Component;

import org.acumos.cds.domain.MLPUser;
import com.github.dockerjava.api.model.Device;
import org.acumos.cds.domain.MLPRole;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class JwtTokenUtil {
	
	private static final EELFLoggerDelegate log = EELFLoggerDelegate.getLogger(JwtTokenUtil.class);
	
	
	MLRoleFunction roleFunction = new MLRoleFunction();
	List<MLRole> mlRoles = null;
	MLPUser userDetails = new MLPUser();
	
	@Autowired
    private UserRoleService roleService;
	
	@Autowired
	private Environment env;

	private static final long serialVersionUID = -3301605591108950415L;

	 String CLAIM_KEY_USERNAME = "sub";
	 String CLAIM_KEY_AUDIENCE = "audience";
	 String CLAIM_KEY_CREATED = "created";
	 String CLAIM_KEY_EXPIRED = "exp";
	 String CLAIM_KEY_ROLE = "role";
	 String CLAIM_KEY_FIRST_NAME = "firstname";
	 String CLAIM_KEY_LAST_NAME = "lastname";
	 String CLAIM_KEY_USERID = "userid";
	 String CLAIM_KEY_ORGNAME = "orgname";
	 String CLAIM_KEY_EMAIL = "email";
	 String CLAIM_KEY_PASSWORD = "password";
	 String CLAIM_KEY_LASTLOGINDATE = "lastlogin";
	 String CLAIM_KEY_MODIFIEDDATE = "modified";
	 String CLAIM_KEY_MLPUSER = "mlpuser";
	
	 String AUDIENCE_UNKNOWN = "unknown";
	 String AUDIENCE_WEB = "web";
	 String AUDIENCE_MOBILE = "mobile";
	 String AUDIENCE_TABLET = "tablet";
	
	

	private TimeProvider timeProvider = new TimeProvider();

	// @Value("${jwt.secret}")
	//String secret = "secret"; //env.getProperty("jwt.auth.secret.key");

	// @Value("${jwt.expiration}")
	private Long expiration;

	public String getUsernameFromToken(String token) {	
		String username;
		try {
			final Claims claims = getClaimsFromToken(token);
			username = claims.getSubject();
		} catch (Exception e) {
			username = null;
		}
		return username;
	}

	public Date getCreatedDateFromToken(String token) {
		Date created;
		try {
			final Claims claims = getClaimsFromToken(token);
			created = new Date((Long) claims.get(CLAIM_KEY_CREATED));
		} catch (Exception e) {
			created = null;
		}
		return created;
	}

	public Date getExpirationDateFromToken(String token) {
		Date expiration;
		try {
			final Claims claims = getClaimsFromToken(token);
			expiration = claims.getExpiration();
		} catch (Exception e) {
			expiration = null;
		}
		return expiration;
	}

	 Claims getClaimsFromToken(String token) {
		Claims claims = null;
		try {
			String secret = env.getProperty("jwt.auth.secret.key");
			claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
		} catch (Exception e) {
			
			claims = null;
		}
		return claims;
	}

	 public Boolean isTokenExpired(String token) {
		final Date expiration = getExpirationDateFromToken(token);
		if(expiration == null) 
			return true;
		return expiration.before(timeProvider.now());
	}

	private Boolean isCreatedBeforeLastPasswordReset(Date created, Date lastPasswordReset) {
		return (lastPasswordReset != null && created.before(lastPasswordReset));
	}

	/*
	 * private String generateAudience(Device device) { String audience =
	 * AUDIENCE_UNKNOWN; if (device.isNormal()) { audience = AUDIENCE_WEB; }
	 * else if (device.isTablet()) { audience = AUDIENCE_TABLET; } else if
	 * (device.isMobile()) { audience = AUDIENCE_MOBILE; } return audience; }
	 */

	private Boolean ignoreTokenExpiration(String token) {
		String audience = getAudienceFromToken(token);
		return (AUDIENCE_TABLET.equals(audience) || AUDIENCE_MOBILE.equals(audience));
	}

	public String generateToken(MLPUser userDetails, Device device) {
		Map<String, Object> claims = new HashMap<>();

		
		claims.put(CLAIM_KEY_USERNAME, userDetails.getLoginName());
		//remove fields which are not part of claim
		userDetails.setPicture(null);
		userDetails.setAuthToken(null);
		claims.put(CLAIM_KEY_MLPUSER,userDetails);
		try{
		List<MLRole> mlRoles = roleService.getRolesForUser(userDetails.getUserId());
		claims.put(CLAIM_KEY_ROLE,mlRoles );
		}catch(Exception e){ }
		// claims.put(CLAIM_KEY_AUDIENCE, generateAudience(device));

		
		Date createdDate = timeProvider.now();

		try {
			claims.put(CLAIM_KEY_CREATED, createdDate);

		} catch (Exception e) {
			
		}

		return doGenerateToken(userDetails,claims);
	}

	private String doGenerateToken(MLPUser userdetails,Map<String, Object> claims) {
		Date expirationDate = null;
		Date createdDate = (Date) claims.get(CLAIM_KEY_CREATED);

		//long createdTime = createdDate.getTime();
		//long expirationTime = createdTime + 100000;
		//expirationDate = new Date(expirationTime);

		try {
			//expirationDate = new Date(createdDate.getTime() + 100000);
			expirationDate = new Date(createdDate.getTime() + (1000 * 60 * 30)); 
		} catch (Exception e) {

			

		}
		String jwtToken = "";
		String secret = env.getProperty("jwt.auth.secret.key");
		jwtToken = Jwts.builder().setClaims(claims).setSubject(userdetails.getLoginName()).setExpiration(expirationDate)
				.signWith(SignatureAlgorithm.HS512, secret).compact();
		return jwtToken;
	}

	public Boolean canTokenBeRefreshed(String token, Date lastPasswordReset) {
		final Date created = getCreatedDateFromToken(token);
		return !isCreatedBeforeLastPasswordReset(created, lastPasswordReset)
				&& (!isTokenExpired(token) || ignoreTokenExpiration(token));
	}

	/*public String refreshToken(String token) {
		String refreshedToken;
		try {
			final Claims claims = getClaimsFromToken(token);
			claims.put(CLAIM_KEY_CREATED, timeProvider.now());
			refreshedToken = doGenerateToken(claims);
		} catch (Exception e) {
			refreshedToken = null;
		}
		return refreshedToken;
	}*/

	public String getAudienceFromToken(String token) {

		String audience;
		try {
			final Claims claims = getClaimsFromToken(token);
			audience = (String) claims.get(CLAIM_KEY_AUDIENCE);
		} catch (Exception e) {
			audience = null;
		}
		return audience;
	}

	public Boolean validateToken(String token, MLPUser userDetails) {
		MLPUser user = (MLPUser) userDetails;
		//log.debug("####### 220 Token Util ########### userDetails : " + userDetails.getUserId());
		final String username = getUsernameFromToken(token);
		final Date created = getCreatedDateFromToken(token);
		// final Date expiration = getExpirationDateFromToken(token);
		//log.debug("####### 228 Check 1  ########### username : " + username.equals(user.getFirstName()));
		//log.debug("####### 228 Check 1  ########### isTokenExpired : " + !isTokenExpired(token));
		//log.debug("####### 228 Check 1  ########### isCreatedBeforeLastPasswordReset : " + !isCreatedBeforeLastPasswordReset(created, user.getLoginPassExpire()));
		
		
		return (username.equals(user.getLoginName()) && !isTokenExpired(token))
				&& !isCreatedBeforeLastPasswordReset(created, user.getLoginPassExpire());
	}
}
