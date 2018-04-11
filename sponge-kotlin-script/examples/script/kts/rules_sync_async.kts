/*
 * Sponge Knowledge base
 * Using rules - synchronous and asynchronous
 */

import org.openksavi.sponge.test.util.CorrelationEventsLog

class Constants {
    companion object {
        val correlationEventsLog = CorrelationEventsLog()
    }
}

fun onInit() {
    // Variables for assertions only
    EPS.setVariable("correlationEventsLog", Constants.correlationEventsLog)
}

class RuleFFF : Rule() {
    override fun onConfigure() {
        setEvents("e1", "e2", "e3 :first")
        setSynchronous(true)
    }

    override fun onRun(event: Event?) {
        logger.debug("Running rule for event: {}", event?.name)
        Constants.correlationEventsLog.addEvents(name, this)
    }
}

class RuleFFL : Rule() {
    override fun onConfigure() {
        setEvents("e1", "e2", "e3 :last")
        duration = Duration.ofSeconds(2)
        setSynchronous(false)
    }

    override fun onRun(event: Event?) {
        logger.debug("Running rule for event: {}", event?.name)
        Constants.correlationEventsLog.addEvents(name, this)
    }
}

fun onStartup() {
    EPS.event("e1").set("label", "1").send()
    EPS.event("e2").set("label", "2").send()
    EPS.event("e2").set("label", "3").send()
    EPS.event("e2").set("label", "4").send()
    EPS.event("e3").set("label", "5").send()
    EPS.event("e3").set("label", "6").send()
    EPS.event("e3").set("label", "7").send()
}