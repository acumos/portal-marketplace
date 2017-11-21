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
import de.vorb.platon.web.api.errors.RequestException;
import de.vorb.platon.web.api.json.CommentJson;
import de.vorb.platon.web.api.json.CommentListResultJson;

import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CommentControllerFindByThreadUrlTest extends CommentControllerTest {

    @Mock
    private CommentJson commentJson;
    @Mock
    private CommentJson childCommentJson;

    @Test
    public void returnsCommentsAsTree() throws Exception {

        final CommentRecord comment = new CommentRecord().setId(4711L);
        final CommentRecord childComment = new CommentRecord().setId(4712L).setParentId(comment.getId());

        final List<CommentRecord> records = Arrays.asList(comment, childComment);

        when(commentRepository.findByThreadUrl(eq(THREAD_URL))).thenReturn(records);
        acceptAllComments();

        convertCommentRecordToJson(comment, commentJson);
        convertCommentRecordToJson(childComment, childCommentJson);

        when(commentJson.getId()).thenReturn(comment.getId());
        when(commentJson.getReplies()).thenReturn(new ArrayList<>());
        when(childCommentJson.getId()).thenReturn(childComment.getId());

        final CommentListResultJson resultJson = commentController.findCommentsByThreadUrl(THREAD_URL);

        assertThat(resultJson.getComments()).isEqualTo(Collections.singletonList(commentJson));
        assertThat(resultJson.getComments().get(0).getReplies()).isEqualTo(Collections.singletonList(childCommentJson));
        assertThat(resultJson.getTotalCommentCount()).isEqualTo(records.size());

        records.forEach(record -> verify(commentFilters).doesCommentCount(eq(record)));
    }

    private void acceptAllComments() {
        when(commentFilters.doesCommentCount(any())).thenReturn(true);
    }

    @Test
    public void throwsNotFoundIfThreadIsEmpty() throws Exception {

        when(commentRepository.findByThreadUrl(any())).thenReturn(Collections.emptyList());

        assertThatExceptionOfType(RequestException.class)
                .isThrownBy(() -> commentController.findCommentsByThreadUrl(THREAD_URL))
                .matches(exception -> exception.getHttpStatus() == HttpStatus.NOT_FOUND)
                .withMessage("No thread found with url = '" + THREAD_URL + "'");
    }
}
