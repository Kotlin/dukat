interface A {
    attribute long x;
};

[Constructor(long a)]
partial interface A {
    attribute long y;
    const long z = 5;
    void f();
};

interface A {
    void g();
};

dictionary B {
    boolean a = true;
};

partial dictionary B {
    boolean b = false;
};