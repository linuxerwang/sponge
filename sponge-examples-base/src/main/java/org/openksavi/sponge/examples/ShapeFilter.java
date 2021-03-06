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

package org.openksavi.sponge.examples;

import org.openksavi.sponge.event.Event;
import org.openksavi.sponge.java.JFilter;

public class ShapeFilter extends JFilter {

    @Override
    public void onConfigure() {
        withEvents("e1", "e2", "e3");
    }

    @Override
    public boolean onAccept(Event event) {
        String shape = event.get("shape", null);
        if (shape == null) {
            getLogger().debug("No shape for event: {}; event rejected", event);
            return false;
        }

        getLogger().debug("Shape is set in event: {}; event accepted", event);

        return true;
    }
}
