package genericOptionalMethods

external interface Foo<T> {
    val methodWithOutArgs: (() -> Unit)? get() = definedExternally
    val <A> methodWithString: ((s: A) -> T)? get() = definedExternally
    val <A : T, B> methodWithManyArgs: ((n: A, settings: Bar) -> B)? get() = definedExternally
}
