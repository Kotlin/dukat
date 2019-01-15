
declare function foo(p: {
    foo: this;
    bar(): this;
}): {
    baz: this;
    boo(): this;
}
