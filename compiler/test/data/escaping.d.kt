package escaping

external var `val`: Any = definedExternally
external var `$foo`: Boolean = definedExternally
external fun `bar$`(`ba$z`: Number): Unit = definedExternally
external fun `fun`(): Unit = definedExternally
external interface This {
    var `when`: String
    var `typealias`: Number
    var `typeof`: Number
    fun `in`(`object`: Foo)
    companion object {
        var `$foo`: Boolean = definedExternally
        fun `bar$`(`ba$z`: Number): Unit = definedExternally
        var aaa: `when`.`interface` = definedExternally
        var bbb: `when`.`$foo` = definedExternally
    }
}
external open class `is`<`interface`> {
    open var `as`: Number = definedExternally
    open fun `package`(a: Any): Boolean = definedExternally
}
external fun <T, U> When(value: `when`.Promise<T>, transform: (`val`: T) -> U): `fun`.Promise<U> = definedExternally
external var `_`: `__`.`___` = definedExternally

// ------------------------------------------------------------------------------------------
@file:JsQualifier("when")
package escaping.`when`

external var `$`: Boolean = definedExternally
external fun `package`(`as`: bar.string.`interface`, b: `$boo`.`typealias`): `$tring` = definedExternally
external interface Promise<T>

// ------------------------------------------------------------------------------------------
@file:JsQualifier("__")
package escaping.`__`

external interface `___`
external interface _OK_
