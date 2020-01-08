
module.exports.assertNotNull = (value, message) => {
    if (value === void 0 || value === null) {
        throw new Error(message)
    }

    return value
}
