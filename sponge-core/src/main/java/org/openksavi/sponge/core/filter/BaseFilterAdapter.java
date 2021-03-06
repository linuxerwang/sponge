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

package org.openksavi.sponge.core.filter;

import org.openksavi.sponge.core.BaseEventProcessorAdapter;
import org.openksavi.sponge.core.BaseProcessorDefinition;
import org.openksavi.sponge.engine.ProcessorType;
import org.openksavi.sponge.filter.Filter;
import org.openksavi.sponge.filter.FilterAdapter;

/**
 * Filer adapter.
 */
public class BaseFilterAdapter extends BaseEventProcessorAdapter<Filter> implements FilterAdapter {

    public BaseFilterAdapter(BaseProcessorDefinition definition) {
        super(definition);
    }

    @Override
    public ProcessorType getType() {
        return ProcessorType.FILTER;
    }

    @Override
    public BaseFilterMeta getMeta() {
        return (BaseFilterMeta) super.getMeta();
    }
}
