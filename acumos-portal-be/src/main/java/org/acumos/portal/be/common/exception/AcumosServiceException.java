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

public class AcumosServiceException extends Exception
{
	private static final long serialVersionUID = 1L;

	public enum ErrorCode
	{
		OBJECT_NOT_FOUND,
		CONNECTION_ISSUE,
		CONSTRAINT_VIOLATION,
		UNIQUE_CONSTRAINT_VIOLATION,
		FOREIGN_KEY_CONSTRAINT_VIOLATION,
		INTERNAL_SERVER_ERROR,
		OPERATION_NOT_ALLOWED,
		INVALID_PARAMETER,
		ACCESS_DENIED,
		INVALID_TOKEN,
		INVALID_LICENSE,
		LICENSE_EXPIRED,
		LICENSE_POLICY_VIOLATION,
		PASSWORD_POLICY_VIOLATION,
		TOKEN_EXPIRED,
		MAIL_EXCEPTION,
		ARITHMATIC_EXCEPTION,
		IO_EXCEPTION,
		FILE_NOT_FOUND,
		CLIENT_PROTOCOL_EXCEPTION,
		OBJECT_STREAM_EXCEPTION;
	}

	private String errorCode;

	public AcumosServiceException(){}
	
	public AcumosServiceException(String message)
	{
		this( AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR,message);
	}
	public AcumosServiceException(String message,Throwable tw)
	{
		this( AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR,message,tw);
	}
	public AcumosServiceException(String errorCode, String message)
	{
		super(message);
		this.errorCode = errorCode;
	}

	public AcumosServiceException(String errorCode, String message, Throwable tw)
	{
		super(message, tw);
		this.errorCode = errorCode;
	}
	
	
	public AcumosServiceException(AcumosServiceException.ErrorCode errorCode, String message)
	{
		this(errorCode.name(),message);
	}

	public AcumosServiceException(AcumosServiceException.ErrorCode errorCode, String message, Throwable tw)
	{
		this(errorCode.name(),message,tw);
	}

	public void setErrorCode(String errorCode)
	{
		this.errorCode = errorCode;
	}

	public String getErrorCode()
	{
		return errorCode;
	}
}
