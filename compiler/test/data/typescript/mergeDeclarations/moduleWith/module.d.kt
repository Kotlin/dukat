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
import Ext.util.IPositionable
import Ext.util.IObservable
import Ext.util.IAnimate
import Ext.util.IElementContainer
import Ext.util.IRenderable
import Ext.state.IStateful

external interface IAbstractComponent : IBase, IPositionable, IObservable, IAnimate, IElementContainer, IRenderable, IStateful {
    var autoEl: Any
    var autoLoad: Any?
        get() = definedExternally
        set(value) = definedExternally
    var autoRender: Any
}

external var num: Number

external fun foo()

external interface IAbstractPlugin : IBase {
    var pluginId: String?
        get() = definedExternally
        set(value) = definedExternally
    var isPlugin: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external var str: String

external fun bar()

external interface IBase

// ------------------------------------------------------------------------------------------
@file:JsQualifier("Ext.util")
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION")
package Ext.util

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

external interface IPositionable

external interface IObservable

external interface IAnimate

external interface IElementContainer

external interface IRenderable

// ------------------------------------------------------------------------------------------
@file:JsQualifier("Ext.state")
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION")
package Ext.state

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

external interface IStateful
