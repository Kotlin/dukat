package function

external interface Fiber_ {
    var reset: () -> Any
    var run: (param: Any? /*= null*/) -> Any
    var throwInto: (ex: Any) -> Any
}

// ------------------------------------------------------------------------------------------
package function.fibers

@JsModule("fibers")
external fun Fiber(fn: Function<*>): Fiber_ = definedExternally

// ------------------------------------------------------------------------------------------
@file:JsModule("fibers")
package function.fibers.Fiber

external var current: Fiber = definedExternally
external fun yield(value: Any? = definedExternally /* null */): Any = definedExternally
