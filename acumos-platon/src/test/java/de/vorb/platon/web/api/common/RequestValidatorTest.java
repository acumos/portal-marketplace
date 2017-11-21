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

import de.vorb.platon.security.SignatureComponents;
import de.vorb.platon.security.SignatureTokenValidator;
import de.vorb.platon.web.api.errors.RequestException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import java.time.Instant;

import static java.time.temporal.ChronoUnit.DAYS;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RequestValidatorTest {

    @InjectMocks
    private RequestValidator requestValidator;

    @Mock
    private SignatureTokenValidator signatureTokenValidator;

    @Test
    public void doesNotThrowIfRequestWasValid() throws Exception {

        final String identifier = "/api/comments/1234";
        final Instant expirationTime = Instant.now().plus(1, DAYS);
        final SignatureComponents signatureComponents = SignatureComponents.of(identifier, expirationTime, new byte[1]);

        when(signatureTokenValidator.isSignatureValid(eq(signatureComponents))).thenReturn(true);

        assertThatCode(() -> requestValidator.verifyValidRequest(signatureComponents.toString(), identifier))
                .doesNotThrowAnyException();

        verify(signatureTokenValidator).isSignatureValid(eq(signatureComponents));
    }

    @Test
    public void throwsBadRequestIfRequestWasInvalid() throws Exception {

        assertThatExceptionOfType(RequestException.class)
                .isThrownBy(() -> requestValidator.verifyValidRequest("invalid signature", "/api/comments/1234"))
                .matches(requestException -> requestException.getHttpStatus() == HttpStatus.BAD_REQUEST)
                .withMessageContaining("signature")
                .withMessageContaining("invalid")
                .withMessageContaining("expired")
                .withCauseExactlyInstanceOf(IllegalArgumentException.class);

    }

}
