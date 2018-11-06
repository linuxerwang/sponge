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

/**
 * Event processor. Each event name may be specified as a regular expression thus creating a pattern matching more event names.
 */
public interface EventProcessor<T extends EventProcessorAdapter<?>> extends Processor<T>, EventProcessorOperations {

    /**
     * Sets event names (or name patterns) this processor listens to.
     *
     * @param eventNames event names.
     */
    void setEventNames(String... eventNames);

    /**
     * A convenient method for setting event names (or name patterns) that this event processor will be listening to.
     *
     * @param eventNames the event names.
     */
    void setEvents(String... eventNames);

    /**
     * A convenient method for setting event name (or name pattern) that this event processor will be listening to.
     *
     * @param eventName the event name.
     */
    void setEvent(String eventName);
}
