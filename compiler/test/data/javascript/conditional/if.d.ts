
function max(a, b) {
    if (a > b)
        return a

    return b
}

function negate(a) {
    if(isNum(a)) {
        return -a
    } else {
        return !a
    }
}
