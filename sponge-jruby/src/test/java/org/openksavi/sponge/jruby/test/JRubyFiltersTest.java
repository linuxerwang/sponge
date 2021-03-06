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

package org.openksavi.sponge.jruby.test;

import org.junit.jupiter.api.Test;

import org.openksavi.sponge.test.script.FiltersTest;
import org.openksavi.sponge.test.script.template.FiltersTestTemplate;

public class JRubyFiltersTest extends JRubyTest implements FiltersTest {

    @Override
    @Test
    public void testJavaFilter() {
        FiltersTestTemplate.testJavaFilter(getType());
    }

    @Override
    @Test
    public void testFilter() {
        FiltersTestTemplate.testFilter(getType());
    }

    @Override
    @Test
    public void testDeduplication() {
        FiltersTestTemplate.testDeduplication(getType());
    }

    @Override
    @Test
    public void testFilterBuilder() {
        FiltersTestTemplate.testFilterBuilder(getType());
    }
}
