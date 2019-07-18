@file:JsQualifier("Ext")
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION")
package Ext

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

external interface IAbstractComponent : Ext.IBase, Ext.util.IPositionable, Ext.util.IObservable, Ext.util.IAnimate, Ext.util.IElementContainer, Ext.util.IRenderable, Ext.state.IStateful {
    var autoEl: Any
    var autoLoad: Any? get() = definedExternally; set(value) = definedExternally
    var autoRender: Any
}
external var num: Number
external fun foo()
external interface IAbstractPlugin : Ext.IBase {
    var pluginId: String? get() = definedExternally; set(value) = definedExternally
    var isPlugin: Boolean? get() = definedExternally; set(value) = definedExternally
}
external var str: String
external fun bar()
