"""
Sponge Knowledge base
Demo - Action - setter context actions
"""

def createFruitWithColorRecordType(name = None):
    return RecordType(name).withLabel("Fruit").withAnnotated().withFields([
                StringType("name").withLabel("Name"),
                StringType("color").withLabel("Color")
            ])

class FruitsWithColorsContextSetter(Action):
    def onConfigure(self):
        self.withLabel("Fruits with colors - context setter").withArgs([
            ListType("fruits").withLabel("Fruits").withElement(createFruitWithColorRecordType("fruit")).withDefaultValue([
                AnnotatedValue({"name":"Orange", "color":"orange"}),
                AnnotatedValue({"name":"Lemon", "color":"yellow"}),
                AnnotatedValue({"name":"Apple", "color":"red"})]).withFeatures({
                    "updateAction":"this=FruitsWithColorsContextSetter_Update(this)", "contextActions":["this=FruitsWithColorsContextSetter_Choose"]})
        ]).withCallable(False)

class FruitsWithColorsContextSetter_Update(Action):
    def onConfigure(self):
        self.withLabel("Update a fruit").withArgs([
            createFruitWithColorRecordType("fruit")
        ]).withResult(createFruitWithColorRecordType("fruit"))
        self.withFeatures({"callLabel":"Save"})
    def onCall(self, fruit):
        return fruit

class FruitsWithColorsContextSetter_Choose(Action):
    def onConfigure(self):
        self.withLabel("Choose a fruit").withArgs([
            createFruitWithColorRecordType("chosenFruit").withNullable().withFeature("visible", False).withProvided(
                ProvidedMeta().withValue().withImplicitMode()),
            ListType("fruits").withLabel("Fruits").withElement(
                    createFruitWithColorRecordType("fruit").withProvided(ProvidedMeta().withSubmittable())
                ).withProvided(
                    ProvidedMeta().withValue().withDependency("chosenFruit").withOptionalMode().withOverwrite()
                ).withFeatures({"activateAction":"submit"})
        ]).withResult(createFruitWithColorRecordType())
        self.withFeatures({"callLabel":"Choose"})

    def onCall(self, chosenFruit, fruits):
        if chosenFruit:
            chosenFruit.valueLabel = None
        return chosenFruit

    def onProvideArgs(self, context):
        chosenFruit = None
        if "fruits.fruit" in context.submit:
            chosenFruit = context.current["fruits.fruit"]

        if "chosenFruit" in context.provide or "fruits.fruit" in context.submit:
            context.provided["chosenFruit"] = ProvidedValue().withValue(chosenFruit)

        if "fruits" in context.provide or "fruits.fruit" in context.submit:
            if chosenFruit is None:
                chosenFruit = context.current["chosenFruit"]
            chosenFruitName = chosenFruit.value["name"] if chosenFruit else None

            context.provided["fruits"] = ProvidedValue().withValue([
                AnnotatedValue({"name":"Kiwi", "color":"green"}).withValueLabel("Kiwi").withFeature("icon", "star" if chosenFruitName == "Kiwi" else None),
                AnnotatedValue({"name":"Banana", "color":"yellow"}).withValueLabel("Banana").withFeature("icon", "star" if chosenFruitName == "Banana" else None)
            ])
