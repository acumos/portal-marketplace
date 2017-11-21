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

import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Collections;

import static de.vorb.platon.web.api.controllers.CommentController.PATH_SINGLE;
import static de.vorb.platon.web.api.controllers.CommentController.PATH_VAR_COMMENT_ID;

@Component
public class CommentUriResolver {

    @SneakyThrows
    public URI createRelativeCommentUriForId(long commentId) {
        return new URI(ServletUriComponentsBuilder.fromCurrentRequest()
                .path(PATH_SINGLE)
                .replaceQuery(null)
                .buildAndExpand(Collections.singletonMap(PATH_VAR_COMMENT_ID, commentId))
                .getPath());
    }

}
