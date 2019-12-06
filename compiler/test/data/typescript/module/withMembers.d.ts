declare module Foo {
    export interface A {
        baz();
    }
    export class B {
        boo();
    }
    export var c: number;
    export function d(a: boolean, b, c: SomeType);
    export interface SomeType {

    }
}

