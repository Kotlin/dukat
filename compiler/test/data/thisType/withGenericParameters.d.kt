package withGenericParameters

external interface Interface<T> {
    var foo: Interface<T> /* this */
    fun bar(): Interface<T> /* this */
}
external open class Class<T, U> {
    open var baz: Class<T, U> /* this */ = definedExternally
    open fun boo(): Class<T, U> /* this */ = definedExternally
}
