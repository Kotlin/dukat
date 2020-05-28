function f() {
    let m = new Map<number, number>()
    for (let [key, value] of m) {
        console.log(key)
        console.log(value)
    }
}