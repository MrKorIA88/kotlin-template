package ru.vtb.at.template.properties

import org.aeonbits.owner.Config
import org.aeonbits.owner.Config.DefaultValue

@Config.LoadPolicy(Config.LoadType.MERGE)
@Config.Sources(
    "system:properties",
    "system:env",
    "file:src/test/resources/test.properties"
)
interface Props: Config {

    @get:Config.Key("test.url")
    var url : String

    @get:Config.Key("timeout")
    @get:DefaultValue(value = "2000")
    var timeout : Long

    @get:Config.Key("expected.value")
    var expected : String
}