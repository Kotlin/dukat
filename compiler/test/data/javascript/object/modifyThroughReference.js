
var obj = {
    x: 0
    y: 0
}

function getObj() {
    return obj
}

function withZ(value) {
    var thisObj = getObj()
    thisObj.z = --value

    return thisObj
}

module.exports.obj = obj
module.exports.withZ = withZ
