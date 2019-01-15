package inClass

external open class MyClass {
    open var self: MyClass /* this */ = definedExternally
    open fun that(): MyClass /* this */ = definedExternally
}
