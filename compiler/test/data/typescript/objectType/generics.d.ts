interface FooBazWithTypes<T> {
    returnsB<B>(b: B): { foo(a): B };
    acceptsT(): { bar(a: T): boolean };
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

declare var Tokens: Array<{}>;
declare var PingableTokens: Array<{ping: () => boolean}>;

declare class SomeSource<P, S> {
    ping(): P;
    withTarget<T>(): {source: S, target: T}
    getTargetHandler<T>(): {handler(source: S, target: T): boolean }
}