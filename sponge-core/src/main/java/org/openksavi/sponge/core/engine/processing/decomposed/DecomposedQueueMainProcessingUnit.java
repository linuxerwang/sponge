/*
 * Copyright 2016-2017 Softelnet.
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

package org.openksavi.sponge.core.engine.processing.decomposed;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import org.openksavi.sponge.EventProcessorAdapter;
import org.openksavi.sponge.ProcessorAdapter;
import org.openksavi.sponge.core.engine.processing.BaseMainProcessingUnit;
import org.openksavi.sponge.core.event.ProcessorControlEvent;
import org.openksavi.sponge.engine.Engine;
import org.openksavi.sponge.engine.ProcessableThreadPool;
import org.openksavi.sponge.engine.ProcessorType;
import org.openksavi.sponge.engine.QueueFullException;
import org.openksavi.sponge.engine.ThreadPool;
import org.openksavi.sponge.engine.event.EventQueue;
import org.openksavi.sponge.event.ControlEvent;
import org.openksavi.sponge.event.Event;
import org.openksavi.sponge.util.Processable;

/**
 * Main processing unit that handles triggers, rules and correlators.
 */
public class DecomposedQueueMainProcessingUnit extends BaseMainProcessingUnit {

    /** The thread pool used by the Main Processing Unit for listening to the Main Event Queue. */
    protected ProcessableThreadPool listenerThreadPool;

    /** The thread pool used by the Main Processing Unit for listening to the decomposed queue. */
    protected ProcessableThreadPool decomposedQueueThreadPool;

    /** The thread pool used by the Main Processing Unit for worker threads. */
    protected ThreadPool workerThreadPool;

    /** The thread pool used by the Main Processing Unit for asynchronous processing of event set processors. */
    protected ThreadPool asyncEventSetProcessorThreadPool;

    /** Decomposed custom queue of entries (trigger adapter or event set processor group adapter, event). */
    private DecomposedQueue<EventProcessorAdapter<?>> decomposedQueue;

    /**
     * Creates a new main processing unit.
     *
     * @param name name.
     * @param engine the engine.
     * @param inQueue input queue.
     * @param outQueue output queue.
     */
    public DecomposedQueueMainProcessingUnit(String name, Engine engine, EventQueue inQueue, EventQueue outQueue) {
        super(name, engine, inQueue, outQueue);

        setDecomposedQueue(new DecomposedQueue<>(engine.getDefaultParameters().getDecomposedQueueCapacity(),
                engine.getDefaultParameters().getAllowConcurrentEventTypeProcessingByEventSetProcessors()));
    }

    public void setDecomposedQueue(DecomposedQueue<EventProcessorAdapter<?>> decomposedQueue) {
        this.decomposedQueue = decomposedQueue;
        setEventProcessorRegistrationListener(decomposedQueue);
    }

    /**
     * Starts up this managed entity.
     */
    @Override
    public void doStartup() {
        startupHandlers();

        asyncEventSetProcessorThreadPool = getEngine().getThreadPoolManager().createMainProcessingUnitAsyncEventSetProcessorThreadPool();
        workerThreadPool = getEngine().getThreadPoolManager().createMainProcessingUnitWorkerThreadPool();

        // One thread for reading from the decomposed queue.
        decomposedQueueThreadPool = getEngine().getThreadPoolManager()
                .createMainProcessingUnitDecomposedQueueThreadPool(new DecomposedQueueReaderProcessable());
        listenerThreadPool = getEngine().getThreadPoolManager().createMainProcessingUnitListenerThreadPool(this);

        getEngine().getThreadPoolManager().startupProcessableThreadPool(decomposedQueueThreadPool);
        getEngine().getThreadPoolManager().startupProcessableThreadPool(listenerThreadPool);
    }

    @Override
    public void doShutdown() {
        getEngine().getThreadPoolManager().shutdownThreadPool(listenerThreadPool, true);
        getEngine().getThreadPoolManager().shutdownThreadPool(decomposedQueueThreadPool, true);

        getEngine().getThreadPoolManager().shutdownThreadPool(workerThreadPool, false);
        getEngine().getThreadPoolManager().shutdownThreadPool(asyncEventSetProcessorThreadPool, false);

        shutdownHandlers();
    }

    /**
     * Creates the worker.
     *
     * @return the worker.
     */
    @Override
    public Runnable createWorker() {
        return new DecomposedQueueWriterLoopWorker(this);
    }

    protected boolean supportsControlEventForProcessor(ProcessorAdapter<?> processorAdapter) {
        ProcessorType type = processorAdapter.getType();

        return type == ProcessorType.TRIGGER || type == ProcessorType.RULE || type == ProcessorType.CORRELATOR
                || type == ProcessorType.RULE_GROUP || type == ProcessorType.CORRELATOR_GROUP;
    }

    /**
     * Processes an event.
     *
     * @param event an event.
     *
     * @return {@code true} if the event has been processed by at least one adapter.
     * @throws java.lang.InterruptedException if interrupted.
     */
    protected boolean processEvent(Event event) throws InterruptedException {
        if (event instanceof ControlEvent) {
            if (event instanceof ProcessorControlEvent) {
                ProcessorAdapter<?> processorAdapter = ((ProcessorControlEvent) event).getProcessorAdapter();
                if (processorAdapter instanceof EventProcessorAdapter && supportsControlEventForProcessor(processorAdapter)) {
                    putIntoDecomposedQueue(new ImmutablePair<>((EventProcessorAdapter<?>) processorAdapter, event));
                }
            }

            return true;
        } else {
            Set<AtomicReference<EventProcessorAdapter<?>>> adapterRs = getEventProcessors(event.getName());
            for (AtomicReference<EventProcessorAdapter<?>> adapterR : adapterRs) {
                putIntoDecomposedQueue(new ImmutablePair<>(adapterR.get(), event));
            }

            return !adapterRs.isEmpty();
        }
    }

    protected void putIntoDecomposedQueue(Pair<EventProcessorAdapter<?>, Event> entry) throws InterruptedException {
        boolean entryPut = false;

        while (!entryPut) {
            try {
                decomposedQueue.put(entry);
                entryPut = true;
            } catch (QueueFullException e) {
                // If decomposed queue is full, than try again after sleep.
                TimeUnit.MILLISECONDS.sleep(getEngine().getDefaultParameters().getInternalQueueBlockingPutSleep());
            }
        }
    }

    protected class DecomposedQueueWriterLoopWorker extends LoopWorker {

        public DecomposedQueueWriterLoopWorker(Processable processable) {
            super(processable);
        }

        @Override
        public boolean runIteration() throws InterruptedException {
            // Get an event from the input queue (blocking operation).
            Event event = getInEvent();

            if (event == null) {
                return false;
            }

            // Add a decomposed entry (trigger adapter or event set processor group adapter, event) to the decomposed queue.
            if (!processEvent(event)) {
                // If the event isn't listened to by any event processor then put the event in the output queue.
                getOutQueue().put(event);
            }

            return true;
        }
    }

    public class DecomposedQueueReaderProcessable implements Processable {

        @Override
        public Runnable createWorker() {
            return new DecomposedQueueReaderWorker(this);
        }

        @Override
        public String toString() {
            return getName() + ".DecomposedQueueReader";
        }
    }

    protected class DecomposedQueueReaderWorker extends LoopWorker {

        public DecomposedQueueReaderWorker(DecomposedQueueReaderProcessable processable) {
            super(processable);
        }

        @Override
        public boolean runIteration() throws InterruptedException {
            try {
                while (isNewOrStartingOrRunning()) {
                    final Pair<EventProcessorAdapter<?>, Event> entry = decomposedQueue.get(GET_ITERATION_TIMEOUT, TimeUnit.MILLISECONDS);
                    if (entry != null) {
                        final EventProcessorAdapter<?> adapter = entry.getLeft();
                        final Event event = entry.getRight();

                        boolean hasBeenRun = false;
                        while (!hasBeenRun) {
                            try {
                                // Process an event by the adapter asynchronously in a thread from a thread pool.
                                CompletableFuture.runAsync(() -> getHandler(adapter.getType()).processEvent(adapter, event),
                                        workerThreadPool.getExecutor()).handle((result, exception) -> {
                                            decomposedQueue.release(entry);
                                            return null;
                                        });
                                hasBeenRun = true;
                            } catch (RejectedExecutionException e) {
                                // If rejected because of the lack of free threads, than try again after sleep.
                                TimeUnit.MILLISECONDS.sleep(getEngine().getDefaultParameters().getInternalQueueBlockingPutSleep());
                            }
                        }

                        return true;
                    }
                }

                return false;
            } catch (InterruptedException e) {
                throw e;
            } catch (Exception e) {
                Throwable rootCause = ExceptionUtils.getRootCause(e);
                if (rootCause != null && rootCause instanceof InterruptedException) {
                    throw (InterruptedException) rootCause;
                }

                getEngine().handleError("runIteration", e);
                return true;
            }
        }
    }

    @Override
    public Executor getAsyncEventSetProcessorExecutor() {
        return asyncEventSetProcessorThreadPool.getExecutor();
    }

    @Override
    public boolean supportsConcurrentListenerThreadPool() {
        return false;
    }
}
