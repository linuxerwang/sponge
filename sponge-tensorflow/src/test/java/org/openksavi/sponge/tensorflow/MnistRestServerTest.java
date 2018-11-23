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

package org.openksavi.sponge.tensorflow;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.inject.Inject;

import org.apache.camel.test.spring.CamelSpringDelegatingTestContextLoader;
import org.apache.camel.test.spring.CamelSpringRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.util.SocketUtils;

import org.openksavi.sponge.camel.SpongeCamelConfiguration;
import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.restapi.RestApiConstants;
import org.openksavi.sponge.restapi.client.DefaultSpongeRestClient;
import org.openksavi.sponge.restapi.client.SpongeRestClientConfiguration;
import org.openksavi.sponge.restapi.client.SpongeRestClient;
import org.openksavi.sponge.restapi.server.RestApiServerPlugin;
import org.openksavi.sponge.spring.SpringSpongeEngine;

@net.jcip.annotations.NotThreadSafe
@RunWith(CamelSpringRunner.class)
@ContextConfiguration(classes = { MnistRestServerTest.TestConfig.class }, loader = CamelSpringDelegatingTestContextLoader.class)
@DirtiesContext
public class MnistRestServerTest {

    protected static final int PORT = SocketUtils.findAvailableTcpPort(RestApiConstants.DEFAULT_PORT);

    @Inject
    protected SpongeEngine engine;

    @Configuration
    public static class TestConfig extends SpongeCamelConfiguration {

        @Bean
        public SpongeEngine spongeEngine() {
            return SpringSpongeEngine.builder().plugins(camelPlugin(), spongeRestApiPlugin())
                    .config("examples/tensorflow/mnist/mnist_rest_server_test.xml").build();
        }

        @Bean
        public RestApiServerPlugin spongeRestApiPlugin() {
            RestApiServerPlugin plugin = new RestApiServerPlugin();
            plugin.getSettings().setPort(PORT);

            return plugin;
        }
    }

    protected SpongeRestClient createRestApiClient() {
        return new DefaultSpongeRestClient(SpongeRestClientConfiguration.builder()
                .url(String.format("http://localhost:%d/%s", PORT, RestApiConstants.DEFAULT_PATH)).build());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testRestCallPredict() {
        byte[] imageData = SpongeUtils.readFileToByteArray("examples/tensorflow/mnist/data/5_0.png");

        try (SpongeRestClient client = createRestApiClient()) {
            List<Number> result = client.call(List.class, "MnistPredictDetailed", imageData);
            List<Double> predictions = result.stream().map(e -> e.doubleValue()).collect(Collectors.toList());

            int prediction = IntStream.range(0, predictions.size()).boxed().max(Comparator.comparingDouble(predictions::get)).get();
            assertEquals(5, prediction);
            assertFalse(engine.isError());
        }
    }
}
