declare class Foo {
    bar(): number;
    static variable: string;
    static bar();
}

declare module Foo {
    export function baz(a);
}
