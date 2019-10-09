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

package org.acumos.portal.be.util;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class ExpiryDateUtils {

	
	@Autowired
	private Environment env;
	
	public Instant getExpiryDate() {
		
		String loginExpireDuration = env.getProperty(PortalConstants.LOGIN_EXPIRE_PROPERTY_KEY );
		if(!PortalUtils.isEmptyOrNullString(loginExpireDuration)) {
			loginExpireDuration = loginExpireDuration.trim();
			Pattern pattern = Pattern.compile("^(\\d+)(M|H|D|W|Y)$");
			Matcher m = pattern.matcher(loginExpireDuration);
			if (m.matches()) {
                int val = Integer.parseInt(m.group(1));
                String denomination = m.group(2);
                if (denomination.equals("H")) {
                	return Instant.now().plus(val, ChronoUnit.HOURS);
                } else if (denomination.equals("D")) {
                	return Instant.now().plus(val, ChronoUnit.DAYS);
                } else if (denomination.equals("M")) {
                	return Instant.now().plus((val* 30), ChronoUnit.DAYS);
                } else if (denomination.equals("W")) {
                	return Instant.now().plus((val * 7 ), ChronoUnit.DAYS);
                } else if (denomination.equals("Y")) {
                	return Instant.now().plus((val * 365 ), ChronoUnit.DAYS);
                } else {
                        throw new RuntimeException(
                                        "Developer error. Unhandled pattern '" + loginExpireDuration + "'"
                                                        + " for '" + loginExpireDuration
                                                        + "'. Valid patterns are 10H, 1D, 1W, 1M");
                }

			} else {
				throw new RuntimeException(
						"Invalid pattern found in "+PortalConstants.LOGIN_EXPIRE_PROPERTY_KEY +" property '" + loginExpireDuration + "'"
								+ " for '" + loginExpireDuration
								+ "'. Valid patterns are '10H' for 10 hours, '1D' for 1 day, '1W' for 1 week, '1W' for 1 month, '1Y' for 1 year");
			}
		
		
		}
		return null;
	}
}
