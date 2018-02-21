/*
 * Copyright 2016 Softelnet.
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

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;

import org.openksavi.sponge.engine.SpongeEngine;

/**
 * A knowledge base file provider.
 */
public interface KnowledgeBaseFileProvider {

    /**
     * Returns a reader for the specified knowledge base file name or {@code null} if there is no such file.
     *
     * @param engine the engine.
     * @param fileName a knowledge base file name.
     * @param charset a knowledge base file charset.
     *
     * @return a reader for the specified knowledge base file name.
     *
     * @throws IOException when IO error.
     */
    Reader getReader(SpongeEngine engine, String fileName, Charset charset) throws IOException;
}
