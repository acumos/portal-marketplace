package org.acumos.portal.be.common.exception;

public interface ServiceExceptions {
	default AcumosServiceException getEnvPropertyException(String property) {
		return new AcumosServiceException(AcumosServiceException.ErrorCode.INVALID_PARAMETER,
    			"Environment not configured: " + property);
	}
}
