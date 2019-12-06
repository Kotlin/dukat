
module.exports.assertNotNull = (value, lazyMessage) => {
    if (value === void 0 || value === null) {
        throw Error(lazyMessage())
    }

    return value
}
