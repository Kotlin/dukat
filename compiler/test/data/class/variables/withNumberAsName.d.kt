package withNumberAsName

external interface IBar
external open class Foo {
    open var `200`: String = definedExternally
    open var `300`: Number = definedExternally
    open var `400`: IBar = definedExternally
}
