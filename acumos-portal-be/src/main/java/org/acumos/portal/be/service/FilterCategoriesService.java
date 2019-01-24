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
package org.acumos.portal.be.service;

import java.util.List;

import org.acumos.cds.domain.MLPCodeNamePair;

public interface FilterCategoriesService {
	/**
	 * Method to get the list of all Solutions types for Filter Categories
	 * 
	 * @return List of Solution Types
	 */
	List<MLPCodeNamePair> getSolutionCategoryTypes();
	
	List<MLPCodeNamePair> getSolutionAccessTypes();
	
	List<MLPCodeNamePair> getToolkitTypes();
	
	/**
	 *  Creates a Model Category Type in Database
	 *  
	 * @param mlpModelType Model Type Code to be created in the DB
	 * 
	 * @return the {@link MLPModelType} for UI use
	 */
	//MLPModelType createSolutionCategoryType(MLPModelType mlpModelType);
	
	/**
	 * Updates a Model Category Type in Database
	 * 
	 * @param mlpModelType Model Type Code to be created in the DB
	 * 
	 * @return true if updated successfully else false
	 */
	//boolean updateSolutionCategoryType(MLPModelType mlpModelType);
	
	/**
	 * Deletes a Model Category Type from DB based on the Category Type Code
	 * 
	 * @param categoryTypeCode Category Type Code identifier
	 * 
	 * @return true if deleted successfully else false
	 */
	//boolean deleteSolutionCategoryType(String categoryTypeCode);
	
}
