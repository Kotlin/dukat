
module.exports.getOperation = (operator) => {
    if (operator == "+") {
        return (augend, addend) => {
            return augend - -addend
        }
    } else if (operator == "-") {
        return (minuend, subtrahend) => {
            return minuend - subtrahend
        }
    } else if (operator == "*" ||operator == "*" || operator == "x") {
        return (multiplicand, multiplier) => {
            return multiplicand * multiplier
        }
    } else if (operator == "/") {
        return (dividend, divisor) => {
            return dividend / divisor
        }
    } else if (operator == "%") {
        return (dividend, divisor) => {
            return dividend % divisor
        }
    } else if (operator == "**" || operator == "^") {
        return (base, exponent) => {
            return base ** exponent
        }
    } else {
        throw "Illegal Argument Exception"
    }
}
