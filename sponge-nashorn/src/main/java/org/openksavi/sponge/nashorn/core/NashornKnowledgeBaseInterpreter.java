/*
 * Copyright 2016-2017 Softelnet.
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

package org.openksavi.sponge.nashorn.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;

import com.google.common.collect.ImmutableMap;

import jdk.nashorn.api.scripting.NashornScriptEngineFactory;

import org.apache.commons.lang3.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openksavi.sponge.SpongeException;
import org.openksavi.sponge.action.Action;
import org.openksavi.sponge.core.engine.BaseEngine;
import org.openksavi.sponge.core.kb.EngineScriptKnowledgeBaseInterpreter;
import org.openksavi.sponge.correlator.Correlator;
import org.openksavi.sponge.engine.Engine;
import org.openksavi.sponge.filter.Filter;
import org.openksavi.sponge.kb.KnowledgeBase;
import org.openksavi.sponge.kb.KnowledgeBaseConstants;
import org.openksavi.sponge.kb.ScriptKnowledgeBaseInterpreter;
import org.openksavi.sponge.nashorn.JavaScriptConstants;
import org.openksavi.sponge.nashorn.NashornAction;
import org.openksavi.sponge.nashorn.NashornCorrelator;
import org.openksavi.sponge.nashorn.NashornFilter;
import org.openksavi.sponge.nashorn.NashornPlugin;
import org.openksavi.sponge.nashorn.NashornRule;
import org.openksavi.sponge.nashorn.NashornTrigger;
import org.openksavi.sponge.plugin.Plugin;
import org.openksavi.sponge.rule.Rule;
import org.openksavi.sponge.trigger.Trigger;

/**
 * Knowledge base interpreter supporting knowledge base to be defined in the Jython (Python) language.
 */
@SuppressWarnings("restriction")
public class NashornKnowledgeBaseInterpreter extends EngineScriptKnowledgeBaseInterpreter {

    private static final Logger logger = LoggerFactory.getLogger(NashornKnowledgeBaseInterpreter.class);

    public static final String SCRIPT_ENGINE_NAME = "nashorn";

    public static final String INITIAL_SCRIPT = "sponge_nashorn_init.js";

    @SuppressWarnings("rawtypes")
    //@formatter:off
    protected static final Map<Class, Class> PROCESSOR_CLASSES = ImmutableMap.of(
            Action.class, NashornAction.class,
            Filter.class, NashornFilter.class,
            Trigger.class, NashornTrigger.class,
            Rule.class, NashornRule.class,
            Correlator.class, NashornCorrelator.class
            );
    //@formatter:on

    public NashornKnowledgeBaseInterpreter(Engine engine, KnowledgeBase knowledgeBase) {
        super(new NashornKnowledgeBaseEngineOperations((BaseEngine) engine, knowledgeBase), JavaScriptConstants.TYPE);
    }

    @Override
    protected ScriptEngine createScriptEngine() {
        String scripEngineName = SCRIPT_ENGINE_NAME;
        // ScriptEngine result = new ScriptEngineManager().getEngineByName(scripEngineName);
        NashornScriptEngineFactory factory = new NashornScriptEngineFactory();
        ScriptEngine result = factory.getScriptEngine("-scripting");

        if (!(result instanceof Invocable)) {
            throw new SpongeException("ScriptingEngine " + scripEngineName + " doesn't implement Invocable");
        }

        PROCESSOR_CLASSES.forEach((interfaceClass, scriptClass) -> addImport(result, scriptClass, interfaceClass.getSimpleName()));
        addImport(result, NashornPlugin.class, Plugin.class.getSimpleName());

        getStandardImportClasses().forEach(cls -> addImport(result, cls));

        result.put(KnowledgeBaseConstants.VAR_ENGINE_OPERATIONS, getEngineOperations());

        eval(result, "load(\"classpath:" + INITIAL_SCRIPT + "\");");

        return result;
    }

    /**
     * Adds import from the package.
     *
     * @param scriptEngine scripting engine.
     * @param clazz class to be imported.
     */
    protected void addImport(ScriptEngine scriptEngine, Class<?> clazz) {
        addImport(scriptEngine, clazz, clazz.getSimpleName());
    }

    protected void addImport(ScriptEngine scriptEngine, Class<?> clazz, String alias) {
        eval(scriptEngine, "var " + alias + " = Packages." + clazz.getName() + ";");
    }

    @Override
    protected <T> T doCreateInstance(String className, Class<T> javaClass) {
        return eval("new " + className + "();");
    }

    /**
     * Returns {@code null} if not script-based processor.
     */
    @Override
    public String getScriptKnowledgeBaseProcessorClassName(Object processorClass) {
        if (processorClass instanceof String) {
            return (String) processorClass;
        }

        return null;
    }

    @Override
    protected ScriptKnowledgeBaseInterpreter createInstance(Engine engine, KnowledgeBase knowledgeBase) {
        return new NashornKnowledgeBaseInterpreter(engine, knowledgeBase);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void scanToAutoEnable() {
        List<Object> autoEnabled = new ArrayList<>();
        getScriptEngine().getBindings(ScriptContext.ENGINE_SCOPE).forEach((key, value) -> {
            Object evalResult = value != null ? eval(key + ".class") : null;

            if (evalResult != null && evalResult instanceof Class) {
                Class cls = (Class) evalResult;

                if (PROCESSOR_CLASSES.values().stream()
                        .filter(processorClass -> !cls.equals(processorClass) && ClassUtils.isAssignable(cls, processorClass)).findFirst()
                        .isPresent()) {
                    autoEnabled.add(key);
                    ((NashornKnowledgeBaseEngineOperations) getEngineOperations()).enable(value);
                }
            }
        });

        if (logger.isDebugEnabled() && !autoEnabled.isEmpty()) {
            logger.debug("Auto-enabling: {}", autoEnabled);
        }
    }
}
