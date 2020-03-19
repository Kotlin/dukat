function gcd(a: number, b: number): number {
    a = Math.abs(a);
    b = Math.abs(b);

    if (b > a) {
        let temp: number = a;
        a = b;
        b = temp;
    }

    while (true) {
        a %= b;
        if (a == 0) { return b; }
        b %= a;
        if (b == 0) { return a; }
    }
}