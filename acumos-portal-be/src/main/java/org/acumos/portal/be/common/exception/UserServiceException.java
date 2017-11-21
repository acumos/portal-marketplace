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

public class UserServiceException extends Throwable {
    static final long serialVersionUID = -3387516993124229948L;

    
    public UserServiceException() {
        super();
    }

   
    public UserServiceException(String message) {
        super(message);
    }

    
    public UserServiceException(String message, Throwable cause) {
        super(message, cause);
    }

   
    public UserServiceException(Throwable cause) {
        super(cause);
    }

  
    protected UserServiceException(String message, Throwable cause,
                        boolean enableSuppression,
                        boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

	public UserServiceException(int scResetContent, String string) {
		// TODO Auto-generated constructor stub
	}
}
