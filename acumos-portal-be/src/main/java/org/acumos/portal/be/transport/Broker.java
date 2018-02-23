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
