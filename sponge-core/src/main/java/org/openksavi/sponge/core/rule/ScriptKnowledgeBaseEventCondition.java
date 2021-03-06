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

package org.openksavi.sponge.core.rule;

import org.openksavi.sponge.event.Event;
import org.openksavi.sponge.rule.Rule;

/**
 * Script knowledge base rule event condition.
 */
public class ScriptKnowledgeBaseEventCondition extends MethodNameEventCondition {

    /**
     * Creates a new knowledge base rule event condition.
     *
     * @param methodName knowledge base rule method name.
     */
    public ScriptKnowledgeBaseEventCondition(String methodName) {
        super(methodName);
    }

    /**
     * Checks rule event condition by evaluating the defined knowledge base rule method.
     *
     * @param rule rule.
     * @param event event.
     * @return {@code true} if this condition is met.
     */
    @Override
    public boolean condition(Rule rule, Event event) {
        Object result = rule.getKnowledgeBase().getInterpreter().invokeMethod(rule, getMethodName(), event);

        if (!(result instanceof Boolean)) {
            throw new IllegalArgumentException("Condition method must return boolean value");
        }

        return (Boolean) result;
    }
}
