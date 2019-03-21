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

package org.acumos.be.test.logging;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.hamcrest.core.IsSame.sameInstance;
import static org.hamcrest.core.StringEndsWith.endsWith;
import static org.hamcrest.number.OrderingComparison.lessThan;

import java.util.Map;
import java.util.UUID;

import javax.xml.bind.DatatypeConverter;

import org.acumos.portal.be.logging.ONAPLogAdapter;
import org.acumos.portal.be.logging.ONAPLogConstants;
import org.junit.After;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
public class LogAdapterTest {

	/**
     * Ensure that MDCs are cleared after each testcase.
     */
	@After
    public void resetMDCs() {
        MDC.clear();
    }
	
    /**
     * Test ENTERING.
     */
    @Test
    public void testEntering() {

        final Logger logger = LoggerFactory.getLogger(this.getClass());
        final ONAPLogAdapter adapter = new ONAPLogAdapter(logger);
        final MockHttpServletRequest http = new MockHttpServletRequest();
        http.setRequestURI("uri123");
        http.setServerName("local123");
        http.setRemoteAddr("remote123");
       // http.addHeader(ONAPLogConstants.MDCs.PARTNER_NAME, "partner123");

        try {
            adapter.entering(http);
            final Map<String, String> mdcs = MDC.getCopyOfContextMap();
         //   assertThat(mdcs.get(ONAPLogConstants.MDCs.PARTNER_NAME), is("partner123"));
            assertThat(mdcs.get("ServerFQDN"), is("local123"));
            assertThat(mdcs.get("ClientIPAddress"), is("remote123"));
            
            // Timestamp format and value:
            final String invokeTimestampString = mdcs.get("InvokeTimestamp");
           // assertThat(invokeTimestampString, notNullValue());
           // assertThat(invokeTimestampString, endsWith("Z"));
           // final long invokeTimestamp = DatatypeConverter.parseDateTime(invokeTimestampString).getTimeInMillis();
           // assertThat(Math.abs(System.currentTimeMillis() - invokeTimestamp), lessThan(5000L));
        }
        finally {
            MDC.clear();
        }
    }
    
    /**
     * Test ENTERING with an EMPTY_STRING serviceName.
     */
    @Test
    public void testEnteringWithEMPTY_STRING_serviceName() {

        final Logger logger = LoggerFactory.getLogger(this.getClass());
        final ONAPLogAdapter adapter = new ONAPLogAdapter(logger);
        final MockHttpServletRequest http = new MockHttpServletRequest();
        http.setRequestURI("uri123");
        http.setServerName("local123");
        http.setRemoteAddr("remote123");
       // http.addHeader(ONAPLogConstants.MDCs.PARTNER_NAME, "partner123");
        http.setAttribute(ONAPLogConstants.MDCs.USER, "test");

        try {
            // an empty string should kick in setting the actual service name (treated same as null)
            adapter.entering(http);
            final Map<String, String> mdcs = MDC.getCopyOfContextMap();
           // assertThat(mdcs.get("PartnerName"), is("partner123"));
            assertThat(mdcs.get("ServerFQDN"), is("local123"));
            assertThat(mdcs.get("ClientIPAddress"), is("remote123"));
            assertThat(mdcs.get(ONAPLogConstants.MDCs.USER), is("test"));

            // Timestamp format and value:

            final String invokeTimestampString = mdcs.get("InvokeTimestamp");
           // assertThat(invokeTimestampString, notNullValue());
           // assertThat(invokeTimestampString, endsWith("Z"));
           // final long invokeTimestamp = DatatypeConverter.parseDateTime(invokeTimestampString).getTimeInMillis();
           // assertThat(Math.abs(System.currentTimeMillis() - invokeTimestamp), lessThan(5000L));
        }
        finally {
            MDC.clear();
        }
    }

  
    @Test
    public void testSetResponseDescriptor() {
        final ONAPLogAdapter.ResponseDescriptor override = new ONAPLogAdapter.ResponseDescriptor();
        final Logger logger = LoggerFactory.getLogger(this.getClass());
        final ONAPLogAdapter adapter = new ONAPLogAdapter(logger);
        final ONAPLogAdapter.ResponseDescriptor before = adapter.getResponseDescriptor();
        adapter.setResponseDescriptor(override);
        final ONAPLogAdapter.ResponseDescriptor after = adapter.getResponseDescriptor();
        assertThat(after, not(sameInstance(before)));
        assertThat(after, is(override));
    }

    @Test
    public void testUnwrap() {
        final Logger logger = LoggerFactory.getLogger(this.getClass());
        final ONAPLogAdapter adapter = new ONAPLogAdapter(logger);
        assertThat(adapter.unwrap(), is(logger));
    }

    /**
     * Test EXITING.
     */
    @Test
    public void testExiting() {

        final Logger logger = LoggerFactory.getLogger(this.getClass());
        final ONAPLogAdapter adapter = new ONAPLogAdapter(logger);

        try {
            MDC.put("somekey", "somevalue");
            assertThat(MDC.get("somekey"), is("somevalue"));
            adapter.exiting();
            assertThat(MDC.get("somekey"), nullValue());
        }
        finally {
            MDC.clear();
        }
    }
  

    @Test
    public void testHttpServletRequestAdapter() {

        final UUID uuid = UUID.randomUUID();
        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("uuid", uuid.toString());
        request.setRequestURI("/ctx0");
        request.setServerName("srv0");

        final ONAPLogAdapter.HttpServletRequestAdapter adapter
                = new ONAPLogAdapter.HttpServletRequestAdapter(request);
        assertThat(adapter.getHeader("uuid"), is(uuid.toString()));
        assertThat(adapter.getRequestURI(), is("/ctx0"));
        assertThat(adapter.getServerAddress(), is("srv0"));
    }
   

    @Test
    public void testResponseDescriptor() {
        
        final ONAPLogAdapter.ResponseDescriptor adapter
                = new ONAPLogAdapter.ResponseDescriptor();
        adapter.setResponseCode("code0");
        adapter.setResponseDescription("desc0");
        adapter.setResponseStatus(ONAPLogConstants.ResponseStatus.COMPLETED);

        assertThat(MDC.get(ONAPLogConstants.MDCs.RESPONSE_CODE), nullValue());
        assertThat(MDC.get(ONAPLogConstants.MDCs.RESPONSE_DESCRIPTION), nullValue());
        assertThat(MDC.get(ONAPLogConstants.MDCs.RESPONSE_SEVERITY), nullValue());
        assertThat(MDC.get(ONAPLogConstants.MDCs.RESPONSE_STATUS_CODE), nullValue());

        adapter.setMDCs();

        assertThat(MDC.get(ONAPLogConstants.MDCs.RESPONSE_CODE), is("code0"));
        assertThat(MDC.get(ONAPLogConstants.MDCs.RESPONSE_DESCRIPTION), is("desc0"));
        assertThat(MDC.get(ONAPLogConstants.MDCs.RESPONSE_STATUS_CODE), is("COMPLETED"));
    }
}
