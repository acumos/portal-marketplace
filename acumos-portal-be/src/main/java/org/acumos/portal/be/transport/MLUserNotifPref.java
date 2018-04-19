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
package org.acumos.portal.be.transport;

public class MLUserNotifPref {
	private Long userNotifPrefId;
	private String userId;
	private String notfDelvMechCode;
	private String msgSeverityCode;

	public Long getUserNotifPrefId() {
		return userNotifPrefId;
	}

	public void setUserNotifPrefId(Long userNotifPrefId) {
		this.userNotifPrefId = userNotifPrefId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getNotfDelvMechCode() {
		return notfDelvMechCode;
	}

	public void setNotfDelvMechCode(String notfDelvMechCode) {
		this.notfDelvMechCode = notfDelvMechCode;
	}

	public String getMsgSeverityCode() {
		return msgSeverityCode;
	}

	public void setMsgSeverityCode(String msgSeverityCode) {
		this.msgSeverityCode = msgSeverityCode;
	}

}
