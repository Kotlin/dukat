var v = {};

v.x = 5;
v.y = 5;
v.z = 5;


function negate(x, y, z) {
    return {
        x: -x,
        y: -y,
        z: -z
    }
}

v.negate = negate;


v.double = {}

v.double.x = 2*v.x;
v.double.y = 2*v.y;
v.double.z = 2*v.z;

module.exports.v = v