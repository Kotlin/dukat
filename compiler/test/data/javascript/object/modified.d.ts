
function get2DVector() {
    return {
        x: 1,
        y: 1
    }
}

function get3DVector() {
    var v2D = get2DVector()

    v2D.z = 1

    return v2D
}

var obj = {
    a: "text"
}

function modObj() {
    obj.b = "text"
}


module.exports.get2DVector = get2DVector
module.exports.get3DVector = get3DVector
module.exports.obj = obj
module.exports.modObj = modObj