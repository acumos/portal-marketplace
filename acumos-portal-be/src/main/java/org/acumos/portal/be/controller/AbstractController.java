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

import java.util.List;

import org.acumos.cds.domain.MLPSolutionRevision;
import org.acumos.portal.be.service.MarketPlaceCatalogService;
import org.acumos.securityverification.domain.Workflow;
import org.acumos.securityverification.service.SecurityVerificationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class AbstractController {

	@Autowired
	private Environment env;

	@Autowired
	private MarketPlaceCatalogService catalogService;

	protected static final String APPLICATION_JSON = "application/json";

	protected final ObjectMapper mapper;

	public AbstractController() {
		mapper = new ObjectMapper();
	}

	public Workflow performSVScan(String solutionId, String revisionId, String workflowId) throws Exception {
		Workflow workflow = getDefaultWorkflow();
		if (Boolean.parseBoolean(env.getProperty("portal.feature.sv.enabled"))) {
			SecurityVerificationServiceImpl sv = getSVClient();
			workflow = sv.securityVerificationScan(solutionId, revisionId, workflowId);
		}
		return workflow;
	}

	public Workflow performSVScan(String solutionId, String workflowId) throws Exception {
		Workflow workflow = getDefaultWorkflow();
		if (Boolean.parseBoolean(env.getProperty("portal.feature.sv.enabled"))) {
			SecurityVerificationServiceImpl sv = getSVClient();
			List<MLPSolutionRevision> revs = catalogService.getSolutionRevision(solutionId);
			for (MLPSolutionRevision rev : revs) {
				workflow = sv.securityVerificationScan(solutionId, rev.getRevisionId(), workflowId);
				if (!workflow.isWorkflowAllowed()) {
					break;
				}
			}
		}
		return workflow;
	}
	
	private SecurityVerificationServiceImpl getSVClient() {
		return new SecurityVerificationServiceImpl();
		/* // For when SV constructor is updated:
		 * return new SecurityVerificationServiceImpl(env.getProperty("portal.feature.sv.api"));
		 */
	}
	
	protected Workflow getDefaultWorkflow() {
		Workflow workflow = new Workflow();
		workflow.setWorkflowAllowed(true);
		return workflow;
	}
}
