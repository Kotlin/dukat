external interface Foo {
    var bar: ((b: Boolean, baz: Any) -> Boolean)? get() = definedExternally; set(value) = definedExternally
}
