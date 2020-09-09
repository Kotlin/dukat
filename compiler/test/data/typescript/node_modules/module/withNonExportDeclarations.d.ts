declare module Foo {
    module Bar {
        export interface A {
            baz();
        }
        class B {
            boo();
        }
        var c: number;
        export function d(a: boolean, b, c: SomeType);
        export interface SomeType {

        }
    }
    interface A {
        baz();
    }
    class B {
        boo();
    }
    var c: number;
    function d(a: boolean, b, c: Bar.SomeType);
}

