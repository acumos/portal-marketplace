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

package org.acumos.portal.be.transport;

import java.time.Instant;


public class OauthUser extends AbstractResponseObject {
	
	private String userId;
	private String providerCd;
	private String providerUserId;
	private int rank;
	private String displayName;
	private String profileURL;
	private String imageURL;
	private String secret;
	private String accessToken;
	private String refreshToken;
	private Instant expireTime;
	private Instant createdDate;
	private Instant modifiedDate;
	

	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	/**
	 * Default Constructor
	 */
	public OauthUser() {
	}
	/**
	 * 
	 * Clone of the User Object
	 * @param userCopy object to copy
	 * 
	 */
	public OauthUser(OauthUser userCopy) {
				
		this.userId = userCopy.getUserId();
		this.providerCd = userCopy.getProviderCd();
		this.providerUserId = userCopy.getProviderUserId();
		this.rank = userCopy.getRank();
		this.displayName = userCopy.getDisplayName();
		this.profileURL = userCopy.getProfileURL();
		this.imageURL = userCopy.getImageURL();
		this.secret = userCopy.getSecret();
		this.accessToken = userCopy.getAccessToken();
		this.refreshToken = userCopy.getRefreshToken();
		this.expireTime = userCopy.getExpireTime();
		this.createdDate = userCopy.getCreatedDate();
		this.modifiedDate = userCopy.getModifiedDate();
		
		
	}
	
	
	
	
	/**
	 * @return the providerCd
	 */
	public String getProviderCd() {
		return providerCd;
	}
	/**
	 * @param providerCd the providerCd to set
	 */
	public void setProviderCd(String providerCd) {
		this.providerCd = providerCd;
	}
	/**
	 * @return the providerUserId
	 */
	public String getProviderUserId() {
		return providerUserId;
	}
	/**
	 * @param providerUserId the providerUserId to set
	 */
	public void setProviderUserId(String providerUserId) {
		this.providerUserId = providerUserId;
	}
	/**
	 * @return the rank
	 */
	public int getRank() {
		return rank;
	}
	/**
	 * @param rank the rank to set
	 */
	public void setRank(int rank) {
		this.rank = rank;
	}
	/**
	 * @return the displayName
	 */
	public String getDisplayName() {
		return displayName;
	}
	/**
	 * @param displayName the displayName to set
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	/**
	 * @return the profileURL
	 */
	public String getProfileURL() {
		return profileURL;
	}
	/**
	 * @param profileURL the profileURL to set
	 */
	public void setProfileURL(String profileURL) {
		this.profileURL = profileURL;
	}
	/**
	 * @return the imageURL
	 */
	public String getImageURL() {
		return imageURL;
	}
	/**
	 * @param imageURL the imageURL to set
	 */
	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}
	/**
	 * @return the secret
	 */
	public String getSecret() {
		return secret;
	}
	/**
	 * @param secret the secret to set
	 */
	public void setSecret(String secret) {
		this.secret = secret;
	}
	/**
	 * @return the accessToken
	 */
	public String getAccessToken() {
		return accessToken;
	}
	/**
	 * @param accessToken the accessToken to set
	 */
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	/**
	 * @return the refreshToken
	 */
	public String getRefreshToken() {
		return refreshToken;
	}
	/**
	 * @param refreshToken the refreshToken to set
	 */
	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
	/**
	 * @return the expireTime
	 */
	public Instant getExpireTime() {
		return expireTime;
	}
	/**
	 * @param expireTime the expireTime to set
	 */
	public void setExpireTime(Instant expireTime) {
		this.expireTime = expireTime;
	}
	/**
	 * @return the createdDate
	 */
	public Instant getCreatedDate() {
		return createdDate;
	}
	/**
	 * @param createdDate the createdDate to set
	 */
	public void setCreatedDate(Instant createdDate) {
		this.createdDate = createdDate;
	}
	/**
	 * @return the modifiedDate
	 */
	public Instant getModifiedDate() {
		return modifiedDate;
	}
	/**
	 * @param modifiedDate the modifiedDate to set
	 */
	public void setModifiedDate(Instant modifiedDate) {
		this.modifiedDate = modifiedDate;
	}
	
	
	@Override
	public String toString() {
		return "OAUTHUser [userId=" + userId + ", providerCd=" + providerCd + ", providerUserId(emailId)=" + providerUserId + ", rank="
				+ rank + ", displayName=" + displayName + ", profileURL=" + profileURL + ", imageURL="
				+ imageURL + ", secret=" + secret + ", accessToken="+ accessToken + ", refreshToken="+ refreshToken + ",expireTime="+ expireTime + ",createdDate="+ createdDate + ",modifiedDate="+ modifiedDate + "]";
	}
	
}
