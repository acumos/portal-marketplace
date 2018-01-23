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
package org.acumos.be.test.controller;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acumos.cds.domain.MLPComment;
import org.acumos.cds.domain.MLPThread;
import org.acumos.cds.transport.RestPageRequest;
import org.acumos.portal.be.common.JsonRequest;
import org.acumos.portal.be.common.JsonResponse;
import org.acumos.portal.be.common.RestPageResponseBE;
import org.acumos.portal.be.common.exception.AcumosServiceException;
import org.acumos.portal.be.controller.ThreadController;
import org.acumos.portal.be.service.ThreadService;
import org.acumos.portal.be.service.impl.ThreadServiceImpl;
import org.acumos.portal.be.util.EELFLoggerDelegate;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

public class ThreadControllerTest {
	private static final EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(ThreadControllerTest.class);

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	@InjectMocks
	ThreadController threadController;
	@Mock
	ThreadService threadService;
	@Mock
	ThreadServiceImpl threadServiceImpl;
	
	final HttpServletResponse response = new MockHttpServletResponse();
	final HttpServletRequest request = new MockHttpServletRequest();
	
	
	@Test
	public void createThreadTest() throws AcumosServiceException{
		MLPThread thread = new MLPThread();
		thread.setThreadId("733fc1a3-c8ea-4d63-8daa-ff823c611067");
		thread.setTitle("ThreadTitle");
		thread.setRevisionId("90361063-3ee0-434b-85da-208a8be6856d");
		thread.setSolutionId("6e5036e0-6e20-4425-bd9d-b4ce55cfd8a4");
		JsonRequest<MLPThread> mlpthread = new JsonRequest<>();
		mlpthread.setBody(thread);
		threadService.createThread(thread);
		JsonResponse<MLPThread> mlpthreadRes = threadController.createThread(request, mlpthread , response);
		if(mlpthreadRes != null){
			logger.debug(EELFLoggerDelegate.debugLogger, "createThread :  ");
		}else{
			logger.error(EELFLoggerDelegate.errorLogger, "Exception Occurred createThread :");
		}
	}
	
	@Test
	public void updateThreadTest(){
		
		try {
			MLPThread thread = new MLPThread();
			thread.setThreadId("733fc1a3-c8ea-4d63-8daa-ff823c611067");
			thread.setTitle("ThreadTitle");
			thread.setRevisionId("90361063-3ee0-434b-85da-208a8be6856d");
			thread.setSolutionId("6e5036e0-6e20-4425-bd9d-b4ce55cfd8a4");
			JsonRequest<MLPThread> mlpthread = new JsonRequest<>();
			mlpthread.setBody(thread);
			threadService.updateThread(thread);
			JsonResponse<MLPThread> mlpthreadRes = threadController.updateThread(request, mlpthread , response);
			if(mlpthreadRes != null){
				logger.debug(EELFLoggerDelegate.debugLogger, "updateThread :  ");
			}
		} catch (AcumosServiceException e) {
				logger.error(EELFLoggerDelegate.errorLogger, "Exception Occurred updateThread :",e);
		}
	}
	
	@Test
	public void deleteThreadTest(){
		try {
			MLPThread thread = new MLPThread();
			thread.setThreadId("733fc1a3-c8ea-4d63-8daa-ff823c611067");
			thread.setTitle("ThreadTitle");
			thread.setRevisionId("90361063-3ee0-434b-85da-208a8be6856d");
			thread.setSolutionId("6e5036e0-6e20-4425-bd9d-b4ce55cfd8a4");
			String threadId = thread.getThreadId();
			threadService.deleteThread(threadId );
			JsonResponse<MLPThread> mlpthreadRes = threadController.deleteThread(request, threadId , response);
			if(mlpthreadRes != null){
				logger.debug(EELFLoggerDelegate.debugLogger, "updateThread :  ");
			}
		} catch (AcumosServiceException e) {
				logger.error(EELFLoggerDelegate.errorLogger, "Exception Occurred updateThread :",e);
		}
	}
	
	@Test
	public void getThreadTest(){
		try {
			MLPThread thread = new MLPThread();
			thread.setThreadId("733fc1a3-c8ea-4d63-8daa-ff823c611067");
			thread.setTitle("ThreadTitle");
			thread.setRevisionId("90361063-3ee0-434b-85da-208a8be6856d");
			thread.setSolutionId("6e5036e0-6e20-4425-bd9d-b4ce55cfd8a4");
			String threadId = thread.getThreadId();
			threadService.getThread(threadId );
			JsonResponse<MLPThread> mlpthreadRes = threadController.getThread(request, threadId , response);
			if(mlpthreadRes != null){
				logger.debug(EELFLoggerDelegate.debugLogger, "updateThread :  ");
			}
		} catch (AcumosServiceException e) {
				logger.error(EELFLoggerDelegate.errorLogger, "Exception Occurred updateThread :",e);
		}
	}
	
	@Test
	public void createCommentTest(){
		try {
			MLPComment mlpcomment = new MLPComment();
			mlpcomment.setCommentId("8ccf5de8-a565-4f57-a36a-7341b5c7cf9b");
			Date created = new Date();
			mlpcomment.setCreated(created);
			Date modified = new Date();
			mlpcomment.setModified(modified );
			mlpcomment.setParentId("8ccf5de8-a565-4f57-a36a-7341b5c7cf9b");
			mlpcomment.setText("CommentsTitile");
			mlpcomment.setThreadId("733fc1a3-c8ea-4d63-8daa-ff823c611067");
			mlpcomment.setUserId("41058105-67f4-4461-a192-f4cb7fdafd34");
			threadService.createComment(mlpcomment);
			JsonRequest<MLPComment> mlpCommentReq = new JsonRequest<>();
			JsonResponse<MLPComment> commentRes = threadController.createComment(request, mlpCommentReq , response);
			if(commentRes != null){
				logger.debug(EELFLoggerDelegate.debugLogger, "createComment :  ");
			}
		} catch (AcumosServiceException e) {
			logger.error(EELFLoggerDelegate.errorLogger, "Exception Occurred createComment :", e);
		}
	}
	
	@Test
	public void updateCommentTest() {
		try {
			MLPComment mlpcomment = new MLPComment();
			mlpcomment.setCommentId("8ccf5de8-a565-4f57-a36a-7341b5c7cf9b");
			Date created = new Date();
			mlpcomment.setCreated(created);
			Date modified = new Date();
			mlpcomment.setModified(modified);
			mlpcomment.setParentId("8ccf5de8-a565-4f57-a36a-7341b5c7cf9b");
			mlpcomment.setText("CommentsTitile");
			mlpcomment.setThreadId("733fc1a3-c8ea-4d63-8daa-ff823c611067");
			mlpcomment.setUserId("41058105-67f4-4461-a192-f4cb7fdafd34");
			threadService.updateComment(mlpcomment);
			JsonRequest<MLPComment> mlpCommentReq = new JsonRequest<>();
			JsonResponse<MLPComment> commentRes = threadController.updateComment(request, mlpCommentReq, response);
			if (commentRes != null) {
				logger.debug(EELFLoggerDelegate.debugLogger, "updateComment :  ");
			}
		} catch (AcumosServiceException e) {
			logger.error(EELFLoggerDelegate.errorLogger, "Exception Occurred updateComment :", e);
		}
	}
	
	@Test
	public void deleteCommentTest(){
		try {
			MLPComment mlpcomment = new MLPComment();
			mlpcomment.setCommentId("8ccf5de8-a565-4f57-a36a-7341b5c7cf9b");
			Date created = new Date();
			mlpcomment.setCreated(created);
			Date modified = new Date();
			mlpcomment.setModified(modified);
			mlpcomment.setParentId("8ccf5de8-a565-4f57-a36a-7341b5c7cf9b");
			mlpcomment.setText("CommentsTitile");
			mlpcomment.setThreadId("733fc1a3-c8ea-4d63-8daa-ff823c611067");
			mlpcomment.setUserId("41058105-67f4-4461-a192-f4cb7fdafd34");
			String threadId= mlpcomment.getThreadId();
			String commentId= mlpcomment.getCommentId();
			threadService.deleteComment(commentId,threadId);
			JsonResponse<MLPComment> commentRes = threadController.deleteComment(request, threadId, commentId, response);
			if (commentRes != null) {
				logger.debug(EELFLoggerDelegate.debugLogger, "deleteComment :  ");
			}
		} catch (AcumosServiceException e) {
			logger.error(EELFLoggerDelegate.errorLogger, "Exception Occurred deleteComment :", e);
		}
	}
	
	@Test
	public void getCommentTest(){
		try {
			MLPComment mlpcomment = new MLPComment();
			mlpcomment.setCommentId("8ccf5de8-a565-4f57-a36a-7341b5c7cf9b");
			Date created = new Date();
			mlpcomment.setCreated(created);
			Date modified = new Date();
			mlpcomment.setModified(modified);
			mlpcomment.setParentId("8ccf5de8-a565-4f57-a36a-7341b5c7cf9b");
			mlpcomment.setText("CommentsTitile");
			mlpcomment.setThreadId("733fc1a3-c8ea-4d63-8daa-ff823c611067");
			mlpcomment.setUserId("41058105-67f4-4461-a192-f4cb7fdafd34");
			String threadId= mlpcomment.getThreadId();
			String commentId= mlpcomment.getCommentId();
			threadService.getComment(commentId,threadId);
			JsonResponse<MLPComment> commentRes = threadController.getComment(request, threadId, commentId, response);
			if (commentRes != null) {
				logger.debug(EELFLoggerDelegate.debugLogger, "getComment :  ");
			}
		} catch (AcumosServiceException e) {
			logger.error(EELFLoggerDelegate.errorLogger, "Exception Occurred getComment :", e);
		}
	}
	
	@Test
	public void getThreadsTest(){
		try {
			MLPThread thread = new MLPThread();
			thread.setThreadId("733fc1a3-c8ea-4d63-8daa-ff823c611067");
			thread.setTitle("ThreadTitle");
			thread.setRevisionId("90361063-3ee0-434b-85da-208a8be6856d");
			thread.setSolutionId("6e5036e0-6e20-4425-bd9d-b4ce55cfd8a4");
			RestPageRequest body = new RestPageRequest();
			body.setPage(0);
			body.setSize(9);
			JsonRequest<RestPageRequest> restPageReq = new  JsonRequest<>();
			restPageReq.setBody(body);
			threadService.getThreads(restPageReq);
			JsonResponse<RestPageResponseBE> mlpthreadRes = threadController.getThreads(restPageReq);
			if(mlpthreadRes != null){
				logger.debug(EELFLoggerDelegate.debugLogger, "getThreads :  ");
			}
		} catch (AcumosServiceException e) {
				logger.error(EELFLoggerDelegate.errorLogger, "Exception Occurred getThreads :",e);
		}
	}
	
	@Test
	public void getThreadCommentsTest(){
		try {
			MLPComment mlpcomment = new MLPComment();
			mlpcomment.setCommentId("8ccf5de8-a565-4f57-a36a-7341b5c7cf9b");
			Date created = new Date();
			mlpcomment.setCreated(created);
			Date modified = new Date();
			mlpcomment.setModified(modified);
			mlpcomment.setParentId("8ccf5de8-a565-4f57-a36a-7341b5c7cf9b");
			mlpcomment.setText("CommentsTitile");
			mlpcomment.setThreadId("733fc1a3-c8ea-4d63-8daa-ff823c611067");
			mlpcomment.setUserId("41058105-67f4-4461-a192-f4cb7fdafd34");
			String threadId= mlpcomment.getThreadId();
			RestPageRequest body = new RestPageRequest();
			body.setPage(0);
			body.setSize(9);
			JsonRequest<RestPageRequest> restPageReq = new  JsonRequest<>();
			restPageReq.setBody(body);
			threadService.getThreadComments(threadId,body);
			JsonResponse<RestPageResponseBE> mlpthreadRes = threadController.getThreadComments(threadId, restPageReq);
			if(mlpthreadRes != null){
				logger.debug(EELFLoggerDelegate.debugLogger, "getThreadCommentsTest :  ");
			}
		} catch (AcumosServiceException e) {
				logger.error(EELFLoggerDelegate.errorLogger, "Exception Occurred getThreadCommentsTest :",e);
		}
	}
	
	@Test
	public void getThreadCountTest(){
		try {
			MLPThread thread = new MLPThread();
			thread.setThreadId("733fc1a3-c8ea-4d63-8daa-ff823c611067");
			thread.setTitle("ThreadTitle");
			thread.setRevisionId("90361063-3ee0-434b-85da-208a8be6856d");
			thread.setSolutionId("6e5036e0-6e20-4425-bd9d-b4ce55cfd8a4");
			RestPageRequest body = new RestPageRequest();
			body.setPage(0);
			body.setSize(9);
			JsonRequest<RestPageRequest> restPageReq = new  JsonRequest<>();
			restPageReq.setBody(body);
			threadService.getThreadCount();
			JsonResponse<RestPageResponseBE> mlpthreadRes = threadController.getThreadCount();
			if(mlpthreadRes != null){
				logger.debug(EELFLoggerDelegate.debugLogger, "getThreadCountTest :  ");
			}
		} catch (AcumosServiceException e) {
				logger.error(EELFLoggerDelegate.errorLogger, "Exception Occurred getThreadCountTest :",e);
		}
	}
	
	@Test
	public void getThreadCommentsCountTest(){
		try {
			MLPComment mlpcomment = new MLPComment();
			mlpcomment.setCommentId("8ccf5de8-a565-4f57-a36a-7341b5c7cf9b");
			Date created = new Date();
			mlpcomment.setCreated(created);
			Date modified = new Date();
			mlpcomment.setModified(modified);
			mlpcomment.setParentId("8ccf5de8-a565-4f57-a36a-7341b5c7cf9b");
			mlpcomment.setText("CommentsTitile");
			mlpcomment.setThreadId("733fc1a3-c8ea-4d63-8daa-ff823c611067");
			mlpcomment.setUserId("41058105-67f4-4461-a192-f4cb7fdafd34");
			String threadId= mlpcomment.getThreadId();
			RestPageRequest body = new RestPageRequest();
			body.setPage(0);
			body.setSize(9);
			JsonRequest<RestPageRequest> restPageReq = new  JsonRequest<>();
			restPageReq.setBody(body);
			threadService.getThreadCommentsCount(threadId);
			JsonResponse<RestPageResponseBE> mlpthreadRes = threadController.getThreadCommentsCount(threadId);
			if(mlpthreadRes != null){
				logger.debug(EELFLoggerDelegate.debugLogger, "getThreadCommentsCountTest :  ");
			}
		} catch (AcumosServiceException e) {
				logger.error(EELFLoggerDelegate.errorLogger, "Exception Occurred getThreadCommentsCountTest :",e);
		}
	}
	
	@Test
	public void getSolutionRevisionThreadsTest(){
		try {
			MLPThread thread = new MLPThread();
			thread.setThreadId("733fc1a3-c8ea-4d63-8daa-ff823c611067");
			thread.setTitle("ThreadTitle");
			thread.setRevisionId("90361063-3ee0-434b-85da-208a8be6856d");
			thread.setSolutionId("6e5036e0-6e20-4425-bd9d-b4ce55cfd8a4");
			RestPageRequest pageRequest = new RestPageRequest();
			pageRequest.setPage(0);
			pageRequest.setSize(9);
			JsonRequest<RestPageRequest> restPageReq = new  JsonRequest<>();
			restPageReq.setBody(pageRequest);
			String solutionId = "6e5036e0-6e20-4425-bd9d-b4ce55cfd8a4";
			String revisionId = "90361063-3ee0-434b-85da-208a8be6856d";
			threadService.getSolutionRevisionThreads(solutionId, revisionId, pageRequest);
			JsonResponse<RestPageResponseBE<MLPThread>> mlpthreadRes = threadController.getSolutionRevisionThreads(solutionId, revisionId, restPageReq);
			if(mlpthreadRes != null){
				logger.debug(EELFLoggerDelegate.debugLogger, "getSolutionRevisionThreadsTest :  ");
			}
		} catch (AcumosServiceException e) {
				logger.error(EELFLoggerDelegate.errorLogger, "Exception Occurred getSolutionRevisionThreadsTest :",e);
		}
	}
	
	@Test
	public void getSolutionRevisionCommentsTest(){
		try {
			MLPComment mlpcomment = new MLPComment();
			mlpcomment.setCommentId("8ccf5de8-a565-4f57-a36a-7341b5c7cf9b");
			Date created = new Date();
			mlpcomment.setCreated(created);
			Date modified = new Date();
			mlpcomment.setModified(modified);
			mlpcomment.setParentId("8ccf5de8-a565-4f57-a36a-7341b5c7cf9b");
			mlpcomment.setText("CommentsTitile");
			mlpcomment.setThreadId("733fc1a3-c8ea-4d63-8daa-ff823c611067");
			mlpcomment.setUserId("41058105-67f4-4461-a192-f4cb7fdafd34");
			String threadId= mlpcomment.getThreadId();
			String solutionId = "6e5036e0-6e20-4425-bd9d-b4ce55cfd8a4";
			String revisionId = "90361063-3ee0-434b-85da-208a8be6856d";
			RestPageRequest pageRequest = new RestPageRequest();
			pageRequest.setPage(0);
			pageRequest.setSize(9);
			JsonRequest<RestPageRequest> restPageReq = new  JsonRequest<>();
			restPageReq.setBody(pageRequest);
			threadService.getSolutionRevisionComments(solutionId, revisionId, pageRequest);
			JsonResponse<RestPageResponseBE<MLPComment>> mlpthreadRes = threadController.getSolutionRevisionComments(solutionId, revisionId, restPageReq);
			if(mlpthreadRes != null){
				logger.debug(EELFLoggerDelegate.debugLogger, "getSolutionRevisionCommentsTest :  ");
			}
		} catch (AcumosServiceException e) {
				logger.error(EELFLoggerDelegate.errorLogger, "Exception Occurred getSolutionRevisionCommentsTest :",e);
		}
	}
}
