@file:JsQualifier("Ext")
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "NESTED_CLASS_IN_EXTERNAL_INTERFACE")
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
    var autoLoad: Any?
    var autoRender: Any
}
external var num: Number
external fun foo(): Unit
external interface IAbstractPlugin : Ext.IBase {
    var pluginId: String?
    var isPlugin: Boolean?
}
external var str: String
external fun bar(): Unit
