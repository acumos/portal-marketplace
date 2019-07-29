package org.acumos.portal.be.controller;

import java.lang.invoke.MethodHandles;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acumos.portal.be.APINames;
import org.acumos.portal.be.common.JSONTags;
import org.acumos.portal.be.common.JsonResponse;
import org.acumos.portal.be.service.ElkService;
import org.acumos.portal.be.transport.ElkSnapshotsResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

//import io.swagger.annotations.ApiOperation;

@Controller
@RequestMapping("/elk")
public class ElkController extends AbstractController  {
	
	@Autowired
	ElkService elkService;

	protected static final String APPLICATION_JSON = "application/json";
	
	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
/*
	@ApiOperation(value = "Creates elk repository", response = String.class)
	@RequestMapping(value = { APINames.CREATE_REPOSITORY }, method = RequestMethod.POST, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<String> createRepository(HttpServletRequest request,
			@RequestBody JsonRequest<ElkRepositoriesRequest> requestJson, HttpServletResponse response) {
		log.debug("createRepository");
		JsonResponse<String> data = new JsonResponse<>();
		try {
			String resp = elkService.createRepository(requestJson.getBody());
			if (resp != null) {
				data.setResponseBody(resp);
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
				data.setResponseDetail("Repository created successfully");
			} else {
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_FAILURE);
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				data.setResponseDetail("Error occured while creating repository");
				log.error("Error Occurred in Creating Repository");
			}
		} catch (Exception e) {
			data.setErrorCode(JSONTags.TAG_ERROR_CODE_EXCEPTION);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			data.setResponseDetail("Exception Occurred Creating Repository");
			log.error("Exception Occurred Creating Repository", e);
		}
		return data;
	}
	
//	@ApiOperation(value = "Fetches elk repositories", response = ElkGetRepositoriesResponse.class)
	@RequestMapping(value = { APINames.GET_REPOSITORY }, method = RequestMethod.GET, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<ElkSnapshotsResponse> getAllRepositories(HttpServletRequest request, HttpServletResponse response) {
		log.debug("getAllRepositories");
		JsonResponse<ElkSnapshotsResponse> data = new JsonResponse<>();
		try {
			ElkSnapshotsResponse resp = elkService.getAllRepositories();
			if (resp != null) {
				data.setResponseBody(resp);
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
				data.setResponseDetail("Repositories fetched successfully");
			} else {
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_FAILURE);
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				data.setResponseDetail("Error occured while fetching repositories");
				log.error("Error Occurred in Fetching Repositories");
			}
		} catch (Exception e) {
			data.setErrorCode(JSONTags.TAG_ERROR_CODE_EXCEPTION);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			data.setResponseDetail("Exception Occurred Fetching Repositories");
			log.error("Exception Occurred Fetching Repositories", e);
			e.printStackTrace();
		}
		return data;
	}
	
	@ApiOperation(value = "Deletes elk repository", response = String.class)
	@RequestMapping(value = { APINames.DELETE_REPOSITORY }, method = RequestMethod.POST, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<String> deleteRepository(HttpServletRequest request,
			@RequestBody JsonRequest<ElkRepositoriesRequest> requestJson, HttpServletResponse response) {
		log.debug("deleteRepository");
		JsonResponse<String> data = new JsonResponse<>();
		try {
			String resp = elkService.deleteRepository(requestJson.getBody());
			if (resp != null) {
				data.setResponseBody(resp);
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
				data.setResponseDetail("Repository deleted successfully");
			} else {
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_FAILURE);
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				data.setResponseDetail("Error occured while deleting repository");
				log.error("Error Occurred in Deleting Repository");
			}
		} catch (Exception e) {
			data.setErrorCode(JSONTags.TAG_ERROR_CODE_EXCEPTION);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			data.setResponseDetail("Exception Occurred Deleting Repository");
			log.error("Exception Occurred Deleting Repository", e);
		}
		return data;
	}
	
	@ApiOperation(value = "Create snapshots", response = String.class)
	@RequestMapping(value = { APINames.CREATE_SNAPSHOTS }, method = RequestMethod.POST, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<String> createSnapshots(HttpServletRequest request,
			@RequestBody JsonRequest<ElkCreateSnapshotRequest> requestJson, HttpServletResponse response) {
		log.debug("createRepository");
		JsonResponse<String> data = new JsonResponse<>();
		try {
			String resp = elkService.createSnapshots(requestJson.getBody());
			if (resp != null) {
				data.setResponseBody(resp);
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
				data.setResponseDetail("Snapshots created successfully");
			} else {
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_FAILURE);
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				data.setResponseDetail("Error occured while creating Snapshots");
				log.error("Error Occurred in Creating Snapshots");
			}
		} catch (Exception e) {
			data.setErrorCode(JSONTags.TAG_ERROR_CODE_EXCEPTION);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			data.setResponseDetail("Exception Occurred Creating Snapshots");
			log.error("Exception Occurred Creating Snapshots", e);
		}
		return data;
	}
	*/
//	@ApiOperation(value = "get all snapshots", response = ElkGetRepositoriesResponse.class)
	@RequestMapping(value = { APINames.GET_SNAPSHOTS }, method = RequestMethod.GET, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<ElkSnapshotsResponse> getAllSnapshots(HttpServletRequest request, HttpServletResponse response) {
		log.debug("getAllSnapshots");
		JsonResponse<ElkSnapshotsResponse> data = new JsonResponse<>();
		try {
			ElkSnapshotsResponse resp = elkService.getAllSnapshots();
			if (resp != null) {
				data.setResponseBody(resp);
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
				data.setResponseDetail("Snapshots fetched successfully");
			} else {
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_FAILURE);
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				data.setResponseDetail("Error occured while fetching Snapshots");
				log.error("Error Occurred in Fetching Snapshots");
			}
		} catch (Exception e) {
			data.setErrorCode(JSONTags.TAG_ERROR_CODE_EXCEPTION);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			data.setResponseDetail("Exception Occurred Fetching Snapshots");
			log.error("Exception Occurred Fetching Snapshots", e);
		}
		return data;
	}
	/*
	@ApiOperation(value = "Deletes snapshots", response = String.class)
	@RequestMapping(value = { APINames.DELETE_SNAPSHOTS }, method = RequestMethod.POST, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<String> deleteSnapshots(HttpServletRequest request,
			@RequestBody JsonRequest<ElkDeleteSnapshotRequest> requestJson, HttpServletResponse response) {
		log.debug("deleteSnapshots");
		JsonResponse<String> data = new JsonResponse<>();
		try {
			String resp = elkService.deleteSnapshots(requestJson.getBody());
			if (resp != null) {
				data.setResponseBody(resp);
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
				data.setResponseDetail("Snapshots deleted successfully");
			} else {
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_FAILURE);
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				data.setResponseDetail("Error occured while deleting Snapshots");
				log.error("Error Occurred in Deleting Snapshots");
			}
		} catch (Exception e) {
			data.setErrorCode(JSONTags.TAG_ERROR_CODE_EXCEPTION);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			data.setResponseDetail("Exception Occurred Deleting Snapshots");
			log.error("Exception Occurred Deleting Snapshots", e);
		}
		return data;
	}
	
	@ApiOperation(value = "Restore snapshots", response = String.class)
	@RequestMapping(value = { APINames.RESTORE_SNAPSHOTS }, method = RequestMethod.POST, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<String> restoreSnapshots(HttpServletRequest request,
			@RequestBody JsonRequest<ElkRestoreSnapshotRequest> requestJson, HttpServletResponse response) {
		log.debug("deleteSnapshots");
		JsonResponse<String> data = new JsonResponse<>();
		try {
			String resp = elkService.restoreSnapshots(requestJson.getBody());
			if (resp != null) {
				data.setResponseBody(resp);
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
				data.setResponseDetail("Snapshots restore successfully");
			} else {
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_FAILURE);
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				data.setResponseDetail("Error occured while restore Snapshots");
				log.error("Error Occurred in restore Snapshots");
			}
		} catch (Exception e) {
			data.setErrorCode(JSONTags.TAG_ERROR_CODE_EXCEPTION);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			data.setResponseDetail("Exception Occurred restore Snapshots");
			log.error("Exception Occurred restore Snapshots", e);
		}
		return data;		
	}
	
	
	@ApiOperation(value = "Fetches indices", response = ElasticStackIndiceResponse.class)
	@RequestMapping(value = { APINames.GET_INDICES }, method = RequestMethod.GET, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<ElasticStackIndiceResponse> getIndices(HttpServletRequest request, HttpServletResponse response) {
		log.debug("getIndices");
		JsonResponse<ElasticStackIndiceResponse> data = new JsonResponse<>();
		try {
			ElasticStackIndiceResponse resp = elkService.getAllIndices();
			if (resp != null) {
				data.setResponseBody(resp);
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
				data.setResponseDetail("Indices fetched successfully");
			} else {
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_FAILURE);
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				data.setResponseDetail("Error occured while fetching repositories");
				log.error("Error Occurred in Fetching Repositories");
			}
		} catch (Exception e) {
			data.setErrorCode(JSONTags.TAG_ERROR_CODE_EXCEPTION);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			data.setResponseDetail("Exception Occurred Fetching Repositories");
			log.error("Exception Occurred Fetching Repositories", e);
		}
		return data;
	}
	
	@ApiOperation(value = "Deletes indices", response = String.class)
	@RequestMapping(value = { APINames.DELETE_INDICES }, method = RequestMethod.POST, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<String> deleteIndices(HttpServletRequest request,
			@RequestBody JsonRequest<ElasticStackIndices> requestJson, HttpServletResponse response) {
		log.debug("deleteIndices");
		JsonResponse<String> data = new JsonResponse<>();
		try {
			String resp = elkService.deleteIndices(requestJson.getBody());
			if (resp != null) {
				data.setResponseBody(resp);
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
				data.setResponseDetail("Indices deleted successfully");
			} else {
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_FAILURE);
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				data.setResponseDetail("Error occured while deleting Indices");
				log.error("Error Occurred in Deleting Indices");
			}
		} catch (Exception e) {
			data.setErrorCode(JSONTags.TAG_ERROR_CODE_EXCEPTION);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			data.setResponseDetail("Exception Occurred Deleting Indices");
			log.error("Exception Occurred Deleting Indices", e);
		}
		return data;
	}
	*/
}