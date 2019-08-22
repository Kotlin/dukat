[NoInterfaceObject]
interface A {
    attribute long x;
    void f();
};

interface B {

};

B implements A;

[Constructor]
interface C : B {

};