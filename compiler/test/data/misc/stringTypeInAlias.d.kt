@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "NESTED_CLASS_IN_EXTERNAL_INTERFACE")
package stringTypeInAlias

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

external open class A11yDialog {
    constructor(el: Element?, containers: NodeList?)
    constructor(el: Element?, containers: Element?)
    constructor(el: Element?, containers: String?)
    constructor(el: Element?, containers: Nothing?)
    open fun create(el: Element? = definedExternally /* null */, containers: NodeList? = definedExternally /* null */): Unit = definedExternally
    open fun create(el: Element? = definedExternally /* null */, containers: Element? = definedExternally /* null */): Unit = definedExternally
    open fun create(el: Element? = definedExternally /* null */, containers: String? = definedExternally /* null */): Unit = definedExternally
    open fun create(el: Element? = definedExternally /* null */, containers: Nothing? = definedExternally /* null */): Unit = definedExternally
    open fun on(evt: String /* "show" */, callback: (dialogElement: Any, event: Event) -> Unit): Unit = definedExternally
    open fun on(evt: String /* "hide" */, callback: (dialogElement: Any, event: Event) -> Unit): Unit = definedExternally
    open fun on(evt: String /* "destroy" */, callback: (dialogElement: Any, event: Event) -> Unit): Unit = definedExternally
    open fun on(evt: String /* "create" */, callback: (dialogElement: Any, event: Event) -> Unit): Unit = definedExternally
    open fun off(evt: String /* "show" */, callback: (dialogElement: Any, event: Event) -> Unit): Unit = definedExternally
    open fun off(evt: String /* "hide" */, callback: (dialogElement: Any, event: Event) -> Unit): Unit = definedExternally
    open fun off(evt: String /* "destroy" */, callback: (dialogElement: Any, event: Event) -> Unit): Unit = definedExternally
    open fun off(evt: String /* "create" */, callback: (dialogElement: Any, event: Event) -> Unit): Unit = definedExternally
    open fun create(): Unit = definedExternally
}