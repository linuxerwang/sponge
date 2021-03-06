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

package org.openksavi.sponge.core.engine.processing;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.openksavi.sponge.EventSetProcessorAdapter;
import org.openksavi.sponge.EventSetProcessorAdapterGroup;
import org.openksavi.sponge.ProcessorAdapter;
import org.openksavi.sponge.engine.ProcessorType;
import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.engine.ThreadPool;
import org.openksavi.sponge.engine.processing.EventSetProcessorMainProcessingUnitHandler;
import org.openksavi.sponge.event.Event;

public abstract class BaseEventSetProcessorMainProcessingUnitHandler<G extends EventSetProcessorAdapterGroup<T>,
        T extends EventSetProcessorAdapter<?>> extends BaseMainProcessingUnitHandler
        implements EventSetProcessorMainProcessingUnitHandler<G, T> {

    private ThreadPool durationThreadPool;

    private Map<T, EventSetProcessorDurationTask<T>> durationTasks = Collections.synchronizedMap(new WeakHashMap<>());

    private Lock lock = new ReentrantLock(true);

    protected BaseEventSetProcessorMainProcessingUnitHandler(ProcessorType type, BaseMainProcessingUnit processingUnit) {
        super(type, processingUnit);
    }

    @Override
    public void startup() {
        lock.lock();
        try {
            durationThreadPool =
                    getProcessingUnit().getEngine().getThreadPoolManager().createMainProcessingUnitEventSetProcessorDurationThreadPool();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void shutdown() {
        lock.lock();
        try {
            if (durationThreadPool != null) {
                durationTasks.values().stream().filter(task -> task.getFuture() != null).forEach(task -> task.getFuture().cancel(false));
                getProcessingUnit().getEngine().getThreadPoolManager().shutdownThreadPool(durationThreadPool);

                durationThreadPool = null;
            }
        } finally {
            lock.unlock();
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void processEvent(ProcessorAdapter<?> adapter, Event event) {
        ((EventSetProcessorAdapterGroup<EventSetProcessorAdapter>) adapter).processEvent(event);
    }

    @Override
    public void addDuration(T adapter) {
        lock.lock();
        try {
            if (adapter.getMeta().hasDuration()) {
                EventSetProcessorDurationTask<T> task = new EventSetProcessorDurationTask<>(adapter);
                durationTasks.put(adapter, task);
                ScheduledFuture<?> future = ((ScheduledExecutorService) durationThreadPool.getExecutor()).schedule(task,
                        adapter.getMeta().getDuration().toMillis(), TimeUnit.MILLISECONDS);
                task.setFuture(future);
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void removeDuration(T adapter) {
        lock.lock();
        try {
            if (adapter.getMeta().hasDuration()) {
                EventSetProcessorDurationTask<T> task = durationTasks.get(adapter);
                if (task != null) {
                    task.getFuture().cancel(true);
                    durationTasks.remove(adapter);
                }
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void removeDurations(G adapterGroup) {
        lock.lock();
        try {
            adapterGroup.getEventSetProcessorAdapters().forEach(adapter -> removeDuration(adapter));
        } finally {
            lock.unlock();
        }
    }

    @Override
    public ThreadPool getAsyncEventSetProcessorThreadPool() {
        return getProcessingUnit().getAsyncEventSetProcessorThreadPool();
    }

    public static class EventSetProcessorDurationTask<T extends EventSetProcessorAdapter<?>> implements Runnable {

        private T adapter;

        private ScheduledFuture<?> future;

        public EventSetProcessorDurationTask(T adapter) {
            this.adapter = adapter;
        }

        public T getAdapter() {
            return adapter;
        }

        public void setAdapter(T adapter) {
            this.adapter = adapter;
        }

        public ScheduledFuture<?> getFuture() {
            return future;
        }

        public void setFuture(ScheduledFuture<?> future) {
            this.future = future;
        }

        @Override
        public void run() {
            SpongeEngine engine = adapter.getKnowledgeBase().getEngineOperations().getEngine();

            try {
                engine.getEventSetProcessorDurationStrategy().durationOccurred(adapter);
            } catch (Throwable e) {
                engine.handleError(adapter, e);
            }
        }
    }
}
