package org.acumos.portal.be.controller;

import org.acumos.portal.be.APINames;
import org.acumos.portal.be.config.AcumosOnboardingProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(APINames.PROPERTIES)
public class AcumosOnboardingPropertiesController {
	
	@Autowired
    private AcumosOnboardingProperties props;
	
	@RequestMapping(value = { APINames.CLI_PUSH_URL }, method = RequestMethod.GET)
    public ResponseEntity<String> getCliPushUrl() {
		String response = "{\"data\":\"" + props.getCliPushUrl() + "\"}";
        return new ResponseEntity<String>(response, HttpStatus.OK);
    }
	
	@RequestMapping(value = { APINames.CLI_AUTH_URL }, method = RequestMethod.GET)
    public ResponseEntity<String> getCliAuthUrl() {
		String response = "{\"data\":\"" + props.getCliAuthUrl() + "\"}";
        return new ResponseEntity<String>(response, HttpStatus.OK);
    }
}
