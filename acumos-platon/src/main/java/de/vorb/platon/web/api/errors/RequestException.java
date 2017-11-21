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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Preconditions;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class RequestException extends RuntimeException {

    @Getter
    private final int status;

    RequestException(int status, String message, Throwable cause) {
        super(message, cause);

        Preconditions.checkArgument(isHttpErrorStatus(status),
                "Status %s is not in the range for HTTP errors (status >= 400)", status);

        this.status = status;
    }

    private static boolean isHttpErrorStatus(int status) {
        return status >= 400;
    }

    /**
     * @throws IllegalArgumentException if {@link HttpStatus} has no constant for the specified numeric withStatus code
     */
    @JsonIgnore
    public HttpStatus getHttpStatus() {
        return HttpStatus.valueOf(status);
    }

    public RequestExceptionJson toJson() {
        return new RequestExceptionJson(status, getMessage(), getCause() == null ? null : getCause().getMessage());
    }

    public ResponseEntity<RequestExceptionJson> toResponseEntity() {
        return ResponseEntity.status(status).body(toJson());
    }

    public static Builder withStatus(int status) {
        return new Builder(status);
    }

    public static Builder withStatus(HttpStatus status) {
        return withStatus(status.value());
    }

    public static Builder badRequest() {
        return withStatus(HttpStatus.BAD_REQUEST);
    }

    public static Builder unauthorized() {
        return withStatus(HttpStatus.UNAUTHORIZED);
    }

    public static Builder forbidden() {
        return withStatus(HttpStatus.FORBIDDEN);
    }

    public static Builder notFound() {
        return withStatus(HttpStatus.NOT_FOUND);
    }

    public static Builder internalServerError() {
        return withStatus(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public static class Builder {

        private final int status;
        private String message;
        private Throwable cause;

        public Builder(int status) {
            this.status = status;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder cause(Throwable cause) {
            this.cause = cause;
            return this;
        }

        public RequestException build() {
            return new RequestException(status, message, cause);
        }
    }
}
