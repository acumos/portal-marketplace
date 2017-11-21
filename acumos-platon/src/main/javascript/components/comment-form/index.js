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

var UserInfoService = require('./../../services/user-info-service.js');
var CommentService = require('../../services/comment-service.js');
var TextService = require('../../services/text-service.js');
var events = require('../../utils/events.js');

var template = require('./comment-form.html');

module.exports = {
    props: {
        parentId: {
            type: Number,
            required: false
        }
    },

    render: template.render,
    staticRenderFns: template.staticRenderFns,

    data: function () {
        return {
            comment: UserInfoService.getUserInfo(),
            rememberUser: UserInfoService.getRememberUser(),
            showPreview: false,
            previewStyle: {},
            markdown: ''
        };
    },

    methods: {
        togglePreview: function () {
            this.showPreview = !this.showPreview;
            if (this.showPreview) {
                this.comment.text = TextService.markdownToHtml(this.markdown);
            }
            try {
                this.previewStyle.height = this.$el.getElementsByClassName('platon-form-text')[0].offsetHeight;
            } catch (e) {
                console.error(e.message);
            }
        },
        postComment: function () {
            var vm = this;

            if (vm.rememberUser) {
                UserInfoService.storeUserInfo({
                    author: vm.comment.author,
                    email: vm.comment.email,
                    url: vm.comment.url
                });
            } else {
                UserInfoService.removeUserInfo();
            }

            vm.comment.parentId = vm.parentId;
            vm.comment.text = TextService.markdownToHtml(vm.markdown);

            CommentService.postComment(window.location.pathname, document.title, vm.comment)
                .then(function (newComment) {
                    vm.$emit('posted', newComment);
                })
                .catch(function (error) {
                    console.error('error', error);
                });
        }
    },

    created: function () {
        var vm = this;
        events.bus.$on(events.types.clearForm, function() {
            vm.markdown = '';
        });
    }
};
