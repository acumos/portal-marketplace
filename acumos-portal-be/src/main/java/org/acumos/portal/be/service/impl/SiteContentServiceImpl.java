package org.acumos.portal.be.service.impl;

import org.acumos.cds.client.ICommonDataServiceRestClient;
import org.acumos.cds.domain.MLPSiteContent;
import org.acumos.portal.be.service.SiteContentService;
import org.acumos.portal.be.util.EELFLoggerDelegate;
import org.springframework.stereotype.Service;

@Service
public class SiteContentServiceImpl extends AbstractServiceImpl implements SiteContentService {
	
	private static final String keyTermsCondition = "global.termsCondition";
	private static final String keyCobrandLogo = "global.coBrandLogo";
	private static final String keyContactInfo = "global.footer.contactInfo";
	
	private static final EELFLoggerDelegate log = EELFLoggerDelegate.getLogger(SiteContentServiceImpl.class);

	@Override
	public MLPSiteContent getTermsCondition() {
		log.debug(EELFLoggerDelegate.debugLogger, "getTermsCondition");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
        return dataServiceRestClient.getSiteContent(keyTermsCondition);
	}

	@Override
	public MLPSiteContent getCobrandLogo() {
		log.debug(EELFLoggerDelegate.debugLogger, "getCobrandLogo");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
        return dataServiceRestClient.getSiteContent(keyCobrandLogo);
	}

	@Override
	public void setCobrandLogo(MLPSiteContent picture) {
		log.debug(EELFLoggerDelegate.debugLogger, "setCobrandLogo");
		picture.setContentKey(keyCobrandLogo);
		createOrUpdateContentPicture(picture);
	}

	@Override
	public MLPSiteContent getContactInfo() {
		log.debug(EELFLoggerDelegate.debugLogger, "getContactInfo");
		ICommonDataServiceRestClient dataServiceRestClient = getClient();
        return dataServiceRestClient.getSiteContent(keyContactInfo);
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
