
module.exports = {
    typeOf(o) {
        return typeof o
    },

    instanceOfObject(o) {
        return o instanceof Object
    },

    keyInObj(key, obj) {
        return key in obj;
    }
}