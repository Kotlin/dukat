class A {
    x: any = new B();
}

class B {
    y: number = 6;
}

declare function isB(x: any) : x is B

function f() {
    let s = new A();
    if (isB(s.x)) {
        console.log(s.x.y)
    }
}