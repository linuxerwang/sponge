"""
Sponge Knowledge base
Demo - Action - setter context actions
"""

def createNumberFilterRecordType(name = None):
    return RecordType(name).withLabel("Filter").withAnnotated().withFields([
                IntegerType("first").withLabel("First").withMinValue(0).withMaxValue(20),
                IntegerType("last").withLabel("Last").withMinValue(0).withMaxValue(20)
            ]).withDefaultValue({"first":0, "last":10})

class NumbersViewFilterInline(Action):
    def onConfigure(self):
        self.withLabel("Numbers with inline filter").withArgs([
            createNumberFilterRecordType("filter"),
            ListType("numbers").withLabel("Numbers").withAnnotated().withProvided(
                ProvidedMeta().withValue().withDependency("filter").withOverwrite()).withElement(
                    IntegerType().withAnnotated())
        ]).withCallable(False)
    def onProvideArgs(self, context):
        if "numbers" in context.provide:
            filter = context.current["filter"].value
            numbers = range(filter["first"], filter["last"] + 1)
            context.provided["numbers"] = ProvidedValue().withValue(AnnotatedValue(list(map(lambda n: AnnotatedValue(n).withValueLabel(str(n)), numbers))))

class NumbersViewFilterInContextAction(Action):
    def onConfigure(self):
        self.withLabel("Numbers with filter in a context action").withArgs([
            createNumberFilterRecordType("filter").withFeature("visible", False),
            ListType("numbers").withLabel("Numbers").withAnnotated().withProvided(
                ProvidedMeta().withValue().withDependency("filter").withOverwrite()
            ).withElement(IntegerType().withAnnotated()).withFeatures({"scroll":True})
        ]).withNoResult().withCallable(False).withFeatures({"contextActions":["filter=NumbersViewFilterInContextAction_Filter(filter)",
                                                                              "this=NumbersViewFilterInContextAction_ThisSubstitution"]})
    def onProvideArgs(self, context):
        if "numbers" in context.provide:
            filter = context.current["filter"].value
            numbers = range(filter["first"], filter["last"] + 1)
            context.provided["numbers"] = ProvidedValue().withValue(AnnotatedValue(list(map(lambda n: AnnotatedValue(n).withValueLabel(str(n)), numbers))))

class NumbersViewFilterInContextAction_Filter(Action):
    def onConfigure(self):
        self.withLabel("Filter")
        self.withArg(createNumberFilterRecordType("filter")).withResult(createNumberFilterRecordType())
        self.withFeatures({"visible":False, "callLabel":"Save", "icon":"filter"})
    def onCall(self, filter):
        return filter

class NumbersViewFilterInContextAction_ThisSubstitution(Action):
    def onConfigure(self):
        self.withLabel("Substitution of this")
        self.withArgs([
            RecordType("record").withFields([
                createNumberFilterRecordType("filter"),
                ListType("numbers").withLabel("Numbers").withAnnotated().withElement(IntegerType().withAnnotated())
            ])
        ]).withResult(RecordType().withFields([
            createNumberFilterRecordType("filter"),
            ListType("numbers").withLabel("Numbers").withAnnotated().withElement(IntegerType().withAnnotated())
        ]))
        self.withFeatures({"visible":False, "callLabel":"Save", "icon":"attachment"})
    def onCall(self, record):
        return record
