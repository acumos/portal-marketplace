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

package de.vorb.platon.persistence.impl;

import de.vorb.platon.jooq.tables.records.PropertyRecord;
import de.vorb.platon.persistence.PropertyRepository;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import static de.vorb.platon.jooq.tables.Property.PROPERTY;

@Repository
@RequiredArgsConstructor
public class JooqPropertyRepository implements PropertyRepository {

    private final DSLContext dslContext;

    @Override
    public String findValueByKey(String key) {
        return dslContext.selectFrom(PROPERTY)
                .where(PROPERTY._KEY.eq(key))
                .fetchOne(PROPERTY.VALUE);
    }

    @Override
    public void insertValue(String key, String value) {
        dslContext.insertInto(PROPERTY)
                .set(new PropertyRecord(key, value))
                .execute();
    }

}
