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

package org.acumos.portal.be.service.impl;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import com.sun.mail.imap.Rights.Right;
import org.acumos.cds.client.ICommonDataServiceRestClient;
import org.acumos.cds.domain.MLPLicenseProfileTemplate;
import org.acumos.cds.domain.MLPRightToUse;
import org.acumos.cds.domain.MLPRole;
import org.acumos.cds.domain.MLPSolution;
import org.acumos.cds.domain.MLPUser;
import org.acumos.cds.transport.RestPageRequest;
import org.acumos.cds.transport.RestPageResponse;
import org.acumos.licensemanager.client.LicenseCreator;
import org.acumos.licensemanager.client.LicenseProfile;
import org.acumos.licensemanager.client.model.CreateRtuRequest;
import org.acumos.licensemanager.client.model.ICreatedRtuResponse;
import org.acumos.licensemanager.client.model.ILicenseCreator;
import org.acumos.licensemanager.exceptions.RightToUseException;
import org.acumos.licensemanager.profilevalidator.exceptions.LicenseProfileException;
import org.acumos.licensemanager.profilevalidator.model.LicenseProfileValidationResults;
import org.acumos.portal.be.common.exception.AcumosServiceException;
import org.acumos.portal.be.service.LicensingService;
import org.acumos.portal.be.transport.RtuUser;
import org.acumos.portal.be.util.PortalUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

@Service
public class LicensingServiceImpl extends AbstractServiceImpl implements LicensingService {

	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	/*
	 * No
	 */
	public LicensingServiceImpl() {

	}

	@Override
	public MLPSolution getMLPSolutions(long rtuId) throws AcumosServiceException {
		log.debug("getMLPSolution");
		MLPRightToUse mlpRightToUse = null;
		String solutionId = null;
		MLPSolution mlpSolution = null;
		try {
			ICommonDataServiceRestClient dataServiceRestClient = getClient();
			mlpRightToUse = dataServiceRestClient.getRightToUse(rtuId);
			solutionId = mlpRightToUse.getSolutionId();

			mlpSolution = dataServiceRestClient.getSolution(solutionId);

		} catch (IllegalArgumentException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INVALID_PARAMETER, e.getMessage());
		} catch (HttpClientErrorException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
		}
		return mlpSolution;
	}

	@Override
	public List<MLPUser> getMLPUsersAssociatedWithRtuId(long rtuId) throws AcumosServiceException {
		log.debug("getMLPUsers");
		List<MLPUser> mlpUsersAssociatedWithRtuId = null;
		try {
			ICommonDataServiceRestClient dataServiceRestClient = getClient();
			mlpUsersAssociatedWithRtuId = dataServiceRestClient.getRtuUsers(rtuId);
		} catch (IllegalArgumentException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INVALID_PARAMETER, e.getMessage());
		} catch (HttpClientErrorException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
		}
		return mlpUsersAssociatedWithRtuId;
	}

	@Override
	public RestPageResponse<MLPSolution> getMLPSolutionBySolutionName(Map<String, Object> solutoinNameParameter,
			boolean flag, RestPageRequest restPageRequest) throws AcumosServiceException {
		log.debug("getMLPSolutionBySolutionName");
		RestPageResponse<MLPSolution> mlpSolutionByServiceName = null;
		try {
			ICommonDataServiceRestClient dataServiceRestClient = getClient();
			mlpSolutionByServiceName = dataServiceRestClient.searchSolutions(solutoinNameParameter, false,
					new RestPageRequest());
		} catch (IllegalArgumentException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INVALID_PARAMETER, e.getMessage());
		} catch (HttpClientErrorException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
		}
		return mlpSolutionByServiceName;
	}

	@Override
	public List<RtuUser> getAllActiveUsers() {
		List<RtuUser> user = null;
		List<MLPUser> mlpUser = null;
		log.debug("getAllActiveUser");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		Map<String, Object> activeUser = new HashMap<>();
		activeUser.put("active", true);
		activeUser.put("size", 10000);
		RestPageResponse<MLPUser> userList = dataServiceRestClient.searchUsers(activeUser, false,
				new RestPageRequest());
		if (userList != null) {
			mlpUser = userList.getContent();
			if (!PortalUtils.isEmptyList(mlpUser)) {
				user = new ArrayList<>();
				for (MLPUser mlpusers : mlpUser) {
					RtuUser users = PortalUtils.convertToRtuUser(mlpusers, false);
					if (users.getUserId() != null) {
						List<MLPRole> mlprolelist = dataServiceRestClient.getUserRoles(users.getUserId());
						users.setUserAssignedRolesList(mlprolelist);
					}
					user.add(users);
				}
			}
		}
		return user;

	}

	@Override
	public List<MLPRightToUse> getRtusByReference(String rtuReferenceId) throws AcumosServiceException {
		log.debug("getRtusByReference");
		List<MLPRightToUse> rtuIds = null;
		try {
			ICommonDataServiceRestClient dataServiceRestClient = getClient();
			rtuIds = dataServiceRestClient.getRtusByReference(rtuReferenceId);
		} catch (IllegalArgumentException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INVALID_PARAMETER, e.getMessage());
		} catch (HttpClientErrorException e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
		}
		return rtuIds;
	}

	@Override
	public List<MLPRightToUse> createRtuUser(String rtuRefId, String solutionId, List<String> userList)
			throws Exception,RightToUseException {

		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		ILicenseCreator licenseSrvc = new LicenseCreator(dataServiceRestClient); 
		List<MLPRightToUse> createdRtus = new ArrayList<MLPRightToUse>();
		try {
				CreateRtuRequest createRtu = new CreateRtuRequest();
				createRtu.setSolutionId(solutionId);
				createRtu.setUserIds(userList);
				List<String> rtuRefIdList = Stream.of(rtuRefId).collect(Collectors.toList()); 
				createRtu.setRtuRefs(rtuRefIdList);
				ICreatedRtuResponse createdRtu = licenseSrvc.createRtu(createRtu);
				List<MLPRightToUse> rtus = createdRtu.getRtus();
				if(rtus != null){
					createdRtus.addAll(rtus);
				}
		}catch (RightToUseException rtuExp){
			throw rtuExp;
		}
		 catch (Exception e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.IO_EXCEPTION, e.getMessage());
		}

		return createdRtus;
	}

	@Override
	public List<MLPRightToUse> createRtuUser(String rtuRefId, String solutionId, boolean siteWide)
			throws Exception {
				ICommonDataServiceRestClient dataServiceRestClient = getClient();
				ILicenseCreator licenseSrvc = new LicenseCreator(dataServiceRestClient); 
				List<MLPRightToUse> createdRtus = new ArrayList<MLPRightToUse>();
				try {
						CreateRtuRequest createRtu = new CreateRtuRequest();
						createRtu.setSolutionId(solutionId);
						createRtu.setSiteWide(true);
						List<String> rtuRefIdList = Stream.of(rtuRefId).collect(Collectors.toList()); 
						createRtu.setRtuRefs(rtuRefIdList);
						ICreatedRtuResponse createRtu2 = licenseSrvc.createRtu(createRtu);
						List<MLPRightToUse> rtus = createRtu2.getRtus();
						if(rtus != null){
							createdRtus.addAll(rtus);
						}
				} catch (Exception e) {
					throw new AcumosServiceException(AcumosServiceException.ErrorCode.IO_EXCEPTION, e.getMessage());
				}
		
				return createdRtus;	}


	public final List<MLPLicenseProfileTemplate> getTemplates() throws LicenseProfileException, AcumosServiceException {
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		LicenseProfile LicenseProfile= new LicenseProfile(dataServiceRestClient);
		List<MLPLicenseProfileTemplate> templateList=new ArrayList<>();
		try {
			templateList=LicenseProfile.getTemplates();
		}catch (LicenseProfileException licExp){
			throw licExp;
		}
		 catch (Exception e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.IO_EXCEPTION, e.getMessage());
		}
		return templateList;
	}

	@Override
	public MLPLicenseProfileTemplate getTemplate(long templateId) throws LicenseProfileException, AcumosServiceException {
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		LicenseProfile LicenseProfile= new LicenseProfile(dataServiceRestClient);
		MLPLicenseProfileTemplate licenseProfileTemplate=null;
		try {
			licenseProfileTemplate=LicenseProfile.getTemplate(templateId);
		}catch (LicenseProfileException licExp){
			throw licExp;
		}
		 catch (Exception e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.IO_EXCEPTION, e.getMessage());
		}
		return licenseProfileTemplate;
	}

	@Override
	public LicenseProfileValidationResults validate(String jsonString)
			throws LicenseProfileException, AcumosServiceException {
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		LicenseProfile LicenseProfile= new LicenseProfile(dataServiceRestClient);
		LicenseProfileValidationResults licenseProfileValidationResults=null;
		try {
			licenseProfileValidationResults=LicenseProfile.validate(jsonString);
		}catch (LicenseProfileException licExp){
			throw licExp;
		}
		 catch (Exception e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.IO_EXCEPTION, e.getMessage());
		}
		return licenseProfileValidationResults;
	}

}	  