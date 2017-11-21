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

package de.vorb.platon.security;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.Instant;

import static java.time.temporal.ChronoUnit.HOURS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SignatureCreatorTest {

    @InjectMocks
    private SignatureCreator signatureCreator;

    @Mock
    private SignatureTokenValidator signatureTokenValidator;

    @Test
    public void usesSignatureTokenToCreateSignatureComponents() throws Exception {

        final String commentUri = "/api/comments/1234";
        final Instant expirationTime = Instant.now().plus(3, HOURS);
        final byte[] signatureToken = new byte[0];

        when(signatureTokenValidator.getSignatureToken(eq(commentUri), eq(expirationTime))).thenReturn(signatureToken);

        assertThat(signatureCreator.createSignatureComponents(commentUri, expirationTime))
                .isEqualTo(SignatureComponents.of(commentUri, expirationTime, signatureToken));
    }

}
