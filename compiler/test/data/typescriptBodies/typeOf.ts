function f(x: any) {
    if (typeof x == "string") {
        console.log(x + x);
    }
    if (typeof x == "number") {
        console.log(x * 2);
    }
}