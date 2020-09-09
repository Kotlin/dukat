declare interface Foo {
    withObjectTypeParam(opt: {
        bar(a): number;
        baz?;
        boo?: any;
        show: (overrideChecks: boolean) => void;
    });

    returnsObjectType(): {
        value?;
        done: boolean;
    };

    foo: {
        bar(a): number;
        baz(a, b: any, c: String): boolean;
    };
}
