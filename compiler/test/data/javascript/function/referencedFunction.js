function refFoo(x, y) {
    var z = x - y;

    for(var i = 0; i < z; i++) {
        z += z
    }

    return z;
}

module.exports = refFoo