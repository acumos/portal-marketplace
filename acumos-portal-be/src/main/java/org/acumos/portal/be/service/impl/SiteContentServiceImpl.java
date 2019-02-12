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

	public static final String KEY_TERMS_CONDITION = "global.termsCondition";
	public static final String KEY_COBRAND_LOGO = "global.coBrandLogo";
	public static final String KEY_CONTACT_INFO = "global.footer.contactInfo";

	private static final EELFLoggerDelegate log = EELFLoggerDelegate.getLogger(SiteContentServiceImpl.class);

	@Override
	public MLPSiteContent getTermsCondition() {
		log.debug(EELFLoggerDelegate.debugLogger, "getTermsCondition");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		return dataServiceRestClient.getSiteContent(KEY_TERMS_CONDITION);
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
		createOrUpdateContentPicture(picture);
	}

	@Override
	public void deleteCobrandLogo() {
		log.debug(EELFLoggerDelegate.debugLogger, "deleteCobrandLogo");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		dataServiceRestClient.deleteSiteContent(KEY_COBRAND_LOGO);
	}

	@Override
	public MLPSiteContent getContactInfo() {
		log.debug(EELFLoggerDelegate.debugLogger, "getContactInfo");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		return dataServiceRestClient.getSiteContent(KEY_CONTACT_INFO);
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
		createOrUpdateContentPicture(picture);
	}

	@Override
	public void deleteCarouselPicture(String key) {
		log.debug(EELFLoggerDelegate.debugLogger, "deleteCarouselPicture ={}", key);
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		dataServiceRestClient.deleteSiteContent(key);
	}

	private void createOrUpdateContentPicture(MLPSiteContent picture) {
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
		MLPSiteContent content = dataServiceRestClient.getSiteContent(picture.getContentKey());
		if (content != null) {
			dataServiceRestClient.updateSiteContent(picture);
		} else {
			dataServiceRestClient.createSiteContent(picture);
		}
	}

}
