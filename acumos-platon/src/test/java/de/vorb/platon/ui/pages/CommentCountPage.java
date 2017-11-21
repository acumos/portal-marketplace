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

package de.vorb.platon.ui.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.openqa.selenium.support.ui.ExpectedConditions.textMatches;

public class CommentCountPage {

    private static final String HREF_SELECTOR = "a[href$=\"#platon-comment-thread\"]";
    private static final String ATTR_SELECTOR = "*[data-platon-thread-url]";

    private static Pattern COUNT_PATTERN = Pattern.compile(".*?(\\d++).*?");

    private final WebDriver webDriver;

    public CommentCountPage(WebDriver webDriver) {
        this.webDriver = webDriver;
    }

    public void waitUntilCommentCountsLoaded() {
        new WebDriverWait(webDriver, 15)
                .until(textMatches(By.cssSelector(HREF_SELECTOR + ", " + ATTR_SELECTOR), COUNT_PATTERN));
    }

    public Map<String, Set<Long>> getCommentCountsByThread() {

        final Map<String, Set<Long>> commentCountsByThread = new HashMap<>();

        final Map<String, Set<Long>> linkCommentCounts = getCommentCounts(
                By.cssSelector(HREF_SELECTOR), CommentCountPage::getThreadUrlFromHref);

        linkCommentCounts.forEach(commentCountsByThread::put);

        final Map<String, Set<Long>> attrCommentCounts = getCommentCounts(
                By.cssSelector(ATTR_SELECTOR), CommentCountPage::getThreadUrlFromDataAttribute);

        attrCommentCounts.forEach((threadUrl, commentCount) -> {
            if (attrCommentCounts.containsKey(threadUrl)) {
                attrCommentCounts.get(threadUrl).addAll(commentCount);
            } else {
                attrCommentCounts.put(threadUrl, commentCount);
            }
        });

        return commentCountsByThread;
    }

    private Map<String, Set<Long>> getCommentCounts(By selector, Function<WebElement, String> getThreadId) {
        return webDriver.findElements(selector).stream()
                .collect(Collectors.groupingBy(getThreadId,
                        Collectors.mapping(CommentCountPage::parseCommentCount, Collectors.toSet())));
    }

    private static Long parseCommentCount(WebElement elem) {
        final Matcher matcher = COUNT_PATTERN.matcher(elem.getText());
        if (matcher.matches()) {
            return Long.parseUnsignedLong(matcher.group(1));
        } else {
            throw new IllegalArgumentException(
                    "Comment count could not be parsed from element text: " + elem.getText());
        }
    }

    private static String getThreadUrlFromHref(WebElement elem) {
        return URI.create(elem.getAttribute("href")).getPath();
    }

    private static String getThreadUrlFromDataAttribute(WebElement elem) {
        return elem.getAttribute("data-platon-thread-url");
    }
}
