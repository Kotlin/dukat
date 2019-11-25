interface A {

};

dictionary B {

};

enum C {
    "xx"
};

typedef (A or B or C) D;

interface E {
    attribute D x;
    attribute (A or B or C) y;
};