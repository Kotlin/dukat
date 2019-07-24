interface A {
    attribute alias x;
    attribute alias2 y;
    attribute sequence<alias> intArray;
    const alias y = 5;
    alias f(alias x);
};

interface B : myAClass {
    attribute myAClass<alias> x;
};

typedef long alias;
typedef long alias2;
typedef A myAClass;