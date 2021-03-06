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

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.spring.CamelSpringDelegatingTestContextLoader;
import org.apache.camel.test.spring.CamelSpringRunner;
import org.apache.camel.test.spring.MockEndpoints;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

@RunWith(CamelSpringRunner.class)
@ContextConfiguration(classes = { CamelProducerCustomUriActionTest.TestConfig.class },
        loader = CamelSpringDelegatingTestContextLoader.class)
@MockEndpoints
@DirtiesContext
public class CamelProducerCustomUriActionTest extends CamelProducerCustomActionAbstractTest {

    @Configuration
    public static class TestConfig extends AbstractTestConfig {

        @Bean
        @Override
        public RouteBuilder route() {
            return new RouteBuilder() {

                @Override
                public void configure() throws Exception {
                    // @formatter:off
                    from("direct:start").routeId("spongeProducer")
                            .to("sponge:spongeEngine?action=CustomAction&managed=false")
                            .log("Action result as a body: ${body}")
                            .to("direct:log");

                    from("direct:log").routeId("directLog")
                            .log("Action result as a body: ${body}");
                    // @formatter:on
                }
            };
        }
    }

    @Override
    @Test
    public void testRoute() throws InterruptedException {
        super.testRoute();
    }
}
