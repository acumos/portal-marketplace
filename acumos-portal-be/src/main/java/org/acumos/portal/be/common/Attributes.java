/*-
 * ===============LICENSE_START=======================================================
 * Acumos
 * ===================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
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

package org.acumos.portal.be.common;

import java.io.Serializable;

/**
* This class represents a common format set for the request body sent from the client.
* Getters and setters encapsulate the fields of a class by making them accessible 
* only through its public methods and keep the values themselves private.
 * @param <T> Type inside request
*/

public class Attributes  implements Serializable {


	public Attributes(String title, String email, String lastname, String[] affiliation, String firstName) {
		super();
		this.mail = email;
		this.profile_name_last = lastname;
		this.affiliation = affiliation;
		this.profile_name_first = firstName;
	}

	public Attributes() {
		super();
	}

	private static final long serialVersionUID = 7576436006913504603L;




	private String mail;


	private String profile_name_last;


	private String[] affiliation;


	private String profile_name_first;

	/**
	 * @return the mail
	 */
	public String getMail() {
		return mail;
	}

	/**
	 * @param mail the mail to set
	 */
	public void setMail(String mail) {
		this.mail = mail;
	}

	/**
	 * @return the profile_name_last
	 */
	public String getProfile_name_last() {
		return profile_name_last;
	}

	/**
	 * @param profile_name_last the profile_name_last to set
	 */
	public void setProfile_name_last(String profile_name_last) {
		this.profile_name_last = profile_name_last;
	}

	/**
	 * @return the affiliation
	 */
	public String[] getAffiliation() {
		return affiliation;
	}

	/**
	 * @param affiliation the affiliation to set
	 */
	public void setAffiliation(String[] affiliation) {
		this.affiliation = affiliation;
	}

	/**
	 * @return the profile_name_first
	 */
	public String getProfile_name_first() {
		return profile_name_first;
	}

	/**
	 * @param profile_name_first the profile_name_first to set
	 */
	public void setProfile_name_first(String profile_name_first) {
		this.profile_name_first = profile_name_first;
	}

}
