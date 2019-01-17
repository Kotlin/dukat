package aliases

external open class Foo
external var fooKey: dynamic /* String | Number | Foo */ = definedExternally
external fun barKey(a: String): Unit = definedExternally
external fun barKey(a: Number): Unit = definedExternally
external fun barKey(a: Foo): Unit = definedExternally
external fun barList(a: List<dynamic /* String | Number | Foo */>): Unit = definedExternally
external fun barArray(a: Array<dynamic /* String | Number | Foo */>): Unit = definedExternally
external interface Parent {
    @nativeInvoke
    operator fun invoke(vararg children: String): Foo
    @nativeInvoke
    operator fun invoke(vararg children: Number): Foo
    @nativeInvoke
    operator fun invoke(vararg children: Foo): Foo
}
