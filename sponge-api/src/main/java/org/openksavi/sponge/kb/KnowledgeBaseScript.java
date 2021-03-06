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

package org.openksavi.sponge.kb;

/**
 * Knowledge base script.
 */
public interface KnowledgeBaseScript {

    /**
     * Returns a knowledge base that uses this script.
     *
     * @return a knowledge base.
     */
    ScriptKnowledgeBase getKnowledgeBase();

    /**
     * Sets a knowledge base that uses this script.
     *
     * @param knowledgeBase a knowledge base.
     */
    void setKnowledgeBase(ScriptKnowledgeBase knowledgeBase);

    /**
     * Returns the name of the script.
     *
     * @return the name of the script.
     */
    String getName();
}
