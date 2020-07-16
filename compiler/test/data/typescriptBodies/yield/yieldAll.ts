function* f(): Iterable<number> {
    let i = 1;
    while (i <= 3) {
        yield i;
        i++
    }
}

function* g(): Iterable<number> {
    yield* f();
    yield* f();
}

function h() {
    for (let x of g()) {
        console.log(x)
    }
}