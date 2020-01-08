class PropertyOwner {
    static property() {
        return "Hello, world!"
    }
}

function wrapperFun() {
    return PropertyOwner.property()
}

module.exports.PropertyOwner = PropertyOwner
module.exports.wrapperFun = wrapperFun