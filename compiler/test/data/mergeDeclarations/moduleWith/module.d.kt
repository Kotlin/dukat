@file:JsQualifier("Ext")
package module.Ext

external interface IAbstractComponent : Ext.IBase, Ext.util.IPositionable, Ext.util.IObservable, Ext.util.IAnimate, Ext.util.IElementContainer, Ext.util.IRenderable, Ext.state.IStateful {
    var autoEl: Any
    var autoLoad: Any? get() = definedExternally; set(value) = definedExternally
    var autoRender: Any
}
external var num: Number = definedExternally
external fun foo(): Unit = definedExternally
external interface IAbstractPlugin : Ext.IBase {
    var pluginId: String? get() = definedExternally; set(value) = definedExternally
    var isPlugin: Boolean? get() = definedExternally; set(value) = definedExternally
}
external var str: String = definedExternally
external fun bar(): Unit = definedExternally
