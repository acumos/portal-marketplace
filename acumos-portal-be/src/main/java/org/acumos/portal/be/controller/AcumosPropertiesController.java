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
public class AcumosPropertiesController {
	
	@Autowired
	private Environment env;
	
	@RequestMapping(value = { APINames.CLI_PUSH_URL }, method = RequestMethod.GET)
    public ResponseEntity<String> getCliPushUrl() {
		String response = "{\"data\":\"" + env.getProperty("onboarding.cliPushUrl") + "\"}";
        return new ResponseEntity<String>(response, HttpStatus.OK);
    }
	
	@RequestMapping(value = { APINames.CLI_AUTH_URL }, method = RequestMethod.GET)
    public ResponseEntity<String> getCliAuthUrl() {
		String response = "{\"data\":\"" +env.getProperty("onboarding.cliAuthUrl") + "\"}";
        return new ResponseEntity<String>(response, HttpStatus.OK);
    }
}
