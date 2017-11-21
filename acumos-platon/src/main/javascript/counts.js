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

var CommentService = require('./services/comment-service.js');

var findGroupedPlatonThreadUrlElements = require('./utils/find-thread-url-elements.js');

var groupedThreadUrlElements = findGroupedPlatonThreadUrlElements();
var threadUrls = Object.keys(groupedThreadUrlElements);

CommentService.countComments(threadUrls).then(function(commentCounts) {
    for (var threadUrl in commentCounts) {
        if (commentCounts.hasOwnProperty(threadUrl)) {
            groupedThreadUrlElements[threadUrl].forEach(function (element) {
                var count = commentCounts[threadUrl];
                element.textContent = count + (count === 1 ? ' Comment' : ' Comments');
            });
        }
    }
});
