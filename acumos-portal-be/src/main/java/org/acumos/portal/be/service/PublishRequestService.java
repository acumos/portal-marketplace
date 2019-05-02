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

import org.acumos.cds.transport.RestPageRequest;
import org.acumos.portal.be.common.PagableResponse;
import org.acumos.portal.be.common.exception.AcumosServiceException;
import org.acumos.portal.be.transport.MLPublishRequest;

public interface PublishRequestService {

	MLPublishRequest getPublishRequestById(Long publishRequestId);

	PagableResponse<List<MLPublishRequest>> getAllPublishRequest(RestPageRequest requestobj);

	MLPublishRequest updatePublishRequest(MLPublishRequest publishRequest) throws AcumosServiceException;

	MLPublishRequest searchPublishRequestByRevId(String revisionId);
	
	MLPublishRequest searchPublishRequestByRevAndCatId(String revisionId, String catalogId);

	MLPublishRequest withdrawPublishRequest(long publishRequestId, String loginUserId) throws AcumosServiceException;

}
