/*
 * Sponge Knowledge base
 * Using rules - events
 */

package org.openksavi.sponge.kotlin.examples

import org.openksavi.sponge.event.Event
import org.openksavi.sponge.kotlin.KKnowledgeBase
import org.openksavi.sponge.kotlin.KRule
import org.openksavi.sponge.test.util.CorrelationEventsLog
import java.time.Duration

class RulesNoneModeEvents : KKnowledgeBase() {

    companion object {
        val correlationEventsLog = CorrelationEventsLog()
    }

    override fun onInit() {
        // Variables for assertions only
        eps.setVariable("correlationEventsLog", correlationEventsLog)
    }

    // Naming F(irst), L(ast), A(ll), N(one)

    class RuleFNNF : KRule() {
        override fun onConfigure() = setEvents("e1", "e5 :none", "e6 :none", "e3")

        override fun onRun(event: Event?) {
            logger.debug("Running rule for events: {}", eventAliasMap)
            correlationEventsLog.addEvents(name, this)
        }
    }

    class RuleFNNNL : KRule() {
        override fun onConfigure() {
            setEvents("e1", "e5 :none", "e6 :none", "e7 :none", "e3 :last")
            duration = Duration.ofSeconds(2)
        }

        override fun onRun(event: Event?) {
            logger.debug("Running rule for events: {}", eventAliasMap)
            correlationEventsLog.addEvents(name, this)
        }
    }

    class RuleFNNNLReject : KRule() {
        override fun onConfigure() {
            setEvents("e1", "e5 :none", "e2 :none", "e7 :none", "e3 :last")
            duration = Duration.ofSeconds(2)
        }

        override fun onRun(event: Event?) {
            logger.debug("Running rule for events: {}", eventAliasMap)
            correlationEventsLog.addEvents(name, this)
        }
    }

    class RuleFNFNL : KRule() {
        override fun onConfigure() {
            setEvents("e1", "e5 :none", "e2", "e7 :none", "e3 :last")
            duration = Duration.ofSeconds(2)
        }

        override fun onRun(event: Event?) {
            logger.debug("Running rule for events: {}", eventAliasMap)
            correlationEventsLog.addEvents(name, this)
        }
    }

    override fun onStartup() {
        eps.event("e1").set("label", "1").send()
        eps.event("e2").set("label", "2").send()
        eps.event("e2").set("label", "3").send()
        eps.event("e2").set("label", "4").send()
        eps.event("e3").set("label", "5").send()
        eps.event("e3").set("label", "6").send()
        eps.event("e3").set("label", "7").send()
    }
}