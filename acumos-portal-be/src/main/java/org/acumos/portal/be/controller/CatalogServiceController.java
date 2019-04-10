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

package org.acumos.portal.be.controller;

import java.lang.invoke.MethodHandles;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acumos.cds.domain.MLPCatalog;
import org.acumos.cds.domain.MLPSolution;
import org.acumos.cds.transport.RestPageRequest;
import org.acumos.cds.transport.RestPageResponse;
import org.acumos.portal.be.APINames;
import org.acumos.portal.be.common.JSONTags;
import org.acumos.portal.be.common.JsonRequest;
import org.acumos.portal.be.common.JsonResponse;
import org.acumos.portal.be.service.CatalogService;
import org.acumos.portal.be.transport.CatalogSearchRequest;
import org.acumos.portal.be.util.SanitizeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.ApiOperation;

@Controller
@RequestMapping("/")
public class CatalogServiceController extends AbstractController {

	@Autowired
	CatalogService catalogService;

	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@ApiOperation(value = "Fetches catalogs, optionally sorted", response = MLPCatalog.class, responseContainer = "List")
	@RequestMapping(value = { APINames.GET_CATALOGS }, method = RequestMethod.POST, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<RestPageResponse<MLPCatalog>> getCatalogs(HttpServletRequest request,
			@RequestBody JsonRequest<RestPageRequest> pageRequestJson, HttpServletResponse response) {
		log.debug("getCatalogs");
		RestPageResponse<MLPCatalog> catalogs = null;
		JsonResponse<RestPageResponse<MLPCatalog>> data = new JsonResponse<>();
		try {
			catalogs = catalogService.getCatalogs(pageRequestJson.getBody());
			if (catalogs != null) {
				data.setResponseBody(catalogs);
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
				data.setResponseDetail("Catalog list fetched successfully");
			} else {
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_FAILURE);
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				data.setResponseDetail("Error occured while fetching catalogs");
				log.error("Error Occurred in Fetching Catalogs");
			}
		} catch (Exception e) {
			data.setErrorCode(JSONTags.TAG_ERROR_CODE_EXCEPTION);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			data.setResponseDetail("Exception Occurred Fetching Catalogs");
			log.error("Exception Occurred Fetching Catalogs", e);
		}
		return data;
	}

	@ApiOperation(value = "Search catalogs via query", response = MLPCatalog.class, responseContainer = "List")
	@RequestMapping(value = { APINames.SEARCH_CATALOGS }, method = RequestMethod.POST, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<RestPageResponse<MLPCatalog>> searchCatalogs(HttpServletRequest request,
			@RequestBody JsonRequest<CatalogSearchRequest> searchRequestJson, HttpServletResponse response) {
		log.debug("searchCatalogs");
		RestPageResponse<MLPCatalog> catalogs = null;
		JsonResponse<RestPageResponse<MLPCatalog>> data = new JsonResponse<>();
		try {
			catalogs = catalogService.searchCatalogs(searchRequestJson.getBody());
			if (catalogs != null) {
				data.setResponseBody(catalogs);
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
				data.setResponseDetail("Catalog list fetched successfully");
			} else {
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_FAILURE);
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				data.setResponseDetail("Error occured while searching catalogs");
				log.error("Error Occurred in Searching Catalogs");
			}
		} catch (Exception e) {
			data.setErrorCode(JSONTags.TAG_ERROR_CODE_EXCEPTION);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			data.setResponseDetail("Exception Occurred Searching Catalogs");
			log.error("Exception Occurred Searching Catalogs", e);
		}
		return data;
	}

	@ApiOperation(value = "Gets catalog by catalog ID", response = MLPCatalog.class)
	@RequestMapping(value = { APINames.GET_CATALOG }, method = RequestMethod.GET, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<MLPCatalog> getCatalog(HttpServletRequest request, @PathVariable String catalogId,
			HttpServletResponse response) {

		catalogId = SanitizeUtils.sanitize(catalogId);

		log.debug("getCatalog ={}", catalogId);
		MLPCatalog catalog = null;
		JsonResponse<MLPCatalog> data = new JsonResponse<>();
		try {
			catalog = catalogService.getCatalog(catalogId);
			if (catalog != null) {
				data.setResponseBody(catalog);
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
				data.setResponseDetail("Catalog fetched successfully");
			} else {
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_FAILURE);
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				data.setResponseDetail("Error occured while fetching catalog");
				log.error("Error Occurred in Fetching Catalog");
			}
		} catch (Exception e) {
			data.setErrorCode(JSONTags.TAG_ERROR_CODE_EXCEPTION);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			data.setResponseDetail("Exception Occurred Fetching Catalog");
			log.error("Exception Occurred Fetching Catalog", e);
		}
		return data;
	}

	@ApiOperation(value = "Creates new catalog", response = MLPCatalog.class)
	@RequestMapping(value = { APINames.CREATE_CATALOG }, method = RequestMethod.POST, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<MLPCatalog> createCatalog(HttpServletRequest request,
			@RequestBody JsonRequest<MLPCatalog> catalogJson, HttpServletResponse response) {
		log.debug("createCatalog");
		MLPCatalog catalog = null;
		JsonResponse<MLPCatalog> data = new JsonResponse<>();
		try {
			catalog = catalogService.createCatalog(catalogJson.getBody());
			if (catalog != null) {
				data.setResponseBody(catalog);
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
				data.setResponseDetail("Catalog created successfully");
			} else {
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_FAILURE);
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				data.setResponseDetail("Error occured while creating catalog");
				log.error("Error Occurred in Creating Catalog");
			}
		} catch (Exception e) {
			data.setErrorCode(JSONTags.TAG_ERROR_CODE_EXCEPTION);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			data.setResponseDetail("Exception Occurred Creating Catalog");
			log.error("Exception Occurred Creating Catalog", e);
		}
		return data;
	}

	@ApiOperation(value = "Updates catalog")
	@RequestMapping(value = { APINames.UPDATE_CATALOG }, method = RequestMethod.PUT, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<Object> updateCatalog(HttpServletRequest request,
			@RequestBody JsonRequest<MLPCatalog> catalogJson, HttpServletResponse response) {
		log.debug("updateCatalog");
		JsonResponse<Object> data = new JsonResponse<>();
		try {
			catalogService.updateCatalog(catalogJson.getBody());
			data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
			data.setResponseDetail("Catalog updated successfully");
		} catch (Exception e) {
			data.setErrorCode(JSONTags.TAG_ERROR_CODE_EXCEPTION);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			data.setResponseDetail("Exception Occurred Updating Catalog");
			log.error("Exception Occurred Updating Catalog", e);
		}
		return data;
	}

	@ApiOperation(value = "Deletes catalog")
	@RequestMapping(value = { APINames.DELETE_CATALOG }, method = RequestMethod.DELETE, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<Object> deleteCatalog(HttpServletRequest request, @PathVariable String catalogId,
			HttpServletResponse response) {
		log.debug("deleteCatalog");
		JsonResponse<Object> data = new JsonResponse<>();
		try {
			catalogService.deleteCatalog(catalogId);
			data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
			data.setResponseDetail("Catalog deleted successfully");
		} catch (Exception e) {
			data.setErrorCode(JSONTags.TAG_ERROR_CODE_EXCEPTION);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			data.setResponseDetail("Exception Occurred Deleting Catalog");
			log.error("Exception Occurred Deleting Catalog", e);
		}
		return data;
	}

	@ApiOperation(value = "Fetches list of catalog ids accessible by peer", response = String.class, responseContainer = "List")
	@RequestMapping(value = {
			APINames.GET_PEER_CATALOG_ACCESS }, method = RequestMethod.GET, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<List<String>> getPeerAccessCatalogIds(HttpServletRequest request, @PathVariable String peerId,
			HttpServletResponse response) {
		log.debug("getPeerAccessCatalogIds ={}", peerId);
		List<String> catalogIds = null;
		JsonResponse<List<String>> data = new JsonResponse<>();
		try {
			catalogIds = catalogService.getPeerAccessCatalogIds(peerId);
			if (catalogIds != null) {
				data.setResponseBody(catalogIds);
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
				data.setResponseDetail("Peer catalog access fetched successfully");
			} else {
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_FAILURE);
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				data.setResponseDetail("Error occured while fetching peer catalog access");
				log.error("Error Occurred Fetching Peer Catalog Access");
			}
		} catch (Exception e) {
			data.setErrorCode(JSONTags.TAG_ERROR_CODE_EXCEPTION);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			data.setResponseDetail("Exception Occurred Fetching Peer Catalog Access");
			log.error("Exception Occurred Fetching Peer Catalog Access", e);
		}
		return data;
	}

	@ApiOperation(value = "Adds catalog access for given peer")
	@RequestMapping(value = {
			APINames.ADD_PEER_CATALOG_ACCESS }, method = RequestMethod.POST, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<Object> addPeerCatalogAccess(HttpServletRequest request, @PathVariable String catalogId,
			@PathVariable String peerId, HttpServletResponse response) {
		log.debug("addPeerCatalogAccess");
		JsonResponse<Object> data = new JsonResponse<>();
		try {
			catalogService.addPeerAccessCatalog(peerId, catalogId);
			data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
			data.setResponseDetail("Catalog access added for peer successfully");
		} catch (Exception e) {
			data.setErrorCode(JSONTags.TAG_ERROR_CODE_EXCEPTION);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			data.setResponseDetail("Exception Occurred Adding Catalog Access for Peer");
			log.error("Exception Occurred Adding Catalog Access for Peer", e);
		}
		return data;
	}

	@ApiOperation(value = "Drops catalog access for given peer")
	@RequestMapping(value = {
			APINames.DROP_PEER_CATALOG_ACCESS }, method = RequestMethod.DELETE, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<Object> dropPeerCatalogAccess(HttpServletRequest request, @PathVariable String catalogId,
			@PathVariable String peerId, HttpServletResponse response) {
		log.debug("dropPeerCatalogAccess");
		JsonResponse<Object> data = new JsonResponse<>();
		try {
			catalogService.dropPeerAccessCatalog(peerId, catalogId);
			data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
			data.setResponseDetail("Catalog access dropped for peer successfully");
		} catch (Exception e) {
			data.setErrorCode(JSONTags.TAG_ERROR_CODE_EXCEPTION);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			data.setResponseDetail("Exception Occurred Dropping Catalog Access for Peer");
			log.error("Exception Occurred Dropping Catalog Access for Peer", e);
		}
		return data;
	}

	@ApiOperation(value = "Fetches count of solutions in given catalog", response = Long.class)
	@RequestMapping(value = {
			APINames.CATALOG_SOLUTION_COUNT }, method = RequestMethod.GET, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<Long> getCatalogSolutionCount(HttpServletRequest request, @PathVariable String catalogId,
			HttpServletResponse response) {
		log.debug("getCatalogSolutionCount ={}", catalogId);
		Long result = null;
		JsonResponse<Long> data = new JsonResponse<>();
		try {
			result = catalogService.getCatalogSolutionCount(catalogId);
			data.setResponseBody(result);
			data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
			data.setResponseDetail("Catalog solution count fetched successfully");
		} catch (Exception e) {
			data.setErrorCode(JSONTags.TAG_ERROR_CODE_EXCEPTION);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			data.setResponseDetail("Exception Occurred Fetching Catalog Solution Count");
			log.error("Exception Occurred Fetching Catalog Solution Count", e);
		}
		return data;
	}

	@ApiOperation(value = "Fetches solutions for any given catalogs", response = MLPSolution.class, responseContainer = "List")
	@RequestMapping(value = {
			APINames.SOLUTIONS_IN_CATALOGS }, method = RequestMethod.POST, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<RestPageResponse<MLPSolution>> getSolutionsInCatalogs(HttpServletRequest request,
			@RequestParam(name = "ctlg", required = true) String[] catalogIds, @RequestBody JsonRequest<RestPageRequest> pageRequestJson,
			HttpServletResponse response) {
		log.debug("getSolutionsInCatalogs");
		RestPageResponse<MLPSolution> solList = null;
		JsonResponse<RestPageResponse<MLPSolution>> data = new JsonResponse<>();
		try {
			solList = catalogService.getSolutionsInCatalogs(catalogIds, pageRequestJson.getBody());
			if (solList != null) {
				data.setResponseBody(solList);
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
				data.setResponseDetail("Catalog solutions fetched successfully");
			} else {
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_FAILURE);
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				data.setResponseDetail("Error occured while fetching catalog solutions");
				log.error("Error Occurred in Fetching Catalog Solutions");
			}
		} catch (Exception e) {
			data.setErrorCode(JSONTags.TAG_ERROR_CODE_EXCEPTION);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			data.setResponseDetail("Exception Occurred Fetching Catalog Solutions");
			log.error("Exception Occurred Fetching Catalog Solutions", e);
		}
		return data;
	}

	@ApiOperation(value = "Fetches catalogs to which a solution is published", response = MLPCatalog.class, responseContainer = "List")
	@RequestMapping(value = { APINames.GET_SOLUTION_CATALOGS }, method = RequestMethod.GET, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<List<MLPCatalog>> getSolutionCatalogs(HttpServletRequest request,
			@PathVariable String solutionId, HttpServletResponse response) {
		log.debug("getSolutionCatalogs ={}", solutionId);
		List<MLPCatalog> catalogList = null;
		JsonResponse<List<MLPCatalog>> data = new JsonResponse<>();
		try {
			catalogList = catalogService.getSolutionCatalogs(solutionId);
			if (catalogList != null) {
				data.setResponseBody(catalogList);
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
				data.setResponseDetail("Solution catalogs fetched successfully");
			} else {
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_FAILURE);
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				data.setResponseDetail("Error occured while fetching solution catalogs");
				log.error("Error Occurred in Fetching Solution Catalogs");
			}
		} catch (Exception e) {
			data.setErrorCode(JSONTags.TAG_ERROR_CODE_EXCEPTION);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			data.setResponseDetail("Exception Occurred Fetching Solution Catalogs");
			log.error("Exception Occurred Fetching Solution Catalogs", e);
		}
		return data;
	}

	@ApiOperation(value = "Adds solution to catalog")
	@RequestMapping(value = { APINames.ADD_CATALOG_SOLUTION }, method = RequestMethod.POST, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<Object> addSolutionToCatalog(HttpServletRequest request, @PathVariable String solutionId,
			@PathVariable String catalogId, HttpServletResponse response) {
		log.debug("addSolutionToCatalog ={}", solutionId);
		JsonResponse<Object> data = new JsonResponse<>();
		try {
			catalogService.addSolutionToCatalog(solutionId, catalogId);
			data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
			data.setResponseDetail("Solution added to catalog successfully");
		} catch (Exception e) {
			data.setErrorCode(JSONTags.TAG_ERROR_CODE_EXCEPTION);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			data.setResponseDetail("Exception Occurred Adding Solution to Catalog");
			log.error("Exception Occurred Adding Solution to Catalog", e);
		}
		return data;
	}

	@ApiOperation(value = "Drops solution from catalog")
	@RequestMapping(value = {
			APINames.DROP_CATALOG_SOLUTION }, method = RequestMethod.DELETE, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<Object> dropSolutionFromCatalog(HttpServletRequest request, @PathVariable String solutionId,
			@PathVariable String catalogId, HttpServletResponse response) {
		log.debug("dropSolutionFromCatalog ={}", solutionId);
		JsonResponse<Object> data = new JsonResponse<>();
		try {
			catalogService.dropSolutionFromCatalog(solutionId, catalogId);
			data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
			data.setResponseDetail("Solution dropped from catalog successfully");
		} catch (Exception e) {
			data.setErrorCode(JSONTags.TAG_ERROR_CODE_EXCEPTION);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			data.setResponseDetail("Exception Occurred Dropping Solution from Catalog");
			log.error("Exception Occurred Dropping Solution from Catalog", e);
		}
		return data;
	}

	@ApiOperation(value = "Fetches list of user favorite catalog ids", response = String.class, responseContainer = "List")
	@RequestMapping(value = {
			APINames.GET_USER_FAVORITE_CATALOGS }, method = RequestMethod.GET, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<List<String>> getUserFavoriteCatalogs(HttpServletRequest request, @PathVariable String userId,
			HttpServletResponse response) {
		log.debug("getUserFavoriteCatalogs ={}", userId);
		List<String> catalogIds = null;
		JsonResponse<List<String>> data = new JsonResponse<>();
		try {
			catalogIds = catalogService.getUserFavoriteCatalogIds(userId);
			if (catalogIds != null) {
				data.setResponseBody(catalogIds);
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
				data.setResponseDetail("User catalog favorites fetched successfully");
			} else {
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_FAILURE);
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				data.setResponseDetail("Error occured while fetching user favorite catalogs");
				log.error("Error Occurred Fetching User Favorite Catalogs");
			}
		} catch (Exception e) {
			data.setErrorCode(JSONTags.TAG_ERROR_CODE_EXCEPTION);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			data.setResponseDetail("Exception Occurred Fetching User Favorite Catalogs");
			log.error("Exception Occurred Fetching User Favorite Catalogs", e);
		}
		return data;
	}

	@ApiOperation(value = "Adds catalog to user favorites")
	@RequestMapping(value = {
			APINames.ADD_USER_FAVORITE_CATALOG }, method = RequestMethod.POST, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<Object> addUserFavoriteCatalog(HttpServletRequest request, @PathVariable String catalogId,
			@PathVariable String userId, HttpServletResponse response) {
		log.debug("addUserFavoriteCatalog");
		JsonResponse<Object> data = new JsonResponse<>();
		try {
			catalogService.addUserFavoriteCatalog(userId, catalogId);
			data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
			data.setResponseDetail("Catalog added to user favorites successfully");
		} catch (Exception e) {
			data.setErrorCode(JSONTags.TAG_ERROR_CODE_EXCEPTION);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			data.setResponseDetail("Exception Occurred Adding Catalog to User Favorites");
			log.error("Exception Occurred Adding Catalog to User Favorites", e);
		}
		return data;
	}

	@ApiOperation(value = "Drops catalog from user favorites")
	@RequestMapping(value = {
			APINames.DROP_USER_FAVORITE_CATALOG }, method = RequestMethod.DELETE, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<Object> dropUserFavoriteCatalog(HttpServletRequest request, @PathVariable String catalogId,
			@PathVariable String userId, HttpServletResponse response) {
		log.debug("dropUserFavoriteCatalog");
		JsonResponse<Object> data = new JsonResponse<>();
		try {
			catalogService.dropUserFavoriteCatalog(userId, catalogId);
			data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
			data.setResponseDetail("Catalog dropped from user favorites successfully");
		} catch (Exception e) {
			data.setErrorCode(JSONTags.TAG_ERROR_CODE_EXCEPTION);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			data.setResponseDetail("Exception Occurred Dropping Catalog from User Favorites");
			log.error("Exception Occurred Dropping Catalog from User Favorites", e);
		}
		return data;
	}
}
