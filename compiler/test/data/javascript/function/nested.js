
function numFun() {
    var x = 3

    function foo() {
        return x
    }

    return foo()
}


var value = "text"

function bar() {
    return value
}

function strFun() {
    var value = 3

    return bar()
}

exports.numFun = numFun
exports.strFun = strFun