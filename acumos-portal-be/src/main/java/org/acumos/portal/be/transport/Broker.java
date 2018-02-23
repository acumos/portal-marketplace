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

import java.util.Date;

import org.acumos.portal.be.common.JsonRequest;

public class Broker {

	private String responseName;
	
	private String responseContent;
	
	private JsonRequest<Url> urlAttribute;
	
	private JsonRequest<BrokerDetail> jsonPosition;
	
	private JsonRequest<BrokerDetail> jsonMapping;

	public String getResponseName() {
		return responseName;
	}

	public void setResponseName(String responseName) {
		this.responseName = responseName;
	}

	public String getResponseContent() {
		return responseContent;
	}

	public void setResponseContent(String responseContent) {
		this.responseContent = responseContent;
	}

	public JsonRequest<Url> getUrlAttribute() {
		return urlAttribute;
	}

	public void setUrlAttribute(JsonRequest<Url> urlAttribute) {
		this.urlAttribute = urlAttribute;
	}

	public JsonRequest<BrokerDetail> getJsonPosition() {
		return jsonPosition;
	}

	public void setJsonPosition(JsonRequest<BrokerDetail> jsonPosition) {
		this.jsonPosition = jsonPosition;
	}

	public JsonRequest<BrokerDetail> getJsonMapping() {
		return jsonMapping;
	}

	public void setJsonMapping(JsonRequest<BrokerDetail> jsonMapping) {
		this.jsonMapping = jsonMapping;
	}

	  
	
}
