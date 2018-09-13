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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.Future;

import org.acumos.cds.domain.MLPUser;
import org.acumos.portal.be.transport.UploadSolution;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;

public interface AsyncServices {

	Future<String> initiateAsyncProcess() throws InterruptedException;
	
	Future<HttpResponse> callOnboarding(String uuid, MLPUser user, UploadSolution solution, String provider, String access_token) throws InterruptedException, FileNotFoundException, ClientProtocolException, IOException;

	Boolean checkONAPCompatible(String solutioId, String revisionId, String userId, String tracking_id);

	Boolean checkONAPCompatible(String solutioId, String revisionId);

	HttpResponse convertSolutioToONAP(String solutionId, String revisionId, String userId, String tracking_id,String modName);
}
