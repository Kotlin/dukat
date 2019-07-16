[Constructor]
interface A {

};

[Constructor(),
 Constructor(double x)]
interface B {

};

[Constructor(double x)]
interface C : B {

};