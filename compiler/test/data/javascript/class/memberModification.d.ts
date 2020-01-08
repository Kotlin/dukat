
class ModifiedLiterals {
    static getStringLiteral() {
        return "text"
    }

    static getNumberLiteral() {
        return 3.141592653
    }

    static getBooleanLiteral() {
        return false
    }
}

ModifiedLiterals.getStringLiteral = ModifiedLiterals.getNumberLiteral
ModifiedLiterals.getBooleanLiteral = ModifiedLiterals.getNumberLiteral


module.exports.ModifiedLiterals = ModifiedLiterals
module.exports.falseStr = ModifiedLiterals.getStringLiteral()
module.exports.trueNum = ModifiedLiterals.getNumberLiteral()
module.exports.falseBool = ModifiedLiterals.getBooleanLiteral()