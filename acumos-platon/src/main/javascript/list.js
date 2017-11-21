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

var Vue = require('vue');
var CommentService = require('./services/comment-service.js');
var updateCommentInList = require('./utils/update-comment-in-list.js');
var removeCommentFromList = require('./utils/remove-comment-from-list.js');
var events = require('./utils/events.js');

var template = require('./list.html');

var userHasScrolled = false;

if (document.getElementById('platon-comment-thread') !== null) {

    new Vue({
        el: '#platon-comment-thread',
        render: template.render,
        staticRenderFns: template.staticRenderFns,

        data: {
            loading: true,
            comments: []
        },

        components: {
            'platon-comment': require('./components/comment'),
            'platon-comment-form': require('./components/comment-form')
        },

        methods: {
            commentPosted: function (newComment) {
                this.comments.push(newComment);
                events.bus.$emit(events.types.clearForm);
            },
            commentEdited: function (updatedComment) {
                updateCommentInList(this.comments, updatedComment);
            },
            commentDeleted: function (commentToRemove) {
                removeCommentFromList(this.comments, commentToRemove);
            }
        },

        created: function () {
            var vm = this;
            CommentService.getComments(window.location.pathname)
                .then(function updateModel(commentsListResult) {
                    vm.comments = commentsListResult.comments;
                    vm.totalCommentCount = commentsListResult.totalCommentCount;
                    vm.loading = false;

                    if (window.location.hash && window.location.hash.indexOf('#platon-comment-') >= 0) {
                        Vue.nextTick(function () {
                            var commentElem = document.querySelector(window.location.hash);
                            if (commentElem != null && !userHasScrolled) {
                                commentElem.scrollIntoView(true);
                            }
                        });
                    }
                })
                .catch(function displayError(reason) {
                    alert(reason);
                });
        }
    });

    window.addEventListener('scroll', scrollListener);
}

function scrollListener() {
    userHasScrolled = true;
    window.removeEventListener('scroll', scrollListener);
}
