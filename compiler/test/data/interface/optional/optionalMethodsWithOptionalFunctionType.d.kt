package optionalMethodsWithOptionalFunctionType

external interface Foo {
    val foo: ((f: ((n: Number, s: String) -> String)? /*= null*/) -> Boolean)? get() = definedExternally
}
