package generics

external open class Foo<T> {
    open var varT: T = definedExternally
    open fun withoutArgumentsReturnsT(): T = definedExternally
    open fun withOneT(a: T): T = definedExternally
    open fun <B> returnsB(a: Any): B = definedExternally
    open fun <A, B> withManyArguments(a: A, b: B): T = definedExternally
}
