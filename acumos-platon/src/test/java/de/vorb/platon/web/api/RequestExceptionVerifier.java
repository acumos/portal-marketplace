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

import de.vorb.platon.web.api.errors.RequestException;

import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;

public class RequestExceptionVerifier {

    public static void assertRequestExceptionWithStatus(HttpStatus expectedStatus, Runnable task) {
        try {
            task.run();
            failBecauseExceptionWasNotThrown(RequestException.class);
        } catch (RequestException e) {
            assertThat(e.getHttpStatus()).isEqualTo(expectedStatus);
        }
    }
}
