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

package org.openksavi.sponge.kotlin;

import org.openksavi.sponge.core.filter.BaseFilter;
import org.openksavi.sponge.kotlin.core.KotlinKnowledgeBaseEngineOperations;

/**
 * Kotlin-specific implementation of the filter.
 */
public abstract class KFilter extends BaseFilter {

    /**
     * Method required for accessing EPS in Kotlin-based processors.
     *
     * @return EPS.
     */
    @Override
    public final KotlinKnowledgeBaseEngineOperations getEps() {
        return (KotlinKnowledgeBaseEngineOperations) super.getEps();
    }
}