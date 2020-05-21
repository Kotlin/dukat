function f() {
    let a = [1, 2, 3];
    let b = a.map(x => {
        let y = x * x
        return y
    });
    b.forEach(x => console.log(x))
}