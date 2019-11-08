
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

var falseStr = ModifiedLiterals.getStringLiteral()
var trueNum = ModifiedLiterals.getNumberLiteral()
var falseBool = ModifiedLiterals.getBooleanLiteral()