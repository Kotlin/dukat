
class Literals {
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

var str = Literals.getStringLiteral()
var num = Literals.getNumberLiteral()
var bool = Literals.getBooleanLiteral()

module.exports.str = str
module.exports.num = num
module.exports.bool = bool