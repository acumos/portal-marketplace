/*-
 * ===============LICENSE_START=======================================================
 * Acumos
 * ===================================================================================
 * Copyright (C) 2019 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
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

import org.acumos.portal.be.common.exception.AcumosServiceException;
import org.acumos.portal.be.transport.MLK8SiteConfig;
import org.springframework.http.ResponseEntity;

public interface DeployCloudService {

	public MLK8SiteConfig getSiteConfig(String configKey) throws AcumosServiceException;

	public ResponseEntity<String> deployToK8(String userId, String solutionId, String revisionId, String envId);
	
}
