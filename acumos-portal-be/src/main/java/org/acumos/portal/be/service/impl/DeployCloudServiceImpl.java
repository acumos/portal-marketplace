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

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

import org.acumos.cds.client.ICommonDataServiceRestClient;
import org.acumos.cds.domain.MLPSiteConfig;
import org.acumos.portal.be.common.exception.AcumosServiceException;
import org.acumos.portal.be.service.DeployCloudService;
import org.acumos.portal.be.transport.K8ConfigValue;
import org.acumos.portal.be.transport.MLK8SiteConfig;
import org.acumos.portal.be.util.DeployK8Utils;
import org.acumos.portal.be.util.ElkClientConstants;
import org.acumos.portal.be.util.PortalConstants;
import org.acumos.portal.be.util.PortalUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class DeployCloudServiceImpl extends AbstractServiceImpl implements DeployCloudService{

	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());	
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Autowired
	DeployK8Utils deployK8Utils;
	@Override
	public MLK8SiteConfig getSiteConfig(String configKey) throws AcumosServiceException {
		log.debug("getSiteConfig ={}", configKey);
		MLPSiteConfig siteConfig = null;
		MLK8SiteConfig mlSiteConfig=null;
		ObjectMapper mapper = new ObjectMapper();
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		try {
			if (configKey != null) {
				siteConfig = dataServiceRestClient.getSiteConfig(configKey);
			}
			String jsonString=siteConfig.getConfigValue();
			List<K8ConfigValue> siteConfigValueList = Arrays.asList(mapper.readValue(jsonString, K8ConfigValue[].class));
			mlSiteConfig = PortalUtils.converToMLSiteConfig(siteConfig);
			mlSiteConfig.setK8ConfigValueList(siteConfigValueList);
		} catch (IOException e) {
			log.debug("Exception occured while fetching Site Config:"+e);
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.IO_EXCEPTION, e.getMessage());
		}
		return mlSiteConfig;
	}

	@Override
	public ResponseEntity<String> deployToK8(String userId, String solutionId, String revisionId, String envId) {
		
		URI uri =deployK8Utils.buildUri(new String[] { PortalConstants.DEPLOY_TO_K8 }, null);
		logger.debug("deployToK8: uri {}", uri);
		RestTemplate restTemplate = deployK8Utils.getRestTemplate(uri.toString());
		
		
		MultiValueMap<String, String> bodyMap = new LinkedMultiValueMap<String, String>();
		bodyMap.add("userId", userId);
		bodyMap.add("solutionId", solutionId);
		bodyMap.add("revisionId", revisionId);
		bodyMap.add("envId", envId);
		
		HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<MultiValueMap<String, String>>(bodyMap, headers);
       
        ResponseEntity<String> response = restTemplate.exchange(uri,
                HttpMethod.POST, requestEntity, String.class);
       return response;
	}

	
}
