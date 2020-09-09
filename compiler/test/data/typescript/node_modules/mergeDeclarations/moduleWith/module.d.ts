// based on Ext.d.ts
declare module Ext {
    export interface IAbstractComponent extends Ext.IBase,Ext.util.IPositionable,Ext.util.IObservable,Ext.util.IAnimate,Ext.util.IElementContainer,Ext.util.IRenderable,Ext.state.IStateful {
        autoEl;
        autoLoad?: any;
        autoRender: any;
    }
    export var num: number;
    export function foo();
}

declare module Ext {
    export interface IAbstractPlugin extends Ext.IBase {
        pluginId?: string;
        isPlugin?: boolean;
    }
    export var str: string;
    export function bar();
    export interface IBase {

    }
}

declare module Ext.util {
    export interface IPositionable {

    }
    export interface IObservable {

    }
    export interface IAnimate {

    }
    export interface IElementContainer {

    }
    export interface IRenderable {

    }
}

declare module Ext.state {
    export interface IStateful {

    }
}