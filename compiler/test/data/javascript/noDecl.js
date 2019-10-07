var x = 3;

function foo(x, y) {
    var z = x - y;

    for(var i = 0; i < z; i++) {
        z += z
    }

    return z;
}