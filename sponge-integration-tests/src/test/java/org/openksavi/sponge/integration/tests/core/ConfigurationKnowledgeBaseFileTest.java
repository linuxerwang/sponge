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

package org.openksavi.sponge.integration.tests.core;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.openksavi.sponge.config.ConfigException;
import org.openksavi.sponge.core.engine.DefaultSpongeEngine;
import org.openksavi.sponge.engine.SpongeEngine;

public class ConfigurationKnowledgeBaseFileTest {

    private static final String DIR = "examples/core/configuration/kb_file/";

    private void doTestExisting(String config) {
        SpongeEngine engine = DefaultSpongeEngine.builder().config(DIR + config).build();
        engine.startup();

        try {
            assertTrue(engine.getOperations().getVariable(Boolean.class, "loaded"));
            assertFalse(engine.isError());
        } finally {
            engine.shutdown();
        }
    }

    @Test
    public void testKnowledgeBaseFileOptionalNonExisting() {
        SpongeEngine engine = DefaultSpongeEngine.builder().config(DIR + "configuration_kb_file_optional_non_existing.xml").build();
        engine.startup();

        try {
            assertFalse(engine.getOperations().hasVariable("loaded"));
            assertFalse(engine.isError());
        } finally {
            engine.shutdown();
        }
    }

    @Test
    public void testKnowledgeBaseFileOptionalExisting() {
        doTestExisting("configuration_kb_file_optional_existing.xml");
    }

    private void doTestNonExistingRequired(String config) {
        SpongeEngine engine = DefaultSpongeEngine.builder().config(DIR + config).build();

        try {
            engine.startup();
        } finally {
            engine.shutdown();
        }
    }

    @Test
    public void testKnowledgeBaseFileRequiredNonExisting() {
        assertThrows(ConfigException.class, () -> doTestNonExistingRequired("configuration_kb_file_required_non_existing.xml"));
    }

    @Test
    public void testKnowledgeBaseFileRequiredExisting() {
        doTestExisting("configuration_kb_file_required_existing.xml");
    }

    @Test
    public void testKnowledgeBaseFileDefaultNonExisting() {
        assertThrows(ConfigException.class, () -> doTestNonExistingRequired("configuration_kb_file_default_non_existing.xml"));
    }

    @Test
    public void testKnowledgeBaseFileDefaultExisting() {
        doTestExisting("configuration_kb_file_default_existing.xml");
    }
}
