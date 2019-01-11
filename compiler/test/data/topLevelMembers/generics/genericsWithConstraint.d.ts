declare function extendsFooT<T extends Foo>(a: T): T;
declare function extendsAny<T extends any>(a: T): T;
declare function withManyExtends<A extends Bar, B extends A>(a: A, b: B): boolean;

declare function extendsFooArrayT<T extends Foo[]>(a: T): T;
declare function extendsFooArray2T<T extends Array<Foo>>(a: T): T;