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

import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class HtmlInputSanitizer implements InputSanitizer {

    private final PolicyFactory htmlContentPolicy;

    private String allowedElements= "h1, h2, h3, h4, h5, h6, br, p, hr, div, span, a, img, em, strong, ol, ul, li, blockquote, code, pre";
    
    public HtmlInputSanitizer(
            //@Value("${platon.input.html_elements}") String allowedHtmlElements) {
    		@Value("h1, h2, h3, h4, h5, h6, br, p, hr, div, span, a, img, em, strong, ol, ul, li, blockquote, code, pre") String allowedHtmlElements) {

    	if(allowedHtmlElements == null || allowedHtmlElements.trim().length() == 0) {
    		allowedHtmlElements= allowedElements;
    	}
        final HtmlPolicyBuilder htmlPolicyBuilder = new HtmlPolicyBuilder()
                .allowUrlProtocols("http", "https", "mailto")
                .allowAttributes("href").onElements("a")
                .allowAttributes("src", "width", "height", "alt").onElements("img")
                .allowAttributes("class").onElements("div", "span");

        htmlPolicyBuilder.allowElements(allowedHtmlElements.trim().split("\\s*,\\s*"));

        this.htmlContentPolicy = htmlPolicyBuilder.toFactory();
    }

    @Override
    public String sanitize(String input) {
        return htmlContentPolicy.sanitize(input);
    }
}
