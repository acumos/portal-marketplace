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
import de.vorb.platon.model.CommentStatus;
import de.vorb.platon.web.api.json.CommentJson;

import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;

@Component
public class CommentConverter {

    private final MessageDigest md5;

    @SneakyThrows
    public CommentConverter() {
        this.md5 = MessageDigest.getInstance("MD5");
    }

    public CommentJson convertRecordToJson(CommentRecord record) {

        final Instant creationDate = record.getCreationDate() == null
                ? null
                : record.getCreationDate().toInstant();

        final Instant lastModificationDate = record.getLastModificationDate() == null
                ? null
                : record.getLastModificationDate().toInstant();

        final CommentStatus status = record.getStatus() == null
                ? null
                : CommentStatus.valueOf(record.getStatus());

        final CommentJson.CommentJsonBuilder json = CommentJson.builder()
                .id(record.getId())
                .parentId(record.getParentId())
                .creationDate(creationDate)
                .lastModificationDate(lastModificationDate)
                .status(status)
                .replies(new ArrayList<>());

        if (status != CommentStatus.DELETED) {

            json.text(record.getText());
            json.author(record.getAuthor());
            json.name(record.getName());
            json.url(record.getUrl());

            if (record.getEmailHash() != null) {
                json.emailHash(Base64.getDecoder().decode(record.getEmailHash()));
            }
        }

        return json.build();
    }

    public CommentRecord convertJsonToRecord(CommentJson json) {
        return new CommentRecord()
                .setId(json.getId())
                .setParentId(json.getParentId())
                .setCreationDate(
                        json.getCreationDate() == null
                                ? null
                                : Timestamp.from(json.getCreationDate()))
                .setLastModificationDate(
                        json.getLastModificationDate() == null
                                ? null
                                : Timestamp.from(json.getLastModificationDate()))
                .setStatus(
                        json.getStatus() == null
                                ? null
                                : json.getStatus().toString())
                .setText(json.getText())
                .setAuthor(json.getAuthor())
                .setName(json.getName())
                .setEmailHash(calculateEmailHash(json.getEmail()))
                .setUrl(json.getUrl());
    }

    private String calculateEmailHash(String email) {
        if (email == null) {
            return null;
        }

        return ByteArrayConverter.bytesToHexString(md5.digest(email.getBytes(StandardCharsets.UTF_8)));
    }

}
