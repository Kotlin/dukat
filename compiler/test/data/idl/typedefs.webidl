interface A {
    attribute alias x;
    const alias y = 5;
    alias f(alias x);
};

interface B : myAClass {

};

typedef long alias;
typedef A myAClass;