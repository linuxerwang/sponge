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

package org.openksavi.sponge.core;

import java.util.Arrays;
import java.util.List;

import org.openksavi.sponge.EventProcessor;
import org.openksavi.sponge.EventProcessorAdapter;

public abstract class BaseEventProcessor<T extends EventProcessorAdapter<?>> extends BaseProcessor<T> implements EventProcessor<T> {

    @Override
    public final List<String> getEventNames() {
        return getAdapter().getEventNames();
    }

    @Override
    public final String getEventName(int index) {
        return getAdapter().getEventName(index);
    }

    @Override
    public final void setEventNames(List<String> eventNames) {
        getAdapter().setEventNames(eventNames);
    }

    @Override
    public final void setEventNames(String... eventNames) {
        setEventNames(Arrays.asList(eventNames));
    }

    @Override
    public final void setEventName(String eventName) {
        getAdapter().setEventName(eventName);
    }

    @Override
    public void setEvents(String... eventNames) {
        setEventNames(eventNames);
    }

    @Override
    public void setEvent(String eventName) {
        setEventName(eventName);
    }
}
