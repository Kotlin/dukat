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

declare module Bar {
    export function someFunction(opt: {
        bar(a): number;
        baz?;
        boo?: any;
        show: (overrideChecks: boolean) => void;
    });

    export function anotherReturnsObjectType(): {
        value?;
        done: boolean;
    };

    export var foo: {
        bar(a): number;
        baz(a, b: any, c: String): boolean;
    };
}

declare function withObjectTypeParam(opt: {
    bar(a): number;
    baz?;
    boo?: any;
    show: (overrideChecks: boolean) => void;
});

declare function returnsObjectType(): {
    value?;
    done: boolean;
};

declare var foo: {
    bar(a): number;
    baz(a, b: any, c: String): boolean;
};
