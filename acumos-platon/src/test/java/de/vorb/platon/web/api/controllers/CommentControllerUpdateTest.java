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
import de.vorb.platon.security.SignatureComponents;
import de.vorb.platon.web.api.errors.RequestException;
import de.vorb.platon.web.api.json.CommentJson;

import org.jooq.exception.DataAccessException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.http.HttpStatus;

import java.sql.Timestamp;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CommentControllerUpdateTest extends CommentControllerTest {

    private static final long SAMPLE_ID = 1234L;

    private static final String SAMPLE_IDENTIFIER = "/api/comments/1234";
    private static final Instant SAMPLE_EXPIRATION_TIME = Instant.parse("2017-10-21T19:19:32.092Z");
    private static final byte[] SAMPLE_SIGNATURE_TOKEN = new byte[]{
            (byte) 131, (byte) 167, (byte) 160, (byte) 189, (byte) 140, (byte) 192, (byte) 44, (byte) 244,
            (byte) 244, (byte) 69, (byte) 155, (byte) 40, (byte) 245, (byte) 148, (byte) 231, (byte) 179, (byte) 218,
            (byte) 126, (byte) 227, (byte) 101, (byte) 253, (byte) 121, (byte) 27, (byte) 223, (byte) 190, (byte) 82,
            (byte) 171, (byte) 212, (byte) 245, (byte) 211, (byte) 65, (byte) 166
    };
    private static final SignatureComponents SAMPLE_SIGNATURE =
            SignatureComponents.of(SAMPLE_IDENTIFIER, SAMPLE_EXPIRATION_TIME, SAMPLE_SIGNATURE_TOKEN);

    @Mock
    private CommentJson commentJson;
    @Spy
    private CommentRecord commentRecord = new CommentRecord();

    @Override
    @Before
    public void setUp() throws Exception {
        clock = Clock.fixed(SAMPLE_EXPIRATION_TIME.minusMillis(1), ZoneId.of("Z"));

        super.setUp();

        when(commentJson.getId()).thenReturn(SAMPLE_ID);
    }

    @Test
    public void comparesCommentIds() throws Exception {

        assertThatExceptionOfType(RequestException.class)
                .isThrownBy(() -> commentController.updateComment(
                        SAMPLE_ID + 1/*, SAMPLE_SIGNATURE.toString()*/, commentJson))
                .matches(requestException -> requestException.getHttpStatus() == HttpStatus.BAD_REQUEST)
                .withMessageStartingWith("Comment IDs do not match");
    }

    @Test
    public void updatesCommentIfAllChecksPass() throws Exception {

        when(commentRepository.findById(eq(SAMPLE_ID))).thenReturn(Optional.of(commentRecord));

        commentController.updateComment(SAMPLE_ID, /*SAMPLE_SIGNATURE.toString(),*/ commentJson);

        verify(requestValidator).verifyValidRequest(eq(SAMPLE_SIGNATURE.toString()), eq(SAMPLE_IDENTIFIER));

        verify(commentRecord).setText(eq(commentJson.getText()));
        verify(commentRecord).setAuthor(eq(commentJson.getAuthor()));
        verify(commentRecord).setUrl(eq(commentJson.getUrl()));

        verify(commentRecord).setLastModificationDate(eq(Timestamp.from(clock.instant())));

        verify(commentRepository).update(eq(commentRecord));
    }

    @Test
    public void throwsBadRequestIfCommentDoesNotExist() throws Exception {

        when(commentRepository.findById(eq(SAMPLE_ID))).thenReturn(Optional.empty());

        assertThatExceptionOfType(RequestException.class)
                .isThrownBy(() -> commentController.updateComment(SAMPLE_ID, /*SAMPLE_SIGNATURE.toString(),*/ commentJson))
                .matches(requestException -> requestException.getHttpStatus() == HttpStatus.BAD_REQUEST)
                .withMessageMatching("Comment .* does not exist");
    }

    @Test
    public void doesNotUpdateCommentIfRequestValidationFails() throws Exception {

        doThrow(RequestException.class)
                .when(requestValidator)
                .verifyValidRequest(eq(SAMPLE_SIGNATURE.toString()), eq(SAMPLE_IDENTIFIER));
        when(commentRepository.findById(eq(SAMPLE_ID))).thenReturn(Optional.of(commentRecord));

        assertThatExceptionOfType(RequestException.class)
                .isThrownBy(() -> commentController.updateComment(SAMPLE_ID,/* SAMPLE_SIGNATURE.toString(),*/ commentJson));

        verify(commentRepository, never()).update(any());
    }

    @Test
    public void throwsConflictExceptionWhenUpdateFails() throws Exception {

        when(commentRepository.findById(eq(SAMPLE_ID))).thenReturn(Optional.of(commentRecord));
        doThrow(DataAccessException.class).when(commentRepository).update(any());

        assertThatExceptionOfType(RequestException.class)
                .isThrownBy(() -> commentController.updateComment(SAMPLE_ID,/* SAMPLE_SIGNATURE.toString(),*/ commentJson))
                .matches(requestException -> requestException.getHttpStatus() == HttpStatus.CONFLICT)
                .withMessageMatching("Conflict .* comment.*");
    }

}
