@file:JsQualifier("Foo")
package inModuleMembers.Foo

external interface `T$0` {
    fun bar(a: Any): Number
    var baz: Any? get() = definedExternally; set(value) = definedExternally
    var boo: Any? get() = definedExternally; set(value) = definedExternally
    var show: (overrideChecks: Boolean) -> Unit
}
external fun withObjectTypeParam(opt: `T$0`): Unit = definedExternally
external interface `T$1` {
    var value: Any? get() = definedExternally; set(value) = definedExternally
    var done: Boolean
}
external fun returnsObjectType(): `T$1` = definedExternally
external object foo {
    fun bar(a: Any): Number = definedExternally
    fun baz(a: Any, b: Any, c: String): Boolean = definedExternally
}
