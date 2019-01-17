//TODO: external module
declare module "atpl" {
    export var __foo;
    export function __express(filename: string, options: any, callback: Function): any;
}

declare var __foo;
declare function __express(filename: string, options: any, callback: Function): any;

declare class __A {
    __foo: number;
    __express(filename: string, options: any, callback: Function): any;
}

declare interface __B {
    __foo: number;
    __express(filename: string, options: any, callback: Function): any;
}

declare enum __E {
    __A,
    __B //TODO = __A + 1
}

declare module __M {
    var __foo: number;
    function __express(filename: string, options: any, callback: Function): any;

    export module __N {
        var __foo: number;
        function __express(filename: string, options: any, callback: Function): any;

        export class __C {
        }
    }
}

declare function foo<__T>(__a: __T, _b: __M.__N.__C/*TODO = __a*/);
