class A {
    private s1 = new Set();
    private s2 = new Set<String>();

    f() {
        let s: Set<number> = new Set()
        s.add(3);
        s.add(5);
        console.log(s.has(3));
        s.forEach(x => console.log(x));
        s.delete(3);
        console.log(s.has(3));
    }
}