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

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class SignatureComponentsTest {

    private static final String SAMPLE_IDENTIFIER = "comment/1";
    private static final Instant SAMPLE_EXPIRATION_TIME = Instant.parse("2017-10-20T19:06:07.105Z");
    private static final byte[] SAMPLE_SIGNATURE_TOKEN = new byte[]{
            (byte) 240, (byte) 61, (byte) 193, (byte) 104, (byte) 80, (byte) 251, (byte) 37, (byte) 225, (byte) 82,
            (byte) 6, (byte) 173, (byte) 6, (byte) 212, (byte) 8, (byte) 119, (byte) 201, (byte) 3, (byte) 68,
            (byte) 54, (byte) 161, (byte) 4, (byte) 152, (byte) 72, (byte) 26, (byte) 113, (byte) 93, (byte) 91,
            (byte) 216, (byte) 106, (byte) 247, (byte) 2, (byte) 232
    };

    private static final String SAMPLE_SIGNATURE = SAMPLE_IDENTIFIER + '|' + SAMPLE_EXPIRATION_TIME + '|' +
            encodeSignatureToken();

    @Test
    public void fromStringParsesComponents() throws Exception {

        final SignatureComponents signatureComponents = SignatureComponents.fromString(SAMPLE_SIGNATURE);

        assertThat(signatureComponents.getIdentifier()).isEqualTo(SAMPLE_IDENTIFIER);
        assertThat(signatureComponents.getExpirationTime()).isEqualTo(SAMPLE_EXPIRATION_TIME);
        assertThat(signatureComponents.getSignatureToken()).isEqualTo(SAMPLE_SIGNATURE_TOKEN);
    }

    @Test
    public void toStringConcatenatesComponentsUsingPipe() throws Exception {

        final SignatureComponents signatureComponents = SignatureComponents.fromString(SAMPLE_SIGNATURE);

        final String signature = signatureComponents.toString();

        assertThat(signature).isEqualTo(SAMPLE_SIGNATURE);
    }

    @Test
    public void throwsIllegalArgumentExceptionWhenComponentCountInvalid() throws Exception {

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> SignatureComponents.fromString("a|b"));

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> SignatureComponents.fromString("a|b|c|d"));
    }

    @Test
    public void throwsDateExceptionWhenDateIsNotParsable() throws Exception {

        assertThatExceptionOfType(DateTimeParseException.class)
                .isThrownBy(() ->
                        SignatureComponents.fromString(SAMPLE_IDENTIFIER + "|1.2.2017|" + encodeSignatureToken()));
    }

    private static String encodeSignatureToken() {
        return Base64.getEncoder().encodeToString(SAMPLE_SIGNATURE_TOKEN);
    }
}
