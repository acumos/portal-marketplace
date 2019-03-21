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
package org.acumos.portal.be.util;

import java.io.UnsupportedEncodingException;
import java.lang.invoke.MethodHandles;
import java.net.URLDecoder;

import org.acumos.portal.be.transport.MLComment;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Hours;
import org.joda.time.Interval;
import org.joda.time.Minutes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DateUtils {
    private static final String FEW_SECONDS_AGO = "Just Now";
    private static final String MINUTES_AGO = "Minutes Ago";
    private static final String TIMESTAMP_FORMAT = "MM/dd/yyyy hh:mm a";
	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());	
	
	
	public String formatCommentTime(DateTime commentTime, String clientTimeZone) {
		try {
			clientTimeZone=URLDecoder.decode(clientTimeZone, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			DateTimeZone defaultZone = DateTimeZone.getDefault();
			log.debug("[DateUtils].formatCommentTime() exception, setting default timeZone to "+defaultZone.getID());
			clientTimeZone=defaultZone.getID(); 
		}
		DateTimeZone clientZone = DateTimeZone.forID(clientTimeZone);
		String stringDate = null;
		DateTime currDate = DateTime.now();
		
		DateTime clientTime = commentTime.withZone(clientZone);
		if (currDate.isAfter(commentTime)) {
			int minutes = Minutes.minutesBetween(commentTime, currDate).getMinutes();
			int hours = Hours.hoursBetween(commentTime, currDate).getHours();

			if (minutes < 1) {
				stringDate = FEW_SECONDS_AGO;
			} else if (minutes <= 59) {
				stringDate = minutes + " " + MINUTES_AGO;
			} else if (hours >= 1 && hours < 48) {
				Interval today = new Interval(currDate.withTimeAtStartOfDay(),
						currDate.plusDays(1).withTimeAtStartOfDay());
				boolean happensToday = today.contains(clientTime);
				if (happensToday) {
					stringDate = "AT " + clientTime.toString("hh:mm a");
				}else {
					Interval yesterday = new Interval(currDate.minusDays(1).withTimeAtStartOfDay(),
							currDate.withTimeAtStartOfDay());
					if(yesterday.contains(clientTime)){
						stringDate = "Yesterday " + clientTime.toString("hh:mm a");
					}else{
						stringDate = clientTime.toString(TIMESTAMP_FORMAT);
					}
				}

			} else {
				stringDate = clientTime.toString(TIMESTAMP_FORMAT);
			}
		}
		return stringDate;
	}
}
