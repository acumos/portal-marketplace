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

var CommentService = require('../../services/comment-service.js');
var TextService = require('../../services/text-service.js');
var updateCommentInList = require('../../utils/update-comment-in-list.js');
var removeCommentFromList = require('../../utils/remove-comment-from-list.js');

var template = require('./comment.html');

module.exports = {
    name: 'platon-comment',
    props: {
        comment: {
            type: Object,
            required: true
        }
    },

    render: template.render,
    staticRenderFns: template.staticRenderFns,

    data: function () {
        return {
            showReplyForm: false,
            showEditForm: false,
            showPreview: false,
            markdown: '',
            editedComment: {
                text: ''
            }
        }
    },

    computed: {
        creationDate: function () {
            return new Date(this.comment.creationDate).toLocaleString();
        },
        longCreationDate: function () {
            return 'Created: ' + new Date(this.comment.creationDate).toISOString();
        },
        longModificationDate: function () {
            return 'Last modified: ' + new Date(this.comment.lastModificationDate).toISOString();
        },
        canEdit: function () {
            return CommentService.canEditComment(this.comment);
        },
        canDelete: function () {
            return CommentService.canDeleteComment(this.comment);
        },
        permalinkId: function () {
            return 'platon-comment-' + this.comment.id;
        }
    },

    components: {
        'platon-comment-form': require('../comment-form')
    },

    methods: {
        replyPosted: function (newComment) {
            this.comment.replies.push(newComment);
            this.showReplyForm = false;
        },
        replyEdited: function (updatedReply) {
            updateCommentInList(this.comment.replies, updatedReply);
        },
        replyDeleted: function (replyToRemove) {
            removeCommentFromList(this.comment.replies, replyToRemove);
        },
        toggleEditPreview: function () {
            this.showPreview = !this.showPreview;

            if (this.showPreview) {
                this.editedComment.text = TextService.markdownToHtml(this.markdown);
            }
        },
        updateMarkdown: function () {
            this.markdown = TextService.htmlToMarkdown(this.comment.text);
        },
        saveEdit: function () {
            var vm = this;

            var comment = JSON.parse(JSON.stringify(this.comment));
            comment.text = TextService.markdownToHtml(this.markdown);

            CommentService.updateComment(comment)
                .then(function () {
                    vm.$emit('edited', comment);
                    vm.showEditForm = false;
                })
                .catch(function () {
                    console.error('error', arguments);
                });
        },
        deleteComment: function () {
            if (confirm('Do you really want to delete this comment?')) {
                var vm = this;
                CommentService.deleteComment(vm.comment).then(function () {
                    vm.$emit('deleted', vm.comment);
                });
            }
        }
    }
};
