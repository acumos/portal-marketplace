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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jvnet.jaxb2_commons.xml.bind.model.MList;

public class MLRequest {

	private String requestId;
	private String requestedDetails;
	private String requestType;
	private String sender;
	private Date date;
	private String action;
	private String status;
	
	public String getRequestId() {
		return requestId;
	}
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	public String getRequestedDetails() {
		return requestedDetails;
	}
	public void setRequestedDetails(String requestedDetails) {
		this.requestedDetails = requestedDetails;
	}
	public String getRequestType() {
		return requestType;
	}
	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}
	public String getSender() {
		return sender;
	}
	public void setSender(String sender) {
		this.sender = sender;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getAction() {
		return action;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public void setAction(String action) {
		this.action = action;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MLRequest [requestId=");
		builder.append(requestId);
		builder.append(", requestedDetails=");
		builder.append(requestedDetails);
		builder.append(", requestType=");
		builder.append(requestType);
		builder.append(", sender=");
		builder.append(sender);
		builder.append(", date=");
		builder.append(date);
		builder.append(", action=");
		builder.append(action);
		builder.append("]");
		return builder.toString();
	}
}
