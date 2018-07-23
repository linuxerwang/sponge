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

package org.openksavi.sponge.type;

/**
 * A type that is a result of the given action, e.g. a list of string values. It allows using a result of one action to be a type for
 * another action argument or result.
 */
public class ActionType extends Type {

    private String actionName;

    protected ActionType() {
        this(null);
    }

    public ActionType(String actionName) {
        super(TypeKind.ACTION);

        this.actionName = actionName;
    }

    @Override
    public ActionType format(String format) {
        return (ActionType) super.format(format);
    }

    @Override
    public ActionType tags(String... tags) {
        return (ActionType) super.tags(tags);
    }

    public String getActionName() {
        return actionName;
    }
}
