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

package org.acumos.portal.be.common.exception;

/**
 * @author Ashwin Sharma
 * Class to hold all Response Codes for Acumos Portal Application
 */
public class AcumosPortalExceptionCode {

	public AcumosPortalExceptionCode() {
		// TODO Auto-generated constructor stub
	}
	
	//User Profile Related Response Code
	public static final String EMAIL_ID_ACCOUNT_ALREADY_EXISTS = "MLP-1";
	public static final String USERNAME_ACCOUNT_ALREADY_EXISTS = "MLP-2";
	public static final String ACCOUNT_DOES_NOT_EXISTS = "MLP-3";
	 
	public static final String AUTHENTICATION_FAILED = "MLP-401";
	public static final String SUCCESS = "MLP-200";
	

}
