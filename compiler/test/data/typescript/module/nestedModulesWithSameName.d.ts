declare namespace Foo {
    export interface A {
        baz();
    }
    export namespace Foo {
        export class B {
            boo() : A;
        }
        export var c: number;
    }

    export function d(a: A, b, c: Foo.B);
}
