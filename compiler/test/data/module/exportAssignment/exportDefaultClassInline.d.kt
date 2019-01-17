@file:JsModule("foo")
package exportDefaultClassInline.foo

external fun baz(): Unit = definedExternally
@JsName("default")
external open class C
