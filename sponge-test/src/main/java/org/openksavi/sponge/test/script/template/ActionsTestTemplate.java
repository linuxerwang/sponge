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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.openksavi.sponge.action.ActionAdapter;
import org.openksavi.sponge.action.ArgMeta;
import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.kb.KnowledgeBaseType;
import org.openksavi.sponge.test.util.ScriptTestUtils;
import org.openksavi.sponge.type.StringType;

public class ActionsTestTemplate {

    public static void testActions(KnowledgeBaseType type) {
        SpongeEngine engine = ScriptTestUtils.startWithKnowledgeBase(type, "actions");

        try {
            await().atMost(30, TimeUnit.SECONDS).until(() -> engine.getOperations().getVariable("scriptActionResult") != null);
            await().atMost(30, TimeUnit.SECONDS).until(() -> engine.getOperations().getVariable("javaActionResult") != null);

            Object scriptResultObject = engine.getOperations().getVariable("scriptActionResult");
            @SuppressWarnings("rawtypes")
            List scriptResult = scriptResultObject instanceof List ? (List) scriptResultObject
                    : Arrays.stream((Object[]) scriptResultObject).collect(Collectors.toList());
            assertEquals(2, scriptResult.size());
            // Note, that different scripting engines may map numbers to different types.
            assertEquals(1, ((Number) scriptResult.get(0)).intValue());
            assertEquals("test", scriptResult.get(1));

            Object[] javaResult = (Object[]) engine.getOperations().getVariable("javaActionResult");
            assertEquals(2, javaResult.length);
            // Note, that different scripting engines may map numbers to different types.
            assertEquals(2, ((Number) javaResult[0]).intValue());
            assertEquals("TEST", javaResult[1]);

            assertEquals(3, engine.getOperations()
                    .call(Number.class, "ArrayArgumentAction", Arrays.asList((Object) new Object[] { 1, 2, "text" })).intValue());

            assertFalse(engine.isError());
        } finally {
            engine.shutdown();
        }
    }

    public static void testHelloWorldAction(KnowledgeBaseType type) {
        SpongeEngine engine = ScriptTestUtils.startWithKnowledgeBase(type, "actions_hello_world");

        try {
            String actionName = "HelloWorldAction";
            ActionAdapter adapter = engine.getActionManager().getActionAdapter(actionName);
            assertEquals(actionName, adapter.getName());
            assertEquals("Hello world", adapter.getLabel());
            assertEquals("Returns a greeting text.", adapter.getDescription());
            assertEquals(1, adapter.getArgsMeta().size());
            assertTrue(adapter.getFeatures().isEmpty());

            ArgMeta<?> argMeta = adapter.getArgsMeta().get(0);
            assertEquals("name", argMeta.getName());
            assertEquals("Your name", argMeta.getLabel());
            assertEquals("Type your name.", argMeta.getDescription());
            assertTrue(argMeta.getType() instanceof StringType);

            assertEquals("Greeting", adapter.getResultMeta().getLabel());
            assertEquals("The greeting text.", adapter.getResultMeta().getDescription());
            assertTrue(adapter.getResultMeta().getType() instanceof StringType);

            String name = "Sponge user";
            assertEquals(String.format("Hello World! Hello %s!", name), engine.getOperations().call(actionName, Arrays.asList(name)));

            assertFalse(engine.isError());
        } finally {
            engine.shutdown();
        }
    }
}
