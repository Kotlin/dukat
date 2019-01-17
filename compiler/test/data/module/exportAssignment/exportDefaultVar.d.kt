@file:JsModule("fooVar")
package exportDefaultVar.fooVar

external fun baz(): Unit = definedExternally
@JsName("default")
external var bar: String = definedExternally
