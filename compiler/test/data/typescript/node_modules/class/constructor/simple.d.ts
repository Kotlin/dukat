declare class Foo {
    constructor();
}

declare class Bar {
    constructor(a: number);
}

declare class BarOptional {
    constructor(a?: number);
}

declare class BarMultiple {
    constructor(a: number);
    constructor(b: string);
    constructor(c: boolean);
}

declare class BarMultipleUnion {
    constructor(a: number | string | boolean);
}