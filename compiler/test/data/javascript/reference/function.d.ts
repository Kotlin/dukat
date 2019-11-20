
function numBinaryFun(a, b) {
    return a - b
}

function numBinaryFunWrapper(a, b) {
    return numBinaryFun(a, b)
}

module.exports.numBinaryFun = numBinaryFun
module.exports.numBinaryFunWrapper = numBinaryFunWrapper