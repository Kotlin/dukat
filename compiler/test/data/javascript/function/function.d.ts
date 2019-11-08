function numBinaryFun(a, b) {
    return a - b
}

function numUnaryFun(a) {
    var b = a
    a = "text"
    return b++
}

function boolBinaryFun(a, b) {
    return a == b
}

function boolUnaryFun(a) {
    return !a
}

function anyFun(a, b) {
    return a + b
}

function unitFunction() {
    return;
}