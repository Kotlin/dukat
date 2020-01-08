
let obj = {
    x: 0
    y: 0
}

function withZ(value) {
    obj.z = --value
    return obj
}

module.exports.obj = obj
module.exports.withZ = withZ
