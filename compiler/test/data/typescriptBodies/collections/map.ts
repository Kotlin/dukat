class A {
    private m1 = new Map();
    private m2 = new Map<String, String>();

    f() {
        let m: Map<number, number> = new Map()
        m.set(3, 2);
        m.set(5, 1);
        console.log(m.get(3));
        for (let [x, y] of m) {
            console.log(`${x} ${y}`)
        }
    }
}