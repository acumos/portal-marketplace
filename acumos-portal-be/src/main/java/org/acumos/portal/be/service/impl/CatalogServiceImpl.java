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

package org.acumos.portal.be.service.impl;

import java.lang.invoke.MethodHandles;
import java.util.List;

import org.acumos.cds.client.ICommonDataServiceRestClient;
import org.acumos.cds.domain.MLPCatalog;
import org.acumos.cds.domain.MLPSolution;
import org.acumos.cds.transport.RestPageRequest;
import org.acumos.cds.transport.RestPageResponse;
import org.acumos.portal.be.service.CatalogService;
import org.acumos.portal.be.transport.CatalogSearchRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CatalogServiceImpl extends AbstractServiceImpl implements CatalogService {

	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Override
	public RestPageResponse<MLPCatalog> getCatalogs(RestPageRequest pageRequest) {
		log.debug("getCatalogs");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		return dataServiceRestClient.getCatalogs(pageRequest);
	}

	@Override
	public RestPageResponse<MLPCatalog> searchCatalogs(CatalogSearchRequest catalogRequest) {
		log.debug("searchCatalogs");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		return dataServiceRestClient.searchCatalogs(catalogRequest.paramsMap(), catalogRequest.isOr(),
				catalogRequest.getPageRequest());
	}

	@Override
	public MLPCatalog getCatalog(String catalogId) {
		log.debug("getCatalog ={}", catalogId);
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		return dataServiceRestClient.getCatalog(catalogId);
	}

	@Override
	public MLPCatalog createCatalog(MLPCatalog catalog) {
		log.debug("createCatalog : ", catalog);
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		return dataServiceRestClient.createCatalog(catalog);
	}

	@Override
	public void updateCatalog(MLPCatalog catalog) {
		log.debug("updateCatalog ={}", catalog.getCatalogId());
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		dataServiceRestClient.updateCatalog(catalog);
	}

	@Override
	public void deleteCatalog(String catalogId) {
		log.debug("deleteCatalog ={}", catalogId);
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		dataServiceRestClient.deleteCatalog(catalogId);
	}
	
	@Override
	public List<String> getPeerAccessCatalogIds(String peerId) {
		log.debug("getPeerCatalogs ={}", peerId);
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		return dataServiceRestClient.getPeerAccessCatalogIds(peerId);
	}
	
	@Override
	public void addPeerAccessCatalog(String peerId, String catalogId) {
		log.debug("addPeerAccessCatalog : peerId=" + peerId + ", catalogId="+ catalogId);
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		dataServiceRestClient.addPeerAccessCatalog(peerId, catalogId);
	}
	
	@Override
	public void dropPeerAccessCatalog(String peerId, String catalogId) {
		log.debug("removePeerAccessCatalog : peerId=" + peerId + ", catalogId="+ catalogId);
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		dataServiceRestClient.dropPeerAccessCatalog(peerId, catalogId);
	}

	@Override
	public long getCatalogSolutionCount(String catalogId) {
		log.debug("getCatalogSolutionCount ={}", catalogId);
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		return dataServiceRestClient.getCatalogSolutionCount(catalogId);
	}

	@Override
	public RestPageResponse<MLPSolution> getSolutionsInCatalogs(String[] catalogIds, RestPageRequest pageRequest) {
		log.debug("getSolutionsInCatalogs");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		return dataServiceRestClient.getSolutionsInCatalogs(catalogIds, pageRequest);
	}

	@Override
	public List<MLPCatalog> getSolutionCatalogs(String solutionId) {
		log.debug("getSolutionCatalogs ={}", solutionId);
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		return dataServiceRestClient.getSolutionCatalogs(solutionId);
	}

	@Override
	public void addSolutionToCatalog(String solutionId, String catalogId) {
		log.debug("addSolutionToCatalog : solution " + solutionId + " into catalog " + catalogId);
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		dataServiceRestClient.addSolutionToCatalog(solutionId, catalogId);
	}

	@Override
	public void dropSolutionFromCatalog(String solutionId, String catalogId) {
		log.debug("dropSolutionFromCatalog : solution " + solutionId + " from catalog " + catalogId);
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		dataServiceRestClient.dropSolutionFromCatalog(solutionId, catalogId);
	}

	@Override
	public List<String> getUserFavoriteCatalogIds(String userId) {
		log.debug("getUserFavoriteCatalogIds ={}", userId);
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		return dataServiceRestClient.getUserFavoriteCatalogIds(userId);
	}

	@Override
	public void addUserFavoriteCatalog(String userId, String catalogId) {
		log.debug("addUserFavoriteCatalog : user=" + userId + ", catalog=" + catalogId);
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		dataServiceRestClient.addUserFavoriteCatalog(userId, catalogId);
	}

	@Override
	public void dropUserFavoriteCatalog(String userId, String catalogId) {
		log.debug("addUserFavoriteCatalog : user=" + userId + ", catalog=" + catalogId);
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		dataServiceRestClient.dropUserFavoriteCatalog(userId, catalogId);
	}
}
