"""
Sponge Knowledge base
Action onConfigure error
"""

class TestAction(Action):
    def onConfigure(self):
        self.argsMeta = []
        self.resultMeta = ResultMeta(StringType()).label_error("Test action")
    def onCall(self):
        return None
