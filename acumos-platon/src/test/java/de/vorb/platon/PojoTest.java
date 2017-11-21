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

package de.vorb.platon;

import de.vorb.platon.web.api.json.CommentCountsJson;
import de.vorb.platon.web.api.json.CommentJson;
import de.vorb.platon.web.api.json.CommentListResultJson;

import com.openpojo.random.RandomFactory;
import com.openpojo.random.RandomGenerator;
import com.openpojo.reflection.PojoClass;
import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.validation.Validator;
import com.openpojo.validation.ValidatorBuilder;
import com.openpojo.validation.rule.impl.NoFieldShadowingRule;
import com.openpojo.validation.rule.impl.NoPrimitivesRule;
import com.openpojo.validation.rule.impl.NoPublicFieldsRule;
import com.openpojo.validation.rule.impl.NoStaticExceptFinalRule;
import com.openpojo.validation.rule.impl.SerializableMustHaveSerialVersionUIDRule;
import com.openpojo.validation.test.impl.GetterTester;
import com.openpojo.validation.test.impl.SetterTester;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;
import java.util.stream.Stream;

@RunWith(Parameterized.class)
public class PojoTest {

    @BeforeClass
    public static void initializeTestDependencies() {
        RandomFactory.addRandomGenerator(new InstantGenerator());
    }

    @Parameters
    public static PojoClass[] getParameters() {
        return Stream.of(CommentJson.class, CommentListResultJson.class, CommentCountsJson.class)
                .map(PojoClassFactory::getPojoClass)
                .toArray(PojoClass[]::new);
    }

    private final PojoClass pojoClass;

    private Validator validator;

    public PojoTest(PojoClass pojoClass) {
        this.pojoClass = pojoClass;
    }

    @Before
    public void setUp() throws Exception {
        validator = ValidatorBuilder.create()
                .with(new NoPrimitivesRule())
                .with(new NoStaticExceptFinalRule())
                .with(new SerializableMustHaveSerialVersionUIDRule())
                .with(new NoFieldShadowingRule())
                .with(new NoPublicFieldsRule())
                .with(new SetterTester())
                .with(new GetterTester())
                .build();
    }

    @Test
    public void validate() throws Exception {
        validator.validate(pojoClass);
    }

    private static class InstantGenerator implements RandomGenerator {

        private static final Random random = new Random(System.currentTimeMillis());

        public Object doGenerate(Class<?> type) {
            return Instant.ofEpochMilli(random.nextLong());
        }

        public Collection<Class<?>> getTypes() {
            return Collections.singletonList(Instant.class);
        }
    }
}
