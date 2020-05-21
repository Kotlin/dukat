class Yielder {
    * f() {
        let i = 1;
        while (true) {
            yield i;
            i++
        }
    }
}

function g() {
    let yielder = new Yielder()
    for (let x of yielder.f()) {
        if (x > 10) {
            break
        }
        console.log(x)
    }
}