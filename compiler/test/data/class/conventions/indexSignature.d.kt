external open class Foo {
    @nativeGetter
    open operator fun get(n: Number): Bar? = definedExternally
    @nativeSetter
    open operator fun set(n: Number, value: Bar): Unit = definedExternally
    @nativeGetter
    open operator fun get(s: String): String? = definedExternally
    @nativeSetter
    open operator fun set(s: String, value: String): Unit = definedExternally
}
