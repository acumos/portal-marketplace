package org.acumos.portal.be.service;

import org.acumos.cds.domain.MLPSiteContent;

public interface SiteContentService {
	
	public MLPSiteContent getTermsCondition();
	
	public MLPSiteContent getCobrandLogo();
	
	public void setCobrandLogo(MLPSiteContent picture);
	
	public MLPSiteContent getContactInfo();
	
	public MLPSiteContent getCarouselPicture(String key);
	
	public void setCarouselPicture(MLPSiteContent picture);
	
	public void deleteCarouselPicture(String key);
}
