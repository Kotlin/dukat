
function get2DVector() {
    let v = {
        x: 1,
        y: 1
    }

    return v
}

function get3DVector() {
    var v2D = get2DVector()

    v2D.z = 1

    return v2D
}

module.exports.get2DVector = get2DVector
module.exports.get3DVector = get3DVector