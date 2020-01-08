var num = 3
var bool = true
var str = "text"

function numReferenceFun() {
    return num
}

function boolReferenceFun() {
    return bool
}

function stringReferenceFun() {
    return str
}

module.exports.numReferenceFun = numReferenceFun
module.exports.boolReferenceFun = boolReferenceFun
module.exports.stringReferenceFun = stringReferenceFun