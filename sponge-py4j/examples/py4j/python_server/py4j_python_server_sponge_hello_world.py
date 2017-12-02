"""
Sponge Knowledge base
Py4J Hello world
"""

class PythonUpperCase(Action):
    def onCall(self, args):
        result = py4j.facade.toUpperCase(args[0])
        self.logger.debug("CPython result for {} is {}", args[0], result)
        return result