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

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acumos.cds.domain.MLPComment;
import org.acumos.cds.domain.MLPThread;
import org.acumos.cds.transport.RestPageRequest;
import org.acumos.portal.be.APINames;
import org.acumos.portal.be.common.JSONTags;
import org.acumos.portal.be.common.JsonRequest;
import org.acumos.portal.be.common.JsonResponse;
import org.acumos.portal.be.common.RestPageResponseBE;
import org.acumos.portal.be.common.exception.AcumosServiceException;
import org.acumos.portal.be.service.ThreadService;
import org.acumos.portal.be.transport.MLComment;
import org.acumos.portal.be.util.SanitizeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.ApiOperation;

@Controller
@RequestMapping("/")
public class ThreadController extends AbstractController {
	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());	

	@Autowired
	private ThreadService threadService;

	public ThreadController() {
		// TODO Auto-generated constructor stub
	}

	@ApiOperation(value = "Create Thread", response = MLPThread.class)
	@RequestMapping(value = { APINames.CREATE_THREAD }, method = RequestMethod.POST, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<MLPThread> createThread(HttpServletRequest request,
			@RequestBody JsonRequest<MLPThread> mlpthread, HttpServletResponse response) {
		JsonResponse<MLPThread> data = new JsonResponse<>();
		try {
			MLPThread thread = threadService.createThread(mlpthread.getBody());
			data.setResponseBody(thread);
			data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
			data.setResponseDetail("Thread created Successfully");
			log.debug("createThread :  ");
		} catch (AcumosServiceException e) {
			data.setErrorCode(JSONTags.TAG_ERROR_CODE);
			data.setResponseDetail("Exception occured while createThread");
			log.error("Exception Occurred createThread :", e);
		}
		return data;
	}

	@ApiOperation(value = "Update Thread", response = MLPThread.class)
	@RequestMapping(value = { APINames.UPDATE_THREAD }, method = RequestMethod.PUT, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<MLPThread> updateThread(HttpServletRequest request,
			@RequestBody JsonRequest<MLPThread> mlpthread, HttpServletResponse response) {
		JsonResponse<MLPThread> data = new JsonResponse<>();
		try {
			threadService.updateThread(mlpthread.getBody());
			data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
			data.setResponseDetail("Thread Updated Successfully");
			log.debug("updateThread :  ");
		} catch (AcumosServiceException e) {
			data.setErrorCode(JSONTags.TAG_ERROR_CODE);
			data.setResponseDetail("Exception occured while updateThread");
			log.error("Exception Occurred updateThread :", e);
		}
		return data;
	}

	@ApiOperation(value = "Delete Thread", response = MLPThread.class)
	@RequestMapping(value = { APINames.DELETE_THREAD }, method = RequestMethod.POST, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<MLPThread> deleteThread(HttpServletRequest request, @PathVariable String threadId,
			HttpServletResponse response) {
		JsonResponse<MLPThread> data = new JsonResponse<>();
		try {
			threadService.deleteThread(threadId);
			data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
			data.setResponseDetail("Thread deleted Successfully");
			log.debug("deleteThread :  ");
		} catch (AcumosServiceException e) {
			data.setErrorCode(JSONTags.TAG_ERROR_CODE);
			data.setResponseDetail("Exception occured while deleteThread");
			log.error("Exception Occurred deleteThread :", e);
		}
		return data;
	}

	@ApiOperation(value = "Get Thread", response = MLPThread.class)
	@RequestMapping(value = { APINames.GET_THREAD }, method = RequestMethod.GET, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<MLPThread> getThread(HttpServletRequest request, @PathVariable String threadId,
			HttpServletResponse response) {
		
		threadId = SanitizeUtils.sanitize(threadId);
		
		JsonResponse<MLPThread> data = new JsonResponse<>();
		try {
			MLPThread thread = threadService.getThread(threadId);
			data.setResponseBody(thread);
			data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
			data.setResponseDetail("Threads fetched Successfully");
			log.debug("getThread :  ");
		} catch (AcumosServiceException e) {
			data.setErrorCode(JSONTags.TAG_ERROR_CODE);
			data.setResponseDetail("Exception occured while getThread");
			log.error("Exception Occurred getThread :", e);
		}
		return data;
	}

	@ApiOperation(value = "Create Comment", response = MLPComment.class)
	@RequestMapping(value = { APINames.CREATE_COMMENT }, method = RequestMethod.POST, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<MLPComment> createComment(HttpServletRequest request,
			@RequestBody JsonRequest<MLPComment> mlpComment, HttpServletResponse response) {
		JsonResponse<MLPComment> data = new JsonResponse<>();
		try {
			MLPComment comment = threadService.createComment(mlpComment.getBody());
			data.setResponseBody(comment);
			data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
			data.setResponseDetail("Comments created Successfully");
			log.debug("createComment :  ");
		} catch (AcumosServiceException e) {
			data.setErrorCode(JSONTags.TAG_ERROR_CODE);
			data.setResponseDetail("Exception occured while createComment");
			log.error("Exception Occurred createComment :", e);
		}
		return data;
	}

	@ApiOperation(value = "Update Comment", response = MLPComment.class)
	@RequestMapping(value = { APINames.UPDATE_COMMENT }, method = RequestMethod.PUT, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<MLPComment> updateComment(HttpServletRequest request,
			@RequestBody JsonRequest<MLPComment> mlpComment, HttpServletResponse response) {
		JsonResponse<MLPComment> data = new JsonResponse<>();
		try {
			threadService.updateComment(mlpComment.getBody());
			data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
			data.setResponseDetail("Comments updated Successfully");
			log.debug("updateComment :  ");
		} catch (AcumosServiceException e) {
			data.setErrorCode(JSONTags.TAG_ERROR_CODE);
			data.setResponseDetail("Exception occured while updateComment");
			log.error("Exception Occurred updateComment :", e);
		}
		return data;
	}

	@ApiOperation(value = "Delete Comment", response = MLPComment.class)
	@RequestMapping(value = { APINames.DELETE_COMMENT }, method = RequestMethod.POST, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<MLPComment> deleteComment(HttpServletRequest request, @PathVariable String threadId,
			@PathVariable String commentId, HttpServletResponse response) {
		JsonResponse<MLPComment> data = new JsonResponse<>();
		try {
			threadService.deleteComment(threadId, commentId);
			data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
			data.setResponseDetail("Comments deleted Successfully");
			log.debug("deleteComment :  ");
		} catch (AcumosServiceException e) {
			data.setErrorCode(JSONTags.TAG_ERROR_CODE);
			data.setResponseDetail("Exception occured while deleteComment");
			log.error("Exception Occurred deleteComment :", e);
		}
		return data;
	}

	@ApiOperation(value = "Get comments", response = MLPComment.class)
	@RequestMapping(value = { APINames.GET_COMMENT }, method = RequestMethod.GET, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<MLPComment> getComment(HttpServletRequest request, @PathVariable String threadId,
			@PathVariable String commentId, HttpServletResponse response) {
		
		threadId = SanitizeUtils.sanitize(threadId);
		commentId = SanitizeUtils.sanitize(commentId);
		
		JsonResponse<MLPComment> data = new JsonResponse<>();
		try {
			MLPComment comment = threadService.getComment(threadId, commentId);
			data.setResponseBody(comment);
			data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
			data.setResponseDetail("Comments fetched Successfully");
			log.debug("deleteComment :  ");
		} catch (AcumosServiceException e) {
			data.setErrorCode(JSONTags.TAG_ERROR_CODE);
			data.setResponseDetail("Exception occured while deleteComment");
			log.error("Exception Occurred deleteComment :", e);
		}
		return data;
	}

	@ApiOperation(value = "Gets a list of Threads ", response = RestPageResponseBE.class)
	@RequestMapping(value = { APINames.GET_THREADS }, method = RequestMethod.POST, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<RestPageResponseBE> getThreads(@RequestBody JsonRequest<RestPageRequest> restPageReq) {
		log.debug("getThreads");
		List<String> threadList = new ArrayList<>();
		JsonResponse<RestPageResponseBE> data = new JsonResponse<>();
		try {
			threadList = threadService.getThreads(restPageReq);
			if (threadList != null) {
				List test = new ArrayList<>();
				RestPageResponseBE responseBody = new RestPageResponseBE<>(test);
				responseBody.setThreads(threadList);
				data.setResponseBody(responseBody);
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
				data.setResponseDetail("Threads fetched Successfully");
			} else {
				data.setErrorCode(JSONTags.TAG_ERROR_CODE);
				data.setResponseDetail("Exception Occurred Fetching thread");
				log.error("Exception Occurred Fetching thread");
			}
		} catch (AcumosServiceException e) {
			data.setErrorCode(e.getErrorCode());
			data.setResponseDetail(e.getMessage());
			log.error("Exception Occurred Fetching thread", e);
		}
		return data;
	}

	
	@ApiOperation(value = "Gets a list of Threads ", response = RestPageResponseBE.class)
	@RequestMapping(value = { APINames.GET_THREAD_COMMENTS}, method = RequestMethod.POST, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<RestPageResponseBE> getThreadComments(@PathVariable String threadId,@RequestBody JsonRequest<RestPageRequest> restPageReq) {
		log.debug("getThreadComments");
		List<String> commentsList = new ArrayList<>();
		JsonResponse<RestPageResponseBE> data = new JsonResponse<>();
		try {
			commentsList = threadService.getThreadComments(threadId, restPageReq.getBody());
			if (commentsList != null) {
				List test = new ArrayList<>();
				RestPageResponseBE responseBody = new RestPageResponseBE(test);
				responseBody.setCommentsList(commentsList);
				data.setResponseBody(responseBody);
				data.setStatus(true);
				data.setStatusCode(100);
				data.setResponseCode("Success");
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
				data.setResponseDetail("Comments fetched Successfully for particular thread");
			} else {
				data.setErrorCode(JSONTags.TAG_ERROR_CODE);
				data.setResponseDetail("Exception Occurred Fetching thread");
				log.error("Exception Occurred Fetching thread");
			}
		} catch (AcumosServiceException e) {
			data.setErrorCode(e.getErrorCode());
			data.setResponseDetail(e.getMessage());
			log.error("Exception Occurred Fetching thread", e);
		}
		return data;
	}
	
	
	@ApiOperation(value = "Get the count of Threads ", response = MLPThread.class)
	@RequestMapping(value = { APINames.GET_THREADCOUNT}, method = RequestMethod.GET, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<RestPageResponseBE> getThreadCount(){
		log.debug("getThreadCount");
		long threadCount;
		JsonResponse<RestPageResponseBE> data = new JsonResponse<>();
		List content = new ArrayList<>();
		RestPageResponseBE responseBody = new RestPageResponseBE<>(content);
		try {
			threadCount = threadService.getThreadCount();
			responseBody.setThreadCount(threadCount);
			data.setResponseBody(responseBody);
			data.setStatus(true);
			data.setStatusCode(100);
			data.setResponseCode("Success");
			data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
			data.setResponseDetail("Thread count fetched successfully");
		} catch (AcumosServiceException e) {
			data.setErrorCode(e.getErrorCode());
			data.setResponseDetail(e.getMessage());
			log.error("Exception Occurred Fetching thread", e);
		}
		
		return data;
		
	}
	
	
	@ApiOperation(value = "Get the count of Threads ", response = MLPThread.class)
	@RequestMapping(value = { APINames.GET_THREADCOMMENTSCOUNT}, method = RequestMethod.GET, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<RestPageResponseBE> getThreadCommentsCount(@PathVariable String threadId){
		log.debug("getThreadCount");
		long threadCommentCount;
		JsonResponse<RestPageResponseBE> data = new JsonResponse<>();
		List content = new ArrayList<>();
		RestPageResponseBE responseBody = new RestPageResponseBE<>(content);
		try {
			threadCommentCount = threadService.getThreadCommentsCount(threadId);
			responseBody.setCommentsCount(threadCommentCount);
			data.setResponseBody(responseBody);
			data.setStatus(true);
			data.setStatusCode(100);
			data.setResponseCode("Success");
			data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
			data.setResponseDetail("ThreadComments count fetched successfully");
		} catch (AcumosServiceException e) {
			data.setErrorCode(e.getErrorCode());
			data.setResponseDetail(e.getMessage());
			log.error("Exception Occurred Fetching threadCommentsCount", e);
		}
		
		return data;
		
	}
	
	@ApiOperation(value = "Gets a list of Threads according to solution and revision id's", response = RestPageResponseBE.class)
	@RequestMapping(value = { APINames.GET_THREAD_SOLUTIONREVISION }, method = RequestMethod.POST, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<RestPageResponseBE<MLPThread>> getSolutionRevisionThreads(@PathVariable String solutionId,@PathVariable String revisionId, @RequestBody JsonRequest<RestPageRequest> restPageReq) {
		log.debug("getSolutionRevisionThreads");
		RestPageResponseBE<MLPThread> mlpThread = null;
		JsonResponse<RestPageResponseBE<MLPThread>> data = new JsonResponse<>();
		try {
			    mlpThread = threadService.getSolutionRevisionThreads(solutionId, revisionId, restPageReq.getBody());
			    if(mlpThread != null){
			    	data.setResponseBody(mlpThread);
			    	data.setStatusCode(100);
			    	data.setStatus(true);
			    	data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
					data.setResponseDetail("Threads fetched Successfully for solution and revision Id's");
			    }
		} catch (AcumosServiceException e) {
			data.setStatus(false);
			data.setErrorCode(JSONTags.TAG_ERROR_CODE);
			data.setResponseDetail("Exception Occurred Fetching thread for solution and revision Id's");
			log.error("Exception Occurred Fetching thread for solution and revision Id's", e);
		}
		return data;
	}
	
	@ApiOperation(value = "Gets a list of child comments belongs to a Thread id's", response = RestPageResponseBE.class)
	@RequestMapping(value = { APINames.GET_THREAD_CHILD_COMMENTS }, method = RequestMethod.POST, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<RestPageResponseBE<MLComment>> getThreadChildComments(@PathVariable String threadId, 
			@RequestBody JsonRequest<RestPageRequest> restPageReq,
			@RequestHeader(value = "clientTimeZone", required = false) String clientTimeZone) {
		log.debug("getSolutionRevisionThreads");
		RestPageResponseBE<MLComment> mlComment = null;
		JsonResponse<RestPageResponseBE<MLComment>> data = new JsonResponse<>();
		try {
			mlComment = threadService.getThreadChildComments(threadId, restPageReq.getBody(),clientTimeZone);
			    if(mlComment != null){
			    	data.setResponseBody(mlComment);
			    	data.setStatusCode(100);
			    	data.setStatus(true);
			    	data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
					data.setResponseDetail("Threads fetched Successfully for solution and revision Id's");
			    }
		} catch (AcumosServiceException e) {
			data.setStatus(false);
			data.setErrorCode(JSONTags.TAG_ERROR_CODE);
			data.setResponseDetail("Exception Occurred Fetching thread for solution and revision Id's");
			log.error("Exception Occurred Fetching thread for solution and revision Id's", e);
		}
		return data;
	}
	
	@ApiOperation(value = "Gets a list of comments according to solution and revision id's", response = RestPageResponseBE.class)
	@RequestMapping(value = { APINames.GET_COMMENT_SOLUTIONREVISION }, method = RequestMethod.POST, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<RestPageResponseBE<MLComment>> getSolutionRevisionComments(@PathVariable String solutionId,@PathVariable String revisionId, @RequestBody JsonRequest<RestPageRequest> restPageReq,
			@RequestHeader(value = "UserTimeZone", required = false) String clientTimeZone) {
		log.debug("getSolutionRevisionComments");
		RestPageResponseBE<MLComment> mlComment = null;
		JsonResponse<RestPageResponseBE<MLComment>> data = new JsonResponse<>();
		try {
			mlComment = threadService.getSolutionRevisionComments(solutionId, revisionId,clientTimeZone, restPageReq.getBody() );
			    if(mlComment != null){
			    	data.setResponseBody(mlComment);
			    	data.setStatusCode(100);
			    	data.setStatus(true);
			    	data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
					data.setResponseDetail("Comments fetched Successfully for solution and revision Id's");
			    }
		} catch (AcumosServiceException e) {
			data.setStatus(false);
			data.setErrorCode(JSONTags.TAG_ERROR_CODE);
			data.setResponseDetail("Exception Occurred Fetching comments for solution and revision Id's");
			log.error("Exception Occurred Fetching comments for solution and revision Id's", e);
		}
		return data;
	}
}
