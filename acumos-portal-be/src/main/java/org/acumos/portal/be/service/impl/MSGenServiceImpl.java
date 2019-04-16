package org.acumos.portal.be.service.impl;

import java.lang.invoke.MethodHandles;
import java.net.ConnectException;
import java.net.URI;
import org.acumos.portal.be.common.exception.AcumosServiceException;
import org.acumos.portal.be.service.MSGenService;
import org.acumos.portal.be.service.impl.AbstractServiceImpl;
import org.acumos.portal.be.transport.MSGeneration;
import org.acumos.portal.be.transport.MSResponse;
import org.acumos.portal.be.util.PortalUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class MSGenServiceImpl extends AbstractServiceImpl implements MSGenService {

	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Autowired
	private Environment env;

	private static final String ENV_MS_URL = "microservice.url";

	@Override
	public ResponseEntity<MSResponse> generateMicroservice(MSGeneration mSGeneration)
			throws InterruptedException, ConnectException, AcumosServiceException {
		log.debug("inside MSGenServiceImpl generateMicroservice");
		ResponseEntity<MSResponse> response = null;
		String url = "";
		try {
			url = PortalUtils.getEnvProperty(env, ENV_MS_URL);

			HttpHeaders requestHeaders = new HttpHeaders();
			// adding header params
			requestHeaders.add("Authorization", mSGeneration.getAuthorization());
			requestHeaders.add("tracking-id", mSGeneration.getTrackingID());
			requestHeaders.add("provider", mSGeneration.getProvider());
			requestHeaders.add("Request-ID", mSGeneration.getRequestId());

			// adding the query params to the URL
			UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(url)
					.queryParam("solutioId", mSGeneration.getSolutioId())
					.queryParam("revisionId", mSGeneration.getRevisionId())
					.queryParam("modName", mSGeneration.getModName())
					.queryParam("deployment_env", mSGeneration.getDeploymentEnv());

			log.debug("the uri is " + uriBuilder.toUriString());
			URI uri = uriBuilder.build().expand().encode().toUri();

			HttpEntity<String> httpEntity = new HttpEntity<String>(null, requestHeaders);
			RestTemplate restTemplate = new RestTemplate();
			response = restTemplate.exchange(uri, HttpMethod.POST, httpEntity, MSResponse.class);
			log.debug("status code is  " + response.getStatusCodeValue());
		} catch (Exception e) {
			log.error("Exception Occured while generating microservice" + e.getMessage(), e);
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
		}

		return response;
	}

}
