class Something {
    x: number = 5;
    isChild(): this is Child {
        return false
    }
}

class Child extends Something {
    y: number = 6;
    isChild(): this is Child {
        return true
    }
}

declare function isSomething(x: any) : x is Something;

function f() {
    let s: any = new Something();
    if (isSomething(s)) {
        console.log(s.x)
        if (s.isChild()) {
            console.log(s.y)
        }
    }
}