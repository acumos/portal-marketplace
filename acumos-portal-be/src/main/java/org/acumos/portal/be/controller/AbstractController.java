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

package org.acumos.portal.be.controller;

import java.lang.invoke.MethodHandles;
import java.util.List;

import org.acumos.cds.domain.MLPSolutionRevision;
import org.acumos.portal.be.service.MarketPlaceCatalogService;
import org.acumos.portal.be.util.PortalUtils;
import org.acumos.securityverification.domain.Workflow;
import org.acumos.securityverification.service.SecurityVerificationClientServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import com.fasterxml.jackson.databind.ObjectMapper;


public abstract class AbstractController {

	@Autowired
	private Environment env;

	@Autowired
	private MarketPlaceCatalogService catalogService;

	protected static final String APPLICATION_JSON = "application/json";
	
	private static final String ENV_CDMS_API = "cdms.client.url";
	private static final String ENV_CDMS_USER = "cdms.client.username";
	private static final String ENV_CDMS_PSWD = "cdms.client.password";
	private static final String ENV_NEXUS_API = "nexus.url";
	private static final String ENV_NEXUS_USER = "nexus.username";
	private static final String ENV_NEXUS_PSWD = "nexus.password";
	private static final String ENV_SV_ENABLED = "portal.feature.sv.enabled";
	private static final String ENV_SV_API = "portal.feature.sv.api";

	protected final ObjectMapper mapper;

	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	public AbstractController() {
		mapper = new ObjectMapper();
	}

	public Workflow performSVScan(String solutionId, String revisionId, String workflowId) {
		log.debug("performSVScan, solutionId=" + solutionId + ", revisionId=" + revisionId + ", workflowId=" + workflowId); 
		Workflow workflow = getValidWorkflow();
		if (Boolean.parseBoolean(env.getProperty(ENV_SV_ENABLED))) {
			try {
				SecurityVerificationClientServiceImpl sv = getSVClient();
				workflow = sv.securityVerificationScan(solutionId, revisionId, workflowId);
				if (!workflow.isWorkflowAllowed()) {
					String message = (!PortalUtils.isEmptyOrNullString(workflow.getReason())) ? workflow.getReason()
							: (!PortalUtils.isEmptyOrNullString(workflow.getSvException())) ? workflow.getSvException()
									: "Unknown problem occurred during security verification";
					workflow.setReason(message);
					log.error("Problem occurred during SV scan: ", message);
				}
			} catch (Exception e) {
				String message = (!PortalUtils.isEmptyOrNullString(workflow.getReason())) ? workflow.getReason()
						: (!PortalUtils.isEmptyOrNullString(workflow.getSvException())) ? workflow.getSvException()
								: "Unknown exception occurred during security verification";
				workflow = getInvalidWorkflow(message);
				log.error("Exception occurred during SV scan: ", message);
			}
		}
		return workflow;
	}

	public Workflow performSVScan(String solutionId, String workflowId) {
		log.debug("performSVScan, solutionId=" + solutionId + ", workflowId=" + workflowId); 
		Workflow workflow = getValidWorkflow();
		if (Boolean.parseBoolean(env.getProperty(ENV_SV_ENABLED))) {
			try {
				SecurityVerificationClientServiceImpl sv = getSVClient();
				List<MLPSolutionRevision> revs = catalogService.getSolutionRevision(solutionId);
				for (MLPSolutionRevision rev : revs) {
					workflow = sv.securityVerificationScan(solutionId, rev.getRevisionId(), workflowId);
					if (!workflow.isWorkflowAllowed()) {
						String message = (!PortalUtils.isEmptyOrNullString(workflow.getReason())) ? workflow.getReason()
								: (!PortalUtils.isEmptyOrNullString(workflow.getSvException())) ? workflow.getSvException()
										: "Unknown problem occurred during security verification";
						workflow.setReason(message);
						log.error("Problem occurred during SV scan: ", message);
						break;
					}
				}
			} catch (Exception e) {
				String message = (!PortalUtils.isEmptyOrNullString(workflow.getReason())) ? workflow.getReason()
						: (!PortalUtils.isEmptyOrNullString(workflow.getSvException())) ? workflow.getSvException()
								: "Unknown exception occurred during security verification";
				workflow = getInvalidWorkflow(message);
				log.error("Exception occurred during SV scan: ", message);
			}
		}
		return workflow;
	}
	
	private SecurityVerificationClientServiceImpl getSVClient() {
		return new SecurityVerificationClientServiceImpl(env.getProperty(ENV_SV_API), env.getProperty(ENV_CDMS_API),
				env.getProperty(ENV_CDMS_USER), env.getProperty(ENV_CDMS_PSWD), env.getProperty(ENV_NEXUS_API),
				env.getProperty(ENV_NEXUS_USER), env.getProperty(ENV_NEXUS_PSWD));
	}
	
	protected Workflow getValidWorkflow() {
		Workflow workflow = new Workflow();
		workflow.setWorkflowAllowed(true);
		return workflow;
	}

	protected Workflow getInvalidWorkflow(String message) {
		Workflow workflow = new Workflow();
		workflow.setWorkflowAllowed(false);
		workflow.setReason(message);
		return workflow;
	}
}
