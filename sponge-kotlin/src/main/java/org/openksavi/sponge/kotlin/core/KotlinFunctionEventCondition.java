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

package org.openksavi.sponge.kotlin.core;

import kotlin.reflect.KFunction;

import org.openksavi.sponge.Experimental;
import org.openksavi.sponge.event.Event;
import org.openksavi.sponge.rule.EventCondition;
import org.openksavi.sponge.rule.Rule;

/**
 * Kotlin function rule event condition. Currently not used.
 */
@Experimental
public class KotlinFunctionEventCondition implements EventCondition {

    private KFunction<Boolean> function;

    /**
     * Creates a new Kotlin function rule event condition.
     *
     * @param function a Kotlin function.
     *
     */
    public KotlinFunctionEventCondition(KFunction<Boolean> function) {
        this.function = function;
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
        return function.call(rule, event);
    }
}
