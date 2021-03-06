/* Copyright 2016-2017 The Sponge authors.
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

package org.openksavi.sponge.test.util;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.SocketUtils;

import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.event.Event;
import org.openksavi.sponge.examples.util.CorrelationEventsLog;

public class TestUtils {

    private static final Logger logger = LoggerFactory.getLogger(TestUtils.class);

    public static final String DEFAULT_KB = "kb";

    @SuppressWarnings("unchecked")
    public static List<Event> getEvents(SpongeEngine engine, String eventName) {
        Map<String, Object> events = engine.getOperations().getVariable(Map.class, "events");
        return (List<Event>) events.get(eventName);
    }

    @SuppressWarnings("unchecked")
    public static int getEventCounter(SpongeEngine engine, String key) {
        return ((Map<String, Number>) engine.getOperations().getVariable("eventCounter")).get(key).intValue();
    }

    public static void assertEventSequences(CorrelationEventsLog eventsLog, String key, String firstEventLabel, String[][] expected) {
        assertEventSequences(eventsLog, key, firstEventLabel, expected, false);
    }

    public static void assertEventSequences(CorrelationEventsLog eventsLog, String key, String firstEventLabel, String[][] expected,
            boolean ignoreOrderOfSequences) {
        List<List<Event>> lists = eventsLog.getEvents(key, firstEventLabel);
        assertEquals(expected.length, lists.size());

        Object expectedSequenceReport = null;
        Object realSequenceReport = null;

        try {
            if (ignoreOrderOfSequences) {
                // The order of sequences doesn't matter, however the order inside a sequence is important.
                Set<List<String>> expectedSet =
                        Stream.of(expected).map(sequence -> Arrays.asList(sequence)).collect(Collectors.toCollection(LinkedHashSet::new));
                Set<List<String>> realSet = new LinkedHashSet<>();
                lists.forEach(list -> realSet.add(list.stream()
                        .map(event -> event != null ? event.get(String.class, CorrelationEventsLog.LABEL_ATTRIBUTE_NAME) : null)
                        .collect(Collectors.toCollection(ArrayList::new))));
                expectedSequenceReport = expectedSet;
                realSequenceReport = realSet;
                assertEquals(expectedSet, realSet);
            } else {
                for (int i = 0; i < expected.length; i++) {
                    String[] expectedSequence = expected[i];
                    String[] realSequence = lists.get(i).stream()
                            .map(event -> event != null ? event.get(String.class, CorrelationEventsLog.LABEL_ATTRIBUTE_NAME) : null)
                            .toArray(String[]::new);
                    expectedSequenceReport = Arrays.asList(expectedSequence);
                    realSequenceReport = Arrays.asList(realSequence);
                    assertArrayEquals(expectedSequence, realSequence);
                }
            }
        } catch (AssertionError e) {
            logger.error("ERROR key=" + key + ", expcted=" + expectedSequenceReport + ", real=" + realSequenceReport, e);
            throw e;
        }
    }

    /**
     * Finds two available neighbouring ports.
     *
     * @return the base available port. The next port (i.e. base port + 1) is available too.
     */
    public static int findAvailablePairOfNeighbouringTcpPorts() {
        return findAvailablePairOfNeighbouringTcpPorts(20000, 60000, 1000);
    }

    public static int findAvailablePairOfNeighbouringTcpPorts(int minPort, int maxPort, int maxAttempts) {
        for (int i = 0; i < maxAttempts; i++) {
            int basePort = SocketUtils.findAvailableTcpPort(minPort, maxPort);
            int neighbourPort = -1;
            try {
                neighbourPort = SocketUtils.findAvailableTcpPort(basePort + 1, basePort + 1);
            } catch (IllegalStateException e) {
                // Ignore.
            }

            if (neighbourPort == basePort + 1) {
                return basePort;
            }
        }

        throw new IllegalStateException("No two available neighbouring ports found");
    }
}
