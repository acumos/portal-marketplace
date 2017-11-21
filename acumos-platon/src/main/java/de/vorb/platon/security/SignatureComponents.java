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

import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.Base64;

import static com.google.common.base.Preconditions.checkArgument;

@Data
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class SignatureComponents {

    public static final char COMPONENT_SEPARATOR = '|';

    private final String identifier;
    private final Instant expirationTime;
    private final byte[] signatureToken;

    public static SignatureComponents of(String identifier, Instant expirationTime, byte[] signatureToken) {
        return new SignatureComponents(identifier, expirationTime, signatureToken);
    }

    public static SignatureComponents fromString(String signatureString) {

        final String[] signatureComponents = signatureString.split("\\" + COMPONENT_SEPARATOR);

        checkArgument(signatureComponents.length == 3);

        final String identifier = signatureComponents[0];
        final Instant expirationTime = Instant.parse(signatureComponents[1]);
        final byte[] signatureToken = Base64.getDecoder().decode(signatureComponents[2]);

        return of(identifier, expirationTime, signatureToken);
    }

    @Override
    public String toString() {
        return identifier +
                COMPONENT_SEPARATOR +
                expirationTime +
                COMPONENT_SEPARATOR +
                Base64.getEncoder().encodeToString(signatureToken);
    }
}
