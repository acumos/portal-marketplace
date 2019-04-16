package org.acumos.portal.be.service;

import java.net.ConnectException;

import org.acumos.portal.be.common.exception.AcumosServiceException;
import org.acumos.portal.be.transport.MSGeneration;
import org.acumos.portal.be.transport.MSResponse;
import org.springframework.http.ResponseEntity;

/**
 * Interface for Supporting MicroServices Operations
 */
public interface MSGenService {
	ResponseEntity<MSResponse> generateMicroservice(MSGeneration mSGeneration) throws InterruptedException, ConnectException ,AcumosServiceException;

}
