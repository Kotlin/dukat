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

type DialogEvents = "show" | "hide" | "destroy" | "create";

interface Foo {
    defaultPort: 80;
    primeSeed?: 2 | 3 | 5 | 7 | 11 | 13;
    floatSeed: 1.34 | 5.66 | 7.22;
    stringSeed: "a" | "b" | "c";
    alphaNumeric: 3 | 20 | "3" | "20"
    alwaysTrue: true,
    ping(evt: DialogEvents);
    randomEvent(): DialogEvents;
}