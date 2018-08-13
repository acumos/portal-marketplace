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
package org.acumos.portal.be.service.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import org.acumos.cds.client.ICommonDataServiceRestClient;
import org.acumos.cds.domain.MLPComment;
import org.acumos.cds.domain.MLPThread;
import org.acumos.cds.transport.RestPageRequest;
import org.acumos.cds.transport.RestPageResponse;
import org.acumos.portal.be.common.JsonRequest;
import org.acumos.portal.be.common.RestPageResponseBE;
import org.acumos.portal.be.common.exception.AcumosServiceException;
import org.acumos.portal.be.service.ThreadService;
import org.acumos.portal.be.transport.MLComment;
import org.acumos.portal.be.util.EELFLoggerDelegate;
import org.acumos.portal.be.util.PortalUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Hours;
import org.joda.time.Interval;
import org.joda.time.Minutes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

@Service
public class ThreadServiceImpl extends AbstractServiceImpl implements ThreadService{
	
    private static final String FEW_SECONDS_AGO = "Just Now";
    private static final String MINUTES_AGO = "Minutes Ago";
    private static final String TIMESTAMP_FORMAT = "MM/dd/yyyy hh:mm a";
	private static final EELFLoggerDelegate log = EELFLoggerDelegate
			.getLogger(ThreadServiceImpl.class);
	
	
	@Autowired
	private Environment env;


	public ThreadServiceImpl() {
		// TODO Auto-generated constructor stub
	}



	@Override
	public MLPComment createComment(MLPComment comment) throws AcumosServiceException {
		MLPComment mlpComment = null;
		try {
			log.debug(EELFLoggerDelegate.debugLogger, "createComment");
			ICommonDataServiceRestClient dataServiceRestClient = getClient();
			mlpComment = dataServiceRestClient.createComment(comment);
		} catch (IllegalArgumentException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INVALID_PARAMETER, e.getMessage());
		} catch (HttpClientErrorException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
		}
		return mlpComment;
	}

	@Override
	public void updateComment(MLPComment comment) throws AcumosServiceException {
		try {
			log.debug(EELFLoggerDelegate.debugLogger, "updateComment");
			ICommonDataServiceRestClient dataServiceRestClient = getClient();
			dataServiceRestClient.updateComment(comment);
		} catch (IllegalArgumentException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INVALID_PARAMETER, e.getMessage());
		} catch (HttpClientErrorException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}

	@Override
	public void deleteComment(String threadId, String commentId) throws AcumosServiceException {
		try {
			log.debug(EELFLoggerDelegate.debugLogger, "deleteComment");
			ICommonDataServiceRestClient dataServiceRestClient = getClient();
			dataServiceRestClient.deleteComment(threadId, commentId);
		} catch (IllegalArgumentException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INVALID_PARAMETER, e.getMessage());
		} catch (HttpClientErrorException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}

	@Override
	public MLPComment getComment(String threadId, String commentId) throws AcumosServiceException {
		MLPComment comment = null;
		try {
			log.debug(EELFLoggerDelegate.debugLogger, "getComment");
			ICommonDataServiceRestClient dataServiceRestClient = getClient();
			comment =  dataServiceRestClient.getComment(threadId, commentId);
		} catch (IllegalArgumentException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INVALID_PARAMETER, e.getMessage());
		} catch (HttpClientErrorException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
		}
		return comment;

	}
	
	@Override
	public MLPThread createThread(MLPThread thread) throws AcumosServiceException{
	    try {
	    	log.debug(EELFLoggerDelegate.debugLogger, "getComment");
			ICommonDataServiceRestClient dataServiceRestClient = getClient();
			thread = dataServiceRestClient.createThread(thread);
			
		} catch (IllegalArgumentException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INVALID_PARAMETER, e.getMessage());
		} catch (HttpClientErrorException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
		}
		
		return thread;
		
	}
	
	@Override
	public void updateThread(MLPThread thread) throws AcumosServiceException{
	    try {
	    	log.debug(EELFLoggerDelegate.debugLogger, "getComment");
			ICommonDataServiceRestClient dataServiceRestClient = getClient();
			dataServiceRestClient.updateThread(thread);
		} catch (IllegalArgumentException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INVALID_PARAMETER, e.getMessage());
		} catch (HttpClientErrorException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
		}
		
	}
	
	@Override
	public void deleteThread(String threadId) throws AcumosServiceException{
	    try {
	    	log.debug(EELFLoggerDelegate.debugLogger, "getComment");
			ICommonDataServiceRestClient dataServiceRestClient = getClient();
			dataServiceRestClient.deleteThread(threadId);
		} catch (IllegalArgumentException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INVALID_PARAMETER, e.getMessage());
		} catch (HttpClientErrorException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
		}
		
	}
	
	@Override
	public MLPThread getThread(String threadId) throws AcumosServiceException{
		MLPThread thread = null;
	    try {
	    	log.debug(EELFLoggerDelegate.debugLogger, "getComment");
			ICommonDataServiceRestClient dataServiceRestClient = getClient();
			thread = dataServiceRestClient.getThread(threadId);
			
		} catch (IllegalArgumentException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INVALID_PARAMETER, e.getMessage());
		} catch (HttpClientErrorException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
		}
		
		return thread;
		
	}
	
	@Override
	public List<String> getThreads(JsonRequest<RestPageRequest> pageRequest) throws AcumosServiceException{
		List<String> threads = new ArrayList<>();
		List<MLPThread> mlpThreadList = new ArrayList<MLPThread>();
	    try {
	    	log.debug(EELFLoggerDelegate.debugLogger, "getThreads");
			ICommonDataServiceRestClient dataServiceRestClient = getClient();
			RestPageResponse<MLPThread> threadList  = dataServiceRestClient.getThreads(null);
			
			if (threadList != null && !PortalUtils.isEmptyList(threadList.getContent())) {
				mlpThreadList = threadList.getContent();
			}
			for (MLPThread mlpThread : mlpThreadList) {
				threads.add(mlpThread.getThreadId());
			}
		} catch (IllegalArgumentException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INVALID_PARAMETER, e.getMessage());
		} catch (HttpClientErrorException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
		}
		return threads;
		
	}
	
	@Override
	public List<String> getThreadComments(String threadId,RestPageRequest pageRequest) throws AcumosServiceException{
		List<String> comments = new ArrayList<>();
		List<MLPComment> mlpCommentList = new ArrayList<MLPComment>();
	    try {
	    	log.debug(EELFLoggerDelegate.debugLogger, "getThreadComments");
			ICommonDataServiceRestClient dataServiceRestClient = getClient();
			RestPageResponse<MLPComment> commentList  = dataServiceRestClient.getThreadComments(threadId,pageRequest);
			
			if (commentList != null && !PortalUtils.isEmptyList(commentList.getContent())) {
				mlpCommentList = commentList.getContent();
			}
			for (MLPComment mlpComment : mlpCommentList) {
				comments.add(mlpComment.getCommentId());
			}
		} catch (IllegalArgumentException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INVALID_PARAMETER, e.getMessage());
		} catch (HttpClientErrorException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
		}
		return comments;
		
	}
	
	@Override
	public long getThreadCount() throws AcumosServiceException{
		long count;
		try{
			log.debug(EELFLoggerDelegate.debugLogger, "getThreadCount");
			ICommonDataServiceRestClient dataServiceRestClient = getClient();
			count = dataServiceRestClient.getThreadCount();
		}catch (IllegalArgumentException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INVALID_PARAMETER, e.getMessage());
		} catch (HttpClientErrorException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
		}
		return count;
	}
	
	
	@Override
	public long getThreadCommentsCount(String threadId) throws AcumosServiceException{
		long count;
		try{
			log.debug(EELFLoggerDelegate.debugLogger, "getThreadCount");
			ICommonDataServiceRestClient dataServiceRestClient = getClient();
			count = dataServiceRestClient.getThreadCommentCount(threadId);
		}catch (IllegalArgumentException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INVALID_PARAMETER, e.getMessage());
		} catch (HttpClientErrorException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
		}
		return count;
	}


	public RestPageResponseBE<MLPThread> getSolutionRevisionThreads(String solutionId, String revisionId, RestPageRequest pageRequest) throws AcumosServiceException{
		List<MLPThread> threadList = new ArrayList<MLPThread>();
		RestPageResponseBE<MLPThread> threadResponse = new RestPageResponseBE<>(threadList);
		try {
			log.debug(EELFLoggerDelegate.debugLogger, "getSolutionRevisionThreads");
			ICommonDataServiceRestClient dataServiceRestClient = getClient();
			RestPageResponse<MLPThread> pageResponse = new RestPageResponse<>();
			pageResponse = dataServiceRestClient.getSolutionRevisionThreads(solutionId, revisionId, pageRequest);
			for (MLPThread mlpThread : pageResponse.getContent()) {
				threadResponse.getContent().add(mlpThread);
			}
		} catch (IllegalArgumentException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INVALID_PARAMETER, e.getMessage());
		} catch (HttpClientErrorException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
		}
		
		return threadResponse;
		
	}
		
	public  RestPageResponseBE<MLComment> getSolutionRevisionComments(String solutionId, String revisionId, String clientTimeZone,RestPageRequest pageRequest) throws AcumosServiceException{
		//List<MLPComment> mlpCommentList = new ArrayList<MLPComment>();
		List<MLComment> content = new ArrayList<MLComment>();
		try {
			clientTimeZone=URLDecoder.decode(clientTimeZone, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		RestPageResponseBE<MLComment> commentResponse = new RestPageResponseBE<>(content);
		DateTimeZone clientZone = DateTimeZone.forID(clientTimeZone);
		try {
			log.debug(EELFLoggerDelegate.debugLogger, "getSolutionRevisionComments");
			ICommonDataServiceRestClient dataServiceRestClient = getClient();
			RestPageResponse<MLPComment> pageResponse = new RestPageResponse<>();
			pageResponse = dataServiceRestClient.getSolutionRevisionComments(solutionId, revisionId, pageRequest);
			List<MLComment> mlcommentList = new ArrayList<MLComment>();
			for(MLPComment mlpComment : pageResponse.getContent()){
				mlcommentList.add(PortalUtils.convertToMLComment(mlpComment));
			}
			for (MLComment mlComment : mlcommentList) {
				String stringDate = null;
				DateTime currDate = DateTime.now();
				DateTime commentTime = new DateTime(mlComment.getModified().getTime());
				DateTime clientTime = commentTime.withZone(clientZone);
				if (currDate.isAfter(commentTime)) {
					int minutes = Minutes.minutesBetween(commentTime, currDate).getMinutes();
					int hours = Hours.hoursBetween(commentTime, currDate).getHours();

					if (minutes < 1) {
						stringDate = FEW_SECONDS_AGO;
					} else if (minutes <= 59) {
						stringDate = minutes + " " + MINUTES_AGO;
					} else if (hours >= 1 && hours < 48) {
						Interval today = new Interval(currDate.withTimeAtStartOfDay(),
								currDate.plusDays(1).withTimeAtStartOfDay());
						boolean happensToday = today.contains(clientTime);
						if (happensToday) {
							stringDate = "AT " + clientTime.toString("hh:mm a");
						}else {
							Interval yesterday = new Interval(currDate.minusDays(1).withTimeAtStartOfDay(),
									currDate.withTimeAtStartOfDay());
							if(yesterday.contains(clientTime)){
								stringDate = "Yesterday " + clientTime.toString("hh:mm a");
							}else{
								stringDate = clientTime.toString(TIMESTAMP_FORMAT);
							}
						}

					} else {
						stringDate = clientTime.toString(TIMESTAMP_FORMAT);
					}
				}
				mlComment.setStringDate(stringDate);				 
				commentResponse.getContent().add(mlComment);
			}
		} catch (IllegalArgumentException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INVALID_PARAMETER, e.getMessage());
		} catch (HttpClientErrorException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
		}
		return commentResponse;

	}
}
