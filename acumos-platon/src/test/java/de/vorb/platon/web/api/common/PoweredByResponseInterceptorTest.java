/*
 * Copyright 2016-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.vorb.platon.web.api.common;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class PoweredByResponseInterceptorTest {

    private PoweredByResponseInterceptor poweredByResponseInterceptor = new PoweredByResponseInterceptor();

    private HttpServletResponse response;

    @Before
    public void setUp() throws Exception {
        response = Mockito.spy(HttpServletResponse.class);
    }

    @Test
    public void postHandle() throws Exception {
        poweredByResponseInterceptor.postHandle(mock(HttpServletRequest.class), response, null, null);

        verify(response).addHeader(eq("X-Powered-By"), eq("Platon"));
    }
}
