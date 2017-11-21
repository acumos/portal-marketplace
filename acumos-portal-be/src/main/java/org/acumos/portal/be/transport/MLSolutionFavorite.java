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

package org.acumos.portal.be.transport;

public class MLSolutionFavorite {
	
	private String solutionID;
	private String userID;
	public String getSolutionID() {
		return solutionID;
	}
	public void setSolutionID(String solutionID) {
		this.solutionID = solutionID;
	}
	public String getUserID() {
		return userID;
	}
	public void setUserID(String userID) {
		this.userID = userID;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MLSolutionFavorite [solutionID=");
		builder.append(solutionID);
		builder.append(", userID=");
		builder.append(userID);
		builder.append("]");
		return builder.toString();
	}
	
	

}
