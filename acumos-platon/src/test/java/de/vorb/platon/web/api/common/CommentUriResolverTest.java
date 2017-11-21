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

import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;

import static java.util.Collections.enumeration;
import static java.util.Collections.singleton;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CommentUriResolverTest {

    private final CommentUriResolver commentUriResolver = new CommentUriResolver();
    private final HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);

    @Test
    public void resolvesCommentUriThroughServletRequest() throws Exception {

        mockPostOrUpdateCommentRequest();

        final URI commentUri = commentUriResolver.createRelativeCommentUriForId(1234);

        assertThat(commentUri).isEqualTo(new URI("/api/comments/1234"));
    }

    private void mockPostOrUpdateCommentRequest() {

        final UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .scheme("https")
                .host("example.org")
                .path("api/comments")
                .queryParam("threadUrl", "/article-1.html")
                .queryParam("threadTitle", "Article")
                .build();

        final String requestUrl = uriComponents.toUriString();
        final String requestQueryString = uriComponents.getQuery();

        when(httpServletRequest.getRequestURL())
                .thenReturn(new StringBuffer(requestUrl));
        when(httpServletRequest.getQueryString())
                .thenReturn(requestQueryString);
        when(httpServletRequest.getHeaderNames())
                .thenReturn(enumeration(singleton(HttpHeaders.CONTENT_TYPE)));
        when(httpServletRequest.getHeaders(eq(HttpHeaders.CONTENT_TYPE)))
                .thenReturn(enumeration(singleton("application/json")));
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(httpServletRequest));
    }
}
