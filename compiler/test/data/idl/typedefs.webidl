interface A {
    attribute alias x;
    alias f(alias x);
};

interface B : myAClass {

};

typedef long alias;
typedef A myAClass;