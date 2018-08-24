package org.acumos.portal.be.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AcumosOnboardingProperties {
	
	@Value("${onboarding.cliPushUrl}")
	private String cliPushUrl;
	
	@Value("${onboarding.cliAuthUrl}")
	private String cliAuthUrl;

    public String getCliPushUrl() {
        return cliPushUrl;
    }

    public String getCliAuthUrl() {
        return cliAuthUrl;
    }

}