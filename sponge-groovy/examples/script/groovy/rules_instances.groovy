/**
 * Sponge Knowledge base
 * Rules - instances
 */

import java.util.concurrent.atomic.AtomicInteger

void onInit() {
    // Variables for assertions only
    EPS.setVariable("countA", new AtomicInteger(0))
    EPS.setVariable("countB", new AtomicInteger(0))
    EPS.setVariable("max", 100)
}

class RuleA extends Rule {
    void onConfigure() {
        this.events = ["a a1", "a a2"]
    }
    void onRun(Event event) {
        EPS.getVariable("countA").incrementAndGet()
    }
}

class RuleB extends Rule {
    void onConfigure() {
        this.events = ["b b1", "b b2"]
    }
    void onRun(Event event) {
        EPS.getVariable("countB").incrementAndGet()
    }
}

void onStartup() {
    for (i in (0..<EPS.getVariable("max"))) {
        EPS.event("a").send()
        EPS.event("b").send()
    }
}