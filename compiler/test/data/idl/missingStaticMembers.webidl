interface A {
    const long x = 5;
    static attribute long y;
    static void f();
};

interface B : A {

};

interface C {

};

C implements B;

