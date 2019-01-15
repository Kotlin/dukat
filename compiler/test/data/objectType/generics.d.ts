interface FooBazWithTypes<T> {
    returnsB<B>(b: B): { bar(a): B };
}
declare function withGenericObjectTypeParam<T>(opt: {
    bar(a): T;
    baz?;
    boo?: T;
    show: (overrideChecks: boolean) => void;
});
declare function withDoublyGenericObjectTypeParam<T,S>(opt: {
    bar(a): T;
    foo(t: { a: T } | string)
    baz?;
    boo?: S;
    show: (overrideChecks: boolean) => void;
});

declare function returnsGenericObjectType<S>(): {
    bar(a): S;
};
