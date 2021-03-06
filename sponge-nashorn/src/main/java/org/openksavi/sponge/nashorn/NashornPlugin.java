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

import org.openksavi.sponge.config.Configuration;
import org.openksavi.sponge.core.plugin.BasePlugin;

/**
 * JavaScript-specific implementation of the plugin.
 */
public abstract class NashornPlugin extends BasePlugin implements NashornScriptObject {

    /** JavaScript object to overcome class inheritance limitations in JavaScript and Nashorn. Must be be thread safe. */
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
    public final void onConfigure(Configuration configuration) {
        onConfigure(getSelf(), configuration);
    }

    public void onConfigure(Object self, Configuration configuration) {
        //
    }

    @Override
    public final void onInit() {
        onInit(getSelf());
    }

    public void onInit(Object self) {
        //
    }

    @Override
    public final void onStartup() {
        onStartup(getSelf());
    }

    public void onStartup(Object self) {
        //
    }

    @Override
    public final void onShutdown() {
        onShutdown(getSelf());
    }

    public void onShutdown(Object self) {
        //
    }

    @Override
    public final void onBeforeReload() {
        onBeforeReload(getSelf());
    }

    public void onBeforeReload(Object self) {
        //
    }

    @Override
    public final void onAfterReload() {
        onAfterReload(getSelf());
    }

    public void onAfterReload(Object self) {
        //
    }
}
