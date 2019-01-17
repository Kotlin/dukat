package undefinedAndNullTypes

external fun foo(a: Nothing?): Nothing? = definedExternally
external fun bar(a: Nothing?): Nothing? = definedExternally
external interface I {
    fun foo(a: Nothing?): Nothing?
    fun bar(a: Nothing?): Nothing?
}
external open class C {
    open fun foo(a: Nothing?): Nothing? = definedExternally
    open fun bar(a: Nothing?): Nothing? = definedExternally
}
