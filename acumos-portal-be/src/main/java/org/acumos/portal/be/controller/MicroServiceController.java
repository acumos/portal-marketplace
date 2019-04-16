package org.acumos.portal.be.controller;

import java.lang.invoke.MethodHandles;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.acumos.portal.be.APINames;
import org.acumos.portal.be.common.JSONTags;
import org.acumos.portal.be.common.JsonRequest;
import org.acumos.portal.be.common.JsonResponse;
import org.acumos.portal.be.service.MSGenService;
import org.acumos.portal.be.transport.MSGeneration;
import org.acumos.portal.be.transport.MSResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import io.swagger.annotations.ApiOperation;

@Controller
@RequestMapping("/microService")
public class MicroServiceController extends AbstractController {

	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Autowired
	private MSGenService mSGenService;

	@ApiOperation(value = "create microservice for the Solution.", response = JsonResponse.class)
	@RequestMapping(value = { APINames.CREATE_MICROSERVICE }, method = RequestMethod.POST, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<MSResponse> generateMicroservice(HttpServletRequest request,
			@RequestBody JsonRequest<MSGeneration> mSGeneration, HttpServletResponse response,
			@RequestHeader("Authorization") String authorization) {
		log.debug("inside MicroServiceController generateMicroservice");
		JsonResponse<MSResponse> data = new JsonResponse<>();
		ResponseEntity<MSResponse> mresponse = null;
		MSGeneration mGeneration = mSGeneration.getBody();
		mGeneration.setAuthorization(authorization);

		try {
			mresponse = mSGenService.generateMicroservice(mGeneration);
			data.setResponseBody(mresponse.getBody());
			data.setStatusCode(mresponse.getStatusCodeValue());
			data.setResponseDetail("generated microservice Successfully");
			data.setResponseCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
		} catch (Exception e) {
			data.setResponseCode(JSONTags.TAG_ERROR_CODE);
			data.setResponseDetail(e.getMessage());
			log.error("Exception Occurred while generating microservice", e);
		}
		return data;
	}
}
