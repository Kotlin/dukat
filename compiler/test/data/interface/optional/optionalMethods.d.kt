package optionalMethods

external interface Foo {
    val methodWithOutArgs: (() -> Unit)? get() = definedExternally
    val methodWithString: ((s: String) -> String)? get() = definedExternally
    val methodWithManyArgs: ((n: Number, settings: Bar) -> Boolean)? get() = definedExternally
}
