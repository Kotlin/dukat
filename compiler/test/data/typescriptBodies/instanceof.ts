function f(isSet: boolean) {
    let x: Set<number> | Array<number>;
    if (isSet) {
        x = new Set()
    } else {
        x = []
    }
    if (x instanceof Set) {
        console.log(x.has(5))
    }
    if (x instanceof Array) {
        x.push(5)
    }
}