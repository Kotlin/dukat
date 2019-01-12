package withPropertyDeclaration

external open class Foo(x: Any) {
    open var x: Any = definedExternally
}
external open class Bar(n: Number, a: Any) {
    open var n: Number = definedExternally
    open var a: Any = definedExternally
}
