declare function foo(a: null): null;
declare function bar(a: undefined): undefined;

interface I {
    foo(a: null): undefined
    bar(a: undefined): null
}

declare class C {
    foo(a: null): undefined
    bar(a: undefined): null
}
