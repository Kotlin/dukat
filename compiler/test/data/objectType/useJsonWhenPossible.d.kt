package useJsonWhenPossible

external var foo: Json = definedExternally
external interface `T$0` {
    @nativeGetter
    operator fun get(s: Number): Any?
    @nativeSetter
    operator fun set(s: Number, value: Any)
}
external interface Foo {
    var foo: Json
    var boo: `T$0`
}

// ------------------------------------------------------------------------------------------
@file:JsQualifier("Module")
package useJsonWhenPossible.Module

external var bar: Json = definedExternally
external fun withObjectTypeParam(bar: Json): Unit = definedExternally
external open class Foo {
    open var prop: Json = definedExternally
}
