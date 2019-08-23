interface A {
    attribute ByteString s1;
    attribute DOMString s2;
    attribute USVString s3;
    attribute String s4;
    attribute CSSOMString s5;
    attribute DOMString? nullableString;

    attribute object obj;
    attribute object? nullableObject;
    attribute UnknownType value;

    attribute A<long>? child;
    attribute long[] intArray;
    attribute long[][] intArray2D;
    attribute sequence<long> intSequence;

    attribute Promise<void> promise;
    attribute FrozenArray<long> readArray;

    sequence<long> f(sequence<long> x);
    A<long> g(A<long> x);
    any h();
};