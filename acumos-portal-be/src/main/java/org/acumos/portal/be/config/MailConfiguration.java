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

package org.acumos.portal.be.config;
 
import java.util.Properties;

import org.acumos.portal.be.util.PortalUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;

@Configuration
public class MailConfiguration {

    @Autowired
    private Environment env;
 
    @Bean
    public JavaMailSender getMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        
        if(!PortalUtils.isEmptyOrNullString(env.getProperty("portal.feature.email_service")) 
        		&& env.getProperty("portal.feature.email_service").equalsIgnoreCase("smtp")) {
        	mailSender.setHost(env.getProperty("spring.mail.host"));
            mailSender.setPort(Integer.parseInt(env.getProperty("spring.mail.port")));
            mailSender.setUsername(env.getProperty("spring.mail.username"));
            mailSender.setPassword(env.getProperty("spring.mail.password"));

            Properties javaMailProperties = new Properties();
            javaMailProperties.put("mail.smtp.starttls.enable", env.getProperty("spring.mail.smtp.starttls.enable"));
            javaMailProperties.put("mail.smtp.auth", env.getProperty("spring.mail.smtp.auth"));
            javaMailProperties.put("mail.transport.protocol", env.getProperty("spring.mail.transport.protocol"));
            javaMailProperties.put("mail.debug", env.getProperty("spring.mail.debug"));
     
            mailSender.setJavaMailProperties(javaMailProperties);
        }
        return mailSender;
    }

    /*
     * FreeMarker configuration.
     */
    @Bean
    @Primary
    public FreeMarkerConfigurationFactoryBean getFreeMarkerConfiguration() {
        FreeMarkerConfigurationFactoryBean bean = new FreeMarkerConfigurationFactoryBean();
        bean.setTemplateLoaderPath("classpath:" + env.getProperty("spring.mail.template.folder.path"));
        return bean;
    }

}
