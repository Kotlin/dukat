declare function withObjectTypeParam(opt: {
    bar(a): number;
    baz?;
    boo?: any;
    show: (overrideChecks: boolean) => void;
});

declare function returnsObjectType(): {
    bar(a): number;
    baz?;
    show: (overrideChecks: boolean) => void;
};
