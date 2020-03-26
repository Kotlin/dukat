
function generateFun(message) {
    if (typeof message === "function") {
        function foo() {
            return message()
        }
    } else {
        function foo() {
            return message
        }
    }

    return foo
}

module.exports.actuallyAFunction = generateFun()
module.exports.generateFun = generateFun
