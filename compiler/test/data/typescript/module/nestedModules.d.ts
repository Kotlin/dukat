declare module Foo {
    export module Bar {
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
}

