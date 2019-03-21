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
import static org.hamcrest.core.IsInstanceOf.instanceOf;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.acumos.portal.be.logging.ONAPLogConstants;
import org.junit.Assert;
import org.junit.Test;

/** 
 * Tests for {@link ONAPLogConstants}
 *
 */
public class LogConstantTest {

	/**
	 * Tests for LogAdapter
	 */
	@Test
    public void testConstructors() throws Exception {
        assertInaccessibleConstructor(ONAPLogConstants.class);
        assertInaccessibleConstructor(ONAPLogConstants.MDCs.class);
        assertInaccessibleConstructor(ONAPLogConstants.Headers.class);
    }

    @Test
    public void testConstructorUnsupported() throws Exception {
        try {
            Constructor<?> c = ONAPLogConstants.class.getDeclaredConstructors()[0];
            c.setAccessible(true);
            c.newInstance();
            Assert.fail("Should fail for hidden constructor.");
        }
        catch (final InvocationTargetException e) {
            assertThat(e.getCause(), instanceOf(UnsupportedOperationException.class));
        }
    }

    @Test
    public void testHeaders() {
        assertThat(ONAPLogConstants.Headers.REQUEST_ID, is("X-ACUMOS-Request-Id"));
    }
   

    @Test
    public void testResponseStatus() {
        assertThat(ONAPLogConstants.ResponseStatus.COMPLETED.toString(), is("COMPLETED"));
        assertThat(ONAPLogConstants.ResponseStatus.ERROR.toString(), is("ERROR"));
    }

    @Test
    public void testMDCs() {

        assertThat(ONAPLogConstants.MDCs.CLIENT_IP_ADDRESS.toString(), is("ClientIPAddress"));
        assertThat(ONAPLogConstants.MDCs.SERVER_FQDN.toString(), is("ServerFQDN"));

        assertThat(ONAPLogConstants.MDCs.ENTRY_TIMESTAMP.toString(), is("EntryTimestamp"));
        assertThat(ONAPLogConstants.MDCs.INVOKE_TIMESTAMP.toString(), is("InvokeTimestamp"));

        assertThat(ONAPLogConstants.MDCs.REQUEST_ID.toString(), is("X-ACUMOS-Request-Id"));
        assertThat(ONAPLogConstants.MDCs.TARGET_SERVICE_NAME.toString(), is("TargetServiceName"));

    }

    static void assertInaccessibleConstructor(final Class<?> c) throws Exception {
        try {
            c.getDeclaredConstructors()[0].newInstance();
            Assert.fail("Should fail for hidden constructor.");
        }
        catch (final IllegalAccessException e) {

        }

        try {
            final Constructor<?> constructor = c.getDeclaredConstructors()[0];
            constructor.setAccessible(true);
            constructor.newInstance();
            Assert.fail("Should fail even when invoked.");
        }
        catch (final InvocationTargetException e) {
            assertThat(e.getCause(), instanceOf(UnsupportedOperationException.class));
        }
    }
}