declare function foo(s: "number"): number;
declare function foo(s: "string"): string;

interface I {
    bar(s: "number"): number
    bar(s: "string"): string
}

declare class C {
    baz(s: "number"): number
    baz(s: "string"): string
}
