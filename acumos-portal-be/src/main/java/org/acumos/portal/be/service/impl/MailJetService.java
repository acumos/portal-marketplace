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
 * This call sends an email to one recipient, using a validated sender address
 */
package org.acumos.portal.be.service.impl;

import java.util.Map;

import org.acumos.portal.be.service.MailJet;
import org.acumos.portal.be.transport.MailData;
import org.acumos.portal.be.util.EELFLoggerDelegate;
import org.acumos.portal.be.util.PortalUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.MailjetResponse;
import com.mailjet.client.errors.MailjetException;
import com.mailjet.client.errors.MailjetSocketTimeoutException;
import com.mailjet.client.resource.Email;

import freemarker.template.Configuration;

@Service
public class MailJetService implements MailJet {

	private static final EELFLoggerDelegate log = EELFLoggerDelegate.getLogger(MailJetService.class);

	@Autowired
	private Configuration freemarkerConfiguration;

	@Autowired
	private Environment env;

	@Override
	public void sendMail(final MailData mailData) {
		MailjetClient client;
		MailjetRequest request = null;
		MailjetResponse response = null;

		String apiKey = env.getProperty("portal.mailjet.api.key");
		String secretKey = env.getProperty("portal.mailjet.secret.key");
		String fromAddress = env.getProperty("portal.mailjet.address.from");

		if (PortalUtils.isEmptyOrNullString(apiKey))
			log.error(EELFLoggerDelegate.errorLogger, "sendMail: failed to find required key " + apiKey);
		if (PortalUtils.isEmptyOrNullString(secretKey))
			log.error(EELFLoggerDelegate.errorLogger, "sendMail: failed to find required key " + secretKey);
		if (PortalUtils.isEmptyOrNullString(fromAddress))
			log.error(EELFLoggerDelegate.errorLogger, "sendMail: failed to find required key " + fromAddress);

		client = new MailjetClient(apiKey, secretKey);
		String text = getFreeMarkerTemplateContent(mailData.getTemplate(), mailData.getModel());

		JSONArray toJsonArray = new JSONArray();
		for (String address : mailData.getTo()) {
			try {
				toJsonArray.put(new JSONObject().put("Email", address));
			} catch (JSONException e) {
				log.error(EELFLoggerDelegate.errorLogger, "sendMail: failed to add address " + address);
			}
		}
		request = new MailjetRequest(Email.resource) //
				.property(Email.FROMEMAIL, fromAddress) //
				// .property(Email.FROMNAME, "User Name")
				.property(Email.SUBJECT, mailData.getSubject()) //
				.property(Email.HTMLPART, text) //
				.property(Email.RECIPIENTS, toJsonArray);
		try {
			log.debug(EELFLoggerDelegate.debugLogger, "sendMail: request: " + request.toString());
			response = client.post(request);
			log.debug(EELFLoggerDelegate.debugLogger, "sendMail: client response: " + response.getData().toString());
		} catch (MailjetException | MailjetSocketTimeoutException e) {
			log.error(EELFLoggerDelegate.errorLogger,
					"sendMail: failed to post email with subject " + mailData.getSubject(), e);
			return;
		}
	}

	private String getFreeMarkerTemplateContent(String template, final Map<String, Object> model) {
		StringBuffer content = new StringBuffer();
		try {
			content.append(FreeMarkerTemplateUtils
					.processTemplateIntoString(freemarkerConfiguration.getTemplate(template), model));
			return content.toString();
		} catch (Exception e) {
			log.error(EELFLoggerDelegate.errorLogger, "getFreeMarkerTemplateContent: failed to get content", e);
		}
		return "";
	}
}
