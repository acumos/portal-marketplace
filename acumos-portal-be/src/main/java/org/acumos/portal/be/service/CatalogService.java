/*-
 * ===============LICENSE_START=======================================================
 * Acumos
 * ===================================================================================
 * Copyright (C) 2019 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
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

package org.acumos.portal.be.service;

import java.util.List;

import org.acumos.cds.domain.MLPCatalog;
import org.acumos.cds.domain.MLPSolution;
import org.acumos.cds.transport.RestPageRequest;
import org.acumos.cds.transport.RestPageResponse;
import org.acumos.portal.be.transport.CatalogSearchRequest;
import org.acumos.portal.be.transport.MLCatalog;

public interface CatalogService {
	RestPageResponse<MLCatalog> getCatalogs(RestPageRequest pageRequest);
	
	RestPageResponse<MLPCatalog> searchCatalogs(CatalogSearchRequest catalogRequest);
	
	MLPCatalog getCatalog(String catalogId);
	
	MLPCatalog createCatalog(MLPCatalog catalog);
	
	void updateCatalog(MLPCatalog catalog);
	
	void deleteCatalog(String catalogId);
	
	List<String> getPeerAccessCatalogIds(String peerId);
	
	void addPeerAccessCatalog(String peerId, String catalogId);
	
	void dropPeerAccessCatalog(String peerId, String catalogId);
	
	long getCatalogSolutionCount(String catalogId);
	
	RestPageResponse<MLPSolution> getSolutionsInCatalogs(String[] catalogIds, RestPageRequest pageRequest);
	
	List<MLPCatalog> getSolutionCatalogs(String solutionId);
	
	void addSolutionToCatalog(String solutionId, String catalogId);
	
	void dropSolutionFromCatalog(String solutionId, String catalogId);
	
	List<String> getUserFavoriteCatalogIds(String userId);
	
	void addUserFavoriteCatalog(String userId, String catalogId);
	
	void dropUserFavoriteCatalog(String userId, String catalogId);
}
