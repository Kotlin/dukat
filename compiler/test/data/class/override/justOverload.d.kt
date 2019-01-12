package justOverload

external open class Foo {
    open fun bar(a: Number): Unit = definedExternally
}
external open class Boo : Foo {
    open fun bar(a: String): Unit = definedExternally
}
