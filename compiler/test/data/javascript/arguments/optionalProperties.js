
function is3D(vector) {
    return vector.z !== undefined && vector.z !== null
}

module.exports.lengthOf = function(vector) {
    let lengthSquared = vector.x ** 2 + vector.y ** 2

    if (is3D(vector)) {
        lengthSquared += vector.z ** 2
    }

    return Math.sqrt(lengthSquared)
}
