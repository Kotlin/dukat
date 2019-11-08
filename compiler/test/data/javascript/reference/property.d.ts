class PropertyOwner {
    static property() {
        return "Hello, world!"
    }
}

function wrapperFun() {
    return PropertyOwner.property()
}