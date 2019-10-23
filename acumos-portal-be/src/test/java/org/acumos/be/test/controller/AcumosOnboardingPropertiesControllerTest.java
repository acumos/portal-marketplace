package org.acumos.be.test.controller;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import org.acumos.portal.be.controller.AcumosOnboardingPropertiesController;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;

public class AcumosOnboardingPropertiesControllerTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();	
	@InjectMocks
	AcumosOnboardingPropertiesController acumosOnboardingPropertiesController;
	@Mock
	Environment env;
	@Test
	public void getCliPushUrlTest() {
		when(env.getProperty("onboarding.cliPushUrl")).thenReturn("http://localhost:8090/onboarding-app/v2/push");
		ResponseEntity<String> resp=acumosOnboardingPropertiesController.getCliPushUrl();
		assertNotNull(resp);
	}
	
	@Test
	public void getCliAuthUrlTest() {
		when(env.getProperty("onboarding.cliAuthUrl")).thenReturn("http://localhost:8090/onboarding-app/v2/auth");
		ResponseEntity<String> resp=acumosOnboardingPropertiesController.getCliAuthUrl();
		assertNotNull(resp);
	}
}
