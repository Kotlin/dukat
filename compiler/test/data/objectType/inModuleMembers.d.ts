declare module Foo {
    export function withObjectTypeParam(opt: {
        bar(a): number;
        baz?;
        boo?: any;
        show: (overrideChecks: boolean) => void;
    });

    export function returnsObjectType(): {
        value?;
        done: boolean;
    };

    export var foo: {
        bar(a): number;
        baz(a, b: any, c: String): boolean;
    };
}
