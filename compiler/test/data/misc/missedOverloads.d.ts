// inspired by jquery.d.ts

declare interface MyXHR {}
declare interface MyQuery {}
declare interface MyEvent {}
declare interface MyOptions {}

declare interface JQueryStatic {
    get(url: string, success?: () => any, dataType?: string): MyXHR;
    get(url: string, data?: Object | string, success?: () => any, dataType?: string): MyXHR;
    get(settings: MyOptions): MyXHR;

    (selector: string, context?: Element | MyQuery): MyQuery
    (element: Element): MyQuery;
    (): MyQuery;

    (html: string, ownerDocument?: Document): MyQuery;
    (html: string, attributes: Object): MyQuery;

    fadeTo(duration: string | number, opacity: number, complete?: Function): MyQuery;
    fadeTo(duration: string | number, opacity: number, easing?: string, complete?: Function): MyQuery;
}

declare class JJ {
    foo(data: string, context?: HTMLElement, keepScripts?: boolean): any[];

    hide(duration?: number|string, complete?: Function): MyQuery;
    hide(duration?: number|string, easing?: string, complete?: Function): MyQuery;
    hide(options: MyOptions): MyQuery;

    trigger(eventType: string, extraParameters?: any[]|Object): MyQuery;
    trigger(event: MyEvent, extraParameters?: any[]|Object): MyQuery;
}

declare function foo(data: string, context?: HTMLElement, keepScripts?: boolean): any[];

declare function hide(duration?: number|string, complete?: Function): MyQuery;
declare function hide(duration?: number|string, easing?: string, complete?: Function): MyQuery;
declare function hide(options: MyOptions): MyQuery;

declare function trigger(eventType: string, extraParameters?: any[]|Object): MyQuery;
declare function trigger(event: MyEvent, extraParameters?: any[]|Object): MyQuery;
