interface UnresolvedUnions {
    attribute number x;
    attribute (long or short) y;
    number f(number x);
};

typedef (double or float) number;

interface A {

};

interface B {

};

interface C {

};

interface D {

};

typedef (A or B or C) ABC;
typedef (ABC or D) ABCD;
typedef (A<Int> or B) AIntOrB;

interface ResolvedUnions {
    attribute ABC? x;
    attribute sequence<ABCD> y;
    attribute (C or D)? z;
    attribute AIntOrB w;
    ABC f(ABC? x);
};