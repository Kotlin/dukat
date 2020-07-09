@file:JsModule("assert")
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package assert.internal

import kotlin.js.*
import kotlin.js.Json
import org.khronos.webgl.*
import org.w3c.dom.*
import org.w3c.dom.events.*
import org.w3c.dom.parsing.*
import org.w3c.dom.svg.*
import org.w3c.dom.url.*
import org.w3c.fetch.*
import org.w3c.files.*
import org.w3c.notifications.*
import org.w3c.performance.*
import org.w3c.workers.*
import org.w3c.xhr.*

external interface `T$0` {
    var message: String?
        get() = definedExternally
        set(value) = definedExternally
    var actual: Any?
        get() = definedExternally
        set(value) = definedExternally
    var expected: Any?
        get() = definedExternally
        set(value) = definedExternally
    var operator: String?
        get() = definedExternally
        set(value) = definedExternally
    var stackStartFn: Function<*>?
        get() = definedExternally
        set(value) = definedExternally
}

external open class AssertionError(options: `T$0`? = definedExternally /* null */) : Error {
    override var name: String
    override var message: String
    open var actual: Any
    open var expected: Any
    open var operator: String
    open var generatedMessage: Boolean
    open var code: String /* 'ERR_ASSERTION' */
}

external fun fail(message: String? = definedExternally /* null */): Any

external fun fail(message: Error? = definedExternally /* null */): Any

external fun fail(actual: Any, expected: Any, message: String? = definedExternally /* null */, operator: String? = definedExternally /* null */, stackStartFn: Function<*>? = definedExternally /* null */): Any

external fun fail(actual: Any, expected: Any, message: Error? = definedExternally /* null */, operator: String? = definedExternally /* null */, stackStartFn: Function<*>? = definedExternally /* null */): Any

external fun ok(value: Any, message: String? = definedExternally /* null */)

external fun ok(value: Any, message: Error? = definedExternally /* null */)

external fun equal(actual: Any, expected: Any, message: String? = definedExternally /* null */)

external fun equal(actual: Any, expected: Any, message: Error? = definedExternally /* null */)

external fun notEqual(actual: Any, expected: Any, message: String? = definedExternally /* null */)

external fun notEqual(actual: Any, expected: Any, message: Error? = definedExternally /* null */)

external fun deepEqual(actual: Any, expected: Any, message: String? = definedExternally /* null */)

external fun deepEqual(actual: Any, expected: Any, message: Error? = definedExternally /* null */)

external fun notDeepEqual(actual: Any, expected: Any, message: String? = definedExternally /* null */)

external fun notDeepEqual(actual: Any, expected: Any, message: Error? = definedExternally /* null */)

external fun strictEqual(actual: Any, expected: Any, message: String? = definedExternally /* null */)

external fun strictEqual(actual: Any, expected: Any, message: Error? = definedExternally /* null */)

external fun notStrictEqual(actual: Any, expected: Any, message: String? = definedExternally /* null */)

external fun notStrictEqual(actual: Any, expected: Any, message: Error? = definedExternally /* null */)

external fun deepStrictEqual(actual: Any, expected: Any, message: String? = definedExternally /* null */)

external fun deepStrictEqual(actual: Any, expected: Any, message: Error? = definedExternally /* null */)

external fun notDeepStrictEqual(actual: Any, expected: Any, message: String? = definedExternally /* null */)

external fun notDeepStrictEqual(actual: Any, expected: Any, message: Error? = definedExternally /* null */)

external fun throws(block: () -> Any, message: String? = definedExternally /* null */)

external fun throws(block: () -> Any, message: Error? = definedExternally /* null */)

external fun throws(block: () -> Any, error: RegExp, message: String? = definedExternally /* null */)

external fun throws(block: () -> Any, error: RegExp, message: Error? = definedExternally /* null */)

external fun throws(block: () -> Any, error: Function<*>, message: String? = definedExternally /* null */)

external fun throws(block: () -> Any, error: Function<*>, message: Error? = definedExternally /* null */)

external fun throws(block: () -> Any, error: Any, message: String? = definedExternally /* null */)

external fun throws(block: () -> Any, error: Any, message: Error? = definedExternally /* null */)

external fun throws(block: () -> Any, error: Error, message: String? = definedExternally /* null */)

external fun throws(block: () -> Any, error: Error, message: Error? = definedExternally /* null */)

external fun doesNotThrow(block: () -> Any, message: String? = definedExternally /* null */)

external fun doesNotThrow(block: () -> Any, message: Error? = definedExternally /* null */)

external fun doesNotThrow(block: () -> Any, error: RegExp, message: String? = definedExternally /* null */)

external fun doesNotThrow(block: () -> Any, error: RegExp, message: Error? = definedExternally /* null */)

external fun doesNotThrow(block: () -> Any, error: Function<*>, message: String? = definedExternally /* null */)

external fun doesNotThrow(block: () -> Any, error: Function<*>, message: Error? = definedExternally /* null */)

external fun ifError(value: Any)

external fun rejects(block: () -> Promise<Any>, message: String? = definedExternally /* null */): Promise<Unit>

external fun rejects(block: () -> Promise<Any>, message: Error? = definedExternally /* null */): Promise<Unit>

external fun rejects(block: Promise<Any>, message: String? = definedExternally /* null */): Promise<Unit>

external fun rejects(block: Promise<Any>, message: Error? = definedExternally /* null */): Promise<Unit>

external fun rejects(block: () -> Promise<Any>, error: RegExp, message: dynamic /* String | Error */ = definedExternally /* null */): Promise<Unit>

external fun rejects(block: () -> Promise<Any>, error: Function<*>, message: dynamic /* String | Error */ = definedExternally /* null */): Promise<Unit>

external fun rejects(block: () -> Promise<Any>, error: Any, message: dynamic /* String | Error */ = definedExternally /* null */): Promise<Unit>

external fun rejects(block: () -> Promise<Any>, error: Error, message: dynamic /* String | Error */ = definedExternally /* null */): Promise<Unit>

external fun rejects(block: Promise<Any>, error: RegExp, message: dynamic /* String | Error */ = definedExternally /* null */): Promise<Unit>

external fun rejects(block: Promise<Any>, error: Function<*>, message: dynamic /* String | Error */ = definedExternally /* null */): Promise<Unit>

external fun rejects(block: Promise<Any>, error: Any, message: dynamic /* String | Error */ = definedExternally /* null */): Promise<Unit>

external fun rejects(block: Promise<Any>, error: Error, message: dynamic /* String | Error */ = definedExternally /* null */): Promise<Unit>

external fun doesNotReject(block: () -> Promise<Any>, message: String? = definedExternally /* null */): Promise<Unit>

external fun doesNotReject(block: () -> Promise<Any>, message: Error? = definedExternally /* null */): Promise<Unit>

external fun doesNotReject(block: Promise<Any>, message: String? = definedExternally /* null */): Promise<Unit>

external fun doesNotReject(block: Promise<Any>, message: Error? = definedExternally /* null */): Promise<Unit>

external fun doesNotReject(block: () -> Promise<Any>, error: RegExp, message: String? = definedExternally /* null */): Promise<Unit>

external fun doesNotReject(block: () -> Promise<Any>, error: RegExp, message: Error? = definedExternally /* null */): Promise<Unit>

external fun doesNotReject(block: () -> Promise<Any>, error: Function<*>, message: String? = definedExternally /* null */): Promise<Unit>

external fun doesNotReject(block: () -> Promise<Any>, error: Function<*>, message: Error? = definedExternally /* null */): Promise<Unit>

external fun doesNotReject(block: Promise<Any>, error: RegExp, message: String? = definedExternally /* null */): Promise<Unit>

external fun doesNotReject(block: Promise<Any>, error: RegExp, message: Error? = definedExternally /* null */): Promise<Unit>

external fun doesNotReject(block: Promise<Any>, error: Function<*>, message: String? = definedExternally /* null */): Promise<Unit>

external fun doesNotReject(block: Promise<Any>, error: Function<*>, message: Error? = definedExternally /* null */): Promise<Unit>

external var strict: Any

external fun fail(): Any

external fun fail(actual: Any, expected: Any): Any

external fun ok(value: Any)

external fun equal(actual: Any, expected: Any)

external fun notEqual(actual: Any, expected: Any)

external fun deepEqual(actual: Any, expected: Any)

external fun notDeepEqual(actual: Any, expected: Any)

external fun strictEqual(actual: Any, expected: Any)

external fun notStrictEqual(actual: Any, expected: Any)

external fun deepStrictEqual(actual: Any, expected: Any)

external fun notDeepStrictEqual(actual: Any, expected: Any)

external fun throws(block: () -> Any)

external fun throws(block: () -> Any, error: RegExp)

external fun throws(block: () -> Any, error: Function<*>)

external fun throws(block: () -> Any, error: Any)

external fun throws(block: () -> Any, error: Error)

external fun doesNotThrow(block: () -> Any)

external fun doesNotThrow(block: () -> Any, error: RegExp)

external fun doesNotThrow(block: () -> Any, error: Function<*>)

external fun rejects(block: () -> Promise<Any>): Promise<Unit>

external fun rejects(block: Promise<Any>): Promise<Unit>

external fun doesNotReject(block: () -> Promise<Any>): Promise<Unit>

external fun doesNotReject(block: Promise<Any>): Promise<Unit>

external fun doesNotReject(block: () -> Promise<Any>, error: RegExp): Promise<Unit>

external fun doesNotReject(block: () -> Promise<Any>, error: Function<*>): Promise<Unit>

external fun doesNotReject(block: Promise<Any>, error: RegExp): Promise<Unit>

external fun doesNotReject(block: Promise<Any>, error: Function<*>): Promise<Unit>