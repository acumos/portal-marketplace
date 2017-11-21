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

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeParseException;

import static com.google.common.base.Preconditions.checkArgument;

@Component
@RequiredArgsConstructor
public class RequestValidator {

    private final SignatureTokenValidator signatureTokenValidator;

    public void verifyValidRequest(String signature, String identifier) {

        try {
            final SignatureComponents signatureComponents = SignatureComponents.fromString(signature);

            checkArgument(signatureComponents.getIdentifier().equals(identifier), "Identifiers do not match");
            checkArgument(signatureTokenValidator.isSignatureValid(signatureComponents));

        } catch (IllegalArgumentException | DateTimeParseException e) {
            throw RequestException.badRequest()
                    .message("Authentication signature is invalid or has expired")
                    .cause(e)
                    .build();
        }
    }

}
