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

import de.vorb.platon.jooq.tables.records.CommentRecord;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class CommentFiltersTest {

    private final CommentFilters commentFilters = new CommentFilters();

    @Test
    public void doesCommentCount() throws Exception {
        assertThat(commentFilters.doesCommentCount(commentWithStatus("DELETED"))).isFalse();
        assertThat(commentFilters.doesCommentCount(commentWithStatus("PUBLIC"))).isTrue();
        assertThat(commentFilters.doesCommentCount(commentWithStatus("AWAITING_MODERATION"))).isFalse();
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> commentFilters.doesCommentCount(commentWithStatus("UNKNOWN")));
    }

    private CommentRecord commentWithStatus(String status) {
        return new CommentRecord().setStatus(status);
    }
}
