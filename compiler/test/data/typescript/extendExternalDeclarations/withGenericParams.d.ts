/// <reference path="../../../testDefinitelyTyped/DefinitelyTyped/ref-array/ref-array.d.ts" />
/// <reference path="../../../testDefinitelyTyped/DefinitelyTyped/ref-array/ref-array.d.ts" />
/// <reference path="../../../testDefinitelyTyped/DefinitelyTyped/q/Q.d.ts" />

declare module Q {
    interface Promise<T> {
        foo<B>(b: B): T
        foo<T, B>(a, b: B): T
        bar: T[]
    }
}

declare module "ref-array" {
    // TODO probably we should ignore declarations with same name inside external module
    interface ArrayType<T> {
        [prop: string]: number;
        someField: string;
        optionalField?: T;
        (resourceId:string, hash?:any, callback?:Function): void;
    }
}

