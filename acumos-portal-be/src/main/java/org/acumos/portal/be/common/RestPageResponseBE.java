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

package org.acumos.portal.be.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

import org.acumos.cds.domain.MLPComment;
import org.acumos.cds.domain.MLPSolution;
import org.acumos.cds.domain.MLPThread;
import org.acumos.portal.be.transport.AbstractResponseObject;
import org.acumos.portal.be.transport.MLRequest;
import org.acumos.portal.be.transport.User;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;


public class RestPageResponseBE<T> extends PageImpl<T>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String jwtToken;
	private AbstractResponseObject responseObject;
	private Future<String> async;
	private Set<String> allTagsSet;	
	private List<String> tags;	
	private List<HashMap<String,String>> prefTags;
	private Set<String> filteredTagSet;
	private List<User> userList;
	private int PrivateModelCount;
    private int PublicModelCount;
    private int CompanyModelCount;
    private int DeletedModelCount; 
    private List<MLPThread> threads;
    private List<MLPComment> commentsList;
    private long commentsCount;
    private long threadCount;
    private int totalElements;
    private List<MLPSolution> modelsSharedWithUser;
    private List<MLRequest> requestList;
    private int pageCount;

	public RestPageResponseBE() {
		super(new ArrayList<T>(), PageRequest.of(0, 10), 0);
	}

	public RestPageResponseBE(List<T> content) {
		super(content, PageRequest.of(0, (content.size() > 0) ? content.size() : 10),
				content.size());
		// TODO Auto-generated constructor stub
	}

	public RestPageResponseBE(List<T> content, Pageable pageable, long total) {
		super(content, pageable, total);
		// TODO Auto-generated constructor stub
	}
	
	@JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
	@JsonIgnoreProperties(ignoreUnknown = true)
    public RestPageResponseBE(@JsonProperty("content") List<T> content, @JsonProperty("number") int number, @JsonProperty("size") int size,
                    @JsonProperty("totalElements") Long totalElements, @JsonProperty("pageable") JsonNode pageable, @JsonProperty("last") boolean last,
                    @JsonProperty("totalPages") int totalPages, @JsonProperty("sort") JsonNode sort, @JsonProperty("first") boolean first,
                    @JsonProperty("numberOfElements") int numberOfElements) {
        super(content, PageRequest.of(number, size), totalElements);
    }

	public String getJwtToken() {
		return jwtToken;
	}

	public void setJwtToken(String jwtToken) {
		this.jwtToken = jwtToken;
	}

	public AbstractResponseObject getResponseObject() {
		return responseObject;
	}

	public void setResponseObject(AbstractResponseObject responseObject) {
		this.responseObject = responseObject;
	}
	
	public Set<String> getAllTagsSet() {
		return allTagsSet;
	}

	public void setAllTagsSet(Set<String> allTagsSet) {
		this.allTagsSet = allTagsSet;
	}

	public Set<String> getFilteredTagSet() {
		return filteredTagSet;
	}

	public void setFilteredTagSet(Set<String> filteredTagSet) {
		this.filteredTagSet = filteredTagSet;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public List<User> getUserList() {
		return userList;
	}

	public void setUserList(List<User> userList) {
		this.userList = userList;
	}

	public Future<String> getAsync() {
		return async;
	}

	public void setAsync(Future<String> async) {
		this.async = async;
	}

	public int getPrivateModelCount() {
		return PrivateModelCount;
	}

	public void setPrivateModelCount(int privateModelCount) {
		PrivateModelCount = privateModelCount;
	}

	public int getPublicModelCount() {
		return PublicModelCount;
	}

	public void setPublicModelCount(int publicModelCount) {
		PublicModelCount = publicModelCount;
	}

	public int getCompanyModelCount() {
		return CompanyModelCount;
	}

	public void setCompanyModelCount(int companyModelCount) {
		CompanyModelCount = companyModelCount;
	}

	public int getDeletedModelCount() {
		return DeletedModelCount;
	}

	public void setDeletedModelCount(int deletedModelCount) {
		DeletedModelCount = deletedModelCount;
	}

	public List<MLPThread> getThreads() {
		return threads;
	}

	public void setThreads(List<MLPThread> threads) {
		this.threads = threads;
	}

	public List<MLPComment> getCommentsList() {
		return commentsList;
	}

	public void setCommentsList(List<MLPComment> commentsList) {
		this.commentsList = commentsList;
	}

	public long getCommentsCount() {
		return commentsCount;
	}

	public void setCommentsCount(long commentsCount) {
		this.commentsCount = commentsCount;
	}

	public long getThreadCount() {
		return threadCount;
	}

	public void setThreadCount(long threadCount) {
		this.threadCount = threadCount;
	}

	public long getTotalElements() {
		return totalElements;
	}

	public void setTotalElements(int totalElements) {
		this.totalElements = totalElements;
	}

	public List<MLPSolution> getModelsSharedWithUser() {
		return modelsSharedWithUser;
	}

	public void setModelsSharedWithUser(List<MLPSolution> modelsSharedWithUser) {
		this.modelsSharedWithUser = modelsSharedWithUser;
	}

	public List<MLRequest> getRequestList() {
		return requestList;
	}

	public void setRequestList(List<MLRequest> requestList) {
		this.requestList = requestList;
	}	
	public List<HashMap<String, String>> getPrefTags() {
		return prefTags;
	}
	public void setPrefTags(List<HashMap<String, String>> prefTags) {
		this.prefTags = prefTags;
	}
    
	public int getPageCount() {
		return pageCount;
	}

	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}
}
