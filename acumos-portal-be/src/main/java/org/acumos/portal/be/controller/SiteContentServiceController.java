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
import java.util.concurrent.TimeUnit;

import org.acumos.cds.domain.MLPSiteContent;
import org.acumos.portal.be.APINames;
import org.acumos.portal.be.common.JSONTags;
import org.acumos.portal.be.common.JsonRequest;
import org.acumos.portal.be.common.JsonResponse;
import org.acumos.portal.be.service.SiteContentService;
import org.acumos.portal.be.transport.HomePageModuleNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.ApiOperation;

@Controller
@RequestMapping(APINames.SITE_PATH)
public class SiteContentServiceController extends AbstractController {

	@Autowired
	SiteContentService siteContentService;

	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());	

	@ApiOperation(value = "Gets terms and conditions ", response = MLPSiteContent.class)
	@RequestMapping(value = { APINames.GET_TERMS_CONDITIONS }, method = RequestMethod.GET, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<String> getTermsConditions() {
		log.debug("getTermsCondition");
		MLPSiteContent content = null;
		JsonResponse<String> data = new JsonResponse<>();
		try {
			content = siteContentService.getTermsConditions();
			if (content != null) {
				data.setResponseBody(new String(content.getContentValue()));
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
				data.setResponseDetail("Terms and conditions fetched successfully");
			}
				else {
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_FAILURE);
				data.setResponseDetail("Error occured while getting Term and Condition");
				log.error("Error Occurred in Fetching Contact Info :");
			}
		} catch (Exception e) {
			data.setErrorCode(JSONTags.TAG_ERROR_CODE);
			data.setResponseDetail("Exception Occurred Fetching Terms and Conditions");
			log.error("Exception Occurred Fetching Terms and Conditions", e);
		}
		return data;
	}

	@ApiOperation(value = "Updates terms and conditions ", response = JsonResponse.class)
	@RequestMapping(value = {
			APINames.UPDATE_TERMS_CONDITIONS }, method = RequestMethod.POST, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<Object> updateTermsConditions(@RequestBody JsonRequest<MLPSiteContent> content) {
		log.debug("updateTermsConditions");
		JsonResponse<Object> data = new JsonResponse<>();
		MLPSiteContent mlpContent = content.getBody();
		if (mlpContent != null) {
			try {
				siteContentService.setTermsConditions(mlpContent);
				data.setStatus(true);
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
				data.setResponseDetail("Terms and conditions updated successfully");
			} catch (Exception e) {
				data.setErrorCode(JSONTags.TAG_ERROR_CODE);
				data.setResponseDetail("Exception Occurred Updating Terms and Conditions");
				log.error("Exception Occurred Updating Terms and Conditions", e);
			}
		}
		return data;
	}

	@ApiOperation(value = "Gets onboarding overview ", response = MLPSiteContent.class)
	@RequestMapping(value = {
			APINames.GET_ONBOARDING_OVERVIEW }, method = RequestMethod.GET, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<MLPSiteContent> getOnboardingOverview() {
		log.debug("getOnboardingOverview");
		MLPSiteContent content = null;
		JsonResponse<MLPSiteContent> data = new JsonResponse<>();
		try {
			content = siteContentService.getOnboardingOverview();
			if (content != null) {
				data.setResponseBody(content);
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
				data.setResponseDetail("Onboarding overview fetched successfully");
			}
		} catch (Exception e) {
			data.setErrorCode(JSONTags.TAG_ERROR_CODE);
			data.setResponseDetail("Exception Occurred Fetching Onboarding Overview");
			log.error("Exception Occurred Fetching Onboarding Overview", e);
		}
		return data;
	}

	@ApiOperation(value = "Updates onboarding overview ", response = JsonResponse.class)
	@RequestMapping(value = {
			APINames.UPDATE_ONBOARDING_OVERVIEW }, method = RequestMethod.POST, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<Object> updateOnboardingOverview(@RequestBody JsonRequest<MLPSiteContent> content) {
		log.debug("updateOnboardingOverview");
		JsonResponse<Object> data = new JsonResponse<>();
		MLPSiteContent mlpContent = content.getBody();
		if (mlpContent != null) {
			try {
				siteContentService.setOnboardingOverview(mlpContent);
				data.setStatus(true);
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
				data.setResponseDetail("Onboarding overview updated successfully");
			} catch (Exception e) {
				data.setErrorCode(JSONTags.TAG_ERROR_CODE);
				data.setResponseDetail("Exception Occurred Updating Onboarding Overview");
				log.error("Exception Occurred Updating Onboarding Overview", e);
			}
		}
		return data;
	}

	@ApiOperation(value = "Gets footer contact information ", response = MLPSiteContent.class)
	@RequestMapping(value = { APINames.GET_CONTACT_INFO }, method = RequestMethod.GET, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<String> getContactInfo() {
		log.debug("getContactInfo");
		MLPSiteContent content = null;
		JsonResponse<String> data = new JsonResponse<>();
		
		
		try {
			content = siteContentService.getContactInfo();
			if (content != null) {
				
				data.setResponseBody(new String(content.getContentValue()));
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
				data.setResponseDetail("Contact info fetched successfully");
			}
			else {
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_FAILURE);
				data.setResponseDetail("Error occured while getting contact Information");
				log.error("Error Occurred in Fetching Contact Info :");
			}
		} catch (Exception e) {
			data.setErrorCode(JSONTags.TAG_ERROR_CODE);
			data.setResponseDetail("Exception Occurred Fetching Contact Info");
			log.error("Exception Occurred Fetching Contact Info", e);
		}
		return data;
	}

	@ApiOperation(value = "Updates footer contact information ", response = JsonResponse.class)
	@RequestMapping(value = { APINames.UPDATE_CONTACT_INFO }, method = RequestMethod.POST, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<Object> updateContactInfo(@RequestBody JsonRequest<MLPSiteContent> content) {
		log.debug("updateContactInfo");
		JsonResponse<Object> data = new JsonResponse<>();
		MLPSiteContent mlpContent = content.getBody();
		if (mlpContent != null) {
			try {
				siteContentService.setContactInfo(mlpContent);
				data.setStatus(true);
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
				data.setResponseDetail("Contact info updated successfully");
			} catch (Exception e) {
				data.setErrorCode(JSONTags.TAG_ERROR_CODE);
				data.setResponseDetail("Exception Occurred Updating Contact Info");
				log.error("Exception Occurred Updating Contact Info", e);
			}
		}
		return data;
	}

	@ApiOperation(value = "Gets cobranding Logo ", response = ResponseEntity.class)
	@RequestMapping(value = {
			APINames.GET_COBRAND_LOGO }, method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
	@ResponseBody
	public ResponseEntity<byte[]> getCobrandLogo() {
		log.debug("getCobrandLogo");
		MLPSiteContent content = null;
		ResponseEntity<byte[]> resp = null;
		try {
			content = siteContentService.getCobrandLogo();
			if (content != null) {
				resp = ResponseEntity.ok().cacheControl(CacheControl.maxAge(1, TimeUnit.HOURS).cachePublic())
						.body(content.getContentValue());
			} else {
				resp = ResponseEntity.noContent().build();
			}
		} catch (Exception e) {
			resp = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
			log.error("Exception Occurred Fetching Cobrand Logo", e);
		}
		return resp;
	}

	@ApiOperation(value = "Updates cobranding Logo ", response = JsonResponse.class)
	@RequestMapping(value = { APINames.UPDATE_COBRAND_LOGO }, method = RequestMethod.POST, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<Object> updateCobrandLogo(@RequestBody JsonRequest<MLPSiteContent> content) {
		log.debug("updateCobrandLogo");
		JsonResponse<Object> data = new JsonResponse<>();
		MLPSiteContent picture = content.getBody();
		if (picture != null) {
			try {
				siteContentService.setCobrandLogo(picture);
				data.setStatus(true);
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
				data.setResponseDetail("Cobrand Logo updated successfully");
			} catch (Exception e) {
				data.setErrorCode(JSONTags.TAG_ERROR_CODE);
				data.setResponseDetail("Exception Occurred Updating Cobrand Logo");
				log.error("Exception Occurred Updating Cobrand Logo", e);
			}
		}
		return data;
	}

	@ApiOperation(value = "Deletes cobranding logo ", response = JsonResponse.class)
	@RequestMapping(value = {
			APINames.DELETE_COBRAND_LOGO }, method = RequestMethod.DELETE, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<Object> deleteCobrandLogo() {
		log.debug("deleteCobrandLogo");
		JsonResponse<Object> data = new JsonResponse<>();
		try {
			siteContentService.deleteCobrandLogo();
			data.setStatus(true);
			data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
			data.setResponseDetail("Cobrand Logo deleted successfully");
		} catch (Exception e) {
			data.setErrorCode(JSONTags.TAG_ERROR_CODE);
			data.setResponseDetail("Exception Occurred Deleting Cobrand Logo");
			log.error("Exception Occurred Deleting Cobrand Logo", e);
		}
		return data;
	}

	@ApiOperation(value = "Gets carousel picture ", response = ResponseEntity.class)
	@RequestMapping(value = {
			APINames.GET_CAROUSEL_PICTURE }, method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
	@ResponseBody
	public ResponseEntity<byte[]> getCarouselPicture(@PathVariable("key") String key) {
		log.debug("getCarouselPicture");
		MLPSiteContent content = null;
		ResponseEntity<byte[]> resp = null;
		try {
			content = siteContentService.getCarouselPicture(key);
			if (content != null) {
				resp = ResponseEntity.ok().cacheControl(CacheControl.maxAge(1, TimeUnit.HOURS))
						.body(content.getContentValue());
			} else {
				resp = ResponseEntity.noContent().build();
			}
		} catch (Exception e) {
			resp = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
			log.error("Exception Occurred Fetching Carousel Picture", e);
		}
		return resp;
	}

	@ApiOperation(value = "Updates carousel picture ", response = JsonResponse.class)
	@RequestMapping(value = {
			APINames.UPDATE_CAROUSEL_PICTURE }, method = RequestMethod.POST, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<Object> updateCarouselPicture(@RequestBody JsonRequest<MLPSiteContent> content) {
		log.debug("updateCarouselPicture");
		JsonResponse<Object> data = new JsonResponse<>();
		MLPSiteContent picture = content.getBody();
		if (picture != null) {
			try {
				siteContentService.setCarouselPicture(picture);
				data.setStatus(true);
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
				data.setResponseDetail("Carousel picture updated successfully");
			} catch (Exception e) {
				data.setErrorCode(JSONTags.TAG_ERROR_CODE);
				data.setResponseDetail("Exception Occurred Updating Carousel Picture");
				log.error("Exception Occurred Updating Carousel Picture", e);
			}
		}
		return data;
	}

	@ApiOperation(value = "Deletes carousel picture ", response = JsonResponse.class)
	@RequestMapping(value = {
			APINames.DELETE_CAROUSEL_PICTURE }, method = RequestMethod.DELETE, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<Object> updateCarouselPicture(@PathVariable("key") String key) {
		log.debug("deleteCarouselPicture");
		JsonResponse<Object> data = new JsonResponse<>();
		try {
			siteContentService.deleteCarouselPicture(key);
			data.setStatus(true);
			data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
			data.setResponseDetail("Carousel picture updated successfully");
		} catch (Exception e) {
			data.setErrorCode(JSONTags.TAG_ERROR_CODE);
			data.setResponseDetail("Exception Occurred Deleting Carousel Picture");
			log.error("Exception Occurred Deleting Carousel Picture", e);
		}
		return data;
	}
	
	@ApiOperation(value = "Gets Discover Acumos information ", response = MLPSiteContent.class)
	@RequestMapping(value = { APINames.GET_DISCOVERACUMOS_INFO }, method = RequestMethod.GET, produces = APPLICATION_JSON)
	@ResponseBody
	public JsonResponse<HomePageModuleNames> getDiscoverAcumosInfo() {
		log.debug("getDiscoverAcumosInfo");
		
		JsonResponse<HomePageModuleNames> data = new JsonResponse<>();
		
		
		try {
			HomePageModuleNames content = siteContentService.getDiscoverAcumosInfo();
			if (content != null) {
				
				data.setResponseBody(content);
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_SUCCESS);
				data.setResponseDetail("Discover Acumos info fetched successfully");
			}
			else {
				data.setErrorCode(JSONTags.TAG_ERROR_CODE_FAILURE);
				data.setResponseDetail("Error occured while getting Discover Acumos info");
				log.error("Error Occurred in Fetching Discover Acumos info :");
			}
		} catch (Exception e) {
			data.setErrorCode(JSONTags.TAG_ERROR_CODE);
			data.setResponseDetail("Exception Occurred Fetching Discover Acumos info");
			log.error("Exception Occurred Fetching Discover Acumos info", e);
		}
		return data;
	}
}
