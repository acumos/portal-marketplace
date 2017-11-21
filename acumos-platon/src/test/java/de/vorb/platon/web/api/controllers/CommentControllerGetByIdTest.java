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

import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

public class CommentControllerGetByIdTest extends CommentControllerTest {

    @Mock
    private CommentJson commentJson;

    @Test
    public void returnsPublicComment() throws Exception {

        final long commentId = 4711;
        final CommentRecord publicComment =
                new CommentRecord()
                        .setId(commentId)
                        .setText("Text")
                        .setStatus("PUBLIC");

        when(commentRepository.findById(eq(commentId))).thenReturn(Optional.of(publicComment));
        convertCommentRecordToJson(publicComment, commentJson);

        assertThat(commentController.getCommentById(commentId)).isSameAs(commentJson);
    }

    @Test
    public void throwsNotFoundForDeletedComment() throws Exception {

        final long commentId = 1337;
        final CommentRecord deletedComment =
                new CommentRecord()
                        .setId(commentId)
                        .setText("Text")
                        .setStatus("DELETED");

        when(commentRepository.findById(eq(commentId))).thenReturn(Optional.of(deletedComment));

        assertThatExceptionOfType(RequestException.class)
                .isThrownBy(() -> commentController.getCommentById(commentId))
                .matches(exception -> exception.getHttpStatus() == HttpStatus.NOT_FOUND)
                .withMessage("No comment found with ID = " + commentId);
    }

}
