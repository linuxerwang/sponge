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

package org.openksavi.sponge.nashorn;

import java.util.List;
import java.util.function.Function;

import jdk.nashorn.api.scripting.ScriptObjectMirror;

import org.openksavi.sponge.core.rule.BaseRule;
import org.openksavi.sponge.core.rule.CompositeEventCondition;
import org.openksavi.sponge.event.Event;
import org.openksavi.sponge.nashorn.core.NashornScriptKnowledgeBaseEventCondition;
import org.openksavi.sponge.rule.EventCondition;

/**
 * JavaScript-specific implementation of the rule.
 */
@SuppressWarnings("restriction")
public abstract class NashornRule extends BaseRule implements NashornScriptObject {

    private static final Function<? super ScriptObjectMirror, ? extends EventCondition> MAPPER =
            function -> new NashornScriptKnowledgeBaseEventCondition(function);

    /** JavaScript object to overcome class inheritance limitations in JavaScript and Nashorn. Doesn't have to be thread safe. */
    private Object target;

    @Override
    public Object getSelf() {
        return this;
    }

    @Override
    public Object getTarget() {
        return target;
    }

    @Override
    public void setTarget(Object target) {
        this.target = target;
    }

    @Override
    public final void onConfigure() {
        onConfigure(getSelf());
    }

    public abstract void onConfigure(Object self);

    @Override
    public final void onInit() {
        onInit(getSelf());
    }

    public void onInit(Object self) {
        //
    }

    @Override
    public final void onRun(Event event) {
        onRun(getSelf(), event);
    }

    public abstract void onRun(Object self, Event event);

    public NashornRule withConditions(String eventAlias, List<ScriptObjectMirror> functions) {
        return (NashornRule) super.withEventCondition(eventAlias, CompositeEventCondition.create(MAPPER, functions));
    }

    public NashornRule withCondition(String eventAlias, ScriptObjectMirror function) {
        return (NashornRule) super.withEventCondition(eventAlias, MAPPER.apply(function));
    }

    public NashornRule withAllConditions(List<ScriptObjectMirror> functions) {
        return (NashornRule) super.withAllEventCondition(CompositeEventCondition.create(MAPPER, functions));
    }

    public NashornRule withAllCondition(ScriptObjectMirror function) {
        return (NashornRule) super.withAllEventCondition(MAPPER.apply(function));
    }
}
