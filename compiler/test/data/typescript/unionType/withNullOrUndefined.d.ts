declare var foo: string | undefined;
declare var bar: string | null;

declare function bar(a: string | undefined): Foo | null;

declare function baz(a: Foo | null, b?: number | undefined): any | undefined;

declare class Foo {
    constructor(a: string | null);

    someMethod(): string | number | undefined;

    foo: Foo | null;
    optionalFoo?: string | null;
    optionalFoo2?: string | undefined;
    optionalFoo3?: string | undefined | null;
    refs: {
        [key: string | undefined]: string | null;
    };
}

interface IBar {
    someMethod(): string | number | undefined;

    foo: Foo | null;
    optionalFoo?: string | null;
    optionalFoo2?: string | undefined;
    optionalFoo3?: string | undefined | null;
}