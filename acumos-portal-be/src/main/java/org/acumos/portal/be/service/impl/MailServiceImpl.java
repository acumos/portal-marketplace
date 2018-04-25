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
package org.acumos.portal.be.service.impl;

import java.util.Map;

import javax.mail.Address;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;

import org.acumos.portal.be.service.MailService;
import org.acumos.portal.be.transport.MailData;
import org.acumos.portal.be.util.EELFLoggerDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;


import freemarker.template.Configuration;



@Service
public class MailServiceImpl implements MailService {

    private static final EELFLoggerDelegate log = EELFLoggerDelegate.getLogger(MailServiceImpl.class);
    
    @Autowired
    Configuration freemarkerConfiguration;
    
    @Autowired
    JavaMailSender mailSender;

    @Override
    public void sendMail(final MailData mailData) {
        MimeMessagePreparator preparator = getMessagePreparator(mailData);
        try {
            mailSender.send(preparator);
            log.debug(EELFLoggerDelegate.debugLogger, "Message has been sent...");
        }
        catch (MailException ex) {
            log.error(EELFLoggerDelegate.errorLogger, "Exception Occurred while Sending Mail to user ={}", ex);
        }
    }


    private MimeMessagePreparator getMessagePreparator(final MailData mailData){

        MimeMessagePreparator preparator = new MimeMessagePreparator() {
 
            public void prepare(MimeMessage mimeMessage) throws Exception {
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

                helper.setSubject(mailData.getSubject());
                helper.setFrom(mailData.getFrom());

                Address[] internetAddress = new InternetAddress[mailData.getTo().size()];
                int i = 0;
                for (String address : mailData.getTo()) {
                    internetAddress[i] = new InternetAddress(address);
                    i++;
                }
                mimeMessage.addRecipients(RecipientType.TO, internetAddress);

                String text = geFreeMarkerTemplateContent(mailData.getTemplate(), mailData.getModel());
                log.debug(EELFLoggerDelegate.debugLogger, "Template content : " + text);

                // use the true flag to indicate you need a multipart message
                helper.setText(text, true);

                //Additionally, can add a resource as an attachment as well.
                //helper.addAttachment("cutie.png", new ClassPathResource("linux-icon.png"));

            }
        };
        return preparator;
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
