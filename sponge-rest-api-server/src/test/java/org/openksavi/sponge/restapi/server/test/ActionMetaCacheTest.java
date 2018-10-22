/*
 * Copyright 2016-2018 The Sponge authors.
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

package org.openksavi.sponge.restapi.server.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.camel.test.spring.CamelSpringDelegatingTestContextLoader;
import org.apache.camel.test.spring.CamelSpringRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.restapi.RestApiConstants;
import org.openksavi.sponge.restapi.client.BaseSpongeRestApiClient;
import org.openksavi.sponge.restapi.client.RestApiClientConfiguration;
import org.openksavi.sponge.restapi.client.okhttp.OkHttpSpongeRestApiClient;
import org.openksavi.sponge.restapi.model.RestActionMeta;
import org.openksavi.sponge.restapi.server.RestApiServerPlugin;
import org.openksavi.sponge.spring.SpringSpongeEngine;

@net.jcip.annotations.NotThreadSafe
@RunWith(CamelSpringRunner.class)
@ContextConfiguration(classes = { ActionMetaCacheTest.TestConfig.class }, loader = CamelSpringDelegatingTestContextLoader.class)
@DirtiesContext
public class ActionMetaCacheTest {

    @Inject
    protected SpongeEngine engine;

    @Inject
    @Named(PortTestConfig.PORT_BEAN_NAME)
    protected Integer port;

    @Configuration
    public static class TestConfig extends PortTestConfig {

        @Bean
        public SpongeEngine spongeEngine() {
            return SpringSpongeEngine.builder().plugins(camelPlugin(), spongeRestApiPlugin())
                    .knowledgeBase("kb", "examples/rest-api-server/rest_api.py").build();
        }

        @Bean
        public RestApiServerPlugin spongeRestApiPlugin() {
            RestApiServerPlugin plugin = new RestApiServerPlugin();
            plugin.getSettings().setPort(spongeRestApiPort());

            return plugin;
        }
    }

    protected BaseSpongeRestApiClient createRestApiClient(boolean useActionMetaCache) {
        return new OkHttpSpongeRestApiClient(
                RestApiClientConfiguration.builder().url(String.format("http://localhost:%d/%s", port, RestApiConstants.DEFAULT_PATH))
                        .useActionMetaCache(useActionMetaCache).build());
    }

    @Test
    public void testActionCacheOn() {
        BaseSpongeRestApiClient client = createRestApiClient(true);
        String actionName = "UpperCase";

        RestActionMeta actionMeta;

        assertNull(client.getActionMetaCache().getIfPresent(actionName));

        actionMeta = client.getActionMeta(actionName);
        assertNotNull(actionMeta);
        assertNotNull(client.getActionMetaCache().getIfPresent(actionName));

        assertNotNull(client.getActionMeta(actionName));
        assertTrue(actionMeta == client.getActionMeta(actionName));
        assertNotNull(client.getActionMetaCache().getIfPresent(actionName));

        client.clearCache();
        assertNull(client.getActionMetaCache().getIfPresent(actionName));

        assertNotNull(client.getActionMeta(actionName));
        assertNotNull(client.getActionMetaCache().getIfPresent(actionName));
    }

    @Test
    public void testActionCacheOff() {
        BaseSpongeRestApiClient client = createRestApiClient(false);
        String actionName = "UpperCase";

        RestActionMeta actionMeta;

        actionMeta = client.getActionMeta(actionName);
        assertNotNull(actionMeta);

        assertNotNull(client.getActionMeta(actionName));
        assertTrue(actionMeta != client.getActionMeta(actionName));

        client.clearCache();

        assertNotNull(client.getActionMeta(actionName));
    }

    @Test
    public void testActionCacheOnGetActions() {
        BaseSpongeRestApiClient client = createRestApiClient(true);
        String actionName = "UpperCase";

        assertNull(client.getActionMetaCache().getIfPresent(actionName));

        client.getActions();
        assertNotNull(client.getActionMetaCache().getIfPresent(actionName));

        assertNotNull(client.getActionMeta(actionName));

        client.clearCache();
        assertNull(client.getActionMetaCache().getIfPresent(actionName));

        client.getActions();
        assertNotNull(client.getActionMetaCache().getIfPresent(actionName));
    }
}