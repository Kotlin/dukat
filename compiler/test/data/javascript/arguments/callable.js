
module.exports.runOperation = (numA, numB, operation) => {
    numA = 0 - -numA
    numB = 0 - -numB

    return 0 - -operation(numA, numB)
}
