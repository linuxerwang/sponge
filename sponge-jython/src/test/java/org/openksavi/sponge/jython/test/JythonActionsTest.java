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

package org.openksavi.sponge.jython.test;

import org.junit.jupiter.api.Test;

import org.openksavi.sponge.test.script.ActionsTest;
import org.openksavi.sponge.test.script.template.ActionsTestTemplate;

public class JythonActionsTest extends JythonTest implements ActionsTest {

    @Override
    @Test
    public void testActions() {
        ActionsTestTemplate.testActions(getType());
    }

    @Override
    @Test
    public void testHelloWorldAction() {
        ActionsTestTemplate.testHelloWorldAction(getType());
    }

    @Override
    @Test
    public void testActionJavaInheritance() {
        ActionsTestTemplate.testActionJavaInheritance(getType());
    }

    @Override
    @Test
    public void testActionBuilder() {
        ActionsTestTemplate.testActionBuilder(getType());
    }

    @Override
    @Test
    public void testActionBuilderMultiExpressionLambda() {
        ActionsTestTemplate.testActionBuilderMultiExpressionLambda(getType());
    }

    @Override
    @Test
    public void testActionBuilderNoArgAndResult() {
        ActionsTestTemplate.testActionBuilderNoArgAndResult(getType());
    }

    @Override
    public void testActionBuilderProvidedArgs() {
        ActionsTestTemplate.testActionBuilderProvidedArgs(getType());
    }

    @Override
    @Test
    public void testActionBuilderDisable() {
        ActionsTestTemplate.testActionBuilderDisable(getType());
    }
}
