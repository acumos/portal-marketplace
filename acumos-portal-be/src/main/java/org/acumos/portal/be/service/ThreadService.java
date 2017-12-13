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
package org.acumos.portal.be.service;

import java.util.List;

import org.acumos.cds.domain.MLPComment;
import org.acumos.cds.domain.MLPThread;
import org.acumos.cds.transport.RestPageRequest;
import org.acumos.portal.be.common.JsonRequest;
import org.acumos.portal.be.common.exception.AcumosServiceException;

public interface ThreadService {
	MLPComment getComment(String threadId, String commentId) throws AcumosServiceException;

	void deleteComment(String threadId, String commentId) throws AcumosServiceException;

	void updateComment(MLPComment comment) throws AcumosServiceException;

	MLPComment createComment(MLPComment comment) throws AcumosServiceException;

	MLPThread createThread(MLPThread thread) throws AcumosServiceException;

	void updateThread(MLPThread thread) throws AcumosServiceException;

	void deleteThread(String threadId) throws AcumosServiceException;

	MLPThread getThread(String threadId) throws AcumosServiceException;

	List<String> getThreads(JsonRequest<RestPageRequest> pageRequest) throws AcumosServiceException;

	List<String> getThreadComments(String threadId, JsonRequest<RestPageRequest> pageRequest)
			throws AcumosServiceException;

}
