declare var foo: {
    bar(a): number;
    baz;
    boo: string;
    show: (overrideChecks: boolean) => void;
    (foo: {bar : number}): {};
    [s: string]: any;
};
