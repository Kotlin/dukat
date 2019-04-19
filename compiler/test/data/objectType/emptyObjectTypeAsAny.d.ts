declare var foo: {};

type Transform = (body: {}) => any;

declare interface Foo {
    boo: {};

    ping(param: {});
    json(fn: Transform);
}
