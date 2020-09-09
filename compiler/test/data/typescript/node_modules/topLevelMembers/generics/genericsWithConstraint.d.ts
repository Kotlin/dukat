declare function extendsFooT<T extends Foo>(a: T): T;
declare function extendsAny<T extends any>(a: T): T;
declare function withManyExtends<A extends Bar, B extends A>(a: A, b: B): boolean;
declare interface Foo {
    
}
declare interface Bar {
    
}