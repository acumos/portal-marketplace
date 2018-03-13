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

package org.acumos.portal.be.common;

public class JSONTags {
	
	public static final String TAG_REQUEST_FNAME = "first_name";
	public static final String TAG_REQUEST_MNAME = "middle_name";
	public static final String TAG_STATUS_SUCCESS = "success";
	public static final String TAG_RESPONSE_STATUS = "status";
	public static final String TAG_RESPONSE_STATUS_CODE = "status_code";
	public static final String TAG_REQUEST_FROM = "request_from";
	public static final String TAG_REQUEST_ID = "request_id";
 
	public static final String TAG_REQUEST_BODY = "request_body";
	public static final String TAG_REQUEST_UNIQUE_ID = "userId";
	public static final String TAG_STATUS_FAILURE = "failure";
	public static final String TAG_REQUEST_UID = "user_id";
	public static final String TAG_REQUEST_PASSWORD = "password";

	public static final String TAG_RESPONSE_DETAIL = "response_detail";
	public static final String TAG_RESPONSE_BODY = "response_body";
	public static final String TAG_RESPONSE_CONTENT = "content";
	public static final String TAG_RESPONSE_MESSAGE = "request";
	public static final String TAG_ERROR_CODE = "error_code";
	public static final String TAG_RESPONSE_CODE = "response_code";
	public static final String TAG_ERROR_CODE_SUCCESS = "100";
	public static final String TAG_ERROR_CODE_FAILURE = "500";
	public static final String TAG_ERROR_CODE_EXCEPTION = "400";
	public static final String TAG_ERROR_CODE_RESET_USERNAME = "202";
	public static final String TAG_ERROR_CODE_RESET_EMAILID = "203";
	public static final String TAG_ERROR_CODE_OLDPASS_NOTMATCH = "204";
	public static final String TAG_ERROR_RESPONSE = "205";
	
	public static final String ROLE_ID = "roleId";
	public static final String ROLE_NAME = "name";
	public static final String ROLE_ACTIVE = "active";
	public static final String ROLE_CREATED = "created";
	public static final String ROLE_MODIFIED = "modified";
	
	public static final String REQUEST_APPROVED = "approve";
	public static final String REQUEST_DENIED = "deny";
}
