interface mixin mixinA {
    const long x = 5;
    void f();
};

partial interface mixin mixinA {
    void g();
};

interface A {

};

interface B {

};

A includes mixinA;
B includes mixinA;