class A {
    private a1 = [];
    private a2 = new Array<String>();

    f() {
        let a: Array<number> = []
        a.push(1, 2, 3);
        a.push(4);
        a.push(...[5, 6, 7]);
        a.forEach(x => console.log(x));
    }
}