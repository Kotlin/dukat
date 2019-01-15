external interface IBar
external interface IFoo {
    var `200`: String
    var `300`: Number? get() = definedExternally; set(value) = definedExternally
    var `400`: IBar? get() = definedExternally; set(value) = definedExternally
}
