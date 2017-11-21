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

import de.vorb.platon.jooq.tables.records.CommentRecord;
import de.vorb.platon.persistence.CommentRepository;
import de.vorb.platon.persistence.ThreadRepository;
import de.vorb.platon.security.SignatureCreator;
import de.vorb.platon.web.api.common.CommentConverter;
import de.vorb.platon.web.api.common.CommentFilters;
import de.vorb.platon.web.api.common.CommentSanitizer;
import de.vorb.platon.web.api.common.CommentUriResolver;
import de.vorb.platon.web.api.common.RequestValidator;
import de.vorb.platon.web.api.json.CommentJson;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.net.URI;
import java.time.Clock;

import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("WeakerAccess")
public abstract class CommentControllerTest {

    protected static final String THREAD_URL = "/sample-article.html";
    protected static final String THREAD_TITLE = "Article";

    protected CommentController commentController;

    protected Clock clock = Clock.systemUTC();

    @Mock
    protected ThreadRepository threadRepository;
    @Mock
    protected CommentRepository commentRepository;
    @Mock
    protected SignatureCreator signatureCreator;

    @Mock
    protected CommentConverter commentConverter;
    @Mock
    protected CommentUriResolver commentUriResolver;
    @Mock
    protected RequestValidator requestValidator;
    @Mock
    protected CommentFilters commentFilters;
    @Mock
    protected CommentSanitizer commentSanitizer;

    @Before
    public void setUp() throws Exception {
        commentController = new CommentController(clock, threadRepository, commentRepository, signatureCreator,
                commentConverter, commentUriResolver, requestValidator, commentFilters, commentSanitizer);

        when(commentUriResolver.createRelativeCommentUriForId(anyLong()))
                .thenAnswer(invocation -> new URI("/api/comments/" + invocation.getArgumentAt(0, long.class)));
    }

    protected void convertCommentRecordToJson(CommentRecord record, CommentJson json) {
        when(commentConverter.convertRecordToJson(eq(record))).thenReturn(json);
    }

    protected void convertCommentJsonToRecord(CommentJson json, CommentRecord record) {
        when(commentConverter.convertJsonToRecord(eq(json))).thenReturn(record);
    }

}
