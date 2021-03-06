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

package org.openksavi.sponge.core.kb;

import java.util.ArrayList;
import java.util.List;

import org.openksavi.sponge.kb.KnowledgeBaseType;

/**
 * Generic knowledge base type.
 */
public class GenericKnowledgeBaseType implements KnowledgeBaseType {

    private String typeCode;

    private String language;

    private List<String> fileExtensions;

    private boolean isScript;

    public GenericKnowledgeBaseType(String typeCode, String language, List<String> fileExtensions, boolean isScript) {
        this.typeCode = typeCode;
        this.language = language;
        this.fileExtensions = new ArrayList<>(fileExtensions);
        this.isScript = isScript;
    }

    @Override
    public String getTypeCode() {
        return typeCode;
    }

    @Override
    public String getLanguage() {
        return language;
    }

    @Override
    public List<String> getFileExtensions() {
        return fileExtensions;
    }

    @Override
    public boolean isScript() {
        return isScript;
    }

    @Override
    public String toString() {
        return typeCode;
    }
}
