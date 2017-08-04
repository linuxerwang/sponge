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

package org.openksavi.sponge.examples.project.news;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openksavi.sponge.config.Configuration;
import org.openksavi.sponge.java.JavaPlugin;

/**
 * Java-based plugin.
 */
public class MultiEchoPlugin extends JavaPlugin {

    private static final Logger logger = LoggerFactory.getLogger(MultiEchoPlugin.class);

    private int count = 1;

    public MultiEchoPlugin() {
    }

    @Override
    public void configure(Configuration configuration) {
        count = configuration.getInteger("count", count);
    }

    @Override
    public void init() {
        logger.debug("Initializing {}", getName());
    }

    @Override
    public void onStartup() {
        logger.debug("Starting up {}", getName());
    }

    public String echo(String text) {
        return StringUtils.repeat(text, ", repeat: ", count).toUpperCase();
    }

    public int getCount() {
        return count;
    }
}
