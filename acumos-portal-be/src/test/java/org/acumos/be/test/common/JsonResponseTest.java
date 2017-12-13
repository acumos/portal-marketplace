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

import org.acumos.portal.be.common.JsonResponse;
import org.junit.Assert;
import org.junit.Test;

public class JsonResponseTest {
  
	@Test
	public void testJsonResponseParameter(){
		
		try{
			String errorCode = "200";
			Object responseBody = null;
			String responseCode = "";
			String responseDetail = "";
			Boolean status = true;
			int statusCode = 0;
			
			JsonResponse jsonReq = new JsonResponse();
			jsonReq.setErrorCode(errorCode);
			jsonReq.setResponseBody(responseBody);
			jsonReq.setResponseCode(responseCode);
			jsonReq.setResponseDetail(responseDetail);
			jsonReq.setStatus(status);
			jsonReq.setStatusCode(statusCode);
			Assert.assertNotNull(jsonReq);
		}catch(Exception e){
			
		}
	}
}
