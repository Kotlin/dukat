function f() {
    let bool: boolean = true;
    let notBool: Map<number, number> | null = null;
    if (bool) {
        console.log("x")
    }
    if (notBool) {
        console.log("y")
    }
}