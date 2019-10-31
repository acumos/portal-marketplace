/*-
 * ===============LICENSE_START=======================================================
 * Acumos
 * ===================================================================================
 * Copyright (C) 2019 Nordix Foundation
 * ===================================================================================
 * This Acumos software file is distributed by Nordix Foundation
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

package org.acumos.portal.be.common;

import java.lang.invoke.MethodHandles;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.acumos.licensemanager.client.model.LicenseAction;
import org.acumos.licensemanager.client.model.LicenseRtuVerification;
import org.acumos.licensemanager.client.model.VerifyLicenseRequest;
import org.acumos.licensemanager.client.rtu.LicenseRtuVerifier;
import org.acumos.licensemanager.exceptions.RightToUseException;
import org.acumos.portal.be.service.impl.AbstractServiceImpl;
import org.acumos.portal.be.util.PortalConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

/**
 * Service to securely get the user name Rather than accessing the user name from the client / query
 * parameter
 */
@Service
public class RtuServiceImpl extends AbstractServiceImpl {

  private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  @Autowired
	private Environment env;

  /**
   * Performs a right to use check
   * 
   * Calls License Mgr Client Library which will call License Usage Manager service.
   * 
   * @param solutionId
   * @param revisionId
   * @param workflowId
   * @param assetUsageId
   * @return a license rtu verification in the future
   */
  public CompletableFuture<LicenseRtuVerification> performRtuCheck(String solutionId,
      String revisionId, String workflowId, String assetUsageId, String loggedInUserName) {
    LicenseAction action = null;
    action = LicenseAction.valueOf(workflowId.toUpperCase());
    assetUsageId = assetUsageId == null ? UUID.randomUUID().toString() : assetUsageId;
    LicenseRtuVerifier licenseVerifier =
        new LicenseRtuVerifier(getClient(), env.getProperty(PortalConstants.ENV_LUM_URL));
    VerifyLicenseRequest licenseDownloadRequest =
        new VerifyLicenseRequest(action, solutionId, revisionId, loggedInUserName, assetUsageId);
    licenseDownloadRequest.setAction(action);
    CompletableFuture<LicenseRtuVerification> verifyUserRTU = null;
    try {
      verifyUserRTU = licenseVerifier.verifyRtu(licenseDownloadRequest);
    } catch (RightToUseException e) {
      log.error("verifyUserRTU failed: {} ", verifyUserRTU);
    }
    return verifyUserRTU;
  }


}
