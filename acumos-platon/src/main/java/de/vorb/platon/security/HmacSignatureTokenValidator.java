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

import org.apache.commons.codec.digest.HmacAlgorithms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Clock;
import java.time.Instant;
import java.util.Arrays;

import static de.vorb.platon.security.SignatureComponents.COMPONENT_SEPARATOR;
import static java.nio.charset.StandardCharsets.UTF_8;

@Service
public class HmacSignatureTokenValidator implements SignatureTokenValidator {

    static final HmacAlgorithms HMAC_ALGORITHM = HmacAlgorithms.HMAC_SHA_256;

    private final Clock clock;

    private final Mac mac;

    @Autowired
    public HmacSignatureTokenValidator(SecretKeyProvider keyProvider, Clock clock) {

        this.clock = clock;

        final SecretKey key = keyProvider.getSecretKey();

        try {
            mac = Mac.getInstance(HMAC_ALGORITHM.toString());
            mac.init(key);
        } catch (NoSuchAlgorithmException e) {
            throw new SecurityException(
                    String.format("Could not find an implementation of the %s algorithm", HMAC_ALGORITHM), e);
        } catch (InvalidKeyException e) {
            throw new SecurityException("The supplied key provider returned an invalid key", e);
        }
    }

    @Override
    public byte[] getSignatureToken(String identifier, Instant expirationTime) {
        final byte[] signatureSource =
                (identifier + COMPONENT_SEPARATOR + expirationTime).getBytes(UTF_8);

        return mac.doFinal(signatureSource);
    }

    @Override
    public boolean isSignatureValid(SignatureComponents signatureComponents) {
        if (currentTime().isAfter(signatureComponents.getExpirationTime())) {
            return false;
        }

        final byte[] referenceSignature =
                getSignatureToken(signatureComponents.getIdentifier(), signatureComponents.getExpirationTime());

        return Arrays.equals(signatureComponents.getSignatureToken(), referenceSignature);
    }

    private Instant currentTime() {
        return clock.instant();
    }

}
