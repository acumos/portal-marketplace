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

import java.time.Instant;

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
import org.acumos.portal.be.transport.MLComment;
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
		//Negative Scenario 
		MLPThread thread1 = new MLPThread();
		thread1.setThreadId(null);
		thread1.setTitle(null);
		thread1.setRevisionId(null);
		thread1.setSolutionId(null);
		JsonRequest<MLPThread> mlpthread1 = new JsonRequest<>();
		mlpthread1.setBody(thread1);
		threadService.createThread(thread1);
		JsonResponse<MLPThread> mlpthreadRes1 = threadController.createThread(request, mlpthread1 , response);
	}
	
	@Test
	public void updateThreadTest() throws AcumosServiceException{
		
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
		
		//Negative Scenario 
				MLPThread thread1 = new MLPThread();
				thread1.setThreadId(null);
				thread1.setTitle(null);
				thread1.setRevisionId(null);
				thread1.setSolutionId(null);
				JsonRequest<MLPThread> mlpthread1 = new JsonRequest<>();
				mlpthread1.setBody(thread1);
				threadService.updateThread(thread1);
				JsonResponse<MLPThread> mlpthreadRes = threadController.updateThread(request, mlpthread1 , response);
	}
	
	@Test
	public void deleteThreadTest() throws AcumosServiceException{
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
		
		//Negative Scenario 
		
		String threadId = null;
		threadService.deleteThread(threadId );
		JsonResponse<MLPThread> mlpthreadRes1 = threadController.deleteThread(request, threadId , response);
	}
	
	@Test
	public void getThreadTest() throws AcumosServiceException{
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
		//Negative Scenario 
		
				String threadId = null;
				threadService.getThread(threadId );
				JsonResponse<MLPThread> mlpthreadRes = threadController.getThread(request, threadId , response);
	}
	
	@Test
	public void createCommentTest() throws AcumosServiceException{
		try {
			MLPComment mlpcomment = new MLPComment();
			mlpcomment.setCommentId("8ccf5de8-a565-4f57-a36a-7341b5c7cf9b");
			Instant created = Instant.now();
			mlpcomment.setCreated(created);
			Instant modified = Instant.now();
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
		//Negative Scenario
		MLPComment mlpcomment1 = new MLPComment();
		mlpcomment1.setCommentId(null);
		mlpcomment1.setCreated(null);
		mlpcomment1.setModified(null );
		mlpcomment1.setParentId(null);
		mlpcomment1.setText(null);
		mlpcomment1.setThreadId(null);
		mlpcomment1.setUserId(null);
		threadService.createComment(mlpcomment1);
		JsonRequest<MLPComment> mlpCommentReq1 = new JsonRequest<>();
		JsonResponse<MLPComment> commentRes1 = threadController.createComment(request, mlpCommentReq1 , response);
	}
	
	@Test
	public void updateCommentTest() throws AcumosServiceException {
		try {
			MLPComment mlpcomment = new MLPComment();
			mlpcomment.setCommentId("8ccf5de8-a565-4f57-a36a-7341b5c7cf9b");
			Instant created = Instant.now();
			mlpcomment.setCreated(created);
			Instant modified = Instant.now();
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
		
		//Negative Scenario
				MLPComment mlpcomment1 = new MLPComment();
				mlpcomment1.setCommentId(null);
				mlpcomment1.setCreated(null);
				mlpcomment1.setModified(null );
				mlpcomment1.setParentId(null);
				mlpcomment1.setText(null);
				mlpcomment1.setThreadId(null);
				mlpcomment1.setUserId(null);
				threadService.updateComment(mlpcomment1);
				JsonRequest<MLPComment> mlpCommentReq = new JsonRequest<>();
				JsonResponse<MLPComment> commentRes = threadController.updateComment(request, mlpCommentReq, response);
	}
	
	@Test
	public void deleteCommentTest() throws AcumosServiceException{
		try {
			MLPComment mlpcomment = new MLPComment();
			mlpcomment.setCommentId("8ccf5de8-a565-4f57-a36a-7341b5c7cf9b");
			Instant created = Instant.now();
			mlpcomment.setCreated(created);
			Instant modified = Instant.now();
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
		//Negative Scenario
		String threadId= null;
		String commentId= null;
		threadService.deleteComment(commentId,threadId);
		JsonResponse<MLPComment> commentRes = threadController.deleteComment(request, threadId, commentId, response);
	}
	
	@Test
	public void getCommentTest() throws AcumosServiceException{
		try {
			MLPComment mlpcomment = new MLPComment();
			mlpcomment.setCommentId("8ccf5de8-a565-4f57-a36a-7341b5c7cf9b");
			Instant created = Instant.now();
			mlpcomment.setCreated(created);
			Instant modified = Instant.now();
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
		
		//Negative Scenario
				String threadId= null;
				String commentId= null;
				threadService.getComment(commentId,threadId);
				JsonResponse<MLPComment> commentRes = threadController.getComment(request, threadId, commentId, response);
	}
	
	@Test
	public void getThreadsTest() throws AcumosServiceException{
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
		//Negative Scenario
		RestPageRequest body = new RestPageRequest();
		body.setPage(null);
		body.setSize(null);
		JsonRequest<RestPageRequest> restPageReq = new  JsonRequest<>();
		restPageReq.setBody(null);
		threadService.getThreads(restPageReq);
		JsonResponse<RestPageResponseBE> mlpthreadRes = threadController.getThreads(restPageReq);
	}
	
	@Test
	public void getThreadCommentsTest() throws AcumosServiceException{
		try {
			MLPComment mlpcomment = new MLPComment();
			mlpcomment.setCommentId("8ccf5de8-a565-4f57-a36a-7341b5c7cf9b");
			Instant created = Instant.now();
			mlpcomment.setCreated(created);
			Instant modified = Instant.now();
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
		
		//Negative Scenario
		String threadId= null;
		RestPageRequest body = new RestPageRequest();
		body.setPage(null);
		body.setSize(null);
		JsonRequest<RestPageRequest> restPageReq = new  JsonRequest<>();
		restPageReq.setBody(null);
		threadService.getThreadComments(threadId,body);
		JsonResponse<RestPageResponseBE> mlpthreadRes = threadController.getThreadComments(threadId, restPageReq);
	}
	
	@Test
	public void getThreadCountTest() throws AcumosServiceException{
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
		
		//Negative Scenario
				String threadId= null;
				RestPageRequest body = new RestPageRequest();
				body.setPage(null);
				body.setSize(null);
				JsonRequest<RestPageRequest> restPageReq = new  JsonRequest<>();
				restPageReq.setBody(null);
				threadService.getThreadCount();
				JsonResponse<RestPageResponseBE> mlpthreadRes = threadController.getThreadCount();
	}
	
	@Test
	public void getThreadCommentsCountTest() throws AcumosServiceException{
		try {
			MLPComment mlpcomment = new MLPComment();
			mlpcomment.setCommentId("8ccf5de8-a565-4f57-a36a-7341b5c7cf9b");
			Instant created = Instant.now();
			mlpcomment.setCreated(created);
			Instant modified = Instant.now();
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
		
		//Negative Scenario
		String threadId= null;
		RestPageRequest body = new RestPageRequest();
		body.setPage(null);
		body.setSize(null);
		JsonRequest<RestPageRequest> restPageReq = new  JsonRequest<>();
		restPageReq.setBody(null);
		threadService.getThreadCommentsCount(threadId);
		JsonResponse<RestPageResponseBE> mlpthreadRes = threadController.getThreadCommentsCount(threadId);
	}
	
	@Test
	public void getSolutionRevisionThreadsTest() throws AcumosServiceException{
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
		//Negative Scenario
				String threadId= null;
				RestPageRequest body = new RestPageRequest();
				body.setPage(null);
				body.setSize(null);
				JsonRequest<RestPageRequest> restPageReq = new  JsonRequest<>();
				restPageReq.setBody(null);
				String solutionId = null;
				String revisionId = null;
				threadService.getSolutionRevisionThreads(solutionId, revisionId, body);
				JsonResponse<RestPageResponseBE<MLPThread>> mlpthreadRes = threadController.getSolutionRevisionThreads(solutionId, revisionId, restPageReq);
	}
	
	@Test
	public void getSolutionRevisionCommentsTest() throws AcumosServiceException{
		try {
			MLPComment mlpcomment = new MLPComment();
			mlpcomment.setCommentId("8ccf5de8-a565-4f57-a36a-7341b5c7cf9b");
			Instant created = Instant.now();
			mlpcomment.setCreated(created);
			Instant modified = Instant.now();
			mlpcomment.setModified(modified);
			mlpcomment.setParentId("8ccf5de8-a565-4f57-a36a-7341b5c7cf9b");
			mlpcomment.setText("CommentsTitile");
			mlpcomment.setThreadId("733fc1a3-c8ea-4d63-8daa-ff823c611067");
			mlpcomment.setUserId("41058105-67f4-4461-a192-f4cb7fdafd34");
			String threadId= mlpcomment.getThreadId();
			String solutionId = "6e5036e0-6e20-4425-bd9d-b4ce55cfd8a4";
			String revisionId = "90361063-3ee0-434b-85da-208a8be6856d";
			String clientTimeZone="Asia%2FKolkata";
			RestPageRequest pageRequest = new RestPageRequest();
			pageRequest.setPage(0);
			pageRequest.setSize(9);
			JsonRequest<RestPageRequest> restPageReq = new  JsonRequest<>();
			restPageReq.setBody(pageRequest);
			threadService.getSolutionRevisionComments(solutionId, revisionId,clientTimeZone, pageRequest);
            JsonResponse<RestPageResponseBE<MLComment>> mlpthreadRes = threadController.getSolutionRevisionComments(solutionId, revisionId, restPageReq, clientTimeZone);
			if(mlpthreadRes != null){
				logger.debug(EELFLoggerDelegate.debugLogger, "getSolutionRevisionCommentsTest :  ");
			}
		} catch (AcumosServiceException e) {
				logger.error(EELFLoggerDelegate.errorLogger, "Exception Occurred getSolutionRevisionCommentsTest :",e);
		}
		
		//Negative Scenario
		String threadId= null;
		RestPageRequest body = new RestPageRequest();
		body.setPage(null);
		body.setSize(null);
		JsonRequest<RestPageRequest> restPageReq = new  JsonRequest<>();
		restPageReq.setBody(null);
		String solutionId = null;
		String revisionId = null;
		String clientTimeZone=null;
		threadService.getSolutionRevisionComments(solutionId, revisionId,clientTimeZone, body);
        JsonResponse<RestPageResponseBE<MLComment>> mlpthreadRes = threadController.getSolutionRevisionComments(solutionId, revisionId, restPageReq, clientTimeZone);
		
	}
}
