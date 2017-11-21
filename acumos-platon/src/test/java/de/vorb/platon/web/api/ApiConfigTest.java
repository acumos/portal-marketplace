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

package de.vorb.platon.web.api;

import de.vorb.platon.web.api.common.PoweredByResponseInterceptor;

import org.junit.Test;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ApiConfigTest {

    private final ApiConfig apiConfig = new ApiConfig();

    @Test
    public void objectMapperWritesInstantsInIsoFormat() throws Exception {
        final Instant instant = Instant.now();
        final String expectedString = '"' + instant.toString() + '"';
        assertThat(apiConfig.objectMapper().writeValueAsString(instant)).isEqualTo(expectedString);
    }

    @Test
    public void addsPoweredByReponseInterceptor() throws Exception {
        final InterceptorRegistry interceptorRegistry = mock(InterceptorRegistry.class);
        apiConfig.addInterceptors(interceptorRegistry);
        verify(interceptorRegistry).addInterceptor(any(PoweredByResponseInterceptor.class));
    }
}
