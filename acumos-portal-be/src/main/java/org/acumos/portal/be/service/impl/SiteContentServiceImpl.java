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

import org.acumos.cds.client.ICommonDataServiceRestClient;
import org.acumos.cds.domain.MLPSiteContent;
import org.acumos.portal.be.service.SiteContentService;
import org.acumos.portal.be.transport.HomePageModuleNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SiteContentServiceImpl extends AbstractServiceImpl implements SiteContentService {

	public static final String KEY_TERMS_CONDITIONS = "global.termsConditions";
	public static final String KEY_COBRAND_LOGO = "global.coBrandLogo";
	public static final String KEY_ONBOARDING_OVERVIEW = "global.onboarding.overview";
	public static final String KEY_CONTACT_INFO = "global.footer.contactInfo";
	
	public static final String KEY_DISCOVER_AC_Market = "global.discoverAcumos.marketPlace";
	public static final String KEY_DISCOVER_AC_DS = "global.discoverAcumos.designStudio";
	public static final String KEY_DISCOVER_AC_SONAP = "global.discoverAcumos.sdnOnap";
	public static final String KEY_DISCOVER_AC_PTOOLKIT = "global.discoverAcumos.preferredToolkit";
	public static final String KEY_DISCOVER_AC_TEAMUP = "global.discoverAcumos.teamUp";
	
	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());	

	@Override
	public MLPSiteContent getTermsConditions() {
		log.debug("getTermsConditions");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		return dataServiceRestClient.getSiteContent(KEY_TERMS_CONDITIONS);
	}

	@Override
	public void setTermsConditions(MLPSiteContent content) {
		log.debug("setTermsConditions");
		content.setContentKey(KEY_TERMS_CONDITIONS);
		createOrUpdateContent(content);
	}

	@Override
	public MLPSiteContent getCobrandLogo() {
		log.debug("getCobrandLogo");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		return dataServiceRestClient.getSiteContent(KEY_COBRAND_LOGO);
	}

	@Override
	public void setCobrandLogo(MLPSiteContent picture) {
		log.debug("setCobrandLogo");
		picture.setContentKey(KEY_COBRAND_LOGO);
		createOrUpdateContent(picture);
	}

	@Override
	public void deleteCobrandLogo() {
		log.debug("deleteCobrandLogo");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		dataServiceRestClient.deleteSiteContent(KEY_COBRAND_LOGO);
	}

	@Override
	public MLPSiteContent getOnboardingOverview() {
		log.debug("getOnboardingOverview");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		return dataServiceRestClient.getSiteContent(KEY_ONBOARDING_OVERVIEW);
	}

	@Override
	public void setOnboardingOverview(MLPSiteContent content) {
		log.debug("setOnboardingOverview");
		content.setContentKey(KEY_ONBOARDING_OVERVIEW);
		createOrUpdateContent(content);
	}

	@Override
	public MLPSiteContent getContactInfo() {
		log.debug("getContactInfo");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		return dataServiceRestClient.getSiteContent(KEY_CONTACT_INFO);
	}

	@Override
	public void setContactInfo(MLPSiteContent content) {
		log.debug("setContactInfo");
		content.setContentKey(KEY_CONTACT_INFO);
		createOrUpdateContent(content);
	}

	@Override
	public MLPSiteContent getCarouselPicture(String key) {
		log.debug("getCarouselPicture ={}", key);
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		return dataServiceRestClient.getSiteContent(key);
	}

	@Override
	public void setCarouselPicture(MLPSiteContent picture) {
		log.debug("setCarouselPicture ={}", picture.getContentKey());
		createOrUpdateContent(picture);
	}

	@Override
	public void deleteCarouselPicture(String key) {
		log.debug("deleteCarouselPicture ={}", key);
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		dataServiceRestClient.deleteSiteContent(key);
	}

	private void createOrUpdateContent(MLPSiteContent content) {
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		MLPSiteContent existing = dataServiceRestClient.getSiteContent(content.getContentKey());
		if (existing != null) {
			dataServiceRestClient.updateSiteContent(content);
		} else {
			dataServiceRestClient.createSiteContent(content);
		}
	}

	

	@Override
	public HomePageModuleNames getDiscoverAcumosInfo() {
		log.debug("getDiscoverAcumosInfo");
		HomePageModuleNames homePageModuleNames=new HomePageModuleNames();
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		homePageModuleNames.setMarketPlace(new String(dataServiceRestClient.getSiteContent(KEY_DISCOVER_AC_Market).getContentValue()));
		homePageModuleNames.setDesignStudio(new String(dataServiceRestClient.getSiteContent(KEY_DISCOVER_AC_DS).getContentValue()));;
		homePageModuleNames.setSdnOnap(new String(dataServiceRestClient.getSiteContent(KEY_DISCOVER_AC_SONAP).getContentValue()));;
		homePageModuleNames.setPreferredToolkit(new String(dataServiceRestClient.getSiteContent(KEY_DISCOVER_AC_PTOOLKIT).getContentValue()));;
		homePageModuleNames.setTeamUp(new String(dataServiceRestClient.getSiteContent(KEY_DISCOVER_AC_TEAMUP).getContentValue()));;
		return homePageModuleNames;
	}

}
