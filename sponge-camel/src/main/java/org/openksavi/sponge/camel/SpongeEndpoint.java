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

package org.openksavi.sponge.camel;

import org.apache.camel.Component;
import org.apache.camel.Consumer;
import org.apache.camel.Exchange;
import org.apache.camel.MultipleConsumersSupport;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriParam;
import org.apache.camel.spi.UriPath;
import org.apache.camel.support.CamelContextHelper;
import org.apache.camel.support.DefaultEndpoint;

import org.openksavi.sponge.SpongeException;
import org.openksavi.sponge.engine.SpongeEngine;

/**
 * Sponge Camel endpoint.
 */
@UriEndpoint(scheme = "sponge", title = "Sponge", syntax = "sponge:engineRef", consumerClass = SpongeConsumer.class, label = "sponge")
public class SpongeEndpoint extends DefaultEndpoint implements MultipleConsumersSupport {

    public static final Boolean DEFAULT_MANAGED = true;

    private SpongeEngine engine;

    @UriPath
    private String engineRef;

    @UriParam
    private String action;

    @UriParam
    private Boolean managed;

    private boolean autoStarted = false;

    public SpongeEndpoint(String endpointUri, Component component, SpongeEngine engine, String action, Boolean managed) {
        super(endpointUri, component);
        this.engine = engine;
        this.action = action;
        this.managed = managed != null ? managed : DEFAULT_MANAGED;
    }

    @Override
    public Producer createProducer() throws Exception {
        return new SpongeProducer(this, engine, action);
    }

    @Override
    public Consumer createConsumer(Processor processor) throws Exception {
        SpongeConsumer answer = new SpongeConsumer(this, processor, engine);
        configureConsumer(answer);
        return answer;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public boolean isMultipleConsumersSupported() {
        return true;
    }

    public Exchange createExchange(Object body) {
        Exchange exchange = createExchange();
        exchange.getIn().setBody(body);
        return exchange;
    }

    public String getEngineRef() {
        return engineRef;
    }

    /**
     * To lookup the Sponge engine from the registry with the given name.
     *
     * @param engineRef the engine reference in the repository.
     */
    public void setEngineRef(String engineRef) {
        this.engineRef = engineRef;
    }

    public SpongeEngine getEngine() {
        return engine;
    }

    /**
     * To use the given Sponge engine instance.
     *
     * @param engine the engine.
     */
    public void setEngine(SpongeEngine engine) {
        this.engine = engine;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Boolean getManaged() {
        return managed;
    }

    public void setManaged(Boolean managed) {
        this.managed = managed;
    }

    @Override
    protected void doStart() throws Exception {
        super.doStart();

        if (engineRef != null && engine == null) {
            engine = CamelContextHelper.mandatoryLookup(getCamelContext(), engineRef, SpongeEngine.class);
        }

        synchronized (getComponent()) {
            configurePlugin();

            if (managed != null && managed) {
                if (!(engine.isStarting() || engine.isRunning())) {
                    engine.startup();
                    autoStarted = true;
                }
            }

            if (!(engine.isStarting() || engine.isRunning())) {
                throw new SpongeException("Sponge engine is not starting or running");
            }

            configureAction();
        }
    }

    protected void configurePlugin() {
        if (engine.getOperations().hasPlugin(CamelPlugin.class, CamelPlugin.NAME)) {
            engine.getOperations().getPlugin(CamelPlugin.class, CamelPlugin.NAME).setContext(getCamelContext());
        } else {
            engine.getPluginManager().addPlugin(new CamelPlugin(getCamelContext()));
        }
    }

    protected void configureAction() {
        if (!engine.getOperations().hasAction(CamelProducerAction.NAME)) {
            engine.getOperations().enableJava(CamelProducerAction.class);
        }
    }

    @Override
    protected void doStop() throws Exception {
        // Automatic shutdown only if auto started.
        synchronized (getComponent()) {
            if (managed != null && managed) {
                if (autoStarted && engine.isRunning()) {
                    engine.shutdown();
                }
            }
        }

        super.doStop();
    }
}
