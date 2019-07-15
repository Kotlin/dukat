interface A {
    attribute long intVar;

    double f1();
    double f1(long x);

    double f2(A other);
};

interface B : A {
    attribute long intVar;

    long f1();
    double f1(long x);

    double f2();
    double f2(B other);
    double f2(D other);
};

interface C : A {
    attribute long intVar;

    double f1();
    double f1(long x);

    double f2(C other);
};

interface D {

};