
function isNum(x) {
    return typeof x == "number"
}

module.exports = {
    max(a, b) {
        if (a > b)
            return a

        return b
    },

    negate(a) {
        if(isNum(a)) {
            return -a
        } else {
            return !a
        }
    }
}
