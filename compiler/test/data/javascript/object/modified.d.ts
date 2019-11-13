
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