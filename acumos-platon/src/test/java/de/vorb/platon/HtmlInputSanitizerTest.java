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

package de.vorb.platon;

import de.vorb.platon.web.api.common.HtmlInputSanitizer;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class HtmlInputSanitizerTest {

    private HtmlInputSanitizer htmlInputSanitizer;

    @Before
    public void setUp() throws Exception {
        htmlInputSanitizer = new HtmlInputSanitizer("p,br");
    }

    @Test
    public void htmlWithScriptTag() throws Exception {

        final String sanitizedHtml = htmlInputSanitizer.sanitize("<p>Text</p><script>alert('boo!');</script>");

        assertThat(sanitizedHtml).doesNotContain("<script");
        assertThat(sanitizedHtml).doesNotContain("alert(");
        assertThat(sanitizedHtml).startsWith("<p>");
        assertThat(sanitizedHtml).endsWith("</p>");
    }

    @Test
    public void worksWithMultipleTags() throws Exception {

        final String sanitizedHtml = htmlInputSanitizer.sanitize("<p>First line<br />Second line</p>");

        assertThat(sanitizedHtml).contains("<p>");
        assertThat(sanitizedHtml).contains("<br />");
        assertThat(sanitizedHtml).contains("</p>");
    }
}
