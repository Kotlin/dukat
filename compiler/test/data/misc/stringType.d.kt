package stringType

external fun foo(s: String /* "number" */): Number = definedExternally
external fun foo(s: String /* "string" */): String = definedExternally
external interface I {
    fun bar(s: String /* "number" */): Number
    fun bar(s: String /* "string" */): String
}
external open class C {
    open fun baz(s: String /* "number" */): Number = definedExternally
    open fun baz(s: String /* "string" */): String = definedExternally
}
