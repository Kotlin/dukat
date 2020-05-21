function* f() {
    let i = 1;
    while (true) {
        yield i;
        i++
    }
}

function g() {
    for (let x of f()) {
        if (x > 10) {
            break
        }
        console.log(x)
    }
}