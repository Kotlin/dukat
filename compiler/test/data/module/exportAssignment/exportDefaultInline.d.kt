@file:JsModule("foo")
package exportDefaultInline.foo

external fun baz(): Unit = definedExternally
@JsName("default")
external fun bar(): String = definedExternally
