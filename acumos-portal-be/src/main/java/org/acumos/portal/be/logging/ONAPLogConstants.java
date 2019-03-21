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

package org.acumos.portal.be.logging;

/**
 * Constants for standard headers, MDCs, etc.
*/
public final class ONAPLogConstants {

	/**
	 * Hide and forbid construction.
	 */
	private ONAPLogConstants() {
		throw new UnsupportedOperationException();
	}	

	/**
	 * MDC name constants.
	 */
	public static final class MDCs {

		/** MDC correlating messages for a logical transaction. */
		public static final String REQUEST_ID = "X-ACUMOS-Request-Id";

		/** MDC recording target service. */
		public static final String TARGET_SERVICE_NAME = "TargetServiceName";

		/** MDC recording caller address. */
		public static final String CLIENT_IP_ADDRESS = "ClientIPAddress";

		/** MDC recording server address. */
		public static final String SERVER_FQDN = "ServerFQDN";

		/**
		 * MDC recording timestamp at the start of the current request, with the same
		 * scope as {@link #REQUEST_ID}.
	    */
		public static final String ENTRY_TIMESTAMP = "EntryTimestamp";

		/** MDC recording timestamp at the start of the current invocation. */
		public static final String INVOKE_TIMESTAMP = "InvokeTimestamp";

		/** MDC reporting outcome code. */
		public static final String RESPONSE_CODE = "ResponseCode";

		/** MDC reporting outcome description. */
		public static final String RESPONSE_DESCRIPTION = "ResponseDescription";

		/** MDC reporting outcome error level. */
		public static final String RESPONSE_SEVERITY = "Severity";

		/** MDC reporting outcome error level. */
		public static final String RESPONSE_STATUS_CODE = "StatusCode";
		
		/** MDC correlating messages for User. */
		public static final String USER = "User";

		/**
		 * Hide and forbid construction.
		 */
		private MDCs() {
			throw new UnsupportedOperationException();
		}
	}

	/**
	 * Header name constants.
	 */
	public static final class Headers {

		public static final String REQUEST_ID = "X-ACUMOS-Request-Id";

		/**
		 * Hide and forbid construction.
		 */
		private Headers() {
			throw new UnsupportedOperationException();
		}
	}

	/**
	 * Response success or not, for setting <tt>StatusCode</tt>.
	 */
	public enum ResponseStatus {

		/** Success. */
		COMPLETED,

		/** Not. */
		ERROR,
		
		/** In Progress. */
		INPROGRESS,

	}
}