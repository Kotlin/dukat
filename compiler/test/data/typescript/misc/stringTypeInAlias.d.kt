@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION")

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

@JsModule("<RESOLVED_MODULE_NAME>")
external open class A11yDialog {
    constructor(el: Element?, containers: NodeList?)
    constructor(el: Element?, containers: Element?)
    constructor(el: Element?, containers: String?)
    constructor(el: Element?, containers: Nothing?)
    open fun create(el: Element? = definedExternally, containers: NodeList? = definedExternally)
    open fun create(el: Element? = definedExternally, containers: Element? = definedExternally)
    open fun create(el: Element? = definedExternally, containers: String? = definedExternally)
    open fun create(el: Element? = definedExternally, containers: Nothing? = definedExternally)
    open fun on(evt: String, callback: (dialogElement: Any, event: Event) -> Unit)
    open fun off(evt: String, callback: (dialogElement: Any, event: Event) -> Unit)
    open fun create()
}