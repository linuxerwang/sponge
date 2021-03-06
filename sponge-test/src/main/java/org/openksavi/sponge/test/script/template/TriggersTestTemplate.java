/*
 * Copyright 2016-2017 The Sponge authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openksavi.sponge.test.script.template;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.PatternSyntaxException;

import org.apache.commons.lang3.exception.ExceptionUtils;

import org.openksavi.sponge.SpongeException;
import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.kb.KnowledgeBaseType;
import org.openksavi.sponge.test.util.ScriptTestUtils;

public class TriggersTestTemplate {

    public static void testTriggers(KnowledgeBaseType type) {
        SpongeEngine engine = ScriptTestUtils.startWithConfig(type, "triggers");

        try {
            await().pollDelay(1, TimeUnit.SECONDS).atMost(30, TimeUnit.SECONDS)
                    .until(() -> engine.getOperations().getVariable(AtomicBoolean.class, "receivedEventA").get());
            await().atMost(30, TimeUnit.SECONDS)
                    .until(() -> engine.getOperations().getVariable(Number.class, "receivedEventBCount").intValue() > 2);
            await().atMost(30, TimeUnit.SECONDS)
                    .until(() -> engine.getOperations().getVariable(Number.class, "receivedEventTestJavaCount").intValue() == 1);
            assertFalse(engine.isError());
        } finally {
            engine.shutdown();
        }
    }

    public static void testTriggersEventPattern(KnowledgeBaseType type) {
        SpongeEngine engine = ScriptTestUtils.startWithKnowledgeBase(type, "triggers_event_pattern");

        try {
            await().atMost(30, TimeUnit.SECONDS).until(() -> engine.getOperations().getVariable(Number.class, "countA").intValue() >= 2);
            await().atMost(30, TimeUnit.SECONDS)
                    .until(() -> engine.getOperations().getVariable(Number.class, "countAPattern").intValue() >= 5);
            assertEquals(2, engine.getOperations().getVariable(Number.class, "countA").intValue());
            assertEquals(5, engine.getOperations().getVariable(Number.class, "countAPattern").intValue());
            assertFalse(engine.isError());
        } finally {
            engine.shutdown();
        }
    }

    public static void testTriggersEventPatternIncorrect(KnowledgeBaseType type) {
        SpongeEngine engine = null;

        try {
            engine = ScriptTestUtils.startWithKnowledgeBase(type, "triggers_event_pattern_incorrect");
            fail("Expected pattern syntax exception");
        } catch (SpongeException e) {
            if (ExceptionUtils.indexOfThrowable(e, PatternSyntaxException.class) < 0) {
                throw e;
            }
        } finally {
            if (engine != null) {
                engine.shutdown();
            }
        }
    }

    public static void testHelloWorld(KnowledgeBaseType type) {
        SpongeEngine engine = ScriptTestUtils.startWithConfig(type, "triggers_hello_world");

        try {
            TimeUnit.SECONDS.sleep(1);
            assertFalse(engine.isError());
        } catch (InterruptedException e) {
            throw new SpongeException(e);
        } finally {
            engine.shutdown();
        }
    }

    public static void testTriggersBuilder(KnowledgeBaseType type) {
        SpongeEngine engine = ScriptTestUtils.startWithKnowledgeBase(type, "triggers_builder");

        try {
            await().pollDelay(1, TimeUnit.SECONDS).atMost(30, TimeUnit.SECONDS)
                    .until(() -> engine.getOperations().getVariable(AtomicBoolean.class, "receivedEventA").get());
            await().atMost(30, TimeUnit.SECONDS)
                    .until(() -> engine.getOperations().getVariable(Number.class, "receivedEventBCount").intValue() > 2);
            assertFalse(engine.isError());
        } finally {
            engine.shutdown();
        }
    }
}
