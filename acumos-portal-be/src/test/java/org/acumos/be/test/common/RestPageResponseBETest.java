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
package org.acumos.be.test.common;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.Future;

import org.acumos.cds.domain.MLPComment;
import org.acumos.cds.domain.MLPSolution;
import org.acumos.cds.domain.MLPThread;
import org.acumos.portal.be.common.RestPageResponseBE;
import org.acumos.portal.be.transport.MLRequest;
import org.acumos.portal.be.transport.User;
import org.junit.Test;

import org.junit.Assert;

public class RestPageResponseBETest {

	@Test
	public void testRestPageResponseBEParameter(){
		
		String jwtToken = "jwtToken";
		//AbstractResponseObject responseObject;
		//Future<String> async;
		//Set<String> allTagsSet;	
		List<String> tags = new ArrayList<String>();
		tags.add("tag-1");
		tags.add("tag-2");
		//List<HashMap<String,String>> prefTags;
		//Set<String> filteredTagSet;
		List<User> userList = new ArrayList<User>();
		userList.add(new User());
		
		int privateModelCount = 0;
	    int publicModelCount = 0;
	    int companyModelCount = 0;
	    int deletedModelCount = 0; 
	    List<MLPThread> threads = new ArrayList<MLPThread>();
	    threads.add(new MLPThread());
	    
	    List<MLPComment> commentsList = new ArrayList<MLPComment>();
	    commentsList.add(new MLPComment());
	    
	    long commentsCount = 0;
	    long threadCount = 0;
	    int totalElements = 0;
	    List<MLPSolution> modelsSharedWithUser = new ArrayList<MLPSolution>();
	    modelsSharedWithUser.add(new MLPSolution());
	    
	    List<MLRequest> requestList = new ArrayList<MLRequest>();
	    requestList.add(new MLRequest());
	    
	    int pageCount = 0;
	    
	    RestPageResponseBE restResponseBE = new RestPageResponseBE();
	    restResponseBE.setJwtToken(jwtToken);
	    restResponseBE.setTags(tags);
	    restResponseBE.setUserList(userList);
	    restResponseBE.setPrivateModelCount(privateModelCount);
	    restResponseBE.setPublicModelCount(publicModelCount);
	    restResponseBE.setCompanyModelCount(companyModelCount);
	    restResponseBE.setDeletedModelCount(deletedModelCount);
	    restResponseBE.setThreads(threads);
	    restResponseBE.setCommentsList(commentsList);
	    restResponseBE.setCommentsCount(commentsCount);
	    restResponseBE.setThreadCount(threadCount);
	    restResponseBE.setTotalElements(totalElements);
	    restResponseBE.setModelsSharedWithUser(modelsSharedWithUser);
	    restResponseBE.setRequestList(requestList);
	    restResponseBE.setPageCount(pageCount);
	    
	    
	    Assert.assertEquals(jwtToken, restResponseBE.getJwtToken());
	    Assert.assertEquals(tags, restResponseBE.getTags());
	    Assert.assertEquals(userList, restResponseBE.getUserList());
	    Assert.assertEquals(privateModelCount, restResponseBE.getPrivateModelCount());
	    Assert.assertEquals(publicModelCount, restResponseBE.getPublicModelCount());
	    Assert.assertEquals(companyModelCount, restResponseBE.getCompanyModelCount());
	    Assert.assertEquals(deletedModelCount, restResponseBE.getDeletedModelCount());
	    Assert.assertEquals(threads, restResponseBE.getThreads());
	    Assert.assertEquals(commentsList, restResponseBE.getCommentsList());
	    Assert.assertEquals(commentsCount, restResponseBE.getCommentsCount());
	    Assert.assertEquals(threadCount, restResponseBE.getThreadCount());
	    Assert.assertEquals(totalElements, restResponseBE.getTotalElements());
	    Assert.assertEquals(modelsSharedWithUser, restResponseBE.getModelsSharedWithUser());
	    Assert.assertEquals(pageCount, restResponseBE.getPageCount());
	    Assert.assertEquals(requestList, restResponseBE.getRequestList());
	    
	    
	}
}
