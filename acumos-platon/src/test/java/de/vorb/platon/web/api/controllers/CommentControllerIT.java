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
import de.vorb.platon.model.CommentStatus;
import de.vorb.platon.persistence.CommentRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class CommentControllerIT {

    private static final String SAMPLE_CREATION_DATE = "2017-10-06T19:45:23.751Z";
    private static final String SAMPLE_LAST_MODIFICATION_DATE = "2017-10-06T19:48:51.179Z";

    private static final CommentRecord SAMPLE_COMMENT = new CommentRecord()
            .setId(4711L)
            .setThreadId(25L)
            .setParentId(1336L)
            .setCreationDate(Timestamp.from(Instant.parse(SAMPLE_CREATION_DATE)))
            .setLastModificationDate(Timestamp.from(Instant.parse(SAMPLE_LAST_MODIFICATION_DATE)))
            .setStatus(CommentStatus.PUBLIC.toString())
            .setText("Sample text")
            .setAuthor("John Doe")
            .setEmailHash("DBe/ZuZJBwFncB0tPNcXEQ==")
            .setUrl("https://example.org");

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentRepository commentRepository;

    @Before
    public void setUp() throws Exception {
        when(commentRepository.findById(eq(SAMPLE_COMMENT.getId()))).thenReturn(Optional.of(SAMPLE_COMMENT));
    }

    @Test
    public void getCommentByIdReturnsSingleComment() throws Exception {
        mockMvc.perform(
                get("/api/comments/{id}", SAMPLE_COMMENT.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(SAMPLE_COMMENT.getId()))
                .andExpect(jsonPath("$.parentId").value(SAMPLE_COMMENT.getParentId()))
                .andExpect(jsonPath("$.creationDate").value(SAMPLE_CREATION_DATE))
                .andExpect(jsonPath("$.lastModificationDate").value(SAMPLE_LAST_MODIFICATION_DATE))
                .andExpect(jsonPath("$.status").value(SAMPLE_COMMENT.getStatus()))
                .andExpect(jsonPath("$.text").value(SAMPLE_COMMENT.getText()))
                .andExpect(jsonPath("$.author").value(SAMPLE_COMMENT.getAuthor()))
                .andExpect(jsonPath("$.emailHash").value("0c17bf66e649070167701d2d3cd71711"))
                .andExpect(jsonPath("$.url").value(SAMPLE_COMMENT.getUrl()))
                .andExpect(jsonPath("$.replies").isEmpty());
    }

}
