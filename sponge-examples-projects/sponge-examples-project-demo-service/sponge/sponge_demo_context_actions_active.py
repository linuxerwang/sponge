"""
Sponge Knowledge base
Demo - Action - Active context actions
"""

class ContextActionsActiveInactive(Action):
    def onConfigure(self):
        self.withLabel("Action with active/inactive context actions").withArgs([
            BooleanType("active").withLabel("Active").withDefaultValue(False)
        ]).withCallable(False).withFeatures({"contextActions":["ContextActionsActiveInactive_ContextAction1(active)",
                                                               "ContextActionsActiveInactive_ContextAction2()"]})

class ContextActionsActiveInactive_ContextAction1(Action):
    def onConfigure(self):
        self.withLabel("Active/inactive context action").withArg(BooleanType("active").withNullable().withFeature("visible", False)).withNoResult()
    def onIsActive(self, context):
        return context.args[0] if context.args is not None and context.args[0] is not None else False
    def onCall(self, active):
        pass

class ContextActionsActiveInactive_ContextAction2(Action):
    def onConfigure(self):
        self.withLabel("Context action 2").withNoArgs().withNoResult().withFeature("visible", False)
    def onCall(self):
        pass
