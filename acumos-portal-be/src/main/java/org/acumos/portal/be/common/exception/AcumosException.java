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

/**
 * 
 */
package org.acumos.portal.be.common.exception;

import org.acumos.portal.be.util.EELFLoggerDelegate;

/**
 * 
 *
 */
public abstract class AcumosException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(AcumosException.class);

	private transient Object param;
	private String errorCode;
	private String errorDesc;

	/**
	 * @return the errorCode
	 */
	public String getErrorCode() {
		return errorCode;
	}

	/**
	 * @param errorCode
	 *            the errorCode to set
	 */
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	/**
	 * @return the errorDesc
	 */
	public String getErrorDesc() {
		return errorDesc;
	}

	/**
	 * @param errorDesc
	 *            the errorDesc to set
	 */
	public void setErrorDesc(String errorDesc) {
		this.errorDesc = errorDesc;
	}

	/**
	 * @return the param
	 */
	public Object getParam() {
		return param;
	}

	/**
	 * @param param
	 *            the param to set
	 */
	public void setParam(Object param) {
		this.param = param;
	}

	/**
	 * 
	 */
	public AcumosException() {
		super();
	}

	/**
	 * 
	 * @param errmessage
	 *            Error message
	 */
	public AcumosException(String errmessage) {
		super(errmessage);
		this.errorDesc = errmessage;
	}

	/**
	 * 
	 * @param message
	 *            Error message
	 * @param errorCode
	 *            Error code
	 * @param errorDesc
	 *            Error description
	 */
	public AcumosException(String message, String errorCode, String errorDesc) {
		super(message);
		this.errorCode = errorCode;
		this.errorDesc = errorDesc;
	}

	/**
	 * 
	 * @param message
	 *            Error message
	 * @param errorCode
	 *            Error code
	 * @param errorDesc
	 *            Error description
	 * @param cause
	 *            Throwable
	 */
	public AcumosException(String message, String errorCode, String errorDesc, Throwable cause) {
		super(message, cause);
		this.errorCode = errorCode;
		this.errorDesc = errorDesc;
	}

	/**
	 * 
	 * 
	 * @param message
	 *            Error message
	 * @param errorCode
	 *            Error code
	 * @param errorDesc
	 *            Error description
	 * @param cause
	 *            Throwable
	 * @param param
	 *            Object
	 */
	public AcumosException(String message, String errorCode, String errorDesc, Throwable cause, Object param) {
		super(message, cause);
		this.param = param;
		this.errorCode = errorCode;
		this.errorDesc = errorDesc;
	}

}
