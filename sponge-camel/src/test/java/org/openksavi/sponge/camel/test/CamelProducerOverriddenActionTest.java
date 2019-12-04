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

package org.openksavi.sponge.camel.test;

import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertFalse;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.spi.Registry;
import org.apache.camel.support.DefaultRegistry;
import org.junit.Test;

import org.openksavi.sponge.core.engine.DefaultSpongeEngine;
import org.openksavi.sponge.engine.SpongeEngine;

public class CamelProducerOverriddenActionTest {

    @Test
    public void testCamelProducerOverridenAction() throws Exception {
        Registry registry = new DefaultRegistry();
        SpongeEngine engine =
                DefaultSpongeEngine.builder().knowledgeBase("camelkb", "examples/camel/camel_producer_overridden_action.py").build();
        registry.bind("spongeEngine", engine);
        try (CamelContext camel = new DefaultCamelContext(registry)) {
            camel.addRoutes(new RouteBuilder() {

                @Override
                public void configure() {
                // @formatter:off
                from("direct:start").routeId("spongeProducer")
                    .to("sponge:spongeEngine");
                // @formatter:on
                }
            });

            camel.start();

            ProducerTemplate producerTemplate = camel.createProducerTemplate();
            producerTemplate.sendBody("direct:start", "Send me to the Sponge");

            await().pollDelay(2, TimeUnit.SECONDS).atMost(60, TimeUnit.SECONDS)
                    .until(() -> engine.getOperations().getVariable(AtomicBoolean.class, "sentCamelMessage_camelEvent").get());

            assertFalse(engine.getOperations().getVariable(AtomicBoolean.class, "sentCamelMessage_spongeProducer").get());
            assertFalse(engine.isError());
        }
    }
}
