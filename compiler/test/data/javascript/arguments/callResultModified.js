
module.exports.generateVector = (vectorProvider) => {
    let vector = vectorProvider(0, 0, 0)

    vector.negative = vectorProvider(-vector.x, -vector.y, -vector.z)

    return vector
}
