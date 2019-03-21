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
package org.acumos.portal.be.controller;

import java.lang.invoke.MethodHandles;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acumos.cds.domain.MLPCodeNamePair;
import org.acumos.portal.be.APINames;
import org.acumos.portal.be.common.JSONTags;
import org.acumos.portal.be.common.JsonResponse;
import org.acumos.portal.be.security.jwt.TokenValidation;
import org.acumos.portal.be.service.FilterCategoriesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.ApiOperation;

@Controller
@RequestMapping(APINames.FILTER)
public class FilterCategoriesServiceController extends AbstractController {

	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());	

	@Autowired
	private FilterCategoriesService filterCategoriesService;

	public FilterCategoriesServiceController() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param request
	 *            HttpServletRequest
	 * 
	 * @param response
	 *            HttpServletResponse
	 * 
	 * @return List of ML Solutions Categories in JSON format.
	 */
	@ApiOperation(value = "Gets a list of ML Solutions Categories for Market Place Catalog.", response = MLPCodeNamePair.class, responseContainer = "List")
	@RequestMapping(value = { APINames.FILTER_MODELTYPE }, method = RequestMethod.GET, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<List<MLPCodeNamePair>> getSolutionsCategoryTypes(HttpServletRequest request,
			HttpServletResponse response) {

		Enumeration headerNames = request.getHeaderNames();
		String key = (String) headerNames.nextElement();
		String jwtToken = request.getHeader(key);

		TokenValidation tokenValidation = new TokenValidation();

		log.debug("getSolutionsCategoryTypes: Entering");
		List<MLPCodeNamePair> mlpModelTypes = null;
		JsonResponse<List<MLPCodeNamePair>> data = new JsonResponse<>();
		try {
			mlpModelTypes = filterCategoriesService.getSolutionCategoryTypes();
			if (mlpModelTypes != null) {
				data.setResponseBody(mlpModelTypes);
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
				data.setResponseDetail("ML Solutions Categories fetched Successfully");
				log.debug("getSolutionsCategoryTypes: size is {} ",
						mlpModelTypes.size());
			}
		} catch (Exception e) {
			data.setErrorCode(JSONTags.TAG_ERROR_CODE);
			data.setResponseDetail("Exception Occurred Fetching ML Solutions Categories for Market Place Catalog");
			log.error(
					"Exception Occurred Fetching ML Solutions Categories for Market Place Catalog", e);
		}
		return data;
	}

	/*
	 * @param request
	 *            HttpServletRequest
	 * 
	 * @param response
	 *            HttpServletResponse
	 * 
	 * @param mlpModelType
	 *            ML Solution Category to be created on the Platform
	 * 
	 * @return Returns ML Solution Category along with Response code as JSON
	 */
	/*
	 * @CrossOrigin
	 * 
	 * @ApiOperation(value =
	 * "Creates a ML Solution Category for Market Place Catalog.", response =
	 * MLPModelType.class)
	 * 
	 * @RequestMapping(value = {APINames.FILTER_MODELTYPE}, method =
	 * RequestMethod.POST, produces = APPLICATION_JSON)
	 * 
	 * @ResponseBody public JsonResponse<MLPModelType>
	 * createSolutionsCategoryType(HttpServletRequest request, HttpServletResponse
	 * response, MLPModelType mlpModelType) {
	 * log.debug("createSolutionsCategoryType: Entering"); JsonResponse<MLPModelType> data =
	 * new JsonResponse<>(); try { if(mlpModelType != null &&
	 * !PortalUtils.isEmptyOrNullString(mlpModelType.getTypeCode())) { mlpModelType
	 * = filterCategoriesService.createSolutionCategoryType(mlpModelType); }
	 * if(mlpModelType != null) { data.setResponseBody(mlpModelType);
	 * data.setResponseCode(JSONTags.TAG_ERROR_CODE_SUCCESS); //Need to remove error
	 * code. Only Use Response Code for both Success and Error Cases
	 * data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
	 * data.setResponseDetail("ML Solution Category created Successfully");
	 * response.setStatus(HttpServletResponse.SC_CREATED);
	 * log.debug("createSolutionsCategoryType:  ",
	 * mlpModelType.toString()); } else {
	 * data.setResponseCode(JSONTags.TAG_ERROR_CODE_EXCEPTION); //Need to remove
	 * error code. Only Use Response Code for both Success and Error Cases
	 * data.setErrorCode(JSONTags.TAG_ERROR_CODE_EXCEPTION);
	 * data.setResponseDetail("ML Solution Category cannot be created");
	 * response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); } } catch
	 * (Exception e) { data.setResponseCode(JSONTags.TAG_ERROR_CODE_FAILURE); //Need
	 * to remove error code. Only Use Response Code for both Success and Error Cases
	 * data.setErrorCode(JSONTags.TAG_ERROR_CODE_FAILURE);
	 * response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
	 * data.setResponseDetail("Exception Occurred Creating ML Solutions Category");
	 * log.error(
	 * "createSolutionsCategoryType: Exception Occurred creating ML Solutions Category"
	 * , e); } return data; }
	 */

	/*
	 * @param request
	 *            HttpServletRequest
	 * 
	 * @param response
	 *            HttpServletResponse
	 * 
	 * @param mlpModelType
	 *            ML Solution Category to be updated on the Platform
	 * 
	 * @return Returns Updated ML Solution Category along with Response code as JSON
	 */
	/*
	 * @CrossOrigin
	 * 
	 * @ApiOperation(value =
	 * "Updates a ML Solution Category for Market Place Catalog.", response =
	 * MLPModelType.class)
	 * 
	 * @RequestMapping(value = {APINames.FILTER_MODELTYPE}, method =
	 * RequestMethod.PUT, produces = APPLICATION_JSON)
	 * 
	 * @ResponseBody public JsonResponse<MLPModelType>
	 * updateSolutionsCategoryType(HttpServletRequest request, HttpServletResponse
	 * response, MLPModelType mlpModelType) {
	 * log.debug("updateSolutionsCategoryType: Entering"); 
	 * JsonResponse<MLPModelType> data =
	 * new JsonResponse<>(); boolean isSuccessfullyUpdated = false; try {
	 * if(mlpModelType != null &&
	 * !PortalUtils.isEmptyOrNullString(mlpModelType.getTypeCode())) {
	 * isSuccessfullyUpdated =
	 * filterCategoriesService.updateSolutionCategoryType(mlpModelType); }
	 * if(isSuccessfullyUpdated) { data.setResponseBody(mlpModelType);
	 * data.setResponseCode(JSONTags.TAG_ERROR_CODE_SUCCESS); //Need to remove error
	 * code. Only Use Response Code for both Success and Error Cases
	 * data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
	 * data.setResponseDetail("ML Solution Category updated Successfully");
	 * response.setStatus(HttpServletResponse.SC_OK);
	 * log.debug("updateSolutionsCategoryType:  ",
	 * mlpModelType.toString()); } else {
	 * data.setResponseCode(JSONTags.TAG_ERROR_CODE_EXCEPTION); //Need to remove
	 * error code. Only Use Response Code for both Success and Error Cases
	 * data.setErrorCode(JSONTags.TAG_ERROR_CODE_EXCEPTION);
	 * data.setResponseDetail("ML Solution Category cannot be updated");
	 * response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); } } catch
	 * (Exception e) { data.setResponseCode(JSONTags.TAG_ERROR_CODE_FAILURE); //Need
	 * to remove error code. Only Use Response Code for both Success and Error Cases
	 * data.setErrorCode(JSONTags.TAG_ERROR_CODE_FAILURE);
	 * response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
	 * data.setResponseDetail("Exception Occurred updating ML Solutions Category");
	 * log.error(
	 * "updateSolutionsCategoryType: Exception Occurred updating ML Solutions Category"
	 * , e); } return data; }
	 */

	/*
	 * @param request HttpServletRequest
	 * 
	 * @param response HttpServletResponse
	 * 
	 * @param categoryTypeCode ML Solution Category Code to be deleted from the
	 * Platform
	 * 
	 * @return Returns a response code as JSON for successful or failure transaction
	 */
	/*
	 * @CrossOrigin
	 * 
	 * @ApiOperation(value =
	 * "Updates a ML Solution Category for Market Place Catalog.", response =
	 * MLPModelType.class)
	 * 
	 * @RequestMapping(value = {APINames.FILTER_MODELTYPE_CODE}, method =
	 * RequestMethod.DELETE, produces = APPLICATION_JSON)
	 * 
	 * @ResponseBody public JsonResponse
	 * deleteSolutionsCategoryType(HttpServletRequest request, HttpServletResponse
	 * response, @PathVariable("categoryTypeCode") String categoryTypeCode) {
	 * log.debug("deleteSolutionsCategoryType: {}",
	 * categoryTypeCode); JsonResponse data = new JsonResponse<>(); boolean
	 * isSuccessfullyDeleted= false; try {
	 * if(!PortalUtils.isEmptyOrNullString(categoryTypeCode)) {
	 * isSuccessfullyDeleted =
	 * filterCategoriesService.deleteSolutionCategoryType(categoryTypeCode); }
	 * if(isSuccessfullyDeleted) {
	 * data.setResponseCode(JSONTags.TAG_ERROR_CODE_SUCCESS); //Need to remove error
	 * code. Only Use Response Code for both Success and Error Cases
	 * data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
	 * data.setResponseDetail("ML Solution Category deleted Successfully");
	 * response.setStatus(HttpServletResponse.SC_OK);
	 * log.debug("deleteSolutionsCategoryType:{}  ",
	 * categoryTypeCode); } else {
	 * data.setResponseCode(JSONTags.TAG_ERROR_CODE_EXCEPTION); //Need to remove
	 * error code. Only Use Response Code for both Success and Error Cases
	 * data.setErrorCode(JSONTags.TAG_ERROR_CODE_EXCEPTION);
	 * data.setResponseDetail("ML Solution Category cannot be deleted");
	 * response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); } } catch
	 * (Exception e) { data.setResponseCode(JSONTags.TAG_ERROR_CODE_FAILURE); //Need
	 * to remove error code. Only Use Response Code for both Success and Error Cases
	 * data.setErrorCode(JSONTags.TAG_ERROR_CODE_FAILURE);
	 * response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
	 * data.setResponseDetail("Exception Occurred deleted ML Solutions Category");
	 * log.error(
	 * "deleteSolutionsCategoryType: Exception Occurred deleting ML Solutions Category"
	 * , e); } return data; }
	 */
	@ApiOperation(value = "Gets a list of ML Solutions Access for Market Place Catalog.", response = MLPCodeNamePair.class, responseContainer = "List")
	@RequestMapping(value = { APINames.FILTER_ACCESSTYPE }, method = RequestMethod.GET, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<List<MLPCodeNamePair>> getSolutionsAccessTypes() {
		log.debug("getSolutionsAccessTypes: Entering");
		List<MLPCodeNamePair> mlpAccessTypes = null;
		JsonResponse<List<MLPCodeNamePair>> data = new JsonResponse<>();
		try {
			mlpAccessTypes = filterCategoriesService.getSolutionAccessTypes();
			if (mlpAccessTypes != null) {
				data.setResponseBody(mlpAccessTypes);
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
				data.setResponseDetail("ML Solutions Access type fetched Successfully");
				log.debug("getSolutionsAccessTypes: size is {} ",
						mlpAccessTypes.size());
			}
		} catch (Exception e) {
			data.setErrorCode(JSONTags.TAG_ERROR_CODE);
			data.setResponseDetail("Exception Occurred Fetching ML Solutions Access Types for Market Place Catalog");
			log.error(
					"Exception Occurred Fetching ML Solutions Access Type for Market Place Catalog", e);
		}
		return data;
	}

	/**
	 * @param request
	 *            HttpServletRequest
	 * 
	 * @param response
	 *            HttpServletResponse
	 * 
	 * @return Returns a response code as JSON for successful or failure transaction
	 */
	@ApiOperation(value = "Gets a list of ToolKit Type Categories for Market Place Catalog.", response = MLPCodeNamePair.class, responseContainer = "List")
	@RequestMapping(value = { APINames.FILTER_TOOLKITTYPE }, method = RequestMethod.GET, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<List<MLPCodeNamePair>> getToolkitTypes(HttpServletRequest request,
			HttpServletResponse response) {
		log.debug("getToolkitTypes: Entering");
		List<MLPCodeNamePair> mlpToolkitTypes = null;
		JsonResponse<List<MLPCodeNamePair>> data = new JsonResponse<>();
		try {
			mlpToolkitTypes = filterCategoriesService.getToolkitTypes();
			if (mlpToolkitTypes != null) {
				data.setResponseBody(mlpToolkitTypes);
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
				data.setResponseDetail("ML Solutions Categories fetched Successfully");
				log.debug("getSolutionsCategoryTypes: size is {} ",
						mlpToolkitTypes.size());
			}
		} catch (Exception e) {
			data.setErrorCode(JSONTags.TAG_ERROR_CODE);
			data.setResponseDetail("Exception Occurred Fetching ML Solutions Categories for Market Place Catalog");
			log.error(
					"Exception Occurred Fetching ML Solutions Categories for Market Place Catalog", e);
		}
		return data;
	}

}
