var x = 3;

module.exports = function foo(x, ...y) {
    var z = 0;

    for(var i = 0; i < y.size(); y++) {
        z += z
    }

    return z;
}
