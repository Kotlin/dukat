package functionAndSecondaryWithTrait

external interface Fiber {
    var reset: () -> Any
    var run: (param: Any? /*= null*/) -> Any
    var throwInto: (ex: Any) -> Any
}

// ------------------------------------------------------------------------------------------
package functionAndSecondaryWithTrait.fibers

@JsModule("fibers")
external fun Fiber(fn: Function<*>): Fiber = definedExternally

// ------------------------------------------------------------------------------------------
@file:JsModule("fibers")
package functionAndSecondaryWithTrait.fibers.Fiber

external var current: Fiber = definedExternally
external fun yield(value: Any? = definedExternally /* null */): Any = definedExternally
