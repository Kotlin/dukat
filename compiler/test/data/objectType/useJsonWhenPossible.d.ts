declare var foo: {[s: string]: any};

declare module Module {
    export var bar: {[s: string]: any};

    export function withObjectTypeParam(bar: {[a: string]: any})

    export class Foo {
        prop: {[p: string]: any};
    }
}

declare interface Foo {
    foo: {[s: string]: any};

    boo: {[s: number]: any};
}
