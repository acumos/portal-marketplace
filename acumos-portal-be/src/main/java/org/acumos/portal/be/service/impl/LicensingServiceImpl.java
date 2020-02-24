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
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.acumos.cds.client.ICommonDataServiceRestClient;
import org.acumos.cds.domain.MLPLicenseProfileTemplate;
import org.acumos.licensemanager.client.LicenseProfile;
import org.acumos.licensemanager.client.model.RegisterAssetRequest;
import org.acumos.licensemanager.client.model.RegisterAssetResponse;
import org.acumos.licensemanager.client.rtu.LicenseAsset;
import org.acumos.licensemanager.exceptions.LicenseAssetRegistrationException;
import org.acumos.licensemanager.profilevalidator.exceptions.LicenseProfileException;
import org.acumos.licensemanager.profilevalidator.model.LicenseProfileValidationResults;
import org.acumos.nexus.client.NexusArtifactClient;
import org.acumos.portal.be.common.exception.AcumosServiceException;
import org.acumos.portal.be.service.LicensingService;
import org.acumos.portal.be.util.PortalConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.networknt.schema.ValidationMessage;

@Service
public class LicensingServiceImpl extends AbstractServiceImpl implements LicensingService {

	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Autowired
	private Environment env;
	
	/*
	 * No
	 */
	public LicensingServiceImpl() {

	}
	@Override
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
	public String validate(String jsonString)
			throws LicenseProfileException, AcumosServiceException {
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		LicenseProfile licenseProfile= new LicenseProfile(dataServiceRestClient);
		LicenseProfileValidationResults licenseProfileValidationResults=null;
		try {
			licenseProfileValidationResults=licenseProfile.validate(jsonString);
			Set<ValidationMessage> errMesgList=licenseProfileValidationResults.getJsonSchemaErrors();
			if(errMesgList == null || errMesgList.isEmpty()) {
				return "SUCCESS";
			}
				return errMesgList.toString();
		}catch (LicenseProfileException licExp){
			throw licExp;
		}
		 catch (Exception e) {
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.IO_EXCEPTION, e.getMessage());
		}
	}
	
	@Override
	public boolean licenseAssetRegister(String solutionId, String revisionId, String userId) {
		log.debug("Enter in register() ..."+" solutionId>>" +solutionId + "revisionId >>"+ revisionId + "userId >>"+  userId);
		boolean isLicenseAssetRegisterd = false;
		try {
			ICommonDataServiceRestClient dataServiceRestClient = getClient();
			NexusArtifactClient nexusArtifactClient= getNexusClient();
			LicenseAsset licenseAsset = new LicenseAsset(dataServiceRestClient, env.getProperty(PortalConstants.ENV_LUM_URL), nexusArtifactClient); 
			RegisterAssetRequest registerAssetRequest=new RegisterAssetRequest();
			registerAssetRequest.setSolutionId(UUID.fromString(solutionId));
			registerAssetRequest.setRevisionId(UUID.fromString(revisionId));
			registerAssetRequest.setLoggedIdUser(userId);
			RegisterAssetResponse response = licenseAsset.register(registerAssetRequest).get();
			
			if(response != null && ! response.isSuccess() ) {
				log.info("LicenseAsset registration response message : "+response.getMessage());
				isLicenseAssetRegisterd = false;
				
			}else if(response != null && response.isSuccess() ) {
				log.info("LicenseAsset registration successfull for solutionId: "+response.getSolutionId()+ " revisionId: " +response.getRevisionId());
				isLicenseAssetRegisterd = true;
			}
			else {
				log.info("LicenseAsset registration called sucessfully but response is null from LicenseAsset");
				isLicenseAssetRegisterd = false;
			}
			
		} 
		catch(LicenseAssetRegistrationException lare) {
			log.error("LicenseAssetRegistrationException in registering licence : "+lare.getMessage());
			return isLicenseAssetRegisterd = false;
		}		
		catch(Exception e) {
			log.error("Excetion in registering licence : "+e.getMessage());
			return isLicenseAssetRegisterd = false;
		}
		log.debug("Exit from register() ...");
		return isLicenseAssetRegisterd;
		
	}

}	  
