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

import org.openksavi.sponge.core.BaseEventProcessorDefinition;
import org.openksavi.sponge.filter.FilterDefinition;

public class BaseFilterDefinition extends BaseEventProcessorDefinition implements FilterDefinition {

    public BaseFilterDefinition() {
        super(new BaseFilterMeta());
    }

    @Override
    public BaseFilterMeta getMeta() {
        return (BaseFilterMeta) super.getMeta();
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
