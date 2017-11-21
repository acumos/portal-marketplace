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

module.exports = function findGroupedPlatonThreadUrlElements() {

    var groupedElements = {};

    function addThreadUrlAndElement(threadUrl, elem) {
        if (!(threadUrl in groupedElements)) {
            groupedElements[threadUrl] = [elem];
        } else if (groupedElements[threadUrl].indexOf(threadUrl) < 0) {
            groupedElements[threadUrl].push(elem);
        }
    }

    var linksToCommentThreads = document.querySelectorAll('a[href$="#platon-comment-thread"]');

    Array.prototype.forEach.call(linksToCommentThreads, function (linkElem) {
        addThreadUrlAndElement(linkElem.pathname, linkElem);
    });

    var elemsWithThreadIds = document.querySelectorAll('*[data-platon-thread-url]');

    Array.prototype.forEach.call(elemsWithThreadIds, function (elem) {
        addThreadUrlAndElement(elem.getAttribute('data-platon-thread-url'), elem);
    });

    return groupedElements;
};
