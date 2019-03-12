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

import org.acumos.cds.client.ICommonDataServiceRestClient;
import org.acumos.cds.domain.MLPSiteContent;
import org.acumos.portal.be.service.SiteContentService;
import org.acumos.portal.be.util.EELFLoggerDelegate;
import org.springframework.stereotype.Service;

@Service
public class SiteContentServiceImpl extends AbstractServiceImpl implements SiteContentService {

	public static final String KEY_TERMS_CONDITIONS = "global.termsCondition";
	public static final String KEY_COBRAND_LOGO = "global.coBrandLogo";
	public static final String KEY_ONBOARDING_OVERVIEW = "global.onboarding.overview";
	public static final String KEY_CONTACT_INFO = "global.footer.contactInfo";

	private static final EELFLoggerDelegate log = EELFLoggerDelegate.getLogger(SiteContentServiceImpl.class);

	@Override
	public MLPSiteContent getTermsConditions() {
		log.debug(EELFLoggerDelegate.debugLogger, "getTermsConditions");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		return dataServiceRestClient.getSiteContent(KEY_TERMS_CONDITIONS);
	}

	@Override
	public void setTermsConditions(MLPSiteContent content) {
		log.debug(EELFLoggerDelegate.debugLogger, "setTermsConditions");
		content.setContentKey(KEY_TERMS_CONDITIONS);
		createOrUpdateContent(content);
	}

	@Override
	public MLPSiteContent getCobrandLogo() {
		log.debug(EELFLoggerDelegate.debugLogger, "getCobrandLogo");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		return dataServiceRestClient.getSiteContent(KEY_COBRAND_LOGO);
	}

	@Override
	public void setCobrandLogo(MLPSiteContent picture) {
		log.debug(EELFLoggerDelegate.debugLogger, "setCobrandLogo");
		picture.setContentKey(KEY_COBRAND_LOGO);
		createOrUpdateContent(picture);
	}

	@Override
	public void deleteCobrandLogo() {
		log.debug(EELFLoggerDelegate.debugLogger, "deleteCobrandLogo");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		dataServiceRestClient.deleteSiteContent(KEY_COBRAND_LOGO);
	}

	@Override
	public MLPSiteContent getOnboardingOverview() {
		log.debug(EELFLoggerDelegate.debugLogger, "getOnboardingOverview");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		return dataServiceRestClient.getSiteContent(KEY_ONBOARDING_OVERVIEW);
	}

	@Override
	public void setOnboardingOverview(MLPSiteContent content) {
		log.debug(EELFLoggerDelegate.debugLogger, "setOnboardingOverview");
		content.setContentKey(KEY_ONBOARDING_OVERVIEW);
		createOrUpdateContent(content);
	}

	@Override
	public MLPSiteContent getContactInfo() {
		log.debug(EELFLoggerDelegate.debugLogger, "getContactInfo");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		return dataServiceRestClient.getSiteContent(KEY_CONTACT_INFO);
	}

	@Override
	public void setContactInfo(MLPSiteContent content) {
		log.debug(EELFLoggerDelegate.debugLogger, "setContactInfo");
		content.setContentKey(KEY_CONTACT_INFO);
		createOrUpdateContent(content);
	}

	@Override
	public MLPSiteContent getCarouselPicture(String key) {
		log.debug(EELFLoggerDelegate.debugLogger, "getCarouselPicture ={}", key);
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		return dataServiceRestClient.getSiteContent(key);
	}

	@Override
	public void setCarouselPicture(MLPSiteContent picture) {
		log.debug(EELFLoggerDelegate.debugLogger, "setCarouselPicture ={}", picture.getContentKey());
		createOrUpdateContent(picture);
	}

	@Override
	public void deleteCarouselPicture(String key) {
		log.debug(EELFLoggerDelegate.debugLogger, "deleteCarouselPicture ={}", key);
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

}
