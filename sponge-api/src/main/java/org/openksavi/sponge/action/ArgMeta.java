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

package org.openksavi.sponge.action;

import org.openksavi.sponge.type.Type;

/**
 * An action argument metadata.
 */
@SuppressWarnings("rawtypes")
public class ArgMeta<T extends Type> {

    /** The argument name. */
    private String name;

    /** The argument data type. */
    private T type;

    /** The argument display name. */
    private String displayName;

    /** The argument description. */
    private String description;

    /** The flag specifying if this argument is optional. */
    private boolean optional = false;

    public ArgMeta(String name, T type) {
        this.name = name;
        this.type = type;
    }

    public ArgMeta<T> displayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public ArgMeta<T> description(String description) {
        this.description = description;
        return this;
    }

    public ArgMeta<T> optional() {
        this.optional = true;
        return this;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public T getType() {
        return type;
    }

    public void setType(T type) {
        this.type = type;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isOptional() {
        return optional;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    public String getLabel() {
        return displayName != null ? displayName : name;
    }
}
