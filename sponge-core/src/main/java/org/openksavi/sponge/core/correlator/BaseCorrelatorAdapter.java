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

package org.openksavi.sponge.core.correlator;

import java.util.concurrent.locks.Lock;

import org.openksavi.sponge.core.BaseEventSetProcessorAdapter;
import org.openksavi.sponge.core.BaseEventSetProcessorDefinition;
import org.openksavi.sponge.correlator.Correlator;
import org.openksavi.sponge.correlator.CorrelatorAdapter;
import org.openksavi.sponge.engine.ProcessorType;
import org.openksavi.sponge.event.Event;

/**
 * Base correlator adapter.
 */
public class BaseCorrelatorAdapter extends BaseEventSetProcessorAdapter<Correlator> implements CorrelatorAdapter {

    private Event firstEvent;

    public BaseCorrelatorAdapter(BaseEventSetProcessorDefinition definition) {
        super(definition);
    }

    @Override
    public ProcessorType getType() {
        return ProcessorType.CORRELATOR;
    }

    @Override
    public BaseCorrelatorDefinition getDefinition() {
        return (BaseCorrelatorDefinition) super.getDefinition();
    }

    @Override
    public BaseCorrelatorMeta getMeta() {
        return (BaseCorrelatorMeta) super.getMeta();
    }

    @Override
    protected void onDuration() {
        getProcessor().onDuration();
    }

    @Override
    public boolean acceptAsFirst(Event event) {
        boolean accepted = getProcessor().onAcceptAsFirst(event);

        if (accepted) {
            firstEvent = event;
        }

        return accepted;
    }

    @Override
    protected void onEvent(Event event) {
        boolean instanceSynchronous = getMeta().isInstanceSynchronous();

        if (instanceSynchronous) {
            lock.lock();
        }
        try {
            getProcessor().onEvent(event);
        } finally {
            if (instanceSynchronous) {
                lock.unlock();
            }
        }
    }

    @Override
    public boolean isCandidateForFirstEvent(Event event) {
        return true;
    }

    @Override
    public Event getFirstEvent() {
        return firstEvent;
    }

    public final Lock getLock() {
        return lock;
    }
}
