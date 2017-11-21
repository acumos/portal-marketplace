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

package de.vorb.platon.web.api.errors;

import org.assertj.core.api.ThrowableAssert;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

public class RequestExceptionTest {

    @Test
    public void acceptOnlyStatusCodesGreaterThan400() throws Exception {

        assertThatIllegalArgumentException().isThrownBy(creatingResultException(100));
        assertThatIllegalArgumentException().isThrownBy(creatingResultException(200));
        assertThatIllegalArgumentException().isThrownBy(creatingResultException(301));
        assertThatIllegalArgumentException().isThrownBy(creatingResultException(308));

        assertThatCode(creatingResultException(400)).doesNotThrowAnyException();
        assertThatCode(creatingResultException(500)).doesNotThrowAnyException();
        assertThatCode(creatingResultException(511)).doesNotThrowAnyException();
        assertThatCode(creatingResultException(999)).doesNotThrowAnyException();
    }

    @Test
    public void returnsOriginalStatus() throws Exception {
        assertThat(RequestException.withStatus(400).build().getStatus()).isEqualTo(400);
    }

    @Test
    public void convertsToJson() throws Exception {

        final String notFoundMessage = "Not Found";
        final RequestException notFound =
                RequestException.notFound()
                        .message(notFoundMessage)
                        .build();
        assertThat(notFound.toJson().getStatus()).isEqualTo(notFound.getStatus());
        assertThat(notFound.toJson().getMessage()).isEqualTo(notFoundMessage);
        assertThat(notFound.toJson().getCause()).isNull();

        final IllegalStateException cause = new IllegalStateException("Unexpected state");
        final RequestException internalServerError =
                RequestException.badRequest()
                        .message("Internal Server Error")
                        .cause(cause)
                        .build();
        assertThat(internalServerError.toJson().getStatus()).isEqualTo(internalServerError.getStatus());
        assertThat(internalServerError.toJson().getMessage()).isEqualTo(internalServerError.getMessage());
        assertThat(internalServerError.toJson().getCause()).isEqualTo(cause.getMessage());
    }

    @Test
    public void toResponseEntity() throws Exception {

        final RequestException requestException = RequestException.badRequest().build();
        final ResponseEntity<RequestExceptionJson> response = requestException.toResponseEntity();

        assertThat(response.getStatusCodeValue()).isEqualTo(requestException.getStatus());
        assertThat(response.getBody()).isEqualTo(requestException.toJson());
    }

    @Test
    public void badRequest() throws Exception {
        assertThat(RequestException.badRequest().build().getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void unauthorized() throws Exception {
        assertThat(RequestException.unauthorized().build().getHttpStatus()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void forbidden() throws Exception {
        assertThat(RequestException.forbidden().build().getHttpStatus()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void notFound() throws Exception {
        assertThat(RequestException.notFound().build().getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void internalServerError() throws Exception {
        assertThat(RequestException.internalServerError().build().getHttpStatus())
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private static ThrowableAssert.ThrowingCallable creatingResultException(int status) {
        return () -> RequestException.withStatus(status).build();
    }
}
