@file:JsModule("domain")
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION")
package domain

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

external open class Domain : events.EventEmitter, NodeJS.Domain {
    open fun <T> run(fn: (args: Array<Any>) -> T, vararg args: Any): T
    open fun add(emitter: events.EventEmitter)
    open fun add(emitter: NodeJS.Timer)
    open fun remove(emitter: events.EventEmitter)
    open fun remove(emitter: NodeJS.Timer)
    open fun <T : Function<*>> bind(cb: T): T
    open fun <T : Function<*>> intercept(cb: T): T
    open var members: Array<dynamic /* events.EventEmitter | NodeJS.Timer */>
    open fun enter()
    open fun exit()
}

external fun create(): Domain