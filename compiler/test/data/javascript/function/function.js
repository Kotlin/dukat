
module.exports = {
    numBinaryFun(a, b) {
        return a - b
    },

    numUnaryFun(a) {
        var b = a
        a = "text"
        return b++
    },

    boolBinaryFun(a, b) {
        return a == b
    },

    boolUnaryFun(a) {
        return !a
    },

    anyFun(a, b) {
        return a + b
    }
}
