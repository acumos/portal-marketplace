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

import de.vorb.platon.persistence.PropertyRepository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DatabaseSecretKeyProviderTest {

    private static final String SECRET_KEY = "secret_key";

    @InjectMocks
    private DatabaseSecretKeyProvider secretKeyProvider;

    @Mock
    private PropertyRepository propertyRepository;

    @Test
    public void noSecretKeyAvailable() throws Exception {
        final SecretKey secretKey = assertThatSecretKeyIsGenerated();
        assertThatSecretKeyIsNotRecreated(secretKey);
    }

    private void assertThatSecretKeyIsNotRecreated(SecretKey secretKey) {
        reset(propertyRepository);
        assertThat(secretKeyProvider.getSecretKey()).isSameAs(secretKey);
        verify(propertyRepository, never()).findValueByKey(any());
    }

    private SecretKey assertThatSecretKeyIsGenerated() {
        when(propertyRepository.findValueByKey(eq(SECRET_KEY))).thenReturn(null);
        final SecretKey secretKey = secretKeyProvider.getSecretKey();
        verify(propertyRepository).insertValue(eq(SECRET_KEY), any());

        return secretKey;
    }

    @Test
    public void secretKeyExists() throws Exception {

        final SecretKey storedSecretKey = KeyGenerator.getInstance(
                HmacSignatureTokenValidator.HMAC_ALGORITHM.toString()).generateKey();

        when(propertyRepository.findValueByKey(eq(SECRET_KEY)))
                .thenReturn(Base64.getEncoder().encodeToString(storedSecretKey.getEncoded()));

        final SecretKey secretKeyFromRepo = secretKeyProvider.getSecretKey();

        assertThat(secretKeyFromRepo).isEqualTo(storedSecretKey);
    }
}
