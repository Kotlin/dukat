declare interface Foo {
    bar(): number;
}

declare module Foo {
    export function baz(a);
    export function bar(): number;
}
