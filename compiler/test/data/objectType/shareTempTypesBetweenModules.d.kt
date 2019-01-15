package shareTempTypesBetweenModules

external interface `T$4` {
    fun bar(a: Any): Number
    var baz: Any? get() = definedExternally; set(value) = definedExternally
    var boo: Any? get() = definedExternally; set(value) = definedExternally
    var show: (overrideChecks: Boolean) -> Unit
}
external fun withObjectTypeParam(opt: `T$4`): Unit = definedExternally
external interface `T$5` {
    var value: Any? get() = definedExternally; set(value) = definedExternally
    var done: Boolean
}
external fun returnsObjectType(): `T$5` = definedExternally
external object foo {
    fun bar(a: Any): Number = definedExternally
    fun baz(a: Any, b: Any, c: String): Boolean = definedExternally
}

// ------------------------------------------------------------------------------------------
@file:JsQualifier("Foo")
package shareTempTypesBetweenModules.Foo

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

// ------------------------------------------------------------------------------------------
@file:JsQualifier("Bar")
package shareTempTypesBetweenModules.Bar

external interface `T$2` {
    fun bar(a: Any): Number
    var baz: Any? get() = definedExternally; set(value) = definedExternally
    var boo: Any? get() = definedExternally; set(value) = definedExternally
    var show: (overrideChecks: Boolean) -> Unit
}
external fun someFunction(opt: `T$2`): Unit = definedExternally
external interface `T$3` {
    var value: Any? get() = definedExternally; set(value) = definedExternally
    var done: Boolean
}
external fun anotherReturnsObjectType(): `T$3` = definedExternally
external object foo {
    fun bar(a: Any): Number = definedExternally
    fun baz(a: Any, b: Any, c: String): Boolean = definedExternally
}
