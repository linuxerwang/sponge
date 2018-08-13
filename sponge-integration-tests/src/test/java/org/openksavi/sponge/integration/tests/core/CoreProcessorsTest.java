/*
 * Copyright 2016-2018 The Sponge authors.
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

package org.openksavi.sponge.integration.tests.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import org.openksavi.sponge.action.Action;
import org.openksavi.sponge.action.ActionAdapter;
import org.openksavi.sponge.core.engine.DefaultSpongeEngine;
import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.integration.tests.core.scanning.ScannedAction1;
import org.openksavi.sponge.test.util.TestUtils;

public class CoreProcessorsTest {

    @Test
    public void testProcessorMetadata() {
        SpongeEngine engine =
                DefaultSpongeEngine.builder().knowledgeBase(TestUtils.DEFAULT_KB, "examples/core/processors_metadata.py").build();
        engine.startup();

        try {
            assertEquals(SpongeUtils.immutableMapOf("visibility", false),
                    engine.getActionManager().getActionAdapter("UpperEchoAction").getFeatures());
            assertEquals(SpongeUtils.immutableMapOf("visibility", true),
                    engine.getActionManager().getActionAdapter("PowerEchoMetadataAction").getFeatures());
        } finally {
            engine.shutdown();
        }
    }

    @Test
    public void testEnhancedProcessorMetadata() {
        SpongeEngine engine =
                DefaultSpongeEngine.builder().knowledgeBase(TestUtils.DEFAULT_KB, "examples/core/processors_metadata_enhanced.py").build();
        engine.startup();

        try {
            ActionAdapter enhActionAdapter = engine.getActionManager().getActionAdapter("EdvancedMetaAction");
            String methodName = (String) enhActionAdapter.getFeatures().get("isVisibleMethod");

            assertTrue((Boolean) enhActionAdapter.getKnowledgeBase().getInterpreter().invokeMethod(enhActionAdapter.getProcessor(),
                    methodName, "day"));
            assertFalse((Boolean) enhActionAdapter.getKnowledgeBase().getInterpreter().invokeMethod(enhActionAdapter.getProcessor(),
                    methodName, "night"));
        } finally {
            engine.shutdown();
        }
    }

    @Test
    public void testProcessorAdditionalInterface() {
        SpongeEngine engine =
                DefaultSpongeEngine.builder().knowledgeBase(TestUtils.DEFAULT_KB, "examples/core/processors_interface.py").build();
        engine.startup();

        try {
            ActionAdapter enhActionAdapter = engine.getActionManager().getActionAdapter("EdvancedMetaAction");
            Action action = enhActionAdapter.getProcessor();

            assertTrue(action instanceof TestActionVisibiliy);
            assertTrue(((TestActionVisibiliy) action).isVisible("day"));
            assertFalse(((TestActionVisibiliy) action).isVisible("night"));
        } finally {
            engine.shutdown();
        }
    }

    @Test
    public void testEnableJavaProcessorsByScan() {
        SpongeEngine engine =
                DefaultSpongeEngine.builder().knowledgeBase(TestUtils.DEFAULT_KB, "examples/core/processors_scan_java_packages.py").build();
        engine.startup();

        try {
            assertEquals(2, engine.getActions().size());
            assertEquals(2, engine.getTriggers().size());
            assertTrue(engine.getOperations().existsAction("ScannedAction1"));
            assertTrue(engine.getOperations().existsAction("ScannedAction2"));
            assertTrue(engine.getOperations().existsTrigger("ScannedTrigger1"));
            assertTrue(engine.getOperations().existsTrigger("ScannedTrigger2"));

            engine.getOperations().disableJavaByScan(ScannedAction1.class.getPackage().getName());

            assertEquals(0, engine.getActions().size());
            assertEquals(0, engine.getTriggers().size());
            assertFalse(engine.getOperations().existsAction("ScannedAction1"));
            assertFalse(engine.getOperations().existsAction("ScannedAction2"));
            assertFalse(engine.getOperations().existsTrigger("ScannedTrigger1"));
            assertFalse(engine.getOperations().existsTrigger("ScannedTrigger2"));
        } finally {
            engine.shutdown();
        }
    }
}
