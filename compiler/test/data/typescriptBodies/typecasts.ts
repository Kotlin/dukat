function f() {
    let x: any = "hi there";
    let s = <string>x;
    let ss = s.substring(0, 3);
    console.log(ss);
}
