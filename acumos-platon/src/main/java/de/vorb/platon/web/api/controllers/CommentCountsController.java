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

package de.vorb.platon.web.api.controllers;


import de.vorb.platon.persistence.CommentRepository;
import de.vorb.platon.web.api.json.CommentCountsJson;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Set;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;


@RestController
@RequestMapping("/api/comment-counts")
@RequiredArgsConstructor
public class CommentCountsController {

    private final CommentRepository commentRepository;

    @GetMapping(produces = APPLICATION_JSON_UTF8_VALUE)
    public CommentCountsJson getCommentCounts(@RequestParam("threadUrl") Set<String> threadUrls) {

        final CommentCountsJson.CommentCountsJsonBuilder commentCounts = CommentCountsJson.builder();

        final Map<String, Integer> counts = commentRepository.countByThreadUrls(threadUrls);

        threadUrls.forEach(threadUrl ->
                commentCounts.commentCount(threadUrl, (long) counts.getOrDefault(threadUrl, 0)));

        return commentCounts.build();
    }
}
