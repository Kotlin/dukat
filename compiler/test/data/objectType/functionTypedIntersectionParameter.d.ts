declare interface FooPart<T> {}

type FooPartAndLiteral<T> = {foo: T; bar;} & FooPart<T>

declare class FooTypedUnion {
    baz<T>(p: {foo: T; sup;});
    bar<T>(p: FooPartAndLiteral<T>);
}
