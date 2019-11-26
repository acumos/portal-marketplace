package org.acumos.portal.be.controller;

import java.lang.invoke.MethodHandles;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acumos.portal.be.APINames;
import org.acumos.portal.be.common.JSONTags;
import org.acumos.portal.be.common.JsonRequest;
import org.acumos.portal.be.common.JsonResponse;
import org.acumos.portal.be.service.ElkService;
import org.acumos.portal.be.transport.ElasticStackIndiceResponse;
import org.acumos.portal.be.transport.ElasticStackIndices;
import org.acumos.portal.be.transport.ElkArchiveResponse;
import org.acumos.portal.be.transport.ElkArchive;
import org.acumos.portal.be.transport.ElkCreateSnapshotRequest;
import org.acumos.portal.be.transport.ElkDeleteSnapshotRequest;
import org.acumos.portal.be.transport.ElkGetRepositoriesResponse;
import org.acumos.portal.be.transport.ElkGetSnapshotsResponse;
import org.acumos.portal.be.transport.ElkRepositoriesRequest;
import org.acumos.portal.be.transport.ElkRepositoriesResponse;
import org.acumos.portal.be.transport.ElkRestoreSnapshotRequest;
import org.acumos.portal.be.transport.ElkSnapshotsResponse;
import org.acumos.portal.be.util.ElkClientConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.ApiOperation;

//import io.swagger.annotations.ApiOperation;

@Controller
@RequestMapping("/elk")
public class ElkController/* extends AbstractController */ {
	
	@Autowired
	ElkService elkService;

	protected static final String APPLICATION_JSON = "application/json";
	
	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@ApiOperation(value = "Creates elk repository", response = ElkRepositoriesResponse.class)
	@RequestMapping(value = { APINames.CREATE_REPOSITORY }, method = RequestMethod.POST, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<ElkRepositoriesResponse> createRepository(HttpServletRequest request,
			@RequestBody JsonRequest<ElkRepositoriesRequest> requestJson, HttpServletResponse response) {
		log.debug("createRepository");
		JsonResponse<ElkRepositoriesResponse> data = new JsonResponse<>();
		try {
			ElkRepositoriesRequest req=requestJson.getBody();
			req.setNodeTimeout(ElkClientConstants.NODE_TIMEOUT_WITH_UNIT);
			ElkRepositoriesResponse resp = elkService.createRepository(req);
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
			data.setResponseDetail(e.getMessage());
			log.error("Exception Occurred Creating Repository", e);
		}
		return data;
	}
	
	@ApiOperation(value = "Fetches elk repositories", response = ElkGetRepositoriesResponse.class)
	@RequestMapping(value = { APINames.GET_REPOSITORY }, method = RequestMethod.GET, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<ElkGetRepositoriesResponse> getAllRepositories(HttpServletRequest request, HttpServletResponse response) {
		log.debug("getAllRepositories");
		JsonResponse<ElkGetRepositoriesResponse> data = new JsonResponse<>();
		try {
			ElkGetRepositoriesResponse resp = elkService.getAllRepositories();
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
			data.setResponseDetail(e.getMessage());
			log.error("Exception Occurred Fetching Repositories", e);
			e.printStackTrace();
		}
		return data;
	}
	
	@ApiOperation(value = "Deletes elk repository", response = ElkRepositoriesResponse.class)
	@RequestMapping(value = { APINames.DELETE_REPOSITORY }, method = RequestMethod.POST, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<ElkRepositoriesResponse> deleteRepository(HttpServletRequest request,
			@RequestBody JsonRequest<ElkRepositoriesRequest> requestJson, HttpServletResponse response) {
		log.debug("deleteRepository");
		JsonResponse<ElkRepositoriesResponse> data = new JsonResponse<>();
		try {
			ElkRepositoriesRequest req = requestJson.getBody();
			req.setNodeTimeout(ElkClientConstants.NODE_TIMEOUT);
			ElkRepositoriesResponse resp = elkService.deleteRepository(req);
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
			data.setResponseDetail(e.getMessage());
			log.error("Exception Occurred Deleting Repository", e);
		}
		return data;
	}
	
	@ApiOperation(value = "Create snapshots", response = ElkSnapshotsResponse.class)
	@RequestMapping(value = { APINames.CREATE_SNAPSHOTS }, method = RequestMethod.POST, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<ElkSnapshotsResponse> createSnapshots(HttpServletRequest request,
			@RequestBody JsonRequest<ElkCreateSnapshotRequest> requestJson, HttpServletResponse response) {
		log.debug("createSnapshots");
		JsonResponse<ElkSnapshotsResponse> data = new JsonResponse<>();
		try {
			ElkCreateSnapshotRequest req=requestJson.getBody();
			req.setNodeTimeout(ElkClientConstants.NODE_TIMEOUT);
			ElkSnapshotsResponse resp = elkService.createSnapshots(req);
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
			data.setResponseDetail(e.getMessage());
			log.error("Exception Occurred Creating Snapshots", e);
		}
		return data;
	}
	
	@ApiOperation(value = "get all snapshots", response = ElkGetRepositoriesResponse.class)
	@RequestMapping(value = { APINames.GET_SNAPSHOTS }, method = RequestMethod.GET, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<ElkGetSnapshotsResponse> getAllSnapshots(HttpServletRequest request, HttpServletResponse response) {
		log.debug("getAllSnapshots");
		JsonResponse<ElkGetSnapshotsResponse> data = new JsonResponse<>();
		try {
			ElkGetSnapshotsResponse resp = elkService.getAllSnapshots();
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
			data.setResponseDetail(e.getMessage());
			log.error("Exception Occurred Fetching Snapshots", e);
		}
		return data;
	}
	
	@ApiOperation(value = "Deletes snapshots", response = ElkSnapshotsResponse.class)
	@RequestMapping(value = { APINames.DELETE_SNAPSHOTS }, method = RequestMethod.POST, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<ElkSnapshotsResponse> deleteSnapshots(HttpServletRequest request,
			@RequestBody JsonRequest<ElkDeleteSnapshotRequest> requestJson, HttpServletResponse response) {
		log.debug("deleteSnapshots");
		JsonResponse<ElkSnapshotsResponse> data = new JsonResponse<>();
		try {
			ElkDeleteSnapshotRequest req=requestJson.getBody();
			req.setNodeTimeout(ElkClientConstants.NODE_TIMEOUT);
			ElkSnapshotsResponse resp = elkService.deleteSnapshots(req);
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
			data.setResponseDetail(e.getMessage());
			log.error("Exception Occurred Deleting Snapshots", e);
		}
		return data;
	}
	
	@ApiOperation(value = "Restore snapshots", response = ElasticStackIndiceResponse.class)
	@RequestMapping(value = { APINames.RESTORE_SNAPSHOTS }, method = RequestMethod.POST, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<ElasticStackIndiceResponse> restoreSnapshots(HttpServletRequest request,
			@RequestBody JsonRequest<ElkRestoreSnapshotRequest> requestJson, HttpServletResponse response) {
		log.debug("restoreSnapshots");
		JsonResponse<ElasticStackIndiceResponse> data = new JsonResponse<>();
		try {
			ElkRestoreSnapshotRequest req=requestJson.getBody();
			req.setNodeTimeout(ElkClientConstants.NODE_TIMEOUT);
			ElasticStackIndiceResponse resp = elkService.restoreSnapshots(req);
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
			data.setResponseDetail(e.getMessage());
			log.error("Exception Occurred restore Snapshots", e);
		}
		return data;		
	}
	
	
	@ApiOperation(value = "Fetches indices", response = ElasticStackIndiceResponse.class)
	@RequestMapping(value = { APINames.GET_INDICES }, method = RequestMethod.GET, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<ElasticStackIndices> getIndices(HttpServletRequest request, HttpServletResponse response) {
		log.debug("getIndices");
		JsonResponse<ElasticStackIndices> data = new JsonResponse<>();
		try {
			ElasticStackIndices resp = elkService.getAllIndices();
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
			data.setResponseDetail(e.getMessage());
			log.error("Exception Occurred Fetching Repositories", e);
		}
		return data;
	}
	
	@ApiOperation(value = "Deletes indices", response = ElasticStackIndiceResponse.class)
	@RequestMapping(value = { APINames.DELETE_INDICES }, method = RequestMethod.POST, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<ElasticStackIndiceResponse> deleteIndices(HttpServletRequest request,
			@RequestBody JsonRequest<ElasticStackIndices> requestJson, HttpServletResponse response) {
		log.debug("deleteIndices");
		JsonResponse<ElasticStackIndiceResponse> data = new JsonResponse<>();
		try {
			ElasticStackIndiceResponse resp = elkService.deleteIndices(requestJson.getBody());
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
			data.setResponseDetail(e.getMessage());
			log.error("Exception Occurred Deleting Indices", e);
		}
		return data;
	}
	
	@ApiOperation(value = "Fetches archive info", response = ElkArchiveResponse.class)
	@RequestMapping(value = { APINames.GET_ARCHIVE }, method = RequestMethod.GET, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<ElkArchiveResponse> getArchive(HttpServletRequest request, HttpServletResponse response) {
		log.debug("getArchive");
		JsonResponse<ElkArchiveResponse> data = new JsonResponse<>();
		try {
			ElkArchiveResponse resp = elkService.getAllArchive();
			if (resp != null) {
				data.setResponseBody(resp);
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
				data.setResponseDetail("Archive fetched successfully");
			} else {
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_FAILURE);
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				data.setResponseDetail("Error occured while fetching archive");
				log.error("Error Occurred in Fetching archive");
			}
		} catch (Exception e) {
			data.setErrorCode(JSONTags.TAG_ERROR_CODE_EXCEPTION);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			data.setResponseDetail(e.getMessage());
			log.error("Exception Occurred Fetching Archive", e);
		}
		return data;
	}
	
	@ApiOperation(value = "Create/Restore/Delete Archive", response = ElkArchiveResponse.class)
	@RequestMapping(value = { APINames.ARCHIVE_ACTION }, method = RequestMethod.POST, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<ElkArchiveResponse> archiveAction(HttpServletRequest request,
			@RequestBody JsonRequest<ElkArchive> requestJson, HttpServletResponse response) {
		log.debug("createRestoreArchive");
		JsonResponse<ElkArchiveResponse> data = new JsonResponse<>();
		try {
			ElkArchiveResponse resp = elkService.archiveAction(requestJson.getBody());
			if (resp.getArchiveInfo() != null) {
				data.setResponseBody(resp);
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
				data.setResponseDetail(" Archive created/restored successfully");
			} else {
				data.setResponseBody(resp);
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_FAILURE);
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				data.setResponseDetail("Error occured while creating/restoring Archive");
				log.error("Error Occurred in Creating Archive");
			}
		} catch (Exception e) {
			data.setErrorCode(JSONTags.TAG_ERROR_CODE_EXCEPTION);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			data.setResponseDetail(e.getMessage());
			log.error("Exception Occurred Creating/restoring Archive", e);
		}
		return data;
	}
}