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
import de.vorb.platon.jooq.tables.records.ThreadRecord;
import de.vorb.platon.security.SignatureComponents;
import de.vorb.platon.web.api.errors.RequestException;
import de.vorb.platon.web.api.json.CommentJson;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RunWith(MockitoJUnitRunner.class)
public class CommentControllerPostTest extends CommentControllerTest {

    private static final AtomicLong COMMENT_ID_SEQUENCE = new AtomicLong();

    @Mock
    private CommentJson commentJson;

    @Spy
    private CommentRecord comment = new CommentRecord();
    @Spy
    private CommentRecord parentComment = new CommentRecord();

    @Test
    public void createsNewThreadOnDemand() throws Exception {

        insertCommentReturnsCommentWithNextId();
        prepareMocksForPostRequest();
        when(comment.getParentId()).thenReturn(null);

        final ResponseEntity<CommentJson> response =
                commentController.postComment(THREAD_URL, THREAD_TITLE, commentJson);
        verify(threadRepository).insert(eq(new ThreadRecord(null, THREAD_URL, THREAD_TITLE)));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThatLocationHeaderIsCorrect(response.getHeaders().getLocation());
    }

    @Test
    public void verifiesParentBelongsToSameThread() throws Exception {

        insertCommentReturnsCommentWithNextId();
        prepareMocksForPostRequest();

        final long parentId = 1L;
        when(comment.getParentId()).thenReturn(parentId);
        when(commentRepository.findById(eq(parentId))).thenReturn(Optional.of(parentComment));
        when(comment.getThreadId()).thenReturn(1L);
        when(parentComment.getThreadId()).thenReturn(2L);

        assertThatExceptionOfType(RequestException.class)
                .isThrownBy(() -> commentController.postComment(THREAD_URL, THREAD_TITLE, commentJson))
                .matches(requestException -> requestException.getHttpStatus() == BAD_REQUEST)
                .withMessage("Parent comment does not belong to same thread");

        verify(commentRepository, never()).insert(any());

        when(parentComment.getThreadId()).thenReturn(1L);

        assertThatCode(() -> commentController.postComment(THREAD_URL, THREAD_TITLE, commentJson))
                .doesNotThrowAnyException();

        verify(commentRepository).insert(eq(comment));
    }

    @Test
    public void verifiesParentCommentExists() throws Exception {

        insertCommentReturnsCommentWithNextId();
        prepareMocksForPostRequest();

        final long parentId = 1L;
        when(comment.getParentId()).thenReturn(parentId);
        when(commentRepository.findById(eq(parentId))).thenReturn(Optional.empty());
        when(comment.getThreadId()).thenReturn(1L);
        when(parentComment.getThreadId()).thenReturn(1L);

        assertThatExceptionOfType(RequestException.class)
                .isThrownBy(() -> commentController.postComment(THREAD_URL, THREAD_TITLE, commentJson))
                .matches(requestException -> requestException.getHttpStatus() == BAD_REQUEST)
                .withMessage("Parent comment does not exist");

        verify(commentRepository, never()).insert(any());
    }

    private void prepareMocksForPostRequest() {
        when(commentJson.getId()).thenReturn(null);
        when(threadRepository.findThreadIdForUrl(any())).thenReturn(Optional.empty());
        when(threadRepository.insert(any())).thenReturn(new ThreadRecord().setId(1L));
        convertCommentJsonToRecord(commentJson, comment);
        when(signatureCreator.createSignatureComponents(any(), any())).thenReturn(mock(SignatureComponents.class));
    }

    private void assertThatLocationHeaderIsCorrect(URI location) {
        assertThat(location).hasScheme(null);
        assertThat(location).hasHost(null);
        assertThat(location).hasNoPort();
        assertThat(location).hasPath("/api/comments/" + comment.getId());
        assertThat(location).hasNoParameters();
    }

    @Test
    public void postCommentWithIdThrowsBadRequestException() throws Exception {
        when(commentJson.getId()).thenReturn(1337L);

        assertThatExceptionOfType(RequestException.class)
                .isThrownBy(() -> commentController.postComment(THREAD_URL, THREAD_TITLE, commentJson))
                .matches(exception -> exception.getHttpStatus() == BAD_REQUEST)
                .withMessage("Comment ID is not null");
    }

    private void insertCommentReturnsCommentWithNextId() {
        when(commentRepository.insert(any()))
                .then(invocation -> {
                    final CommentRecord insertedComment = (CommentRecord) invocation.getArguments()[0];
                    final long nextCommentId = COMMENT_ID_SEQUENCE.incrementAndGet();
                    return insertedComment.setId(nextCommentId);
                });
    }

}
