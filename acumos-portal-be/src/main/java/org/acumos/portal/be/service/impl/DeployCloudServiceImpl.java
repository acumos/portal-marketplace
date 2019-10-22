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
import org.acumos.portal.be.transport.K8DeployRequest;
import org.acumos.portal.be.transport.MLK8SiteConfig;
import org.acumos.portal.be.util.PortalConstants;
import org.acumos.portal.be.util.PortalUtils;
import org.acumos.portal.be.util.URIBuildUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class DeployCloudServiceImpl extends AbstractServiceImpl implements DeployCloudService{

	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());	

	@Autowired
	Environment env;

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
			if(siteConfig !=null) {
				String jsonString=siteConfig.getConfigValue();
				List<K8ConfigValue> siteConfigValueList = Arrays.asList(mapper.readValue(jsonString, K8ConfigValue[].class));
				mlSiteConfig = PortalUtils.converToMLSiteConfig(siteConfig);
				mlSiteConfig.setK8ConfigValueList(siteConfigValueList);
			}
			
		} catch (IOException e) {
			log.debug("Exception occured while fetching Site Config:"+e);
			throw new AcumosServiceException(AcumosServiceException.ErrorCode.IO_EXCEPTION, e.getMessage());
		}
		return mlSiteConfig;
	}

	@Override
	public ResponseEntity<String> deployToK8(String userId, String solutionId, String revisionId, String envId) {
		
		String url=env.getProperty("k8_deploy.url");
		URI uri =URIBuildUtils.buildUri(url,new String[] { PortalConstants.DEPLOY_TO_K8 }, null);
		log.debug("deployToK8: uri {}", uri);
		RestTemplate restTemplate = URIBuildUtils.getRestTemplate(uri.toString());
		
		K8DeployRequest k8DeployRequest=new K8DeployRequest();
		k8DeployRequest.setUserId(userId);
		k8DeployRequest.setSolutionId(solutionId);
		k8DeployRequest.setRevisionId(revisionId);
		k8DeployRequest.setEnvId(envId);
		HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<K8DeployRequest> entity = new HttpEntity<K8DeployRequest>(k8DeployRequest, headers);
        ResponseEntity<String> response = restTemplate.exchange(uri,
                HttpMethod.POST, entity, String.class);
       return response;
	}

	
}
