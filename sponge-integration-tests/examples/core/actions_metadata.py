"""
Sponge Knowledge base
Action argument definitions
"""

from org.openksavi.sponge.examples import PowerEchoMetadataAction

def onInit():
    # Variables for assertions only
    EPS.setVariable("scriptActionResult", None)
    EPS.setVariable("javaActionResult", None)

class UpperEchoAction(Action):
    def onConfigure(self):
        self.displayName = "Echo Action"
        self.description = "Returns the upper case string"
        self.argsMeta = [ ArgMeta("arg1", Type.STRING).displayName("Argument 1").description("Argument 1 description") ]
        self.resultMeta = ResultMeta(Type.STRING).displayName("Upper case string").description("Result description")
    def onCall(self, text):
        self.logger.info("Action {} called", self.name)
    	return str(text).upper()

def onLoad():
    EPS.enableJava(PowerEchoMetadataAction)

def onStartup():
    EPS.logger.debug("Calling script defined action")
    scriptActionResult = EPS.call("UpperEchoAction", "test")
    EPS.logger.debug("Action returned: {}", scriptActionResult)
    EPS.setVariable("scriptActionResult", scriptActionResult)

    EPS.logger.debug("Calling Java defined action")
    javaActionResult = EPS.call("PowerEchoMetadataAction", 1, "test")
    EPS.logger.debug("Action returned: {}", javaActionResult)
    EPS.setVariable("javaActionResult", javaActionResult)

