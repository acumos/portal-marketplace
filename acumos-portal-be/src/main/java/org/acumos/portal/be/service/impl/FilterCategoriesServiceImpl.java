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

/**
 * 
 */
package org.acumos.portal.be.service.impl;

import java.util.List;

import org.acumos.portal.be.service.FilterCategoriesService;
import org.acumos.portal.be.util.EELFLoggerDelegate;
import org.acumos.portal.be.util.PortalUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.acumos.cds.CodeNameType;
import org.acumos.cds.client.CommonDataServiceRestClientImpl;
import org.acumos.cds.client.ICommonDataServiceRestClient;
import org.acumos.cds.domain.MLPCodeNamePair;

@Service
public class FilterCategoriesServiceImpl extends AbstractServiceImpl implements FilterCategoriesService {

	private static final EELFLoggerDelegate log = EELFLoggerDelegate.getLogger(FilterCategoriesServiceImpl.class);
	
	@Autowired
	private Environment env;
	
	
	@Override
	public List<MLPCodeNamePair> getSolutionCategoryTypes() {
		log.debug(EELFLoggerDelegate.debugLogger, "getSolutionCategoryTypes");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		List<MLPCodeNamePair> mlpModelTypes = null;
		mlpModelTypes = dataServiceRestClient.getCodeNamePairs(CodeNameType.MODEL_TYPE);
		if(mlpModelTypes !=null) {
			log.debug(EELFLoggerDelegate.debugLogger, "getSolutionCategoryTypes : ", mlpModelTypes.size());
		}
		return mlpModelTypes;
	}
	
	@Override
	public List<MLPCodeNamePair> getSolutionAccessTypes() {
		log.debug(EELFLoggerDelegate.debugLogger, "getSolutionAccessTypes");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		List<MLPCodeNamePair> mlpAccessTypes = null;
		mlpAccessTypes = dataServiceRestClient.getCodeNamePairs(CodeNameType.ACCESS_TYPE);
		if(mlpAccessTypes !=null) {
			log.debug(EELFLoggerDelegate.debugLogger, "getSolutionCategoryTypes : ", mlpAccessTypes.size());
		}
		return mlpAccessTypes;
	}

	@Override
	public List<MLPCodeNamePair> getToolkitTypes() {
		log.debug(EELFLoggerDelegate.debugLogger, "getToolkitTypes");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		List<MLPCodeNamePair> mlpToolkitTypes = null;
		mlpToolkitTypes = dataServiceRestClient.getCodeNamePairs(CodeNameType.TOOLKIT_TYPE);
		if(mlpToolkitTypes !=null) {
			log.debug(EELFLoggerDelegate.debugLogger, "getToolkitTypes : ", mlpToolkitTypes.size());
		}
		return mlpToolkitTypes;
	}
	

	// Commenting Create, Update & Delete Filter Categories as of now based on the discussion with team. Any new values will be inserted from backend.
	/*@Deprecated
	@Override
	public MLPModelType createSolutionCategoryType(MLPModelType mlpModelType) {
		log.debug(EELFLoggerDelegate.debugLogger, "createSolutionCategoryType");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		List<MLPModelType> mlpModelTypes = dataServiceRestClient.getModelTypes();
		boolean isModelTypeExists = false;
		if(!PortalUtils.isEmptyList(mlpModelTypes)) {
			for(MLPModelType type : mlpModelTypes) {
				if(type != null && type.getTypeCode().equalsIgnoreCase(mlpModelType.getTypeCode())) {
					log.error(EELFLoggerDelegate.debugLogger, "Model Type code already exists");
					isModelTypeExists = true;
					break;
				}
			}
		}
		if(!isModelTypeExists) {
			mlpModelType = dataServiceRestClient.createModelType(mlpModelType);
			if(mlpModelType !=null) {
				log.debug(EELFLoggerDelegate.debugLogger, "createSolutionCategoryType : ", mlpModelType.toString());
			}
		} 
		return mlpModelType;
	}*/

	/*@Override
	public boolean updateSolutionCategoryType(MLPModelType mlpModelType) {
		log.debug(EELFLoggerDelegate.debugLogger, "updateSolutionCategoryType");
		boolean modelTypeUpdated = false;
		boolean isModelTypeExists = false;
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		List<MLPModelType> mlpModelTypes = dataServiceRestClient.getModelTypes();
		if(!PortalUtils.isEmptyList(mlpModelTypes)) {
			for(MLPModelType type : mlpModelTypes) {
				if(type != null && type.getTypeCode().equalsIgnoreCase(mlpModelType.getTypeCode())) {
					//Model Type Code already exists. Lets update it with new value.
					log.debug(EELFLoggerDelegate.debugLogger, "Model Type code already exists. Need to update");
					isModelTypeExists = true;
					break;
				}
			}
		}
		if(isModelTypeExists) {
			dataServiceRestClient.updateModelType(mlpModelType);
			modelTypeUpdated = true;
			if(mlpModelType !=null) {
				log.debug(EELFLoggerDelegate.debugLogger, "updateSolutionCategoryType : ", mlpModelType.toString());
			}
		} 
		return modelTypeUpdated;
	}

	@Override
	public boolean deleteSolutionCategoryType(String categoryTypeCode) {
		log.debug(EELFLoggerDelegate.debugLogger, "deleteSolutionCategoryType={}", categoryTypeCode );
		boolean modelTypeDeleted = false;
		boolean isModelTypeExists = false;
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		List<MLPModelType> mlpModelTypes = dataServiceRestClient.getModelTypes();
		if(!PortalUtils.isEmptyList(mlpModelTypes)) {
			for(MLPModelType type : mlpModelTypes) {
				if(type != null && type.getTypeCode().equalsIgnoreCase(categoryTypeCode)) {
					//Model Type Code already exists. Lets update it with new value.
					log.debug(EELFLoggerDelegate.debugLogger, "Model Type code already exists. Need to update");
					isModelTypeExists = true;
					break;
				}
			}
		}
		if(isModelTypeExists) {
			dataServiceRestClient.deleteModelType(categoryTypeCode);
			modelTypeDeleted = true;
			log.debug(EELFLoggerDelegate.debugLogger, "deleteSolutionCategoryType");
		} 
		return modelTypeDeleted;
	}*/

	public void setEnvironment(Environment environment){
		env = environment;
	}
}
