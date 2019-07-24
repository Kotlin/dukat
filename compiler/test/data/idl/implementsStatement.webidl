[NoInterfaceObject]
interface A {

};

[NoInterfaceObject]
interface B {

};

interface C : A {

};

C implements B;