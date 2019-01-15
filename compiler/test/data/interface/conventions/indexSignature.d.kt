external interface Foo {
    @nativeGetter
    operator fun get(n: Number): Bar?
    @nativeSetter
    operator fun set(n: Number, value: Bar)
    @nativeGetter
    operator fun get(s: String): String?
    @nativeSetter
    operator fun set(s: String, value: String)
}
