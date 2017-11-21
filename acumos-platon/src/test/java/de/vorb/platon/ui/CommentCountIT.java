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

package de.vorb.platon.ui;

import de.vorb.platon.SpringUiIntegrationTestConfig;
import de.vorb.platon.jooq.tables.records.CommentRecord;
import de.vorb.platon.jooq.tables.records.ThreadRecord;
import de.vorb.platon.model.CommentStatus;
import de.vorb.platon.ui.pages.CommentCountPage;

import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.UnsupportedCommandException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static de.vorb.platon.jooq.Tables.COMMENT;
import static de.vorb.platon.jooq.Tables.THREAD;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = SpringUiIntegrationTestConfig.class)
@Slf4j
public class CommentCountIT {

    @Autowired
    private WebDriver webDriver;

    @Autowired
    private DSLContext dslContext;

    @Value("http://localhost:${server.port}/comment-count.html")
    private String testUrl;

    private static final String URL_THREAD_1 = "/comment-count-thread-1.html";
    private static final String URL_THREAD_2 = "/comment-count-thread-2.html";

    private Long thread1Id;
    private List<CommentRecord> commentsThread1 = new ArrayList<>();
    private List<CommentRecord> commentsThread2 = Collections.emptyList();

    @Before
    public void setUp() throws Exception {
        ThreadRecord thread = new ThreadRecord();
        thread.setUrl(URL_THREAD_1);
        thread.setTitle("Thread Count 1");
        thread = dslContext.insertInto(THREAD).set(thread).returning(THREAD.ID).fetchOne();
        thread1Id = thread.getId();

        CommentRecord comment = new CommentRecord();
        comment.setThreadId(thread1Id);
        comment.setCreationDate(Timestamp.from(Instant.now()));
        comment.setLastModificationDate(comment.getCreationDate());
        comment.setStatus(CommentStatus.PUBLIC.toString());
        comment.setText("Comment 1");
        comment = dslContext.insertInto(COMMENT).set(comment).returning(COMMENT.ID, COMMENT.STATUS).fetchOne();
        commentsThread1.add(comment);

        comment = new CommentRecord();
        comment.setThreadId(thread1Id);
        comment.setCreationDate(Timestamp.from(Instant.now()));
        comment.setLastModificationDate(comment.getCreationDate());
        comment.setStatus(CommentStatus.AWAITING_MODERATION.toString());
        comment.setText("Comment 2");
        comment = dslContext.insertInto(COMMENT).set(comment).returning(COMMENT.ID, COMMENT.STATUS).fetchOne();
        commentsThread1.add(comment);

        comment = new CommentRecord();
        comment.setThreadId(thread1Id);
        comment.setCreationDate(Timestamp.from(Instant.now()));
        comment.setLastModificationDate(comment.getCreationDate());
        comment.setStatus(CommentStatus.DELETED.toString());
        comment.setText("Comment 1");
        comment = dslContext.insertInto(COMMENT).set(comment).returning(COMMENT.ID, COMMENT.STATUS).fetchOne();
        commentsThread1.add(comment);

        comment = new CommentRecord();
        comment.setThreadId(thread1Id);
        comment.setParentId(comment.getId());
        comment.setCreationDate(Timestamp.from(Instant.now()));
        comment.setLastModificationDate(comment.getCreationDate());
        comment.setStatus(CommentStatus.PUBLIC.toString());
        comment.setText("Comment 1");
        comment = dslContext.insertInto(COMMENT).set(comment).returning(COMMENT.ID, COMMENT.STATUS).fetchOne();
        commentsThread1.add(comment);
    }

    @After
    public void tearDown() throws Exception {
        dslContext.deleteFrom(COMMENT).where(COMMENT.THREAD_ID.eq(thread1Id)).execute();
        dslContext.deleteFrom(THREAD).where(THREAD.ID.eq(thread1Id)).execute();
    }

    @Test
    public void loadCommentCounts() throws Exception {

        final CommentCountPage commentPage = new CommentCountPage(webDriver);

        webDriver.get(testUrl);
        try {
            commentPage.waitUntilCommentCountsLoaded();
        } catch (TimeoutException e) {
            displayBrowserLogs();
        }

        final Map<String, Set<Long>> commentCountsByThread = commentPage.getCommentCountsByThread();

        commentCountsByThread.values().forEach(commentCounts -> assertThat(commentCounts).hasSize(1));

        final long countThread1 = commentCountsByThread.get(URL_THREAD_1).iterator().next();
        final long expectedCountThread1 = commentsThread1.stream().filter(
                comment -> CommentStatus.valueOf(comment.getStatus()) == CommentStatus.PUBLIC).count();

        assertThat(countThread1).isEqualTo(expectedCountThread1);

        final long countThread2 = commentCountsByThread.get(URL_THREAD_2).iterator().next();
        final long expectedCountThread2 = commentsThread2.stream().filter(
                comment -> CommentStatus.valueOf(comment.getStatus()) == CommentStatus.PUBLIC).count();

        assertThat(countThread2).isEqualTo(expectedCountThread2);
    }

    private void displayBrowserLogs() {
        try {
            webDriver.manage().logs().get(LogType.BROWSER).getAll().forEach(this::logBrowserLogEntry);
        } catch (UnsupportedCommandException e) {
            log.warn("Unable to collect logs from browser");
        }
    }

    private void logBrowserLogEntry(LogEntry logEntry) {
        log.info("Browser log: <{}> [{}] {}", logEntry.getLevel(),
                Instant.ofEpochMilli(logEntry.getTimestamp()), logEntry.getMessage());
    }
}
