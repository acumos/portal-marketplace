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
import org.acumos.portal.be.util.EELFLoggerDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.ApiOperation;

@Controller
@RequestMapping("/")
public class ThreadController extends AbstractController {
	private static final EELFLoggerDelegate log = EELFLoggerDelegate.getLogger(ThreadController.class);

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
			log.debug(EELFLoggerDelegate.debugLogger, "createThread :  ");
		} catch (AcumosServiceException e) {
			data.setErrorCode(JSONTags.TAG_ERROR_CODE);
			data.setResponseDetail("Exception occured while createThread");
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred createThread :", e);
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
			log.debug(EELFLoggerDelegate.debugLogger, "updateThread :  ");
		} catch (AcumosServiceException e) {
			data.setErrorCode(JSONTags.TAG_ERROR_CODE);
			data.setResponseDetail("Exception occured while updateThread");
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred updateThread :", e);
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
			log.debug(EELFLoggerDelegate.debugLogger, "deleteThread :  ");
		} catch (AcumosServiceException e) {
			data.setErrorCode(JSONTags.TAG_ERROR_CODE);
			data.setResponseDetail("Exception occured while deleteThread");
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred deleteThread :", e);
		}
		return data;
	}

	@ApiOperation(value = "Get Thread", response = MLPThread.class)
	@RequestMapping(value = { APINames.GET_THREAD }, method = RequestMethod.GET, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<MLPThread> getThread(HttpServletRequest request, @PathVariable String threadId,
			HttpServletResponse response) {
		JsonResponse<MLPThread> data = new JsonResponse<>();
		try {
			MLPThread thread = threadService.getThread(threadId);
			data.setResponseBody(thread);
			data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
			data.setResponseDetail("Threads fetched Successfully");
			log.debug(EELFLoggerDelegate.debugLogger, "getThread :  ");
		} catch (AcumosServiceException e) {
			data.setErrorCode(JSONTags.TAG_ERROR_CODE);
			data.setResponseDetail("Exception occured while getThread");
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred getThread :", e);
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
			log.debug(EELFLoggerDelegate.debugLogger, "createComment :  ");
		} catch (AcumosServiceException e) {
			data.setErrorCode(JSONTags.TAG_ERROR_CODE);
			data.setResponseDetail("Exception occured while createComment");
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred createComment :", e);
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
			log.debug(EELFLoggerDelegate.debugLogger, "updateComment :  ");
		} catch (AcumosServiceException e) {
			data.setErrorCode(JSONTags.TAG_ERROR_CODE);
			data.setResponseDetail("Exception occured while updateComment");
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred updateComment :", e);
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
			log.debug(EELFLoggerDelegate.debugLogger, "deleteComment :  ");
		} catch (AcumosServiceException e) {
			data.setErrorCode(JSONTags.TAG_ERROR_CODE);
			data.setResponseDetail("Exception occured while deleteComment");
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred deleteComment :", e);
		}
		return data;
	}

	@ApiOperation(value = "Get comments", response = MLPComment.class)
	@RequestMapping(value = { APINames.GET_COMMENT }, method = RequestMethod.GET, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<MLPComment> getComment(HttpServletRequest request, @PathVariable String threadId,
			@PathVariable String commentId, HttpServletResponse response) {
		JsonResponse<MLPComment> data = new JsonResponse<>();
		try {
			MLPComment comment = threadService.getComment(threadId, commentId);
			data.setResponseBody(comment);
			data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
			data.setResponseDetail("Comments fetched Successfully");
			log.debug(EELFLoggerDelegate.debugLogger, "deleteComment :  ");
		} catch (AcumosServiceException e) {
			data.setErrorCode(JSONTags.TAG_ERROR_CODE);
			data.setResponseDetail("Exception occured while deleteComment");
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred deleteComment :", e);
		}
		return data;
	}

	@ApiOperation(value = "Gets a list of Threads ", response = RestPageResponseBE.class)
	@RequestMapping(value = { APINames.GET_THREADS }, method = RequestMethod.POST, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<RestPageResponseBE> getThreads(@RequestBody JsonRequest<RestPageRequest> restPageReq) {
		log.debug(EELFLoggerDelegate.debugLogger, "getThreads");
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
				log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred Fetching thread");
			}
		} catch (AcumosServiceException e) {
			data.setErrorCode(e.getErrorCode());
			data.setResponseDetail(e.getMessage());
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred Fetching thread", e);
		}
		return data;
	}

	
	/*@ApiOperation(value = "Gets a list of Threads ", response = RestPageResponseBE.class)
	@RequestMapping(value = { APINames.GET_THREAD_COMMENTS}, method = RequestMethod.POST, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<RestPageResponseBE> getThreadComments(@PathVariable String threadId,@RequestBody JsonRequest<RestPageRequest> restPageReq) {
		log.debug(EELFLoggerDelegate.debugLogger, "getThreadComments");
		List<String> commentsList = new ArrayList<>();
		JsonResponse<RestPageResponseBE> data = new JsonResponse<>();
		try {
			commentsList = threadService.getThreadComments(threadId, restPageReq);
			if (commentsList != null) {
				List test = new ArrayList<>();
				RestPageResponseBE responseBody = new RestPageResponseBE<>(test);
				responseBody.setThreads(commentsList);
				data.setResponseBody(responseBody);
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
				data.setResponseDetail("Comments fetched Successfully for particular thread");
			} else {
				data.setErrorCode(JSONTags.TAG_ERROR_CODE);
				data.setResponseDetail("Exception Occurred Fetching thread");
				log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred Fetching thread");
			}
		} catch (AcumosServiceException e) {
			data.setErrorCode(e.getErrorCode());
			data.setResponseDetail(e.getMessage());
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred Fetching thread", e);
		}
		return data;
	}*/
}
