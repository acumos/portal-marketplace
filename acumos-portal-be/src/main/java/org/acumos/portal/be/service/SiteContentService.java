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

package org.acumos.portal.be.service;

import org.acumos.cds.domain.MLPSiteContent;

public interface SiteContentService {

	/**
	 * Method to fetch terms and conditions
	 * 
	 * @return html embedded in json string
	 */
	public MLPSiteContent getTermsConditions();

	/**
	 * Method to create or update terms and conditions
	 * 
	 * @param content
	 *            html embedded in json string
	 */
	public void setTermsConditions(MLPSiteContent content);

	/**
	 * Method to fetch the co-brand logo
	 * 
	 * @return picture data w/ mime type information
	 */
	public MLPSiteContent getCobrandLogo();

	/**
	 * Method to create or update the co-brand logo
	 * 
	 * @param picture
	 *            picture data w/ mime type information
	 */
	public void setCobrandLogo(MLPSiteContent picture);

	/**
	 * Method to delete the co-brand logo
	 */
	public void deleteCobrandLogo();

	/**
	 * Method to fetch the footer contact information
	 * 
	 * @return html embedded in json string
	 */
	public MLPSiteContent getContactInfo();

	/**
	 * Method to create or update the footer contact information
	 * 
	 * @param content
	 *            html embedded in json string
	 */
	public void setContactInfo(MLPSiteContent content);

	/**
	 * Method to fetch the carousel picture stored under the given key
	 * 
	 * @param key
	 *            content key
	 * @return picture data w/ mime type information
	 */
	public MLPSiteContent getCarouselPicture(String key);

	/**
	 * Method to create or update a carousel picture
	 * 
	 * @param picture
	 *            picture data w/ mime type information
	 */
	public void setCarouselPicture(MLPSiteContent picture);

	/**
	 * Method to delete a carousel picture stored under the given key
	 * 
	 * @param key
	 *            content key
	 */
	public void deleteCarouselPicture(String key);
}
