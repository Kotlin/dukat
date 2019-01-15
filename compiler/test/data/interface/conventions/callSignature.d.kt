external interface Foo {
    @nativeInvoke
    operator fun invoke()
    @nativeInvoke
    operator fun invoke(n: Number): Boolean
    @nativeInvoke
    operator fun invoke(foo: Foo, s: String): Bar
}
