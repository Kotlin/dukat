package variables

external open class Foo {
    companion object {
        var varAsAny: Any = definedExternally
        var varAsNumber: Number = definedExternally
        var varAsBoolean: Boolean = definedExternally
        var varAsString: String = definedExternally
    }
}
