declare var foo: string|number;

declare function bar(a: string|number|Foo);

declare function baz(a: string|number|Foo, b: number|Foo);

declare class Foo {
    constructor(a: string|number);
    someMethod(): string|number;
    foo: string|number;
    optionalFoo?: string|number;
    refs: {
        [key: string]: string|number;
    };
}
