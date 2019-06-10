/*
 * Copyright 2016-2019 The Sponge authors.
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

package org.openksavi.sponge.grpcapi.server.support.kb;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.openksavi.sponge.action.ProvideArgsContext;
import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.java.JAction;
import org.openksavi.sponge.type.DynamicType;
import org.openksavi.sponge.type.RecordType;
import org.openksavi.sponge.type.StringType;
import org.openksavi.sponge.type.provided.ProvidedMeta;
import org.openksavi.sponge.type.provided.ProvidedValue;
import org.openksavi.sponge.type.value.AnnotatedValue;
import org.openksavi.sponge.type.value.DynamicValue;

public class GrpcApiSendEvent extends JAction {

    @Override
    public void onConfigure() {
        withLabel("Send event").withDescription("Sends a new event.");
        withArgs(
                new StringType("name").withLabel("Event type").withDescription("Event type.")
                        .withProvided(new ProvidedMeta().withValueSet()),
                new DynamicType("attributes").withLabel("Attributes").withDescription("Event attributes.")
                        .withProvided(new ProvidedMeta().withValue().withDependency("name")));
        withNoResult();
        withFeatures(SpongeUtils.immutableMapOf("callLabel", "Send", "icon", "send"));
    }

    public void onCall(String name, DynamicValue<Map<String, Object>> attributes) {
        getSponge().event(name).set(attributes.getValue()).send();
    }

    @Override
    public void onProvideArgs(ProvideArgsContext context) {
        if (context.getNames().contains("name")) {
            List<AnnotatedValue<String>> annotatedValueSet = getSponge().getEventTypes().entrySet().stream()
                    .map(entry -> new AnnotatedValue<>(entry.getKey())
                            .withLabel(entry.getValue().getLabel() != null ? entry.getValue().getLabel() : entry.getKey()))
                    .collect(Collectors.toList());
            context.getProvided().put("name", new ProvidedValue<String>().withAnnotatedValueSet(annotatedValueSet));
        }

        if (context.getNames().contains("attributes")) {
            RecordType eventType = getSponge().getEventType((String) context.getCurrent().get("name"));
            Map<String, Object> attributes = new LinkedHashMap<>();
            attributes.put("person", Collections.emptyMap());
            context.getProvided().put("attributes", new ProvidedValue<>().withValue(new DynamicValue<>(attributes, eventType)));
        }
    }
}