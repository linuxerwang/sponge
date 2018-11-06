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

package org.openksavi.sponge;

import java.util.List;

/**
 * Event processor operations. Each event name may be specified as a regular expression thus creating a pattern matching more event names.
 *
 */
public interface EventProcessorOperations extends ProcessorOperations {

    /**
     * Returns event names (or name patterns) this processor listens to.
     *
     * @return event names.
     */
    List<String> getEventNames();

    /**
     * Returns an event name (or name pattern) for the specified event index.
     *
     * @param index event index.
     *
     * @return event name.
     */
    String getEventName(int index);

    /**
     * Sets event names (or name patterns) this processor listens to.
     *
     * @param eventNames event names.
     */
    void setEventNames(List<String> eventNames);

    /**
     * Sets event name (or name pattern) this processor listens to.
     *
     * @param eventName event name.
     */
    void setEventName(String eventName);
}
