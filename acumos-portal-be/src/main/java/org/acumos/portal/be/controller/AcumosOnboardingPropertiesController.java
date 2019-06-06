package org.acumos.portal.be.controller;

import org.acumos.portal.be.APINames;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(APINames.PROPERTIES)
public class AcumosOnboardingPropertiesController {
	
	@Autowired
	private Environment env;
	
	@RequestMapping(value = { APINames.CLI_PUSH_MODEL_BUNDLE_URL }, method = RequestMethod.GET)
    public ResponseEntity<String> getCliPushModelBundleUrl() {
		String response = "{\"data\":\"" + env.getProperty("onboarding.cliPushModelBundleUrl") + "\"}";
        return new ResponseEntity<String>(response, HttpStatus.OK);
    }
	
	@RequestMapping(value = { APINames.CLI_PUSH_MODEL_URL }, method = RequestMethod.GET)
    public ResponseEntity<String> getCliPushModelUrl() {
		String response = "{\"data\":\"" + env.getProperty("onboarding.cliPushModelUrl") + "\"}";
        return new ResponseEntity<String>(response, HttpStatus.OK);
    }
	
	@RequestMapping(value = { APINames.CLI_AUTH_URL }, method = RequestMethod.GET)
    public ResponseEntity<String> getCliAuthUrl() {
		String response = "{\"data\":\"" +env.getProperty("onboarding.cliAuthUrl") + "\"}";
        return new ResponseEntity<String>(response, HttpStatus.OK);
    }
}
