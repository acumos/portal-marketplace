/**
 * This call sends an email to one recipient, using a validated sender address
 * Do not forget to update the sender address used in the sample
 */
package org.acumos.portal.be.service.impl;
import com.mailjet.client.errors.MailjetException;
import com.mailjet.client.errors.MailjetSocketTimeoutException;
import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.MailjetResponse;
import com.mailjet.client.resource.Email;

import freemarker.template.Configuration;

import java.util.Map;

import org.acumos.portal.be.service.MailService;
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


@Service
public class MailJet {
	
	
    @Autowired
    Configuration freemarkerConfiguration;

	@Autowired
	private Environment env;
	
    private static final EELFLoggerDelegate log = EELFLoggerDelegate.getLogger(MailJet.class);
	
	public void sendMail(final MailData mailData) {
      MailjetClient client;
      MailjetRequest request = null;
      MailjetResponse response = null;
      
      String apiKey = env.getProperty("portal.mailjet.api.key");
      String secretKey = env.getProperty("portal.mailjet.secret.key");
      String fromAddress = env.getProperty("portal.mailjet.address.from");
      
      if(PortalUtils.isEmptyOrNullString(apiKey) || PortalUtils.isEmptyOrNullString(secretKey) || PortalUtils.isEmptyOrNullString(fromAddress)) {
          log.error(EELFLoggerDelegate.errorLogger, "MailJet Credentials Not Configured ={}");
      }

      client = new MailjetClient(apiKey, secretKey);
      String text = geFreeMarkerTemplateContent(mailData.getTemplate(), mailData.getModel());
      
      JSONArray toJsonArray = new JSONArray();
      
      for (String address : mailData.getTo()) {
    	  try {toJsonArray.put(
			new JSONObject().put("Email", address));
		} catch (JSONException e) {
			log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred while Sending Mail to user ={}", e);
		}
      }
      request = new MailjetRequest(Email.resource)
	                    .property(Email.FROMEMAIL, fromAddress)
	                    //.property(Email.FROMNAME, "User Name")
	                    .property(Email.SUBJECT, mailData.getSubject())
	                    .property(Email.HTMLPART, text)
	                    .property(Email.RECIPIENTS, toJsonArray);
      log.debug(request.toString());
      try {
		response = client.post(request);
	} catch (MailjetException | MailjetSocketTimeoutException e) {
		log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred while Sending Mail to user ={}", e);
		return;
	}
      log.debug(response.getData().toString());
    }
	

    private String geFreeMarkerTemplateContent(String template, final Map<String, Object> model){
        StringBuffer content = new StringBuffer();
        try{
           content.append(FreeMarkerTemplateUtils.processTemplateIntoString( 
                   freemarkerConfiguration.getTemplate(template),model));
           return content.toString();
        }catch(Exception e){
            
            log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred while Sending Mail to user ={}", e);
        }
        return "";
    }
}
