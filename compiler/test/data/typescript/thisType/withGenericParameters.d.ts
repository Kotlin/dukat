interface Interface<T> {
    foo: this;
    bar(): this;
}

declare class Class<T, U> {
    baz: this;
    boo(): this;
}
