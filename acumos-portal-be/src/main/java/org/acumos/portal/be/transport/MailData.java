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

import java.util.List;
import java.util.Map;

public class MailData {

    private String subject;
    private List<String> to;
    private  String from;
    private String template;
	private Map<String, Object> model;
	
	

	public String getSubject() {
		return subject;
	}


	public void setSubject(String subject) {
		this.subject = subject;
	}


	public List<String> getTo() {
		return to;
	}


	public void setTo(List<String> to) {
		this.to = to;
	}


	public String getFrom() {
		return from;
	}


	public void setFrom(String from) {
		this.from = from;
	}


	public String getTemplate() {
		return template;
	}


	public void setTemplate(String template) {
		this.template = template;
	}


	public Map<String, Object> getModel() {
		return model;
	}


	public void setModel(Map<String, Object> model) {
		this.model = model;
	}


	@Override
	public String toString() {
		return "MailData [subject=" + subject + ", to=" + to + ", from=" + from + ", template=" + template + ", model="
				+ model + "]";
	}


 
 
}
