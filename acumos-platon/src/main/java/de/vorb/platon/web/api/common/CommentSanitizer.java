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

import de.vorb.platon.jooq.tables.records.CommentRecord;

import com.google.common.collect.ImmutableSet;
import lombok.RequiredArgsConstructor;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class CommentSanitizer {

    private static final Set<String> ACCEPTED_URL_SCHEMES = ImmutableSet.of("http", "https", "mailto");

    private static final PolicyFactory NO_HTML_POLICY = new HtmlPolicyBuilder().toFactory();

    private final InputSanitizer inputSanitizer;

    public void sanitizeComment(CommentRecord comment) {

        if (comment.getAuthor() != null) {
            comment.setAuthor(NO_HTML_POLICY.sanitize(comment.getAuthor()).trim());
        }

        if (comment.getName() != null) {
            comment.setName(NO_HTML_POLICY.sanitize(comment.getName()).trim());
        }

        if (comment.getUrl() != null) {
            String url = comment.getUrl();
            if (!url.contains("://")) {
                url = "https://" + url;
            }

            comment.setUrl(
                    validateUrl(url)
                            .map(URI::toASCIIString)
                            .orElse(null)
            );
        }

        final String requestText = comment.getText();
        final String sanitizedText = inputSanitizer.sanitize(requestText);
        comment.setText(sanitizedText);
    }

    private Optional<URI> validateUrl(String urlAsString) {
        try {
            final String decodedUrl = URLDecoder.decode(urlAsString, StandardCharsets.UTF_8.name());
            final URL url = new URL(decodedUrl);

            if (!ACCEPTED_URL_SCHEMES.contains(url.getProtocol())) {
                return Optional.empty();
            }

            final URI uri = new URI(
                    url.getProtocol(),
                    url.getUserInfo(),
                    url.getHost(),
                    url.getPort(),
                    url.getPath(),
                    url.getQuery(),
                    url.getRef()
            );

            return Optional.of(uri);
        } catch (UnsupportedEncodingException | MalformedURLException | URISyntaxException e) {
            return Optional.empty();
        }
    }

}
